package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code new} verb - a one-time scaffold action, not wired
 * into any lifecycle task (run explicitly, like Gradle's own {@code init}).
 */
public abstract class BxSitesNewTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.NEW;
    }
}
