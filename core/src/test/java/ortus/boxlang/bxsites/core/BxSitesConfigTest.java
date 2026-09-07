package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class BxSitesConfigTest {

    @Test
    void defaultCacheDir_isUnderUserHomeDotBxsitesCache_whenNoOverrideIsSet() {
        // BXSITES_CACHE_DIR isn't set in this test environment; if it ever is,
        // this assertion would need an env-var-aware fixture instead.
        Path expected = Path.of(System.getProperty("user.home"), ".bxsites", "cache");

        assertEquals(expected, BxSitesConfig.defaultCacheDir());
    }
}
