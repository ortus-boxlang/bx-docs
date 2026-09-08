package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * The actual {@link Doclet} implementation driving
 * {@link JavadocDocGenerator}. Package-private - never used directly,
 * always through the generator, which is the only supported entry point.
 *
 * <p>Configuration flows in via {@link #CURRENT}, a {@link ThreadLocal} set
 * by the generator immediately before invoking the tool and cleared
 * immediately after - the {@link javax.tools.DocumentationTool} API only
 * accepts a doclet {@link Class}, not a pre-configured instance, so this is
 * the standard way custom doclets receive configuration when invoked
 * programmatically (rather than via {@code -X}-style command line
 * options). Safe here because each generation run is synchronous and
 * single-threaded within the calling build tool's own task/goal
 * execution.
 */
public final class BxSitesJavadocDoclet implements Doclet {

    static final ThreadLocal<Config> CURRENT = new ThreadLocal<>();

    record Config(Path contentDir, String pagePathPrefix, List<String> tags, List<Path> pageFilesOut, List<String> warningsOut) {
    }

    /** Public no-arg constructor required - {@link javax.tools.DocumentationTool} instantiates doclets by reflection. */
    public BxSitesJavadocDoclet() {
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
    }

    @Override
    public String getName() {
        return "BxSitesMarkdownDoclet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        Config config = CURRENT.get();
        if (config == null) {
            throw new IllegalStateException("BxSitesJavadocDoclet was invoked without a Config set - internal error.");
        }

        DocTrees docTrees = environment.getDocTrees();
        for (TypeElement type : ElementFilter.typesIn(environment.getIncludedElements())) {
            if (type.getNestingKind() != NestingKind.TOP_LEVEL || !type.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            try {
                Path page = renderPage(type, docTrees, config);
                config.pageFilesOut().add(page);
            } catch (IOException e) {
                config.warningsOut().add("Failed to render " + type.getQualifiedName() + ": " + e.getMessage());
            }
        }
        return true;
    }

    private static Path renderPage(TypeElement type, DocTrees docTrees, Config config) throws IOException {
        DocCommentTree classDoc = docTrees.getDocCommentTree(type);
        String summary = classDoc == null ? "" : renderBody(classDoc.getFirstSentence());
        String body = classDoc == null ? "" : renderBody(classDoc.getFullBody());

        StringBuilder md = new StringBuilder();
        md.append(FrontMatter.render(new FrontMatter.Fields(
                type.getSimpleName().toString(), null, null, summary.isBlank() ? null : summary, config.tags())));
        md.append("\n# ").append(type.getSimpleName()).append("\n\n");
        md.append('`').append(type.getQualifiedName()).append("`\n\n");
        if (!body.isBlank()) {
            md.append(body).append("\n\n");
        }

        List<ExecutableElement> constructors = ElementFilter.constructorsIn(type.getEnclosedElements()).stream()
                .filter(BxSitesJavadocDoclet::isDocumentable).toList();
        List<ExecutableElement> methods = ElementFilter.methodsIn(type.getEnclosedElements()).stream()
                .filter(BxSitesJavadocDoclet::isDocumentable).toList();
        List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements()).stream()
                .filter(BxSitesJavadocDoclet::isDocumentable).toList();

        if (!constructors.isEmpty() || !methods.isEmpty() || !fields.isEmpty()) {
            Map<String, String> chips = new LinkedHashMap<>();
            if (!constructors.isEmpty()) {
                chips.put("constructor", "Constructors");
            }
            if (!methods.isEmpty()) {
                chips.put("method", "Methods");
            }
            if (!fields.isEmpty()) {
                chips.put("field", "Fields");
            }
            md.append(MemberFilterUi.styles());
            md.append(MemberFilterUi.toolbarOpen(chips));

            if (!constructors.isEmpty()) {
                md.append(MemberFilterUi.sectionOpen("constructor"));
                md.append("## Constructors\n\n");
                for (ExecutableElement ctor : constructors) {
                    appendMember(md, ctor, docTrees, signatureOf(ctor));
                }
                md.append(MemberFilterUi.sectionClose());
            }
            if (!methods.isEmpty()) {
                md.append(MemberFilterUi.sectionOpen("method"));
                md.append("## Methods\n\n");
                for (ExecutableElement method : methods) {
                    appendMember(md, method, docTrees, signatureOf(method));
                }
                md.append(MemberFilterUi.sectionClose());
            }
            if (!fields.isEmpty()) {
                md.append(MemberFilterUi.sectionOpen("field"));
                md.append("## Fields\n\n");
                for (VariableElement field : fields) {
                    appendMember(md, field, docTrees, fieldSignature(field));
                }
                md.append(MemberFilterUi.sectionClose());
            }

            md.append(MemberFilterUi.toolbarClose());
        }

        Path pageFile = config.contentDir()
                .resolve(config.pagePathPrefix())
                .resolve(type.getQualifiedName().toString().replace('.', '/') + ".md");
        Files.createDirectories(pageFile.getParent());
        Files.writeString(pageFile, md.toString());
        return pageFile;
    }

    private static boolean isDocumentable(Element member) {
        return member.getModifiers().contains(Modifier.PUBLIC) || member.getModifiers().contains(Modifier.PROTECTED);
    }

    private static void appendMember(StringBuilder md, Element member, DocTrees docTrees, String signature) {
        md.append(MemberFilterUi.itemOpen(signature));

        md.append("### `").append(signature).append("`\n\n");

        DocCommentTree doc = docTrees.getDocCommentTree(member);
        if (doc != null) {
            if (hasDeprecatedTag(doc)) {
                md.append("**Deprecated.**\n\n");
            }
            String description = renderBody(doc.getFullBody());
            if (!description.isBlank()) {
                md.append(description).append("\n\n");
            }
            String tagLines = renderBlockTags(doc);
            if (!tagLines.isBlank()) {
                md.append(tagLines).append('\n');
            }
        }

        md.append(MemberFilterUi.itemClose());
    }

    private static boolean hasDeprecatedTag(DocCommentTree doc) {
        return doc.getBlockTags().stream().anyMatch(DeprecatedTree.class::isInstance);
    }

    private static String renderBlockTags(DocCommentTree doc) {
        StringBuilder sb = new StringBuilder();
        for (DocTree tag : doc.getBlockTags()) {
            if (tag instanceof ParamTree t) {
                sb.append("- **Parameter** `").append(t.getName()).append("` - ").append(renderBody(t.getDescription())).append('\n');
            } else if (tag instanceof ReturnTree t) {
                sb.append("- **Returns** ").append(renderBody(t.getDescription())).append('\n');
            } else if (tag instanceof ThrowsTree t) {
                sb.append("- **Throws** `").append(t.getExceptionName()).append("` - ").append(renderBody(t.getDescription())).append('\n');
            }
        }
        return sb.toString();
    }

    private static String signatureOf(ExecutableElement member) {
        boolean isConstructor = member.getKind() == javax.lang.model.element.ElementKind.CONSTRUCTOR;
        String name = isConstructor ? member.getEnclosingElement().getSimpleName().toString() : member.getSimpleName().toString();
        String params = member.getParameters().stream()
                .map(BxSitesJavadocDoclet::parameterSignature)
                .collect(Collectors.joining(", "));
        String returnType = isConstructor ? "" : member.getReturnType() + " ";
        return returnType + name + "(" + params + ")";
    }

    private static String parameterSignature(VariableElement parameter) {
        return parameter.asType() + " " + parameter.getSimpleName();
    }

    private static String fieldSignature(VariableElement field) {
        String modifiers = field.getModifiers().stream().map(Modifier::toString).collect(Collectors.joining(" "));
        return (modifiers.isEmpty() ? "" : modifiers + " ") + field.asType() + " " + field.getSimpleName();
    }

    /**
     * Best-effort plain-text/Markdown rendering of a doc comment body -
     * see the class javadoc for exactly what this does and doesn't handle.
     */
    private static String renderBody(List<? extends DocTree> body) {
        StringBuilder sb = new StringBuilder();
        for (DocTree tree : body) {
            sb.append(renderDocTree(tree));
        }
        return sb.toString().trim().replaceAll("[ \\t]*\\n[ \\t]*", "\n").trim();
    }

    private static String renderDocTree(DocTree tree) {
        return switch (tree) {
            case com.sun.source.doctree.TextTree t -> t.getBody();
            case LiteralTree t -> "`" + t.getBody().getBody() + "`";
            case com.sun.source.doctree.LinkTree t -> "`" + t.getReference().getSignature() + "`";
            case com.sun.source.doctree.StartElementTree ignored -> "";
            case com.sun.source.doctree.EndElementTree ignored -> "";
            default -> "";
        };
    }
}
