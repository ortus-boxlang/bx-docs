package ortus.boxlang.bxsites.core.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ortus.boxlang.bxsites.core.springboot.fixtures.BookController;

/**
 * Scans this module's own real, compiled test-fixture classes (annotated
 * with the real Spring annotations, resolved by
 * {@link Thread#getContextClassLoader()} - both are genuinely on the test
 * classpath, so this exercises {@link ControllerScanGenerator} exactly as
 * the forked-JVM {@link ControllerScanMain} would, no mocking of Spring's
 * annotation types.
 */
class ControllerScanGeneratorTest {

    @Test
    void scan_writesNothingWhenClassesDirDoesNotExist(@TempDir Path dir) throws IOException {
        ControllerScanGenerator.Result result = ControllerScanGenerator.scan(
                new ControllerScanGenerator.Request(dir.resolve("does-not-exist"), dir, "api/controllers", List.of()),
                Thread.currentThread().getContextClassLoader());

        assertTrue(result.pageFiles().isEmpty());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void scan_findsRealSpringControllersAmongTheFixtureClasses() throws IOException, URISyntaxException {
        Path classesDir = fixturesClassesDir();
        Path contentDir = Files.createTempDirectory("bxsites-controller-scan-test");

        ControllerScanGenerator.Result result = ControllerScanGenerator.scan(
                new ControllerScanGenerator.Request(classesDir, contentDir, "api/controllers", List.of("api", "controllers")),
                Thread.currentThread().getContextClassLoader());

        assertTrue(result.warnings().isEmpty(), "unexpected warnings: " + result.warnings());
        assertEquals(2, result.pageFiles().size(), "expected exactly BookController and PlainController, not NotAController");

        Path bookControllerPage = contentDir.resolve("api/controllers")
                .resolve("ortus/boxlang/bxsites/core/springboot/fixtures/BookController.md");
        assertTrue(result.pageFiles().contains(bookControllerPage));
        String page = Files.readString(bookControllerPage);
        assertTrue(page.contains("title: \"BookController\""));
        assertTrue(page.contains("tags: [api, controllers]"));
        assertTrue(page.contains("| GET | `/api/books` |"), "expected the bare @GetMapping combined with the class-level base path");
        assertTrue(page.contains("| GET | `/api/books/{id}` |"), "expected the @GetMapping(\"/{id}\") combined with the base path");
        assertTrue(page.contains("| POST | `/api/books` |"), "expected the @PostMapping endpoint");

        Path plainControllerPage = contentDir.resolve("api/controllers")
                .resolve("ortus/boxlang/bxsites/core/springboot/fixtures/PlainController.md");
        assertTrue(Files.readString(plainControllerPage).contains("_No mapped endpoints found._"));

        Path notAControllerPage = contentDir.resolve("api/controllers")
                .resolve("ortus/boxlang/bxsites/core/springboot/fixtures/NotAController.md");
        assertFalse(Files.exists(notAControllerPage), "a plain, unannotated class must never get a page");
    }

    private static Path fixturesClassesDir() throws URISyntaxException {
        URL classUrl = BookController.class.getResource(BookController.class.getSimpleName() + ".class");
        Path packageDir = Path.of(classUrl.toURI()).getParent();
        String[] packageSegments = BookController.class.getPackageName().split("\\.");
        Path classesDir = packageDir;
        for (String ignored : packageSegments) {
            classesDir = classesDir.getParent();
        }
        return classesDir;
    }
}
