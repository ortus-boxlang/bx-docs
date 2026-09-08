package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Fast, offline tests for {@link OpenApiMojo} - real file I/O against a
 * {@code @TempDir}, no Maven container needed for logic this simple (same
 * reflection-based parameter injection pattern as {@link BuildMojoTest}).
 */
class OpenApiMojoTest {

    @Test
    void execute_writesTheWrapperPageAndCopiesTheSpec(@TempDir Path projectRoot) throws Exception {
        Files.writeString(projectRoot.resolve("bxsites.yaml"), "name: \"Test\"\nopenapi: true\n");
        Path spec = projectRoot.resolve("openapi.json");
        Files.writeString(spec, "{\"openapi\":\"3.0.0\"}");

        OpenApiMojo mojo = new OpenApiMojo();
        setField(mojo, "projectRoot", projectRoot.toFile());
        setField(mojo, "specFile", spec.toFile());
        setField(mojo, "pageTitle", "Bookshelf API");
        setField(mojo, "pagePath", "api/openapi.md");
        setField(mojo, "autoPatchConfig", false);

        mojo.execute();

        Path page = projectRoot.resolve("docs").resolve("api").resolve("openapi.md");
        String content = Files.readString(page);
        assertTrue(content.contains("title: \"Bookshelf API\""));
        assertTrue(content.contains("::: openapi src=\"assets/openapi/openapi.json\" title=\"Bookshelf API\""));

        Path copiedSpec = projectRoot.resolve("docs").resolve("assets").resolve("openapi").resolve("openapi.json");
        assertTrue(Files.exists(copiedSpec));
    }

    @Test
    void execute_throwsAnActionableErrorWhenOpenApiIsNotEnabled(@TempDir Path projectRoot) throws IOException, ReflectiveOperationException {
        Files.writeString(projectRoot.resolve("bxsites.yaml"), "name: \"Test\"\n");
        Path spec = projectRoot.resolve("openapi.json");
        Files.writeString(spec, "{\"openapi\":\"3.0.0\"}");

        OpenApiMojo mojo = new OpenApiMojo();
        setField(mojo, "projectRoot", projectRoot.toFile());
        setField(mojo, "specFile", spec.toFile());
        setField(mojo, "pageTitle", "API Reference");
        setField(mojo, "pagePath", "api/openapi.md");
        setField(mojo, "autoPatchConfig", false);

        MojoExecutionException e = assertThrows(MojoExecutionException.class, mojo::execute);
        assertTrue(e.getCause().getMessage().contains("openapi: true is not set"));
    }

    private static void setField(OpenApiMojo mojo, String name, Object value) throws ReflectiveOperationException {
        Field field = OpenApiMojo.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(mojo, value);
    }
}
