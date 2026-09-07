package ortus.boxlang.bxsites.gradle.tasks;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import ortus.boxlang.bxsites.core.BxSitesConfig;
import ortus.boxlang.bxsites.core.BxSitesInvoker;
import ortus.boxlang.bxsites.core.BxSitesVerb;
import ortus.boxlang.bxsites.core.provisioning.Provisioner;
import ortus.boxlang.bxsites.core.provisioning.Downloader;

/**
 * Shared base for every Gradle task that wraps a single bx-sites CLI verb.
 * Concrete subclasses only need to declare {@link #verb()}, their own
 * {@code @InputXxx}/{@code @OutputXxx}-annotated properties for Gradle's
 * up-to-date checking, and (where one exists) {@link #expectedOutputFile()}.
 */
public abstract class AbstractBxSitesVerbTask extends DefaultTask {

    @Internal
    public abstract DirectoryProperty getProjectRoot();

    @Internal
    public abstract DirectoryProperty getBoxlangHomeDir();

    @Input
    public abstract Property<String> getBoxlangMiniserverVersion();

    // Tracked so a pinned-version bump correctly invalidates this task's
    // up-to-date state - without it, e.g. bxSitesBuild stayed UP-TO-DATE even
    // though bxSitesProvision re-provisioned a different bx-sites version
    // into boxlangHomeDir underneath it (boxlangHomeDir's own contents are
    // deliberately @Internal, since BoxLang's own runtime writes logs/caches
    // into that same directory at verb-execution time - see the plan for why
    // that directory can't be a clean, fully input-tracked one yet).
    @Input
    public abstract Property<String> getBxSitesVersion();

    @Input
    public abstract ListProperty<String> getExtraArgs();

    protected abstract BxSitesVerb verb();

    /** The file whose existence/non-emptiness proves this verb succeeded, or {@code null} if none is defined. */
    protected File expectedOutputFile() {
        return null;
    }

    @TaskAction
    public final void run() {
        Provisioner provisioner = new Provisioner(Downloader.httpClient(), BxSitesConfig.defaultCacheDir());
        Path miniserverJar = provisioner.resolveMiniserverJar(getBoxlangMiniserverVersion().get());
        Path boxlangHome = getBoxlangHomeDir().get().getAsFile().toPath();
        Path projectRoot = getProjectRoot().get().getAsFile().toPath();
        File expectedOutput = expectedOutputFile();

        BxSitesInvoker invoker = new BxSitesInvoker(miniserverJar, boxlangHome);
        BxSitesInvoker.InvocationResult result = invoker.invoke(
                verb(),
                projectRoot,
                getExtraArgs().get(),
                expectedOutput == null ? null : expectedOutput.toPath());

        getLogger().lifecycle(result.output());

        if (!result.success()) {
            throw new GradleException(
                    "bxSites " + verb().verbId() + " failed (exit code " + result.exitCode() + "):\n" + result.output());
        }
    }
}
