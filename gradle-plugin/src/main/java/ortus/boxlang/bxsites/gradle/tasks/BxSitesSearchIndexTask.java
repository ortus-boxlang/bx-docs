package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code search-index} verb - rebuilds
 * {@code <projectRoot>/site/search-index.json} without a full site build.
 * No output-file check: writing that file is a legitimate no-op when search
 * is disabled in the project's own {@code bxsites.yaml}/{@code .toml}/
 * {@code .json} (confirmed against {@code BuildPipeline.bx}), so verifying
 * against it here would misreport that valid case as a failure - success is
 * judged purely by the absence of an {@code Error:} line, same as bx-sites'
 * own CLI dispatcher.
 */
public abstract class BxSitesSearchIndexTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.SEARCH_INDEX;
    }
}
