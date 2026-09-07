package ortus.boxlang.bxsites.gradle;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.Delete;

import ortus.boxlang.bxsites.core.ConfigFileResolver;
import ortus.boxlang.bxsites.core.ContentDirResolver;
import ortus.boxlang.bxsites.core.SiteDirResolver;
import ortus.boxlang.bxsites.core.provisioning.ArtifactCoordinates;
import ortus.boxlang.bxsites.gradle.tasks.AbstractBxSitesVerbTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesBuildTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesControllerScanDocTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesDeployTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesDoctorTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesJavadocDocTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesLintTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesNewTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesOpenApiDocTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesPackageTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesProvisionTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesPublishTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesSearchIndexTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesServeTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesStatsTask;

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
            task.getProvisionedModuleDir().set(project.getLayout().dir(
                    project.provider(() -> extension.getBoxlangHomeDir().get().getAsFile().toPath()
                            .resolve("modules").resolve(ArtifactCoordinates.BXSITES_MODULE_MAPPING_NAME).toFile())));
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
            task.getConfigFile().from(project.provider(() -> resolveConfigFileIfPresent(
                    extension.getProjectRoot().get().getAsFile().toPath())));
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

        // Fast-follow verb tasks - same thin AbstractBxSitesVerbTask wrapper
        // pattern as the core four above, one per remaining bx-sites verb
        // that's a natural fit for a build-tool task (the CMS-content verbs -
        // post:new, page:new, theme:*, i18n:*, blog:*, etc. - are more
        // interactive CLI conveniences than build-pipeline steps, so they're
        // deliberately not wrapped here).
        project.getTasks().register("bxSitesSearchIndex", BxSitesSearchIndexTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Rebuilds site/search-index.json without a full site build.");
            wireCommonProperties(task, extension, provision.get());
        });

        var lint = project.getTasks().register("bxSitesLint", BxSitesLintTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Lints the docs/ Markdown source.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesDeploy", BxSitesDeployTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Builds the site and deploys it to the configured target.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesPublish", BxSitesPublishTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Builds the site and publishes it to bxSites Cloud.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesPackage", BxSitesPackageTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Builds the site and zips it to site.zip.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesStats", BxSitesStatsTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Reports page/word counts and other stats for the built site.");
            wireCommonProperties(task, extension, provision.get());
        });

        project.getTasks().register("bxSitesDoctor", BxSitesDoctorTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Runs bx-sites' own project health diagnostics.");
            wireCommonProperties(task, extension, provision.get());
        });

        // Spring Boot doc generators - build-tool-native tasks, not verb
        // wrappers, since they never invoke the bx-sites subprocess at all.
        project.getTasks().register("bxSitesOpenApiDoc", BxSitesOpenApiDocTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Wires a springdoc-generated OpenAPI spec into the bx-sites site.");
            var openApi = extension.getSpringBoot().getOpenApi();
            task.getSpecFile().set(openApi.getSpecFile());
            task.getContentDir().set(project.getLayout().dir(
                    project.provider(() -> resolveContentDir(project,
                            extension.getProjectRoot().get().getAsFile().toPath()).toFile())));
            task.getConfigFile().from(project.provider(() -> resolveConfigFileIfPresent(
                    extension.getProjectRoot().get().getAsFile().toPath())));
            task.getPageTitle().set(openApi.getPageTitle());
            task.getPagePath().set(openApi.getPagePath());
            task.getAutoPatchConfig().set(openApi.getAutoPatchConfig());
            task.getOpenApiAssetsDir().set(task.getContentDir().dir("assets/openapi"));
            task.getPageFile().set(task.getContentDir().flatMap(dir -> task.getPagePath().map(dir::file)));
            task.onlyIf(t -> openApi.getEnabled().get());
        });

        project.getTasks().register("bxSitesJavadocDoc", BxSitesJavadocDocTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Emits one Markdown page per public top-level Java type.");
            var javadoc = extension.getSpringBoot().getJavadoc();
            task.getSourceFiles().from(javadoc.getSourceFiles());
            task.getContentDir().set(project.getLayout().dir(
                    project.provider(() -> resolveContentDir(project,
                            extension.getProjectRoot().get().getAsFile().toPath()).toFile())));
            task.getPagePathPrefix().set(javadoc.getPagePathPrefix());
            task.getTags().set(javadoc.getTags());
            task.getJavadocPagesDir().set(task.getContentDir().dir(javadoc.getPagePathPrefix()));
            task.onlyIf(t -> javadoc.getEnabled().get());
        });

        project.getTasks().register("bxSitesControllerScanDoc", BxSitesControllerScanDocTask.class, task -> {
            task.setGroup("bx-sites");
            task.setDescription("Reflection-scans compiled classes for Spring MVC controllers and emits one page per controller.");
            var controllerScan = extension.getSpringBoot().getControllerScan();
            task.getClassesDir().set(controllerScan.getClassesDir());
            task.getRuntimeClasspath().from(controllerScan.getRuntimeClasspath());
            task.getContentDir().set(project.getLayout().dir(
                    project.provider(() -> resolveContentDir(project,
                            extension.getProjectRoot().get().getAsFile().toPath()).toFile())));
            task.getPagePathPrefix().set(controllerScan.getPagePathPrefix());
            task.getTags().set(controllerScan.getTags());
            task.getControllerPagesDir().set(task.getContentDir().dir(controllerScan.getPagePathPrefix()));
            task.onlyIf(t -> controllerScan.getEnabled().get());
        });

        project.getTasks().named("assemble", task -> {
            if (extension.getHookIntoAssemble().get()) {
                task.dependsOn(project.getTasks().named("bxSitesBuild"));
            }
        });

        project.getTasks().named("check", task -> {
            if (extension.getHookIntoCheck().get()) {
                task.dependsOn(lint);
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

    private static List<File> resolveConfigFileIfPresent(Path projectRoot) {
        Path resolved = ConfigFileResolver.resolve(projectRoot);
        return Files.isRegularFile(resolved) ? List.of(resolved.toFile()) : List.of();
    }

    private static void wireCommonProperties(AbstractBxSitesVerbTask task, BxSitesExtension extension, BxSitesProvisionTask provision) {
        task.dependsOn(provision);
        task.getProjectRoot().set(extension.getProjectRoot());
        task.getBoxlangHomeDir().set(extension.getBoxlangHomeDir());
        task.getBoxlangMiniserverVersion().set(extension.getBoxlangMiniserverVersion());
        task.getBxSitesVersion().set(extension.getBxSitesVersion());
        task.getExtraArgs().convention(List.of());
    }
}
