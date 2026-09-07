package ortus.boxlang.bxsites.gradle.springboot;

import java.util.List;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { springBoot { javadoc { } } } } DSL block - see
 * {@code ortus.boxlang.bxsites.core.springboot.JavadocDocGenerator} for
 * what actually happens, and its class javadoc for exactly what this
 * generator's deliberately-scoped-down v1 covers and doesn't.
 */
public abstract class BxSitesJavadocExtension {

    @Inject
    public BxSitesJavadocExtension(Project project) {
        getEnabled().convention(false);
        getPagePathPrefix().convention("api/javadoc");
        getTags().convention(List.of("api", "javadoc"));
    }

    /** Off by default - the {@code bxSitesJavadocDoc} task is a no-op unless this is set. */
    public abstract Property<Boolean> getEnabled();

    /**
     * The {@code .java} files to walk - typically
     * {@code sourceSets.main.allJava} in a project with the {@code java}
     * plugin applied. Empty by default; this plugin never assumes a
     * {@code java} plugin is present, so wire this explicitly.
     */
    public abstract ConfigurableFileCollection getSourceFiles();

    /** Where generated pages go, relative to the resolved content dir. */
    public abstract Property<String> getPagePathPrefix();

    /** Frontmatter tags applied to every generated page. */
    public abstract ListProperty<String> getTags();
}
