package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * {@code mvn bxsites:stats} - a read-only report over an already-built
 * {@code site/} (page/word counts, etc). Writes no file (confirmed against
 * {@code Stats.bx}).
 */
@Mojo(name = "stats", threadSafe = true)
public class StatsMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.STATS;
    }
}
