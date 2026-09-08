package ortus.boxlang.bxsites.maven;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import ortus.boxlang.bxsites.core.springboot.ControllerScanMain;

/**
 * {@code mvn bxsites:controller-scan} - reflection-scans compiled classes
 * for Spring MVC controllers and emits one page per controller (see
 * {@code ortus.boxlang.bxsites.core.springboot.ControllerScanGenerator},
 * whose class javadoc documents exactly what this deliberately-scoped-down
 * v1 generator covers and doesn't). Runs the scan in a forked JVM against
 * this project's own runtime classpath (Spring included) - never in this
 * plugin's own JVM, which stays Spring-agnostic. Not bound to any
 * lifecycle phase by default.
 */
@Mojo(name = "controller-scan", threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class ControllerScanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "bxsites.projectRoot", defaultValue = "${project.basedir}", required = true)
    private File projectRoot;

    @Parameter(property = "bxsites.controllerScan.classesDir", defaultValue = "${project.build.outputDirectory}")
    private File classesDir;

    @Parameter(property = "bxsites.controllerScan.pagePathPrefix", defaultValue = "api/controllers")
    private String pagePathPrefix;

    @Parameter(property = "bxsites.controllerScan.tags")
    private List<String> tags = List.of("api", "controllers");

    @Override
    public void execute() throws MojoExecutionException {
        if (!classesDir.isDirectory()) {
            getLog().info("bxsites:controller-scan: classesDir does not exist (" + classesDir + ") - skipping.");
            return;
        }

        List<String> classpath = new ArrayList<>();
        classpath.add(coreClasspathEntry());
        classpath.add(classesDir.getAbsolutePath());
        try {
            classpath.addAll(project.getRuntimeClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to resolve the runtime classpath for the controller scan", e);
        }

        List<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        command.add("-cp");
        command.add(String.join(File.pathSeparator, classpath));
        command.add(ControllerScanMain.class.getName());
        command.add(classesDir.getAbsolutePath());
        command.add(MavenContentDirs.resolve(projectRoot.toPath()).toString());
        command.add(pagePathPrefix);
        command.add(String.join(",", tags));

        try {
            Process process = new ProcessBuilder(command).inheritIO().start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoExecutionException("Controller scan subprocess failed (exit code " + exitCode + ")");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to run the controller scan subprocess", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException("Interrupted while waiting for the controller scan subprocess", e);
        }
    }

    private static String coreClasspathEntry() throws MojoExecutionException {
        try {
            return new File(ControllerScanMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new MojoExecutionException("Could not resolve this plugin's own classpath entry for the controller scan", e);
        }
    }
}
