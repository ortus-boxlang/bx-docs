package ortus.boxlang.bxsites.gradle.boxlang;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * The {@code bxSites { boxlang { } } } nested block - doc generators for
 * BoxLang and ColdBox projects, the sibling of {@code springBoot { }} for
 * Java ones.
 *
 * <p>Both generators here are verb wrappers rather than Java
 * reimplementations: the BoxLang side already owns the logic, and one
 * implementation that both build tools drive can't drift the way two
 * would.
 */
public abstract class BxSitesBoxLangExtension {

    private final BxSitesDocBoxExtension docbox;
    private final BxSitesColdBoxExtension coldbox;

    @Inject
    public BxSitesBoxLangExtension(Project project) {
        docbox = project.getObjects().newInstance(BxSitesDocBoxExtension.class, project);
        coldbox = project.getObjects().newInstance(BxSitesColdBoxExtension.class, project);
    }

    /** The {@code docbox { }} block - a BoxLang/CFML API reference from DocBox. */
    public BxSitesDocBoxExtension getDocbox() {
        return docbox;
    }

    public void docbox(Action<? super BxSitesDocBoxExtension> action) {
        action.execute(docbox);
    }

    /** The {@code coldbox { }} block - a ColdBox application's routes, handlers, models and modules. */
    public BxSitesColdBoxExtension getColdbox() {
        return coldbox;
    }

    public void coldbox(Action<? super BxSitesColdBoxExtension> action) {
        action.execute(coldbox);
    }
}
