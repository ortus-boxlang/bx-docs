package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.ConfigFileResolver;
import ortus.boxlang.bxsites.core.springboot.OpenApiDocGenerator;

/**
 * {@code mvn bxsites:openapi} - wires a springdoc-generated OpenAPI/Swagger
 * spec into the bx-sites site (see {@link OpenApiDocGenerator}). Never
 * invokes the bx-sites subprocess at all, so - like {@link CleanMojo} -
 * this extends {@link AbstractMojo} directly rather than
 * {@link AbstractBxSitesMojo}, and needs no provisioning. Not bound to any
 * lifecycle phase by default; bind it yourself once your project's own
 * springdoc plugin has produced a spec file, e.g. to {@code pre-site}.
 */
@Mojo(name = "openapi", threadSafe = true)
public class OpenApiMojo extends AbstractMojo {

    @Parameter(property = "bxsites.projectRoot", defaultValue = "${project.basedir}", required = true)
    private File projectRoot;

    @Parameter(property = "bxsites.openapi.specFile", required = true)
    private File specFile;

    @Parameter(property = "bxsites.openapi.pageTitle", defaultValue = "API Reference")
    private String pageTitle;

    @Parameter(property = "bxsites.openapi.pagePath", defaultValue = "api/openapi.md")
    private String pagePath;

    @Parameter(property = "bxsites.openapi.autoPatchConfig", defaultValue = "false")
    private boolean autoPatchConfig;

    @Override
    public void execute() throws MojoExecutionException {
        Path root = projectRoot.toPath();
        OpenApiDocGenerator.Request request = new OpenApiDocGenerator.Request(
                specFile.toPath(),
                MavenContentDirs.resolve(root),
                ConfigFileResolver.resolve(root),
                pageTitle,
                Path.of(pagePath),
                autoPatchConfig);

        try {
            OpenApiDocGenerator.Result result = OpenApiDocGenerator.generate(request);
            getLog().info("Wrote " + result.pageFile() + " (spec: " + result.copiedSpecFile() + ")");
        } catch (IOException | IllegalStateException | IllegalArgumentException | UnsupportedOperationException e) {
            throw new MojoExecutionException("Failed to generate the OpenAPI doc page", e);
        }
    }
}
