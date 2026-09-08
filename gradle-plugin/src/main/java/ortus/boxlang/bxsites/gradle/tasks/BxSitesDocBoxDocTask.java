package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code docbox} verb - generates a BoxLang/CFML API
 * reference into the project's content directory from DocBox's own JSON
 * output. The BoxLang counterpart of {@code bxSitesJavadocDoc}, and unlike
 * it a verb wrapper rather than an in-JVM generator, since the
 * implementation lives on the BoxLang side.
 *
 * <p>Not wired into any lifecycle by default - chain it into
 * {@code bxSitesBuild} yourself, e.g.
 * {@code tasks.named("bxSitesBuild") { dependsOn("bxSitesDocBoxDoc") }}.
 */
public abstract class BxSitesDocBoxDocTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DOCBOX;
    }
}
