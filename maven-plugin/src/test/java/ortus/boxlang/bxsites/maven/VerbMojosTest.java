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

    @Test
    void searchIndexMojo_wrapsTheSearchIndexVerb() {
        assertEquals(BxSitesVerb.SEARCH_INDEX, new SearchIndexMojo().verb());
    }

    @Test
    void lintMojo_wrapsTheLintVerb() {
        assertEquals(BxSitesVerb.LINT, new LintMojo().verb());
    }

    @Test
    void deployMojo_wrapsTheDeployVerb() {
        assertEquals(BxSitesVerb.DEPLOY, new DeployMojo().verb());
    }

    @Test
    void publishMojo_wrapsThePublishVerb() {
        assertEquals(BxSitesVerb.PUBLISH, new PublishMojo().verb());
    }

    @Test
    void packageMojo_wrapsThePackageVerb() {
        assertEquals(BxSitesVerb.PACKAGE, new PackageMojo().verb());
    }

    @Test
    void statsMojo_wrapsTheStatsVerb() {
        assertEquals(BxSitesVerb.STATS, new StatsMojo().verb());
    }

    @Test
    void doctorMojo_wrapsTheDoctorVerb() {
        assertEquals(BxSitesVerb.DOCTOR, new DoctorMojo().verb());
    }

    @Test
    void fastFollowMojos_haveNoExpectedOutputFile() {
        assertNull(new SearchIndexMojo().expectedOutputFile());
        assertNull(new LintMojo().expectedOutputFile());
        assertNull(new DeployMojo().expectedOutputFile());
        assertNull(new PublishMojo().expectedOutputFile());
        assertNull(new PackageMojo().expectedOutputFile());
        assertNull(new StatsMojo().expectedOutputFile());
        assertNull(new DoctorMojo().expectedOutputFile());
    }
}
