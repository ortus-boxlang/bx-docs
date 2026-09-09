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
| `bxsites:build` | Renders the site into `<projectRoot>/site/`. Skips re-running the subprocess when nothing under the content dir or config file has changed since the last build - see [Build staleness checking](#build-staleness-checking) below. |
| `bxsites:serve` | Builds and serves the site locally with live reload. Runs in the foreground until you stop it (Ctrl+C). |
| `bxsites:clean` | Removes `<projectRoot>/site/`. Plain directory delete - no subprocess. |
| `bxsites:search-index` | Rebuilds `site/search-index.json` without a full site build. |
| `bxsites:lint` | Lints the docs/ Markdown source. |
| `bxsites:deploy` | Builds the site and deploys it to the configured target. |
| `bxsites:publish` | Builds the site and publishes it to bxSites Cloud. |
| `bxsites:package` | Builds the site and zips it to `site.zip`. |
| `bxsites:stats` | Reports page/word counts and other stats for the built site. |
| `bxsites:doctor` | Runs bx-sites' own project health diagnostics. |
| `bxsites:docbox` | Generates a BoxLang/CFML API reference from DocBox - see [BoxLang doc generation](#boxlang-doc-generation) below. |

Each goal provisions (downloads/caches) what it needs itself, on first
run - unlike the Gradle plugin, there's no separate "provision" goal to
run beforehand.

No goal is bound to any Maven lifecycle phase by default - run them
explicitly. If you'd like `bxsites:build` to run automatically, bind it
yourself in an `<executions>` block, e.g. to `pre-site` (a natural pairing
with Maven's own built-in `site` lifecycle).

## Build staleness checking

Maven has no Gradle-style built-in incremental-build engine, so
`bxsites:build` implements its own lightweight manual check: it compares
the newest last-modified timestamp anywhere under the content directory
(plus the config file, if one exists) against the newest timestamp already
present in `<projectRoot>/site/`. If nothing is newer, the goal logs that
it's skipping and returns without re-invoking bx-sites at all. Force a
rebuild regardless with:

```bash
mvn bxsites:build -Dbxsites.build.forceRebuild=true
```

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

## Spring Boot doc generation

`bxsites:openapi` wires a springdoc-generated OpenAPI/Swagger spec into
your site as an interactive widget, using bx-sites' own native
`::: openapi :::` content block - no OpenAPI parsing happens on our side
at all. Not bound to any lifecycle phase by default:

```bash
mvn bxsites:openapi -Dbxsites.openapi.specFile=target/openapi/openapi.json
```

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <specFile>${project.build.directory}/openapi/openapi.json</specFile>
    <pageTitle>Bookshelf API</pageTitle>       <!-- default: "API Reference" -->
    <pagePath>api/openapi.md</pagePath>         <!-- relative to the content dir; this is the default -->
    <autoPatchConfig>false</autoPatchConfig>    <!-- set true to auto-add openapi: true instead of failing -->
  </configuration>
</plugin>
```

Requires your own springdoc Maven plugin already applied and configured
to generate the spec file `specFile` points at - `bxsites:openapi` only
*consumes* that file, copying it into the content dir's
`assets/openapi/` and writing a thin wrapper page. It also expects
`bxsites.yaml`'s `openapi: true` to already be set (see
[Configuration](../configuration.md#openapi)) - fails with an actionable
error if it isn't, unless `autoPatchConfig` is on (YAML/TOML configs
only; JSON is never auto-patched, since safely inserting a key into
arbitrary JSON without a real parser is too risky). **Known limitation:**
Swagger UI renders entirely client-side, so per-endpoint text never
reaches bx-sites' own search index - only the wrapper page's
title/frontmatter is indexed.

`bxsites:javadoc` emits one Markdown page per public top-level Java type,
using the JDK's own Javadoc doclet SPI in-process (no `javadoc`
subprocess). Not bound to any lifecycle phase by default:

```bash
mvn bxsites:javadoc
```

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <sourceRoots>${project.compileSourceRoots}</sourceRoots>  <!-- this is the default -->
    <pagePathPrefix>api/javadoc</pagePathPrefix>              <!-- this is the default -->
    <tags><tag>api</tag><tag>javadoc</tag></tags>             <!-- this is the default -->
  </configuration>
</plugin>
```

**Deliberately scoped down for v1, not a complete Javadoc-to-Markdown
converter** - this is the heaviest of the three Spring Boot generators
and a full doclet-spec-compliant implementation is a much larger effort
than this milestone budgeted for. What it covers: public/protected
constructors and methods of public top-level types (nested and
package-private types, and fields, are skipped entirely), each member's
own doc comment (not inherited ones), and `@param`/`@return`/`@throws`/
`@deprecated` tags. What it deliberately doesn't do yet: inline HTML in
doc comments is stripped rather than converted to Markdown (a best-effort
text extraction, not a full HTML-to-Markdown converter); `{@link}`/
`{@see}` render as inline code with no cross-page hyperlink resolution;
no index/nav page is generated - wiring pages into your site's own
navigation is left to you. Runs against source files directly, so records'
compiler-generated accessors/`toString`/`equals`/`hashCode` show up too,
matching the standard `javadoc` tool's own behavior.

`bxsites:controller-scan` reflection-scans compiled classes for Spring
MVC controllers and emits one page per controller listing its mapped
endpoints - the fallback generator, for projects without OpenAPI
generation on. Not bound to any lifecycle phase by default:

```bash
mvn bxsites:controller-scan
```

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <classesDir>${project.build.outputDirectory}</classesDir>  <!-- this is the default -->
    <pagePathPrefix>api/controllers</pagePathPrefix>           <!-- this is the default -->
    <tags><tag>api</tag><tag>controllers</tag></tags>          <!-- this is the default -->
  </configuration>
</plugin>
```

Runs the scan in a forked JVM against your project's own runtime
classpath (Spring included, resolved via `project.getRuntimeClasspathElements()`)
- never in this plugin's own JVM, which stays Spring-agnostic. Requires
`requiresDependencyResolution=RUNTIME`, so bind it after `compile` if you
wire it into your own build (e.g. to `process-classes`). **Deliberately
scoped down**: only classes directly annotated
`@Controller`/`@RestController` are recognized (a custom stereotype
annotation built on `@Controller` isn't); only directly annotated
`@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/
`@DeleteMapping`/`@PatchMapping` methods are recognized; nested classes
are skipped; and since reflection has no access to source-level doc
comments, generated pages list endpoints (method, path, handler
signature) with no per-endpoint description text.

## BoxLang doc generation

`bxsites:docbox` generates a BoxLang/CFML API reference from
[DocBox](https://docbox.ortusbooks.com), for a JVM project whose sources
include `.bx`/`.cfc` classes. Unlike the Spring Boot goals above it's a
thin wrapper around the bx-sites `docbox` verb rather than an in-JVM
generator - the implementation lives on the BoxLang side, and one
implementation both build tools drive can't drift the way two would.

```xml title="pom.xml"
<plugin>
	<groupId>io.boxlang</groupId>
	<artifactId>bxsites-maven-plugin</artifactId>
	<configuration>
		<mappings>
			<models>models</models>
		</mappings>
		<projectTitle>Bookshelf API</projectTitle>
		<excludes>tests|build</excludes>
		<pagePathPrefix>api/docbox</pagePathPrefix>
		<tags>
			<tag>api</tag>
			<tag>docbox</tag>
		</tags>
	</configuration>
</plugin>
```

Only parameters you actually set are passed through, so anything left out
still falls through to `bxsites.yaml` - the config file stays the single
source of truth and this block only overrides it. See
[DocBox API Reference](docbox.md) for what the pages look like, and note
that the `bx-docbox` module has to be installed in the provisioned BoxLang
runtime.

**There is deliberately no ColdBox goal.** A ColdBox application is built
and run through CommandBox, never Maven, so
[`bxSites coldbox`](coldbox.md) stays a bx-sites CLI concern.

## What's not built yet

- **`bxsites:serve`'s live output streaming** - currently buffers output with a 30-minute timeout, both wrong for a goal meant to run indefinitely.

See the [Gradle Plugin](gradle-plugin.md) guide for the equivalent on the
Gradle side - both wrap the same underlying logic, so verb coverage and
behavior stay identical between the two build tools.
