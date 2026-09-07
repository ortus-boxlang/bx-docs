package ortus.boxlang.bxsites.gradle.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import ortus.boxlang.bxsites.core.springboot.OpenApiDocGenerator;

/**
 * Wires a springdoc-generated OpenAPI spec into the bx-sites site - see
 * {@link OpenApiDocGenerator} for what actually happens. Deliberately not
 * wired into any lifecycle (mirrors {@code bxSitesNew}'s own
 * "run it once, explicitly, or wire it yourself" convention) - a consuming
 * project chains it into {@code bxSitesBuild} itself, e.g.
 * {@code tasks.named("bxSitesBuild") { dependsOn("bxSitesOpenApiDoc") }},
 * once its own springdoc task has produced a spec file.
 */
@CacheableTask
public abstract class BxSitesOpenApiDocTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract RegularFileProperty getSpecFile();

    @Internal
    public abstract DirectoryProperty getContentDir();

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.NONE)
    public abstract ConfigurableFileCollection getConfigFile();

    @Input
    public abstract Property<String> getPageTitle();

    @Input
    public abstract Property<String> getPagePath();

    @Input
    public abstract Property<Boolean> getAutoPatchConfig();

    @OutputDirectory
    public abstract DirectoryProperty getOpenApiAssetsDir();

    @OutputFile
    public abstract RegularFileProperty getPageFile();

    @TaskAction
    public void generate() {
        File configFile = getConfigFile().getFiles().stream().findFirst()
                .orElseThrow(() -> new GradleException(
                        "No bxsites.yaml/.yml/.toml/.json config file found at the project root - run bxSitesNew first."));

        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                getSpecFile().get().getAsFile().toPath(),
                getContentDir().get().getAsFile().toPath(),
                configFile.toPath(),
                getPageTitle().get(),
                Path.of(getPagePath().get()),
                getAutoPatchConfig().get());

        try {
            OpenApiDocGenerator.Result result = OpenApiDocGenerator.generate(request);
            getLogger().lifecycle("Wrote " + result.pageFile() + " (spec: " + result.copiedSpecFile() + ")");
        } catch (IOException e) {
            throw new GradleException("Failed to generate the OpenAPI doc page", e);
        }
    }
}
