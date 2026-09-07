package ortus.boxlang.bxsites.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

/**
 * A lightweight manual staleness check for build tools with no built-in
 * incremental-build engine of their own (Maven) - compares the newest
 * last-modified timestamp under the content directory (plus the config
 * file, if one exists) against the newest timestamp anywhere already
 * inside the site output directory. Gradle doesn't need this (its
 * Provider-API {@code @InputDirectory}/{@code @InputFiles}/
 * {@code @OutputDirectory} annotations already give it a real
 * incremental-build engine); this exists for Maven's {@code BuildMojo},
 * which has no equivalent of its own.
 */
public final class BuildStalenessChecker {

    private BuildStalenessChecker() {
    }

    /**
     * @return true if {@code siteDir} already reflects the current state of
     *         {@code contentDir}/{@code configFile} and a rebuild can be
     *         skipped; false if either side is missing/empty (nothing to
     *         compare, so always rebuild) or an input is newer than every
     *         file already in the site output.
     */
    public static boolean isUpToDate(Path contentDir, Path configFile, Path siteDir) {
        Optional<Instant> newestInput = newestMTimeUnder(contentDir);
        if (Files.isRegularFile(configFile)) {
            Instant configMTime = mtimeOf(configFile);
            newestInput = Optional.of(newestInput.filter(t -> t.isAfter(configMTime)).orElse(configMTime));
        }
        if (newestInput.isEmpty()) {
            return false;
        }

        Optional<Instant> newestOutput = newestMTimeUnder(siteDir);
        if (newestOutput.isEmpty()) {
            return false;
        }

        return !newestInput.get().isAfter(newestOutput.get());
    }

    private static Optional<Instant> newestMTimeUnder(Path root) {
        if (!Files.isDirectory(root)) {
            return Optional.empty();
        }
        try (var walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .map(BuildStalenessChecker::mtimeOf)
                    .max(Instant::compareTo);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Instant mtimeOf(Path file) {
        try {
            return Files.getLastModifiedTime(file).toInstant();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
