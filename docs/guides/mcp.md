---
title: MCP Server
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Turn on mcp to write a full-text, untruncated site/mcp-index.json on every build, so bxSites Cloud can expose your published site through a public, read-only MCP server for AI agents - independent of search/searchProvider.
tags: [guides, mcp, ai]
---

# MCP Server

Turn `mcp: true` on in `bxsites.yaml` and every `build` writes
`site/mcp-index.json` alongside your rendered pages - a full-text,
machine-readable copy of your site's content that
[bxSites Cloud](https://bxsites.io/cloud) reads to expose a public,
read-only [MCP](https://modelcontextprotocol.io/) server for your
published site, the same idea as
[GitBook's "MCP servers for published docs"](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Once published, AI agents and assistants (Claude, ChatGPT, and any other
MCP-aware client) can search and retrieve your site's content directly,
instead of scraping rendered HTML.

BxSites itself only ever produces the `mcp-index.json` file described on
this page - actually serving it as an MCP server over the network is
bxSites Cloud's job, not something this module does on its own.

## Turning it on

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

`false` (the default) skips the whole step - no `mcp-index.json` is
written, and `build` pays no extra cost beyond checking the flag.

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
convention `search-index.json`/the nav both use):

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ..."
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

The only difference from `search-index.json`'s own entry shape is `body`:
there, it's cut to 400 characters with a trailing ellipsis; here, it's the
full page.

## Multi-version and multi-locale sites

Just like `search-index.json`, `mcp-index.json` is written once per
rendered tree - the main site, `/next/` (when `versions.default` is set),
each `/versions/<name>/` tree, and each locale sub-tree - so every
published tree gets its own `mcp-index.json` covering just that tree's
own pages.
