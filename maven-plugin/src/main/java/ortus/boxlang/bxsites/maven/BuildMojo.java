package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.BuildStalenessChecker;
import ortus.boxlang.bxsites.core.BxSitesVerb;
import ortus.boxlang.bxsites.core.ConfigFileResolver;
import ortus.boxlang.bxsites.core.ContentDirResolver;
import ortus.boxlang.bxsites.core.SiteDirResolver;

/**
 * {@code mvn bxsites:build} - renders the bx-sites documentation site.
 *
 * <p>The output directory is <em>not</em> an independently configurable
 * parameter - bx-sites itself hardcodes it to {@code <projectRoot>/site}
 * (confirmed in {@code BuildPipeline.bx}, no CLI flag or config key
 * overrides it), so this Mojo derives it from {@link #projectRoot} via
 * {@link SiteDirResolver} rather than exposing a settable property that
 * bx-sites would silently ignore.
 *
 * <p>No Gradle-style built-in incremental-build engine exists in Maven, so
 * {@link #isUpToDate()} implements the lightweight manual staleness check
 * the plan describes: the newest last-modified time under the content dir
 * (+ the config file, if present) vs. the newest last-modified time already
 * in {@code siteDir}. Set {@code -Dbxsites.build.forceRebuild=true} to
 * bypass it.
 */
@Mojo(name = "build", threadSafe = true)
public class BuildMojo extends AbstractBxSitesMojo {

    @Parameter(property = "bxsites.build.forceRebuild", defaultValue = "false")
    private boolean forceRebuild;

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.BUILD;
    }

    @Override
    protected File expectedOutputFile() {
        return SiteDirResolver.resolve(projectRoot.toPath()).resolve("index.html").toFile();
    }

    @Override
    protected boolean isUpToDate() {
        if (forceRebuild) {
            return false;
        }
        Path root = projectRoot.toPath();
        return BuildStalenessChecker.isUpToDate(
                resolveContentDir(root),
                ConfigFileResolver.resolve(root),
                SiteDirResolver.resolve(root));
    }

    /**
     * {@link ContentDirResolver#resolve} falls back to {@code src/} when
     * {@code docs/} doesn't exist yet - correct for a standalone docs
     * project, but wrong here in a typical Maven Java project (this
     * plugin's headline use case): {@code src/main/java} is Java source,
     * not bx-sites content. Mirrors the same guard the Gradle plugin
     * applies via {@code JavaBasePlugin} - Maven has no plugin-applied
     * signal to check, so a {@code src/main/java} directory is used as the
     * equivalent "this is a Java project" marker instead.
     */
    private static Path resolveContentDir(Path projectRoot) {
        Path docs = projectRoot.resolve("docs");
        boolean skipSrcFallback = Files.isDirectory(docs) || Files.isDirectory(projectRoot.resolve("src/main/java"));
        return skipSrcFallback ? docs : ContentDirResolver.resolve(projectRoot);
    }
}
