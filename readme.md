# ⚡︎ BX Sites

```
|:------------------------------------------------------:|
| ⚡︎ B o x L a n g ⚡︎
| Dynamic : Modular : Productive |
| :----------------------------: |
```

<blockquote>
	Copyright Since 2023 by Ortus Solutions, Corp
	<br>
	<a href="https://www.boxlang.io">www.boxlang.io</a> |
	<a href="https://www.ortussolutions.com">www.ortussolutions.com</a>
</blockquote>

<p>&nbsp;</p>

Write your content in Markdown. BX Sites turns it into a complete, themed, searchable website - ready to deploy.

It's a general-purpose static site generator, not just for documentation - docs, a blog, a marketing site, a knowledge base, anything Markdown can express, all built the same way, with the same themes, search and i18n. Built in BoxLang on top of [bx-markdown](https://github.com/ortus-boxlang/bx-markdown), in the spirit of [mkdocs](https://www.mkdocs.org/): point it at a `docs/` folder (or `src/`, if that reads better for your project) and get a themed, searchable static site out. Already on GitBook, mkdocs, or Notion, or just have a folder of Markdown? `bxSites migrate --from=gitbook|mkdocs|markdown-zip|notion` converts it straight into a bx-sites project - see [Migrating from GitBook](docs/guides/migrating-from-gitbook.md) or [Migrating from mkdocs](docs/guides/migrating-from-mkdocs.md).

----

## Features

