package ortus.boxlang.bxsites.gradle.tasks;

import java.io.File;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code build} verb. Real up-to-date checking: re-runs only
 * when the content directory or version properties change; skipped
 * ("UP-TO-DATE") otherwise.
 */
@CacheableTask
public abstract class BxSitesBuildTask extends AbstractBxSitesVerbTask {

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract DirectoryProperty getContentDir();

    @OutputDirectory
    public abstract DirectoryProperty getSiteDir();

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.BUILD;
    }

    @Override
    protected File expectedOutputFile() {
        return getSiteDir().get().getAsFile().toPath().resolve("index.html").toFile();
    }
}
