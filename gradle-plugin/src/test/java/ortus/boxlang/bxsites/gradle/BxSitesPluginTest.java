package ortus.boxlang.bxsites.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    void extension_hookIntoAssembleDefaultsToFalse(@TempDir Path projectDir) {
        Project project = newProject(projectDir);

        BxSitesExtension extension = project.getExtensions().getByType(BxSitesExtension.class);

        assertFalse(extension.getHookIntoAssemble().get());
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
