package ortus.boxlang.bxsites.core.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises the real JDK DocumentationTool against real, on-disk {@code
 * .java} source files - no mocking of the doclet SPI, since a fake would
 * prove nothing about whether the actual {@link BxSitesJavadocDoclet}
 * wiring works.
 */
class JavadocDocGeneratorTest {

    @Test
    void generate_writesNothingWhenThereAreNoSourceFiles(@TempDir Path dir) throws IOException {
        JavadocDocGenerator.Result result = JavadocDocGenerator.generate(
                new JavadocDocGenerator.Request(List.of(), dir, "api/javadoc", List.of()));

        assertTrue(result.pageFiles().isEmpty());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void generate_writesOnePagePerPublicTopLevelType(@TempDir Path dir) throws IOException {
        Path sourceFile = writeSource(dir, "com/example/Widget.java", """
                package com.example;

                /**
                 * A single widget on a shelf.
                 *
                 * <p>Longer description with some {@code inline code} and a
                 * {@link java.util.List} reference.
                 */
                public class Widget {

                    /**
                     * Creates a widget with the given name.
                     *
                     * @param name the widget's display name
                     */
                    public Widget(String name) {
                    }

                    /**
                     * Returns this widget's display name.
                     *
                     * @return the display name
                     * @throws IllegalStateException if not yet initialized
                     */
                    public String getName() {
                        return null;
                    }

                    /**
                     * @deprecated use {@link #getName()} instead
                     */
                    @Deprecated
                    public String name() {
                        return null;
                    }

                    // package-private - must not be documented
                    void internalOnly() {
                    }
                }
                """);
        Path contentDir = Files.createDirectory(dir.resolve("docs"));

        JavadocDocGenerator.Result result = JavadocDocGenerator.generate(new JavadocDocGenerator.Request(
                List.of(sourceFile), contentDir, "api/javadoc", List.of("api", "javadoc")));

        assertTrue(result.warnings().isEmpty(), "unexpected warnings: " + result.warnings());
        assertEquals(1, result.pageFiles().size());

        Path expectedPage = contentDir.resolve("api/javadoc/com/example/Widget.md");
        assertEquals(expectedPage, result.pageFiles().get(0));

        String page = Files.readString(expectedPage);
        assertTrue(page.contains("title: \"Widget\""), "expected the frontmatter title");
        assertTrue(page.contains("summary: \"A single widget on a shelf.\""), "expected the first-sentence summary");
        assertTrue(page.contains("tags: [api, javadoc]"), "expected the configured tags");
        assertTrue(page.contains("`com.example.Widget`"), "expected the fully qualified name");
        assertTrue(page.contains("Longer description with some `inline code` and a"), "expected {@code} rendered as inline code");
        assertTrue(page.contains("`java.util.List`"), "expected {@link} rendered as inline code with no cross-page resolution");

        assertTrue(page.contains("### `Widget(java.lang.String name)`"), "expected the constructor signature");
        assertTrue(page.contains("- **Parameter** `name` - the widget's display name"));

        assertTrue(page.contains("### `java.lang.String getName()`"), "expected the getter's signature");
        assertTrue(page.contains("- **Returns** the display name"));
        assertTrue(page.contains("- **Throws** `IllegalStateException` - if not yet initialized"));

        assertTrue(page.contains("### `java.lang.String name()`"));
        assertTrue(page.contains("**Deprecated.**"));

        assertFalse(page.contains("internalOnly"), "package-private members must never be documented");
    }

    @Test
    void generate_skipsNonPublicTopLevelTypes(@TempDir Path dir) throws IOException {
        Path sourceFile = writeSource(dir, "com/example/Hidden.java", """
                package com.example;

                /** Not part of the public API. */
                class Hidden {
                }
                """);
        Path contentDir = Files.createDirectory(dir.resolve("docs"));

        JavadocDocGenerator.Result result = JavadocDocGenerator.generate(
                new JavadocDocGenerator.Request(List.of(sourceFile), contentDir, "api/javadoc", List.of()));

        assertTrue(result.pageFiles().isEmpty());
    }

    @Test
    void generate_writesOnePagePerPublicTypeAcrossMultipleFiles(@TempDir Path dir) throws IOException {
        Path first = writeSource(dir, "com/example/First.java", """
                package com.example;

                /** First type. */
                public class First {
                }
                """);
        Path second = writeSource(dir, "com/example/Second.java", """
                package com.example;

                /** Second type. */
                public interface Second {
                }
                """);
        Path contentDir = Files.createDirectory(dir.resolve("docs"));

        JavadocDocGenerator.Result result = JavadocDocGenerator.generate(
                new JavadocDocGenerator.Request(List.of(first, second), contentDir, "api/javadoc", List.of()));

        assertEquals(2, result.pageFiles().size());
        assertTrue(Files.exists(contentDir.resolve("api/javadoc/com/example/First.md")));
        assertTrue(Files.exists(contentDir.resolve("api/javadoc/com/example/Second.md")));
    }

    private static Path writeSource(Path dir, String relativePath, String content) throws IOException {
        Path file = dir.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
        return file;
    }
}
