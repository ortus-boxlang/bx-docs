package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code stats} verb - a read-only report over an
 * already-built {@code site/} (page/word counts, etc.). Writes no file
 * (confirmed against {@code Stats.bx}).
 */
public abstract class BxSitesStatsTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.STATS;
    }
}
