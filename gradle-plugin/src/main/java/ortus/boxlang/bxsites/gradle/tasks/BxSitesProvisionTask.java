package ortus.boxlang.bxsites.gradle.tasks;

import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
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

    @OutputDirectory
    public abstract DirectoryProperty getBoxlangHomeDir();

    @TaskAction
    public void provision() {
        Provisioner provisioner = new Provisioner(Downloader.httpClient(), BxSitesConfig.defaultCacheDir());
        Path boxlangHome = getBoxlangHomeDir().get().getAsFile().toPath();
        provisioner.provision(getBoxlangMiniserverVersion().get(), getBxSitesVersion().get(), boxlangHome);
    }
}
