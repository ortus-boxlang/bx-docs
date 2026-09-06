package ortus.boxlang.bxsites.maven;

import java.io.File;
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

    /** Pinned default - see the plan's "Provisioning subsystem" for why this must stay a snapshot for now. */
    static final String DEFAULT_MINISERVER_VERSION = "1.18.0-snapshot";
    static final String DEFAULT_BXSITES_VERSION = "1.0.0-snapshot";

    @Parameter(defaultValue = "${project.basedir}", required = true)
    protected File projectRoot;

    @Parameter(defaultValue = "${project.build.directory}/bxsites/boxlang-home", required = true)
    protected File boxlangHomeDir;

    @Parameter(defaultValue = DEFAULT_MINISERVER_VERSION, required = true)
    protected String boxlangMiniserverVersion;

    @Parameter(defaultValue = DEFAULT_BXSITES_VERSION, required = true)
    protected String bxSitesVersion;

    @Parameter
    protected List<String> extraArgs = new ArrayList<>();

    protected abstract BxSitesVerb verb();

    /** The file whose existence/non-emptiness proves this verb succeeded, or {@code null} if none is defined. */
    protected File expectedOutputFile() {
        return null;
    }

    @Override
    public final void execute() throws MojoExecutionException {
        Provisioner provisioner = new Provisioner(Downloader.httpClient(), BxSitesConfig.defaultCacheDir());
        Path miniserverJar = provisioner.resolveMiniserverJar(boxlangMiniserverVersion);
        Path boxlangHome = provisioner.provisionBoxlangHome(bxSitesVersion, boxlangHomeDir.toPath());
        File expectedOutput = expectedOutputFile();

        BxSitesInvoker invoker = new BxSitesInvoker(miniserverJar, boxlangHome);
        BxSitesInvoker.InvocationResult result = invoker.invoke(
                verb(),
                projectRoot.toPath(),
                extraArgs,
                expectedOutput == null ? null : expectedOutput.toPath());

        getLog().info(result.output());

        if (!result.success()) {
            throw new MojoExecutionException(
                    "bxSites " + verb().verbId() + " failed (exit code " + result.exitCode() + "):\n" + result.output());
        }
    }
}
