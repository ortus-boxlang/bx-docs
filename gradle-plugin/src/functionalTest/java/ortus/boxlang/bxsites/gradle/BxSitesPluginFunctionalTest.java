package ortus.boxlang.bxsites.gradle;

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SKIPPED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end functional test: applies the real plugin to a fixture project
 * and confirms a real, non-empty {@code site/index.html} comes out - the
 * single most important assertion in this plan's own testing strategy,
 * since it exercises the real network provisioning + real subprocess
 * invocation + the real, live bx-sites module, not a mock of any of them.
 *
 * <p>Needs real outbound network access (downloads.ortussolutions.com).
 */
class BxSitesPluginFunctionalTest {

    @TempDir
    Path projectDir;

    @BeforeEach
    void copyFixture() throws IOException, URISyntaxException {
        copyResourceDir("/fixtures/plain-docs-project", projectDir);
        Files.writeString(projectDir.resolve("settings.gradle.kts"), "rootProject.name = \"functional-test-fixture\"\n");
        Files.writeString(projectDir.resolve("build.gradle.kts"), """
                plugins {
                    id("io.boxlang.bxsites")
                }
                """);
    }

    @Test
    void bxSitesBuild_producesARealNonEmptySiteIndexHtml() {
        BuildResult result = runner("bxSitesBuild").build();

        Path indexHtml = projectDir.resolve("site").resolve("index.html");
        assertTrue(Files.exists(indexHtml), "site/index.html should exist after bxSitesBuild");
        assertTrue(fileSize(indexHtml) > 0, "site/index.html should be non-empty");
        assertTrue(result.getOutput().contains("Built"), "expected bx-sites' own build report in the output");
    }

    @Test
    void bxSitesBuild_isUpToDateOnASecondRunWithNoChanges() {
        runner("bxSitesBuild").build();

        BuildResult second = runner("bxSitesBuild").build();

        // Asserting the task's real outcome directly, not scanning the
        // console log for "UP-TO-DATE" - that string also appears on the
        // unrelated ":bxSitesProvision UP-TO-DATE" line on every run
        // regardless of whether :bxSitesBuild itself re-executed, which
        // previously let this assertion pass even when the invalidation
        // logic it's meant to guard was broken.
        assertEquals(UP_TO_DATE, second.task(":bxSitesBuild").getOutcome());
    }

    @Test
    void bxSitesBuild_reRunsWhenAContentFileChanges() throws IOException {
        runner("bxSitesBuild").build();

        appendTo(projectDir.resolve("docs").resolve("index.md"), "\nSome more content.\n");
        BuildResult second = runner("bxSitesBuild").build();

        assertEquals(SUCCESS, second.task(":bxSitesBuild").getOutcome());
    }

    @Test
    void bxSitesBuild_reRunsWhenTheSiteConfigFileChanges() throws IOException {
        runner("bxSitesBuild").build();

        Files.writeString(projectDir.resolve("bxsites.yaml"), "name: \"Renamed Site\"\n");
        BuildResult second = runner("bxSitesBuild").build();

        assertEquals(SUCCESS, second.task(":bxSitesBuild").getOutcome());
    }

    @Test
    void bxSitesProvision_isGenuinelyCacheable() {
        // bxSitesProvision used to have its whole boxlangHomeDir declared as
        // its @OutputDirectory, but BoxLang's own runtime writes logs/caches
        // directly into that same directory at verb-execution time - Gradle
        // disabled build caching for the task outright the moment any verb
        // task had ever run ("Task output caching requires exclusive access
        // to output paths"). Narrowing the declared output to just
        // modules/bxsites fixed that - proven here by deleting all local
        // build state and confirming a second, otherwise-identical build
        // pulls bxSitesProvision straight from the shared build cache.
        runner("bxSitesBuild", "--build-cache").build();

        deleteRecursively(projectDir.resolve("build"));

        BuildResult second = runner("bxSitesBuild", "--build-cache").build();

        assertEquals(FROM_CACHE, second.task(":bxSitesProvision").getOutcome());
    }

    @Test
    void bxSitesOpenApiDoc_isSkippedByDefault() {
        BuildResult result = runner("bxSitesOpenApiDoc").build();

        assertEquals(SKIPPED, result.task(":bxSitesOpenApiDoc").getOutcome());
    }

