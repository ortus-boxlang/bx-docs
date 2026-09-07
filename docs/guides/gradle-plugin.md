---
title: Gradle Plugin
order: 6.3
icon: phosphor-duotone:gear-six
tags: [guides, java, gradle, integration]
---

# Gradle Plugin

Java and Spring Boot developers don't need CommandBox or a system-wide
BoxLang install to add a bx-sites site to their own project - the
`io.boxlang.bxsites` Gradle plugin downloads everything it needs (the
BoxLang runtime and bx-sites itself) into a local cache the first time it
runs. The only prerequisite is a JDK 21.

> **Status:** pre-1.0, not yet published to the Gradle Plugin Portal - see
> [`gradle-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/gradle-plugin)
> in the bx-sites repo for the source and current build/test instructions.
> This page documents what it does once published; the mechanics below are
> already real and verified, just not yet available as a one-line
> `plugins { }` dependency.

## Quick start

```kotlin title="build.gradle.kts"
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # scaffolds docs/ + bxsites.yaml
./gradlew bxSitesBuild   # renders docs/**.md into site/
./gradlew bxSitesServe   # build + serve locally with live reload
```

A default setup needs nothing else configured - the plugin infers the
content directory (`docs/`, falling back to `src/` - except in a project
with the Java plugin applied, where `src/` is your actual Java source root
and is never used as bx-sites content) and the output directory (always
`<projectRoot>/site/`) automatically. Your site's own
look, theme, nav, and every other setting is controlled entirely by
`bxsites.yaml`/`.toml`/`.json` at the project root, exactly as documented
in [Configuration](../configuration.md) - the plugin never duplicates that
schema, it only wires up *how* and *when* bx-sites runs from your build.

## Tasks

| Task | What it does |
|---|---|
| `bxSitesNew` | Scaffolds a new bx-sites project. Not wired into any lifecycle - run it once, explicitly. |
| `bxSitesBuild` | Renders the site. Real up-to-date checking: reruns only when your content, config, or the pinned versions actually change. |
| `bxSitesServe` | Builds and serves the site locally with live reload. Runs in the foreground until stopped. |
| `bxSitesClean` | Removes the built `site/` directory. |
| `bxSitesSearchIndex` | Rebuilds `site/search-index.json` without a full site build. |
| `bxSitesLint` | Lints the docs/ Markdown source. Wired into `check` by default (see `hookIntoCheck` below). |
| `bxSitesDeploy` | Builds the site and deploys it to the configured target. |
| `bxSitesPublish` | Builds the site and publishes it to bxSites Cloud. |
| `bxSitesPackage` | Builds the site and zips it to `site.zip`. |
| `bxSitesStats` | Reports page/word counts and other stats for the built site. |
| `bxSitesDoctor` | Runs bx-sites' own project health diagnostics. |

`bxSitesBuild` never runs automatically as part of `assemble` unless you
opt in (see `hookIntoAssemble` below) - a docs build is a distinct,
often-slower concern from compiling your actual code.

## Configuration

```kotlin title="build.gradle.kts"
bxSites {
    projectRoot.set(layout.projectDirectory)
    boxlangMiniserverVersion.set("1.18.0-snapshot")   // pinned BoxLang runtime version
    bxSitesVersion.set("1.0.0-snapshot")               // pinned bx-sites version
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                        // opt-in: run bxSitesBuild as part of assemble
    hookIntoCheck.set(true)                            // wires bxSitesLint into `check` by default
}
```

Every property has a sensible default. The output directory isn't settable
here at all - bx-sites itself hardcodes it to `<projectRoot>/site/`, so the
plugin derives it rather than exposing a setting that wouldn't actually be
honored.

## What's not built yet

- **Spring Boot doc generation** (OpenAPI, Javadoc, controller-scan) - planned.
- **`bxSitesServe`'s live output streaming** - currently buffers output with a 30-minute timeout, both wrong for a task meant to run indefinitely.

See the [Maven Plugin](maven-plugin.md) guide for the equivalent on the
Maven side - both wrap the same underlying logic, so verb coverage and
behavior stay identical between the two build tools.
