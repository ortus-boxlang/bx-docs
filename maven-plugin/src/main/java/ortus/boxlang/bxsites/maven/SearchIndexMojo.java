package ortus.boxlang.bxsites.maven;

import org.apache.maven.plugins.annotations.Mojo;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/** {@code mvn bxsites:search-index} - rebuilds {@code site/search-index.json} without a full site build. */
@Mojo(name = "search-index", threadSafe = true)
public class SearchIndexMojo extends AbstractBxSitesMojo {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.SEARCH_INDEX;
    }
}
