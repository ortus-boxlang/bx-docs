---
title: Redirects
order: 11
icon: phosphor-duotone:signpost
tags: [guides, redirects]
---

# Redirects

Keep an old URL working after you move, rename, or restructure a page -
a static HTML stub gets written at the old path, so a search engine's
stale index entry or someone's old bookmark still lands on the right
page instead of 404ing. No server-side rewrite rule is involved (a
static host has nowhere to run one) - the stub is just enough HTML for a
browser to redirect itself and a crawler to learn the real canonical URL.

## Per-page: frontmatter `redirect_from`

Add one or more old paths to a page's own frontmatter:

```md title="docs/guides/new-setup.md"
---
title: New Setup
redirect_from:
  - guides/old-setup
  - setup
---
```

Each entry is a pretty-URL segment - no leading/trailing slash, no
`.md`/`.html` extension - the same shape the page's own URL takes. A
build then writes a stub at each one (`site/guides/old-setup/index.html`,
`site/setup/index.html` for the example above), both redirecting to this
page's own real URL.

`redirect_from` is scoped to whichever tree the page itself belongs to -
a version's own page redirects within that version
(`site/versions/2.0/old-path/`), a locale's own translated page redirects
within that locale (`site/es/old-path/`), exactly the same way the page's
own real URL already is. There's nothing extra to configure per tree.

## Site-wide: `bxsites.yaml` `redirects`

For an old URL that never belonged to a specific page - a restructured
section, an old domain's path, anything not naturally a single page's own
"old name" - list an explicit `from`/`to` pair instead:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
      - from: moved-to-another-site
        to: https://example.com/docs
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" },
    		{ "from": "moved-to-another-site", "to": "https://example.com/docs" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"

    [[redirects]]
    from = "moved-to-another-site"
    to = "https://example.com/docs"
    ```

- `from` - the old pretty-URL segment, same shape as `redirect_from` above
- `to` - either a root-relative path (resolved against the site's own
  `baseURL`, same convention `theme.logo`/`ogImage` already use) or a full
  `https://` URL, for redirecting off-site entirely

`redirects` only ever applies to the main site tree - a bare `to` is a
root-relative path that's only unambiguous at the site root. A
version/locale tree wanting the same old-URL mapping needs its own
page-level `redirect_from` instead.

## `page:rename` stamps this for you

Renaming/moving a page with [`page:rename`](../cli-reference.md#pagerename)
automatically adds its old path to the moved page's own `redirect_from` -
on top of rewriting every relative Markdown link that pointed at it, the
old URL itself keeps working too:

```bash title="Usage"
bxSites page:rename --from=guides/old-setup.md --to=guides/new-setup.md
```

Renaming a page more than once just keeps appending - a page's
`redirect_from` list can carry as many old paths as it's had over time.

## Conflicts

A build fails outright, rather than silently overwriting real content, if:

- A redirect's own `from` path collides with a real page already built at
  that path (`BxSites.RedirectConflict`)
- Two redirects (`redirect_from` entries, `redirects` config entries, or
  one of each) both target the same `from` path

## What's out of scope (for now)

- **Blog posts don't get `redirect_from`.** The frontmatter key is only
  read for regular `docs/` pages, not `docs/blog/posts/**` - a moved blog
  post needs its own `redirects` config entry instead.
- **No wildcard/pattern redirects.** Every `from` is one exact old path -
  there's no `guides/old/*` catch-all.
