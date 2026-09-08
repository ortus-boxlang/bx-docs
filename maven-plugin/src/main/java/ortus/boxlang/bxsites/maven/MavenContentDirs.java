package ortus.boxlang.bxsites.maven;

import java.nio.file.Files;
import java.nio.file.Path;

import ortus.boxlang.bxsites.core.ContentDirResolver;

/**
 * {@link ContentDirResolver#resolve} falls back to {@code src/} when
 * {@code docs/} doesn't exist yet - correct for a standalone docs project,
 * but wrong in a typical Maven Java project (this plugin's headline use
 * case): {@code src/main/java} is Java source, not bx-sites content.
 * Mirrors the same guard the Gradle plugin applies via
 * {@code JavaBasePlugin} - Maven has no plugin-applied signal to check, so
 * a {@code src/main/java} directory is used as the equivalent "this is a
 * Java project" marker instead. Shared by every Mojo that needs to know
 * the content dir without invoking bx-sites itself (which does its own,
 * unguarded resolution internally).
 */
final class MavenContentDirs {

    private MavenContentDirs() {
    }

    static Path resolve(Path projectRoot) {
        Path docs = projectRoot.resolve("docs");
        boolean skipSrcFallback = Files.isDirectory(docs) || Files.isDirectory(projectRoot.resolve("src/main/java"));
        return skipSrcFallback ? docs : ContentDirResolver.resolve(projectRoot);
    }
}
