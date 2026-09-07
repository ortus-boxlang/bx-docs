package ortus.boxlang.bxsites.gradle;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

import ortus.boxlang.bxsites.core.BxSitesConfig;

/**
 * The {@code bxSites { }} DSL block. Deliberately thin - it covers only
 * plugin/task wiring (versions, directories), never bx-sites' own
 * {@code bxsites.yaml}/{@code .toml}/{@code .json} site-config schema, which
 * a project keeps hand-authoring exactly like any other bx-sites project.
 */
public abstract class BxSitesExtension {

    @Inject
    public BxSitesExtension(Project project) {
        getProjectRoot().convention(project.getLayout().getProjectDirectory());
        getBoxlangMiniserverVersion().convention(BxSitesConfig.DEFAULT_MINISERVER_VERSION);
        getBxSitesVersion().convention(BxSitesConfig.DEFAULT_BXSITES_VERSION);
        getBoxlangHomeDir().convention(project.getLayout().getBuildDirectory().dir("bxsites/boxlang-home"));
        getHookIntoAssemble().convention(false);
        getHookIntoCheck().convention(true);
    }

    /** The bx-sites project root; defaults to this Gradle project's own directory. */
    public abstract DirectoryProperty getProjectRoot();

    /** Pinned boxlang-miniserver version - a snapshot build, for now (see the plan). */
    public abstract Property<String> getBoxlangMiniserverVersion();

    /** Pinned bx-sites version - resolves the {@code -with-deps} bundle. */
    public abstract Property<String> getBxSitesVersion();

    /** Per-project BOXLANG_HOME; defaults under this project's own build directory. */
    public abstract DirectoryProperty getBoxlangHomeDir();

    /** Whether {@code bxSitesBuild} should run as part of {@code assemble}. Off by default. */
    public abstract Property<Boolean> getHookIntoAssemble();

    /** Whether lint/check-style verbs should run as part of {@code check}. On by default. */
    public abstract Property<Boolean> getHookIntoCheck();
}
