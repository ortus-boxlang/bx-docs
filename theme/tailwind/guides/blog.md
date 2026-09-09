---
title: Blog
order: 10
icon: phosphor-duotone:newspaper
tags: [guides, blog]
---

# Blog

A blog is another by-convention feature, the same shape as
[versions](../configuration.md#versioning)/[i18n](i18n.md) or the
[tags index](../getting-started.md#add-pages) - drop posts under
`docs/blog/posts/`, and BxSites builds `/blog/` (paginated), a category page
per category, a year archive page per calendar year, an author page per
author, an RSS feed per category plus one for the whole blog, and a
`/blog/stats/` page, with zero config required. A project with no
`docs/blog/posts/` folder simply doesn't have a blog - nothing else changes.

## Writing a post

Every `.md` file under `docs/blog/posts/`, at any depth, is a post -
subfolders are entirely optional and purely for your own editing
convenience. A flat folder works fine for a handful of posts; once you're
into the hundreds, filing posts under `docs/blog/posts/2026/` (or
`docs/blog/posts/2026/03/`, or any scheme you like) keeps your editor's
file tree browsable without renaming anything or touching a frontmatter
date-prefix convention. None of it affects the built site - a post's sort
order, its year archive, and its URL (`blog/<slug>/`) are all derived from
frontmatter alone, never from where the file happens to live, so a post's
folder and its actual `date` are always free to disagree:

```text title="Project structure"
docs/blog/posts/
├── hello-world.md              (flat is fine too)
├── 2026/
│   ├── announcing-2-0.md
│   └── 03/
│       └── a-deep-dive.md
```

Frontmatter, for any post regardless of where it's filed:

```markdown title="docs/blog/posts/announcing-2-0.md" linenums="1"
---
title: Announcing BoxLang 2.0
date: 2026-08-15
authors: [lmajano]
categories: [Releases]
tags: [boxlang, release]
summary: A faster runtime, a smaller footprint, and a few surprises.
image: assets/blog/boxlang-2-cover.png
---

A short intro paragraph or two.

<!-- more -->

The rest of the post - everything below the `<!-- more -->` marker is left
out of the excerpt shown on `/blog/` and category pages, but still renders
in full on the post's own page.
```

- `date` (required) - any BxSites can parse (`2026-08-15`, or a full
  date-time). Sets the post's own sort order (newest first) and its
  `<pubDate>`/`article:published_time`.
- `authors` - a list of ids matching [`docs/blog/authors.yml`](#authors)
  entries, or a plain name with no matching entry (rendered as unlinked text
  rather than failing the build - handy for a one-off guest post).
- `categories` - a post's own taxonomy, each getting its own
  `/blog/category/<slug>/` page (and its own `/blog/category/<slug>/feed.xml`
  RSS feed - see [Feed](#feed)). Unrelated to `tags`, below.
- `tags` - the same site-wide `tags` frontmatter every other page already
  has (see [Getting Started](../getting-started.md#add-pages)) - a post's
  tags render as badges and fold into the main `/tags/` index alongside
  every other tagged page.
- `summary` - a one-line excerpt shown on `/blog/`/category pages and in
  the RSS feed, used when a post has no `<!-- more -->` marker. Without
  either, BxSites falls back to a plain-text truncation of the post's own
  body.
- `image` - a featured image (a `docs/assets/`-relative path, or a full
  URL) - shown at the top of the post and as a thumbnail on every list/
  category card. Also becomes the post's own `og:image`/Twitter card unless
  `ogImage` overrides it separately. A `docs/assets/`-relative image (and an
  author's own `avatar`, below) gets the same responsive `<picture>`/
  `srcset`/WebP treatment as any other image under `docs/assets/` - see
  [Images](images.md).
- `slug` - overrides the URL segment (`/blog/<slug>/`) - derived from the
  filename by default.
- `draft: true` - excludes the post from a real `bxSites build` entirely.
  `bxSites serve` previews it anyway (with a visible "🚧 Draft" banner on the
  post itself and a dashed-border card wherever it's listed), so you can
  proofread a draft locally before it's ready - see
  [Previewing drafts](#previewing-drafts).

Every other page-level frontmatter key already documented in
[Getting Started](../getting-started.md#add-pages) (`icon`, `description`,
`ogImage`, `toc`) works on a post too.

## Featured images and other blog assets

`docs/assets/blog/` is nothing special beyond an ordinary subfolder of
`docs/assets/` (already copied to `site/assets/` wholesale) - it's just
where this guide (and the by-convention author avatar lookup below) expects
post covers/author photos to live, so a project's own `docs/assets/`
doesn't get cluttered mixing blog images in with the rest of its diagrams
and icons. Nothing enforces the location - any `docs/assets/**` path works
in `image`/`avatar`.

## Authors

`docs/blog/authors.yml` is optional - one entry per author id, referenced
by a post's own `authors` list:

```yaml title="docs/blog/authors.yml" linenums="1"
lmajano:
  name: Luis Majano
  title: CEO, Ortus Solutions
  bio: >
    Founder of Ortus Solutions and creator of ColdBox, WireBox, and
    BoxLang. Building developer tools since 2005.
  url: https://github.com/lmajano
  email: lmajano@ortussolutions.com
  socials:
    github: https://github.com/lmajano
    twitter: https://x.com/lmajano
```

Only `name` is required. Every author referenced by at least one post gets
their own `/blog/authors/<id>/` page (bio, socials, every post they've
written) - an author nobody's credited yet doesn't get a page, even if
they're in the roster.

**Avatar, by convention** - drop a file at
`docs/assets/blog/authors/<id>.{jpg,jpeg,png,webp,svg}` and it's picked up
automatically, no `avatar:` key needed. An explicit `avatar` in
`authors.yml` (a URL or a `docs/assets/`-relative path) always overrides
the by-convention lookup.

## Categories, archives, pagination, and the "Blog" nav entry

Every distinct `categories` value across all posts gets its own
`/blog/category/<slug>/` page, listing just that category's own posts.
Every calendar year with at least one post also gets its own
`/blog/archive/<year>/` page (`/blog/archive/2026/`, `/blog/archive/2025/`,
...), derived entirely from each post's own `date` frontmatter - no folder
structure or filename convention required, so where a post's `.md` file
actually lives under `docs/blog/posts/` (flat, or split into your own
subfolders for easier browsing while editing) never has to match its
`date`. The main `/blog/` list gets "Browse by year"/"Browse by category"
links blocks, each with a post count per year/category, automatically once
posts span more than one year/category - a single year or category alone
isn't worth a links block, so it's left off either way.

The main `/blog/` list, every category page, and every year archive page
all paginate identically - `blog.postsPerPage` in the site config controls
how many posts per page (default `10`); page 2 onward moves to
`.../page/2/`, `.../page/3/`, etc.

A single "Blog" entry is added to the main nav automatically, once
`docs/blog/posts/` has at least one non-draft post - no `nav`/`docs/nav.json`
change needed. By default it's appended last, after everything else. To put
it somewhere specific instead, add your own entry with an explicit `url`
(bypasses the usual `path`-must-match-a-real-page rule, since the blog isn't
a `docs/` page) to your `nav` array or `docs/nav.json` - doing so suppresses
the auto-appended one entirely, so there's never a duplicate:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - path: index.md
      - title: Blog
        url: blog/index.html
        icon: lucide:newspaper
      - path: about.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		{ "path": "index.md" },
    		{ "title": "Blog", "url": "blog/index.html", "icon": "lucide:newspaper" },
    		{ "path": "about.md" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Blog"
    url = "blog/index.html"
    icon = "lucide:newspaper"

    [[nav]]
    path = "about.md"
    ```

Individual posts aren't added to the nav themselves (same as the tags
index) - they're reachable from `/blog/`, their own category page, their
own year archive, their author's page, search, and each other's prev/next
links (posts chronologically adjacent to one another, independent of the
regular nav's own prev/next chain).

Every post's own meta line (on its card and its detail page) also shows an
estimated reading time next to the date - a rough word-count / 200wpm
estimate, the same ballpark figure most reading-time features use, not
configurable.

## Feed

`/blog/feed.xml` - a standard RSS 2.0 feed of the most recent posts, newest
first, written whenever the site config resolves an absolute `baseURL`
(same requirement as `sitemap.xml`) and `blog.feed` isn't set to `false`.
Every category also gets its own filtered feed at
`/blog/category/<slug>/feed.xml`. Both are capped to `blog.feedLimit` posts
(default `25`) - most feed readers only care about what's new, so an
unbounded feed on a large blog just wastes bandwidth on every poll; set it
to `0` for every post, uncapped:

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

## Previewing drafts

`draft: true` keeps a post out of a real `bxSites build` entirely - but
`bxSites serve` includes it anyway, so you can read through a draft (and
click every link, check the featured image, see how it lists on `/blog/`)
before it's ready. A previewed draft always carries a visible "🚧 Draft"
banner - on its own detail page, and as a dashed-border card wherever it's
listed (the main `/blog/` list, its own category/archive/author pages) -
so there's never any ambiguity about what's actually published. Stop
`bxSites serve` and run `bxSites build` and the same draft is gone, exactly
as if it didn't exist.

## Stats

`/blog/stats/` - a handful of aggregate cards about the blog as a whole:
total posts, total words written, average reading time, category/
contributor/year counts, and three "spotlight" cards (longest post, most
active category, most active author) each linked to the real page they're
about. Computed purely from the posts already loaded for this build - no
separate analytics, no tracking, nothing persisted between builds - and
always built, even for a brand-new blog with zero posts yet. Linked from
the bottom of the main `/blog/` list.

## SEO and social

Every post already gets everything a normal page does (`<meta name="description">`,
`og:description`, `og:image`+`twitter:card` when an image is set - see
[Configuration: `ogImage`](../configuration.md#ogimage)) plus a few
post-specific tags every built-in theme adds automatically: `og:type` is
`"article"` instead of `"website"`, and `article:published_time`/
`article:author` (one per credited author who has a `url` set in
`authors.yml`) are included in the page `<head>`.

## Search

Posts are indexed into the same `search-index.json` every other page is
(module spec section 7) - no separate blog search UI, the existing search
box already finds posts alongside docs pages.

## Customizing the blog's appearance

There's no separate "blog theme" to write - every blog page (the main
`/blog/` list, a category/archive/author page, `/blog/stats/`, and each
post's own detail page) renders through the exact same `layout.bxm`/
`page.bxm` as any other page in your site, so a blog automatically looks
like the rest of your docs, and any theme override you've already made
(see [Themes](themes.md#overriding-a-theme)) applies to it unchanged, with
no extra wiring.

The blog-specific markup itself (post cards, the date/author/reading-time
meta line, the pager, an author's profile block, the "Browse by year"/
"Browse by category" link lists) is built as plain HTML with a handful of
fixed class names, then dropped into `page.contentHtml` just like a
converted Markdown page:

| Class | Where it shows up |
|---|---|
| `blog-post-card` / `blog-post-card--draft` | Each post's card on `/blog/`, a category page, or an archive page |
| `blog-post-meta` | The date/author/reading-time line, on a card and on a post's own page |
| `blog-post-featured-image` | A post's `image` frontmatter, on its own detail page |
| `blog-draft-badge` | The "🚧 Draft" banner (`bxSites serve` only) |
| `blog-pager` | Prev/next pagination links on a paginated list |
| `blog-author-profile` | An author's bio/socials block on their `/blog/authors/<id>/` page |
| `blog-archive-links` / `blog-category-links` | The "Browse by year"/"Browse by category" link blocks on `/blog/` |

Two ways to restyle it, same as any other page:

- **A quick visual tweak** - target these classes from your own
  [`extraCss`](../configuration.md#extracss--extrajs), the same way you'd
  [customize a theme's colors](themes.md#customizing-colors-without-a-theme-override).
  A built-in theme's own rules for these classes live in its
  `assets/style.css` (e.g. `resources/themes/bootstrap/assets/style.css`)
  if you want a starting point to override.
- **Structural changes** - since blog pages share `layout.bxm`/`page.bxm`
  with everything else, [overriding a theme](themes.md#overriding-a-theme)
  (or [writing one from scratch](themes.md#writing-a-theme-from-scratch))
  changes the blog's chrome (header, nav, footer, article wrapper) right
  along with every other page - there's no separate blog template to copy.

What you can't do is swap out the post-card/pager/author-profile markup
itself for your own - it's generated once by `BlogBuilder.bx`, not read
from a template file in `theme/`, so restyling it with CSS (above) is the
supported path rather than a per-component override.
