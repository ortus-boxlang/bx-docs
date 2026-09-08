package ortus.boxlang.bxsites.core.springboot;

import java.util.Map;

/**
 * Emits a small, self-contained Alpine.js-driven filter toolbar directly
 * into generated Markdown - raw HTML the underlying Markdown pipeline
 * passes through untouched. Confirmed by direct testing against a real
 * bx-sites build: a blank line immediately after an opening block-level
 * tag, and another immediately before its closing tag, lets any Markdown
 * nested inside still convert normally (CommonMark's own HTML-block
 * rules, not something specific to bx-sites) - every open/close pair
 * below follows that rule.
 *
 * <p>No bx-sites core changes needed for this to work in every theme:
 * every theme already loads Alpine.js identically and already defines
 * the same {@code --bxsites-*} CSS variable API, so this one inline
 * stylesheet (emitted once per page) looks right everywhere without
 * touching a single theme file.
 *
 * <p>Shared by {@link BxSitesJavadocDoclet} (kinds: constructor/method/field)
 * and {@link ControllerScanGenerator} (kinds: get/post/put/delete/patch) -
 * one small reusable piece rather than two near-duplicate ones.
 */
final class MemberFilterUi {

    private MemberFilterUi() {
    }

    static String styles() {
        return """
                <style>
                .bx-filter-toolbar { display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem; margin: 1.5rem 0; }
                .bx-filter-search { flex: 1 1 12rem; min-width: 8rem; padding: 0.4rem 0.75rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: var(--bxsites-bg); color: var(--bxsites-text); font-size: 0.9rem; }
                .bx-filter-chip { padding: 0.35rem 0.9rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: transparent; color: var(--bxsites-muted); font-size: 0.85rem; cursor: pointer; transition: all 0.15s ease; }
                .bx-filter-chip:hover { border-color: var(--bxsites-accent); color: var(--bxsites-text); }
                .bx-filter-chip.bx-filter-chip-on { background: var(--bxsites-accent); border-color: var(--bxsites-accent); color: var(--bxsites-bg); }
                </style>

                """;
    }

    /**
     * @param chips ordered kind -> chip label, e.g. {@code constructor -> "Constructors"}
     */
    static String toolbarOpen(Map<String, String> chips) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"bx-filter\" x-data=\"{ q: '', k: 'all' }\">\n\n");
        sb.append("<div class=\"bx-filter-toolbar\">\n");
        sb.append("<input type=\"search\" class=\"bx-filter-search\" x-model=\"q\" placeholder=\"Search...\">\n");
        sb.append("<button type=\"button\" class=\"bx-filter-chip\" :class=\"{ 'bx-filter-chip-on': k==='all' }\" @click=\"k='all'\">All</button>\n");
        for (Map.Entry<String, String> chip : chips.entrySet()) {
            sb.append("<button type=\"button\" class=\"bx-filter-chip\" :class=\"{ 'bx-filter-chip-on': k==='")
                    .append(chip.getKey()).append("' }\" @click=\"k='").append(chip.getKey()).append("'\">")
                    .append(chip.getValue()).append("</button>\n");
        }
        sb.append("</div>\n\n");
        return sb.toString();
    }

    static String toolbarClose() {
        return "</div>\n\n";
    }

    /**
     * Wraps a whole kind's section (heading + its items) so it hides/shows
     * as a unit - including when the kind matches but the search query
     * matches none of its items, so a heading never sits alone over an
     * empty section.
     */
    static String sectionOpen(String kind) {
        return "<div x-show=\"(k==='all'||k==='" + kind + "')&&(!q||"
                + "Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))\">\n\n";
    }

    static String sectionClose() {
        return "\n</div>\n\n";
    }

    /** Wraps one item; only needs to check the search query - kind visibility is already handled by the enclosing section. */
    static String itemOpen(String searchableName) {
        return "<div class=\"bx-filter-item\" data-n=\"" + escapeAttr(searchableName)
                + "\" x-show=\"!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())\">\n\n";
    }

    static String itemClose() {
        return "\n</div>\n\n";
    }

    /**
     * For raw markup contexts that can't be div-wrapped without breaking
     * their own structure (e.g. a table row) - filters by both kind and
     * search query on the one element itself, rather than relying on an
     * enclosing section for kind. Embed directly inside the element's
     * opening tag, e.g. {@code <tr " + rowAttributes(...) + ">"}.
     */
    static String rowAttributes(String kind, String searchableName) {
        return "data-k=\"" + kind + "\" data-n=\"" + escapeAttr(searchableName)
                + "\" x-show=\"(k==='all'||k===$el.dataset.k)&&(!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase()))\"";
    }

    static String escapeAttr(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;");
    }

    static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
