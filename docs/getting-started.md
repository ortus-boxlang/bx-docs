---
title: Getting Started
order: 2
icon: phosphor-duotone:rocket-launch
summary: Install the module, scaffold a project, and build your first site.
tags: [guides, setup]
---

# Getting Started

## Prerequisite: install BoxLang

Everything below assumes the BoxLang runtime itself is already on your
machine - `install-bx-module` is BoxLang's own CLI installing a module into
an existing installation, and CommandBox's `box install` still needs a
BoxLang engine to run against. If you don't have BoxLang yet, install it
first with either:

- the **quick installer** (single version, simplest to get started):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install.boxlang.io/ | bash
  ```

- or **BVM**, the BoxLang Version Manager (installs multiple BoxLang
  versions side by side and lets you switch between them):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install-bvm.boxlang.io/ | bash
  bvm install latest && bvm use latest
  ```

Windows and Homebrew installers, plus the full BVM command reference, are
covered in
[BoxLang's own installation docs](https://boxlang.ortusbooks.com/getting-started/installation).

## Install

BxSites depends on [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
for Markdown rendering, [bx-esapi](https://github.com/ortus-boxlang/bx-esapi)
for HTML-encoding, [bx-yaml](https://github.com/ortus-boxlang/bx-yaml) for
reading `bxsites.yaml`, and [bx-image](https://github.com/ortus-boxlang/bx-image)
for the responsive-image pipeline (see [Responsive Images](guides/images.md)) -
all four are installed automatically as `box.json` dependencies, so
installing `bx-sites` itself is the only command needed, either via
BoxLang's own OS binary installer:

```bash frame="terminal" title="Terminal"
install-bx-module bx-sites
```

or via [CommandBox](https://commandbox.ortusbooks.com/):

```bash frame="terminal" title="Terminal"
box install bx-sites
```

Either one reads `box.json`'s `boxlang.executable`
and drops a `bxSites` script on your `PATH` (in `~/.boxlang/bin`), so every
command below works either as a short standalone command:

```bash title="Usage"
bxSites <verb> [options]
```

or, everywhere BoxLang is available but that `PATH` shim isn't (a CI
runner, a module registered by hand rather than installed) - both forms
run the exact same thing:

```bash title="Usage (no PATH shim)"
boxlang bxSites <verb> [options]
```

The rest of this guide uses the short form.

## Scaffold a project

```bash frame="terminal" title="Terminal" linenums="1"
bxSites new my-docs
cd my-docs
```

This creates:

```text title="Project structure"
my-docs/
├── docs/
│   ├── assets/
│   └── index.md
└── bxsites.yaml
```

Pass `--theme=material` or `--theme=tailwind` to scaffold with a different
default theme, and `--name="My Project Docs"` to set the site name up
front - otherwise `new` derives it from the target directory name.

### Config file format

`bxsites.yaml` is the default and preferred format - it's what `new`
scaffolds unless told otherwise, and every example in this guide and in
[Configuration](configuration.md) shows it first. `bxsites.toml` and
`bxsites.json` are fully supported too: pass `--format=toml` or
`--format=json` to scaffold one instead, or just hand-write/rename one
yourself. ConfigLoader resolves whichever of `bxsites.yaml`/`.yml`/`.toml`/
`.json` is actually present, in that order, with no other config needed to
switch. See [Configuration](configuration.md) for the full key reference in
all three formats.

Already have content in GitBook? `bxSites migrate --source=/path/to/export`
converts a GitBook export straight into `docs/` - see
[Migrating from GitBook](guides/migrating-from-gitbook.md) - and you can
skip ahead to [Build](#build).

## Add pages

Every `.md` file under `docs/` becomes a page. Folder nesting becomes nav
nesting automatically:

!!! note "docs/ or src/"
    `docs/` is what `new` scaffolds and what every example here uses, but a
    project that isn't really "docs" in spirit - a marketing site, a
    portfolio - can use `src/` instead, with zero other changes: every verb
    (`build`, `serve`, `site:check`, `lint`, `page:new`, ...) looks for `docs/`
    first and falls back to `src/` when that's what actually exists. Build
    output always lands in `site/` either way - the two never collide, since
    `site/` is never itself a valid source-folder name.

```text title="docs/ → nav"
docs/
├── index.md              -> /
├── guides/
│   ├── index.md          -> /guides/
│   └── deployment.md     -> /guides/deployment/
```

(A large site can override this inferred order/grouping entirely with an
explicit nav - see [`nav`](configuration.md#nav).)

### Linking between pages

Link to another page the normal mkdocs way - a file-relative path to its
`.md` source, exactly as if the two files were sitting next to each other
on disk (because they are):

```markdown title="Example link"
See [Deployment](guides/deployment.md) or, from that same guide,
[back to Getting Started](../getting-started.md#add-pages).
```

BxSites rewrites every such link to its built pretty-URL at build time
(`guides/deployment.md` -> `/guides/deployment/index.html`, anchors and
query strings preserved), resolved against the *linking* page's own
folder - `../` and sibling references work exactly like they would
resolving any other relative path. This is also why the link keeps
working if you read the file directly on GitHub instead of the built
site: it's a real, valid relative path to a real file either way. Absolute
URLs, `mailto:`, and links already starting with `/` are left untouched.

### Downloading a page as Markdown

Every built page also gets its own original `.md` source published
alongside it - `docs/guides/deployment.md` ends up copied to
`site/guides/deployment.md`, right next to
`site/guides/deployment/index.html` - with a "Download Markdown" link on
the page itself, next to "Edit this page". No config needed, always on.

This is the same motivation as [`llms.txt`](configuration.md#llmstxt) -
a person (or an LLM) can fetch a page's raw Markdown directly instead of
scraping rendered HTML - and since the whole `docs/` tree is mirrored 1:1,
a page's own relative links keep working read this way too.

Each page can start with a small frontmatter block:

```markdown title="docs/guides/deployment.md" linenums="1"
---
title: Deployment
order: 2
hidden: false
description: How to deploy a built BxSites site.
tags: [guides, deployment]
icon: 🚀
summary: Everything you need to publish a built site.
ogImage: assets/deployment-card.png
toc: true
---

# Deployment

Your content here.
```

- `title` - overrides the nav/page title (otherwise derived from the filename)
- `order` - controls sibling ordering in the nav (lower sorts first; omitted pages sort last, alphabetically)
- `hidden` - `true` excludes the page from the nav (and from search) without excluding it from the build
- `description` - this page's social-card/meta description (see
  [`ogImage`](configuration.md#ogimage)); when omitted, falls back to this
  page's own `summary` (below), then to a plain-text excerpt of the page's
  own rendered content, then - only if that content has no plain text of
  its own either - to the site-wide `description` in the site config
- `tags` - an array of tags for this page, rendered as clickable badges
  under the title and collected into a site-wide `/tags/` index page
  (only built at all once at least one page has tags); also boosts search
  relevance for matching queries
- `icon` - shown next to the page title and its nav entry - a plain emoji, or
  a named icon from a bundled library (`rocket`, `lucide:rocket`,
  `tabler:rocket`, or a project's own `custom:my-icon`) - see
  [Icons](guides/icons.md)
- `summary` - a one-line lead-in shown under the title (distinct from
  `description`, which is meta-tag-only and never rendered on the page itself)
- `ogImage` - overrides this one page's social-card image - see
  [`ogImage`](configuration.md#ogimage)
- `toc` - `false` hides this page's own "On this page" table of contents,
  even with 2+ headings (the usual trigger for it to render) - handy for a
  landing/hero page that doesn't want a floating TOC competing with its own
  content; defaults to `true`

Frontmatter values can be inline lists (`tags: [a, b, c]`), YAML-style block
lists (`tags:` followed by indented `- item` lines), or `>`/`|` block
scalars for a multi-line value - it's a small hand-rolled parser though, not
full YAML, so nested objects/maps aren't supported.

## Build

```bash frame="terminal" title="Terminal"
bxSites build
```

Renders every page in `docs/` into a static site in `site/`, ready to host
anywhere that serves static files.

## Serve locally

```bash frame="terminal" title="Terminal"
bxSites serve
```

Builds the project, serves `site/` at `http://127.0.0.1:8080/`, and
rebuilds automatically whenever you save a change under `docs/`, your
`bxsites.yaml`/`.json` site config, or a project-level `theme/` override -
your browser reloads on its own. Reacts to the save itself (a native
BoxLang file watcher, not a poll loop) and only reconverts the page(s) you
actually changed, reusing everything else from the last build - so saving
one page stays fast even on a large site. Pass `--port=3000` or
`--host=0.0.0.0` to change how it binds.

## Clean

```bash frame="terminal" title="Terminal"
bxSites clean
```

Removes `site/` and any build cache, without touching your `docs/` source.
