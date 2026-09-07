package ortus.boxlang.bxsites.gradle.tasks;

import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import ortus.boxlang.bxsites.core.BxSitesConfig;
import ortus.boxlang.bxsites.core.provisioning.Downloader;
import ortus.boxlang.bxsites.core.provisioning.Provisioner;

/**
 * Downloads (or reuses from the shared cache) the boxlang-miniserver jar and
 * the bx-sites {@code -with-deps} bundle, then assembles this project's own
 * {@code BOXLANG_HOME} from them. Every verb task depends on this one.
 */
@CacheableTask
public abstract class BxSitesProvisionTask extends DefaultTask {

    @Input
    public abstract Property<String> getBoxlangMiniserverVersion();

    @Input
    public abstract Property<String> getBxSitesVersion();

    /**
     * Where this project's {@code BOXLANG_HOME} lives. Deliberately
     * {@code @Internal}, not {@code @OutputDirectory}: BoxLang's own runtime
     * writes logs/caches/{@code version.properties} directly under this
     * directory (siblings of {@code modules/}) every time a verb subprocess
     * runs, so tracking the whole tree as this task's output made Gradle
     * disable caching for it outright ("output caching requires exclusive
     * access to output paths") the moment any verb task had ever run - see
     * {@link #getProvisionedModuleDir()} for the part that's actually this
     * task's own, exclusively-owned output.
     */
    @Internal
    public abstract DirectoryProperty getBoxlangHomeDir();

    /**
     * The bx-sites module tree this task actually owns and writes
     * exclusively - {@code <boxlangHomeDir>/modules/bxsites}. This, not the
     * whole {@code boxlangHomeDir}, is what Gradle should track for
     * up-to-date/build-cache purposes.
     */
    @OutputDirectory
    public abstract DirectoryProperty getProvisionedModuleDir();

    @TaskAction
    public void provision() {
        Provisioner provisioner = new Provisioner(Downloader.httpClient(), BxSitesConfig.defaultCacheDir());
        Path boxlangHome = getBoxlangHomeDir().get().getAsFile().toPath();
        provisioner.provision(getBoxlangMiniserverVersion().get(), getBxSitesVersion().get(), boxlangHome);
    }
}
