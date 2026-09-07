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
    private final BxSitesJavadocExtension javadoc;
    private final BxSitesControllerScanExtension controllerScan;

    @Inject
    public BxSitesSpringBootExtension(Project project) {
        openApi = project.getObjects().newInstance(BxSitesOpenApiExtension.class, project);
        javadoc = project.getObjects().newInstance(BxSitesJavadocExtension.class, project);
        controllerScan = project.getObjects().newInstance(BxSitesControllerScanExtension.class, project);
        // "Enabled by default only when OpenAPI generation is off" (the plan's
        // own stated policy for the fallback generator) - a convention, not a
        // hardcoded value, so it re-evaluates lazily against whatever openApi
        // ends up configured to, and an explicit controllerScan.enabled.set(...)
        // still overrides it either way.
        controllerScan.getEnabled().convention(openApi.getEnabled().map(enabled -> !enabled));
    }

    public BxSitesOpenApiExtension getOpenApi() {
        return openApi;
    }

    public void openApi(Action<? super BxSitesOpenApiExtension> action) {
        action.execute(openApi);
    }

    public BxSitesJavadocExtension getJavadoc() {
        return javadoc;
    }

    public void javadoc(Action<? super BxSitesJavadocExtension> action) {
        action.execute(javadoc);
    }

    public BxSitesControllerScanExtension getControllerScan() {
        return controllerScan;
    }

    public void controllerScan(Action<? super BxSitesControllerScanExtension> action) {
        action.execute(controllerScan);
    }
}
