package ortus.boxlang.bxsites.gradle.boxlang;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { boxlang { coldbox { } } } } DSL block - the ColdBox
 * counterpart of {@code springBoot { controllerScan { } }}.
 *
 * <p>Where the Spring generator reflection-scans compiled classes, this
 * drives the bx-sites {@code coldbox} verb, which reads the application's
 * conventions off disk and never boots it. Nothing needs to be compiled,
 * and no datasource or environment has to be reachable, so it runs the
 * same on a CI runner as it does locally.
 */
public abstract class BxSitesColdBoxExtension {

    @Inject
    public BxSitesColdBoxExtension(Project project) {
        getEnabled().convention(false);
    }

    /** Off by default - the {@code bxSitesColdBoxDoc} task is a no-op unless this is set. */
    public abstract Property<Boolean> getEnabled();

    /** The ColdBox application root, relative to the project root. Defaults to the project root itself. */
    public abstract Property<String> getAppRoot();

    /** Where generated pages go, relative to the resolved content dir. */
    public abstract Property<String> getPagePathPrefix();

    /** Frontmatter tags applied to every generated page. */
    public abstract ListProperty<String> getTags();

    /**
     * Which page sets to generate: any of {@code routes}, {@code handlers},
     * {@code models}, {@code modules}, {@code interceptors},
     * {@code scheduler}. All of them when unset.
     */
    public abstract ListProperty<String> getInclude();

    /**
     * This block as {@code coldbox} verb flags - only what's actually set,
     * so anything left out falls through to {@code bxsites.yaml}.
     */
    public List<String> toVerbArguments() {
        List<String> args = new ArrayList<>();

        BxSitesDocBoxExtension.addIfPresent(args, "--appRoot", getAppRoot());
        BxSitesDocBoxExtension.addIfPresent(args, "--pagePathPrefix", getPagePathPrefix());

        if (getTags().isPresent() && !getTags().get().isEmpty()) {
            args.add("--tags=" + String.join(",", getTags().get()));
        }

        if (getInclude().isPresent() && !getInclude().get().isEmpty()) {
            args.add("--include=" + String.join(",", getInclude().get()));
        }

        return args;
    }
}