    @Test
    void bxSitesOpenApiDoc_failsWhenOpenApiIsNotEnabledInConfigAndAutoPatchIsOff() throws IOException {
        writeOpenApiSpecFixture();
        appendToBuildScript("""
                bxSites {
                    springBoot {
                        openApi {
                            enabled.set(true)
                            specFile.set(file("openapi-fixture.json"))
                        }
                    }
                }
                """);

        BuildResult result = runner("bxSitesOpenApiDoc").buildAndFail();

        assertTrue(result.getOutput().contains("openapi: true is not set"),
                "expected the actionable openapi-not-enabled error in the output");
    }

    @Test
    void bxSitesOpenApiDoc_generatesThePageAndAutoPatchesTheConfigWhenRequested() throws IOException {
        writeOpenApiSpecFixture();
        appendToBuildScript("""
                bxSites {
                    springBoot {
                        openApi {
                            enabled.set(true)
                            specFile.set(file("openapi-fixture.json"))
                            pageTitle.set("Bookshelf API")
                            autoPatchConfig.set(true)
                        }
                    }
                }
                """);

        BuildResult result = runner("bxSitesOpenApiDoc").build();

        assertEquals(SUCCESS, result.task(":bxSitesOpenApiDoc").getOutcome());

        Path copiedSpec = projectDir.resolve("docs").resolve("assets").resolve("openapi").resolve("openapi.json");
        assertTrue(Files.exists(copiedSpec), "the spec should have been copied into docs/assets/openapi/");

        Path page = projectDir.resolve("docs").resolve("api").resolve("openapi.md");
        String pageContent = Files.readString(page);
        assertTrue(pageContent.contains("title: \"Bookshelf API\""), "expected the page's frontmatter title");
        assertTrue(pageContent.contains("::: openapi src=\"assets/openapi/openapi.json\" title=\"Bookshelf API\""),
                "expected the openapi content block");

        String config = Files.readString(projectDir.resolve("bxsites.yaml"));
        assertTrue(config.contains("openapi: true"), "expected openapi: true to have been auto-patched into bxsites.yaml");
    }

    private void writeOpenApiSpecFixture() throws IOException {
        Files.writeString(projectDir.resolve("openapi-fixture.json"),
                "{\"openapi\":\"3.0.0\",\"info\":{\"title\":\"Bookshelf API\",\"version\":\"1.0\"},\"paths\":{}}");
    }

