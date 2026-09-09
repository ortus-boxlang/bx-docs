---
title: Ejemplo de salida de DocBox
order: 6.9
icon: phosphor-duotone:code
tags: [guías, boxlang, docbox, example]
---

# Ejemplo de salida de DocBox

Un ejemplo real de lo que produce [`bxSites docbox`](docbox.md). Cada
página de abajo salió de una ejecución real contra el código que se
muestra: nada aquí es una ilustración escrita a mano.

Dada esta clase en la carpeta `models/` de un proyecto:

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

...`bxSites docbox` escribe `api/docbox/models/Book.md` con este
frontmatter:

```yaml title="Frontmatter"
---
title: "Book"
summary: "A single book on the shelf, immutable once created."
tags: [api, docbox]
---
```

Y este cuerpo, reproducido abajo no como bloque de código sino renderizado
de verdad: este *es* el cuerpo real de la página generada, en vivo, con su
barra de filtro. Prueba el chip Private, o escribe "title" en el buscador:

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

## En qué fijarse

- **Que la tabla de propiedades exista siquiera.** La estrategia JSON de
  DocBox descarta los bloques `property` declarados; bx-sites los relee de
  los mismos metadatos de clase que usó DocBox, y por eso `title` y
  `datasource` aparecen aquí con sus descripciones intactas, y por eso
  sobrevive el `inject` de WireBox en `datasource`.
- **El texto de `@return` también sobrevive**, por la misma razón: mira la
  línea Returns bajo `withTitle`.
- **Los métodos privados se incluyen**, a diferencia del generador de
  Javadoc. DocBox los reporta, así que se documentan, y el chip Private los
  oculta cuando no los quieres.
- **`init` es el constructor**, con su propia sección antes de los métodos.

Consulta [Referencia de API con DocBox](docbox.md) para la configuración y
[Ejemplo de salida de ColdBox](coldbox-example.md) para lo que el verbo
ColdBox produce del mismo proyecto.
