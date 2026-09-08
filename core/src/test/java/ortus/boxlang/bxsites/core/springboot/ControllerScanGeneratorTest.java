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

        // The filter toolbar - one chip per HTTP method actually present.
        assertTrue(page.contains("x-data=\"{ q: '', k: 'all' }\""), "expected the Alpine filter toolbar");
        assertTrue(page.contains("@click=\"k='get'\">GET</button>"), "expected a GET chip");
        assertTrue(page.contains("@click=\"k='post'\">POST</button>"), "expected a POST chip");
        assertFalse(page.contains("@click=\"k='delete'\""), "no DELETE endpoint exists on this fixture - no DELETE chip");

        // Each row carries its own kind/search-name for the toolbar to filter on -
        // the bare @GetMapping combined with the class-level base path.
        assertTrue(page.contains("data-k=\"get\" data-n=\"/api/books String list()\""));
        assertTrue(page.contains("<td>GET</td><td><code>/api/books</code></td><td><code>String list()</code></td>"));
        // @GetMapping("/{id}") combined with the base path.
        assertTrue(page.contains("data-k=\"get\" data-n=\"/api/books/{id} String getOne(String)\""));
        assertTrue(page.contains("<td>GET</td><td><code>/api/books/{id}</code></td><td><code>String getOne(String)</code></td>"));
        // @PostMapping endpoint.
        assertTrue(page.contains("data-k=\"post\" data-n=\"/api/books String create(String)\""));
        assertTrue(page.contains("<td>POST</td><td><code>/api/books</code></td><td><code>String create(String)</code></td>"));

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
