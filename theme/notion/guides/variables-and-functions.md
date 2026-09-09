---
title: Variables & Magic Functions
order: 12
icon: phosphor-duotone:magic-wand
tags: [guides, variables, functions]
---

# Variables & Magic Functions

Two small, related features for keeping repeated facts and repeated logic
out of your Markdown: **reusable variables**, defined once in
`bxsites.yaml` and dropped into any page with `{{ }}`, and **magic
functions**, small BoxLang helpers you write once in `docs/functions.bxs`
and call the same way - everywhere, with no import, no plugin, no wiring.

Both share one syntax:

```text
{{ dotted.path }}          # a reusable variable
{{ $name(arg1, arg2) }}    # a magic function call
```

## Reusable variables

Add a `variables` block to `bxsites.yaml` - any shape you like, flat or
nested:

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product:
        name: "BoxLang"
        supportEmail: "support@example.com"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": {
    			"name": "BoxLang",
    			"supportEmail": "support@example.com"
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [variables]
    company = "Ortus Solutions"

    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

Then reference any of it, by dotted path, from any Markdown page:

```markdown title="docs/index.md"
# Welcome to {{ company }}

We build {{ product.name }} tools. Need help? Write us at
{{ product.supportEmail }}.
```

builds to:

```html
<h1>Welcome to Ortus Solutions</h1>
<p>We build BoxLang tools. Need help? Write us at support@example.com.</p>
```

