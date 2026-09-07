package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.springboot.JavadocDocGenerator;

/**
 * {@code mvn bxsites:javadoc} - emits one Markdown page per public
 * top-level Java type (see {@link JavadocDocGenerator}, whose class
 * javadoc documents exactly what this deliberately-scoped-down v1
 * generator covers and doesn't). Never invokes the bx-sites subprocess, so
 * - like {@link CleanMojo} - this extends {@link AbstractMojo} directly.
 * Not bound to any lifecycle phase by default.
 */
@Mojo(name = "javadoc", threadSafe = true)
public class JavadocMojo extends AbstractMojo {

    @Parameter(property = "bxsites.projectRoot", defaultValue = "${project.basedir}", required = true)
    private File projectRoot;

    @Parameter(property = "bxsites.javadoc.sourceRoots", defaultValue = "${project.compileSourceRoots}")
    private List<String> sourceRoots;

    @Parameter(property = "bxsites.javadoc.pagePathPrefix", defaultValue = "api/javadoc")
    private String pagePathPrefix;

    @Parameter(property = "bxsites.javadoc.tags")
    private List<String> tags = List.of("api", "javadoc");

    @Override
    public void execute() throws MojoExecutionException {
        List<Path> sourceFiles = collectJavaSourceFiles();

        JavadocDocGenerator.Request request = new JavadocDocGenerator.Request(
                sourceFiles,
                MavenContentDirs.resolve(projectRoot.toPath()),
                pagePathPrefix,
                tags);

        try {
            JavadocDocGenerator.Result result = JavadocDocGenerator.generate(request);
            result.warnings().forEach(getLog()::warn);
            getLog().info("Wrote " + result.pageFiles().size() + " Javadoc page(s)");
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate Javadoc pages", e);
        }
    }

    private List<Path> collectJavaSourceFiles() throws MojoExecutionException {
        List<Path> sourceFiles = new ArrayList<>();
        for (String sourceRoot : sourceRoots) {
            Path root = Path.of(sourceRoot);
            if (!Files.isDirectory(root)) {
                continue;
            }
            try (var walk = Files.walk(root)) {
                walk.filter(p -> p.toString().endsWith(".java")).forEach(sourceFiles::add);
            } catch (IOException | UncheckedIOException e) {
                throw new MojoExecutionException("Failed to walk source root " + root, e);
            }
        }
        return sourceFiles;
    }
}
