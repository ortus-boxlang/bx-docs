package ortus.boxlang.bxsites.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * {@code mvn bxsites:coldbox} - documents a ColdBox application's routes,
 * handlers, models, modules, interceptors and scheduled tasks from its
 * conventions on disk. The Maven mirror of the Gradle plugin's
 * {@code bxSitesColdBoxDoc}.
 *
 * <p>The ColdBox counterpart of {@code bxsites:controller-scan}, with one
 * difference in method: that goal reflects over compiled classes, while
 * this reads source, so it needs neither a compile step nor an application
 * that boots.
 */
@Mojo(name = "coldbox", threadSafe = true)
public class ColdBoxMojo extends AbstractBxSitesMojo {

    /** The ColdBox application root, relative to the project root. Defaults to the project root itself. */
    @Parameter(property = "bxsites.coldbox.appRoot")
    protected String appRoot;

    @Parameter(property = "bxsites.coldbox.pagePathPrefix")
    protected String pagePathPrefix;

    @Parameter(property = "bxsites.coldbox.tags")
    protected List<String> tags = new ArrayList<>();

    /**
     * Which page sets to generate: any of {@code routes}, {@code handlers},
     * {@code models}, {@code modules}, {@code interceptors},
     * {@code scheduler}. All of them when unset.
     */
    @Parameter(property = "bxsites.coldbox.include")
    protected List<String> include = new ArrayList<>();

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.COLDBOX;
    }

    @Override
    protected List<String> verbArguments() {
        List<String> args = new ArrayList<>(super.verbArguments());

        DocBoxMojo.addIfSet(args, "--appRoot", appRoot);
        DocBoxMojo.addIfSet(args, "--pagePathPrefix", pagePathPrefix);

        if (tags != null && !tags.isEmpty()) {
            args.add("--tags=" + String.join(",", tags));
        }

        if (include != null && !include.isEmpty()) {
            args.add("--include=" + String.join(",", include));
        }

        return args;
    }
}
