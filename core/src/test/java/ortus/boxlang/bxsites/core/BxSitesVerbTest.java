package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("search-index", BxSitesVerb.SEARCH_INDEX.verbId());
        assertEquals("lint", BxSitesVerb.LINT.verbId());
        assertEquals("deploy", BxSitesVerb.DEPLOY.verbId());
        assertEquals("publish", BxSitesVerb.PUBLISH.verbId());
        assertEquals("package", BxSitesVerb.PACKAGE.verbId());
        assertEquals("stats", BxSitesVerb.STATS.verbId());
        assertEquals("doctor", BxSitesVerb.DOCTOR.verbId());
        assertEquals("post:new", BxSitesVerb.POST_NEW.verbId());
        assertEquals("i18n:status", BxSitesVerb.I18N_STATUS.verbId());
    }
}
