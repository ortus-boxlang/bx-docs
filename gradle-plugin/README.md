# BX Sites Gradle Plugin

Generate a [bx-sites](https://github.com/ortus-boxlang/bx-sites) documentation
site from a Gradle build — no CommandBox, no system-wide BoxLang install.
The plugin downloads everything it needs (the BoxLang runtime and bx-sites
itself) into a local cache the first time it runs.

**Requirements:** a JDK 21. Nothing else.

> **Status:** pre-1.0, not yet published to the Gradle Plugin Portal. Build
> and use it locally for now (see [Development](#development) below).

## Quick start

```kotlin
// build.gradle.kts
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # scaffolds docs/ + bxsites.yaml
./gradlew bxSitesBuild   # renders docs/**.md into site/
./gradlew bxSitesServe   # build + serve locally with live reload
```

That's it for a default setup — the plugin infers everything else (content
directory, output directory, versions) from sensible conventions. Your
site's own look/feel/nav/theme/etc. is still controlled entirely by
`bxsites.yaml`/`.toml`/`.json` at the project root, exactly as documented in
[bx-sites' own configuration guide](../docs/configuration.md) — this plugin
never duplicates that schema, it only wires up *how* and *when* bx-sites runs.

## Tasks

| Task | What it does |
|---|---|
| `bxSitesNew` | Scaffolds a new bx-sites project (content dir + config file). Not wired into any lifecycle — run it explicitly, once. |
| `bxSitesBuild` | Renders the site into `<projectRoot>/site/`. Real up-to-date checking: reruns only when your content, config, or the pinned versions change. |
| `bxSitesServe` | Builds and serves the site locally with live reload. Runs in the foreground until you stop it (Ctrl+C). |
| `bxSitesClean` | Removes `<projectRoot>/site/`. Plain directory delete — no subprocess. |

`bxSitesBuild` opts into `check` by default (see `hookIntoCheck` below) but
never auto-runs as part of `assemble` unless you turn that on — a docs
build is a distinct, often-slower concern from compiling your actual code.

## Configuration

```kotlin
bxSites {
    projectRoot.set(layout.projectDirectory)               // default: this project's own directory
    boxlangMiniserverVersion.set("1.18.0-snapshot")         // pinned BoxLang runtime version
    bxSitesVersion.set("1.0.0-snapshot")                    // pinned bx-sites version
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                             // opt-in: run bxSitesBuild as part of assemble
    hookIntoCheck.set(true)                                 // bxSitesLint/Check-style verbs on by default (fast-follow, not yet implemented)
}
```

Every property has a sensible default — a fresh project needs to set none
of these. The content directory (`docs/`, falling back to `src/`) and the
output directory (always `<projectRoot>/site/` — bx-sites itself hardcodes
this, it isn't configurable) are derived automatically, not settable here.

## Not yet implemented

- **Spring Boot doc generation** (OpenAPI, Javadoc, controller-scan) — planned, not built yet.
- **`bxSitesServe`'s live output streaming** — currently buffers output and applies a 30-minute timeout, both wrong for a verb meant to run indefinitely. Works, but not ideal yet.
- Fast-follow verb tasks beyond the core four (`bxSitesDeploy`, `bxSitesPublish`, `bxSitesPackage`, `bxSitesLint`, `bxSitesCheck`, etc.) — bx-sites itself supports these verbs; the Gradle wrapper tasks for them don't exist yet.

## Development

This plugin depends on `../core` (a shared library also used by the Maven
plugin) via a Gradle composite build — nothing extra to set up, `./gradlew`
resolves it automatically.

```bash
./gradlew check   # unit tests (fast, no network) + functional tests (real network + real bx-sites build)
```

The functional tests provision real artifacts from the network and run a
real bx-sites build — expect them to take longer than the unit tests and to
need outbound internet access.

## License

Apache-2.0, same as bx-sites itself.
