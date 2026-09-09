---
title: DocBox Output Example
order: 6.9
icon: phosphor-duotone:code
tags: [guides, boxlang, docbox, example]
---

# DocBox Output Example

A real example of what [`bxSites docbox`](docbox.md) produces. Every page
below came out of an actual run against the source shown - nothing here is
hand-written illustration.

Given this class in a project's `models/` folder:

```java title="models/Book.bx"
/**
 * A single book on the shelf, immutable once created.
 *
 * @author Ortus Solutions
 */
class singleton accessors=true {

	/**
	 * The book's title
	 */
	property name="title" type="string";

	/**
	 * Where book records are read from
	 */
	property name="datasource" type="string" inject="coldbox:setting:bookDatasource";

	/**
	 * Build a book.
	 *
	 * @title The book's title
	 * @author The book's author
	 */
	function init( required string title, string author = "Unknown" ) {
		return this
	}

	/**
	 * Return a copy of this book with a new title.
	 *
	 * @newTitle The replacement title
	 *
	 * @return a copy carrying the new title
	 */
	Book function withTitle( required string newTitle ) {}

	private function normalize() {}
}
```

...`bxSites docbox` writes `api/docbox/models/Book.md` with this
frontmatter:

```yaml title="Generated frontmatter"
---
title: "Book"
summary: "A single book on the shelf, immutable once created."
tags: [api, docbox]
---
```

And this body, reproduced below not as a code block but rendered for real -
this *is* the generated page's actual body, live, including the filter
toolbar. Try the Private chip, or type "title" into the search box:

---

## Book

`models.Book` &middot; Class

A single book on the shelf, immutable once created.

### Annotations

- `@accessors` true
- `@singleton`

### Properties

| Property | Type | Default | Description |
|---|---|---|---|
| `title` | `string` | &mdash; | The book's title |
| `datasource` | `string` | &mdash; | Where book records are read from `@inject coldbox:setting:bookDatasource` |

<style>
.bx-filter-toolbar { display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem; margin: 1.5rem 0; }
.bx-filter-search { flex: 1 1 12rem; min-width: 8rem; padding: 0.4rem 0.75rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: var(--bxsites-bg); color: var(--bxsites-text); font-size: 0.9rem; }
.bx-filter-chip { padding: 0.35rem 0.9rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: transparent; color: var(--bxsites-muted); font-size: 0.85rem; cursor: pointer; transition: all 0.15s ease; }
.bx-filter-chip:hover { border-color: var(--bxsites-accent); color: var(--bxsites-text); }
.bx-filter-chip.bx-filter-chip-on { background: var(--bxsites-accent); border-color: var(--bxsites-accent); color: var(--bxsites-bg); }
</style>

<div class="bx-filter" x-data="{ q: '', k: 'all' }">

<div class="bx-filter-toolbar">
<input type="search" class="bx-filter-search" x-model="q" placeholder="Search...">
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='all' }" @click="k='all'">All</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='constructor' }" @click="k='constructor'">Constructor</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='public' }" @click="k='public'">Public</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='private' }" @click="k='private'">Private</button>
</div>


<div x-show="(k==='all'||k==='constructor')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Constructor

<div class="bx-filter-item" data-n="Any init( required string title, string author = &quot;Unknown&quot; )" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Any init( required string title, string author = "Unknown" )`

Build a book.

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `title` | `string` | yes | &mdash; | The book's title |
| `author` | `string` | no | `Unknown` | The book's author |


</div>



</div>


<div x-show="(k==='all'||k==='public')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Public methods

<div class="bx-filter-item" data-n="Book withTitle( required string newTitle )" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Book withTitle( required string newTitle )`

Return a copy of this book with a new title.

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `newTitle` | `string` | yes | &mdash; | The replacement title |

- **Returns** a copy carrying the new title


</div>



</div>


<div x-show="(k==='all'||k==='private')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Private methods

<div class="bx-filter-item" data-n="Any normalize()" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Any normalize()`


</div>



</div>


</div>

---

## What to notice

- **The properties table exists at all.** DocBox's own JSON strategy drops
  declared `property` blocks; bx-sites reads them back from the same class
  metadata DocBox used, which is why `title` and `datasource` appear here
  with their hints intact - and why the WireBox `inject` on `datasource`
  survives, since that's how a ColdBox model declares its wiring.
- **`@return` text survives too**, for the same reason - see the Returns
  line under `withTitle`.
- **Private methods are included**, unlike the Javadoc generator's
  public/protected-only rule. DocBox reports them, so they're documented,
  and the Private chip filters them away when you don't want them.
- **`init` is the constructor**, given its own section ahead of the
  methods.

## The index pages

Alongside the class pages, the same run writes an overview index listing
every package and class, and one index per package:

```markdown title="api/docbox/index.md"
# Bookshelf API

## Packages

| Package | Classes |
|---|---|
| [`models`](models/index.md) | 1 |

## Classes

- [`Book`](models/Book.md)
```

See [DocBox API Reference](docbox.md) for configuration, and
[ColdBox Output Example](coldbox-example.md) for what the ColdBox verb
produces from the same project.
