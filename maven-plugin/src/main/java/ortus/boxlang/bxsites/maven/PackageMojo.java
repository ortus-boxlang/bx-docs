package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** {@code mvn bxsites:package} - builds the site and zips it to site.zip. */
@Mojo(name = "package", threadSafe = true)
public class PackageMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.PACKAGE;
    }
}
