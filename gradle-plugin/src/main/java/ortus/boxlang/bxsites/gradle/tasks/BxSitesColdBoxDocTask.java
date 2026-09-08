package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code coldbox} verb - documents a ColdBox application's
 * routes, handlers, models, modules, interceptors and scheduled tasks from
 * its conventions on disk. The ColdBox counterpart of
 * {@code bxSitesControllerScanDoc}, with the difference that it reads
 * source rather than reflecting over compiled classes, so it needs neither
 * a compile step nor a bootable application.
 *
 * <p>Not wired into any lifecycle by default.
 */
public abstract class BxSitesColdBoxDocTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.COLDBOX;
    }
}
