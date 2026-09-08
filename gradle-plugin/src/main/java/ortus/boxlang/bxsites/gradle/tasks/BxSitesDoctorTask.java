package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code doctor} verb - a purely diagnostic health check;
 * nothing here mutates a project, and it writes no file (confirmed against
 * {@code Doctor.bx}).
 */
public abstract class BxSitesDoctorTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DOCTOR;
    }
}
