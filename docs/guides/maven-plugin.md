---
title: Maven Plugin
order: 6.4
icon: phosphor-duotone:puzzle-piece
tags: [guides, java, maven, integration]
---

# Maven Plugin

Java and Spring Boot developers don't need CommandBox or a system-wide
BoxLang install to add a bx-sites site to their own project - the
`io.boxlang:bxsites-maven-plugin` Maven plugin downloads everything it
needs (the BoxLang runtime and bx-sites itself) into a local cache the
first time it runs. The only prerequisite is a JDK 21. It's the Maven-side
counterpart to the [Gradle Plugin](gradle-plugin.md) - both wrap the same
underlying logic, so verb coverage and behavior stay identical between the
two build tools.

> **Status:** pre-1.0, not yet published to Maven Central - see
> [`maven-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/maven-plugin)
> in the bx-sites repo for the source and current build/test instructions.
> This page documents what it does once published; the mechanics below are
> already real and verified, just not yet available as a Maven Central
> coordinate.

## Quick start

```xml title="pom.xml"
<build>
  <plugins>
    <plugin>
      <groupId>io.boxlang</groupId>
      <artifactId>bxsites-maven-plugin</artifactId>
      <version>&lt;version&gt;</version>
    </plugin>
  </plugins>
</build>
```

```bash
mvn bxsites:new     # scaffolds docs/ + bxsites.yaml
mvn bxsites:build   # renders docs/**.md into site/
mvn bxsites:serve   # build + serve locally with live reload
```

The short `bxsites:<goal>` form (confirmed working) requires the `<plugin>`
block above to be declared under `<build><plugins>` specifically, not just
`<pluginManagement>` - that's what registers `io.boxlang` as a resolvable
goal prefix for the current project. Without it declared there, use the
fully-qualified form instead: `mvn io.boxlang:bxsites-maven-plugin:build`.

A default setup needs nothing else configured - the plugin infers the
content directory (`docs/`, falling back to `src/`) and the output
directory (always `<projectRoot>/site/`) automatically. Your site's own
look, theme, nav, and every other setting is controlled entirely by
`bxsites.yaml`/`.toml`/`.json` at the project root, exactly as documented
in [Configuration](../configuration.md) - the plugin never duplicates that
schema, it only wires up *how* and *when* bx-sites runs from your build.

## Goals

| Goal | What it does |
|---|---|
| `bxsites:new` | Scaffolds a new bx-sites project (content dir + config file). |
| `bxsites:build` | Renders the site into `<projectRoot>/site/`. |
| `bxsites:serve` | Builds and serves the site locally with live reload. Runs in the foreground until you stop it (Ctrl+C). |
| `bxsites:clean` | Removes `<projectRoot>/site/`. Plain directory delete - no subprocess. |

Each goal provisions (downloads/caches) what it needs itself, on first
run - unlike the Gradle plugin, there's no separate "provision" goal to
run beforehand.

No goal is bound to any Maven lifecycle phase by default - run them
explicitly. If you'd like `bxsites:build` to run automatically, bind it
yourself in an `<executions>` block, e.g. to `pre-site` (a natural pairing
with Maven's own built-in `site` lifecycle).

## Configuration

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <projectRoot>${project.basedir}</projectRoot>
    <boxlangMiniserverVersion>1.18.0-snapshot</boxlangMiniserverVersion>
    <bxSitesVersion>1.0.0-snapshot</bxSitesVersion>
    <boxlangHomeDir>${project.build.directory}/bxsites/boxlang-home</boxlangHomeDir>
  </configuration>
</plugin>
```

Every parameter has a sensible default - a fresh project needs to set none
of these. The output directory isn't settable here at all - bx-sites
itself hardcodes it to `<projectRoot>/site/`, so the plugin derives it
rather than exposing a setting that wouldn't actually be honored.

## What's not built yet

- **Spring Boot doc generation** (OpenAPI, Javadoc, controller-scan) - planned.
- **`bxsites:serve`'s live output streaming** - currently buffers output with a 30-minute timeout, both wrong for a goal meant to run indefinitely.
- **Up-to-date/staleness checking** - Maven has no Gradle-style built-in incremental-build engine; `bxsites:build` currently re-runs the full build every invocation rather than skipping when nothing changed (the Gradle plugin does have real up-to-date checking for `bxSitesBuild`).
- Wrapper goals for bx-sites' other verbs (`deploy`, `publish`, `package`, `lint`, `check`, etc.) beyond the core four above.

See the [Gradle Plugin](gradle-plugin.md) guide for the equivalent on the
Gradle side - both wrap the same underlying logic, so verb coverage and
behavior stay identical between the two build tools.
