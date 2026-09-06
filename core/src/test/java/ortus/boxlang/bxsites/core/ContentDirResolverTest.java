package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContentDirResolverTest {

    @Test
    void defaultsToDocsWhenNeitherExists(@TempDir Path projectRoot) {
        assertEquals(projectRoot.resolve("docs"), ContentDirResolver.resolve(projectRoot));
    }

    @Test
    void prefersDocsWhenBothExist(@TempDir Path projectRoot) throws IOException {
        Files.createDirectory(projectRoot.resolve("docs"));
        Files.createDirectory(projectRoot.resolve("src"));

        assertEquals(projectRoot.resolve("docs"), ContentDirResolver.resolve(projectRoot));
    }

    @Test
    void fallsBackToSrcWhenOnlySrcExists(@TempDir Path projectRoot) throws IOException {
        Files.createDirectory(projectRoot.resolve("src"));

        assertEquals(projectRoot.resolve("src"), ContentDirResolver.resolve(projectRoot));
    }
}
