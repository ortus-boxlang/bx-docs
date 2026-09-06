package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class BxSitesConfigTest {

    @Test
    void defaultCacheDir_isUnderUserHomeDotBxsitesCache() {
        Path expected = Path.of(System.getProperty("user.home"), ".bxsites", "cache");

        assertEquals(expected, BxSitesConfig.defaultCacheDir());
    }
}
