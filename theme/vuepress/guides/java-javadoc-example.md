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
top-level type, emitting one page per type. Given this class:

```java title="com/example/Book.java"
package com.example;

/**
 * A single book in the shelf, immutable once created. Two books are
 * considered equal only by reference, not by content.
 */
public class Book {

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
this *is* the generated page's actual body, live:

---

# Book

`com.example.Book`

A single book in the shelf, immutable once created. Two books are
considered equal only by reference, not by content.

## Constructors

### `Book(java.lang.String title, java.lang.String author)`

Creates a book with the given title and author.

- **Parameter** `title` - the book's title
- **Parameter** `author` - the book's author

## Methods

### `com.example.Book withTitle(java.lang.String newTitle)`

Returns a copy of this book with a new title.

- **Parameter** `newTitle` - the new title
- **Returns** a copy of this book with the given title

---

Notice the class's first sentence became the frontmatter `summary`, while
the page body carries the *full* doc comment (both sentences) - that's real
`DocCommentTree` first-sentence extraction, not a simplification made for
this example.

## What this doesn't cover

**Deliberately scoped down for v1, not a complete Javadoc-to-Markdown
converter** - this is the heaviest of the three Spring Boot generators.
Nested and package-private types (and fields) are skipped entirely; only
each member's *own* doc comment is used, never an inherited one; inline
HTML in doc comments is stripped rather than converted to Markdown;
`{@link}`/`{@see}` render as inline code with no cross-page hyperlink
resolution; no index/nav page is generated. A `record` also picks up its
compiler-generated accessors/`toString`/`equals`/`hashCode`, matching the
standard `javadoc` tool's own behavior - `Book` above is a plain class so
this example stays focused on the common case. See the Gradle/Maven guides
linked above for the full list.
