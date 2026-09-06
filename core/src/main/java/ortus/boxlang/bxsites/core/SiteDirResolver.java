package ortus.boxlang.bxsites.core;

import java.nio.file.Path;

/**
 * Where a bx-sites build writes its output - confirmed fixed and
 * non-configurable in bx-sites itself (`models/build/BuildPipeline.bx`
 * hardcodes {@code var siteDir = root & "/site"}, with no CLI flag or
 * config key to override it). Both plugins compute this the same way
 * rather than exposing it as an independently user-settable property -
 * setting it to anything else would silently point Gradle's/Maven's own
 * up-to-date checks at a directory bx-sites never actually writes to.
 */
public final class SiteDirResolver {

    private SiteDirResolver() {
    }

    public static Path resolve(Path projectRoot) {
        return projectRoot.resolve("site");
    }
}
