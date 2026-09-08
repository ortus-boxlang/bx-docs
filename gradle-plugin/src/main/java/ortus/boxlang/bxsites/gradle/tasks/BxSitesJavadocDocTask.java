package ortus.boxlang.bxsites.gradle.tasks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import ortus.boxlang.bxsites.core.springboot.JavadocDocGenerator;

/**
 * Emits one Markdown page per public top-level Java type - see
 * {@link JavadocDocGenerator} for what actually happens, and its class
 * javadoc for exactly what this deliberately-scoped-down v1 generator
 * covers and doesn't. Not wired into any lifecycle by default - chain it
 * into {@code bxSitesBuild} yourself, e.g.
 * {@code tasks.named("bxSitesBuild") { dependsOn("bxSitesJavadocDoc") }}.
 */
@CacheableTask
public abstract class BxSitesJavadocDocTask extends DefaultTask {

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getSourceFiles();

    @Internal
    public abstract DirectoryProperty getContentDir();

    @Input
    public abstract Property<String> getPagePathPrefix();

    @Input
    public abstract ListProperty<String> getTags();

    @OutputDirectory
    public abstract DirectoryProperty getJavadocPagesDir();

    @TaskAction
    public void generate() {
        List<Path> sourceFiles = getSourceFiles().getFiles().stream().map(f -> f.toPath()).toList();

        JavadocDocGenerator.Request request = new JavadocDocGenerator.Request(
                sourceFiles,
                getContentDir().get().getAsFile().toPath(),
                getPagePathPrefix().get(),
                getTags().get());

        try {
            JavadocDocGenerator.Result result = JavadocDocGenerator.generate(request);
            result.warnings().forEach(getLogger()::warn);
            getLogger().lifecycle("Wrote " + result.pageFiles().size() + " Javadoc page(s) to "
                    + getContentDir().get().getAsFile().toPath().resolve(getPagePathPrefix().get()));
        } catch (IOException e) {
            throw new GradleException("Failed to generate Javadoc pages", e);
        }
    }
}
