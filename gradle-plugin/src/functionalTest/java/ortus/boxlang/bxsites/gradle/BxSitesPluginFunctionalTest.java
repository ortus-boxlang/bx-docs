package ortus.boxlang.bxsites.gradle;

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
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
