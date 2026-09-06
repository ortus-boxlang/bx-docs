---
title: Troubleshooting
order: 7
icon: phosphor-duotone:lifebuoy
summary: Diagnose common setup, build, and serve problems - and where to get help when this page doesn't cover it.
tags: [troubleshooting, faq]
---

# Troubleshooting

## Run `doctor` first

Before digging further, run the built-in health check - it catches most of
the issues on this page in one shot:

```bash frame="terminal" title="Terminal"
bxSites doctor
```

It checks the JVM version, that `docs/` (or `src/`) exists, that
`bxsites.yaml`/`.json` actually parses and validates, that the required
BoxLang modules are installed and activated, and - if a project-level
`theme/` override exists - that it satisfies the theme contract. It exits
`1` if any check fails and prints what's wrong; nothing here mutates your
project.

## Common issues

??? bug "`No docs/ directory found`"
    `build`/`serve`/`audit`/etc. look for `docs/` (falling back to `src/`)
    relative to the current directory, or to `--projectRoot=<path>` if you
    passed one. Either run the command from inside your project's root
    folder, or pass `--projectRoot`:

    ```bash frame="terminal" title="Terminal"
    bxSites build --projectRoot=/path/to/my-docs
    ```

??? bug "`bxsites.yaml`/`.json` fails to parse or validate"
    Run `bxSites doctor` to see the exact key/line the config loader
    rejected. Common causes: mixing tabs and spaces in YAML indentation,
    a stray trailing comma in JSON, or a key that expects an array (like
    `nav` or `i18n.locales`) written as a bare string. See
    [Configuration](configuration.md) for the full key reference.

??? bug "`bx-markdown`/`bx-esapi`/`bx-yaml`/`bx-image` not installed/activated"
    `build`, `serve`, and `search-index` all need these four BoxLang
    modules. Installing `bx-sites` itself installs them automatically as
    `box.json` dependencies (`install-bx-module bx-sites` or
    `box install bx-sites`) - if you're seeing this, either the install
    didn't finish, or the module was registered by hand without its
    dependencies. Re-running `box install` from your project root
    re-resolves everything; `bxSites doctor` confirms which module (if
    any) is still missing.

??? bug "A project `theme/` override fails to build"
    A custom `theme/` folder must provide both `layout.bxm` and
    `page.bxm` - `doctor` reports which one is missing. See
    [Themes](guides/themes.md) for the full contract, or run
    `bxSites theme:new` to eject a working built-in theme as a starting
    point instead of writing one from scratch.

??? bug "`serve` doesn't pick up a change"
    `serve` watches `docs/`, your `bxsites.yaml`/`.json`, and a
    project-level `theme/` override - a change anywhere else (for example
    editing a file under `resources/` in a module checkout, not a real
    project) won't trigger a rebuild. If a change genuinely isn't
    reflected, stop `serve`, run `bxSites clean` to clear any stale build
    cache, then `bxSites serve` again.

??? bug "A build looks stale, or CI reports success but nothing changed"
    `build` doesn't delete previously built output that no longer has a
    matching source page. Run `bxSites clean` before `build` to remove
    `site/` and any build cache entirely, then rebuild from scratch. If a
    CI step reports success but the deployed site doesn't reflect it,
    check the actual build-step log output for `Error:` - a crashed build
    can still exit with a misleading status in some CI setups.

??? bug "A translated page shows an untranslated-page notice"
    That's expected, not a bug: a locale doesn't need every page
    translated before it's usable. A page missing from
    `docs/i18n/<code>/` still builds at its expected URL, showing the
    default locale's content with a small notice at the top. See
    [Internationalization (i18n)](guides/i18n.md).

??? bug "`i18n:status` reports 100% but a translation still looks out of date"
    `i18n:status` checks per-locale page *presence* only, not per-page
    content parity - a locale copy can exist but still be missing a
    section added later to the default-locale page. Diff the locale file
    against its default-locale counterpart directly if you suspect this.

## Still stuck?

If none of the above covers it, reach out through one of the support
channels below - see [Contribute](contribute.md#support-questions) for the
full list:

::: cards
::: card title="Ortus Community Discourse" icon="phosphor-duotone:chats-circle" href="https://community.ortussolutions.com"
Ask questions and search existing discussions.
:::
::: card title="Box Slack Team" icon="phosphor-duotone:slack-logo" href="https://boxteam.slack.com"
Chat in real time with the community and maintainers.
:::
::: card title="File an issue" icon="phosphor-duotone:bug" href="https://github.com/ortus-boxlang/bx-sites/issues"
For a reproducible bug, with `bxSites doctor` output attached.
:::
::: card title="Professional Support" icon="phosphor-duotone:handshake" href="services.md"
Want the team behind BxSites to fix it for you? Ortus Solutions offers
professional support and consulting.
:::
:::
