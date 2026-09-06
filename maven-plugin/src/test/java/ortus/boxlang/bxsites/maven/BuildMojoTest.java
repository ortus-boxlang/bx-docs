package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

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

    private static void setProjectRoot(AbstractBxSitesMojo mojo, File projectRoot) throws ReflectiveOperationException {
        Field field = AbstractBxSitesMojo.class.getDeclaredField("projectRoot");
        field.setAccessible(true);
        field.set(mojo, projectRoot);
    }
}
