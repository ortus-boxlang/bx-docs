package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code publish} verb - builds the site, then uploads it to
 * bxSites Cloud over HTTP (confirmed against {@code Publish.bx}/
 * {@code CloudPublisher.bx}). Writes no local manifest/log file, so there's
 * no output artifact to check here.
 */
public abstract class BxSitesPublishTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.PUBLISH;
    }
}
