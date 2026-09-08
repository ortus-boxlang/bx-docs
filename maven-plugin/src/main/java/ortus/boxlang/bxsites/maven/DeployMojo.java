package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** {@code mvn bxsites:deploy} - builds the site and deploys it to the configured target. */
@Mojo(name = "deploy", threadSafe = true)
public class DeployMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DEPLOY;
    }
}
