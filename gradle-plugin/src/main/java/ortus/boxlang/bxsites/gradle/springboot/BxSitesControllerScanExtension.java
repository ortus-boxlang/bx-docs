package ortus.boxlang.bxsites.gradle.springboot;

import java.util.List;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { springBoot { controllerScan { } } } } DSL block -
 * see {@code ortus.boxlang.bxsites.core.springboot.ControllerScanGenerator}
 * for what actually happens, and its class javadoc for exactly what this
 * deliberately-scoped-down v1 generator covers and doesn't.
 *
 * <p>{@link #getEnabled()}'s convention is wired by
 * {@link BxSitesSpringBootExtension} to the plan's own stated default -
 * "enabled by default only when OpenAPI generation is off" - rather than a
 * flat {@code false} here, so a user who never touches either block still
 * gets the plan's intended out-of-the-box behavior.
 */
public abstract class BxSitesControllerScanExtension {

    @Inject
    public BxSitesControllerScanExtension(Project project) {
        getPagePathPrefix().convention("api/controllers");
        getTags().convention(List.of("api", "controllers"));
    }

    /** See the class javadoc - defaults to "on unless OpenAPI generation is on", not a flat default. */
    public abstract Property<Boolean> getEnabled();

    /** Where the target project's compiled classes live, e.g. {@code sourceSets.main.output.classesDirs.singleFile}. */
    public abstract DirectoryProperty getClassesDir();

    /**
     * The target project's own full runtime classpath (its compiled classes
     * plus every dependency, Spring included) - the forked scan JVM needs
     * this since real reflection is how it resolves Spring's annotations.
     */
    public abstract ConfigurableFileCollection getRuntimeClasspath();

    /** Where generated pages go, relative to the resolved content dir. */
    public abstract Property<String> getPagePathPrefix();

    /** Frontmatter tags applied to every generated page. */
    public abstract ListProperty<String> getTags();
}
