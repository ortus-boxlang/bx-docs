package ortus.boxlang.bxsites.gradle;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { }} DSL block. Deliberately thin - it covers only
 * plugin/task wiring (versions, directories), never bx-sites' own
 * {@code bxsites.yaml}/{@code .toml}/{@code .json} site-config schema, which
 * a project keeps hand-authoring exactly like any other bx-sites project.
 */
public abstract class BxSitesExtension {

    /** Pinned default - see the plan's "Provisioning subsystem" for why this must stay a snapshot for now. */
    public static final String DEFAULT_MINISERVER_VERSION = "1.18.0-snapshot";
    public static final String DEFAULT_BXSITES_VERSION = "1.0.0-snapshot";

    @Inject
    public BxSitesExtension(Project project) {
        getProjectRoot().convention(project.getLayout().getProjectDirectory());
        getSiteDir().convention(getProjectRoot().dir("site"));
        getBoxlangMiniserverVersion().convention(DEFAULT_MINISERVER_VERSION);
        getBxSitesVersion().convention(DEFAULT_BXSITES_VERSION);
        getBoxlangHomeDir().convention(project.getLayout().getBuildDirectory().dir("bxsites/boxlang-home"));
        getHookIntoAssemble().convention(false);
        getHookIntoCheck().convention(true);
    }

    /** The bx-sites project root; defaults to this Gradle project's own directory. */
    public abstract DirectoryProperty getProjectRoot();

    /** Where the built site lands; defaults to {@code <projectRoot>/site}. */
    public abstract DirectoryProperty getSiteDir();

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
