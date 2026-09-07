package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Fast, offline unit tests for {@link BuildMojo} - no Maven test harness
 * needed for logic this simple, just direct field access (Mojo parameters
 * are populated by Maven's own container at runtime, so tests set them
 * directly via reflection, the same pattern {@code maven-plugin-testing}
 * itself uses under the hood).
 */
class BuildMojoTest {

    @Test
    void verb_isBuild() {
        assertEquals(BxSitesVerb.BUILD, new BuildMojo().verb());
    }

    @Test
    void expectedOutputFile_isAlwaysProjectRootSlashSiteSlashIndexHtml_neverIndependentlyConfigurable(
            @TempDir Path projectRoot) throws ReflectiveOperationException {
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());

        File expected = projectRoot.resolve("site").resolve("index.html").toFile();

        assertEquals(expected, mojo.expectedOutputFile());
    }

    @Test
    void isUpToDate_falseWhenSiteDirDoesNotExistYet(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        writeFile(projectRoot.resolve("docs").resolve("index.md"), Instant.now());
        writeFile(projectRoot.resolve("bxsites.yaml"), Instant.now());
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());

        assertFalse(mojo.isUpToDate());
    }

    @Test
    void isUpToDate_trueWhenNothingChangedSinceTheLastBuild(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        Instant past = Instant.now().minusSeconds(3600);
        writeFile(projectRoot.resolve("docs").resolve("index.md"), past);
        writeFile(projectRoot.resolve("bxsites.yaml"), past);
        writeFile(projectRoot.resolve("site").resolve("index.html"), Instant.now());
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());

        assertTrue(mojo.isUpToDate());
    }

    @Test
    void isUpToDate_falseWhenAContentFileChangedAfterTheLastBuild(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        writeFile(projectRoot.resolve("site").resolve("index.html"), Instant.now().minusSeconds(3600));
        writeFile(projectRoot.resolve("docs").resolve("index.md"), Instant.now());
        writeFile(projectRoot.resolve("bxsites.yaml"), Instant.now().minusSeconds(3600));
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());

        assertFalse(mojo.isUpToDate());
    }

    @Test
    void isUpToDate_falseWhenForceRebuildIsSet_evenIfNothingChanged(@TempDir Path projectRoot)
            throws IOException, ReflectiveOperationException {
        Instant past = Instant.now().minusSeconds(3600);
        writeFile(projectRoot.resolve("docs").resolve("index.md"), past);
        writeFile(projectRoot.resolve("bxsites.yaml"), past);
        writeFile(projectRoot.resolve("site").resolve("index.html"), Instant.now());
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());
        setForceRebuild(mojo, true);

        assertFalse(mojo.isUpToDate());
    }

    @Test
    void isUpToDate_neverFallsBackToSrcMainJavaAsContent(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        // src/main/java is Java source in a typical Maven project, not
        // bx-sites content - a change there must never be treated as a
        // reason to consider the build stale, and its absence must never
        // count as "content unchanged" either.
        Instant past = Instant.now().minusSeconds(3600);
        writeFile(projectRoot.resolve("src").resolve("main").resolve("java").resolve("App.java"), Instant.now());
        writeFile(projectRoot.resolve("bxsites.yaml"), past);
        writeFile(projectRoot.resolve("site").resolve("index.html"), past);
        BuildMojo mojo = new BuildMojo();
        setProjectRoot(mojo, projectRoot.toFile());

        // No docs/ exists, so the (nonexistent) docs/ dir contributes no
        // input mtime - only bxsites.yaml does, and it's older than the
        // site output, so this should be up-to-date despite the Java
        // source file being brand new.
        assertTrue(mojo.isUpToDate());
    }

    private static void writeFile(Path file, Instant mtime) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, "content");
        Files.setLastModifiedTime(file, FileTime.from(mtime));
    }

    private static void setForceRebuild(BuildMojo mojo, boolean value) throws ReflectiveOperationException {
        Field field = BuildMojo.class.getDeclaredField("forceRebuild");
        field.setAccessible(true);
        field.set(mojo, value);
    }

    private static void setProjectRoot(AbstractBxSitesMojo mojo, File projectRoot) throws ReflectiveOperationException {
        Field field = AbstractBxSitesMojo.class.getDeclaredField("projectRoot");
        field.setAccessible(true);
        field.set(mojo, projectRoot);
    }
}
