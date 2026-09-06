package ortus.boxlang.bxsites.core;

import java.nio.file.Path;

/**
 * Plain, build-tool-agnostic configuration for a single bx-sites invocation.
 * Both the Gradle extension and the Maven plugin's {@code <configuration>}
 * block are thin, tool-native wrappers that ultimately build one of these.
 *
 * @param projectRoot            the target bx-sites project root (passed to
 *                               the CLI as {@code --projectRoot})
 * @param boxlangMiniserverVersion the pinned boxlang-miniserver version (see
 *                               {@link ArtifactCoordinates})
 * @param bxSitesVersion         the pinned bx-sites version (resolves the
 *                               {@code -with-deps} bundle)
 * @param boxlangHomeDir         where this project's own {@code BOXLANG_HOME}
 *                               should be assembled (per-project, not shared)
 * @param cacheDir               the shared, cross-project download cache
 */
public record BxSitesConfig(
        Path projectRoot,
        String boxlangMiniserverVersion,
        String bxSitesVersion,
        Path boxlangHomeDir,
        Path cacheDir) {

    /** Default cross-project cache directory: {@code ~/.bxsites/cache}. */
    public static Path defaultCacheDir() {
        return Path.of(System.getProperty("user.home"), ".bxsites", "cache");
    }
}
