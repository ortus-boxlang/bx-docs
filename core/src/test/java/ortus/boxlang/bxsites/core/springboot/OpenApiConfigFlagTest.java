package ortus.boxlang.bxsites.core.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OpenApiConfigFlagTest {

    @Test
    void isEnabled_falseWhenConfigFileDoesNotExist(@TempDir Path dir) throws IOException {
        assertFalse(OpenApiConfigFlag.isEnabled(dir.resolve("bxsites.yaml")));
    }

    @Test
    void isEnabled_falseWhenKeyIsAbsent(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.yaml", "name: \"Test\"\n");

        assertFalse(OpenApiConfigFlag.isEnabled(config));
    }

    @Test
    void isEnabled_falseWhenKeyIsExplicitlyFalse(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.yaml", "name: \"Test\"\nopenapi: false\n");

        assertFalse(OpenApiConfigFlag.isEnabled(config));
    }

    @Test
    void isEnabled_trueForYaml(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.yaml", "name: \"Test\"\nopenapi: true\n");

        assertTrue(OpenApiConfigFlag.isEnabled(config));
    }

    @Test
    void isEnabled_trueForJson(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.json", "{ \"name\": \"Test\", \"openapi\": true }");

        assertTrue(OpenApiConfigFlag.isEnabled(config));
    }

    @Test
    void isEnabled_trueForToml(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.toml", "name = \"Test\"\nopenapi = true\n");

        assertTrue(OpenApiConfigFlag.isEnabled(config));
    }

    @Test
    void enable_appendsTheKeyWhenAbsentFromYaml(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.yaml", "name: \"Test\"\n");

        OpenApiConfigFlag.enable(config);

        assertTrue(OpenApiConfigFlag.isEnabled(config));
        assertEquals("name: \"Test\"\nopenapi: true\n", Files.readString(config));
    }

    @Test
    void enable_flipsAnExistingFalseToTrue(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.yaml", "name: \"Test\"\nopenapi: false\ntheme: bootstrap\n");

        OpenApiConfigFlag.enable(config);

        assertEquals("name: \"Test\"\nopenapi: true\ntheme: bootstrap\n", Files.readString(config));
    }

    @Test
    void enable_usesEqualsSyntaxForToml(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.toml", "name = \"Test\"\n");

        OpenApiConfigFlag.enable(config);

        assertEquals("name = \"Test\"\nopenapi = true\n", Files.readString(config));
    }

    @Test
    void enable_refusesToPatchJson(@TempDir Path dir) throws IOException {
        Path config = writeConfig(dir, "bxsites.json", "{ \"name\": \"Test\" }");

        assertThrows(UnsupportedOperationException.class, () -> OpenApiConfigFlag.enable(config));
    }

    @Test
    void enable_throwsWhenNoConfigFileExistsYet(@TempDir Path dir) {
        assertThrows(IllegalStateException.class, () -> OpenApiConfigFlag.enable(dir.resolve("bxsites.yaml")));
    }

    private static Path writeConfig(Path dir, String name, String content) throws IOException {
        Path file = dir.resolve(name);
        Files.writeString(file, content);
        return file;
    }
}
