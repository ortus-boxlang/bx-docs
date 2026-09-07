package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** {@code mvn bxsites:publish} - builds the site and publishes it to bxSites Cloud. */
@Mojo(name = "publish", threadSafe = true)
public class PublishMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.PUBLISH;
    }
}
