package ortus.boxlang.bxsites.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link BxSitesInvoker}'s command-construction and
 * success-evaluation logic - the two pure, package-private static methods
 * split out specifically so this bug-prone logic (see the class doc on why
 * the exit code alone can't be trusted) is testable without launching a
 * real subprocess. Real end-to-end invocation is covered by both plugins'
 * functional/integration tests against the live network instead.
 */
class BxSitesInvokerTest {

    @Test
    void buildCommand_invokesBoxRunnerViaClasspathWithModulePrefixedVerb(@TempDir Path tmp) {
        Path jar = tmp.resolve("boxlang-miniserver-1.18.0-snapshot.jar");
        Path projectRoot = tmp.resolve("my-project");

        List<String> command = BxSitesInvoker.buildCommand(jar, BxSitesVerb.BUILD, projectRoot, List.of());

        assertEquals("-cp", command.get(1));
        assertEquals(jar.toAbsolutePath().toString(), command.get(2));
        assertEquals(BxSitesInvoker.BOX_RUNNER_CLASS, command.get(3));
        assertEquals("module:bxSites", command.get(4));
        assertEquals("build", command.get(5));
        assertEquals("--projectRoot=" + projectRoot.toAbsolutePath(), command.get(6));
    }

    @Test
    void buildCommand_appendsExtraArgsVerbatimAfterProjectRoot(@TempDir Path tmp) {
        Path jar = tmp.resolve("miniserver.jar");
        Path projectRoot = tmp.resolve("proj");

        List<String> command = BxSitesInvoker.buildCommand(jar, BxSitesVerb.SERVE, projectRoot, List.of("--port=9090"));

        assertEquals("--port=9090", command.get(command.size() - 1));
    }

    @Test
    void evaluateSuccess_trueOnCleanExitWithNoErrorAndPresentOutput(@TempDir Path tmp) throws IOException {
        Path output = tmp.resolve("site").resolve("index.html");
        Files.createDirectories(output.getParent());
        Files.writeString(output, "<html></html>");

        assertTrue(BxSitesInvoker.evaluateSuccess(0, "Built 1 page(s)\n", output));
    }

    @Test
    void evaluateSuccess_falseWhenExitCodeIsZeroButOutputContainsAnErrorLine(@TempDir Path tmp) throws IOException {
        // Reproduces the exact real-world case confirmed during the M0 spike:
        // a genuine config error still exits 0.
        Path output = tmp.resolve("site").resolve("index.html");
        Files.createDirectories(output.getParent());
        Files.writeString(output, "<html></html>");

        boolean success = BxSitesInvoker.evaluateSuccess(
                0, "Error: No bxsites.yaml, bxsites.toml, or bxsites.json found\n", output);

        assertFalse(success, "an Error: line must be treated as failure even when the exit code is 0");
    }

    @Test
    void evaluateSuccess_falseWhenExpectedOutputFileIsMissing(@TempDir Path tmp) {
        Path neverWritten = tmp.resolve("site").resolve("index.html");

        assertFalse(BxSitesInvoker.evaluateSuccess(0, "Built 1 page(s)\n", neverWritten));
    }

    @Test
    void evaluateSuccess_falseWhenExpectedOutputFileIsEmpty(@TempDir Path tmp) throws IOException {
        Path empty = tmp.resolve("site").resolve("index.html");
        Files.createDirectories(empty.getParent());
        Files.createFile(empty);

        assertFalse(BxSitesInvoker.evaluateSuccess(0, "Built 1 page(s)\n", empty));
    }

    @Test
    void evaluateSuccess_falseOnNonZeroExitCodeEvenWithNoErrorLine() {
        assertFalse(BxSitesInvoker.evaluateSuccess(1, "some output with no Error: line\n", null));
    }

    @Test
    void evaluateSuccess_ignoresOutputArtifactCheckWhenNoneIsExpected() {
        // Verbs like `serve`/`new` have no single defined output file to check.
        assertTrue(BxSitesInvoker.evaluateSuccess(0, "Created new BX Sites project\n", null));
    }

    @Test
    void ensureProjectRootExists_createsTheDirectoryForTheNewVerb(@TempDir Path tmp) {
        Path freshSubdir = tmp.resolve("does-not-exist-yet").resolve("nested");

        BxSitesInvoker.ensureProjectRootExistsForScaffoldingVerb(BxSitesVerb.NEW, freshSubdir);

        assertTrue(Files.isDirectory(freshSubdir), "new should scaffold into a project root that doesn't exist yet");
    }

    @Test
    void ensureProjectRootExists_leavesOtherVerbsAlone(@TempDir Path tmp) {
        Path missing = tmp.resolve("does-not-exist");

        BxSitesInvoker.ensureProjectRootExistsForScaffoldingVerb(BxSitesVerb.BUILD, missing);

        assertFalse(Files.exists(missing), "only `new` should create a missing project root - build/serve/etc. expect one to already exist");
    }

    // javaExecutable() prefers java.home's own bin/ launcher over a bare
    // "java" PATH lookup - these fake a JDK layout under java.home for each
    // OS shape rather than assuming the JDK actually running this test
    // matches either one, so the Windows case is exercised on this (Linux)
    // CI runner too, not just wherever the suite happens to run.

    @Test
    void javaExecutable_prefersJavaHomesOwnLauncherOnUnix(@TempDir Path fakeJavaHome) throws IOException {
        Path bin = Files.createDirectories(fakeJavaHome.resolve("bin"));
        Path javaBinary = Files.createFile(bin.resolve("java"));
        javaBinary.toFile().setExecutable(true);

        withJavaHome(fakeJavaHome, () -> assertEquals(javaBinary.toString(), BxSitesInvoker.javaExecutable()));
    }

    @Test
    void javaExecutable_prefersJavaHomesOwnLauncherOnWindows_javaExeNotBareJava(@TempDir Path fakeJavaHome) throws IOException {
        // Reproduces the actual bug: a Windows JDK/JRE never ships a bare
        // "java" file, only "java.exe" - Path.of(javaHome, "bin", "java")
        // alone (this method's old, whole implementation) would find
        // nothing here and silently fall through to the bare "java"
        // PATH-lookup fallback on every real Windows machine.
        Path bin = Files.createDirectories(fakeJavaHome.resolve("bin"));
        Path javaExe = Files.createFile(bin.resolve("java.exe"));
        javaExe.toFile().setExecutable(true);

        withJavaHome(fakeJavaHome, () -> assertEquals(javaExe.toString(), BxSitesInvoker.javaExecutable()));
    }

    @Test
    void javaExecutable_fallsBackToBareJavaWhenJavaHomeHasNeitherLauncher(@TempDir Path fakeJavaHome) {
        withJavaHome(fakeJavaHome, () -> assertEquals("java", BxSitesInvoker.javaExecutable()));
    }

    private static void withJavaHome(Path fakeJavaHome, Runnable assertion) {
        String original = System.getProperty("java.home");
        System.setProperty("java.home", fakeJavaHome.toString());
        try {
            assertion.run();
        } finally {
            System.setProperty("java.home", original);
        }
    }
}
