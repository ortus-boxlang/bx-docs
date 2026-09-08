package ortus.boxlang.bxsites.gradle;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

import ortus.boxlang.bxsites.core.BxSitesConfig;
import ortus.boxlang.bxsites.gradle.boxlang.BxSitesBoxLangExtension;
import ortus.boxlang.bxsites.gradle.springboot.BxSitesSpringBootExtension;

/**
 * The {@code bxSites { }} DSL block. Deliberately thin - it covers only
 * plugin/task wiring (versions, directories), never bx-sites' own
 * {@code bxsites.yaml}/{@code .toml}/{@code .json} site-config schema, which
 * a project keeps hand-authoring exactly like any other bx-sites project.
 */
public abstract class BxSitesExtension {

    private final BxSitesSpringBootExtension springBoot;
    private final BxSitesBoxLangExtension boxlang;

    @Inject
    public BxSitesExtension(Project project) {
        getProjectRoot().convention(project.getLayout().getProjectDirectory());
        getBoxlangMiniserverVersion().convention(BxSitesConfig.DEFAULT_MINISERVER_VERSION);
        getBxSitesVersion().convention(BxSitesConfig.DEFAULT_BXSITES_VERSION);
        getBoxlangHomeDir().convention(project.getLayout().getBuildDirectory().dir("bxsites/boxlang-home"));
        getHookIntoAssemble().convention(false);
        getHookIntoCheck().convention(true);
        springBoot = project.getObjects().newInstance(BxSitesSpringBootExtension.class, project);
        boxlang = project.getObjects().newInstance(BxSitesBoxLangExtension.class, project);
    }

    /** The {@code springBoot { }} nested block - Spring Boot doc generators (OpenAPI, Javadoc, controller-scan). */
    public BxSitesSpringBootExtension getSpringBoot() {
        return springBoot;
    }

    public void springBoot(Action<? super BxSitesSpringBootExtension> action) {
        action.execute(springBoot);
    }

    /** The {@code boxlang { }} nested block - the DocBox API reference generator. */
    public BxSitesBoxLangExtension getBoxlang() {
        return boxlang;
    }

    public void boxlang(Action<? super BxSitesBoxLangExtension> action) {
        action.execute(boxlang);
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
