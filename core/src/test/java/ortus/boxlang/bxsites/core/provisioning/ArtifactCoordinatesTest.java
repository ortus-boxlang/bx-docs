package ortus.boxlang.bxsites.core.provisioning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Locks down the exact URL patterns confirmed directly against the live
 * download server during the M0 spike - including the checksum-extension
 * mismatch caught by a real functional-test failure (the with-deps zip
 * publishes {@code .sha512}, not {@code .sha-256} like the runtime jar).
 * A regression here would silently 404 in production the same way it did
 * the first time.
 */
class ArtifactCoordinatesTest {

    @Test
    void miniserverJarUrl_matchesConfirmedS3Layout() {
        assertEquals(
                "https://downloads.ortussolutions.com/ortussolutions/boxlang-runtimes/boxlang-miniserver/1.18.0-snapshot/boxlang-miniserver-1.18.0-snapshot.jar",
                ArtifactCoordinates.miniserverJarUrl("1.18.0-snapshot"));
    }

    @Test
    void miniserverJarChecksumUrl_isSha256() {
        assertEquals(
                ArtifactCoordinates.miniserverJarUrl("1.18.0-snapshot") + ".sha-256",
                ArtifactCoordinates.miniserverJarChecksumUrl("1.18.0-snapshot"));
    }

    @Test
    void bxSitesWithDepsZipUrl_matchesConfirmedS3Layout() {
        assertEquals(
                "https://downloads.ortussolutions.com/ortussolutions/boxlang-modules/bx-sites/1.0.0-snapshot/bx-sites-1.0.0-snapshot-with-deps.zip",
                ArtifactCoordinates.bxSitesWithDepsZipUrl("1.0.0-snapshot"));
    }

    @Test
    void bxSitesWithDepsZipChecksumUrl_isSha512NotSha256() {
        assertEquals(
                ArtifactCoordinates.bxSitesWithDepsZipUrl("1.0.0-snapshot") + ".sha512",
                ArtifactCoordinates.bxSitesWithDepsZipChecksumUrl("1.0.0-snapshot"));
    }

    @Test
    void bxSitesModuleMappingName_isBxsitesNotTheForgeboxSlug() {
        assertEquals("bxsites", ArtifactCoordinates.BXSITES_MODULE_MAPPING_NAME);
    }
}
