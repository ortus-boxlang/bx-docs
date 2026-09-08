package ortus.boxlang.bxsites.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Covers the two BoxLang doc-generation goals - which verb each wraps, and
 * how their typed parameters turn into verb flags. Unlike the Spring Boot
 * goals these are verb wrappers, so the interesting behavior is entirely
 * in the arguments they build.
 */
class BoxLangMojosTest {

    @Test
    void docBoxMojo_wrapsTheDocBoxVerb() {
        assertEquals(BxSitesVerb.DOCBOX, new DocBoxMojo().verb());
    }

    @Test
    void coldBoxMojo_wrapsTheColdBoxVerb() {
        assertEquals(BxSitesVerb.COLDBOX, new ColdBoxMojo().verb());
    }

    @Test
    void docBoxMojo_passesNothingWhenNothingIsConfigured() {
        assertTrue(new DocBoxMojo().verbArguments().isEmpty());
    }

    @Test
    void docBoxMojo_turnsItsParametersIntoVerbFlags() {
        DocBoxMojo mojo = new DocBoxMojo();
        mojo.mappings = Map.of("models", "models");
        mojo.projectTitle = "Bookshelf API";
        mojo.pagePathPrefix = "api/classes";
        mojo.tags = List.of("api", "classes");

        List<String> args = mojo.verbArguments();

        assertTrue(args.contains("--mappings:models=models"));
        assertTrue(args.contains("--projectTitle=Bookshelf API"));
        assertTrue(args.contains("--pagePathPrefix=api/classes"));
        assertTrue(args.contains("--tags=api,classes"));
        // Unset options are never passed, so the project's own bxsites.yaml
        // still decides them.
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--excludes")));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--jsonDir")));
    }

    @Test
    void docBoxMojo_keepsExtraArgsAlongsideItsOwnFlags() {
        DocBoxMojo mojo = new DocBoxMojo();
        mojo.extraArgs = List.of("--verbose");
        mojo.projectTitle = "Bookshelf API";

        List<String> args = mojo.verbArguments();

        assertTrue(args.contains("--verbose"));
        assertTrue(args.contains("--projectTitle=Bookshelf API"));
    }

    @Test
    void coldBoxMojo_passesNothingWhenNothingIsConfigured() {
        assertTrue(new ColdBoxMojo().verbArguments().isEmpty());
    }

    @Test
    void coldBoxMojo_turnsItsParametersIntoVerbFlags() {
        ColdBoxMojo mojo = new ColdBoxMojo();
        mojo.appRoot = "app";
        mojo.include = List.of("routes", "handlers");
        mojo.tags = List.of("api", "coldbox");

        List<String> args = mojo.verbArguments();

        assertTrue(args.contains("--appRoot=app"));
        assertTrue(args.contains("--include=routes,handlers"));
        assertTrue(args.contains("--tags=api,coldbox"));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--pagePathPrefix")));
    }

    @Test
    void bothMojos_haveNoExpectedOutputFile() {
        // Where pages land depends on the project's own config, so there is
        // no single file whose existence would prove the run succeeded.
        assertEquals(null, new DocBoxMojo().expectedOutputFile());
        assertEquals(null, new ColdBoxMojo().expectedOutputFile());
    }
}
