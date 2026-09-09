package ortus.boxlang.bxsites.gradle.boxlang;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * The {@code bxSites { boxlang { } } } nested block - the sibling of
 * {@code springBoot { }}, for a JVM project whose sources include BoxLang
 * or CFML classes.
 *
 * <p>Deliberately narrow. A ColdBox application is built and run through
 * CommandBox, never Gradle or Maven, so the {@code coldbox} verb has no
 * business in a Java build tool and is reachable only from the bx-sites
 * CLI. What belongs here is what a JVM build can genuinely own: source
 * that sits in the project being built.
 *
 * <p>The generator here is a verb wrapper rather than a Java
 * reimplementation: the BoxLang side already owns the logic, and one
 * implementation that both build tools drive can't drift the way two
 * would.
 */
public abstract class BxSitesBoxLangExtension {

    private final BxSitesDocBoxExtension docbox;

    @Inject
    public BxSitesBoxLangExtension(Project project) {
        docbox = project.getObjects().newInstance(BxSitesDocBoxExtension.class, project);
    }

    /** The {@code docbox { }} block - a BoxLang/CFML API reference from DocBox. */
    public BxSitesDocBoxExtension getDocbox() {
        return docbox;
    }

    public void docbox(Action<? super BxSitesDocBoxExtension> action) {
        action.execute(docbox);
    }

}
