---
title: Tables
order: 4.6
icon: phosphor-duotone:table
tags: [guides, markdown]
---

# Tables

Standard [GFM](https://github.github.com/gfm/#tables-extension-) pipe
tables, on top of everything in [Markdown Extensions](markdown.md) - no
`bxsites.yaml` config needed, always on:

```markdown title="Example" linenums="1"
| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |
```

Which renders as:

| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |

A row of `---` under the header turns the table on; put colons on that
separator row to control alignment per column - `:---` left, `:---:`
center, `---:` right (no colons at all defaults every column to left).

## Cell content is regular inline markdown

`code`, **bold**, *italic*, and [links](../index.md) all work inside a
cell exactly the way they do anywhere else on the page:

```markdown title="Example" linenums="1"
| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |
```

Which renders as:

| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |

## Escaping a pipe inside a cell

A literal `|` inside a cell's own plain text needs a backslash, `\|` - an
unescaped one is read as the next column's separator instead:

```markdown title="Example" linenums="1"
| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |
```

Which renders as:

| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |

A `|` inside inline code doesn't need escaping at all - the code span
(`` `a | b` ``) already protects it:

| Expression | Meaning |
| --- | --- |
| `a | b` | bitwise OR |

## Short and long rows

A data row doesn't have to match the header's column count exactly - a
short row is padded out with empty cells, and a long row has its extra
cells silently dropped, both controlled by `tableOptions.appendMissingColumns`/`discardExtraColumns` below:

```markdown title="Example" linenums="1"
| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |
```

Which renders as:

| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |

## Configuring parsing

Short/long row handling, the `---` separator row's own strictness, and
the CSS class every `<table>` renders with are all controlled by
`bxsites.yaml`'s [`markdown.tableOptions`](../configuration.md#markdown);
the defaults shown throughout this page are almost always what you want.

## A more complex example

Everything above combines freely - alignment, inline code, links, bold/
italic, and even a short row - in one real-world-shaped table:

```markdown title="Example" linenums="1"
| Endpoint | Method | Auth | Notes |
| --- | :---: | :---: | --- |
| `/sites` | `GET` | ✅ | List every site. See [Sites API](../guides/variables-and-functions.md). |
| `/sites/{id}` | `GET` | ✅ | Fetch one site by id. |
| `/sites` | `POST` | ✅ | **Create** a site; body is a `bxsites.yaml`-shaped JSON object. |
| `/sites/{id}` | `DELETE` | ✅ | Irreversible. |
| `/health` | `GET` | |
```

Which renders as:

| Endpoint | Method | Auth | Notes |
| --- | :---: | :---: | --- |
| `/sites` | `GET` | ✅ | List every site. See [Sites API](../guides/variables-and-functions.md). |
| `/sites/{id}` | `GET` | ✅ | Fetch one site by id. |
| `/sites` | `POST` | ✅ | **Create** a site; body is a `bxsites.yaml`-shaped JSON object. |
| `/sites/{id}` | `DELETE` | ✅ | Irreversible. |
| `/health` | `GET` | |

## Responsive scroll and a sticky header

Every rendered table is automatically wrapped in a `.bxsites-table-wrap`
div - no `bxsites.yaml` config, no extra markdown. It gives a wide table
its own horizontal scrollbar instead of overflowing the page (a table is
never clipped vertically - it always renders at its own full height,
with normal breathing room below it), and its header row sticks to the
top of the viewport while the surrounding page scrolls past it, so a
long table's column headers stay in view - a short table like the ones
above never needs any of this, since it already fits on screen. The
wrapper is also what the theme paints as the table's own card - see
[Theming](#theming) for the tokens behind it - and a custom `theme/`
override can restyle `.bxsites-table-wrap` like any other CSS class.

## Large tables get an automatic filter

Any table with 10 or more data rows automatically gets a live filter
input injected right above it - no `bxsites.yaml` config, no extra
markdown, same "just works" treatment as the scroll wrapper above.
Typing into it hides every row whose text doesn't match, checked against
each row as a whole (every cell's text, not just one column), so it's a
quick way to jump to the right entry in a long reference table without
scrolling:

```markdown title="Example - 10+ rows" linenums="1"
| Code | Category | Description |
| :--: | --- | --- |
| 200 | Success | OK |
| 201 | Success | Created |
| 204 | Success | No Content |
| 301 | Redirection | Moved Permanently |
| 304 | Redirection | Not Modified |
| 400 | Client Error | Bad Request |
| 401 | Client Error | Unauthorized |
| 403 | Client Error | Forbidden |
| 404 | Client Error | Not Found |
| 429 | Client Error | Too Many Requests |
| 500 | Server Error | Internal Server Error |
| 503 | Server Error | Service Unavailable |
```

Which renders as:

| Code | Category | Description |
| :--: | --- | --- |
| 200 | Success | OK |
| 201 | Success | Created |
| 204 | Success | No Content |
| 301 | Redirection | Moved Permanently |
| 304 | Redirection | Not Modified |
| 400 | Client Error | Bad Request |
| 401 | Client Error | Unauthorized |
| 403 | Client Error | Forbidden |
| 404 | Client Error | Not Found |
| 429 | Client Error | Too Many Requests |
| 500 | Server Error | Internal Server Error |
| 503 | Server Error | Service Unavailable |

Try typing "error" or "3" into the filter above - notice the [endpoints
table](#a-more-complex-example) higher up this page (5 rows) never got
one; the threshold is a flat per-table row count, not a page-wide
setting. Sorting isn't part of this - it's a filter only. For a table a
reader can also re-sort, see [A sortable, filterable
table](interactivity.md#a-sortable-filterable-table) below, which builds
the table from Alpine data instead of markdown.

## Theming

Every built-in theme renders a table as a self-contained card - one
rounded, bordered wrapper, a tinted header strip, horizontal row dividers
with no vertical grid lines, and a zebra/hover tint on the rows - and it
paints all of that from six CSS custom properties, declared per mode
(`:root` and `[data-theme="dark"]`) in the theme's own
`assets/style.css`:

| Token | What it paints |
| --- | --- |
| `--bxsites-table-bg` | The card's own surface, behind every row |
| `--bxsites-table-head-bg` | The header row, and the filter input above it |
| `--bxsites-table-head-text` | Header label text |
| `--bxsites-table-border` | The card outline and the row dividers |
| `--bxsites-table-stripe-bg` | Even rows - an alpha tint over the card surface |
| `--bxsites-table-hover-bg` | The row under the cursor - the same, slightly stronger |

Because every one of them is declared in both modes, a table follows the
theme toggle like the rest of the page instead of leaving a light-mode
slab of white behind on a dark one. Retarget any of them from
[`extraCss`](themes.md#customizing-colors-without-a-theme-override) - no
theme override needed:

```css title="docs/assets/brand.css" linenums="1"
[data-theme="dark"] {
	--bxsites-table-head-bg: #241b2e;
	--bxsites-table-hover-bg: rgba(167, 139, 250, 0.12);
}
```

Keep `--bxsites-table-stripe-bg`/`-hover-bg` alpha colors: they're
painted on top of whatever `--bxsites-table-bg` puts down, so an opaque
value there covers the card surface instead of tinting it. Anything past
color - padding, the corner radius, the uppercase header labels - is a
real `theme/` override, the same as any other CSS in a theme.

## Beyond plain data

Two more recipes build directly on top of a plain table like the ones
above:

- Need a status chip or a star rating in a cell? See [Visualizer
  recipes](variables-and-functions.md#visualizer-recipes).
- Need a reader to actually sort or filter a table client-side, rather
  than just read it? See [A sortable, filterable
  table](interactivity.md#a-sortable-filterable-table).
