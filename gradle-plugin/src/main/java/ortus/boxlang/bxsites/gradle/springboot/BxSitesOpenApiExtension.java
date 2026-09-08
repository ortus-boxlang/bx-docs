package ortus.boxlang.bxsites.gradle.springboot;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { springBoot { openApi { } } } } DSL block - wires a
 * springdoc-generated OpenAPI/Swagger spec into the bx-sites site. See
 * {@code ortus.boxlang.bxsites.core.springboot.OpenApiDocGenerator} for
 * what actually happens; this is only the Gradle-native configuration
 * surface over it.
 */
public abstract class BxSitesOpenApiExtension {

    @Inject
    public BxSitesOpenApiExtension(Project project) {
        getEnabled().convention(false);
        getSpecFile().convention(project.getLayout().getBuildDirectory().file("openapi/openapi.json"));
        getPageTitle().convention("API Reference");
        getPagePath().convention("api/openapi.md");
        getAutoPatchConfig().convention(false);
    }

    /** Off by default - the {@code bxSitesOpenApiDoc} task is a no-op unless this is set. */
    public abstract Property<Boolean> getEnabled();

    /** Where springdoc's own Gradle plugin wrote the spec - point this at its output file. */
    public abstract RegularFileProperty getSpecFile();

    /** Title used for both the generated page's frontmatter and the {@code ::: openapi :::} block. */
    public abstract Property<String> getPageTitle();

    /** Where to write the generated page, relative to the resolved content dir. */
    public abstract Property<String> getPagePath();

    /**
     * When {@code bxsites.yaml}/{@code .toml} doesn't already have
     * {@code openapi: true}, patch it in automatically instead of failing
     * with an actionable error. JSON configs are never auto-patched - see
     * {@code OpenApiConfigFlag}.
     */
    public abstract Property<Boolean> getAutoPatchConfig();
}
