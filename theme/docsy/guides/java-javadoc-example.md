---
title: Javadoc Output Example
order: 6.6
icon: phosphor-duotone:code
tags: [guides, java, spring-boot, javadoc, example]
---

# Javadoc Output Example

A real example of what `bxSitesJavadocDoc` (Gradle) / `bxsites:javadoc`
(Maven) actually produces - see
[Gradle Plugin](gradle-plugin.md#spring-boot-doc-generation) or
[Maven Plugin](maven-plugin.md#spring-boot-doc-generation) for how to turn
this on in your own project.

Point it at your own project's `.java` sources and it walks every public
top-level type, emitting one page per type, its constructors/methods/fields
wrapped in a small Alpine.js-driven filter toolbar - a search box plus one
chip per kind actually present - so a long class stays easy to scan. Given
this class:

```java title="com/example/Book.java"
package com.example;

/**
 * A single book in the shelf, immutable once created. Two books are
 * considered equal only by reference, not by content.
 */
public class Book {

    /** The longest title this shelf will accept. */
    public static final int MAX_TITLE_LENGTH = 200;

    /**
     * Creates a book with the given title and author.
     *
     * @param title  the book's title
     * @param author the book's author
     */
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    /**
     * Returns a copy of this book with a new title.
     *
     * @param newTitle the new title
     * @return a copy of this book with the given title
     */
    public Book withTitle(String newTitle) {
        return new Book(newTitle, author);
    }
}
```

...the generator writes `api/javadoc/com/example/Book.md` with this
frontmatter:

```yaml title="Generated frontmatter"
---
title: "Book"
summary: "A single book in the shelf, immutable once created."
tags: [api, javadoc]
---
```

And this body, reproduced below not as a code block but rendered for real -
this *is* the generated page's actual body, live, including the filter
toolbar. Try the Fields chip, or type "title" into the search box:

---

# Book

`com.example.Book`

A single book in the shelf, immutable once created. Two books are
considered equal only by reference, not by content.

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
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='constructor' }" @click="k='constructor'">Constructors</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='method' }" @click="k='method'">Methods</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='field' }" @click="k='field'">Fields</button>
</div>

<div x-show="(k==='all'||k==='constructor')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">

## Constructors

<div class="bx-filter-item" data-n="Book(java.lang.String title, java.lang.String author)" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">

### `Book(java.lang.String title, java.lang.String author)`

Creates a book with the given title and author.

- **Parameter** `title` - the book's title
- **Parameter** `author` - the book's author


</div>


</div>

<div x-show="(k==='all'||k==='method')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">

## Methods

<div class="bx-filter-item" data-n="com.example.Book withTitle(java.lang.String newTitle)" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">

### `com.example.Book withTitle(java.lang.String newTitle)`

Returns a copy of this book with a new title.

- **Parameter** `newTitle` - the new title
- **Returns** a copy of this book with the given title


</div>


</div>

<div x-show="(k==='all'||k==='field')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">

## Fields

<div class="bx-filter-item" data-n="public static final int MAX_TITLE_LENGTH" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">

### `public static final int MAX_TITLE_LENGTH`

The longest title this shelf will accept.


</div>


</div>

</div>

---

Notice the class's first sentence became the frontmatter `summary`, while
the page body carries the *full* doc comment (both sentences) - that's real
`DocCommentTree` first-sentence extraction, not a simplification made for
this example. Notice too that the toolbar/chips/search box above are real,
working Alpine.js - not a screenshot - built entirely from plain HTML
attributes and a small inline stylesheet driven by this theme's own
`--bxsites-*` variables, so it looks right in whichever theme you're
reading this in without bx-sites itself needing any changes.

## What this doesn't cover

**Deliberately scoped down for v1, not a complete Javadoc-to-Markdown
converter** - this is the heaviest of the three Spring Boot generators.
Nested and package-private types are skipped entirely; only each member's
*own* doc comment is used, never an inherited one; inline HTML in doc
comments is stripped rather than converted to Markdown; `{@link}`/`{@see}`
render as inline code with no cross-page hyperlink resolution; no
index/nav page is generated. A `record` also picks up its
compiler-generated accessors/`toString`/`equals`/`hashCode`, matching the
standard `javadoc` tool's own behavior - `Book` above is a plain class so
this example stays focused on the common case. See the Gradle/Maven guides
linked above for the full list.
