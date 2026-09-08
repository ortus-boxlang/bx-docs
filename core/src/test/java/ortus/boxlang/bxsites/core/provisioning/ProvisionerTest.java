package ortus.boxlang.bxsites.core.provisioning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProvisionerTest {

    private static final String MINISERVER_URL = ArtifactCoordinates.miniserverJarUrl("1.18.0-snapshot");
    private static final String WITH_DEPS_URL = ArtifactCoordinates.bxSitesWithDepsZipUrl("1.0.0-snapshot");

    @TempDir
    Path cacheDir;

    @Test
    void resolveMiniserverJar_downloadsAndCachesOnFirstCall(@TempDir Path scratch) throws IOException {
        FakeDownloader downloader = new FakeDownloader();
        downloader.put(MINISERVER_URL, "fake-jar-bytes".getBytes(StandardCharsets.UTF_8));
        Provisioner provisioner = new Provisioner(downloader, cacheDir);

        Path jar = provisioner.resolveMiniserverJar("1.18.0-snapshot");

        assertTrue(Files.exists(jar));
        assertEquals("fake-jar-bytes", Files.readString(jar));
        // one fetch for the jar, one for its checksum file
        assertEquals(2, downloader.fetchCount());
    }

    @Test
    void resolveMiniserverJar_secondCallIsCachedAndDoesNotRefetch() {
        FakeDownloader downloader = new FakeDownloader();
        downloader.put(MINISERVER_URL, "fake-jar-bytes".getBytes(StandardCharsets.UTF_8));
        Provisioner provisioner = new Provisioner(downloader, cacheDir);

        provisioner.resolveMiniserverJar("1.18.0-snapshot");
        int fetchesAfterFirstCall = downloader.fetchCount();
        provisioner.resolveMiniserverJar("1.18.0-snapshot");

        assertEquals(fetchesAfterFirstCall, downloader.fetchCount(), "second call should hit the cache, not the network");
    }

    @Test
    void resolveMiniserverJar_rejectsChecksumMismatch() {
        Provisioner provisioner = new Provisioner(new Downloader() {
            @Override
            public byte[] fetch(String url) {
                if (url.equals(MINISERVER_URL)) {
                    return "fake-jar-bytes".getBytes(StandardCharsets.UTF_8);
                }
                if (url.equals(MINISERVER_URL + ".sha-256")) {
                    return "0000000000000000000000000000000000000000000000000000000000000000  wrong\n"
                            .getBytes(StandardCharsets.UTF_8);
                }
                throw new IllegalStateException("unexpected url " + url);
            }
        }, cacheDir);

        assertThrows(IllegalStateException.class, () -> provisioner.resolveMiniserverJar("1.18.0-snapshot"));
    }

    @Test
    void provisionBoxlangHome_unzipsWithDepsZipAsOneSelfContainedUnit(@TempDir Path boxlangHomeDir) throws IOException {
        byte[] fakeZip = buildFakeWithDepsZip();
        FakeDownloader downloader = new FakeDownloader();
        downloader.putWithSha512(WITH_DEPS_URL, fakeZip);
        Provisioner provisioner = new Provisioner(downloader, cacheDir);

        Path result = provisioner.provisionBoxlangHome("1.0.0-snapshot", boxlangHomeDir);

        assertEquals(boxlangHomeDir, result);
        Path bxsitesModuleDir = boxlangHomeDir.resolve("modules").resolve("bxsites");
        assertTrue(Files.exists(bxsitesModuleDir.resolve("ModuleConfig.bx")), "bx-sites' own ModuleConfig.bx should be at the module root");
        assertTrue(Files.exists(bxsitesModuleDir.resolve("modules").resolve("bx-markdown").resolve("ModuleConfig.bx")),
                "nested dependency should land under the bx-sites module's own modules/ folder (module inception)");
    }

    @Test
    void provisionBoxlangHome_isIdempotentAndDoesNotRedownload(@TempDir Path boxlangHomeDir) {
        byte[] fakeZip = buildFakeWithDepsZip();
        FakeDownloader downloader = new FakeDownloader();
        downloader.putWithSha512(WITH_DEPS_URL, fakeZip);
        Provisioner provisioner = new Provisioner(downloader, cacheDir);

        provisioner.provisionBoxlangHome("1.0.0-snapshot", boxlangHomeDir);
        int fetchesAfterFirstCall = downloader.fetchCount();
        provisioner.provisionBoxlangHome("1.0.0-snapshot", boxlangHomeDir);

        assertEquals(fetchesAfterFirstCall, downloader.fetchCount(), "a second provision of an already-provisioned home should be a no-op");
    }

    private static byte[] buildFakeWithDepsZip() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                writeEntry(zos, "ModuleConfig.bx", "this.mapping = \"bxsites\";");
                writeEntry(zos, "box.json", "{\"slug\":\"bx-sites\"}");
                writeEntry(zos, "modules/bx-markdown/ModuleConfig.bx", "this.mapping = \"bxMarkdown\";");
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeEntry(ZipOutputStream zos, String name, String content) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }
}