- **Ten built-in themes** - `bootstrap` (default), `material`, `tailwind`, and seven more inspired by Docsy, Stripe, Docusaurus, Just the Docs, VuePress, GitBook and Notion, all overridable via a project's own `theme/` folder. See [Themes](docs/guides/themes.md).
- **Static, client-side search** - a MiniSearch-powered index (fuzzy matching, prefix search) with a Cmd/Ctrl+K command palette, or wire up Algolia DocSearch/Pagefind/your own provider instead. See [Search](docs/guides/search.md).
- **A rich Markdown+ toolkit** - GitBook-style content blocks (cards, tabs, expandables, buttons, prompts, conditional/audience-switched content), always-on GFM tables with a sticky-header/responsive-scroll treatment, an embeddable OpenAPI/Swagger widget, admonitions, footnotes, math and Mermaid diagrams. See [Content Blocks](docs/guides/content-blocks.md) and [Markdown Extensions](docs/guides/markdown.md).
- **Reusable `{{ variables }}` and magic functions** - reference `bxsites.yaml`-defined values or call a small BoxLang helper function straight from Markdown, no plugin needed. See [Variables & Magic Functions](docs/guides/variables-and-functions.md).
- **A blog, versioning, and i18n**, all by convention - `docs/blog/posts/` for authors/categories/RSS/drafts, `version:new` to snapshot a release, `docs/i18n/<code>/` to translate. See [Blog](docs/guides/blog.md), [Versioning](docs/guides/versioning.md), [i18n](docs/guides/i18n.md).
- **Ship it anywhere** - `bxSites deploy` to S3 (and any S3-compatible service), Azure Blob Storage, GCS, Firebase Hosting, FTP/SFTP, rsync, Netlify, Vercel, Cloudflare Pages, a local directory, or GitHub Pages - one shared build, every `deployments/*.json` target at once if you want, `bxSites publish` to [bxSites Cloud](https://bxsites.io) itself, or `bxSites package` for a plain zip instead. See [Deployment](docs/guides/deployment.md).
- **A real plugin/theme system** - a plugin is just another BoxLang module; `install:plugin`/`install:theme` pull published ones from ForgeBox, and `theme:import` converts an existing mkdocs/jekyll/hugo theme into a starting point. See [Plugins](docs/guides/plugins.md).
- **AI agent skills** - `bxSites skills:install` (or `npx skills add ortus-boxlang/bx-sites-skills`, or `coldbox ai skills install`) teaches Claude Code, Cursor, Codex, and other AI coding assistants bx-sites' own conventions - content blocks, config keys, deployment, and more. See [AI Agent Skills](docs/guides/ai-agent-skills.md).
- **SEO and correctness out of the box** - `sitemap.xml`, `robots.txt` (or your own hand-authored one), canonical links, `llms.txt`, and redirects that keep a moved/renamed page's old URL working instead of 404ing.
- **Fast and air-gapped by default** - fingerprinted CSS/JS bundling and responsive images, with Bootstrap, highlight.js, Alpine.js, MiniSearch and (opt-in) Mermaid all vendored locally - zero outbound requests from a built site.

## Quick Start

Needs the [BoxLang](https://boxlang.io) runtime installed first - either
the quick installer (`curl -fsSL https://install.boxlang.io/ | bash`) or
[BVM](https://boxlang.ortusbooks.com/getting-started/installation/boxlang-version-manager-bvm)
(`curl -fsSL https://install-bvm.boxlang.io/ | bash`). See
[Getting Started](docs/getting-started.md#prerequisite-install-boxlang) for
details.

```bash
# OS Binary
install-bx-module bx-sites

# CommandBox
box install bx-sites

# Scaffold a new docs project (docs/ + bxsites.yaml)
bxSites new my-docs
cd my-docs

# Build the static site to site/
bxSites build

# Or build and serve locally with live reload while you write
bxSites serve
```

See [Getting Started](docs/getting-started.md) for the full walkthrough.

## Usage

`box install` drops a standalone `bxSites` script on your `PATH` (via
`box.json`'s `boxlang.executable`), so every verb can be run either that
short way, or as `boxlang bxSites <verb>` - both run the exact same
thing; use the longer form anywhere the `PATH` shim isn't set up (a CI
runner, a module registered by hand):

```bash
bxSites <verb> [options]
# or, equivalently:
boxlang bxSites <verb> [options]
```

| Verb | Purpose |
|---|---|
| `new` | Scaffold a docs project (`docs/` + `bxsites.yaml`, defaulting to the `bootstrap` theme; `--format=json` for `bxsites.json` instead) |
| `build` | Render `docs/**.md` into a static site in `site/`, including the search index, `sitemap.xml`, `llms.txt` and assets. Needs bx-markdown, bx-esapi, bx-yaml and bx-image installed |
| `serve` | Build and serve the site locally with live reload (needs bx-markdown, bx-esapi, bx-yaml and bx-image too) |
| `search-index` | Rebuild `site/search-index.json` standalone (also runs automatically during `build`) |
| `clean` | Remove `site/` and any build cache |
| `migrate` | Convert an existing GitBook export, mkdocs project, plain zip of Markdown, or Notion export into `docs/` + `nav.json` (`--from=gitbook`, the default, `--from=mkdocs`, `--from=markdown-zip`, or `--from=notion`) |
| `check` | CI-grade content check on a built `site/`: broken internal links/images, missing alt text, orphaned pages |
| `stats` | Read-only summary report on a built `site/`: page/word counts, versions/locales, blog, tags, search index, site size |
| `doctor` | Environment/config health check: JVM, `docs/` (or `src/`), config validity, required modules, theme override |
| `post:new` | Scaffold a new blog post at `docs/blog/posts/<slug>.md` |
| `version:new` | Snapshot `docs/` into a new `docs/versions/<name>/` |
| `i18n:status` | Per-locale translation coverage report against the default tree |
| `i18n:new` | Scaffold a new `docs/i18n/<code>/` locale |
| `page:new` | Scaffold a single docs page at an arbitrary path |
| `plugin:new` | Scaffold a plugin module skeleton |
| `install:plugin` | Download a plugin from ForgeBox into project-local `boxlang_modules/` |
| `theme:new` | Eject a built-in theme into project `theme/` for customizing |
| `install:theme` | Download a theme from ForgeBox into project-local `themes/<name>/` |
| `skills:install` | Install the [official AI agent skill pack](docs/guides/ai-agent-skills.md) (`ortus-boxlang/bx-sites-skills`) via `npx skills add` |
| `theme:import` | Best-effort convert a mkdocs/jekyll/hugo theme's own template files into a `themes/<name>/` scaffold |
| `page:rename` | Move a docs page and rewrite every relative link that pointed at it |
| `blog:drafts` | List every blog post whose frontmatter sets `draft: true` |
| `blog:find` | Filter blog posts by author/category/tag/date range |
| `search:query` | Query a built `search-index.json` and rank results |
| `lint` | Pre-build content checks: heading level skips, blog posts missing a valid date |
| `gh-deploy` | Build and force-push `site/` to a `gh-pages`-style branch (`--branch`, `--remote`, `--message`) |
| `deploy` | Build and ship `site/` to a real target (s3, azure, gcs, firebase, ftp, sftp, rsync, netlify, vercel, cloudflare-pages, local, github-pages); no args deploys every `deployments/*.json` entry; `--verbose` |
| `publish` | Build and publish `site/` to [bxSites Cloud](https://bxsites.io) via `cloud.siteId`/`cloud.apiUrl` in `bxsites.yaml` (`--token`, overrides the `BXSITES_CLOUD_TOKEN` env var) |
| `package` | Build and zip `site/` into a single archive (`--output=<path>`, defaults to `site.zip`) |

Every verb accepts `--projectRoot=<path>` (or a bare positional path) to target a project other than the current directory. Run `bxSites --help` for full usage.

## Documentation

This repository documents itself with BX Sites - see `bxsites.yaml` and `docs/` at the repo root, published at:

- **[bxsites.io](https://bxsites.io/)** - the latest released version (`bxsites.yaml`'s own `versions.default`)
- **[bxsites.io/next](https://bxsites.io/next/)** - unreleased docs, currently being written toward the next release

Both are built with all ten built-in themes side by side (`bootstrap` at the root, every other theme under `theme/<name>/`) - this doubles as a theme gallery - use the switcher next to the dark-mode toggle to flip between them on any page. CI builds each theme as its own parallel job (see `.github/workflows/pages.yml`); `./buildMultiTheme.sh` reproduces the same gallery layout locally for a quick preview.

Or read the source directly:

- [Getting Started](docs/getting-started.md)
- [CLI Reference](docs/cli-reference.md)
- [Configuration](docs/configuration.md) - the full site config reference (`bxsites.yaml`, the default, or `bxsites.json`)
- Guides: [Themes](docs/guides/themes.md) · [Search](docs/guides/search.md) · [Markdown Extensions](docs/guides/markdown.md) · [Content Blocks](docs/guides/content-blocks.md) · [Tables](docs/guides/tables.md) · [OpenAPI/Swagger](docs/guides/openapi.md) · [Variables & Magic Functions](docs/guides/variables-and-functions.md) · [Blog](docs/guides/blog.md) · [Versioning](docs/guides/versioning.md) · [i18n](docs/guides/i18n.md) · [Deployment](docs/guides/deployment.md) · [Plugins](docs/guides/plugins.md) · [Migrating from GitBook](docs/guides/migrating-from-gitbook.md) - see the [Guides index](docs/guides/index.md) for the rest
- [Releases](docs/releases/index.md) - versioning policy and what's new per release

See [MODULE_SPEC.md](MODULE_SPEC.md) for the design spec driving this module's development.

## Directory Structure

- `.github/workflows` - CI: tests (`tests.yml`), PR checks (`pr.yml`), snapshot/release builds (`snapshot.yml`, `release.yml`), and publishing this repo's own docs to GitHub Pages (`pages.yml`)
- `models` - the module's own source: `models/cli` (one dispatcher per `bxSites` verb), `models/config` (site config loader/validator - `bxsites.yaml`/`.yml`/`.json`), `models/build` (project scaffolding + the docs/nav/markdown/theme/search/sitemap build pipeline), `models/deploy` (the `deploy` verb's pluggable targets - S3, Azure, GCS, Firebase, FTP/SFTP, rsync, Netlify, Vercel, Cloudflare Pages, local, GitHub Pages), `models/publish` (the `publish` verb's own bxSites Cloud client)
- `resources/themes` - built-in themes (native BoxLang `.bxm` templates + assets), ten in total: `bootstrap` (default), `material`, `tailwind`, plus seven `material`-forked themes (`docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook`, `notion`) - all with the BoxLang brand palette, dark mode, breadcrumbs and code-copy buttons applied out of the box. A project can override any of them via its own `theme/` folder (same `layout.bxm` + `page.bxm` contract - see [Themes](docs/guides/themes.md))
- `resources/assets` - module-wide shared client-side assets: the search widget (`search.js`, MiniSearch-backed with a Cmd/Ctrl+K palette), the copy-code button, and per-feature init scripts (tabs, Mermaid, math, OpenAPI, prompts, conditional content, ...), plus every vendored third-party library under `vendor/` (Bootstrap, highlight.js, Alpine.js, MiniSearch, Mermaid, Swagger UI) - no CDN, no outbound requests from a built site
- `docs` / `bxsites.yaml` - this repository's own docs, built by BX Sites itself (`boxlang bxSites build`)
- `tests/specs` - TestBox specs, one bundle per class under `models/`
- `box.json` - package metadata used to publish to ForgeBox
- `ModuleConfig.bx` - this module's configuration/CLI entry point

## Local Building

`Build.bx` packages this module for distribution to ForgeBox: it produces a zip in `build/` containing everything needed to run the module (`box.json`, `ModuleConfig.bx`, and the rest of the module's own files).

It also produces a second, self-contained artifact - `build/artifacts/bx-sites-<version>-with-deps.zip` - that additionally bundles every runtime dependency from `box.json`'s `dependencies` block (`bx-markdown`, `bx-esapi`, `bx-yaml`, `bx-image`, `bx-ftp`, `bx-toml`) inside a `modules/` folder alongside the module itself, using BoxLang's [module inception](https://boxlang.ortusbooks.com/boxlang-framework/module-development/module-inception): a module's own `modules/` folder is discovered and activated before the module itself, so this artifact needs nothing pre-installed to run standalone - just drop it into a `modules/` (or `boxlang_modules/`) folder on its own. The primary `bx-sites-<version>.zip` artifact - and the `box forgebox publish` step, which publishes from `build/module` - are unaffected; the bundled dependencies only ever land in the `-with-deps` artifact, built from a separate `build/module-with-deps` copy.

```bash
boxlang Build.bx --version=1.1.0
```

| Option    | Required | Default Value    | Description                        |
| --------- | -------- | ---------------- | ---------------------------------- |
| `version` | No       | `1.0.0`          | The version of the module.         |
| `branch`  | No       | `development`    | The branch being built.            |
| `buildId` | No       | UUID (generated) | A unique identifier for the build. |

## Running Tests

Copy `.env.example` to `.env` for the local preview settings.

```bash
cp .env.example .env
```

Run the setup script once to create `boxlang_modules/` and register this checkout as `boxlang_modules/bx-sites`. If the root `modules/` folder is missing, it asks whether it should run `box install` for you:

```bash
boxlang Setup.bx
```

With CommandBox installed, run tests using `bash ./testbox/run`.

To preview the dogfooded documentation site locally:

```bash
boxlang bxSites serve --host="$BXSITES_PREVIEW_HOST" --port="$BXSITES_PREVIEW_PORT"
```

Open `http://$BXSITES_PREVIEW_HOST:$BXSITES_PREVIEW_PORT` in a browser. `serve` builds the site, watches source files, and reloads the browser when changes are detected. Stop it with `Ctrl+C`; the generated `site/` directory is ignored and should not be committed.

## Version Management

This module uses [SemVer](https://semver.org/) for versioning. The version is stored in `box.json`, used to publish to ForgeBox, and bumped automatically by the release workflow, which also tags the repo for each release.

## GitHub Actions Automation

- `pr.yml` - runs the test suite against every pull request
- `tests.yml` - the reusable test-suite workflow (`./testbox/run`), called by both `pr.yml` and `snapshot.yml`
- `snapshot.yml` - on every push to `development`: runs tests, then builds and publishes a snapshot release
- `release.yml` - builds and publishes a release to ForgeBox (a stable release from `main`, or a snapshot when called from `snapshot.yml`); requires a `FORGEBOX_API_TOKEN` secret
- `pages.yml` - builds and publishes this repo's own dogfooded docs to GitHub Pages (see [Deploying to GitHub Pages](docs/guides/deployment.md))
- `cron.yml` - runs the test suite daily against `development`

## Ortus Sponsors

BoxLang is a professional open-source project and it is completely funded by the [community](https://patreon.com/ortussolutions) and [Ortus Solutions, Corp](https://www.ortussolutions.com). Ortus Patreons get many benefits like a cfcasts account, a FORGEBOX Pro account and so much more. If you are interested in becoming a sponsor, please visit our patronage page: [https://patreon.com/ortussolutions](https://patreon.com/ortussolutions)

### THE DAILY BREAD

> "I am the way, and the truth, and the life; no one comes to the Father, but by me (JESUS)" Jn 14:1-12
