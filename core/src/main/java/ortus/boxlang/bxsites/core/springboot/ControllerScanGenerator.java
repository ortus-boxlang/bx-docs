package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reflection-scans a project's already-compiled classes for Spring MVC
 * controllers, emitting one bx-sites-frontmattered Markdown page per
 * controller listing its endpoints. The fallback generator - the plan
 * itself scopes it in "enabled by default only when OpenAPI generation is
 * off" (this class doesn't enforce that policy itself; each plugin's
 * wiring does).
 *
 * <p>Each page's endpoint table is a plain Markdown pipe table, which
 * bx-sites' own {@code TableWrapProcessor} then gives a live filter box -
 * see {@code appendFilterableTable} for why a generator-authored raw table
 * with its own per-row filtering cannot work in this pipeline.
 *
 * <p><b>Must run in a JVM whose classpath already includes both this
 * class and the target project's own compiled classes/dependencies</b>
 * (Spring included) - see {@code ControllerScanMain}, the forked-JVM entry
 * point both plugins actually launch. Resolves Spring's own annotation
 * types purely by fully-qualified name via reflection rather than
 * compiling against {@code spring-web}/{@code spring-context} - this
 * module stays Spring-agnostic; a class only becomes a Spring controller
 * (or a mapping) if the annotations are present when reflected on at scan
 * time, wherever they came from.
 *
 * <p><b>Deliberately scoped down, matching the same "budget accordingly"
 * spirit as {@link JavadocDocGenerator}:</b>
 * <ul>
 *   <li>Only classes directly annotated {@code @Controller} or
 *       {@code @RestController} are recognized - a custom stereotype
 *       annotation built on top of either (Spring's own meta-annotation
 *       composition, resolved via {@code AnnotatedElementUtils} normally)
 *       is not detected. Direct annotation covers the overwhelming
 *       majority of real-world Spring MVC controllers.
 *   <li>Only directly-annotated {@code @RequestMapping}/{@code @GetMapping}/
 *       {@code @PostMapping}/{@code @PutMapping}/{@code @DeleteMapping}/
 *       {@code @PatchMapping} methods are recognized - same reasoning.
 *   <li>Nested classes are skipped entirely (top-level controllers only).
 *   <li>No per-endpoint description text - reflection has no access to
 *       source-level doc comments, unlike {@link JavadocDocGenerator}.
 *       Combining the two generators' output is a possible fast-follow,
 *       not attempted here.
 * </ul>
 */
public final class ControllerScanGenerator {

    private static final Set<String> CONTROLLER_ANNOTATIONS = Set.of(
            "org.springframework.stereotype.Controller",
            "org.springframework.web.bind.annotation.RestController");

    private static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";

