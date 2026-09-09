---
title: ColdBox Applications
order: 6.8
icon: phosphor-duotone:tree-structure
tags: [guides, boxlang, coldbox, api, integration]
---

# ColdBox Applications

`bxSites coldbox` documents a [ColdBox](https://coldbox.ortusbooks.com)
application from its conventions on disk: its routes, event handlers,
models and WireBox mappings, modules, interceptors and scheduled tasks.

It never boots the application. Nothing is compiled, no datasource has to
be reachable, and no environment variable has to be set, so it runs the
same on a CI runner as it does on your laptop. The cost of that is stated
plainly in [what it can't see](#what-it-cant-see) below.

## Quick start

From the root of a ColdBox app that also holds a bx-sites project:

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites build
```

Pages land under `docs/api/coldbox/`. If your app lives somewhere other
than the project root, point at it:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app
```

## Configuration

Everything is optional. See [`coldbox`](../configuration.md#coldbox) for
the full schema.

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [coldbox]
    appRoot = "."
    pagePathPrefix = "api/coldbox"
    tags = [ "api", "coldbox" ]
    include = [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    ```

`include` decides which page sets get generated; leave a token out and that
set is skipped entirely. Each key has a flag that overrides it for one run:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app --include=routes,handlers \
	--pagePathPrefix=reference --tags=reference,api
```

## What gets generated

```
docs/api/coldbox/
├── index.md               # app overview and counts
├── routes.md              # every route, in declaration order
├── handlers/
│   ├── index.md
│   ├── Main.md
│   └── api/Orders.md      # module handlers nest under their module
├── models/
│   ├── index.md           # models plus the binder's own mappings
│   └── UserService.md
├── modules/
│   ├── index.md
│   └── api.md
├── interceptors.md
└── scheduled-tasks.md
```

### Routes

Every route the app router and each module router declares, in the order
they're declared - ColdBox matches the first pattern that fits, so order is
meaningful and the page preserves it. `resources()`/`apiResources()` expand
into the individual routes ColdBox generates for them, and a module's
routes carry the entry point they're actually mounted at:

```markdown
| Verbs | Pattern | Target | Name | Module |
|---|---|---|---|---|
| ANY | `/home` | `main.index` | `home` | &mdash; |
| GET | `/users/:id` | `users.show` | &mdash; | &mdash; |
| ANY | `/old/book` | `/mybook` *redirect* | &mdash; | &mdash; |
| GET | `/v1/orders` | `orders.index` | &mdash; | `api` |
| ANY | `:handler/:action?` | *by convention* | &mdash; | &mdash; |
```

### Handlers

One page per handler, listing the routes that reach it and the action each
one hits, then its routable actions with their doc comments and arguments.
ColdBox's own lifecycle hooks (`preHandler`, `aroundHandler`, `onError`
and friends) get their own section rather than being listed as actions a
URL can reach, and `init` and private methods are left out.

### Models

One page per model: its scope, what WireBox injects into it, its own
properties, and its public methods. Injected dependencies are separated
from plain properties, because `property name="x" inject="y"` is wiring
rather than data. The index adds the binder's declared mappings -
`map()`, `mapPath()` and `mapDirectory()` with their destinations and
scopes.

### Modules, interceptors and scheduled tasks

A module page carries what its `ModuleConfig` declares - author, version,
entry point, dependencies - and what it contributes to the app, each item
linked to the page describing it. The interceptors page covers both halves
of how ColdBox finds them: what `config/ColdBox` registers, and what
`interceptors/` declares, with each class' public methods listed as the
interception points they are. The scheduled tasks page reads
`config/Scheduler` and every module's own.

## Richer pages with DocBox

Routes, modules, interceptors and the shape of the app come from the
conventions alone. Per-class detail - a handler's actions, a model's
methods, their arguments and doc comments - comes from
[DocBox](docbox.md), so installing `bx-docbox` makes those pages
substantially richer:

```bash frame="terminal" title="Terminal"
install-bx-module bx-docbox
```

Without it the verb still runs and still lists every handler, model,
route and module; the pages simply say what's missing and how to get it.

## A real example

[ColdBox Output Example](coldbox-example.md) shows five of these pages as
they actually come out of a run, against a small application whose source
is shown alongside them.

## What it can't see

Static reading has a hard edge, and it's better to name it than to pretend
otherwise. Anything an application decides at runtime isn't on disk to be
found:

- A route whose pattern or target is built from a variable, or registered
  in a loop.
- A WireBox mapping or scheduled task whose name is computed.
- A module installed at boot rather than committed under `modules_app/`.
- What `mapDirectory()` will actually register, which depends on what's on
  disk when the app boots.

Every one of these is skipped rather than guessed at, so a generated page
under-reports rather than lying. Anything declared literally parses
correctly whether or not the rest of the file does.

## Not available from Gradle or Maven

Deliberately. A ColdBox application is built and run through CommandBox,
never a Java build tool, so there's no `bxSitesColdBoxDoc` task or
`bxsites:coldbox` goal. The [Gradle](gradle-plugin.md) and
[Maven](maven-plugin.md) plugins do expose the [DocBox](docbox.md)
generator, which documents BoxLang/CFML classes that genuinely sit in a
JVM project being built.
