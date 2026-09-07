package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DocumentationTool;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Runs the JDK's own {@code jdk.javadoc.doclet.Doclet} SPI (via
 * {@link javax.tools.DocumentationTool}, in-process - no {@code javadoc}
 * subprocess needed) against a project's public Java API, emitting one
 * bx-sites-frontmattered Markdown page per public top-level type.
 *
 * <p><b>Deliberately scoped down, not a complete Javadoc-to-Markdown
 * converter</b> (the plan itself flags this as the heaviest of the three
 * Spring Boot generators - "budget accordingly"). What v1 covers:
 * public/protected constructors and methods of public top-level types,
 * their own doc comments' first-sentence/full-body text, and
 * {@code @param}/{@code @return}/{@code @throws}/{@code @deprecated} block
 * tags. What it deliberately does <b>not</b> do, so a fast-follow can close
 * these gaps without redesigning the API:
 * <ul>
 *   <li>Nested/package-private types, and fields, are skipped entirely.
 *   <li>Inherited members are never walked - only members declared
 *       directly on the type itself.
 *   <li>Inline HTML in doc comments (e.g. {@code <p>}, {@code <ul>}) is
 *       stripped rather than converted to Markdown - this is a best-effort
 *       text extraction, not a full HTML-to-Markdown converter.
 *   <li>{@code {@link}}/{@code {@see}} references render as inline code
 *       of their raw signature text, with no cross-page hyperlink
 *       resolution to the other generated pages.
 *   <li>No index/nav page is generated - wiring generated pages into
 *       bx-sites' own navigation is left to the consuming project.
 * </ul>
 *
 * <p>Runs against source files directly (not a compiled classpath), so it
 * needs no dependency resolution of its own - at the cost of degraded
 * (but not broken) signature rendering for types referencing classes
 * outside the given source set.
 */
public final class JavadocDocGenerator {

    private JavadocDocGenerator() {
    }

    /**
     * @param sourceFiles    the {@code .java} files to walk (typically every file under {@code src/main/java})
     * @param contentDir     the resolved bx-sites content dir (docs/ or src/)
     * @param pagePathPrefix where generated pages go, relative to contentDir (e.g. {@code api/javadoc})
     * @param tags           frontmatter tags applied to every generated page
     */
    public record Request(List<Path> sourceFiles, Path contentDir, String pagePathPrefix, List<String> tags) {
        public Request {
            sourceFiles = List.copyOf(sourceFiles);
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    public record Result(List<Path> pageFiles, List<String> warnings) {
    }

    public static Result generate(Request request) throws IOException {
        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
        if (tool == null) {
            throw new IllegalStateException(
                    "No system DocumentationTool available - this generator needs a full JDK (not a JRE) to run.");
        }
        if (request.sourceFiles().isEmpty()) {
            return new Result(List.of(), List.of());
        }

        List<Path> pageFiles = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        BxSitesJavadocDoclet.Config config = new BxSitesJavadocDoclet.Config(
                request.contentDir(), request.pagePathPrefix(), request.tags(), pageFiles, warnings);

        StringWriter diagnostics = new StringWriter();
        BxSitesJavadocDoclet.CURRENT.set(config);
        try (StandardJavaFileManager fileManager = tool.getStandardFileManager(null, null, null)) {
            DocumentationTool.DocumentationTask task = tool.getTask(
                    new PrintWriter(diagnostics),
                    fileManager,
                    null,
                    BxSitesJavadocDoclet.class,
                    List.of("-quiet"),
                    fileManager.getJavaFileObjectsFromPaths(request.sourceFiles()));

            boolean ok = task.call();
            if (!ok) {
                warnings.add("javadoc reported failures: " + diagnostics);
            }
        } catch (UncheckedIOException e) {
            throw e.getCause();
        } finally {
            BxSitesJavadocDoclet.CURRENT.remove();
        }

        return new Result(List.copyOf(pageFiles), List.copyOf(warnings));
    }
}
