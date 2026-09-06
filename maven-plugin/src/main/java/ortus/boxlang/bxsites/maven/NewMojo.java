package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** {@code mvn bxsites:new} - scaffolds a new bx-sites project (content dir + config file). */
@Mojo(name = "new")
public class NewMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.NEW;
    }
}
