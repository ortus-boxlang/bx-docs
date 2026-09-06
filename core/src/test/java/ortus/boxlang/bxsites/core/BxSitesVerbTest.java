package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BxSitesVerbTest {

    @Test
    void verbId_matchesTheLiteralBxSitesCliToken() {
        // These exact strings are what gets passed to `module:bxSites <verb>`
        // - a typo here silently breaks the real CLI invocation.
        assertEquals("new", BxSitesVerb.NEW.verbId());
        assertEquals("build", BxSitesVerb.BUILD.verbId());
        assertEquals("serve", BxSitesVerb.SERVE.verbId());
        assertEquals("clean", BxSitesVerb.CLEAN.verbId());
        assertEquals("post:new", BxSitesVerb.POST_NEW.verbId());
        assertEquals("i18n:status", BxSitesVerb.I18N_STATUS.verbId());
    }

    @Test
    void hasOutput_reflectsWhetherAFixedOutputArtifactExistsToVerify() {
        assertTrue(BxSitesVerb.BUILD.hasOutput());
        assertTrue(BxSitesVerb.SEARCH_INDEX.hasOutput());
        assertFalse(BxSitesVerb.NEW.hasOutput());
        assertFalse(BxSitesVerb.SERVE.hasOutput());
        assertFalse(BxSitesVerb.CLEAN.hasOutput());
    }
}
