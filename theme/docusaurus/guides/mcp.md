---
title: MCP Server
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Turn on mcp to write a full-text, untruncated site/mcp-index.json (plus site/mcp-manifest.json and a per-tree mcp-nav.json) on every build, so bxSites Cloud can expose your published site through a public, read-only MCP server for AI agents - independent of search/searchProvider.
tags: [guides, mcp, ai]
---

# MCP Server

Turn `mcp: true` on in `bxsites.yaml` and every `build` writes three files
alongside your rendered pages - a full-text, machine-readable copy of your
site's content, nav, and tree layout that
[bxSites Cloud](https://bxsites.io/cloud) reads to expose a public,
read-only [MCP](https://modelcontextprotocol.io/) server for your
published site, the same idea as
[GitBook's "MCP servers for published docs"](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Once published, AI agents and assistants (Claude, ChatGPT, and any other
MCP-aware client) can search, browse, and retrieve your site's content
directly, instead of scraping rendered HTML:

- `site/mcp-index.json` - one full-text entry per page, per tree
- `site/mcp-nav.json` - that tree's own nav structure, per tree
- `site/mcp-manifest.json` - one site-wide index of every tree (main site,
  versions, locales) and where to find each one's own files above

BxSites itself only ever produces the files described on this page -
actually serving them as an MCP server over the network is bxSites
Cloud's job, not something this module does on its own.

!!! note "Requires a paid bxSites Cloud plan"
    Building `mcp-index.json` itself is free and works with `bxSites build`
    alone, no Cloud account required. Actually exposing it as a live MCP
    server for a published site is a bxSites Cloud feature available on its
    paid plans only - it isn't included on the free plan. See
    [bxsites.io/cloud](https://bxsites.io/cloud) for current plan details.

## Turning it on

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    mcp = true
    ```

`false` (the default) skips the whole step - none of `mcp-index.json`,
`mcp-nav.json`, or `mcp-manifest.json` are written, and `build` pays no
extra cost beyond checking the flag.

## Independent of `search`

`mcp` has nothing to do with [`search`/`searchProvider`](search.md) - the
two are unrelated switches:

- `mcp-index.json` is generated regardless of whether `search` is `true`
  or `false`, and regardless of `searchProvider.provider` (`"local"`,
  `"algolia"`, `"pagefind"`, or a custom one).
- Turning `mcp` on never changes anything about your site's own visitor-
  facing search box, and turning `search` off never disables `mcp-index.json`.

This is why `mcp-index.json` can't simply reuse `search-index.json`: that
file is only ever built for the `"local"` search provider (an
Algolia- or Pagefind-backed site produces no `search-index.json` at all,
since each of those keeps its own index elsewhere), and its `body` field
is deliberately truncated - `search-index.json` ships to every visitor's
browser for the in-page search box, so keeping it small matters. Neither
constraint applies to `mcp-index.json`: it's fetched server-side by
bxSites Cloud, not shipped to visitors, and a truncated body risks an AI
agent giving an incomplete or wrong answer.

## The `mcp-index.json` format

One entry per non-hidden page (the same "hidden pages are excluded"
convention `search-index.json`/the nav both use), covering both regular
docs pages and [blog](blog.md) posts:

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ...",
    "type": "page",
    "categories": [],
    "updatedAt": "2026-08-18T10:15:00.000Z"
  },
  {
    "title": "Announcing bxSites 2.0",
    "url": "blog/announcing-bxsites-2/index.html",
    "tags": ["release"],
    "headings": ["Announcing bxSites 2.0"],
    "body": "Announcing bxSites 2.0 Today we're shipping...",
    "type": "post",
    "categories": ["Releases"],
    "updatedAt": "2026-08-15"
  }
]
```

| Field | Description |
|---|---|
| `title` | The page's title (frontmatter `title`, same as `search-index.json`) |
| `url` | The page's URL path, relative to the site root |
| `tags` | The page's frontmatter `tags` array |
| `headings` | The plain text of every `h1`-`h6` on the page, in document order |
| `body` | The **complete** plain-text page content, HTML tags stripped - never truncated |
| `type` | `"page"` for a regular docs page, `"post"` for a [blog](blog.md) post |
| `categories` | A post's own frontmatter `categories` array; always `[]` for a docs page (docs pages don't have categories) |
| `updatedAt` | A post's own frontmatter `date`; a docs page's own frontmatter `date` when it sets one, else its source file's last-modified time as an ISO-8601 instant. `""` when neither is available. |

Aside from `type`/`categories`/`updatedAt`, this is the same entry shape
`search-index.json` uses, with one difference: there, `body` is cut to 400
characters with a trailing ellipsis; here, it's the full page.

## The `mcp-manifest.json` format

Written once per site build (not once per tree), `site/mcp-manifest.json`
lists every tree that got its own `mcp-index.json`/`mcp-nav.json`, so
bxSites Cloud's MCP server can offer version-and-locale-aware tools
without guessing bx-sites' own directory conventions:

```json title="site/mcp-manifest.json"
[
  { "path": "", "label": "1.0.x", "version": "1.0.x", "locale": "en", "default": true },
  { "path": "next", "label": "Next", "version": null, "locale": "en", "default": false },
  { "path": "versions/0.9", "label": "0.9", "version": "0.9", "locale": "en", "default": false },
  { "path": "es", "label": "Español", "version": "1.0.x", "locale": "es", "default": false }
]
```

| Field | Description |
|---|---|
| `path` | This tree's own root-relative path - `""` for the main tree at the site root, `"next"`/`"versions/<name>"`/`"<localeCode>"`/`"versions/<name>/<localeCode>"` otherwise. Join with `/mcp-index.json` (or `/mcp-nav.json`) to get that tree's own file, e.g. `"versions/0.9/mcp-index.json"` - and just `"mcp-index.json"` for the root's own (`path` is `""` there). |
| `label` | A human-readable label for this tree - the version switcher's own label (`versions.default`'s name, a plain version's own name, or `"Latest"`/`"Next"`), combined with the locale's own label for a locale sub-tree |
| `version` | The `docs/versions/<name>/` name this tree renders, or `null` when it isn't a named version (the unversioned main tree, or `/next/`) |
| `locale` | This tree's own locale code - `i18n.defaultLocale.code` for a tree with no locale suffix, otherwise that locale's own code |
| `default` | `true` for the one tree that renders at the site root - the one a visitor/AI agent lands on with no version or locale specified |

## The `mcp-nav.json` format

Written alongside every tree's own `mcp-index.json` (site root, `/next/`,
each `/versions/<name>/` tree, each locale sub-tree), `mcp-nav.json` is
that tree's own nav - the exact same nested `{ title, url, order, icon,
children }` structure the theme itself renders the sidebar from - so
bxSites Cloud's MCP server can offer a `get_nav`/table-of-contents tool
without inventing a second nav shape of its own:

```json title="site/mcp-nav.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "order": 1,
    "icon": "phosphor-duotone:rocket-launch",
    "children": []
  },
  {
    "title": "Guides",
    "url": "",
    "order": 2,
    "icon": "",
    "children": [
      { "title": "Search", "url": "guides/search/index.html", "order": 1, "icon": "", "children": [] }
    ]
  }
]
```

A folder-group node with no `index.md` of its own (like `"Guides"` above)
has an empty `url` - it's not a page, just a heading for its `children`.

## Multi-version and multi-locale sites

Just like `search-index.json`, `mcp-index.json`/`mcp-nav.json` are written
once per rendered tree - the main site, `/next/` (when `versions.default`
is set), each `/versions/<name>/` tree, and each locale sub-tree - so
every published tree gets its own pair of files covering just that tree's
own pages and nav. `mcp-manifest.json`, on the other hand, is written
once per build, at the site root only, listing every one of those trees.
