package ortus.boxlang.bxsites.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ortus.boxlang.bxsites.gradle.tasks.AbstractBxSitesVerbTask;
import ortus.boxlang.bxsites.gradle.tasks.BxSitesBuildTask;

/**
 * Fast, offline unit tests for plugin/task wiring - built with
 * {@link ProjectBuilder} rather than Gradle TestKit, so these need no
 * network and run in milliseconds. Real end-to-end behavior (a genuine
 * subprocess invocation against the live network) is covered separately by
 * {@code BxSitesPluginFunctionalTest}.
 */
class BxSitesPluginTest {

    @Test
    void apply_registersAllCoreTasks(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("bxSitesProvision"));
        assertNotNull(project.getTasks().findByName("bxSitesNew"));
        assertNotNull(project.getTasks().findByName("bxSitesBuild"));
        assertNotNull(project.getTasks().findByName("bxSitesServe"));
        assertNotNull(project.getTasks().findByName("bxSitesClean"));
    }

    @Test
    void apply_createsAssembleAndCheckEvenWithoutTheJavaPlugin(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("assemble"));
        assertNotNull(project.getTasks().findByName("check"));
    }

    @Test
    void bxSitesBuild_dependsOnBxSitesProvision(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        Task build = project.getTasks().getByName("bxSitesBuild");

        assertTrue(dependsOnTransitively(build, "bxSitesProvision"), "bxSitesBuild must depend on bxSitesProvision");
    }

    @Test
    void bxSitesBuild_siteDirIsAlwaysProjectRootSlashSite_neverIndependentlyConfigurable(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertEquals(projectDir.resolve("site"), build.getSiteDir().get().getAsFile().toPath());
    }

    @Test
    void bxSitesBuild_contentDirPrefersDocsOverSrc(@TempDir Path projectDir) throws IOException {
        Files.createDirectories(projectDir.resolve("docs"));
        Files.createDirectories(projectDir.resolve("src"));
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertEquals(projectDir.resolve("docs"), build.getContentDir().get().getAsFile().toPath());
    }

    @Test
    void bxSitesBuild_contentDirFallsBackToSrcWhenNoJavaPluginIsApplied(@TempDir Path projectDir) throws IOException {
        Files.createDirectories(projectDir.resolve("src"));
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertEquals(projectDir.resolve("src"), build.getContentDir().get().getAsFile().toPath());
    }

    @Test
    void bxSitesBuild_contentDirNeverFallsBackToSrcWhenTheJavaPluginIsApplied(@TempDir Path projectDir) throws IOException {
        // src/ is the Java source root here, not bx-sites content - falling
        // back to it would silently point the docs build at Java sources.
        Files.createDirectories(projectDir.resolve("src").resolve("main").resolve("java"));
        Project project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        project.getPluginManager().apply("java");
        project.getPluginManager().apply(BxSitesPlugin.class);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertEquals(projectDir.resolve("docs"), build.getContentDir().get().getAsFile().toPath());
    }

    @Test
    void bxSitesBuild_configFileIsEmptyWhenNoConfigFileExists(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertTrue(build.getConfigFile().getFiles().isEmpty(),
                "no bxsites.yaml/.toml/.json exists yet - the input collection should be empty, not point at a nonexistent file");
    }

    @Test
    void bxSitesBuild_configFileIsContributedWhenBxsitesYamlExists(@TempDir Path projectDir) throws IOException {
        Files.writeString(projectDir.resolve("bxsites.yaml"), "name: \"Test\"\n");
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");

        assertEquals(java.util.Set.of(projectDir.resolve("bxsites.yaml").toFile()), build.getConfigFile().getFiles());
    }

    @Test
    void bxSitesBuild_bxSitesVersionIsWiredFromTheExtension(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        BxSitesBuildTask build = (BxSitesBuildTask) project.getTasks().getByName("bxSitesBuild");
        BxSitesExtension extension = project.getExtensions().getByType(BxSitesExtension.class);

        assertEquals(extension.getBxSitesVersion().get(), build.getBxSitesVersion().get());
    }

    @Test
    void apply_registersAllFastFollowVerbTasks(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("bxSitesSearchIndex"));
        assertNotNull(project.getTasks().findByName("bxSitesLint"));
        assertNotNull(project.getTasks().findByName("bxSitesDeploy"));
        assertNotNull(project.getTasks().findByName("bxSitesPublish"));
        assertNotNull(project.getTasks().findByName("bxSitesPackage"));
        assertNotNull(project.getTasks().findByName("bxSitesStats"));
        assertNotNull(project.getTasks().findByName("bxSitesDoctor"));
    }

    @Test
    void extension_hookIntoCheckDefaultsToTrue_wiresBxSitesLintIntoCheck(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        Task check = project.getTasks().getByName("check");

        assertTrue(project.getExtensions().getByType(BxSitesExtension.class).getHookIntoCheck().get());
        assertTrue(dependsOnTransitively(check, "bxSitesLint"), "check should depend on bxSitesLint by default");
    }

    @Test
    void extension_hookIntoCheckFalse_doesNotWireBxSitesLintIntoCheck(@TempDir Path projectDir) {
        Project project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        project.getPluginManager().apply(BxSitesPlugin.class);
        project.getExtensions().getByType(BxSitesExtension.class).getHookIntoCheck().set(false);

        Task check = project.getTasks().getByName("check");

        assertFalse(dependsOnTransitively(check, "bxSitesLint"));
    }

    @Test
    void extension_hookIntoAssembleDefaultsToFalse(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        BxSitesExtension extension = project.getExtensions().getByType(BxSitesExtension.class);

        assertFalse(extension.getHookIntoAssemble().get());
    }

    @Test
    void apply_registersTheOpenApiDocTask(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("bxSitesOpenApiDoc"));
    }

    @Test
    void springBootOpenApiExtension_hasSensibleDefaults(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        var openApi = project.getExtensions().getByType(BxSitesExtension.class).getSpringBoot().getOpenApi();

        assertFalse(openApi.getEnabled().get());
        assertEquals("API Reference", openApi.getPageTitle().get());
        assertEquals("api/openapi.md", openApi.getPagePath().get());
        assertFalse(openApi.getAutoPatchConfig().get());
    }

    @Test
    void springBootOpenApiExtension_isConfigurableViaTheNestedDsl(@TempDir Path projectDir) {
        Project project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        project.getPluginManager().apply(BxSitesPlugin.class);
        BxSitesExtension extension = project.getExtensions().getByType(BxSitesExtension.class);

        extension.springBoot(springBoot -> springBoot.openApi(openApi -> {
            openApi.getEnabled().set(true);
            openApi.getPageTitle().set("Bookshelf API");
        }));

        var openApi = extension.getSpringBoot().getOpenApi();
        assertTrue(openApi.getEnabled().get());
        assertEquals("Bookshelf API", openApi.getPageTitle().get());
    }

    @Test
    void apply_registersTheJavadocDocTask(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("bxSitesJavadocDoc"));
    }

    @Test
    void springBootJavadocExtension_hasSensibleDefaults(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        var javadoc = project.getExtensions().getByType(BxSitesExtension.class).getSpringBoot().getJavadoc();

        assertFalse(javadoc.getEnabled().get());
        assertEquals("api/javadoc", javadoc.getPagePathPrefix().get());
        assertEquals(java.util.List.of("api", "javadoc"), javadoc.getTags().get());
        assertTrue(javadoc.getSourceFiles().isEmpty());
    }

    @Test
    void apply_registersTheDocBoxDocTask(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        assertNotNull(project.getTasks().findByName("bxSitesDocBoxDoc"));
    }

    @Test
    void apply_registersNoColdBoxTask(@TempDir Path projectDir) {
        // A ColdBox application is built through CommandBox, never Gradle -
        // the `coldbox` verb is a bx-sites CLI concern only.
        Project project = newProject(projectDir);

        assertNull(project.getTasks().findByName("bxSitesColdBoxDoc"));
    }

    @Test
    void boxlangDocBoxExtension_hasSensibleDefaults(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        var docbox = project.getExtensions().getByType(BxSitesExtension.class).getBoxlang().getDocbox();

        assertFalse(docbox.getEnabled().get());
        assertTrue(docbox.getMappings().get().isEmpty());
        assertTrue(docbox.toVerbArguments().isEmpty());
    }

    @Test
    void boxlangDocBoxExtension_turnsItsOptionsIntoVerbFlags(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        var docbox = project.getExtensions().getByType(BxSitesExtension.class).getBoxlang().getDocbox();
        docbox.getMappings().put("models", "models");
        docbox.getProjectTitle().set("Bookshelf API");
        docbox.getPagePathPrefix().set("api/classes");
        docbox.getTags().set(java.util.List.of("api", "classes"));

        var args = docbox.toVerbArguments();

        assertTrue(args.contains("--mappings:models=models"));
        assertTrue(args.contains("--projectTitle=Bookshelf API"));
        assertTrue(args.contains("--pagePathPrefix=api/classes"));
        assertTrue(args.contains("--tags=api,classes"));
        // Never passed when unset, so the project's own config still decides.
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--excludes")));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--jsonDir")));
    }

    @Test
    void docBoxTask_carriesItsExtensionOptionsAsVerbArguments(@TempDir Path projectDir) {
        Project project = newProject(projectDir);
        project.getExtensions().getByType(BxSitesExtension.class).getBoxlang().getDocbox()
                .getProjectTitle().set("Bookshelf API");

        var docboxTask = (AbstractBxSitesVerbTask) project.getTasks().getByName("bxSitesDocBoxDoc");

        assertTrue(docboxTask.getExtraArgs().get().contains("--projectTitle=Bookshelf API"));
    }

    @Test
    void extension_hookIntoAssembleTrue_wiresBxSitesBuildIntoAssemble(@TempDir Path projectDir) {
        // BxSitesPlugin registers its `assemble` wiring via Tasks.named(...),
        // which defers evaluation until `assemble` is actually realized/
        // configured - so setting the extension property here, before that
        // realization happens below, mirrors how a real build.gradle.kts
        // (extension block evaluated during script execution, before task
        // realization) would behave.
        Project project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        project.getPluginManager().apply(BxSitesPlugin.class);
        project.getExtensions().getByType(BxSitesExtension.class).getHookIntoAssemble().set(true);

        Task assemble = project.getTasks().getByName("assemble");

        assertTrue(dependsOnTransitively(assemble, "bxSitesBuild"));
    }

    private static Project newProject(Path projectDir) {
        Project project = ProjectBuilder.builder().withProjectDir(projectDir.toFile()).build();
        project.getPluginManager().apply(BxSitesPlugin.class);
        return project;
    }

    private static boolean dependsOnTransitively(Task task, String taskName) {
        return task.getTaskDependencies().getDependencies(task).stream()
                .anyMatch(dep -> dep.getName().equals(taskName));
    }
}
