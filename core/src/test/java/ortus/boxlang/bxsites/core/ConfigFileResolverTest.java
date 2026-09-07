package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigFileResolverTest {

    @Test
    void defaultsToBxsitesYamlWhenNoneExist(@TempDir Path projectRoot) {
        assertEquals(projectRoot.resolve("bxsites.yaml"), ConfigFileResolver.resolve(projectRoot));
    }

    @Test
    void prefersYamlOverAllOtherFormats(@TempDir Path projectRoot) throws IOException {
        Files.createFile(projectRoot.resolve("bxsites.yaml"));
        Files.createFile(projectRoot.resolve("bxsites.yml"));
        Files.createFile(projectRoot.resolve("bxsites.toml"));
        Files.createFile(projectRoot.resolve("bxsites.json"));

        assertEquals(projectRoot.resolve("bxsites.yaml"), ConfigFileResolver.resolve(projectRoot));
    }

    @Test
    void fallsBackToYmlWhenYamlIsAbsent(@TempDir Path projectRoot) throws IOException {
        Files.createFile(projectRoot.resolve("bxsites.yml"));
        Files.createFile(projectRoot.resolve("bxsites.toml"));
        Files.createFile(projectRoot.resolve("bxsites.json"));

        assertEquals(projectRoot.resolve("bxsites.yml"), ConfigFileResolver.resolve(projectRoot));
    }

    @Test
    void fallsBackToTomlWhenYamlAndYmlAreAbsent(@TempDir Path projectRoot) throws IOException {
        Files.createFile(projectRoot.resolve("bxsites.toml"));
        Files.createFile(projectRoot.resolve("bxsites.json"));

        assertEquals(projectRoot.resolve("bxsites.toml"), ConfigFileResolver.resolve(projectRoot));
    }

    @Test
    void fallsBackToJsonWhenOnlyJsonExists(@TempDir Path projectRoot) throws IOException {
        Files.createFile(projectRoot.resolve("bxsites.json"));

        assertEquals(projectRoot.resolve("bxsites.json"), ConfigFileResolver.resolve(projectRoot));
    }
}
