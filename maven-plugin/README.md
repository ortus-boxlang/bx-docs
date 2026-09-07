# BX Sites Maven Plugin

Generate a [bx-sites](https://github.com/ortus-boxlang/bx-sites) documentation
site from a Maven build — no CommandBox, no system-wide BoxLang install.
The plugin downloads everything it needs (the BoxLang runtime and bx-sites
itself) into a local cache the first time it runs. It's the Maven-side
counterpart to the [Gradle plugin](../gradle-plugin/README.md) — both wrap
the same underlying logic, so verb coverage and behavior stay identical
between the two build tools.

**Requirements:** a JDK 21. Nothing else.

> **Status:** pre-1.0, not yet published to Maven Central. Build and use it
> locally for now (see [Development](#development) below).

## Quick start

```xml
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
block above to be under `<build><plugins>` specifically, not just
`<pluginManagement>` — that's what registers `io.boxlang` as a resolvable
goal prefix for the current project. Without it declared there at all, use
the fully-qualified form instead:
`mvn io.boxlang:bxsites-maven-plugin:build`. Your site's own
look/feel/nav/theme/etc. is controlled entirely by
`bxsites.yaml`/`.toml`/`.json` at the project root, exactly as documented
in [bx-sites' own configuration guide](../docs/configuration.md) — this
plugin never duplicates that schema, it only wires up *how* and *when*
bx-sites runs.

## Goals

| Goal | What it does |
|---|---|
| `bxsites:new` | Scaffolds a new bx-sites project (content dir + config file). |
| `bxsites:build` | Renders the site into `<projectRoot>/site/`. |
| `bxsites:serve` | Builds and serves the site locally with live reload. Runs in the foreground until you stop it (Ctrl+C). |
| `bxsites:clean` | Removes `<projectRoot>/site/`. Plain directory delete — no subprocess. |

Each goal provisions (downloads/caches) what it needs itself, on first
run — there's no separate "provision" goal to run beforehand.

No goal is bound to any Maven lifecycle phase by default — run them
explicitly. If you'd like `bxsites:build` to run automatically, bind it
yourself in an `<executions>` block, e.g. to `pre-site` (a natural pairing
with Maven's own built-in `site` lifecycle).

## Configuration

```xml
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

Every parameter has a sensible default — a fresh project needs to set none
of these. The output directory is always `<projectRoot>/site/` (bx-sites
itself hardcodes this, it isn't configurable here).

## Not yet implemented

- **Spring Boot doc generation** (OpenAPI, Javadoc, controller-scan) — planned, not built yet.
- **`bxsites:serve`'s live output streaming** — currently buffers output and applies a 30-minute timeout, both wrong for a goal meant to run indefinitely. Works, but not ideal yet.
- **Up-to-date/staleness checking** — Maven has no Gradle-style built-in incremental-build engine; `bxsites:build` currently re-runs the full build every invocation rather than skipping when nothing changed.
- Fast-follow goals beyond the core four (`bxsites:deploy`, `bxsites:publish`, `bxsites:package`, `bxsites:lint`, `bxsites:check`, etc.) — bx-sites itself supports these verbs; the Maven wrappers for them don't exist yet.

## Development

This plugin depends on `../core` (a shared library also used by the Gradle
plugin), resolved as a normal Maven dependency from your local repository.
Build and install it first:

```bash
cd ../core && ./gradlew publishToMavenLocal
cd ../maven-plugin
mvn verify   # unit tests (fast, no network) + integration test (real network + real bx-sites build)
```

The integration test (via `maven-invoker-plugin`) provisions real artifacts
from the network and runs a real bx-sites build — expect it to take longer
than the unit tests and to need outbound internet access.

## License

Apache-2.0, same as bx-sites itself.
