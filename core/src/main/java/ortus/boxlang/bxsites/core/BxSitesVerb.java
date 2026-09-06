package ortus.boxlang.bxsites.core;

/**
 * Registry of every bx-sites CLI verb this tooling knows how to invoke.
 *
 * <p>This is the single source of truth both the Gradle plugin and the Maven
 * plugin build their own tasks/goals from, so the two never drift on verb
 * coverage. Each entry mirrors exactly one {@code models/cli/*.bx} dispatcher
 * class in the bx-sites module itself.
 *
 * <p>{@code hasOutput} distinguishes a verb whose success can be verified by
 * checking for a real artifact on disk (used by {@link BxSitesInvoker} since
 * the bx-sites CLI's own exit code is not reliable on failure) from one that
 * has no such fixed output to check (e.g. {@code serve}, which runs in the
 * foreground until interrupted).
 */
public enum BxSitesVerb {

    NEW("new", false),
    BUILD("build", true),
    SERVE("serve", false),
    SEARCH_INDEX("search-index", true),
    CLEAN("clean", false),
    MIGRATE("migrate", true),
    STATS("stats", false),
    DOCTOR("doctor", false),
    LINT("lint", false),
    DEPLOY("deploy", false),
    PUBLISH("publish", false),
    PACKAGE("package", true),
    POST_NEW("post:new", true),
    VERSION_NEW("version:new", true),
    I18N_STATUS("i18n:status", false),
    I18N_NEW("i18n:new", true),
    PAGE_NEW("page:new", true),
    PLUGIN_NEW("plugin:new", true),
    INSTALL_PLUGIN("install:plugin", true),
    THEME_NEW("theme:new", true),
    INSTALL_THEME("install:theme", true),
    SKILLS_INSTALL("skills:install", false),
    THEME_IMPORT("theme:import", true),
    PAGE_RENAME("page:rename", true),
    BLOG_DRAFTS("blog:drafts", false),
    BLOG_FIND("blog:find", false),
    SEARCH_QUERY("search:query", false);

    private final String verbId;
    private final boolean hasOutput;

    BxSitesVerb(String verbId, boolean hasOutput) {
        this.verbId = verbId;
        this.hasOutput = hasOutput;
    }

    /**
     * The literal token passed to {@code BoxRunner} as the verb argument
     * (e.g. {@code "new"}, {@code "post:new"}).
     */
    public String verbId() {
        return verbId;
    }

    /**
     * Whether this verb's success can be verified by checking for a real
     * output artifact on disk, in addition to scanning captured output for
     * an {@code Error:} line.
     */
    public boolean hasOutput() {
        return hasOutput;
    }
}
