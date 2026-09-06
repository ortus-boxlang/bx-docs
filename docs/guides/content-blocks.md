---
title: Content Blocks
order: 4.5
icon: phosphor-duotone:squares-four
tags: [guides, markdown]
---

# Content Blocks

On top of everything in [Markdown Extensions](markdown.md), BxSites
supports a family of rich content blocks for things plain CommonMark has
no concept of - cards, tabs of steps, downloads, embeds, and more. Every
one uses the same `::: name ... :::` container syntax (a bare `:::` on
its own line closes whichever block is currently open, or write the
closing `:::` right on the same line for a block with no body of its
own - `::: file src="assets/spec.pdf" :::` works exactly the same as the
two-line form) - no `bxsites.yaml` config needed, always available. A
block can nest inside another (an expandable containing a cards group,
for instance) - each is scanned again for further blocks inside its own
content. Migrating from GitBook? Every block here maps directly onto its
GitBook counterpart of the same name - see
[Migrating from GitBook](migrating-from-gitbook.md).

## Expandable

A plain collapsible section - no callout icon/color, unlike a
collapsible admonition (`???`, see
[Admonitions](markdown.md#collapsible-admonitions)):

```markdown title="Example" linenums="1"
::: expandable "Is this different from a collapsible admonition?"
Yes - this has no type/icon/color, just a plain expand/collapse section.
Add `open="true"` to start it expanded.
:::
```

::: expandable "Is this different from a collapsible admonition?"
Yes - this has no type/icon/color, just a plain expand/collapse section.
Add `open="true"` to start it expanded.
:::

## Cards

A grid of link cards, each its own `::: card` inside a `::: cards`
wrapper - `title`, `icon`, `image` and `href` are all optional (a card
with no `href` renders as a plain, non-clickable card). `icon` is resolved
the same way frontmatter/nav `icon` values are - a plain emoji, or a named
icon from a bundled library (`icon="phosphor-duotone:rocket-launch"`,
`icon="lucide:rocket"`, ...) - see [Icons](icons.md):

```markdown title="Example" linenums="1"
::: cards
::: card title="Getting Started" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Install, scaffold and build your first site.
:::
::: card title="Themes" icon="phosphor-duotone:palette" href="themes.md"
Customize a built-in theme or write your own.
:::
:::
```

::: cards
::: card title="Getting Started" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Install, scaffold and build your first site.
:::
::: card title="Themes" icon="phosphor-duotone:palette" href="themes.md"
Customize a built-in theme or write your own.
:::
:::

## Columns

A side-by-side layout - `::: column` accepts an optional `width` (a plain
CSS length/percentage, e.g. `"40%"`); columns with no explicit width
share the row equally:

```markdown title="Example" linenums="1"
::: columns
::: column width="60%"
The wider column.
:::
::: column
The narrower one.
:::
:::
```

::: columns
::: column width="60%"
The wider column.
:::
::: column
The narrower one.
:::
:::

## Stepper

A numbered, connected sequence of steps:

```markdown title="Example" linenums="1"
::: stepper
::: step "Install"
`install-bx-module bx-sites`
:::
::: step "Scaffold"
`bxSites new`
:::
:::
```

::: stepper
::: step "Install"
`install-bx-module bx-sites`
:::
::: step "Scaffold"
`bxSites new`
:::
:::

A step's own optional `color` attribute flags its marker with one of four
semantic colors - the default (no `color`), `success`, `warning` or
`danger` - independent of the step's position in the sequence:

```markdown title="Example" linenums="1"
::: stepper
::: step "Back up your data" color="success"
Routine, safe to run any time.
:::
::: step "Optional: enable telemetry" color="warning"
Skip this one if you're not sure.
:::
::: step "Delete the old install" color="danger"
Irreversible - make sure the backup above finished first.
:::
:::
```

::: stepper
::: step "Back up your data" color="success"
Routine, safe to run any time.
:::
::: step "Optional: enable telemetry" color="warning"
Skip this one if you're not sure.
:::
::: step "Delete the old install" color="danger"
Irreversible - make sure the backup above finished first.
:::
:::

The numbered marker, connecting line, and each of the three `color`
palettes above are themeable independently of the rest of the site's
palette, via CSS custom properties - see [Customizing colors](themes.md#customizing-colors-without-a-theme-override).

## File

A download card for a PDF, video, or any other project asset - `src` is
resolved the same way `theme.logo`/frontmatter `ogImage` already are
(relative to `docs/assets/`):

```markdown title="Example" linenums="1"
::: file src="assets/spec.pdf" title="API Specification" :::
```

::: file src="assets/og-image.png" title="Site Preview Image" :::

## Page break

Forces a page break wherever it's placed - handy for printing the built
site, or exporting it to PDF (e.g. via a browser's own "Print to PDF"),
and want a section to always start on a fresh page. No attributes:

```markdown title="Example" linenums="1"
::: pagebreak :::
```

It renders as a plain, subtle divider on screen (so it's still visible
while editing or previewing), and only forces an actual page break inside
print/PDF output - it has no other effect on normal browsing.

## Buttons

A call-to-action button - `::: button` on its own, or several laid out in
a row inside a `::: buttons` wrapper. The leading `"Label"` and `href` are
the only pieces most buttons need:

```markdown title="Example" linenums="1"
::: button "Get Started" href="../getting-started.md" style="primary" :::
```

::: button "Get Started" href="../getting-started.md" style="primary" :::

A few optional attributes give each button its own abilities:

- `style="primary"` or `style="secondary"` (the default) - solid-accent vs.
  outline.
- `size="small"`, `"medium"` (the default) or `"large"`.
- `icon="..."` - resolved the same way a card's own `icon` is (a plain
  emoji, or a named icon like `icon="phosphor-duotone:rocket-launch"` -
  see [Themes: Icons](themes.md#icons)).
- `target="_blank"` - opens the link in a new tab instead of the same one
  (`rel="noopener noreferrer"` is added automatically).
- `disabled="true"` - renders an inert, unclickable button (no `href`
  needed) for a "coming soon" call to action.

```markdown title="Example" linenums="1"
::: buttons
::: button "Read the docs" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Star on GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Coming soon" disabled="true" :::
:::
```

::: buttons
::: button "Read the docs" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Star on GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Coming soon" disabled="true" :::
:::

## Embed

A responsive iframe embed for a recognized provider - currently YouTube,
Vimeo, CodePen, Spotify, Loom and Figma. A URL from anywhere else falls
back to a plain "visit ↗" link card instead of an iframe that would just
refuse to render (most sites block being framed):

```markdown title="Example" linenums="1"
::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::
```

::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::

## Page link

A rich preview card linking to another page - `href` follows the same
file-relative convention as an ordinary [page link](../getting-started.md#linking-between-pages).
Unlike a card, its title/icon/summary are pulled automatically from the
target page's own frontmatter, so it stays in sync if that page is
renamed or its summary changes:

```markdown title="Example" linenums="1"
::: page-link href="../getting-started.md" :::
```

::: page-link href="../getting-started.md" :::

## Link preview

A rich preview card for an *external* URL - the same card shape as
`::: page-link`, but for a link that isn't one of this site's own pages, so
there's no page to pull a title/summary from automatically. Every field
comes from the directive's own attributes: only `url` is required, `title`
falls back to the bare URL when omitted, and `description`/`image` are both
optional. There's no build-time fetch of the target URL to auto-fill these
- the same reasoning that keeps [`audit`](../cli-reference.md#audit) internal-links-only
applies here too, so a slow or unreachable third-party site never affects
build time:

```markdown title="Example" linenums="1"
::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::
```

::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::

## AI Prompt

A styled container for a reusable AI prompt. The block's body *is* the
prompt text, written as ordinary Markdown (so
headings, lists, and code inside it still get their own formatting); every
prompt gets a "Copy" button that copies that exact source text, formatting
markup included, ready to paste into whatever AI tool you're using it with.
`description` (an optional one-line summary) and `icon` (resolved the same
way `::: card`'s own `icon` is - defaults to a sparkle glyph when omitted)
are both optional:

```markdown title="Example" linenums="1"
::: prompt description="Summarizes a pull request for a changelog entry" icon="phosphor-duotone:git-pull-request"
Summarize the following pull request diff as a single changelog entry,
written for an end user rather than a developer. Group related changes
together and skip anything purely internal (refactors, tests, CI).
:::
```

::: prompt description="Summarizes a pull request for a changelog entry" icon="phosphor-duotone:git-pull-request"
Summarize the following pull request diff as a single changelog entry,
written for an end user rather than a developer. Group related changes
together and skip anything purely internal (refactors, tests, CI).
:::

Add `expanded="preview"` to clamp a long prompt to a short, fade-out
preview until the reader clicks "Show more", or `expanded="hidden"` to
start it fully collapsed behind a "Show prompt" button - handy for a page
that lists several prompts back to back. Omit `expanded` (or set it to
`"full"`, the default) to always show the whole prompt:

```markdown title="Example" linenums="1"
::: prompt description="A longer, multi-step prompt" expanded="preview"
1. Read the attached error log line by line.
2. For each stack trace, identify the failing module.
3. Group failures by root cause, not by timestamp.
4. Propose one fix per root cause, not per failure.
5. Skip anything that already has an open issue - list those separately.
:::
```

::: prompt description="A longer, multi-step prompt" expanded="preview"
1. Read the attached error log line by line.
2. For each stack trace, identify the failing module.
3. Group failures by root cause, not by timestamp.
4. Propose one fix per root cause, not per failure.
5. Skip anything that already has an open issue - list those separately.
:::

There's no "Open in AI providers" menu here - bx-sites never talks to a
third-party AI provider, so a prompt's own "Copy" button is the one way
to get it into whatever tool you're using.

## Updates (changelog)

A dated, taggable changelog list - `::: update` accepts `date="YYYY-MM-DD"`
and an optional comma-separated `tags`:

```markdown title="Example" linenums="1"
::: updates
::: update date="2026-01-15" tags="feature,fix"
Added dark mode and fixed a footer alignment bug.
:::
::: update date="2026-01-01"
Initial release.
:::
:::
```

::: updates
::: update date="2026-01-15" tags="feature,fix"
Added dark mode and fixed a footer alignment bug.
:::
::: update date="2026-01-01"
Initial release.
:::
:::

A page with an `::: updates` block also gets its own `feed.xml` (RSS 2.0)
written alongside it once `bxsites.yaml`'s `baseURL` is a full URL - same
requirement as `sitemap.xml` - so readers can subscribe to just that
page's changelog.

## Reusable content (includes)

`::: include src="..."` splices another file's raw Markdown in at that
point. Unlike every block above, this becomes real page content
(headings, paragraphs, its own nested blocks), not something wrapped in
a widget - useful for a warning/notice repeated across several pages.
Put the partial itself under `docs/includes/` - the same reserved-folder
convention as `assets/`/`versions/`/`i18n/`/`blog/`. A file under
`includes/` is never built as its own page and never appears in
nav/search/sitemap/tags - it only exists to be spliced into other pages:

```text title="docs/ layout"
docs/
├── index.md
├── includes/
│   ├── beta-notice.md
│   └── legal/
│       └── terms.md
└── guides/
    └── deep/
        └── setup.md
```

A **bare** `src` (no leading `./` or `../`) always resolves against the
current tree's own `docs/includes/`, no matter how deeply nested the
including page is - `guides/deep/setup.md` above reaches the same file
`index.md` does, both with the exact same `src`:

```markdown title="From either index.md or guides/deep/setup.md"
::: include src="beta-notice.md"
```

A bare `src` can also point into a subfolder of `includes/` itself:

```markdown title="Example"
::: include src="legal/terms.md"
```

Prefix `src` with `./` or `../` instead to reach a page-adjacent
fragment that isn't meant to live in the centralized `includes/`
folder - that form resolves file-relative to the *including* page's own
directory, the same convention as an ordinary page link:

```markdown title="From guides/deep/setup.md, one level up instead of centralized"
::: include src="../local-note.md"
```

A version/locale tree gets its own `includes/` the same way - a page
under `docs/versions/2.0/` resolves a bare `src` against
`docs/versions/2.0/includes/`, and one under `docs/i18n/es/` against
`docs/i18n/es/includes/` - each tree's partials are its own, not shared
with the main tree's `docs/includes/`.

An included file can itself include another (a circular chain throws
`BxSites.CircularInclude` at build time rather than looping forever).

## Conditional content

Shows one of several variants of a block based on a reader's own choice -
"Free" vs. "Pro" instructions on the same page, say. This is a fully
static site with no visitor identity of any kind, so unlike a platform
with a real backend, there's no server-evaluated "who is this reader" -
the reader picks for themself, and their choice is simply remembered in
their own browser (`localStorage`) for every later page too:

```markdown title="Example" linenums="1"
::: audience-switcher key="plan" options="free:Free,pro:Pro" :::

::: conditional key="plan" value="free"
The Free plan includes basic search.
:::

::: conditional key="plan" value="pro"
The Pro plan adds AI-assisted search and unlimited team seats.
:::
```

::: audience-switcher key="plan" options="free:Free,pro:Pro" :::

::: conditional key="plan" value="free"
The Free plan includes basic search.
:::

::: conditional key="plan" value="pro"
The Pro plan adds AI-assisted search and unlimited team seats.
:::

`::: conditional key="..." value="..."` marks one variant; `key` is
whatever preference name you're switching on (`"plan"` above, but it
could just as well be `"os"`, `"language"`, anything), and `value` is the
one setting this particular block should show for. Every variant always
renders in the HTML - hidden client-side, never omitted - so a reader with
JavaScript disabled (or a search crawler) still sees every variant rather
than none.

`::: audience-switcher key="..." options="value:Label,value:Label,..."` is
an optional, ready-made control - one button per option, switching every
`::: conditional` block sharing that same `key` immediately, anywhere on
the page. You don't need it at all: a link ending in `?plan=pro` sets the
same preference automatically on load (handy for sharing a direct link to
"the Pro version of this page"), and a project's own theme override can
call `window.bxSitesSetPreference( key, value )` directly to drive it from
custom UI instead.

## Loop and conditional (data-driven)

`::: for` and `::: if` render their own content against [reusable
data](data-files.md) - a `docs/data/*.yaml`/`.json` file's own value,
addressed by dotted path. Unlike every block above, these two take a bare
expression instead of `key="value"` attributes - deliberately narrow, the
same dotted-path-only philosophy `{{ }}` itself already uses (no
comparison operators in this first version):

```markdown title="Example" linenums="1"
::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::
```

::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::

`::: for <item>, <index> in <dotted.path>` binds `<item>`/`<index>` the
same way BoxLang's own two-variable `for` loop does for whatever the path
resolves to - item + 1-based index for an array (as above), or key + value
for a struct, the identical syntax either way:

```markdown title="Example" linenums="1"
::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::
```

::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::

`::: if <dotted.path>` renders its content only when the resolved value is
truthy - an empty array/struct/string, `0` and `false` all count as
falsy:

```markdown title="Example" linenums="1"
::: if data.flags.betaBanner
Beta features are enabled on this build.
:::
```

::: if data.flags.betaBanner
Beta features are enabled on this build.
:::

Chain `::: elseif <dotted.path>` (any number of them) and a trailing bare
`::: else` after a `::: if` for real `if`/`elseif`/`else` semantics - the
first truthy condition wins, `::: else` (no condition of its own) catches
whatever's left, and a condition after the winning one is never even
resolved, so a typo'd `::: elseif` path only breaks the build once its
own branch is actually reached. The whole chain closes with **one**
trailing `:::` - `::: elseif`/`::: else` themselves mark where the
previous branch ends, so there's no `:::` needed before each of them:

```markdown title="Example" linenums="1"
::: if data.flags.darkModeDefault
Dark mode is on by default.
::: elseif data.flags.betaBanner
Beta features are enabled, though dark mode isn't on by default.
::: else
Nothing special about this build.
:::
```

::: if data.flags.darkModeDefault
Dark mode is on by default.
::: elseif data.flags.betaBanner
Beta features are enabled, though dark mode isn't on by default.
::: else
Nothing special about this build.
:::

A `:::` before an `::: elseif`/`::: else` still works too, if you'd
rather close each branch explicitly - both forms parse identically.

Both bodies can contain ordinary Markdown and even other content blocks -
including another `::: for`/`::: if`, nested exactly like any block above.
See [Data Files: Consuming data](data-files.md#consuming-data) for the
full loop/conditional story, including the two other ways to work with
`data.*` - a theme override, or a magic function.

## Course index

`::: course id="..." :::` renders a whole [course](courses.md)'s lessons
as one numbered, linked index - a big "1. Introduction, 2. Windows
Installation, 3. Mac Installation..." list, each number a real link,
built from a `docs/data/courses.yaml` manifest rather than hand-authored:

```markdown title="Example" linenums="1"
::: course id="getting-started" :::
```

Unlike every block above, this one takes only a bare `id`, no `href`/body
content of its own - the lessons themselves, and their order, come
entirely from the manifest. See [Courses](courses.md) for the manifest
format, scoped lesson-to-lesson navigation, and how reader progress gets
tracked once the index is on the page.
