---
title: AI Agent Skills
order: 6.3
icon: phosphor-duotone:robot
summary: Give Claude Code, Cursor, Codex, and other AI coding assistants deep, structured knowledge of bx-sites - install the official skill pack via npx, the ColdBox CLI, or bxSites' own skills:install verb.
tags: [guides, ai, skills]
---

# AI Agent Skills

An **[Agent Skill](https://code.claude.com/docs/en/skills)** is a small,
self-contained `SKILL.md` file that teaches an AI coding assistant how to do
one specific thing well - the assistant loads it automatically, on demand,
whenever a task matches what the skill describes. Instead of re-explaining
bx-sites' own conventions to your assistant in every conversation (how
`::: card :::` blocks work, what `bxsites.yaml`'s `redirects` key expects,
how `page:new` differs from a hand-written file), a skill hands it that
knowledge up front, written the way bx-sites itself works.

[`ortus-boxlang/bx-sites-skills`](https://github.com/ortus-boxlang/bx-sites-skills)
is the official skill pack for this project - thirteen skills covering
everything from scaffolding a new project to troubleshooting this
repository's own GitHub Actions. They work with any assistant that supports
the Agent Skills format (Claude Code, Cursor, Codex, and others).

## Install

Three ways to install the pack - pick whichever fits your workflow:

### `npx skills add`

The [`skills` CLI](https://github.com/skillslib/skills) works with any
project, regardless of language or runtime, and needs nothing but Node.js:

```bash title="Install every skill"
npx skills add ortus-boxlang/bx-sites-skills
```

```bash title="Non-interactive (CI, scripts)"
npx -y skills add ortus-boxlang/bx-sites-skills -y
```

Install a single skill instead of the whole set by pointing at it directly:

```bash title="Install just one skill"
npx skills add ortus-boxlang/bx-sites-skills/skills/bx-sites-deployment
```

### `coldbox ai skills install`

If you already have the ColdBox CLI, it can install straight from the same
GitHub source - see the [BoxLang Skills Directory](https://skills.boxlang.io/):

```bash title="Install a single skill"
coldbox ai skills install ortus-boxlang/bx-sites-skills/bx-sites-deployment
```

### `bxSites skills:install`

bx-sites also ships its own one-shot verb - a thin wrapper over `npx skills
add` that installs the whole pack straight into the current project, so a
freshly-scaffolded project's AI assistant knows bx-sites from the very first
prompt:

```bash title="Usage"
bxSites skills install
# or, equivalently:
bxSites skills:install
```

```bash title="Install just one skill"
bxSites skills:install --skill=bx-sites-deployment
```

Requires Node.js/`npx` on `PATH` (the same requirement `npx skills add` has
on its own) - see [CLI Reference](../cli-reference.md#skillsinstall) for the
full flag reference.

## Available skills

Each skill is a single, self-contained `SKILL.md` (no bundled resource
files), so it installs correctly through every path above:

| Skill | Description |
|---|---|
| `bx-sites-getting-started` | Install bx-sites, scaffold a new project (or migrate an existing GitBook/mkdocs/Notion one in), project layout, page frontmatter, linking, build/serve/clean. |
| `bx-sites-content-blocks` | Rich `::: name :::` content blocks - cards, columns, stepper, buttons, embeds, page-link/link-preview, prompt, updates, includes, conditional content, OpenAPI widget. |
| `bx-sites-markdown` | Admonitions, footnotes, definition lists, content tabs, code-block annotations, Mermaid, math, tables, icons, responsive images, Alpine.js interactivity. |
| `bx-sites-variables-functions` | Reusable `{{ variables }}` and BoxLang magic functions (`docs/functions.bxs`), including status-badge/rating/progress-bar visualizer recipes. |
| `bx-sites-blog-versioning-i18n` | The blog (`docs/blog/posts/`), versioned docs (`docs/versions/`), translated locales (`docs/i18n/`), and redirects. |
| `bx-sites-content-quality` | Pre-build content checks without a full build - `lint`, `blog:drafts`, `blog:find`, `search:query`. |
| `bx-sites-build` | `build`/`serve`/`clean`/`search-index`, plus `doctor`/`stats`/`site:check` diagnostics on a built site. |
| `bx-sites-configuration` | The full `bxsites.yaml`/`bxsites.json` key reference - `baseURL`, `nav`, `redirects`, `markdown`, assets, and more. |
| `bx-sites-themes` | Choosing, customizing, overriding, installing, or writing a theme; the `ThemeProvider` contract. |
| `bx-sites-search` | Search providers - local (MiniSearch), Algolia DocSearch, Pagefind, and wiring up a custom provider. |
| `bx-sites-plugins` | Writing/installing a bx-sites plugin (build-lifecycle hooks) or a CLI provider (new `bxSites` verbs). |
| `bx-sites-deployment` | Deploy targets (S3/Azure/GCS/Firebase/FTP/SFTP/rsync/Netlify/Vercel/Cloudflare Pages/GitHub Pages), `package`, and the GitHub Actions publishing workflow. |
| `bx-sites-actions` | Operate and troubleshoot bx-sites' own GitHub Actions workflows (tests, snapshot, release, docs, pages). |

## Example prompts

Once installed, just describe what you want - your assistant picks the
matching skill on its own:

```text title="Scaffold a new site"
Scaffold a new bx-sites project called "acme-docs" using the gitbook
theme, add a Getting Started page, and start the dev server.
```

```text title="Author content"
Add a three-step ::: stepper ::: block to docs/getting-started.md walking
through install, scaffold, and build - and a ::: cards ::: grid linking
to the three main guides.
```

```text title="Ship it"
Add a Netlify deploy target to bxsites.yaml and explain which
environment variable it expects for the auth token.
```

## FAQ

??? faq "What's actually in a SKILL.md file?"
    A short YAML frontmatter block (`name`, `description` - the trigger an
    assistant matches against) followed by plain Markdown instructions,
    conventions, and examples for that one topic. No bundled scripts or
    resource files - every skill in this pack is a single file, by design,
    so it installs the same way through `npx skills add`, `coldbox ai
    skills install`, and `bxSites skills:install` alike.

??? faq "Do I need all thirteen skills?"
    No - every install path supports installing just one skill by name
    (see [Install](#install) above). Most projects are fine installing the
    whole pack, though: an assistant only loads a skill's content when a
    task actually matches it, so unused skills cost nothing at prompt time.

??? faq "Which AI assistants does this work with?"
    Any assistant that supports the Agent Skills format - Claude Code,
    Cursor, Codex, and others. `npx skills add`/`coldbox ai skills install`
    detect which assistant(s) your project already has configured and
    install into each one automatically.

??? faq "How do I verify a skill actually installed?"
    Check for a new `SKILL.md` under your assistant's own skills directory
    (e.g. `.claude/skills/bx-sites-getting-started/SKILL.md` for Claude
    Code) - or just ask your assistant something the skill covers (e.g.
    "how do I add a Netlify deploy target?") and see whether its answer
    matches this documentation.

??? faq "`bxSites skills:install` failed with an error about npx/Node.js"
    It shells out to the real `npx skills add` under the hood, so it needs
    Node.js on `PATH` the same way that command does on its own - install
    Node.js from [nodejs.org](https://nodejs.org/) and try again. Or skip
    `bxSites`'s own wrapper entirely and run `npx skills add
    ortus-boxlang/bx-sites-skills` directly.

??? faq "Can I install these into a project that isn't a bx-sites project yet?"
    Yes - `npx skills add`/`coldbox ai skills install` work in any
    directory. `bxSites skills:install` specifically needs an existing
    `--projectRoot` (the current directory by default), same as every
    other `bxSites` verb, but that project doesn't need to be built yet -
    installing skills before you've written a single page is exactly the
    point, so your assistant already knows bx-sites for the very first
    prompt.

## Source

- Skills repository: [ortus-boxlang/bx-sites-skills](https://github.com/ortus-boxlang/bx-sites-skills)
- bx-sites repository: [ortus-boxlang/bx-sites](https://github.com/ortus-boxlang/bx-sites)
- BoxLang Skills Directory: [skills.boxlang.io](https://skills.boxlang.io/)
