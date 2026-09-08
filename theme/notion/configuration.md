---
title: Configuration
order: 4
icon: phosphor-duotone:gear-six
summary: Every site config key, what it defaults to, and what it does.
tags: [reference, configuration]
---

# Configuration

Every project has one site config at its root - `bxsites.yaml` (or `.yml`),
the default/preferred format, `bxsites.toml`, or `bxsites.json`. All are fully
supported and produce the exact same result; `bxSites new` scaffolds
`bxsites.yaml` unless `--format=toml` or `--format=json` is passed (see
[Getting Started](getting-started.md#config-file-format)). If a project
somehow has more than one, `bxsites.yaml` wins, then `bxsites.yml`, then
`bxsites.toml`, then `bxsites.json`.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    name: "My Docs"
    description: ""
    baseURL: "/"
    theme:
      name: bootstrap
      options: {}
      logo: ""
      favicon: ""
    search: true
    mcp: false
    searchProvider:
      provider: local
      algolia: { appId: "", apiKey: "", indexName: "", insights: false }
    nav: []
    markdown:
      enableAdmonition: true
    repo:
      url: ""
      editUri: ""
    social: []
    footer: false
    lastUpdated: false
    mermaid: false
    math: false
    analytics:
      provider: ""
      id: ""
    ogImage: ""
    generateOgImages: false
    extraCss: []
    extraJs: []
    assets:
      fingerprint: true
      bundle: true
      images: { enabled: true, widths: [400, 800, 1200, 1600], formats: [original, webp] }
    plugins: []
    i18n:
      defaultLocale: { code: en, label: English }
      locales: []
    blog:
      postsPerPage: 10
      feed: true
    variables: {}
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"name": "My Docs",

  === "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true
    [searchProvider]
    provider = "algolia"
    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    ```
    	"description": "",
    	"baseURL": "/",
    	"theme": {
    		"name": "bootstrap",
    		"options": {},
    		"logo": "",
    		"favicon": ""
    	},
    	"search": true,
    	"mcp": false,
    	"searchProvider": {
    		"provider": "local",
    		"algolia": { "appId": "", "apiKey": "", "indexName": "", "insights": false }
    	},
    	"nav": [],
    	"markdown": { "enableAdmonition": true },
    	"repo": {
    		"url": "",
    		"editUri": ""
    	},
    	"social": [],
    	"footer": false,
    	"lastUpdated": false,
    	"mermaid": false,
    	"math": false,
    	"analytics": {
    		"provider": "",
    		"id": ""
    	},
    	"ogImage": "",
    	"generateOgImages": false,
    	"extraCss": [],
    	"extraJs": [],
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": { "enabled": true, "widths": [400, 800, 1200, 1600], "formats": ["original", "webp"] }
    	},
    	"plugins": [],
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": []
    	},
    	"blog": {
    		"postsPerPage": 10,
    		"feed": true
    	},
    	"variables": {}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    name = "My Docs"
    description = ""
    baseURL = "/"
    search = true
    mcp = false
    nav = []
    social = []
    footer = false
    lastUpdated = false
    extraCss = []
    extraJs = []
    plugins = []
    variables = {}

    [theme]
    name = "bootstrap"
    options = {}
    logo = ""
    favicon = ""

    [markdown]
    enableAdmonition = true

    [repo]
    url = ""
    editUri = ""
    ```

Only `name` is required - everything else falls back to the defaults shown
above. A partial `theme` object is merged one level deep, so
`{theme: {name: material}}` alone still keeps the default (empty)
`options`. Every key below is named/shaped identically in both formats -
switch the tabs on any example below to see it either way.

## `name`

The site name, shown in the header/brand mark and page titles. Required.

## `description`

An optional site description, used as the fallback `<meta name="description">`
and `og:description` for any page that has neither its own `description`
frontmatter nor a `summary` nor any plain text of its own to derive an
excerpt from - see [Getting Started](getting-started.md#add-pages) for the
full per-page fallback chain.

## `baseURL`

Controls how every internal link, asset path and nav entry is prefixed, and
doubles as the site's canonical URL for `sitemap.xml`, `robots.txt`,
`llms.txt`, and every page's own `<link rel="canonical">` tag.

- Left blank or `"/"` (the default) - links stay root-relative (`/page/`),
  and neither `sitemap.xml`, a `Sitemap:` line in `robots.txt`, an
  absolute-URL `llms.txt`, nor a `<link rel="canonical">` tag is generated
  (there's no canonical domain to build them from).
- A bare path, e.g. `"my-docs"` or `"/my-docs/"` - the site is assumed to be
  served from that sub-path, and every internal link, nav entry and asset
  is prefixed with it (`/my-docs/page/`). Still no `sitemap.xml`/canonical
  tags, since there's still no absolute domain.
- A full URL, e.g. `"https://docs.example.com/"` - the path portion
  (`/` here) is used the same way a bare path would be, **and**
  `sitemap.xml` is written at build time with every non-hidden page's
  absolute URL under that domain, `robots.txt` gains a `Sitemap:` line
  pointing at it, and every page gets its own correct
  `<link rel="canonical">` (a version/locale tree's own page still points
  at *that tree's own* URL, not the main site's).

`llms.txt` (see [below](#llmstxt)) is always written; it just prefers an
absolute URL when `baseURL` provides one.

## `llms.txt`

Every build writes a `llms.txt` to the site root - a plain Markdown index of
every non-hidden page, following the emerging
[llms.txt](https://llmstxt.org) convention for helping LLM-based tools
navigate a site without crawling its rendered HTML. There's no config key
for this; it's generated automatically, using an absolute URL per link when
`baseURL` is a full URL, or a `basePath`-relative one otherwise.

## `sitemap.xml`

Written at the site root, but only when `baseURL` is a full URL (see
above) - a sitemap needs an absolute domain to be meaningful. Lists every
non-hidden page per the [sitemaps.org](https://www.sitemaps.org/) protocol.

## `robots.txt`

Every build writes a `robots.txt` to the site root - no config key needed
unless you want to change its default, permissive behavior:

=== "YAML"
    ```yaml title="bxsites.yaml"
    robots: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "robots": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    robots = false
    ```

- `true` (the default) - `Allow: /` for every crawler, plus a `Sitemap:`
  line pointing at `sitemap.xml` when `baseURL` is a full URL (see above).
- `false` - `Disallow: /` for every crawler instead, and no `Sitemap:`
  line - the common "don't index this staging/internal deploy at all" need.
  This is a *crawler* opt-out only, not access control - the site is still
  fully reachable by anyone with the URL; see
  [Deployment](guides/deployment.md#restricting-who-can-reach-your-site) if you actually need to restrict who
  can reach it at all.

Need more than the on/off toggle - specific disallowed paths, multiple
`Sitemap:` lines, a `Crawl-delay`, per-user-agent rules? Drop your own
`robots.txt` right next to `index.md` (`docs/robots.txt`, or `src/robots.txt`
for a `src/`-based project - see [`docs/` or `src/`](getting-started.md#add-pages))
and it's copied through byte-for-byte instead of the generated one, on every
build - the `robots` key above is ignored entirely once this file exists.

## `theme`

- `theme.name` - one of the built-in themes (`bootstrap`, `material`,
  `tailwind`), or the name of a custom theme you provide via a `theme/`
  folder at the project root (see [Themes](guides/themes.md))
- `theme.logo` - path/URL to an image shown next to the site name in the
  header brand mark (in place of the default "⚡" glyph) - a relative path
  (e.g. `"assets/logo.svg"`, resolved against `docs/assets/`) is prefixed
  with `baseURL` like any other internal asset; an absolute URL is used
  as-is. Left blank (the default), the header shows "⚡ &lt;site name&gt;".
- `theme.favicon` - path/URL to a favicon, resolved the same way as
  `theme.logo`. Left blank (the default), no `<link rel="icon">` is
  rendered at all (falling back to the browser's own default behavior).
- `theme.options` - theme-specific options, read by every built-in theme:
  - `theme.options.colorMode` - `"auto"` (the default), `"light"` or
    `"dark"`. Controls which mode a first-time visitor sees before they've
    picked one themselves via the header's dark/light toggle - `"auto"`
    follows their OS preference, `"light"`/`"dark"` pins a fixed default.
    Once a visitor toggles the switch, their own choice (stored in
    `localStorage`) always wins on later visits, regardless of this setting.

    === "YAML"
        ```yaml
        theme: { options: { colorMode: dark } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "colorMode": "dark" } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      colorMode = "dark"
      ```
  - `theme.options.navCollapsible` - `false` (the default) renders every nav
    section always expanded, as today. `true` gives every section with
    children a toggle button the visitor can click to collapse/expand it -
    whether that section is a bare group heading (a folder with no
    `index.md`) or links to its own page. The section containing the page
    you're currently on always starts open, regardless of `navExpandAll`,
    so navigating there never buries the very link you're on.
  - `theme.options.navExpandAll` - only relevant when `navCollapsible` is
    `true`. `true` (the default) starts every section open; `false` starts
    every section collapsed, except the one containing the current page.

    === "YAML"
        ```yaml
        theme: { options: { navCollapsible: true, navExpandAll: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "navCollapsible": true, "navExpandAll": false } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      navCollapsible = true
      navExpandAll = false
      ```
  - `theme.options.tocPosition` - where a page's own "On this page" table of
    contents renders. `"top"` (the default) renders it inline, at the top of
    the article, same as today. `"sticky"` moves it into its own right-hand
    column that stays in view while the article scrolls underneath it - the
    same "On this page" list, just pinned, which helps on long pages. The
    pinned column only fits on wide viewports (it's hidden below the point a
    3-column layout would get cramped); below that width `sticky` mode
    instead renders a collapsible "On this page" bar pinned to the top of
    the viewport while scrolling - tap to expand the list, a common
    treatment for narrow viewports - so the TOC stays reachable at every
    viewport width, it just changes shape depending on how much room there is.

    === "YAML"
        ```yaml
        theme: { options: { tocPosition: sticky } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "tocPosition": "sticky" } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      tocPosition = "sticky"
      ```
  - `theme.options.pageMetaPosition` - where the edit-this-page/download-
    markdown/last-updated row renders relative to a page's own content.
    `"bottom"` (the default) renders it as a small footer note right before
    the article ends. `"top"` renders it up near the title instead, the same
    place it always rendered before this option existed.

    === "YAML"
        ```yaml
        theme: { options: { pageMetaPosition: top } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "pageMetaPosition": "top" } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      pageMetaPosition = "top"
      ```
  - `theme.options.pageActionsPosition` - where the [page-actions
    dropdown](#pageactions) renders relative to a page's own content.
    `"top"` (the default) renders it right-aligned above the breadcrumbs/
    title. `"bottom"` renders it after the page, past the prev/next links.

    === "YAML"
        ```yaml
        theme: { options: { pageActionsPosition: bottom } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "pageActionsPosition": "bottom" } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      pageActionsPosition = "bottom"
      ```
  - `theme.options.breadcrumbs` - `true` (the default) renders the
    "Guides > Setup" trail above a page's own title, whenever that page is
    nested more than one level deep (a page at the top of `docs/` never
    has one - there's nothing to trail from). `false` turns breadcrumbs
    off site-wide, across every built-in theme.

    === "YAML"
        ```yaml
        theme: { options: { breadcrumbs: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "breadcrumbs": false } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      breadcrumbs = false
      ```
  - `theme.options.accentColor`/`theme.options.accentColorDark` - a hex
    color (3- or 6-digit, e.g. `#2563eb`) overriding the theme's own
    interactive/accent color (links, active nav items, focus states, ...)
    for light mode and dark mode respectively. This is a site-wide,
    build-time setting picked by the site owner - there's no visitor-facing
    UI to change it. Either or both can be set independently; leaving one
    unset keeps that mode on the theme's own native accent color.

    === "YAML"
        ```yaml
        theme: { options: { accentColor: "#2563eb", accentColorDark: "#60a5fa" } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "accentColor": "#2563eb", "accentColorDark": "#60a5fa" } } }
        ```

    === "TOML"
      ```toml
      [theme.options]
      accentColor = "#2563eb"
      accentColorDark = "#60a5fa"
      ```

## `search`

`true` (the default) builds a static search index and wires up the search
box; `false` skips both entirely - no `search-index.json`, no search UI, no
extra JS shipped. See [Search](guides/search.md).

## `searchProvider`

Which search UI `search: true` wires up:

- `provider` - `"local"` (the default) is bx-sites' own static/client-side
  search (`search-index.json` + [MiniSearch](https://lucaong.github.io/minisearch/), see [Search](guides/search.md#local-the-default)).
  `"algolia"` wires up [Algolia DocSearch](guides/search.md#algolia)
  instead, and `"pagefind"` wires up [Pagefind](guides/search.md#pagefind).
  Any other value is a project's own custom provider, wired up by a
  `theme/` override - see [Search](guides/search.md#other-search-providers).
- `algolia` - required when `provider` is `"algolia"`: `appId`, `apiKey`
  (the *search-only* public API key, not an admin key) and `indexName`,
  exactly as Algolia's own DocSearch client expects them. `insights`
  (`false` by default) turns on DocSearch's click/conversion analytics.

  === "YAML"
      ```yaml title="bxsites.yaml" linenums="1"
      search: true
      searchProvider:
        provider: algolia
        algolia:
          appId: ABC123
          apiKey: a1b2c3d4e5f6...
          indexName: my-docs
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
      			"indexName": "my-docs"
      		}
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

- `pagefind` - both keys optional when `provider` is `"pagefind"`: `bin`
  (default `"pagefind"`) is the CLI executable name/path, resolved against
  `PATH` when it's a bare name; `options` is an array of extra raw CLI
  flags passed straight through. The `pagefind` CLI itself must already be
  installed and on `PATH` - BxSites shells out to it (like `git` for
  `lastUpdated`/`gh-deploy`), it doesn't install it for you.

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

## `mcp`

`false` (the default) skips it entirely. `true` writes three files on
every `build` - a full-text, untruncated `mcp-index.json`, a per-tree
`mcp-nav.json`, and a site-wide `mcp-manifest.json` listing every tree -
for [bxSites Cloud](https://bxsites.io/cloud)'s public, read-only MCP
server for your published site. Entirely independent of `search`/
`searchProvider` above - it's produced no matter which search provider
(or none) is configured. Building the files themselves is free; bxSites
Cloud only actually serves them as a live MCP server on its paid plans,
not the free plan. See [MCP Server](guides/mcp.md).

## `nav`

By default, nav is inferred from `docs/`'s own folder/file structure (with
`order`/`hidden` frontmatter) - fine for small sites, but a large one can
outgrow it: an explicit nav lets you title, group and order pages however
you want, independent of where their files actually live.

An empty array (the default) means "infer from folder structure". A
non-empty array replaces that inference entirely - array order becomes nav
order, and a page not referenced anywhere in it is still built, just not
linked from the nav (same as `hidden: true`). Each entry is either:

- a bare docs/-relative path string, e.g. `"guides/setup.md"` - title comes
  from that page's own frontmatter/filename, same as folder-inference would
  give it
- an object `{ "title", "path", "icon", "children" }` - `path`, `icon` and
  `children` are all optional; a `title`-only entry with no `path` is an
  unlinked group heading (like a folder with no `index.md` today), and an
  explicit `title`/`icon` always overrides the linked page's own
  title/icon in the nav (the page's real `<h1>`/`<title>` is untouched -
  only the nav label/icon changes) - see [Icons](guides/icons.md)
  for what an `icon` value can be

A `title`-only entry with `children` and no `path` is exactly a menu
container/section label - a non-clickable heading that just groups its
children:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - index.md
      - title: Main Components
        children:
          - title: Quick Start
            path: guides/setup.md
          - guides/deployment.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		"index.md",
    		{
    			"title": "Main Components",
    			"children": [
    				{ "title": "Quick Start", "path": "guides/setup.md" },
    				"guides/deployment.md"
    			]
    		}
    	]
    }
    ```

  === "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [i18n.defaultLocale]
    code = "en"
    label = "English"
    [[i18n.locales]]
    code = "es"
    label = "Español"
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Main Components"
    children = [
      { title = "Quick Start", path = "guides/setup.md" },
      { path = "guides/deployment.md" }
    ]
    ```

Give that same group entry a `path` instead and it becomes a normal linked
section (its own landing page, plus children) rather than a bare label - both
shapes nest under `theme.options.navCollapsible` the same way (see above).

For a nav large enough that it clutters `bxsites.yaml`, move it to its own
`docs/nav.json` file instead - same array shape, just as the whole file's
top-level content:

```json title="docs/nav.json" linenums="1"
[
	"index.md",
	{ "title": "Guides", "children": [ "guides/setup.md" ] }
]
```

`bxsites.yaml`'s own `nav`, when non-empty, always wins over `docs/nav.json`.
Only the main tree honors either - a `docs/versions/<name>/` tree always
infers its nav from its own folder structure, even when the main tree has
an explicit one.

## `redirects`

`[]` (the default) - site-wide `from`/`to` old-URL redirects, only ever
applied to the main tree:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"
    ```

- `redirects[].from` - the old pretty-URL segment a static redirect stub
  gets written at (no leading/trailing slash, no extension)
- `redirects[].to` - a root-relative path (resolved against `baseURL`) or
  a full `https://` URL

A page's own frontmatter `redirect_from` is the per-page, per-tree
alternative (works inside a version/locale tree too) - see
[Redirects](guides/redirects.md) for the full picture, including how
`page:rename` stamps it on automatically.

## `markdown`

Forwarded as-is to [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)'s
own module settings before each page renders. BxSites doesn't redefine or
validate these keys; whatever you put here is bx-markdown's own option set,
straight through - so this list can drift from bx-markdown's own as it
evolves. Tables, `~~strikethrough~~`, `- [ ]` task-list checkboxes and the
in-page table of contents are always on, with no toggle. The one exception
is `enableAdmonition` - bx-markdown itself defaults it to `false`, but BX
Docs defaults it to `true` (see the [Markdown Extensions guide](guides/markdown.md)).

| Key | Default | Effect |
|---|---|---|
| `enableAdmonition` | `true` *(BxSites default; bx-markdown's own default is `false`)* | `!!!`/`???`/`???+` callout blocks - see the [Markdown Extensions guide](guides/markdown.md#admonitions) |
| `enableFootnotes` | `false` | `[^label]` footnote references - see the [Markdown Extensions guide](guides/markdown.md#footnotes) |
| `enableDefinitionLists` | `false` | `Term\n:   Definition` lists - see the [Markdown Extensions guide](guides/markdown.md#definition-lists) |
| `autoLinkUrls` | `true` | Auto-links bare URLs and email addresses |
| `anchorLinks` | `true` | Adds a clickable anchor link to every heading |
| `anchorSetId` | `true` | Stamps an `id` attribute onto every heading |
| `achorSetName` *(sic)* | `true` | Stamps a `name` attribute onto every heading |
| `anchorWrapText` | `false` | Wraps the whole heading text in the anchor link, instead of just a bare marker |
| `anchorClass` | `"anchor"` | CSS class on the anchor `<a>` |
| `anchorPrefix` / `anchorSuffix` | `""` | Raw HTML injected immediately before/after the heading text |
| `enableYouTubeTransformer` | `false` | Auto-embeds bare YouTube links as a player |
| `codeStyleHTMLOpen` / `codeStyleHTMLClose` | `"<code>"` / `"</code>"` | Wrapper HTML around inline code spans |
| `fencedCodeLanguageClassPrefix` | `"language-"` | Class prefix bx-sites's client-side syntax highlighter (and Mermaid, see below) key off of, e.g. ` ```js ` -> `class="language-js"` |
| `tableOptions.columnSpans` | `true` | Honors `colspan`-style merged table cells |
| `tableOptions.appendMissingColumns` | `true` | Pads a short row out to the header's column count |
| `tableOptions.discardExtraColumns` | `true` | Drops extra cells in an over-long row |
| `tableOptions.className` | `"table"` | CSS class on every rendered `<table>` |
| `tableOptions.headerSeparationColumnMatch` | `true` | Requires the `---` separator row to match the header's column count |

Every rendered table also gets a responsive-scroll/sticky-header wrapper automatically, with no config key of its own - see [Tables](guides/tables.md#responsive-scroll-and-a-sticky-header).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    markdown:
      enableFootnotes: true
      enableDefinitionLists: true
      anchorLinks: false
      enableYouTubeTransformer: true
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"markdown": {
    		"enableFootnotes": true,
    		"enableDefinitionLists": true,
    		"anchorLinks": false,
    		"enableYouTubeTransformer": true
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [markdown]
    enableFootnotes = true
    enableDefinitionLists = true
    anchorLinks = false
    enableYouTubeTransformer = true
    ```

## `repo`

Adds a repository icon link to the header (every built-in theme) and,
when both keys are set, an "Edit this page" link on every page.

- `repo.url` - your repo's URL, e.g. `"https://github.com/acme/docs"`.
  Renders the header icon link on its own; leave blank to omit it entirely.
- `repo.editUri` - the path segment between the repo URL and a page's own
  source path, e.g. `"edit/main/docs/"` (GitHub's own "edit" URL
  convention). Combined with `repo.url` and a page's `docs/`-relative
  source path to build its edit link - e.g. with the example above,
  `docs/guides/setup.md` gets
  `https://github.com/acme/docs/edit/main/docs/guides/setup.md`. Requires
  `repo.url` too; leave blank to omit edit links while still showing the
  header icon.

=== "YAML"
    ```yaml title="bxsites.yaml"
    repo: { url: "https://github.com/acme/docs", editUri: "edit/main/docs/" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "repo": { "url": "https://github.com/acme/docs", "editUri": "edit/main/docs/" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [repo]
    url = "https://github.com/acme/docs"
    editUri = "edit/main/docs/"
    ```

## `social`

An array of social/external links rendered in the footer (see
[`footer`](#footer) - has no effect unless it's also turned on). Each entry
needs a `url`; `icon` selects from a small built-in icon set (`github`,
`twitter`/`x`, `youtube`, `linkedin`, `facebook`, `bluesky`, `threads`,
`slack`, `patreon`, `rss`, `email`, falling back to a generic link glyph
for anything else), and `label` sets the link's accessible name/tooltip
(defaults to `icon`, then `"Link"`).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    social:
      - { url: "https://twitter.com/acme", icon: twitter, label: Twitter }
      - { url: "https://acme.com/rss.xml", icon: rss, label: RSS }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"social": [
    		{ "url": "https://twitter.com/acme", "icon": "twitter", "label": "Twitter" },
    		{ "url": "https://acme.com/rss.xml", "icon": "rss", "label": "RSS" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[social]]
    url = "https://twitter.com/acme"
    icon = "twitter"
    label = "Twitter"

    [[social]]
    url = "https://acme.com/rss.xml"
    icon = "rss"
    label = "RSS"
    ```

## `footer`

`false` (the default) - no footer at all. `true` adds one to every page:
a copyright line (`© <year> <site name>`), the `social` links (if any),
and a "Built with BxSites" credit.

=== "YAML"
    ```yaml title="bxsites.yaml"
    footer: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "footer": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    footer = true
    ```

## `lastUpdated`

`false` (the default) - no last-updated date. `true` adds a "Last updated"
line next to the edit link (or on its own, if `repo.editUri` isn't set),
sourced from `git log` on each page's own Markdown file at build time.
Silently omitted for a page git has no history for - a fresh `git init`
with no commits yet, a build running from a downloaded zip with no `.git`
at all, or git not being installed on the build machine - rather than
breaking the build.

=== "YAML"
    ```yaml title="bxsites.yaml"
    lastUpdated: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "lastUpdated": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    lastUpdated = true
    ```

## `analytics`

Wires up pageview analytics. Currently supports Google Analytics
(`gtag.js`) only:

- `analytics.provider` - `"google"` to enable it; left blank (the default),
  no analytics script is shipped at all.
- `analytics.id` - the Google Analytics measurement ID (e.g. `"G-ABC123"`).
  Required when `provider` is `"google"`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    analytics: { provider: google, id: "G-ABC123" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "analytics": { "provider": "google", "id": "G-ABC123" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [analytics]
    provider = "google"
    id = "G-ABC123"
    ```

## `ogImage`

Path/URL to a default social-card image, rendered as `og:image` (and paired
with a `twitter:card` of `summary_large_image`) on every page that doesn't
override it - resolved the same way as `theme.logo` (relative paths are
prefixed with `baseURL`, absolute URLs are used as-is). Left blank (the
default) and `generateOgImages` off, no `og:image`/`twitter:card` tags are
rendered.

=== "YAML"
    ```yaml title="bxsites.yaml"
    ogImage: assets/social-card.png
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "ogImage": "assets/social-card.png" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    ogImage = "assets/social-card.png"
    ```

A page's own frontmatter `ogImage` (see [Getting Started](getting-started.md#add-pages))
always wins over this site-wide default for that one page.

### `generateOgImages`

`false` (the default) - no per-page cards. `true` renders a real 1200x630
PNG social card for every page that doesn't already have its own
frontmatter `ogImage` - the page's title on the brand gradient, written to
`site/assets/og/<page>.png` - instead of every page sharing one generic
site-wide image. Pure `java.awt`/`javax.imageio` under the hood (part of
any JVM BoxLang runs on), so this needs no headless browser, external
service, or network access at build time.

=== "YAML"
    ```yaml title="bxsites.yaml"
    generateOgImages: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "generateOgImages": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    generateOgImages = true
    ```

## `extraCss` / `extraJs`

Arrays of extra stylesheet/script URLs to include on every page, appended
after the theme's own assets - each entry is resolved the same way as
`theme.logo` (a relative path is prefixed with `baseURL`; an absolute URL
is used as-is). `extraJs` entries are loaded with `defer`.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/custom.css ]
    extraJs: [ assets/custom.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/custom.css"],
    	"extraJs": ["assets/custom.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/custom.css" ]
    extraJs = [ "assets/custom.js" ]
    ```

When `assets.bundle` is on (the default), a local `extraCss`/`extraJs`
list like the one above is bundled into one fingerprinted file each,
instead of one `<link>`/`<script>` tag per entry - see [`assets`](#assets)
below.

## `assets`

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      fingerprint: true
      bundle: true
      images:
        enabled: true
        widths: [ 400, 800, 1200, 1600 ]
        formats: [ original, webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": {
    			"enabled": true,
    			"widths": [400, 800, 1200, 1600],
    			"formats": ["original", "webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [assets]
    fingerprint = true
    bundle = true

    [assets.images]
    enabled = true
    widths = [ 400, 800, 1200, 1600 ]
    formats = [ "original", "webp" ]
    ```

The asset pipeline - image resizing/WebP via
[bx-image](https://github.com/ortus-boxlang/bx-image) (a required
dependency, installed alongside bx-markdown/bx-esapi) and CSS/JS
bundling. Everything here is on by default with reasonable settings - a
fresh `bxSites new` project needs to touch none of this. See
[Responsive Images](guides/images.md) for the full picture, including
what deliberately isn't covered (AVIF, animated GIFs, SVGs).

- `assets.fingerprint` - `true` (the default). Content-hash-names every
  generated image variant, CSS/JS bundle, and the active theme's own
  `assets/style.css` (e.g. `screenshot-800w.a3f9c2e1.webp`,
  `bundle.a3f9c2e1.css`, `style.a3f9c2e1.css`) so they can be served with
  safe, far-future cache headers - a project's build changes the file's own
  name only when its content actually changes. This means a theme update -
  a built-in theme upgrade, or a custom author editing their own theme's
  CSS - automatically busts every visitor's browser cache on the next
  deploy, with no manual versioning step. Does not rename a project's own
  original files under `docs/assets/` - only pipeline-generated output gets
  fingerprinted, so anything else that references an asset by its plain
  filename (a `::: file` download card, a raw markdown link) keeps working
  unchanged.
- `assets.bundle` - `true` (the default). Concatenates `extraCss`/`extraJs`
  into one fingerprinted file each - pure BoxLang/JVM, no Node/esbuild
  toolchain. Falls back to today's exact per-URL `<link>`/`<script>`
  behavior, untouched, the moment any entry in the list is an external
  URL (a CDN link) or names a file that doesn't exist - see
  [Responsive Images](guides/images.md#css-js-bundling).
- `assets.images.enabled` - `true` (the default). Every eligible
  `docs/assets/**` image (`.png`/`.jpg`/`.jpeg`) gets resized/WebP
  variants generated via bx-image, and every matching `<img>` gets
  rewritten into a `<picture>` with `srcset`. Set `false` to fall back
  to plain, unprocessed image copying, exactly as before this feature
  existed.
- `assets.images.widths` - breakpoints to generate, in pixels. A width at
  or above a given image's own width is skipped automatically for that
  image - nothing is ever upscaled.
- `assets.images.formats` - `"original"` keeps the source format as the
  `<img>` fallback; `"webp"` adds a same-size `<source type="image/webp">`
  variant. Both on by default.

## `mermaid`

`false` (the default) - no [Mermaid](https://mermaid.js.org/) diagram
support shipped at all. `true` loads `mermaid.js` client-side and renders
every ` ```mermaid ` fenced code block as a diagram. See
[Markdown Extensions](guides/markdown.md#diagrams) for the syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    mermaid: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mermaid": true }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    mermaid = true
    ```

## `math`

`false` (the default) - no [KaTeX](https://katex.org/) shipped at all.
`true` loads it client-side and typesets `$...$`/`$$...$$` written directly
into a page's markdown. See
[Markdown Extensions](guides/markdown.md#math) for the syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    math: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "math": true }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    math = true
    ```

Admonitions (note/warning/tip-style callout boxes), content tabs, and
fenced-code `hl_lines`/`linenums`/`title` annotations are always available
in every page's markdown, no config needed - see
[Markdown Extensions](guides/markdown.md#admonitions).

## `openapi`

`false` (the default) - no [Swagger UI](https://swagger.io/tools/swagger-ui/)
shipped at all. `true` loads it client-side and renders every
`::: openapi src="..."` content block as an interactive widget for the
referenced OpenAPI/Swagger spec (JSON or YAML). See
[OpenAPI / Swagger](guides/openapi.md) for the syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    openapi: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "openapi": true }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    openapi = true
    ```

## `pageActions`

`false` (the default) - no page-actions dropdown at all. `true` adds a
"Copy" button to every page (near the title by default -
see `theme.options.pageActionsPosition` above) that opens a small menu:

- **Copy page** - copies the page's own raw Markdown source to the
  clipboard.
- **Copy link** - copies the page's absolute canonical URL to the
  clipboard, right after "Copy page" since both are clipboard actions.
- **View as Markdown** - opens that same raw `.md` source in a new tab.
- **Open in ChatGPT** / **Open in Claude** - opens a new chat on the
  respective site, prefilled with a prompt pointing at the page's URL.
- **Export as PDF** - triggers the browser's own print dialog
  (`window.print()`), scoped to just that page - see
  [Content Blocks: Page break](guides/content-blocks.md#page-break) for
  forcing a page break at a chosen spot first.
- **Report an issue** - opens a pre-filled "new issue" form on `repo.url`
  ([Configuration: `repo`](#repo)), naming the page it was opened from.
- **Share on X** / **Share on LinkedIn** / **Share on Facebook** - opens
  that platform's own share-intent page, prefilled with the page's URL
  (and title, for X).

Press **c** anywhere on the page to toggle the menu open/closed too - no
modifier key, same as the `/` search shortcut, and disabled while you're
already typing somewhere else.

"Copy link", both "Open in..." links, and all three "Share on..." links
only render when `baseURL` is a full URL (same requirement
`<link rel="canonical">` itself already has) - there's otherwise no real
page URL to hand off. "Report an issue" only renders when `repo.url` is
set. No extra JS is shipped at all unless this is turned on.

A thin divider separates the "Open in ChatGPT"/"Open in Claude" pair and
the "Share on..." group from the rest of the menu, so the AI and
social-sharing groups read as their own sections rather than
running together with everything else.

=== "YAML"
    ```yaml title="bxsites.yaml"
    pageActions: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "pageActions": true }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    pageActions = true
    ```

## `plugins`

`[]` (the default) - an array of BoxLang module names to activate as
plugins. Installing a plugin module (`box install`) never activates it on
its own; it has to be named here too. See [Plugins](guides/plugins.md)
for how to write one.

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    plugins = [ "myBxSitesPlugin" ]
    ```

## `i18n`

Metadata for the [`docs/i18n/<code>/`](guides/i18n.md) locale-folder
convention - a locale builds automatically once its folder exists;
`i18n` just supplies its display label/direction for the language
switcher.

- `i18n.defaultLocale` - `{ "code", "label", "flag", "strings" }` for the
  project's own regular `docs/` tree, defaulting to `{ "code": "en", "label": "English" }`.
  Only needs setting when your default locale isn't English.
- `i18n.locales` - `[]` (the default) - an array of `{ "code", "label", "dir", "flag", "strings" }`
  for every other locale. `code` doubles as the `docs/i18n/<code>/` folder
  name and the built URL prefix - letters/digits/hyphens only (`es`,
  `pt-BR`, `zh-Hans`). `dir` is `"ltr"` (the default) or `"rtl"`. `flag` is
  an optional emoji override for the language switcher's flag icon - most
  common codes already resolve to a sensible flag on their own. `strings`
  overrides that locale's own theme-chrome UI text (search placeholder,
  "On this page," the 404 page, ...) - see
  [Internationalization](guides/i18n.md#theme-chrome-ui-strings) for the
  full key list; `de`/`es`/`it`/`ja` already ship a built-in translation,
  so `strings` is only needed to override a key or add another locale.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    i18n:
      defaultLocale: { code: en, label: English }
      locales:
        - { code: es, label: Español }
        - { code: ar, label: العربية, dir: rtl }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": [
    			{ "code": "es", "label": "Español" },
    			{ "code": "ar", "label": "العربية", "dir": "rtl" }
    		]
    	}
    }
    ```

See [Internationalization](guides/i18n.md) for the full picture -
untranslated-page fallback, the language switcher, and what isn't
translated yet.

## `blog`

Options for the [blog](guides/blog.md) - itself a by-convention feature
(`docs/blog/posts/`), no key here required to turn it on.

- `blog.postsPerPage` - `10` (the default) - how many posts per page on
  `/blog/`, every category page, and every `/blog/archive/<year>/` page
  before it moves to `.../page/2/`.
- `blog.feed` - `true` (the default) - whether `/blog/feed.xml` (RSS 2.0)
  is written. Only meaningful with an absolute `baseURL`, same requirement
  as `sitemap.xml`.
- `blog.feedLimit` - `25` (the default) - caps `/blog/feed.xml` to this
  many most-recent posts. `0` means unlimited (every post, in full). Most
  feed readers only care about what's new, so an unbounded feed on a blog
  with hundreds of posts just wastes bandwidth on every poll - see
  [Blog: Feed](guides/blog.md#feed).

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

  === "TOML"
    ```toml title="bxsites.toml"
    [blog]
    postsPerPage = 10
    feed = true
    feedLimit = 25
    ```

See [Blog](guides/blog.md) for post/author frontmatter, categories,
featured images, and SEO/social metadata.

## `versions`

[Versioning](guides/versioning.md) itself is a by-convention feature
(`docs/versions/<name>/`), no key here required to turn it on -
`versions.default` is the one exception.

- `versions.default` - `""` (the default - the plain `docs/` tree builds
  at the site root, as always). Set it to a `docs/versions/<name>/`
  folder's own name and that version builds at the site root instead -
  `docs/` itself then builds separately at `/next/`. Throws
  `BxSites.UnknownDefaultVersion` if it doesn't match any discovered
  version folder.

=== "YAML"
    ```yaml title="bxsites.yaml"
    versions: { default: "1.0.x" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "versions": { "default": "1.0.x" } }
    ```

=== "TOML"
  ```toml title="bxsites.toml"
  [versions]
  default = "1.0.x"
  ```

See [Versioning](guides/versioning.md#publishing-a-default-version-at-the-site-root)
for what publishing a default version actually changes (the `/next/` tree,
the version switcher, `sitemap.xml`/`robots.txt` exclusion).

## `variables`

`{}` (the default) - an object of reusable values, any shape, referenced
from any Markdown page as `{{ dotted.path }}`. A page's own frontmatter is
also reachable this way, with no entry needed here - see
[Variables & Magic Functions](guides/variables-and-functions.md#page-variables).

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product: { name: "BoxLang", supportEmail: "support@example.com" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": { "name": "BoxLang", "supportEmail": "support@example.com" }
    	}
    }
    ```

  === "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [variables]
    company = "Ortus Solutions"
    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

```markdown title="docs/index.md"
Welcome to {{ company }}! We build {{ product.name }}.
```

A `docs/functions.bxs` file (no config key of its own - by convention,
same as `docs/nav.json`/`docs/blog/authors.yml`) adds BoxLang "magic
functions" alongside `variables` - callable the same way, as
`{{ $name(...) }}`. See
[Variables & Magic Functions](guides/variables-and-functions.md#magic-functions).

A `docs/data/*.yaml`/`.yml`/`.json` folder (also no config key of its own)
adds structured, nested/array-shaped data - reachable as `{{ data.<file>.<key> }}` -
for anything `variables`' own flat shape doesn't fit well (a team roster,
a pricing table). See [Data Files](guides/data-files.md).

## `cloud`

Where [`bxSites publish`](cli-reference.md#publish) ships the built site -
`cloud.siteId` (the target site's UUID in
[bxSites Cloud](guides/deployment.md#the-publish-command)) and
`cloud.apiUrl` (that instance's base URL, e.g. `https://cloud.bxsites.app`,
or a self-hosted URL). Both default to `""` - not configuring this block at
all is fine for every other verb; `publish` is the only one that needs it,
and fails with a clear error if either is missing.

**No API token field lives here, ever** - `publish` reads it from the
`BXSITES_CLOUD_TOKEN` environment variable (or an explicit `--token` flag),
never from `bxsites.yaml`, so this file always stays safe to commit.

=== "YAML"
    ```yaml title="bxsites.yaml"
    cloud:
      siteId: "3f2b1c9a-....-....-............"
      apiUrl: "https://cloud.bxsites.app"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"cloud": {
    		"siteId": "3f2b1c9a-....-....-............",
    		"apiUrl": "https://cloud.bxsites.app"
    	}
    }
    ```

  === "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [cloud]
    siteId = "3f2b1c9a-....-....-............"
    apiUrl = "https://cloud.bxsites.app"
    ```

See [Deployment](guides/deployment.md#the-publish-command) for the full
`publish` workflow.

## Versioning

Versioned docs are convention over configuration - there's no `bxsites.yaml`
key for it. Add a `docs/versions/<name>/` folder and it builds automatically
as its own doc tree, with a version switcher every theme renders for free
once more than one version exists. See [Versioning](guides/versioning.md)
for the full picture - cutting a new version with `version:new`, how
versions sort and build, and what's out of scope (per-tree search
scoping, no deprecated/EOL flag).
