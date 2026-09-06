package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CleanMojoTest {

    @Test
    void execute_removesTheSiteDirectoryRecursively(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        Path siteDir = projectRoot.resolve("site");
        Files.createDirectories(siteDir.resolve("assets"));
        Files.writeString(siteDir.resolve("index.html"), "<html></html>");
        Files.writeString(siteDir.resolve("assets").resolve("style.css"), "body{}");

        runClean(projectRoot);

        assertFalse(Files.exists(siteDir), "site/ should be gone after clean");
    }

    @Test
    void execute_isANoOpWhenSiteDirectoryDoesNotExist(@TempDir Path projectRoot) throws ReflectiveOperationException {
        // Must not throw just because there's nothing to clean yet.
        runClean(projectRoot);

        assertTrue(true, "execute() completed without throwing");
    }

    private static void runClean(Path projectRoot) throws ReflectiveOperationException {
        CleanMojo mojo = new CleanMojo();
        Field field = CleanMojo.class.getDeclaredField("projectRoot");
        field.setAccessible(true);
        field.set(mojo, projectRoot.toFile());
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            throw new AssertionError("clean should not have failed", e);
        }
    }
}
