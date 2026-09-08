package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code package} verb - builds the site, then zips it to
 * {@code <projectRoot>/site.zip} by default (confirmed against
 * {@code Package.bx}). No output-file check here: the destination is
 * overridable via {@code --output=<path>} through {@link #getExtraArgs()},
 * and hardcoding the default path would misreport success as failure
 * whenever a project overrides it.
 */
public abstract class BxSitesPackageTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.PACKAGE;
    }
}
