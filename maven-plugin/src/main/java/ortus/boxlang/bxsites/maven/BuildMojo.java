package ortus.boxlang.bxsites.maven;

import java.io.File;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;
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
 * <p>No Gradle-style built-in incremental-build engine exists in Maven;
 * this Mojo does not (yet) implement the manual staleness check the plan
 * describes (comparing content-dir/config mtimes against the site's own
 * output) - every invocation currently re-runs the full build. Flagged as a
 * fast-follow, not silently treated as done.
 */
@Mojo(name = "build", threadSafe = true)
public class BuildMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.BUILD;
    }

    @Override
    protected File expectedOutputFile() {
        return SiteDirResolver.resolve(projectRoot.toPath()).resolve("index.html").toFile();
    }
}
