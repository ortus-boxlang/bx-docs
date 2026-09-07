package ortus.boxlang.bxsites.gradle.springboot;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * The {@code bxSites { springBoot { } } } DSL block - the parent config
 * object for all three Spring Boot doc generators (OpenAPI, Javadoc,
 * controller-scan). Each generator gets its own nested block so there's no
 * ambiguity about apply order between generators feeding the same content
 * dir. Only {@link #getOpenApi()} exists so far; {@code javadoc}/
 * {@code controllerScan} are added as those generators land.
 */
public abstract class BxSitesSpringBootExtension {

    private final BxSitesOpenApiExtension openApi;

    @Inject
    public BxSitesSpringBootExtension(Project project) {
        openApi = project.getObjects().newInstance(BxSitesOpenApiExtension.class, project);
    }

    public BxSitesOpenApiExtension getOpenApi() {
        return openApi;
    }

    public void openApi(Action<? super BxSitesOpenApiExtension> action) {
        action.execute(openApi);
    }
}
