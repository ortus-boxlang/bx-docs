package ortus.boxlang.bxsites.core.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OpenApiDocGeneratorTest {

    @Test
    void generate_throwsWhenSpecFileDoesNotExist(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "openapi: true\n");

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                dir.resolve("does-not-exist.json"), contentDir, configFile, "API Reference", Path.of("api", "openapi.md"), false);

        assertThrows(IllegalStateException.class, () -> OpenApiDocGenerator.generate(request));
    }

    @Test
    void generate_throwsWhenSpecFileIsEmpty(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "openapi: true\n");
        Path emptySpec = Files.createFile(dir.resolve("openapi.json"));

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                emptySpec, contentDir, configFile, "API Reference", Path.of("api", "openapi.md"), false);

        assertThrows(IllegalStateException.class, () -> OpenApiDocGenerator.generate(request));
    }

    @Test
    void generate_throwsWhenOpenApiIsNotEnabledAndAutoPatchIsOff(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "name: \"Test\"\n");
        Path spec = writeSpec(dir, "openapi.json", "{\"openapi\":\"3.0.0\"}");

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                spec, contentDir, configFile, "API Reference", Path.of("api", "openapi.md"), false);

        assertThrows(IllegalStateException.class, () -> OpenApiDocGenerator.generate(request));
    }

    @Test
    void generate_autoPatchesTheConfigWhenRequested(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "name: \"Test\"\n");
        Path spec = writeSpec(dir, "openapi.json", "{\"openapi\":\"3.0.0\"}");

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                spec, contentDir, configFile, "API Reference", Path.of("api", "openapi.md"), true);

        OpenApiDocGenerator.generate(request);

        assertTrue(OpenApiConfigFlag.isEnabled(configFile));
    }

    @Test
    void generate_copiesTheSpecAndWritesAWrapperPage(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "openapi: true\n");
        Path spec = writeSpec(dir, "my-spec.yaml", "openapi: 3.0.0\ninfo:\n  title: Bookshelf API\n");

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                spec, contentDir, configFile, "Bookshelf API", Path.of("api", "openapi.md"), false);

        OpenApiDocGenerator.Result result = OpenApiDocGenerator.generate(request);

        Path expectedSpec = contentDir.resolve("assets").resolve("openapi").resolve("openapi.yaml");
        assertEquals(expectedSpec, result.copiedSpecFile());
        assertEquals("openapi: 3.0.0\ninfo:\n  title: Bookshelf API\n", Files.readString(expectedSpec));
        assertEquals("assets/openapi/openapi.yaml", result.specAssetPath());

        Path expectedPage = contentDir.resolve("api").resolve("openapi.md");
        assertEquals(expectedPage, result.pageFile());
        String page = Files.readString(expectedPage);
        assertTrue(page.contains("title: \"Bookshelf API\""));
        assertTrue(page.contains("::: openapi src=\"assets/openapi/openapi.yaml\" title=\"Bookshelf API\""));
        assertTrue(page.contains(":::\n"));
    }

    @Test
    void generate_rejectsAnUnsupportedSpecExtension(@TempDir Path dir) throws IOException {
        Path contentDir = Files.createDirectory(dir.resolve("docs"));
        Path configFile = writeConfig(dir, "openapi: true\n");
        Path spec = writeSpec(dir, "spec.txt", "not a real spec");

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                spec, contentDir, configFile, "API Reference", Path.of("api", "openapi.md"), false);

        assertThrows(IllegalArgumentException.class, () -> OpenApiDocGenerator.generate(request));
    }

    private static Path writeConfig(Path dir, String content) throws IOException {
        Path file = dir.resolve("bxsites.yaml");
        Files.writeString(file, content);
        return file;
    }

    private static Path writeSpec(Path dir, String name, String content) throws IOException {
        Path file = dir.resolve(name);
        Files.writeString(file, content);
        return file;
    }
}
