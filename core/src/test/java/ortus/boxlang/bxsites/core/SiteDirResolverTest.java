package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SiteDirResolverTest {

    @Test
    void resolve_isAlwaysProjectRootSlashSite(@TempDir Path projectRoot) {
        // bx-sites hardcodes this (BuildPipeline.bx: `var siteDir = root & "/site"`)
        // - no flag or config key overrides it, so this must never become
        // independently configurable in either plugin. See the plan/commit
        // history for the real bug this resolver was introduced to fix.
        assertEquals(projectRoot.resolve("site"), SiteDirResolver.resolve(projectRoot));
    }
}
