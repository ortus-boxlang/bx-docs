package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.SiteDirResolver;

/**
 * {@code mvn bxsites:clean} - removes the built bx-sites site directory.
 * Plain directory deletion, no subprocess needed - mirrors the Gradle
 * plugin's {@code bxSitesClean}, which is a plain {@code Delete} task for
 * the same reason.
 */
@Mojo(name = "clean", threadSafe = true)
public class CleanMojo extends AbstractMojo {

    @Parameter(property = "bxsites.projectRoot", defaultValue = "${project.basedir}", required = true)
    private File projectRoot;

    @Override
    public void execute() throws MojoExecutionException {
        Path siteDir = SiteDirResolver.resolve(projectRoot.toPath());
        if (!Files.exists(siteDir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(siteDir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException | UncheckedIOException e) {
            throw new MojoExecutionException("Failed to delete " + siteDir, e);
        }
        getLog().info("Removed: " + siteDir);
    }
}
