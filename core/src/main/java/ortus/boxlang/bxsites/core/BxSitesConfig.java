package ortus.boxlang.bxsites.core;

import java.nio.file.Path;

/**
 * Small, shared constants both plugins pull their own defaults from, so a
 * pinned-version bump or a cache-location change is a one-place edit instead
 * of drifting between the Gradle extension and the Maven Mojo parameters.
 */
public final class BxSitesConfig {

    /** Pinned default - see core/README.md's "Provisioning subsystem" note for why this must stay a snapshot for now. */
    public static final String DEFAULT_MINISERVER_VERSION = "1.18.0-snapshot";

    /** Pinned default - resolves the {@code -with-deps} bundle. */
    public static final String DEFAULT_BXSITES_VERSION = "1.0.0-snapshot";

    private BxSitesConfig() {
    }

    /**
     * The shared, cross-project download cache directory - a machine with
     * many bx-sites-enabled projects only downloads each pinned version
     * once, regardless of which build tool or how many projects use it.
     *
     * <p>Overridable via the {@code BXSITES_CACHE_DIR} environment variable
     * (e.g. to point it at a CI-cacheable path, or somewhere writable in a
     * container where {@code $HOME} isn't) - defaults to
     * {@code ~/.bxsites/cache} otherwise.
     */
    public static Path defaultCacheDir() {
        String override = System.getenv("BXSITES_CACHE_DIR");
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        return Path.of(System.getProperty("user.home"), ".bxsites", "cache");
    }
}
