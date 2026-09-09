---
title: DocBox-Ausgabebeispiel
order: 6.9
icon: phosphor-duotone:code
tags: [anleitungen, boxlang, docbox, example]
---

# DocBox-Ausgabebeispiel

Ein echtes Beispiel dafür, was [`bxSites docbox`](docbox.md) erzeugt.
Jede Seite unten stammt aus einem tatsächlichen Lauf gegen den gezeigten
Quellcode - nichts davon ist von Hand geschrieben.

Gegeben diese Klasse im Ordner `models/` eines Projekts:

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

...schreibt `bxSites docbox` die Datei `api/docbox/models/Book.md` mit
diesem Frontmatter:

```yaml title="Frontmatter"
---
title: "Book"
summary: "A single book on the shelf, immutable once created."
tags: [api, docbox]
---
```

Und diesen Body - unten nicht als Codeblock, sondern wirklich gerendert.
Das *ist* der Body der erzeugten Seite, live, samt Filterleiste. Probier
den Private-Chip oder tippe "title" in das Suchfeld:

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

## Worauf zu achten ist

- **Dass es die Properties-Tabelle überhaupt gibt.** DocBox' eigene
  JSON-Strategie lässt deklarierte `property`-Blöcke weg; bx-sites liest
  sie aus denselben Klassenmetadaten nach, die DocBox verwendet hat -
  deshalb stehen `title` und `datasource` hier mitsamt Hinweistext, und
  deshalb überlebt auch das WireBox-`inject` auf `datasource`.
- **Der `@return`-Text überlebt aus demselben Grund** - siehe die
  Returns-Zeile unter `withTitle`.
- **Private Methoden sind enthalten**, anders als beim Javadoc-Generator.
  DocBox meldet sie, also werden sie dokumentiert; der Private-Chip blendet
  sie aus, wenn man sie nicht sehen will.
- **`init` ist der Konstruktor** und bekommt einen eigenen Abschnitt vor
  den Methoden.

Siehe [DocBox-API-Referenz](docbox.md) für die Konfiguration und
[ColdBox-Ausgabebeispiel](coldbox-example.md) für das, was der
ColdBox-Verb aus demselben Projekt macht.
