---
title: Themes
order: 1
icon: phosphor-duotone:palette
tags: [guides, themes]
---

# Themes

Themes are native BoxLang `.bxm` templates - there's no separate template
engine or build step involved.

## Built in

| Theme | Base | Notes |
|---|---|---|
| `bootstrap` (default) | [Bootstrap 5](https://getbootstrap.com/), vendored | Poppins font, brand gradient navbar |
| `material` | Hand-rolled Material-style CSS | Card layout, elevation shadows, Roboto font |
| `tailwind` | [Tailwind Play CDN](https://tailwindcss.com/) | Utility-class driven, no build step |
| `docsy` | Hand-rolled CSS, forked from `material` | Read the Docs/Docsy-inspired navy-blue reference-manual look |
| `slate` | Hand-rolled CSS, forked from `material` | Stripe/Slate-inspired - a permanently dark sidebar regardless of light/dark mode |
| `docusaurus` | Hand-rolled CSS, forked from `material` | Docusaurus-inspired bold full-width colored navbar, rounded cards |
| `justthedocs` | Hand-rolled CSS, forked from `material` | Just the Docs-inspired minimalism - search box lives at the top of the sidebar |
| `vuepress` | Hand-rolled CSS, forked from `material` | VuePress-inspired green accent, soft rounded corners |
| `gitbook` | Hand-rolled CSS, forked from `material` | GitBook-inspired centered reading column, serif headings |
| `notion` | Hand-rolled CSS, forked from `material` | Notion-inspired borderless sidebar, near-grayscale UI, generous whitespace |

The seven `material`-forked themes above reuse `material`'s exact BoxLang
templates (layout.bxm/page.bxm/search.bxm) unchanged except for a scoped
CSS-class-prefix rename - only `assets/style.css` (and, for `justthedocs`,
one relocated `<bx:include>` line moving the search box into the sidebar)
differs, so they inherit the same full feature set and the same
air-gapped-capable behavior `material` already has.

Every built-in theme's own CSS/JS (Bootstrap's CSS/JS bundle, highlight.js,
Alpine.js, MiniSearch for the default `local` search provider, and Mermaid
when `mermaid` is turned on) ships vendored with this module and is
copied straight into every built `site/` - no CDN, no internet access
needed to view a built site. The `tailwind` theme's own utility engine (a
client-side JIT compiler, not a static stylesheet) and other optional
features you turn on yourself (`math`, Algolia search, Google Analytics)
still load from a CDN or a hosted API - see
[Air-gapped/offline sites](#air-gapped-offline-sites) below.

`bootstrap`, `material` and `tailwind` apply the same BoxLang brand
palette (a `#00FF78 -> #00DBFF` gradient and a `#FFF500` accent); the
seven gallery themes below them each use their own distinct palette
instead, inspired by whichever platform they borrow their look from - see
the table above. Every one of the ten ships the same set of page features
regardless of palette:

- **An in-page "On this page" table of contents**, generated from each
  page's own `h2`/`h3` headings.
- **Breadcrumbs**, showing a page's ancestor chain when it's nested more
  than one level deep under a linked ancestor.
- **Prev/next page links** at the bottom of the article, following the
  nav's own reading order.
- **Syntax-highlighted code blocks**, via [highlight.js](https://highlightjs.org/)
  plus a BoxLang grammar of its own (` ```bx `/` ```boxlang `/` ```cfscript `),
  each with a **copy button** - shown on hover on devices that support it,
  always visible on touch devices (there's no hover to reveal it there). See
  [Markdown Extensions](markdown.md#code-blocks).
- **Self-hosted webfonts** - no `fonts.googleapis.com` request at view time.
- **A dark/light mode toggle**, powered by [Alpine.js](https://alpinejs.dev/)
  for reactivity. The visitor's choice is remembered in `localStorage`
  (falling back to their OS preference), and applied before first paint to
  avoid a flash of the wrong theme.
- **A responsive header** that stays a single row at every width - a
  narrow viewport shrinks the search box rather than wrapping it onto its
  own line - plus a collapsible sidebar nav (a hamburger toggle in
  `bootstrap`/`material`/`tailwind` alike).
- **Keyboard shortcuts** in the search box: `/` focuses search from
  anywhere on the page, and `Escape` closes the results. See
  [Search](search.md).
- **A repo link and "Edit this page"/"Last updated" line**, when
  `bxsites.yaml`'s `repo`/`lastUpdated` options are set. See
  [Configuration](../configuration.md#repo).
- **A "Download Markdown" link**, next to "Edit this page" - every page's
  raw `.md` source is published alongside its built HTML (`guides/themes.md`
  sitting next to `guides/themes/index.html`), so it (or an LLM) can read
  the page as plain Markdown directly instead of parsing rendered HTML.
  Always on, no config needed. See [Getting Started](../getting-started.md#downloading-a-page-as-markdown).
- **An opt-in footer** (copyright, `social` links, a "Built with BxSites"
  credit) when `bxsites.yaml`'s `footer` is `true`. See
  [Configuration](../configuration.md#footer).
- **A version switcher**, appearing automatically once a project has a
  `docs/versions/` folder with more than one version in it. See
  [Configuration](../configuration.md#versioning).
- **A themed `404.html`**, served automatically by most static hosts
  (including GitHub Pages) for any unmatched path. Add a `404.md` at the
  root of `docs/` (or `src/`) to override its title and body with your
  own - it's never built as a regular page (no nav entry, no
  `sitemap.xml` URL), just rendered into `site/404.html` in its place.
- **A custom logo and favicon**, when `bxsites.yaml`'s `theme.logo`/
  `theme.favicon` are set. See [Configuration](../configuration.md#theme).
- **A collapsible sidebar nav**, opt-in via `theme.options.navCollapsible` -
  every section with children (linked or not) gets a toggle button instead
  of always showing its children inline, and the section containing the
  current page always starts open. See
  [Configuration](../configuration.md#theme).
- **Google Analytics**, when `bxsites.yaml`'s `analytics` is configured. See
  [Configuration](../configuration.md#analytics).
- **Social share cards** (Open Graph + Twitter Card meta tags), sourced
  from each page's `description` frontmatter (or the site-wide
  `description`) and its own `ogImage` (or the site-wide one) - optionally
  auto-generated per page via `bxsites.yaml`'s `generateOgImages`. See
  [Configuration](../configuration.md#ogimage).
- **Page tags, an icon and a summary line**, all opt-in via a page's own
  frontmatter - tags render as badges linking into a site-wide `/tags/`
  index. See [Getting Started](../getting-started.md#add-pages).
- **An explicit nav override**, in `bxsites.yaml` or its own `docs/nav.json`,
  replacing folder-inference for large sites. See
  [Configuration](../configuration.md#nav).
- **Extra CSS/JS**, injected via `bxsites.yaml`'s `extraCss`/`extraJs`. See
  [Configuration](../configuration.md#extracss--extrajs).
- **Admonition (note/warning/tip/...) callout boxes**, on by default in any
  page's markdown, including collapsible variants - no config needed. See
  [Markdown Extensions](markdown.md#admonitions).
- **Footnotes and definition lists**, opt-in via `bxsites.yaml`'s `markdown`.
  See [Markdown Extensions](markdown.md#footnotes).
- **Content tabs**, **code line numbers/highlighted lines/titles**, and
  **diff markers/terminal-style frames** for code blocks, no config needed.
  See [Markdown Extensions](markdown.md#content-tabs).
- **Responsive images** - resized + WebP variants and a `<picture>`
  rewrite for every eligible `docs/assets/**` image, on by default. See
  [Responsive Images](images.md).
- **Mermaid diagrams**, opt-in via `bxsites.yaml`'s `mermaid`. See
  [Markdown Extensions](markdown.md#diagrams).
- **Math** (KaTeX), opt-in via `bxsites.yaml`'s `math`. See
  [Markdown Extensions](markdown.md#math).

Set which one a project uses in `bxsites.yaml`:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: material }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "material" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "material"
    ```

## Installing a published theme

A theme published to ForgeBox installs with nothing but the `bxSites`
binary itself - no `box`/CommandBox needed. Browse what's already published
under the [`bxsites-themes`](https://www.forgebox.io/type/bxsites-themes)
category on ForgeBox:

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

This downloads the package's zip and extracts it into
`themes/bx-sites-theme-blog1/` at the project root, validating it
satisfies the `ThemeProvider` contract below before finishing. A project
can carry several installed themes side by side this way and switch
between them purely by name:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: bx-sites-theme-blog1 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "bx-sites-theme-blog1" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "bx-sites-theme-blog1"
    ```

A theme needs no BoxLang module/class-loader involvement at all (unlike a
plugin) - it's pure files, so there's no separate activation step the way
`install:plugin` has; setting `theme.name` is the only wiring needed. See
[`install:theme`](../cli-reference.md#installtheme) in the CLI reference.

Starting from a theme built for a different static site generator
instead? See [Importing a theme](theme-import.md) - `theme:import`
mechanically converts an mkdocs/jekyll/hugo theme's own template files
into a best-effort `themes/<name>/` scaffold.

## Air-gapped/offline sites

A built site works with no internet access at all by default, for
`bootstrap`, `material`, and the seven `material`-forked themes (`docsy`,
`slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook`, `notion`) with
the default `local` search provider: Bootstrap's own CSS/JS, highlight.js,
Alpine.js, and MiniSearch are all vendored with this module
(`resources/assets/vendor/`) and copied straight into
`site/assets/vendor/` at build time - no CDN `<script>`/`<link>` tag
anywhere in the generated HTML for any of those. Turning on
`bxsites.yaml`'s `mermaid` key vendors Mermaid the same way - its
`mermaid.min.js` bundle is copied into `site/assets/vendor/mermaid/` and
every built-in theme loads it from there, so diagrams still render with
zero outbound requests.

A few things still reach out to the network, only when you turn them on
yourself:

- The `tailwind` theme's own utility engine is a client-side JIT compiler
  loaded from `cdn.tailwindcss.com` - it isn't a static stylesheet this
  module can vendor the same way, so this theme isn't air-gapped-capable
  yet.
- Mermaid's own layout engine lazy-loads one extra chunk, `elk-api.js`,
  from jsDelivr - but only for diagram types that opt into the `elk`
  layout algorithm; the vendored `mermaid.min.js` renders every other
  diagram type entirely on its own.
- `bxsites.yaml`'s `math` option loads KaTeX (both its JS and its own font
  files) from a CDN when turned on.
- `searchProvider.provider: "algolia"` and `analytics.provider: "google"`
  inherently talk to a hosted API/tracking endpoint - vendoring the JS
  file wouldn't remove that dependency.

If your deployment target genuinely has zero internet access, stick to
`bootstrap`/`material`/one of the seven `material`-forked themes, the
default `local` search provider, avoid `elk`-layout Mermaid diagrams if
`mermaid` is on, and leave `math`/Algolia/analytics off.

See [Icons](icons.md) for how a page's own `icon` frontmatter (or a
`nav.json` entry's own `icon`) resolves to an emoji, a named icon from one
of eight bundled libraries, or a project's own custom SVG.

## The `ThemeProvider` contract

A theme is just a folder with:

- **`layout.bxm`** (required) - the outer HTML shell + nav. Receives
  `variables.page`, `variables.nav`, `variables.siteConfig`,
  `variables.themeDir` and `variables.basePath` in scope, and includes the
  sibling `page.bxm` via `#variables.themeDir#/page.bxm`.
  `variables.basePath` is always a root-relative path ending in `/` (`/` by
  default, `/my-docs/` when `bxsites.yaml`'s `baseURL` overrides it) - prefix
  every internal `href`/`src` with it, rather than hardcoding a leading `/`,
  so the theme still works when the site is served from a sub-path.
- **`page.bxm`** (required) - the article body. Renders
  `variables.page.contentHtml` - the already-converted markdown.
- **`search.bxm`** (optional) - the search box markup, included by
  `layout.bxm` only when `bxsites.yaml`'s `search` is `true`. See
  [Search](search.md).
- **`assets/`** (optional) - theme CSS/JS, copied to `site/assets/theme/`
  at build time.

`variables.page.editUrl`/`.lastUpdated` (empty strings when unconfigured)
and `variables.siteConfig.repo`/`.social`/`.footer` are always available too,
backing the repo link/edit link/last-updated/footer features above - a
custom theme decides for itself whether and how to render them, same as
everything else. `variables.versions` (`[ { label, url } ]`, "Latest"
first) and `variables.currentVersion` (the `label` being rendered right
now) back the version switcher - empty/`"Latest"` for a project that isn't
versioned, so a theme only needs to render a switcher when
`variables.versions.len() gt 1`. Every built-in theme gets its repo/social icons from
a small shared SVG lookup, `<bx:include template="#variables.moduleAssetsDir#/icons.bxm">`
(defines `bxsitesIcon( name )`, one of `github`, `twitter`/`x`, `rss`,
`youtube`, `linkedin`, `facebook`, `bluesky`, `threads`, `slack`,
`patreon`, `email`, `edit`, `clock`, falling back to a generic link glyph)
- a custom
theme can include it the same way, or supply its own icons entirely.

A theme folder missing either required file fails fast with a clear
`BxSites.InvalidTheme` error at build time, rather than a confusing template
error deep inside rendering.

## Customizing colors without a theme override

For a color/font tweak, forking a whole theme is overkill - each built-in
theme reads its palette from a handful of CSS custom properties on `:root`,
re-declared under `[data-theme="dark"]` for dark mode. `bxsites.yaml`'s
[`extraCss`](../configuration.md#extracss--extrajs) loads *after* the
theme's own stylesheet, so a same-specificity re-declaration in it wins
without touching `resources/themes/` at all:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/brand.css ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/brand.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/brand.css" ]
    ```

```css title="docs/assets/brand.css" linenums="1"
/* docs/assets/brand.css - copied to site/assets/brand.css at build time */
:root {
	--bxsites-gradient-start: #7C3AED;
	--bxsites-gradient-end: #DB2777;
	--bxsites-accent: #FBBF24;
	--bxsites-link: #7C3AED;
	--bxsites-link-hover: #9F5AF0;
}

[data-theme="dark"] {
	--bxsites-link: #C4B5FD;
	--bxsites-link-hover: #DDD6FE;
}
```

The `bootstrap` theme's own set (`resources/themes/bootstrap/assets/style.css`)
is `--bxsites-gradient-start`/`-end`, `--bxsites-accent`, `--bxsites-bg`,
`--bxsites-text`, `--bxsites-sidebar-bg`, `--bxsites-sidebar-text`,
`--bxsites-border`, `--bxsites-link`, `--bxsites-link-hover`,
`--bxsites-code-bg`, `--bxsites-step-marker-bg`, `--bxsites-step-marker-text`,
`--bxsites-step-line`, `--bxsites-step-success-bg`/`-text`,
`--bxsites-step-warning-bg`/`-text` and `--bxsites-step-danger-bg`/`-text`.
Every built-in theme guarantees `--bxsites-gradient-start`/`-end`,
`--bxsites-accent` and the `--bxsites-step-*` set under those exact names,
so `extraCss` can always retarget the brand color/stepper accents
regardless of theme - but only `bootstrap`, `slate` and `notion` also
expose `--bxsites-bg`/`-text`/`-sidebar-bg`/`-sidebar-text`/`-border`/`-link`/`-link-hover`/`-code-bg`
under those names (`justthedocs` aliases all but the two `-sidebar-*` ones
the same way). Every other built-in theme (`material`, `tailwind`,
`docsy`, `docusaurus`, `vuepress`, `gitbook`) uses its own internal
custom-property names for that second group instead (e.g. material's own
`assets/style.css` uses `--md-bg`/`--md-ink`/`--md-link`/...) - open that
theme's own `assets/style.css` to find its real names before overriding
one of those via `extraCss`. Anything beyond color/font (layout,
adding/removing chrome) needs a real override or a custom theme - see below.

The rest back the [`::: stepper`/`::: step`](content-blocks.md#stepper) directive
block - `--bxsites-step-marker-bg`/`-text` are the default numbered circle's
background/text color (`bootstrap`/`material` default it to the theme's
own `--bxsites-accent`; `tailwind` uses a dedicated teal/mint pair since it
doesn't have a single shared accent token), `--bxsites-step-line` is the
connecting line between steps, and the `-success`/`-warning`/`-danger`
pairs back a step's own optional `color="..."` attribute - unlike the
default marker, these three are the same fixed bg/text pair in both
light and dark mode (a self-contained badge, not tied to the theme's own
brand accent), so there's no `[data-theme="dark"]` override to redeclare:

```css title="docs/assets/brand.css" linenums="1"
:root {
	--bxsites-step-marker-bg: #7C3AED;
	--bxsites-step-marker-text: #fff;
	--bxsites-step-success-bg: #059669;
	--bxsites-step-success-text: #fff;
}

[data-theme="dark"] {
	--bxsites-step-marker-bg: #C4B5FD;
	--bxsites-step-marker-text: #1b1f21;
}
```

## Homepage hero banner

Every built-in theme ships CSS for a full-width homepage banner with a
headline image and call-to-action buttons - this very site's own
`docs/index.md` uses it. There's no directive block or config for it, just
plain HTML any page can drop in (a homepage is just a normal page, `order: 1`
or otherwise first in nav):

```markdown title="docs/index.md"
<div class="bxsites-hero">
	<img class="bxsites-hero__banner" src="assets/home-banner.jpg" alt="...">
	<div class="bxsites-hero__actions">
		<a class="bxsites-hero__btn bxsites-hero__btn--primary" href="getting-started.md">Get Started</a>
		<a class="bxsites-hero__btn bxsites-hero__btn--secondary" href="https://github.com/your/repo">View on GitHub</a>
	</div>
</div>
```

`bxsites-hero__btn--primary`/`--secondary` are the same two accent styles
every theme already uses elsewhere - swap, drop, or add buttons freely, and
resize/replace `bxsites-hero__banner`'s own image via a `docs/assets/`-relative
`src` the same way any other image resolves.

## Overriding a theme

Drop your own `layout.bxm` + `page.bxm` (and optionally `search.bxm` /
`assets/`) into a `theme/` folder at your project root. BxSites prefers a
project-level `theme/` override over both an installed `themes/<name>/`
theme and any built-in theme, as long as it satisfies the contract below -
the built-in themes under this module's own `resources/themes/` are a good
starting point to copy and adapt. Full resolution order: `theme/` (this
section) -> `themes/theme.name/` (an [installed theme](#installing-a-published-theme),
if `theme.name` matches one) -> a built-in theme named `theme.name`.

A worked example - start from `bootstrap` and swap its brand palette and
heading font for your own, keeping everything else (nav, search, dark mode,
code highlighting, ...) exactly as it already works:

```text title="Project structure"
my-project/
├── bxsites.yaml
├── docs/
└── theme/                    ← project-level override, checked before any built-in theme
    ├── layout.bxm             ← copied from resources/themes/bootstrap/layout.bxm
    ├── page.bxm                ← copied from resources/themes/bootstrap/page.bxm, unchanged
    ├── search.bxm               ← copied unchanged
    └── assets/
        └── style.css              ← copied from bootstrap's assets/style.css, then edited
```

1. Copy the three `.bxm` files and `assets/style.css` out of this module's
   `resources/themes/bootstrap/` into your project's `theme/`.
2. Edit only what you need to change. To swap the brand palette and font,
   that's just the top of `theme/assets/style.css`:

   ```css title="theme/assets/style.css" linenums="1"
   :root {
   	--bxsites-gradient-start: #7C3AED;  /* was #00FF78 */
   	--bxsites-gradient-end: #DB2777;    /* was #00DBFF */
   	--bxsites-accent: #FBBF24;          /* was #FFF500 */
   }

   body {
   	font-family: "Inter", system-ui, sans-serif;  /* was "Poppins" */
   }
   ```

3. Run `bxSites build` (or `serve` while iterating) - BxSites
   picks up `theme/` automatically, no `bxsites.yaml` change needed (a
   project-level `theme/` folder always takes precedence over the built-in
   theme named in `theme.name`). Everything you didn't touch - nav
   rendering, search, the dark-mode toggle, code annotations - keeps
   working exactly as it did in the original `bootstrap` theme, since it's
   still the exact same `layout.bxm`/`page.bxm` markup underneath.

A project `theme/` folder is all-or-nothing, though - once BxSites finds
one, it's used instead of the built-in theme entirely, so it still needs
its own `layout.bxm` + `page.bxm` even if all you changed is
`assets/style.css` (a folder missing either fails fast with
`BxSites.InvalidTheme` rather than silently falling back). For a
CSS-only/no-`.bxm` tweak, use [`extraCss`](#customizing-colors-without-a-theme-override)
above instead - it layers on top of whichever theme `bxsites.yaml` names,
no `theme/` folder involved at all. `theme/` is for when you also need to
change the markup itself, covered next.

## Writing a theme from scratch

A theme only needs the two required files, so here's a genuinely minimal
one - no Bootstrap/Tailwind, no dark mode, no search UI - to show exactly
what's required versus what the built-in themes add on top. Save both as
`theme/layout.bxm` and `theme/page.bxm` in your project - a project-level
`theme/` folder is picked up automatically (as above), no `bxsites.yaml`
change needed:

```bx title="theme/layout.bxm" linenums="1"
<!-- theme/layout.bxm -->
<bx:script>
	function renderNav( required array nodes ) {
		var html = "<ul>"
		for ( var node in arguments.nodes ) {
			html &= "<li>"
			html &= len( node.url )
				? '<a href="' & variables.basePath & node.url & '">' & encodeForHTML( node.title ) & '</a>'
				: encodeForHTML( node.title )
			if ( node.children.len() ) {
				html &= renderNav( node.children )
			}
			html &= "</li>"
		}
		return html & "</ul>"
	}
</bx:script>
<bx:output>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>#encodeForHTML( variables.page.title )# - #encodeForHTML( variables.siteConfig.name )#</title>
	<link rel="stylesheet" href="#variables.basePath##variables.siteConfig.theme.styleUrl ?: 'assets/theme/style.css'#">
</head>
<body>
	<header><a href="#variables.basePath#">#encodeForHTML( variables.siteConfig.name )#</a></header>
	<nav>#renderNav( variables.nav )#</nav>
	<main>
</bx:output>
<bx:include template="#variables.themeDir#/page.bxm">
<bx:output>
	</main>
</body>
</html>
</bx:output>
```

```bx title="theme/page.bxm" linenums="1"
<!-- theme/page.bxm -->
<bx:output>
<article>
	<h1>#encodeForHTML( variables.page.title )#</h1>
	#variables.page.contentHtml#
</article>
</bx:output>
```

That's a complete, working theme - `variables.page.contentHtml` is the
already-converted markdown (syntax highlighting, admonitions, tabs, math
and all), so there's nothing left to parse, only to lay out. From here,
add whatever the built-in themes have that you actually want:
`search.bxm` (only included when `bxsites.yaml`'s `search` is `true` - see
[Search](search.md)), a dark-mode toggle (copy the `x-data`/`x-init`
Alpine.js pair off `resources/themes/bootstrap/layout.bxm`'s `<body>` tag
and the matching `[data-theme="dark"]` CSS block), breadcrumbs/tags/prev-next
links (`page.bxm` in any built-in theme shows the pattern - each is just an
`if` around a small render function, all driven by fields already present
on `variables.page`), or an `assets/` folder for your own CSS/JS, copied to
`site/assets/theme/` automatically at build time.

The `variables.siteConfig.theme.styleUrl ?: 'assets/theme/style.css'`
pattern used above (rather than a hardcoded `assets/theme/style.css`) is
what makes your theme's own `assets/style.css` pick up [content-hash
fingerprinting](../configuration.md#assets) - `assets.fingerprint` (on by
default) means every edit you make to that CSS gets a new filename on the
next build, so visitors' browsers never serve a stale cached copy. Any
`.bxm` (built-in or custom) that hardcodes the plain path instead won't
break, it just won't bust caches automatically when the CSS changes.
