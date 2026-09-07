package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * {@code mvn bxsites:doctor} - a purely diagnostic health check; nothing
 * here mutates a project, and it writes no file (confirmed against
 * {@code Doctor.bx}).
 */
@Mojo(name = "doctor", threadSafe = true)
public class DoctorMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DOCTOR;
    }
}
