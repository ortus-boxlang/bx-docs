---
title: DocBox API Reference
order: 6.7
icon: phosphor-duotone:brackets-curly
tags: [guides, boxlang, docbox, api, integration]
---

# DocBox API Reference

`bxSites docbox` turns your BoxLang/CFML classes into a themed, searchable
API reference inside your own site - the BoxLang counterpart of the
`bxSitesJavadocDoc`/`bxsites:javadoc` generators the
[Gradle](gradle-plugin.md) and [Maven](maven-plugin.md) plugins give Java
projects.

It doesn't parse a single line of BoxLang itself.
[DocBox](https://docbox.ortusbooks.com) already does that job well, so this
runs DocBox's own JSON strategy and converts the result into ordinary
Markdown pages. They land in your content directory like any other page,
which means the next `build` themes them, indexes them for search, and
serves them exactly as it would something you hand-wrote.

## Prerequisite

The `bx-docbox` module has to be installed in the BoxLang runtime:

```bash frame="terminal" title="Terminal"
# OS binary
install-bx-module bx-docbox

# CommandBox
box install bx-docbox
```

The verb fails with an actionable message rather than a stack trace when it
isn't there.

## Quick start

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites build
```

With no configuration at all, `docbox` documents whichever of the
conventional BoxLang source folders your project actually has - `models`,
`handlers`, `bifs`, `components`, `interceptors` - and writes pages under
`docs/api/docbox/`.

## Configuration

Everything is optional. See [`docbox`](../configuration.md#docbox) for the
full schema.

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "My API"
      mappings:
        models: models
        bifs: bifs
      excludes: "tests|build"
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "My API",
    		"mappings": { "models": "models", "bifs": "bifs" },
    		"excludes": "tests|build",
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

Every key has a flag that overrides it for one run:

```bash frame="terminal" title="Terminal"
bxSites docbox --mappings:models=models --projectTitle="My API" \
	--pagePathPrefix=api/classes --tags=api,classes --excludes=tests
```

`--jsonDir=<path>` keeps DocBox's own JSON output instead of discarding it,
if you want to feed the same metadata to something else.

## What gets generated

Three kinds of page: an overview index, one index per package, and one page
per class.

```
docs/api/docbox/
├── index.md                    # every package and class
├── models/
│   ├── index.md                # this package's classes
│   ├── UserService.md
│   └── security/
│       ├── index.md
│       └── Auth.md
```

A class page carries the class' own docblock, its declared properties, and
its functions grouped by access - constructor first, then public, package
and private - each with its full signature, hint, parameter table and
`@return` text. Members sit inside the same Alpine.js filter toolbar the
Javadoc pages use, so a long class stays scannable:

```markdown title="docs/api/docbox/models/UserService.md"
---
title: "UserService"
summary: "Loads and stores users."
tags: [api, docbox]
---

# UserService

`models.UserService` &middot; Class &middot; implements `models.IService`

Loads and stores users.

## Annotations

- `@singleton`
- `@accessors` true

## Properties

| Property | Type | Default | Description |
|---|---|---|---|
| `datasource` | `string` | &mdash; | The user datasource `@inject coldbox:setting:userDatasource` |
| `pageSize` | `numeric` | `25` | How many users to page by |

## Public methods

### `String findById( required numeric id )`

Find a user by its id.

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `id` | `numeric` | yes | &mdash; | The user id |

- **Returns** the user, or null when there is none
```

## Wiring it into your nav

Generated pages are ordinary content, so they appear in the automatic
directory nav on their own. To place them deliberately, name the index in
your own [`nav`](../configuration.md#nav):

=== "YAML"
    ```yaml title="bxsites.yaml"
    nav:
      - title: Reference
        children:
          - api/docbox/index.md
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"nav": [
    		{ "title": "Reference", "children": [ "api/docbox/index.md" ] }
    	]
    }
    ```

## What it deliberately doesn't do

- **No cross-page links between classes.** An `extends`/`implements`
  target, or a type named in a signature, renders as inline code even when
  that class has its own generated page.
- **No inherited members.** Only what a class declares itself, matching
  DocBox's own JSON output.
- **No nav wiring.** Pages land under `pagePathPrefix`; placing them is
  yours to decide, as above.

## A note on where the metadata comes from

DocBox's JSON strategy serializes a class' name, package, type, `extends`
and functions, but not its declared `property` blocks, the interfaces it
implements, its class-level annotations, or per-function `@return` text.
Those four matter for a BoxLang API reference - properties especially,
since they carry every `accessors` field and every WireBox `inject` - so
bx-sites reads them back from the same class metadata DocBox itself used
and merges them into the JSON before rendering. Nothing is parsed twice,
and a class whose metadata can't be re-resolved is simply rendered as
DocBox wrote it.

## From Gradle or Maven

Both build plugins expose this generator too, for a JVM project whose
sources include BoxLang or CFML classes - see
[Gradle Plugin](gradle-plugin.md#boxlang-doc-generation) and
[Maven Plugin](maven-plugin.md#boxlang-doc-generation).

Documenting a ColdBox application is a separate verb - see
[ColdBox Applications](coldbox.md).
