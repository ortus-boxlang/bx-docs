package ortus.boxlang.bxsites.maven;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * {@code mvn bxsites:docbox} - generates a BoxLang/CFML API reference into
 * the project's content directory from DocBox's own JSON output. The
 * BoxLang counterpart of {@code bxsites:javadoc}, and the Maven mirror of
 * the Gradle plugin's {@code bxSitesDocBoxDoc}.
 *
 * <p>Every parameter here becomes a verb flag; anything left unset falls
 * through to whatever {@code bxsites.yaml} declares, so the config file
 * stays the single source of truth and this goal only overrides it.
 *
 * <p>Requires the {@code bx-docbox} module to be resolvable by the
 * provisioned BoxLang runtime; the verb fails with an actionable message
 * rather than a stack trace when it isn't.
 */
@Mojo(name = "docbox", threadSafe = true)
public class DocBoxMojo extends AbstractBxSitesMojo {

    /** Mapping name to source directory, e.g. {@code models} to {@code models}. */
    @Parameter
    protected Map<String, String> mappings = new LinkedHashMap<>();

    @Parameter(property = "bxsites.docbox.projectTitle")
    protected String projectTitle;

    @Parameter(property = "bxsites.docbox.excludes")
    protected String excludes;

    @Parameter(property = "bxsites.docbox.pagePathPrefix")
    protected String pagePathPrefix;

    @Parameter(property = "bxsites.docbox.tags")
    protected List<String> tags = new ArrayList<>();

    @Parameter(property = "bxsites.docbox.jsonDir")
    protected String jsonDir;

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DOCBOX;
    }

    @Override
    protected List<String> verbArguments() {
        List<String> args = new ArrayList<>(super.verbArguments());

        for (Map.Entry<String, String> mapping : mappings.entrySet()) {
            args.add("--mappings:" + mapping.getKey() + "=" + mapping.getValue());
        }

        addIfSet(args, "--projectTitle", projectTitle);
        addIfSet(args, "--excludes", excludes);
        addIfSet(args, "--pagePathPrefix", pagePathPrefix);
        addIfSet(args, "--jsonDir", jsonDir);

        if (tags != null && !tags.isEmpty()) {
            args.add("--tags=" + String.join(",", tags));
        }

        return args;
    }

    static void addIfSet(List<String> args, String flag, String value) {
        if (value != null && !value.isBlank()) {
            args.add(flag + "=" + value);
        }
    }
}
