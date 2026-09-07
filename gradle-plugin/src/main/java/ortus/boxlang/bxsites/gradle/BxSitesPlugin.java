package ortus.boxlang.bxsites.gradle;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.Delete;

import ortus.boxlang.bxsites.core.ConfigFileResolver;
import ortus.boxlang.bxsites.core.ContentDirResolver;
import ortus.boxlang.bxsites.core.SiteDirResolver;
import ortus.boxlang.bxsites.gradle.tasks.AbstractBxSitesVerbTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesBuildTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesNewTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesProvisionTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesServeTask;

public class BxSitesPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        // Guarantees assemble/check/clean lifecycle tasks exist regardless of
        // whether the consuming project applies `java`/`application`/etc -
        // this plugin isn't Java-specific, a docs site can sit on any project.
        project.getPlugins().apply(BasePlugin.class);

        BxSitesExtension extension = project.getExtensions().create("bxSites", BxSitesExtension.class, project);

        var provision = project.getTasks().register("bxSitesProvision", BxSitesProvisionTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Downloads (or reuses from cache) the boxlang-miniserver runtime and bx-sites itself.");
            task.getBoxlangMiniserverVersion().set(extension.getBoxlangMiniserverVersion());
            task.getBxSitesVersion().set(extension.getBxSitesVersion());
            task.getBoxlangHomeDir().set(extension.getBoxlangHomeDir());
        });

        project.getTasks().register("bxSitesNew", BxSitesNewTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Scaffolds a new bx-sites project (docs/ + config file).");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesBuild", BxSitesBuildTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Builds the bx-sites documentation site.");
            wireCommonProperties(task, extension, provision.get());
            task.getContentDir().set(project.getLayout().dir(
                    project.provider(() -> resolveContentDir(project,
                            extension.getProjectRoot().get().getAsFile().toPath()).toFile())));
            // Only contributed when it actually exists - see BxSitesBuildTask's
            // own javadoc on why this is a file collection, not @InputFile.
            task.getConfigFile().from(project.provider(() -> {
                java.nio.file.Path root = extension.getProjectRoot().get().getAsFile().toPath();
                java.nio.file.Path resolved = ConfigFileResolver.resolve(root);
                return java.nio.file.Files.isRegularFile(resolved)
                        ? java.util.List.of(resolved.toFile())
                        : java.util.List.<java.io.File>of();
            }));
            // Fixed, not independently configurable - bx-sites itself always writes to
            // <projectRoot>/site (confirmed in BuildPipeline.bx), so this is derived from
            // projectRoot rather than exposed as its own settable extension property.
            task.getSiteDir().set(project.getLayout().dir(
                    project.provider(() -> SiteDirResolver.resolve(
                            extension.getProjectRoot().get().getAsFile().toPath()).toFile())));
        });

        project.getTasks().register("bxSitesServe", BxSitesServeTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Builds and serves the bx-sites documentation site locally with live reload.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesClean", Delete.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Removes the built bx-sites site directory.");
            task.delete(project.provider(() -> SiteDirResolver.resolve(
                    extension.getProjectRoot().get().getAsFile().toPath()).toFile()));
        });

        project.getTasks().named("assemble", task -> {
            if (extension.getHookIntoAssemble().get()) {
                task.dependsOn(project.getTasks().named("bxSitesBuild"));
            }
        });
    }

    /**
     * {@link ContentDirResolver#resolve} falls back to {@code src/} when
     * {@code docs/} doesn't exist yet - correct for a standalone docs
     * project, but wrong for this plugin's headline use case (adding docs
     * to an existing Java project): in a project with the Java plugin
     * applied, {@code src/} is the Java source root, not bx-sites content.
     * Never fall back to it there - default to {@code docs/} instead,
     * matching what {@code bxSitesNew} would scaffold into anyway.
     */
    private static Path resolveContentDir(Project project, Path projectRoot) {
        Path docs = projectRoot.resolve("docs");
        boolean skipSrcFallback = Files.isDirectory(docs) || project.getPlugins().hasPlugin(JavaBasePlugin.class);
        return skipSrcFallback ? docs : ContentDirResolver.resolve(projectRoot);
    }

    private static void wireCommonProperties(AbstractBxSitesVerbTask task, BxSitesExtension extension, BxSitesProvisionTask provision) {
        task.dependsOn(provision);
        task.getProjectRoot().set(extension.getProjectRoot());
        task.getBoxlangHomeDir().set(extension.getBoxlangHomeDir());
        task.getBoxlangMiniserverVersion().set(extension.getBoxlangMiniserverVersion());
        task.getBxSitesVersion().set(extension.getBxSitesVersion());
        task.getExtraArgs().convention(java.util.List.of());
    }
}