A `{{ }}` variable is resolved once, at build time, against whatever
`bxsites.yaml`'s own `variables` block has right then - rename a product,
update a support address, or bump a year in one place, and every page
using it picks up the change on the next build. See
[`variables`](../configuration.md#variables) in the configuration
reference.

## Page variables

Every page's own frontmatter is available the same way, under a reserved
`page` name - no `bxsites.yaml` `variables` entry needed:

```markdown title="docs/guides/setup.md"
---
title: Setup
product: BoxLang
---

# {{ page.title }}

This guide covers installing {{ page.frontmatter.product }}.
```

`page.title`/`page.summary`/`page.description`/etc. - anything bx-sites
itself already reads off frontmatter (see [Getting Started](../getting-started.md#add-pages)'s
frontmatter reference) - are reachable directly; any other, project-defined
frontmatter key (`product` above) is reachable under `page.frontmatter.<key>`,
since only bx-sites' own known fields get promoted to a top-level `page.*`
name of their own. A blog post's own frontmatter works identically (see
[Writing a post](blog.md#writing-a-post)).

`page` is a reserved name here, the same way it's already reserved for a
magic function's own bare `page` reference (see [Reserved names](#reserved-names)
below) - a `bxsites.yaml` `variables.page` entry, if a project somehow
declared one, would be shadowed by the current page's own struct rather
than winning.

## Magic functions

Add a `docs/functions.bxs` file (or `src/functions.bxs`, if your project
uses `src/` - see [Getting Started](../getting-started.md)) - a plain
BoxLang script. Any function you name with a leading `$` becomes a *magic
function*: callable from `{{ }}` in Markdown, and callable bare, directly,
from a project's own [`theme/`](themes.md#overriding-a-theme)
`.bxm` overrides.

```bx title="docs/functions.bxs" linenums="1"
function $shout( text ) {
	return uCase( arguments.text ) & "!"
}

function $badge( label, kind = "info" ) {
	return '<span class="badge bg-' & arguments.kind & '">' & arguments.label & '</span>'
}
```

```markdown title="docs/index.md"
{{ $shout('this is important') }}

Status: {{ $badge('Stable', 'success') }}
```

builds to:

```html
<p>THIS IS IMPORTANT!</p>
<p>Status: <span class="badge bg-success">Stable</span></p>
```

A magic function can return anything `toString()`-able - plain text,
HTML, a number - and it's spliced straight into the page's Markdown
before it's converted, so returning real HTML (like `$badge()` above)
works exactly the way you'd expect.

A function declared *without* a leading `$` in the same `functions.bxs` is
just a private helper, meant to be called only from your other
`$`-prefixed functions in the same file (they're all loaded into the same
scope, so one can call another bare) - `{{ }}` can never call one directly
(only a `$name(...)` call target is ever recognized), and it isn't part of
the documented public surface a theme override should call either, even
though it happens to be technically reachable there too:

```bx title="docs/functions.bxs"
private string function formatPrice( amount ) {
	return "$" & numberFormat( arguments.amount, "9.99" )
}

function $price( amount ) {
	return formatPrice( arguments.amount )
}
```

### Calling a magic function from a theme override

Because a magic function is bound directly into template scope, a
project's own `theme/page.bxm` (or `layout.bxm`) can call it bare, with no
prefix at all - the same way it already reads `variables.page`/
`variables.siteConfig`:

```bx title="theme/page.bxm (excerpt)"
<p class="build-banner">#$shout( 'built with boxlang' )#</p>
```

### Context variables

Every magic function's own body can also read a fixed set of "supporting
variables" - bare, with no argument needed - regardless of whether it's
called from `{{ }}` in Markdown or bare from a theme override:

| Variable | What it is |
|---|---|
| `siteConfig` | The site's own `bxsites.yaml` config (already defaulted/validated) |
| `page` | The current page (see the note below - not every field is populated yet when called from Markdown) |
| `nav` | This tree's own nav tree |
| `basePath` | Root-relative base path, ending with `/` |
| `versions` | Version switcher entries - `[ { label, url } ]` |
| `currentVersion` | Which `versions` entry is being rendered right now |
| `locales` | Language switcher entries - `[ { code, label, url, dir, flag } ]` |
| `currentLocale` | Which `locales` entry's code is being rendered right now |
| `currentLocaleDir` | `"ltr"`/`"rtl"` for the current locale |
| `data` | This project's own [data files](data-files.md) - `docs/data/*.yaml`/`.json`, one key per file - `{}` when the project has none |

```bx title="docs/functions.bxs"
function $sitename() {
	return siteConfig.name
}

function $pagetitle() {
	return page.title
}
```

```markdown title="docs/index.md"
Site: {{ $sitename() }}
Page: {{ $pagetitle() }}
```

**`page` isn't equally complete in both places.** Called from Markdown,
`page` is this specific page's own struct *as loaded from disk* -
`title`/`description`/`tags`/`icon`/`summary`/`ogImage`/`urlPath`/
`relativePath`/`body`/etc. are already there, but the fields only known
once every page in the tree has finished converting - `toc`,
`prevPage`/`nextPage`, `breadcrumbs`, `editUrl`/`lastUpdated`, `iconHtml`,
`markdownUrl`, `canonicalUrl`, `course` - don't exist on it yet. Called
bare from `page.bxm`, `page` is the fully-enriched struct, all of those
included.

`page.course` is always present (an all-empty `{id:"", courseTitle:"",
lessonIndex:0, lessonTotal:0, prevLesson:{title:"",url:""},
nextLesson:{title:"",url:""}, indexUrl:""}` struct for a page that isn't
part of any [course](courses.md)) - `prevLesson`/`nextLesson` are scoped
to that one course only, independent of the site-wide `prevPage`/
`nextPage` above, which always walks the whole nav tree instead.
Every other supporting variable (`siteConfig`, `nav`, `basePath`,
`versions`, `currentVersion`, `locales`, `currentLocale`,
`currentLocaleDir`) is identical in both places.

### Argument syntax

A magic function call's arguments are simple, comma-separated literals or
variable references - no nested function calls or expressions in this
first version:

- Numbers: `{{ $discount(20) }}`
- Quoted strings: `{{ $greet('World') }}` or `{{ $greet("World") }}`
- Booleans: `{{ $badge('Beta', true) }}`
- A `{{ }}`-less dotted variable reference: `{{ $greet(product.name) }}`

## Visualizer recipes

A magic function returning HTML isn't limited to a status badge - it's a
general-purpose way to get visual cells (a star rating, a colored chip, a
progress bar) with no database-backed column picker needed - bx-sites'
git-based, plain-Markdown source has no equivalent of one. The four below are this site's own
[`docs/functions.bxs`](https://github.com/ortus-boxlang/bx-sites/blob/development/docs/functions.bxs),
rendering live on this exact page.

### Ratings

```bx title="docs/functions.bxs"
function $stars( required numeric rating, numeric max = 5 ) {
	var filled = min( max( round( arguments.rating ), 0 ), arguments.max )
	var stars = repeatString( "★", filled ) & repeatString( "☆", arguments.max - filled )
	return '<span title="' & arguments.rating & ' out of ' & arguments.max & '" style="color:##f5a623;letter-spacing:2px">' & stars & '</span>'
}
```

`` `{{ $stars(4) }}` `` renders as: {{ $stars(4) }}

### Status chips

```bx title="docs/functions.bxs"
function $badge( required string label, string kind = "info" ) {
	var palette = {
		"info"    : { "bg" : "##e0edff", "fg" : "##1d4ed8" },
		"success" : { "bg" : "##dcfce7", "fg" : "##15803d" },
		"danger"  : { "bg" : "##fee2e2", "fg" : "##b91c1c" },
		"warning" : { "bg" : "##fef9c3", "fg" : "##854d0e" }
	}
	var pick = palette.keyExists( arguments.kind ) ? palette[ arguments.kind ] : { "bg" : "##f1f5f9", "fg" : "##475569" }
	return '<span style="display:inline-block;padding:0.1em 0.6em;border-radius:999px;font-size:0.85em;font-weight:600;background:'
		& pick.bg & ";color:" & pick.fg & '">' & encodeForHTML( arguments.label ) & "</span>"
}
```

`` `{{ $badge('Stable', 'success') }}` `` renders as: {{ $badge('Stable', 'success') }} - and `` `{{ $badge('Beta', 'info') }}` ``: {{ $badge('Beta', 'info') }}

### Progress bars

```bx title="docs/functions.bxs"
function $progress( required numeric percent ) {
	var pct = min( max( arguments.percent, 0 ), 100 )
	return '<span style="display:inline-block;width:120px;height:8px;background:##e5e7eb;border-radius:999px;overflow:hidden;vertical-align:middle"><span style="display:block;height:100%;width:'
		& pct & '%;background:##2563eb"></span></span> ' & pct & "%"
}
```

`` `{{ $progress(72) }}` `` renders as: {{ $progress(72) }}

### Trend indicators

```bx title="docs/functions.bxs"
function $trend( required numeric value ) {
	var isUp = arguments.value >= 0
	var arrow = isUp ? "▲" : "▼"
	var color = isUp ? "##16a34a" : "##dc2626"
	var sign = isUp ? "+" : ""
	return '<span style="color:' & color & ';font-weight:600">' & arrow & " " & sign & numberFormat( arguments.value, "0.0" ) & "%</span>"
}
```

`` `{{ $trend(4.2) }}` `` renders as: {{ $trend(4.2) }} - `` `{{ $trend(-1.8) }}` ``: {{ $trend(-1.8) }}

### Inside a table cell

`{{ }}` resolves against the raw Markdown before [tables](tables.md)
are even parsed, so any of the above work inside a pipe table's cells the
same as anywhere else on the page:

```markdown title="Example" linenums="1"
| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |
```

Which renders as:

| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |

## Showing the syntax literally

A `{{ }}` shown inside a fenced code block (three backticks or more, like
every example on this page) is left completely untouched rather than
resolved - the same convention this module already uses for `$...$` math
and `=== "Tab"` content tabs. Unlike those two, a `{{ }}` shown in *inline*
code (`` `{{ example }}` ``, single or double backticks) is protected too -
every bullet point above showing `` `{{ $discount(20) }}` `` inline is a
real, working example of that.

A `{{ }}` whose contents don't look like either a variable path or a
`$name(...)` call - some other templating engine's own `{{ }}` syntax
shown in prose, say - is left untouched rather than treated as an error.
Only a token that *looks like* a variable or a magic function call, but
doesn't resolve, fails the build (see [Errors](#errors) below) - that's
deliberate, to catch a real typo without misreading unrelated `{{ }}` text
as broken syntax.

## Scope

- `functions.bxs` is project-wide - one file, loaded once, the same set of
  magic functions available on every page across the main tree and every
  [version](versioning.md)/[locale](i18n.md) tree. You don't need to
  duplicate it into `docs/versions/<name>/` or `docs/i18n/<code>/`.
- `variables` is likewise a single, project-wide `bxsites.yaml` block -
  it isn't itself translatable per locale. A multilingual project wanting
  different variable text per language can instead reach for a magic
  function that switches on `siteConfig.i18n.defaultLocale.code` (or
  simply keep the value locale-neutral - a product name, a support email).

## Reserved names

A `theme/page.bxm`/`layout.bxm` override calling a magic function bare
(`$name(...)`) works because every loaded function - `$`-prefixed or a
private helper alike - is bound directly into that same template's own
rendering scope, right alongside the built-in `variables.page`/
`variables.siteConfig`/etc. that every theme already reads. That means a
`functions.bxs` function sharing a name with one of those already has one:
avoid `page`, `nav`, `siteConfig`, `themeDir`, `basePath`,
`moduleAssetsDir`, `versions`, `currentVersion`, `locales`,
`currentLocale`, `currentLocaleDir`, `strings`, `requiredFiles`,
`stringsResolver` and `data` for a private helper's own name (a
`$`-prefixed magic function can never collide with any of these, since
none of them start with `$`). See [Data Files: Scope](data-files.md#scope)
for `data`'s own reserved-name note.

## Errors

- `BxSites.UnknownVariable` - a `{{ dotted.path }}` (or a `$name(...)`
  argument that looks like a variable reference) doesn't match anything in
  `bxsites.yaml`'s `variables` block.
- `BxSites.UnknownFunction` - a `{{ $name(...) }}` call doesn't match any
  `$`-prefixed function in `docs/functions.bxs`.
- `BxSites.InvalidFunctions` - `docs/functions.bxs` failed to load (a
  BoxLang syntax error in the file itself).
- `BxSites.InvalidConfig` - `bxsites.yaml`'s `variables` key is present but
  isn't an object.
