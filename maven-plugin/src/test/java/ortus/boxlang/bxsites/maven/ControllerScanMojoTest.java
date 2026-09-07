package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Only the classesDir-missing no-op path is covered here, offline - the
 * real forked-JVM scan (constructing a live {@code MavenProject} with a
 * resolved runtime classpath isn't practical to fake convincingly) was
 * verified for real instead: a scratch project with a real spring-web
 * dependency, a real compiled {@code @RestController}, and this plugin's
 * own installed jar, run via {@code mvn ...:controller-scan} then
 * {@code ...:build} - confirmed a correct page and a real site build
 * including it.
 */
class ControllerScanMojoTest {

    @Test
    void execute_isANoOpWhenClassesDirDoesNotExist(@TempDir Path projectRoot) throws ReflectiveOperationException {
        ControllerScanMojo mojo = new ControllerScanMojo();
        setField(mojo, "projectRoot", projectRoot.toFile());
        setField(mojo, "classesDir", projectRoot.resolve("does-not-exist").toFile());
        setField(mojo, "pagePathPrefix", "api/controllers");
        setField(mojo, "tags", List.of("api", "controllers"));

        assertDoesNotThrow(mojo::execute);
    }

    private static void setField(ControllerScanMojo mojo, String name, Object value) throws ReflectiveOperationException {
        Field field = ControllerScanMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mojo, value);
    }
}
