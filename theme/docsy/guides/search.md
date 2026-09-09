---
title: Search
order: 2
icon: phosphor-duotone:magnifying-glass
tags: [guides, search]
---

# Search

BxSites ships one search provider by default and can be pointed at others
via `bxsites.yaml`'s [`searchProvider`](../configuration.md#searchprovider) -
`search: true`/`false` stays the master on/off switch regardless of which
provider is active.

## Local (the default)

BxSites' search is fully static and client-side - the same approach
[mkdocs](https://www.mkdocs.org/) uses by default: an index built once at
`build` time, and [MiniSearch](https://lucaong.github.io/minisearch/) doing
the actual searching in the visitor's browser. There's no server, database,
or external search service involved.

MiniSearch adds prefix matching (finds "installation" while you're still
typing "instal") and typo-tolerant fuzzy matching (finds "search" for a
query like "serch") on top of the same per-field boosting every provider
below uses (title weighted highest, then tags, then headings, then body) -
both are on by default, no extra configuration needed.

## How it works

1. At `build` time, `SearchIndexer` walks every non-hidden page and writes
   `site/search-index.json`: one entry per page with its `title`, `url`,
   frontmatter `tags`, the text of every heading on the page, and a
   truncated plain-text copy of its body (HTML tags stripped).
2. Each theme's `search.bxm` partial renders a search box; `layout.bxm`
   only includes it (and the MiniSearch bundle + shared `search.js` scripts) when
   `bxsites.yaml`'s `search` is `true` and `searchProvider.provider` is
   `"local"` (the default - see [Other providers](#other-search-providers)
   below for what changes with a different one).
3. In the browser, the shared `assets/search.js` widget fetches
   `search-index.json` once, builds a `MiniSearch` index from it (`title`
   weighted highest, then frontmatter `tags`, then `headings`, then plain
   body text, with prefix and fuzzy matching both turned on), and
   re-searches it on every keystroke - no network round-trip per query.

## Keyboard shortcuts

- **`/`** focuses the sidebar search box from anywhere on the page (unless
  you're already typing in another field) - the same convention
  [mkdocs-material](https://squidfunk.github.io/mkdocs-material/) uses. The
  search box shows a small `Ctrl K`/`⌘K` hint (platform-detected) so the
  shortcut below is discoverable.
- **Cmd/Ctrl+K** opens a separate command-palette-style overlay instead - a
  centered modal over a backdrop, built entirely in JS (no theme template
  changes needed) and shared across every built-in theme. Arrow Up/Down
  move a highlight across results, **Enter** navigates to the highlighted
  one, and **Escape** (or clicking the backdrop) closes it - the same
  "Quick Find"/⌘K convention Algolia DocSearch, Pagefind, VitePress,
  and Docusaurus all share.
- **`Escape`** also closes the sidebar box's own results dropdown and
  blurs it, independent of the palette above.

The palette reuses the exact same already-built `MiniSearch` index the sidebar
widget itself builds, rather than fetching `search-index.json` a second
time - only ever available for `local` (the default provider); `algolia`
gets its own Cmd+K for free from DocSearch itself (`keyboardShortcuts`
defaults to `true`), and `pagefind` gets Cmd+K wired up by `layout.bxm`
to focus its own `PagefindUI`, since that library doesn't bind it on its
own - neither opens this module's own palette.

## Turning it off

=== "YAML"
    ```yaml title="bxsites.yaml"
    search: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "search": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    search = false
    ```

Skips building `search-index.json` entirely, and skips the search box, the
vendored MiniSearch script, and the shared `search.js` widget in every
rendered page - a project with search off ships nothing search-related at
all. This
is the master switch - it applies no matter which `searchProvider` is
configured.

## Rebuilding just the index

```bash frame="terminal" title="Terminal"
bxSites search-index
```

Useful if you only need to refresh `search-index.json` - `build` already
does this as one of its own steps, so you don't need to run this
separately after a normal build. Only runs for providers that use the local
index (`"local"`, and any provider bx-sites doesn't otherwise know about) -
it's a no-op (`skipped: true`) when `searchProvider.provider` is `"algolia"`
or `"pagefind"`, since neither ever uses it.

## Algolia

Set `searchProvider.provider` to `"algolia"` to swap the search box for
[Algolia DocSearch](https://docsearch.algolia.com/) - the same
crawler-hosted search mkdocs-material, VitePress, Starlight and Docusaurus
all support:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: algolia
      algolia:
        appId: ABC123
        apiKey: a1b2c3d4e5f6...
        indexName: my-docs
        insights: false
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "algolia",
    		"algolia": {
    			"appId": "ABC123",
    			"apiKey": "a1b2c3d4e5f6...",
    			"indexName": "my-docs",
    			"insights": false
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "algolia"

    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    insights = false
    ```

`appId`, `apiKey` and `indexName` are required - `apiKey` is the
**search-only** public API key DocSearch gives you (never an admin key; it's
shipped straight into every rendered page). `insights` (`false` by default)
turns on DocSearch's own click/conversion analytics.

With `algolia` active:

- No `search-index.json` is built, and the shared `MiniSearch`/`search.js`
  widget isn't shipped - Algolia serves results from its own hosted index,
  populated by [DocSearch's crawler](https://docsearch.algolia.com/docs/what-is-docsearch/)
  or your own [Algolia Crawler](https://www.algolia.com/products/search-and-discovery/crawler/)
  config, not by anything BxSites writes at build time. You still need to
  register the site with DocSearch (or run your own crawler) separately -
  BxSites only wires up the client widget.
- Each built-in theme instead renders an empty `#bxsites-search-algolia`
  container, and `layout.bxm` loads `@docsearch/css`/`@docsearch/js` from
  jsDelivr and calls `docsearch({...})` against it - DocSearch renders its
  own search button and modal into that container.

## Pagefind

Set `searchProvider.provider` to `"pagefind"` to swap the search box for
[Pagefind](https://pagefind.app/) - another fully static/no-server search
engine, but indexed from the *built* `site/` HTML rather than crawled like
Algolia:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: pagefind
      pagefind: { bin: pagefind, options: [] }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "pagefind",
    		"pagefind": { "bin": "pagefind", "options": [] }
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "pagefind"

    [searchProvider.pagefind]
    bin = "pagefind"
    options = []
    ```

Both `pagefind` keys are optional - `bin` (default `"pagefind"`) is the
executable name/path, resolved against `PATH` when it's a bare name;
`options` is an array of extra raw CLI flags passed straight through (e.g.
`["--exclude-selectors", ".no-index"]`).

With `pagefind` active:

- **The `pagefind` CLI must already be installed and on `PATH`** - BxSites
  shells out to it (there's no BoxLang-native binding, the same reason
  `lastUpdated`/`gh-deploy` shell out to `git`), it doesn't install it for
  you. See [Pagefind's installation docs](https://pagefind.app/docs/installation/).
  Unlike `lastUpdated`, a missing/failing binary fails the `build` loudly
  (`BxSites.PagefindFailed`) rather than degrading silently - shipping a
  site whose configured search provider doesn't work is worse than a
  failed build.
- Right after every doc tree (main + versions + locales) is written and
  `sitemap.xml`/`llms.txt` are generated, BxSites runs
  `pagefind --site <siteDir> [...options]` against the *entire* built
  `site/` - so a multi-version/multi-locale site gets everything indexed
  in one pass, unlike bx-sites' own per-tree `search-index.json`. Pagefind
  writes its own bundle straight into `site/pagefind/` - self-hosted, no
  CDN involved.
- No `search-index.json` is built, and the shared `MiniSearch`/`search.js`
  widget isn't shipped (same as `algolia`) - and `bxSites search-index` is
  a no-op for the same reason (see above).
- Each built-in theme renders an empty `#bxsites-search-pagefind`
  container, and `layout.bxm` loads `site/pagefind/pagefind-ui.{css,js}`
  and calls `new PagefindUI({...})` against it - Pagefind renders its own
  inline search box and results into that container.

## Choosing a provider

| | `local` (default) | `algolia` | `pagefind` |
|---|---|---|---|
| Server/account needed | No | Yes (Algolia account) | No |
| Indexed from | `search-index.json` (title/tags/headings/truncated body) | Algolia's own hosted index (crawler/API) | The built `site/` HTML directly |
| Fuzzy/prefix matching | Yes (MiniSearch) | Yes (Algolia's own engine) | Yes (Pagefind's own engine) |
| Extra install | None | None (client-only) | The `pagefind` CLI on `PATH` at build time |
| Best for | Most projects - zero setup, zero ongoing dependency | Large sites wanting hosted analytics/typo-tolerance tuning | Large multi-version/multi-locale sites wanting full-page indexing without a hosted account |

Both `algolia` and `pagefind` are real, built-in alternatives - see their
own sections above for exactly what changes when you pick one. Everything
below is for the fourth option: a search service this module doesn't ship
built-in support for.

## Building your own provider

`searchProvider.provider` isn't limited to `"local"`/`"algolia"`/`"pagefind"` -
any other value is accepted by `bxsites.yaml` as-is (BxSites' own config
validation only checks the three providers above, and freely allows an
arbitrary sub-block alongside it, e.g. `searchProvider.meilisearch: {...}`,
for your own provider's own settings). There's no plugin hook for the UI
itself - the built-in themes simply render nothing for an unrecognized
provider name - so wiring up a fourth search service (Meilisearch,
Typesense, a proprietary internal search API, ...) is a project-level
[theme override](themes.md#overriding-a-theme). Worked example, wiring up
[Meilisearch](https://www.meilisearch.com/):

1. **Configure it** - any shape you want, since `bxsites.yaml` doesn't
   validate it:

   ```yaml title="bxsites.yaml" linenums="1"
   search: true
   searchProvider:
     provider: meilisearch
     meilisearch:
       host: https://my-project.meilisearch.io
       apiKey: a1b2c3d4e5f6...
       indexName: my-docs
   ```

2. **Eject a theme to customize** - `bxSites theme:new --theme=bootstrap`
   copies the built-in `bootstrap` theme into your project's own `theme/`
   folder (see [Overriding a theme](themes.md#overriding-a-theme));
   project-theme-wins resolution means bx-sites now renders through your
   copy instead of the built-in one.

3. **Add the mount point** - `theme/search.bxm` already branches on
   `variables.searchProviderName` for `local`/`algolia`/`pagefind`; add
   your own branch the same way:

   ```html title="theme/search.bxm"
   <bx:if variables.searchProviderName eq 'meilisearch'>
       <div id="bxsites-search-meilisearch"></div>
   </bx:if>
   ```

4. **Load the client and wire it up** - `theme/layout.bxm` already has a
   `<bx:if variables.searchEnabled and variables.searchProviderName eq 'algolia'>`
   block loading DocSearch's CSS/JS and calling `docsearch({...})`; add the
   equivalent for Meilisearch's own
   [instant-meilisearch](https://github.com/meilisearch/meilisearch-docsearch)
   widget, reading your own config block back out of `siteConfig`:

   ```html title="theme/layout.bxm"
   <bx:if variables.searchEnabled and variables.searchProviderName eq 'meilisearch'>
       <script src="https://cdn.jsdelivr.net/npm/@meilisearch/instant-meilisearch/dist/instant-meilisearch.umd.min.js"></script>
       <script>
           document.addEventListener( "DOMContentLoaded", function () {
               // instantMeiliSearch()/instantsearch() setup, reading:
               //   host      : "#encodeForJavaScript( variables.siteConfig.searchProvider.meilisearch.host ?: '' )#"
               //   apiKey    : "#encodeForJavaScript( variables.siteConfig.searchProvider.meilisearch.apiKey ?: '' )#"
               //   indexName : "#encodeForJavaScript( variables.siteConfig.searchProvider.meilisearch.indexName ?: '' )#"
               // mounted against #bxsites-search-meilisearch, the same shape
               // the built-in algolia branch already uses just above this one.
           } );
       </script>
   </bx:if>
   ```

5. **Index the built site, if it isn't crawler-hosted** - Algolia's own
   crawler populates its index out of band, so nothing else is needed
   there; Meilisearch (like Pagefind) needs *something* to push documents
   into it after `build` writes `site/`. Rather than shelling out from a
   theme template, use this module's own plugin system: a
   `models/BxSitesPlugin.bx` with an `onBuildComplete( siteDir, config )`
   hook (see [Plugins](plugins.md)) runs once, after every doc tree is
   written, with the finished `site/` directory and the resolved config
   both in hand - reuse `site/search-index.json` (still built for
   `meilisearch` too, since `SearchProviderRegistry.usesLocalIndex()`
   defaults to `true` for any provider it doesn't otherwise know about)
   as the payload to push, exactly like `PagefindIndexer.bx` shells out to
   the `pagefind` CLI against `site/` in the built-in provider.

This is the same shape `layout.bxm`/`search.bxm`/`BuildPipeline.bx` already
use for `algolia`/`pagefind` internally - a fourth provider is just one
more branch alongside them, entirely in project-owned files.
