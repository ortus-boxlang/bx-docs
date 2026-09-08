package ortus.boxlang.bxsites.gradle.tasks;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import ortus.boxlang.bxsites.core.springboot.ControllerScanMain;

/**
 * Runs {@link ControllerScanMain} in a forked JVM against the target
 * project's own runtime classpath - see
 * {@code ortus.boxlang.bxsites.core.springboot.ControllerScanGenerator}
 * for what actually happens, and its class javadoc for exactly what this
 * deliberately-scoped-down v1 generator covers and doesn't. Not
 * {@code @CacheableTask} - its output depends on the full contents of an
 * arbitrary external runtime classpath, which isn't a stable cache key.
 * Not wired into any lifecycle by default - chain it into
 * {@code bxSitesBuild} yourself.
 */
public abstract class BxSitesControllerScanDocTask extends DefaultTask {

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract DirectoryProperty getClassesDir();

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    public abstract ConfigurableFileCollection getRuntimeClasspath();

    @Internal
    public abstract DirectoryProperty getContentDir();

    @Input
    public abstract Property<String> getPagePathPrefix();

    @Input
    public abstract ListProperty<String> getTags();

    @OutputDirectory
    public abstract DirectoryProperty getControllerPagesDir();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @TaskAction
    public void generate() {
        // The default policy enables this task whenever OpenAPI generation
        // is off (see BxSitesControllerScanExtension) - harmless as long as
        // this stays a no-op until classesDir is actually wired up, rather
        // than failing the build the moment someone applies this plugin and
        // never touches controllerScan at all.
        if (!getClassesDir().isPresent()) {
            getLogger().lifecycle("bxSitesControllerScanDoc: no classesDir configured - skipping (nothing to scan).");
            return;
        }

        List<Object> classpath = new ArrayList<>();
        classpath.add(coreClasspathEntry());
        classpath.add(getClassesDir().get().getAsFile());
        for (File f : getRuntimeClasspath().getFiles()) {
            classpath.add(f);
        }

        getExecOperations().javaexec(spec -> {
            spec.classpath(classpath);
            spec.getMainClass().set(ControllerScanMain.class.getName());
            spec.args(
                    getClassesDir().get().getAsFile().getAbsolutePath(),
                    getContentDir().get().getAsFile().getAbsolutePath(),
                    getPagePathPrefix().get(),
                    String.join(",", getTags().get()));
        });
    }

    private static File coreClasspathEntry() {
        try {
            return new File(ControllerScanMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new GradleException("Could not resolve this plugin's own classpath entry for the controller scan", e);
        }
    }
}
