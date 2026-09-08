package ortus.boxlang.bxsites.gradle.boxlang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

/**
 * The {@code bxSites { boxlang { docbox { } } } } DSL block - the BoxLang
 * counterpart of {@code springBoot { javadoc { } }}, generating a
 * BoxLang/CFML API reference from DocBox instead of a Java one from the
 * Javadoc doclet.
 *
 * <p>Unlike the Spring Boot generators (which run in this plugin's own
 * JVM), this drives the bx-sites {@code docbox} verb, so every option here
 * becomes a CLI flag rather than a Java call. That keeps one implementation
 * of the generator - the BoxLang one - rather than a second Java port that
 * could drift from it.
 *
 * <p>Requires the {@code bx-docbox} module to be resolvable by the
 * provisioned BoxLang runtime; the verb fails with an actionable message
 * rather than a stack trace when it isn't.
 */
public abstract class BxSitesDocBoxExtension {

    @Inject
    public BxSitesDocBoxExtension(Project project) {
        getEnabled().convention(false);
    }

    /** Off by default - the {@code bxSitesDocBoxDoc} task is a no-op unless this is set. */
    public abstract Property<Boolean> getEnabled();

    /**
     * Mapping name to source directory, e.g. {@code models} to
     * {@code models}. Empty by default, in which case the verb documents
     * whichever conventional BoxLang source folders the project actually
     * has.
     */
    public abstract MapProperty<String, String> getMappings();

    /** Title recorded in the generated overview page. Defaults to the site's own name plus " API". */
    public abstract Property<String> getProjectTitle();

    /** A regex of paths for DocBox to skip. */
    public abstract Property<String> getExcludes();

    /** Where generated pages go, relative to the resolved content dir. */
    public abstract Property<String> getPagePathPrefix();

    /** Frontmatter tags applied to every generated page. */
    public abstract ListProperty<String> getTags();

    /** Where to keep DocBox's own JSON output; discarded after the run when unset. */
    public abstract Property<String> getJsonDir();

    /**
     * This block as {@code docbox} verb flags.
     *
     * <p>Only options actually set are passed, so an unset one falls
     * through to whatever {@code bxsites.yaml} says - the config file stays
     * the single source of truth, and this block only overrides it.
     */
    public List<String> toVerbArguments() {
        List<String> args = new ArrayList<>();

        for (Map.Entry<String, String> mapping : getMappings().get().entrySet()) {
            args.add("--mappings:" + mapping.getKey() + "=" + mapping.getValue());
        }

        addIfPresent(args, "--projectTitle", getProjectTitle());
        addIfPresent(args, "--excludes", getExcludes());
        addIfPresent(args, "--pagePathPrefix", getPagePathPrefix());
        addIfPresent(args, "--jsonDir", getJsonDir());

        if (getTags().isPresent() && !getTags().get().isEmpty()) {
            args.add("--tags=" + String.join(",", getTags().get()));
        }

        return args;
    }

    static void addIfPresent(List<String> args, String flag, Property<String> property) {
        if (property.isPresent() && !property.get().isBlank()) {
            args.add(flag + "=" + property.get());
        }
    }
}
