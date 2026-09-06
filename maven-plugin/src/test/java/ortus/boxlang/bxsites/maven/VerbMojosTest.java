package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** Locks down each simple Mojo's verb mapping - a typo here silently wraps the wrong bx-sites verb. */
class VerbMojosTest {

    @Test
    void newMojo_wrapsTheNewVerb() {
        assertEquals(BxSitesVerb.NEW, new NewMojo().verb());
    }

    @Test
    void serveMojo_wrapsTheServeVerb() {
        assertEquals(BxSitesVerb.SERVE, new ServeMojo().verb());
    }

    @Test
    void newMojo_hasNoExpectedOutputFile() {
        assertNull(new NewMojo().expectedOutputFile());
    }

    @Test
    void serveMojo_hasNoExpectedOutputFile() {
        assertNull(new ServeMojo().expectedOutputFile());
    }
}
