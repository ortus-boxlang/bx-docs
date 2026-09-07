package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.BxSitesConfig;
import ortus.boxlang.bxsites.core.BxSitesInvoker;
import ortus.boxlang.bxsites.core.BxSitesVerb;
import ortus.boxlang.bxsites.core.provisioning.Downloader;
import ortus.boxlang.bxsites.core.provisioning.Provisioner;

/**
 * Shared base for every Maven goal that wraps a single bx-sites CLI verb -
 * the Maven-side mirror of {@code AbstractBxSitesVerbTask} in the Gradle
 * plugin. Both are thin adapters over the same {@code core} module, so the
 * two build tools can't drift on verb behavior.
 *
 * <p>Unlike the Gradle plugin (where a separate {@code bxSitesProvision}
 * task is a shared prerequisite other tasks {@code dependsOn}), each Mojo
 * provisions for itself at the start of {@link #execute()} - {@code core}'s
 * own cache makes a repeat call within the same {@code mvn} invocation a
 * cheap no-op, so there's no need to force a separate goal into Maven's own
 * goal-binding model just to share this step.
 */
public abstract class AbstractBxSitesMojo extends AbstractMojo {

    @Parameter(property = "bxsites.projectRoot", defaultValue = "${project.basedir}", required = true)
    protected File projectRoot;

    @Parameter(property = "bxsites.boxlangHomeDir", defaultValue = "${project.build.directory}/bxsites/boxlang-home", required = true)
    protected File boxlangHomeDir;

    @Parameter(property = "bxsites.boxlangMiniserverVersion", defaultValue = BxSitesConfig.DEFAULT_MINISERVER_VERSION, required = true)
    protected String boxlangMiniserverVersion;

    @Parameter(property = "bxsites.bxSitesVersion", defaultValue = BxSitesConfig.DEFAULT_BXSITES_VERSION, required = true)
    protected String bxSitesVersion;

    @Parameter(property = "bxsites.extraArgs")
    protected List<String> extraArgs = new ArrayList<>();

    protected abstract BxSitesVerb verb();

    /** The file whose existence/non-emptiness proves this verb succeeded, or {@code null} if none is defined. */
    protected File expectedOutputFile() {
        return null;
    }

    @Override
    public final void execute() throws MojoExecutionException {
        BxSitesInvoker.InvocationResult result;
        try {
            Provisioner provisioner = new Provisioner(Downloader.httpClient(), BxSitesConfig.defaultCacheDir());
            Path miniserverJar = provisioner.resolveMiniserverJar(boxlangMiniserverVersion);
            Path boxlangHome = provisioner.provisionBoxlangHome(bxSitesVersion, boxlangHomeDir.toPath());
            File expectedOutput = expectedOutputFile();

            BxSitesInvoker invoker = new BxSitesInvoker(miniserverJar, boxlangHome);
            result = invoker.invoke(
                    verb(),
                    projectRoot.toPath(),
                    extraArgs,
                    expectedOutput == null ? null : expectedOutput.toPath());
        } catch (UncheckedIOException | IllegalStateException e) {
            // Provisioner/BxSitesInvoker throw these as plain unchecked
            // exceptions (shared with the Gradle plugin, which lets Gradle's
            // own exception handling deal with it) - wrapped here so a
            // provisioning failure (network error, checksum mismatch, a bad
            // pinned version), a subprocess launch failure, or a timeout all
            // surface as a normal Maven build failure rather than an opaque
            // PluginExecutionException wrapping an arbitrary unchecked type.
            throw new MojoExecutionException("Failed to run the " + verb().verbId() + " goal", e);
        }

        getLog().info(result.output());

        if (!result.success()) {
            throw new MojoExecutionException(
                    "bxSites " + verb().verbId() + " failed (exit code " + result.exitCode() + "):\n" + result.output());
        }
    }
}
