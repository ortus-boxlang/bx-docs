package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code lint} verb - a stdout-report-only content linter
 * over the raw {@code docs/} Markdown source (confirmed against
 * {@code ContentLinter.bx}); it never writes any file, so there's no output
 * artifact to verify against. Wired into {@code check} by default - see
 * {@code BxSitesExtension#getHookIntoCheck()}.
 */
public abstract class BxSitesLintTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.LINT;
    }
}
