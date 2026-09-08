package ortus.boxlang.bxsites.core;

/**
 * Registry of every bx-sites CLI verb this tooling knows how to invoke.
 *
 * <p>This is the single source of truth both the Gradle plugin and the Maven
 * plugin build their own tasks/goals from, so the two never drift on verb
 * coverage. Each entry mirrors exactly one {@code models/cli/*.bx} dispatcher
 * class in the bx-sites module itself.
 *
 * <p>Whether (and where) a given verb writes a real output artifact varies -
 * some write one unconditionally ({@code build}), some only when a config
 * option enables the feature they'd write into ({@code search-index}, which
 * is a legitimate no-op when search is disabled), and some never do
 * ({@code lint}/{@code stats}/{@code doctor}, stdout-report-only). Each
 * plugin's own task/Mojo decides whether and what to pass as an expected
 * output file to verify against - see {@link BxSitesInvoker#invoke}.
 */
public enum BxSitesVerb {

    NEW("new"),
    BUILD("build"),
    SERVE("serve"),
    SEARCH_INDEX("search-index"),
    CLEAN("clean"),
    MIGRATE("migrate"),
    STATS("stats"),
    DOCTOR("doctor"),
    LINT("lint"),
    DEPLOY("deploy"),
    PUBLISH("publish"),
    PACKAGE("package"),
    POST_NEW("post:new"),
    VERSION_NEW("version:new"),
    I18N_STATUS("i18n:status"),
    I18N_NEW("i18n:new"),
    PAGE_NEW("page:new"),
    PLUGIN_NEW("plugin:new"),
    INSTALL_PLUGIN("install:plugin"),
    THEME_NEW("theme:new"),
    INSTALL_THEME("install:theme"),
    SKILLS_INSTALL("skills:install"),
    THEME_IMPORT("theme:import"),
    PAGE_RENAME("page:rename"),
    BLOG_DRAFTS("blog:drafts"),
    BLOG_FIND("blog:find"),
    SEARCH_QUERY("search:query");

    private final String verbId;

    BxSitesVerb(String verbId) {
        this.verbId = verbId;
    }

    /**
     * The literal token passed to {@code BoxRunner} as the verb argument
     * (e.g. {@code "new"}, {@code "post:new"}).
     */
    public String verbId() {
        return verbId;
    }
}
