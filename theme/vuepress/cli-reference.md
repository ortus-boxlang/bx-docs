---
title: CLI Reference
order: 3
icon: phosphor-duotone:terminal-window
summary: Every bxSites verb and its flags.
tags: [reference, cli]
---

# CLI Reference

```bash title="Usage"
bxSites <verb> [options]
```

`box install bx-sites` drops a standalone `bxSites` script on your `PATH`
(via `box.json`'s `boxlang.executable`), so every verb below can be run
either that short way, or as `boxlang bxSites <verb>` - both run the
exact same thing; use the longer form anywhere the `PATH` shim isn't set
up (a CI runner, a module registered by hand):

```bash title="Usage (no PATH shim)"
boxlang bxSites <verb> [options]
```

Every verb accepts `--projectRoot=<path>` (or a bare positional path) to
target a project other than the current directory, and the two global
flags below can appear before any verb.

Every `docs/` mentioned below applies equally to a project using `src/`
instead - see [Getting Started](getting-started.md#add-pages) for the
`docs/`-or-`src/` convention. `new` always scaffolds `docs/`.

## Global options

| Flag | Description |
|---|---|
| `-v` | Show the module version and exit |
| `help`, or no verb at all | Show usage and exit |

`-h`, `--help`, and the long `--version` form are reserved by BoxLang's
own CLI itself (its own `--help`/`--version` handling short-circuits
before any module - not just bx-sites - ever sees them), so they never
reach bx-sites: `-h`/`--help` print BoxLang's own generic runtime help
instead of bx-sites' usage, and `--version` silently prints usage (not
the version) since BoxLang strips it from argv before dispatch. Use `-v`
or `help`/no-args instead - both are unaffected because neither is one of
BoxLang's own reserved global tokens.

Every verb below ships with core. An installed and activated addon module
can register further verbs of its own (e.g. a commercial deploy/hosting
addon adding `bxSites cloud publish`) - `help`/no-args lists whatever's
currently activated in your project alongside the verbs below. See
[CLI Providers](guides/cli-providers.md) if you're writing one.

## `new`

Scaffold a docs project.

```bash title="Usage"
bxSites new [path] [--name=...] [--theme=<see guides/themes.md for all 10>] [--description=...] [--format=yaml|toml|json]
```

- `--name` - the site name written into the site config (defaults to the target directory's name)
- `--theme` - defaults to `bootstrap`
- `--description` - the site description written into the site config
- `--format` - `yaml` (default, scaffolds `bxsites.yaml`) or `json` (scaffolds `bxsites.json`) - see [Configuration](configuration.md)

## `build`

Render `docs/**.md` into a static site in `site/`. Also builds the search
index (unless `search` is `false` in the site config, or `searchProvider` is
set to a provider - like `algolia`/`pagefind` - that doesn't use it, see
[Search](guides/search.md)), runs the `pagefind` CLI against the finished
`site/` when `searchProvider.provider` is `"pagefind"`, and copies theme +
`docs/assets/**` into `site/`.

```bash frame="terminal" title="Terminal"
bxSites build
```

## `serve`

Build and serve the site locally with live reload.

```bash title="Usage"
bxSites serve [--port=8080] [--host=127.0.0.1]
```

Runs in the foreground until interrupted (Ctrl+C). A native BoxLang file
watcher - not a poll loop - reacts to a saved change immediately, and only
reconverts the page(s) that actually changed rather than the whole site,
so the save-and-reload loop stays fast.

## `search-index`

Rebuild `site/search-index.json` standalone, without re-rendering pages or
copying assets. `build` already runs this same step automatically - this
verb exists for when you only need to refresh the index. Only ever covers
the main `docs/` tree, even on a project with `docs/versions/`/`docs/i18n/`
- a real `build` writes each tree's own scoped index instead (see
[Versioning](guides/versioning.md#whats-out-of-scope-for-now)).

```bash frame="terminal" title="Terminal"
bxSites search-index
```

## `clean`

Remove `site/` and any build cache, leaving `docs/` and the site config alone.

```bash frame="terminal" title="Terminal"
bxSites clean
```

## `gh-deploy`

Builds the site, then force-pushes it to a `gh-pages`-style branch - one
commit per deploy, no accumulated history on that branch, matching mkdocs'
own `mkdocs gh-deploy` convention. Requires the project to be a git
repository with a configured remote; never touches your own current branch
or working tree (it does the push from a throwaway `git worktree`).

```bash title="Usage"
bxSites gh-deploy [--branch=gh-pages] [--remote=origin] [--message="..."]
```

- `--branch` - defaults to `gh-pages`
- `--remote` - defaults to `origin`
- `--message` - the branch's single commit message, defaults to `"Deploy site via bxSites gh-deploy"`

See [Deployment](guides/deployment.md) for the full GitHub Pages setup
(enabling Pages for the branch, `baseURL`, etc.).

## `deploy`

Builds the site, then ships it to a real deployment target - S3 (and any
S3-compatible service - DigitalOcean Spaces, Cloudflare R2, Backblaze B2,
MinIO), Azure Blob Storage, Google Cloud Storage, Firebase Hosting, FTP,
SFTP, rsync-over-SSH, Netlify, Vercel, Cloudflare Pages, a local directory,
or GitHub Pages (the same push `gh-deploy` does, just reachable from this
one unified command too).

```bash title="Usage"
bxSites deploy --entry=<name> [--verbose]
bxSites deploy [--target=local|github-pages] [target-specific flags] [--verbose]
bxSites deploy [--verbose]
```

Three ways to invoke it:

1. **`--entry=<name>`** - dispatches to whatever target a
   `deployments/<name>.json` file declares (see below). Every target
   except `local`/`github-pages` needs this - there's more configuration
   than a couple of flags can reasonably carry.
2. **`--target=<name>` with its own flags** - a flag-only shorthand for
   the two simplest targets, needing no `deployments/` folder at all:
   `local` (`--destination=<path>`) and `github-pages` (`[--branch]
   [--remote] [--message]`, every field optional, same defaults as
   `gh-deploy`).
3. **Neither flag - deploy everything.** Every `deployments/*.json` entry
   is deployed in turn, off a single shared build (the site is built once,
   not once per target). Requires at least one `deployments/*.json` entry
   to exist. One target failing doesn't stop the rest - every entry is
   attempted, and the command only exits non-zero if at least one of them
   failed; the final summary reports how many succeeded (e.g. `Deployed to
   2/3 target(s) (1 failed)`).

`--verbose` prints a progress line as the build starts/finishes and as each
target starts/finishes, instead of just the final one-line summary.

See [Deployment](guides/deployment.md) for every target's own config
shape and a real `deployments/*.json` example for each.

## `publish`

Builds the site, then ships it to [bxSites Cloud](guides/deployment.md#the-publish-command)
- the hosting SaaS this module targets - over its publish API. Distinct
from `deploy`'s pluggable targets (which ship to infrastructure *you* own):
`publish` always ships to bxSites Cloud, identified by
[`cloud.siteId`/`cloud.apiUrl`](configuration.md#cloud) in `bxsites.yaml`.

```bash title="Usage"
bxSites publish [--token=<token>]
```

- `--token` - a bxSites Cloud API token, overriding the `BXSITES_CLOUD_TOKEN`
  environment variable when both are set. One of the two is required - the
  token is never read from `bxsites.yaml` itself.

On success, prints the published site's live URL. Fails with a clear error
(not a raw exception) when: the `[cloud]` block is missing/incomplete in
`bxsites.yaml`; no token is available from either source; the token is
rejected (401/403 - check it's valid and has access to this site); the
configured `[cloud.siteId]` isn't found (404 - check it's correct); or any
other server/network failure (the actual HTTP status and response body are
included).

See [Deployment](guides/deployment.md#the-publish-command) for the full
setup.

## `package`

Builds the site, then zips it into a single distributable archive - a
plain zip whose root is the built site's own contents (not a wrapping
`site/` folder), ready to attach to a release or hand to any host that
only accepts a zip upload.

```bash title="Usage"
bxSites package [--output=<path>]
```

`--output` defaults to `<projectRoot>/site.zip` (a relative value is
resolved against the project root); a nested destination's parent
directories are created automatically.

## `migrate`

Converts an existing docs project into this one - `--from` picks the
source format: `gitbook` (the default), `mkdocs`, `markdown-zip`, or `notion`.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites migrate --source=/path/to/gitbook-export
bxSites migrate --source=/path/to/mkdocs-project --from=mkdocs
bxSites migrate --source=/path/to/export.zip --from=markdown-zip
bxSites migrate --source=/path/to/notion-export --from=notion
```

- `--source` (required) - path to the export/project's root directory (`SUMMARY.md` for `gitbook`, `mkdocs.yml` for `mkdocs`), or a `.zip` file (`markdown-zip`; `notion` accepts either a `.zip` or an already-extracted folder)
- `--from` - `gitbook` (default), `mkdocs`, `markdown-zip`, or `notion`

### `--from=gitbook` (default)

A GitBook export - a `SUMMARY.md` table of contents plus its `.md` files,
GitBook's own on-disk sync format - into this project's `docs/` tree:
`SUMMARY.md` becomes `docs/nav.json`, `{% block %}` syntax becomes its
bx-sites equivalent (`::: name` directives, or the native `=== "Title"`
tabs / `!!! type` admonition syntax where a closer match already exists -
see [Content Blocks](guides/content-blocks.md)),
`README.md` files become `index.md`, and `.gitbook/assets/**` is copied to
`docs/assets/gitbook/`.

### `--from=mkdocs`

An mkdocs project - `mkdocs.yml` plus its `docs/` folder - into a
complete bx-sites project: `mkdocs.yml` becomes `bxsites.yaml` +
`docs/nav.json`, and every page is copied across largely unchanged, since
mkdocs-material's own admonition/tabs/math/code-annotation syntax already
*is* bx-sites' own native syntax - see
[Migrating from mkdocs](guides/migrating-from-mkdocs.md). Non-`.md` assets
(images commonly sitting next to the page that uses them, mkdocs has no
single asset-folder convention) are relocated to `docs/assets/mkdocs/` and
their references rewritten.

### `--from=markdown-zip`

A plain `.zip` of Markdown files - no proprietary export format to
translate, since a folder's own nesting already *is* bx-sites' own nav
convention and a page-to-page relative `.md` link already resolves the
way bx-sites expects. Mostly a straight copy: every non-`.md` file (an
image, say) is relocated to `docs/assets/imported/` and every page's own
reference to it rewritten to match. No `bxsites.yaml`/`docs/nav.json` is
written - a plain zip carries no site name or nav structure of its own to
translate.

### `--from=notion`

A Notion "Export as Markdown & CSV" archive (a `.zip`, or an
already-extracted folder) - handles Notion's own two quirks nothing else
here migrates from has: every page/sub-page folder is suffixed with a
space and a 32-character id (disambiguating same-titled pages, never
meant to be read), and a page's title is repeated as a literal leading
`# Heading` rather than carried in frontmatter. Both are cleaned up: the
id suffix is stripped and the remaining name slugified for the output
filename, the leading heading becomes a real `title` frontmatter field
instead of a duplicate first line, and every link/image target (which
Notion writes URL-encoded, still pointing at the original id-suffixed
names) is rewritten to match. Non-`.md` files are relocated to
`docs/assets/imported/`, same as `markdown-zip` above.

### All four

Prints a summary of pages (and, for mkdocs/markdown-zip/notion, assets)
converted and, when anything couldn't be auto-converted, a list of
exactly what needs a manual look - nothing is silently dropped. A
destination file, `bxsites.yaml`, or `docs/nav.json` that already exists
is overwritten (also reported), so review the migrated output before
committing it.

## `audit`

A CI-grade content quality gate over an already-built `site/` - run `build`
first. Checks for:

- **Broken internal links/images** - any `<a href>`/`<img src>` pointing at
  a page or asset that doesn't exist in `site/`. Fails the check.
- **Missing alt text** - any `<img>` with no `alt` attribute at all. An
  empty `alt=""` (the correct markup for a purely decorative image) is not
  flagged. Fails the check.
- **Orphaned pages** - pages that exist in `site/` but aren't reachable by
  following links from any tree's own homepage (the main site's
  `index.html`, and each version's/locale's own). Informational only -
  never fails the check, since a page a project deliberately left out of
  its own nav (e.g. frontmatter `hidden: true`) is *supposed* to only be
  reachable by a direct link.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites build
bxSites audit
```

Exits `1` when there are any broken links/images or missing-alt images,
`0` otherwise (orphaned pages never affect the exit code). Deliberately
internal-links-only - it does not make HTTP requests to check external
URLs, which belongs in a dedicated link-checking tool run as its own job.

## `stats`

A read-only summary report of an already-built `site/` - run `build`
first. Reports:

- **Pages and words** - total page count and a rough word count (tags
  stripped, same "good enough for an estimate" standard as the blog's own
  reading-time figure), plus a per-tree breakdown once there's more than
  one tree (a version, or a non-default locale).
- **Versions and locales** - names of every `docs/versions/`/non-default
  `docs/i18n/` folder.
- **Blog** - post/category/author/year-active counts, straight off
  `site/blog/`'s own folder shape (so it always matches what was actually
  published, drafts excluded) - `none` when there's no blog.
- **Tags** - the number of distinct tags across the whole site.
- **Search index** - entry count and file size of `search-index.json`, or
  `none` when search is off or a non-local provider is active.
- **Site output** - total file count and on-disk size of the built `site/`.

```bash
bxSites build
bxSites stats
```

Always exits `0` - purely informational, nothing here is a pass/fail gate
(that's `audit`'s job).

## `doctor`

A one-shot environment/config health check - the "run this before filing a
bug report" verb. Checks the JVM version, that `docs/` exists, that
`bxsites.yaml`/`.json` actually parses and validates, that the required
BoxLang modules (`bx-markdown`, `bx-esapi`, `bx-yaml`, `bx-image`) are
installed and activated, and - if a project-level `theme/` override
exists - that it satisfies the two-required-file `layout.bxm`/`page.bxm`
contract.

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Exits `1` if any check fails, `0` otherwise. Nothing here mutates a
project - purely diagnostic.

## `post:new`

Scaffold a new blog post at `docs/blog/posts/<slug>.md`.

```bash title="Usage"
bxSites post:new --title="My New Post" [--slug=...] [--date=...] [--authors=...] [--categories=...] [--tags=...] [--draft]
```

- `--title` (required) - also becomes the post's frontmatter `title`
- `--slug` - defaults to a slugified `--title`
- `--date` - defaults to today (`yyyy-MM-dd`)
- `--authors`, `--categories`, `--tags` - comma-separated
- `--draft` - defaults to `true` (pass `--!draft` to publish immediately)

See [Blog](guides/blog.md) for the full frontmatter reference.

## `version:new`

Snapshot the current `docs/` tree into `docs/versions/<name>/`, excluding
`assets/`, `versions/`, `i18n/`, and `blog/` (each is its own separately
loaded tree, not part of the snapshot).

```bash title="Usage"
bxSites version:new --name=1.0
```

- `--name` (required) - the version folder/label, e.g. `1.0`

See [Configuration's "Versioning" section](configuration.md#versioning).

## `i18n:status`

Reports per-locale translation coverage - for every configured locale,
how many of the default tree's pages exist (at the same relative path)
under `docs/i18n/<code>/`, and which ones are still missing.

```bash frame="terminal" title="Terminal"
bxSites i18n:status
```

Always exits `0` - purely informational.

## `i18n:new`

Scaffold a new `docs/i18n/<code>/` locale folder, seeding an `index.md`
copied from the default locale's own `index.md` when one exists.

```bash title="Usage"
bxSites i18n:new --code=es
```

- `--code` (required) - the locale code, e.g. `es`, `fr`, `pt-BR`

See [Internationalization](guides/i18n.md) for wiring the new locale into
`bxsites.yaml`'s `i18n.locales`.

## `page:new`

Scaffold a single docs page at an arbitrary path under `docs/`, with the
requested frontmatter already filled in.

```bash title="Usage"
bxSites page:new --path=guides/setup.md [--title=...] [--description=...] [--icon=...] [--tags=...] [--order=...]
```

- `--path` (required) - `docs/`-relative, must end in `.md`
- `--title`, `--description`, `--icon`, `--order` - written into frontmatter
- `--tags` - comma-separated

## `plugin:new`

Scaffold a plugin module skeleton (`box.json`, `ModuleConfig.bx`, a
`models/BxSitesPlugin.bx` with every hook stubbed out) mirroring
`examples/hello-plugin/`.

```bash title="Usage"
bxSites plugin:new --name=my-analytics-plugin [--dest=...]
```

- `--name` (required) - the plugin's module name/slug
- `--dest` - defaults to `<projectRoot>/<name>`

See [Plugins](guides/plugins.md) for the hook reference and how to wire the
finished plugin into `bxsites.yaml`'s `plugins` array.

## `install:plugin`

Download a published plugin from ForgeBox and drop it straight into the
project's own `boxlang_modules/` - BoxLang's own auto-loaded local-module
convention, so nothing beyond the `bxSites` binary itself is needed (no
`box`/CommandBox involved).

```bash title="Usage"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

- `--name` (required) - the ForgeBox slug to install
- `--version` - a specific version; omit for the latest

Prints the module's real registered mapping name once loaded - add that
name to `bxsites.yaml`'s `plugins` array to activate it (installing alone
never activates a plugin - see [Plugins](guides/plugins.md)).

## `theme:new`

Eject one of the built-in themes into the project's own `theme/` folder
for customizing, matching mkdocs' `--theme` eject workflow.

```bash title="Usage"
bxSites theme:new --theme=material
```

- `--theme` (required) - `bootstrap`, `material`, `tailwind`, `docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook`, or `notion` - see [Themes](guides/themes.md#built-in)

Fails rather than overwriting an existing `theme/`. See
[Themes](guides/themes.md) for the override contract (`layout.bxm` +
`page.bxm`).

## `install:theme`

Download a published theme from ForgeBox into the project's own
`themes/<name>/` - nothing but the `bxSites` binary needed, same as
`install:plugin`.

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

- `--name` (required) - the ForgeBox slug to install
- `--version` - a specific version; omit for the latest

Validates the downloaded package against the `ThemeProvider` contract
(`layout.bxm` + `page.bxm`) before finishing, so a broken package fails at
install time rather than at the next `build`. Set `bxsites.yaml`'s
`theme.name` to the installed name to use it - see
[Themes](guides/themes.md#installing-a-published-theme).

## `skills:install`

Install the [official AI agent skill pack](guides/ai-agent-skills.md)
(`ortus-boxlang/bx-sites-skills`) into every AI coding assistant this
project has configured - a thin wrapper over `npx skills add`, so a
freshly-scaffolded project's assistant knows bx-sites from the first
prompt. Also runnable as the two-word `bxSites skills install` - every
colon-joined verb doubles as its own space-separated form this way.

```bash title="Usage"
bxSites skills:install [--skill=<name>]
```

- `--skill` - install just this one skill (its bare name, e.g.
  `bx-sites-deployment`) instead of the full thirteen-skill set

Requires Node.js/`npx` on `PATH`. See
[AI Agent Skills](guides/ai-agent-skills.md) for what a skill is, the full
skill list, and the other two install paths (`npx skills add`/`coldbox ai
skills install`) that don't need `bxSites` at all.

## `theme:import`

Best-effort conversion of a theme from another static site generator's
ecosystem (`mkdocs`/`jekyll`/`hugo`) into a bx-sites theme scaffold under
`themes/<name>/` - a starting point, not a lossless one-command port.

```bash title="Usage"
bxSites theme:import --source=mkdocs --path=/path/to/theme --name=my-imported-theme
```

- `--source` (required) - `mkdocs`, `jekyll`, or `hugo`
- `--path` (required) - the source theme's own root folder
- `--name` (required) - the destination name, written to `themes/<name>/`

Safe to re-run against the same `--name` - `layout.bxm`/`page.bxm` are
overwritten and any newly-found asset folders merged in. See
[Importing a theme](guides/theme-import.md) for exactly what does and
doesn't get translated, and what to check afterward.

## `page:rename`

Move a docs page from one path to another, rewriting every relative
Markdown link across `docs/**` that pointed at the old path - the same
file-relative link-rot problem the built HTML side already solves
(`audit`), applied to raw Markdown source at rename time instead.

```bash title="Usage"
bxSites page:rename --from=guides/old-name.md --to=guides/new-name.md
```

- `--from` (required) - the page's current `docs/`-relative path
- `--to` (required) - its new `docs/`-relative path

Only bare `[text](relative/path.md)`-style links are rewritten - absolute
URLs, `mailto:`, and pure in-page anchors are left alone. `docs/assets/**`
is never scanned.

Also stamps the moved page's own frontmatter `redirect_from` with its old
URL, so a build ([Redirects](guides/redirects.md)) keeps answering for it
instead of letting the rename 404 every outside link this project doesn't
control the source of.

## `blog:drafts`

Lists every blog post whose frontmatter sets `draft: true` - `build`
always skips drafts, so this is the only place their existence is
surfaced.

```bash frame="terminal" title="Terminal"
bxSites blog:drafts
```

Always exits `0`.

## `blog:find`

Filters blog posts by author/category/tag/date range, without running a
full `build`.

```bash title="Usage"
bxSites blog:find [--author=...] [--category=...] [--tag=...] [--since=...] [--until=...] [--drafts]
```

- `--author`, `--category`, `--tag` - case-insensitive exact match against any of the post's own values
- `--since`, `--until` - a date; only posts on/after `--since` and/or on/before `--until` match
- `--drafts` - include draft posts too (excluded by default)

Every filter is optional and independent - passing none lists every
published post.

## `search:query`

Runs a keyword query against an already-built `site/search-index.json` -
run `build` or `search-index` first. Ranks results using the same
relative field weighting the client-side search widget uses (title,
then tags, then headings, then body), so you can sanity-check what a
real visitor's search would surface without opening a browser.

```bash title="Usage"
bxSites search:query --query="getting started" [--limit=10]
```

- `--query` (required) - space-separated search terms
- `--limit` - maximum results to return, defaults to `10`

## `lint`

A pre-build content quality pass over raw `docs/` Markdown source,
distinct from `audit` (which only inspects an already-built `site/`).
Checks for:

- **Heading level skips** - a page body jumping straight from `##` to
  `####` with no `###` in between (confusing structure, and bad for
  accessibility). Lines inside a fenced code block are never mistaken for
  headings.
- **Blog post date issues** - a `docs/blog/posts/**` post with a
  missing or invalid frontmatter `date` (`build` itself throws on this the
  moment it loads posts - `lint` surfaces it as a finding instead).

```bash frame="terminal" title="Terminal"
bxSites lint
```

Exits `1` when either check finds anything, `0` otherwise.

## `docbox`

Generates a BoxLang/CFML API reference into the content directory from
[DocBox](https://docbox.ortusbooks.com)' own JSON output - the BoxLang
counterpart of the Java plugins' Javadoc generator. See
[DocBox API Reference](guides/docbox.md) for the full walkthrough and
[`docbox`](configuration.md#docbox) for the config block.

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites docbox --mappings:models=models --projectTitle="My API"
bxSites docbox --pagePathPrefix=api/classes --tags=api,classes
bxSites docbox --jsonDir=build/docbox-json
```

| Flag | Purpose |
|---|---|
| `--mappings:<name>=<path>` | A source tree to document, repeatable. Overrides `docbox.mappings` |
| `--projectTitle` | Title on the generated overview page |
| `--excludes` | A regex of paths for DocBox to skip |
| `--pagePathPrefix` | Where pages go, relative to the content dir |
| `--tags` | Comma-separated frontmatter tags for every generated page |
| `--jsonDir` | Keep DocBox's JSON output at this path instead of discarding it |

With no mappings configured or passed, it documents whichever conventional
folders the project has: `models`, `handlers`, `bifs`, `components`,
`interceptors`. Needs the `bx-docbox` module installed
(`install-bx-module bx-docbox`), and says so plainly when it isn't.

## `coldbox`

Documents a ColdBox application from its conventions on disk - routes,
handlers, models and WireBox mappings, modules, interceptors and scheduled
tasks - without booting it. See
[ColdBox Applications](guides/coldbox.md) for the full walkthrough and
[`coldbox`](configuration.md#coldbox) for the config block.

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites coldbox --appRoot=app
bxSites coldbox --include=routes,handlers
```

| Flag | Purpose |
|---|---|
| `--appRoot` | Where the ColdBox app lives, relative to the project root |
| `--pagePathPrefix` | Where pages go, relative to the content dir |
| `--tags` | Comma-separated frontmatter tags for every generated page |
| `--include` | Which page sets to generate: `routes`, `handlers`, `models`, `modules`, `interceptors`, `scheduler` |

Fails with `BxSites.NotAColdBoxApp` when the resolved root holds no
`handlers/`, `config/ColdBox`, `config/Router` or `modules_app/`.
Per-class detail (a handler's actions, a model's methods) comes from
DocBox, so `bx-docbox` makes those pages richer; without it every page is
still generated and says what's missing.
