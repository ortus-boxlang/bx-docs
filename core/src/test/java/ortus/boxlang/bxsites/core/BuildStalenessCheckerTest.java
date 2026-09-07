package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BuildStalenessCheckerTest {

    @Test
    void notUpToDateWhenSiteDirDoesNotExistYet(@TempDir Path projectRoot) throws IOException {
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", Instant.now());
        Path configFile = createFile(projectRoot.resolve("bxsites.yaml"), Instant.now());
        Path siteDir = projectRoot.resolve("site");

        assertFalse(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    @Test
    void notUpToDateWhenSiteDirIsEmpty(@TempDir Path projectRoot) throws IOException {
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", Instant.now());
        Path configFile = createFile(projectRoot.resolve("bxsites.yaml"), Instant.now());
        Path siteDir = Files.createDirectory(projectRoot.resolve("site"));

        assertFalse(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    @Test
    void upToDateWhenEveryInputIsOlderThanTheSiteOutput(@TempDir Path projectRoot) throws IOException {
        Instant past = Instant.now().minusSeconds(3600);
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", past);
        Path configFile = createFile(projectRoot.resolve("bxsites.yaml"), past);
        Path siteDir = createDirWithFile(projectRoot.resolve("site"), "index.html", Instant.now());

        assertTrue(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    @Test
    void notUpToDateWhenAContentFileIsNewerThanTheSiteOutput(@TempDir Path projectRoot) throws IOException {
        Path siteDir = createDirWithFile(projectRoot.resolve("site"), "index.html", Instant.now().minusSeconds(3600));
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", Instant.now());
        Path configFile = createFile(projectRoot.resolve("bxsites.yaml"), Instant.now().minusSeconds(3600));

        assertFalse(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    @Test
    void notUpToDateWhenTheConfigFileIsNewerThanTheSiteOutput(@TempDir Path projectRoot) throws IOException {
        Path siteDir = createDirWithFile(projectRoot.resolve("site"), "index.html", Instant.now().minusSeconds(3600));
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", Instant.now().minusSeconds(3600));
        Path configFile = createFile(projectRoot.resolve("bxsites.yaml"), Instant.now());

        assertFalse(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    @Test
    void notUpToDateWhenNoConfigFileExistsYet(@TempDir Path projectRoot) throws IOException {
        Path contentDir = createDirWithFile(projectRoot.resolve("docs"), "index.md", Instant.now().minusSeconds(3600));
        Path configFile = projectRoot.resolve("bxsites.yaml");
        Path siteDir = createDirWithFile(projectRoot.resolve("site"), "index.html", Instant.now());

        assertTrue(BuildStalenessChecker.isUpToDate(contentDir, configFile, siteDir));
    }

    private static Path createDirWithFile(Path dir, String fileName, Instant mtime) throws IOException {
        Files.createDirectories(dir);
        return createFile(dir.resolve(fileName), mtime).getParent();
    }

    private static Path createFile(Path file, Instant mtime) throws IOException {
        Files.writeString(file, "content");
        Files.setLastModifiedTime(file, FileTime.from(mtime));
        return file;
    }
}