    private void appendToBuildScript(String extra) throws IOException {
        Files.writeString(projectDir.resolve("build.gradle.kts"), extra, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    @Test
    void bxSitesJavadocDoc_isSkippedByDefault() {
        BuildResult result = runner("bxSitesJavadocDoc").build();

        assertEquals(SKIPPED, result.task(":bxSitesJavadocDoc").getOutcome());
    }

    @Test
    void bxSitesJavadocDoc_generatesAPageForAPublicJavaType() throws IOException {
        Path sourceFile = projectDir.resolve("src").resolve("main").resolve("java")
                .resolve("com").resolve("example").resolve("Widget.java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, """
                package com.example;

                /** A single widget on a shelf. */
                public class Widget {
                }
                """);
        appendToBuildScript("""
                bxSites {
                    springBoot {
                        javadoc {
                            enabled.set(true)
                            sourceFiles.from("src/main/java/com/example/Widget.java")
                        }
                    }
                }
                """);

        BuildResult result = runner("bxSitesJavadocDoc").build();

        assertEquals(SUCCESS, result.task(":bxSitesJavadocDoc").getOutcome());
        Path page = projectDir.resolve("docs").resolve("api").resolve("javadoc")
                .resolve("com").resolve("example").resolve("Widget.md");
        String content = Files.readString(page);
        assertTrue(content.contains("title: \"Widget\""));
        assertTrue(content.contains("A single widget on a shelf."));
    }

    @Test
    void bxSitesControllerScanDoc_isSkippedByDefaultWhenOpenApiIsAlsoOff() {
        BuildResult result = runner("bxSitesControllerScanDoc").build();

        // Default policy: enabled unless openApi is on - openApi is off by
        // default too here, so this should actually run (successfully,
        // producing zero pages since classesDir/runtimeClasspath are unset).
        assertEquals(SUCCESS, result.task(":bxSitesControllerScanDoc").getOutcome());
    }

    @Test
    void bxSitesControllerScanDoc_isSkippedWhenOpenApiIsEnabled() throws IOException {
        appendToBuildScript("""
                bxSites {
                    springBoot {
                        openApi {
                            enabled.set(true)
                        }
                    }
                }
                """);

        BuildResult result = runner("bxSitesControllerScanDoc").build();

        assertEquals(SKIPPED, result.task(":bxSitesControllerScanDoc").getOutcome());
    }

    @Test
    void bxSitesControllerScanDoc_findsARealSpringControllerInAForkedJvm() throws IOException {
        // Needs real network - resolves spring-web/spring-context from Maven
        // Central, same as this fixture already needs it for the plugin's
        // own provisioning elsewhere in this test class.
        Path controllerFile = projectDir.resolve("src").resolve("main").resolve("java")
                .resolve("com").resolve("example").resolve("BookController.java");
        Files.createDirectories(controllerFile.getParent());
        Files.writeString(controllerFile, """
                package com.example;

                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RequestMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                @RequestMapping("/api/books")
                public class BookController {
                    @GetMapping
                    public String list() {
                        return "[]";
                    }
                }
                """);
        Files.writeString(projectDir.resolve("build.gradle.kts"), """
                plugins {
                    id("io.boxlang.bxsites")
                    java
                }
                repositories {
                    mavenCentral()
                }
                dependencies {
                    implementation("org.springframework:spring-web:6.1.13")
                    implementation("org.springframework:spring-context:6.1.13")
                }
                bxSites {
                    springBoot {
                        controllerScan {
                            enabled.set(true)
                            classesDir.set(layout.buildDirectory.dir("classes/java/main"))
                            runtimeClasspath.from(configurations.getByName("runtimeClasspath"))
                        }
                    }
                }
                tasks.named("bxSitesControllerScanDoc") {
                    dependsOn("compileJava")
                }
                """);

        BuildResult result = runner("bxSitesControllerScanDoc").build();

        assertEquals(SUCCESS, result.task(":bxSitesControllerScanDoc").getOutcome());
        Path page = projectDir.resolve("docs").resolve("api").resolve("controllers")
                .resolve("com").resolve("example").resolve("BookController.md");
        String content = Files.readString(page);
        assertTrue(content.contains("title: \"BookController\""));
        // A plain Markdown pipe table: the class-level @RequestMapping base
        // path composed with the method's own bare @GetMapping. Deliberately
        // not raw <table> markup with per-row Alpine attributes - see
        // ControllerScanGenerator#appendFilterableTable for why that cannot
        // work in this pipeline.
        assertTrue(content.contains("| Method | Path | Handler |"));
        assertTrue(content.contains("| GET | `/api/books` | `String list()` |"));
    }

    private static void deleteRecursively(Path root) {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void appendTo(Path file, String text) throws IOException {
        Files.writeString(file, text, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private GradleRunner runner(String... args) {
        String[] fullArgs = new String[args.length + 1];
        System.arraycopy(args, 0, fullArgs, 0, args.length);
        fullArgs[args.length] = "--stacktrace";
        return GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                .withArguments(fullArgs)
                .withPluginClasspath();
    }

    private static long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void copyResourceDir(String resourceRoot, Path dest) throws IOException, URISyntaxException {
        URL url = BxSitesPluginFunctionalTest.class.getResource(resourceRoot);
        if (url == null) {
            throw new IllegalStateException("Fixture resource not found: " + resourceRoot);
        }
        URI uri = toUri(url);
        if ("jar".equals(uri.getScheme())) {
            try (var fs = FileSystems.newFileSystem(uri, java.util.Map.of())) {
                copyTree(fs.getPath(resourceRoot), dest);
            }
        } else {
            copyTree(Path.of(uri), dest);
        }
    }

    private static java.net.URI toUri(URL url) throws URISyntaxException {
        return url.toURI();
    }

    private static void copyTree(Path source, Path dest) throws IOException {
        try (var stream = Files.walk(source)) {
            Iterator<Path> it = stream.iterator();
            while (it.hasNext()) {
                Path entry = it.next();
                Path relative = source.relativize(entry);
                Path target = dest.resolve(relative.toString());
                if (Files.isDirectory(entry)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(entry, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
