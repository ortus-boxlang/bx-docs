package ortus.boxlang.bxsites.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Runs one bx-sites CLI verb as a subprocess, identically whether called
 * from a Gradle task or a Maven Mojo.
 *
 * <p><b>Confirmed exact invocation (M0 spike, against live artifacts):</b>
 * {@code java -cp <miniserver-jar> ortus.boxlang.runtime.BoxRunner
 * module:bxSites <verb> --projectRoot=<path> [flags...]}, with
 * {@code BOXLANG_HOME} set as an environment variable. The miniserver jar's
 * own default {@code java -jar} entrypoint launches an embedded web server
 * instead (a different {@code Main-Class} in the same jar) - the CLI runner
 * class must be selected explicitly via {@code -cp}.
 *
 * <p><b>The subprocess exit code is not reliable on failure</b> - confirmed
 * directly: a real configuration error still exits {@code 0}. Every
 * invocation is therefore verified two other ways: scanning captured output
 * for a line starting with {@code Error:} (the CLI dispatcher's own,
 * consistent error-message prefix), and - for a verb with a defined output
 * artifact - confirming that artifact actually exists and is non-empty.
 * Either signal means failure, regardless of the reported exit code.
 *
 * <p>The command-construction and success-evaluation logic below is
 * deliberately split into package-private static methods so it can be unit
 * tested (see {@code BxSitesInvokerTest}) without actually launching a
 * subprocess - only {@link #invoke} itself needs a real process, and that
 * part is exercised by both plugins' functional/integration test tiers
 * against the real network instead.
 */
public final class BxSitesInvoker {

    static final String BOX_RUNNER_CLASS = "ortus.boxlang.runtime.BoxRunner";

    private final Path miniserverJar;
    private final Path boxlangHomeDir;

    public BxSitesInvoker(Path miniserverJar, Path boxlangHomeDir) {
        this.miniserverJar = miniserverJar;
        this.boxlangHomeDir = boxlangHomeDir;
    }

    /**
     * Runs {@code verb} against {@code projectRoot}.
     *
     * @param verb          the verb to invoke
     * @param projectRoot   the target bx-sites project directory
     * @param extraArgs     any additional CLI flags, verbatim (e.g.
     *                      {@code "--port=9090"})
     * @param expectedOutput a file whose existence and non-emptiness proves
     *                      success (e.g. {@code site/index.html} after
     *                      {@code build}), or {@code null} if this verb has
     *                      no single defined output to check
     */
    public InvocationResult invoke(BxSitesVerb verb, Path projectRoot, List<String> extraArgs, Path expectedOutput) {
        List<String> command = buildCommand(miniserverJar, verb, projectRoot, extraArgs);

        ProcessBuilder pb = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .directory(projectRoot.toFile());
        pb.environment().put("BOXLANG_HOME", boxlangHomeDir.toAbsolutePath().toString());

        String output;
        int exitCode;
        try {
            Process process = pb.start();
            // Reading process.getInputStream() blocks until EOF, which only
            // happens when the process exits - calling it before waitFor()
            // (as this used to) meant the 30-minute timeout below could
            // never actually fire: a hung process would block here forever,
            // never reaching the bounded wait at all (confirmed by measuring
            // readAllBytes() block until process exit, independent of any
            // timeout argument). Draining on a separate thread lets the two
            // waits run concurrently, so the timeout genuinely bounds total
            // wall time.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Thread drain = new Thread(() -> {
                try {
                    process.getInputStream().transferTo(buffer);
                } catch (IOException ignored) {
                    // Stream closed out from under us (e.g. destroyForcibly()
                    // below) - whatever was captured before that is fine.
                }
            }, "bxsites-output-drain");
            drain.setDaemon(true);
            drain.start();

            if (!process.waitFor(30, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                drain.join(TimeUnit.SECONDS.toMillis(30));
                throw new IllegalStateException("bxSites " + verb.verbId() + " timed out after 30 minutes");
            }
            exitCode = process.exitValue();
            // The process has exited, so its stdout will reach EOF shortly -
            // give the drain thread a bounded window to finish copying it.
            drain.join(TimeUnit.SECONDS.toMillis(30));
            output = buffer.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to launch bxSites " + verb.verbId() + " subprocess", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running bxSites " + verb.verbId(), e);
        }

        boolean success = evaluateSuccess(exitCode, output, expectedOutput);
        return new InvocationResult(success, exitCode, output);
    }

    /**
     * Builds the exact argv for invoking {@code verb} against
     * {@code projectRoot} - a pure function of its inputs, split out purely
     * so it's unit-testable without launching a process.
     */
    static List<String> buildCommand(Path miniserverJar, BxSitesVerb verb, Path projectRoot, List<String> extraArgs) {
        List<String> command = new ArrayList<>();
        command.add(javaExecutable());
        command.add("-cp");
        command.add(miniserverJar.toAbsolutePath().toString());
        command.add(BOX_RUNNER_CLASS);
        command.add("module:bxSites");
        command.add(verb.verbId());
        command.add("--projectRoot=" + projectRoot.toAbsolutePath());
        command.addAll(extraArgs);
        return command;
    }

    /**
     * Decides whether one invocation actually succeeded - never trusting
     * the subprocess exit code alone (confirmed unreliable, see class
     * doc). Split out purely so this bug-prone logic is unit-testable
     * without launching a process.
     */
    static boolean evaluateSuccess(int exitCode, String output, Path expectedOutput) {
        boolean errorLinePresent = output.lines().anyMatch(line -> line.startsWith("Error:"));
        boolean outputArtifactMissing = expectedOutput != null && !isNonEmptyFile(expectedOutput);
        return exitCode == 0 && !errorLinePresent && !outputArtifactMissing;
    }

    private static boolean isNonEmptyFile(Path path) {
        try {
            return Files.isRegularFile(path) && Files.size(path) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static String javaExecutable() {
        String javaHome = System.getProperty("java.home");
        Path candidate = Path.of(javaHome, "bin", "java");
        return Files.isExecutable(candidate) ? candidate.toString() : "java";
    }

    /** The outcome of one verb invocation. */
    public record InvocationResult(boolean success, int exitCode, String output) {
    }
}
