package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * {@code mvn bxsites:serve} - builds and serves the site locally with live
 * reload. Foreground: {@code Serve.bx} runs its own internal build-and-watch
 * loop, so this goal never completes on its own.
 *
 * <p><b>Known gap, not yet solved</b> - same as the Gradle plugin's
 * {@code BxSitesServeTask}: {@code AbstractBxSitesMojo}'s current invocation
 * path buffers all output before returning and enforces a 30-minute
 * timeout, both wrong for a verb meant to run indefinitely with
 * live-streamed output. Live streaming and signal-forwarding are real,
 * not-yet-implemented follow-up work.
 */
@Mojo(name = "serve")
public class ServeMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.SERVE;
    }
}
