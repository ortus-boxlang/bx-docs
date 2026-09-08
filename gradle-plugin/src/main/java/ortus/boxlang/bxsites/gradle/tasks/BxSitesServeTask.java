package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code serve} verb - builds and serves the site locally
 * with live reload. Foreground/non-cacheable: {@code Serve.bx} runs its own
 * internal build-and-watch loop, so this task never completes on its own
 * (stop it the same way you'd stop any other long-running Gradle task, e.g.
 * Ctrl+C). Deliberately does not {@code dependsOn} {@code bxSitesBuild} for
 * the same reason.
 *
 * <p><b>Known gap, not yet solved:</b> {@link AbstractBxSitesVerbTask}'s
 * current invocation path buffers all output before returning and enforces
 * a 30-minute timeout - both wrong for a verb that's meant to run
 * indefinitely with live-streamed output. Live output streaming and
 * signal-forwarding (so Ctrl+C on the Gradle process actually stops the
 * child) are real, not-yet-implemented follow-up work.
 */
public abstract class BxSitesServeTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.SERVE;
    }
}
