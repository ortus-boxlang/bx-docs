package ortus.boxlang.bxsites.core.provisioning;

/**
 * URL construction for the two artifacts {@link Provisioner} downloads.
 * Kept as a small, pure, easily unit-testable class separate from any actual
 * network I/O.
 *
 * <p>Both artifacts are published at stable, predictable URLs on Ortus's own
 * download server - no ForgeBox/CommandBox CLI required to resolve them. See
 * the plan's "Distribution artifacts" notes for how this was confirmed.
 */
public final class ArtifactCoordinates {

    private static final String DOWNLOADS_BASE = "https://downloads.ortussolutions.com/ortussolutions";

    private ArtifactCoordinates() {
    }

    /**
     * The boxlang-miniserver jar for a given version - a self-contained,
     * runnable jar that bundles the full BoxLang CLI runtime
     * ({@code ortus.boxlang.runtime.BoxRunner}) alongside its own embedded
     * web-server entrypoint. For now this should be pointed at a
     * {@code <version>-snapshot} revision (module-inception support isn't in
     * a stable release yet - see the plan for why).
     */
    public static String miniserverJarUrl(String version) {
        return DOWNLOADS_BASE + "/boxlang-runtimes/boxlang-miniserver/" + version
                + "/boxlang-miniserver-" + version + ".jar";
    }

    /** SHA-256, confirmed - see {@code boxlang-runtimes} artifacts' own published checksum extension. */
    public static String miniserverJarChecksumUrl(String version) {
        return miniserverJarUrl(version) + ".sha-256";
    }

    /**
     * The bx-sites {@code -with-deps} bundle for a given version - bx-sites
     * itself plus every one of its runtime dependencies, pre-installed into
     * a nested {@code modules/} folder via BoxLang's module-inception
     * mechanism. Unzips as one self-contained unit; see {@link Provisioner}.
     */
    public static String bxSitesWithDepsZipUrl(String version) {
        return DOWNLOADS_BASE + "/boxlang-modules/bx-sites/" + version
                + "/bx-sites-" + version + "-with-deps.zip";
    }

    /**
     * SHA-512, confirmed against live artifacts - {@code Build.bx}'s own
     * {@code bundleDependencies()} publishes {@code .sha512}/{@code .md5}
     * for this bundle, NOT {@code .sha-256} (the boxlang-runtimes artifacts'
     * own convention). The two artifact families are checksummed
     * differently - don't assume one convention applies to both.
     */
    public static String bxSitesWithDepsZipChecksumUrl(String version) {
        return bxSitesWithDepsZipUrl(version) + ".sha512";
    }

    /** bx-sites' own confirmed BoxLang module mapping name - see AGENTS.md. */
    public static final String BXSITES_MODULE_MAPPING_NAME = "bxsites";
}
