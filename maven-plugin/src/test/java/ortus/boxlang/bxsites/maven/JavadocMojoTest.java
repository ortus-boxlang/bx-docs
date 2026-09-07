package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Real, offline exercise of {@link JavadocMojo} against a real on-disk
 * {@code .java} source tree - same reflection-based parameter injection
 * pattern as {@link BuildMojoTest}.
 */
class JavadocMojoTest {

    @Test
    void execute_writesOnePagePerPublicTypeUnderEverySourceRoot(@TempDir Path projectRoot) throws Exception {
        Path sourceRoot = projectRoot.resolve("src").resolve("main").resolve("java");
        Path sourceFile = sourceRoot.resolve("com").resolve("example").resolve("Widget.java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, """
                package com.example;

                /** A single widget on a shelf. */
                public class Widget {
                }
                """);

        JavadocMojo mojo = new JavadocMojo();
        setField(mojo, "projectRoot", projectRoot.toFile());
        setField(mojo, "sourceRoots", List.of(sourceRoot.toString()));
        setField(mojo, "pagePathPrefix", "api/javadoc");
        setField(mojo, "tags", List.of("api", "javadoc"));

        mojo.execute();

        Path page = projectRoot.resolve("docs").resolve("api").resolve("javadoc")
                .resolve("com").resolve("example").resolve("Widget.md");
        String content = Files.readString(page);
        assertTrue(content.contains("title: \"Widget\""));
        assertTrue(content.contains("A single widget on a shelf."));
    }

    @Test
    void execute_ignoresANonExistentSourceRoot(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        JavadocMojo mojo = new JavadocMojo();
        setField(mojo, "projectRoot", projectRoot.toFile());
        setField(mojo, "sourceRoots", List.of(projectRoot.resolve("does-not-exist").toString()));
        setField(mojo, "pagePathPrefix", "api/javadoc");
        setField(mojo, "tags", List.of("api", "javadoc"));

        assertDoesNotThrow(mojo::execute);
    }

    private static void setField(JavadocMojo mojo, String name, Object value) throws ReflectiveOperationException {
        Field field = JavadocMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mojo, value);
    }
}