    private static final Map<String, String> METHOD_SHORTCUT_ANNOTATIONS = Map.ofEntries(
            Map.entry("org.springframework.web.bind.annotation.GetMapping", "GET"),
            Map.entry("org.springframework.web.bind.annotation.PostMapping", "POST"),
            Map.entry("org.springframework.web.bind.annotation.PutMapping", "PUT"),
            Map.entry("org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
            Map.entry("org.springframework.web.bind.annotation.PatchMapping", "PATCH"));

    private ControllerScanGenerator() {
    }

    public record Request(Path classesDir, Path contentDir, String pagePathPrefix, List<String> tags) {
        public Request {
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    public record Endpoint(List<String> methods, List<String> paths, String signature) {
    }

    public record Result(List<Path> pageFiles, List<String> warnings) {
    }

    public static Result scan(Request request, ClassLoader loader) throws IOException {
        if (!Files.isDirectory(request.classesDir())) {
            return new Result(List.of(), List.of());
        }

        List<Path> pageFiles = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try (var walk = Files.walk(request.classesDir())) {
            List<Path> classFiles = walk.filter(p -> p.toString().endsWith(".class")).toList();
            for (Path classFile : classFiles) {
                String binaryName = binaryNameOf(request.classesDir(), classFile);
                if (binaryName.contains("$")) {
                    continue;
                }
                try {
                    Class<?> clazz = Class.forName(binaryName, false, loader);
                    if (!isController(clazz, loader)) {
                        continue;
                    }
                    List<Endpoint> endpoints = endpointsOf(clazz, loader);
                    Path page = renderControllerPage(clazz, endpoints, request);
                    pageFiles.add(page);
                } catch (Throwable t) {
                    warnings.add("Skipped " + binaryName + ": " + t);
                }
            }
        }

        return new Result(List.copyOf(pageFiles), List.copyOf(warnings));
    }

    private static String binaryNameOf(Path classesDir, Path classFile) {
        String relative = classesDir.relativize(classFile).toString();
        String withoutExtension = relative.substring(0, relative.length() - ".class".length());
        return withoutExtension.replace(classFile.getFileSystem().getSeparator(), ".");
    }

    private static boolean isController(Class<?> clazz, ClassLoader loader) {
        for (String annotationName : CONTROLLER_ANNOTATIONS) {
            if (hasAnnotation(clazz, annotationName, loader)) {
                return true;
            }
        }
        return false;
    }

    private static List<Endpoint> endpointsOf(Class<?> controller, ClassLoader loader) {
        List<String> basePaths = pathsOf(annotationOn(controller, REQUEST_MAPPING, loader));
        List<Endpoint> endpoints = new ArrayList<>();

        for (Method method : controller.getDeclaredMethods()) {
            List<String> methodNames = null;
            List<String> relativePaths = null;

            Object requestMapping = annotationOn(method, REQUEST_MAPPING, loader);
            if (requestMapping != null) {
                methodNames = requestMethodsOf(requestMapping);
                relativePaths = pathsOf(requestMapping);
            } else {
                for (Map.Entry<String, String> shortcut : METHOD_SHORTCUT_ANNOTATIONS.entrySet()) {
                    Object shortcutAnnotation = annotationOn(method, shortcut.getKey(), loader);
                    if (shortcutAnnotation != null) {
                        methodNames = List.of(shortcut.getValue());
                        relativePaths = pathsOf(shortcutAnnotation);
                        break;
                    }
                }
            }

            if (methodNames == null) {
                continue;
            }
            List<String> fullPaths = combine(basePaths, relativePaths);
            endpoints.add(new Endpoint(methodNames, fullPaths, signatureOf(method)));
        }
        return endpoints;
    }

    private static List<String> combine(List<String> basePaths, List<String> relativePaths) {
        if (basePaths.isEmpty()) {
            return relativePaths.isEmpty() ? List.of("/") : relativePaths;
        }
        if (relativePaths.isEmpty()) {
            return basePaths;
        }
        List<String> combined = new ArrayList<>();
        for (String base : basePaths) {
            for (String relative : relativePaths) {
                combined.add(joinPath(base, relative));
            }
        }
        return combined;
    }

    private static String joinPath(String base, String relative) {
        String left = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String right = relative.startsWith("/") ? relative : "/" + relative;
        return left + right;
    }

    private static Object annotationOn(java.lang.reflect.AnnotatedElement element, String annotationClassName, ClassLoader loader) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationClassName, false, loader);
            return element.getAnnotation(annotationClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static boolean hasAnnotation(java.lang.reflect.AnnotatedElement element, String annotationClassName, ClassLoader loader) {
        return annotationOn(element, annotationClassName, loader) != null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> pathsOf(Object annotation) {
        if (annotation == null) {
            return List.of();
        }
        String[] value = (String[]) invoke(annotation, "value");
        String[] path = (String[]) invoke(annotation, "path");
        String[] chosen = (path != null && path.length > 0) ? path : value;
        return chosen == null ? List.of() : List.of(chosen);
    }

    private static List<String> requestMethodsOf(Object requestMappingAnnotation) {
        Object[] methods = (Object[]) invoke(requestMappingAnnotation, "method");
        if (methods == null || methods.length == 0) {
            return List.of("ANY");
        }
        List<String> names = new ArrayList<>();
        for (Object m : methods) {
            names.add(m.toString());
        }
        return names;
    }

    private static Object invoke(Object annotation, String methodName) {
        try {
            Method m = annotation.getClass().getMethod(methodName);
            return m.invoke(annotation);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static String signatureOf(Method method) {
        String params = java.util.Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return method.getReturnType().getSimpleName() + " " + method.getName() + "(" + params + ")";
    }

    private static Path renderControllerPage(Class<?> controller, List<Endpoint> endpoints, Request request) throws IOException {
        StringBuilder md = new StringBuilder();
        md.append(FrontMatter.render(new FrontMatter.Fields(controller.getSimpleName(), null, null, null, request.tags())));
        md.append("\n# ").append(controller.getSimpleName()).append("\n\n");
        md.append('`').append(controller.getName()).append("`\n\n");

        if (endpoints.isEmpty()) {
            md.append("_No mapped endpoints found._\n");
        } else {
            appendFilterableTable(md, endpoints);
        }

        Path pageFile = request.contentDir()
                .resolve(request.pagePathPrefix())
                .resolve(controller.getName().replace('.', '/') + ".md");
        Files.createDirectories(pageFile.getParent());
        Files.writeString(pageFile, md.toString());
        return pageFile;
    }

    /**
     * One row per (endpoint x path x method) - an endpoint mapped to
     * multiple HTTP methods gets one row per method rather than a combined
     * "GET, POST" cell, so each row reads as the single endpoint it is.
     *
     * <p>A plain Markdown pipe table, not raw {@code <table>} markup. An
     * earlier version emitted raw rows carrying their own Alpine
     * {@code x-show} attributes, plus a chip toolbar to drive them, but
     * that never worked in a real build: bx-sites' own
     * {@code TableWrapProcessor} injects its own {@code x-show} into every
     * {@code <tr>} of a table with ten or more rows (so the generator's
     * attribute lands second on the tag and is ignored), and the Markdown
     * renderer entity-escapes a {@code <tr>}'s attribute values, breaking
     * the expression regardless. Verified against built HTML, not assumed.
     *
     * <p>Nothing is lost by dropping it: that same processor gives a table
     * this size a live filter box for free, and a pipe table stays readable
     * in the raw Markdown, themed everywhere, and fully in the search
     * index. The BoxLang-side ColdBox routes page renders the same way, for
     * the same reason.
     */
    private static void appendFilterableTable(StringBuilder md, List<Endpoint> endpoints) {
        md.append("| Method | Path | Handler |\n");
        md.append("|---|---|---|\n");

        for (Endpoint endpoint : endpoints) {
            for (String path : endpoint.paths()) {
                for (String method : endpoint.methods()) {
                    md.append("| ").append(cell(method))
                            .append(" | `").append(cell(path))
                            .append("` | `").append(cell(endpoint.signature()))
                            .append("` |\n");
                }
            }
        }

        md.append('\n');
    }

    /**
     * A table cell: single-lined, with pipes escaped so a path or signature
     * containing one can't break the table it sits in.
     */
    private static String cell(String value) {
        return value == null ? "" : value.replace("|", "\\|").replaceAll("\\s+", " ").trim();
    }
}
