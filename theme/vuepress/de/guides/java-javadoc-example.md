---
title: Javadoc-Ausgabebeispiel
order: 6.6
icon: phosphor-duotone:code
tags: [anleitungen, java, spring-boot, javadoc, beispiel]
---

# Javadoc-Ausgabebeispiel

Ein reales Beispiel dessen, was `bxSitesJavadocDoc` (Gradle) /
`bxsites:javadoc` (Maven) tatsächlich erzeugt - siehe
[Gradle-Plugin](gradle-plugin.md#spring-boot-doc-generation) oder
[Maven-Plugin](maven-plugin.md#spring-boot-doc-generation), um das im
eigenen Projekt zu aktivieren.

Richte es auf die eigenen `.java`-Quellen, und es durchläuft jeden
öffentlichen Top-Level-Typ und erzeugt eine Seite pro Typ. Bei dieser
Klasse:

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

...schreibt der Generator `api/javadoc/com/example/Book.md` mit diesem
Frontmatter:

```yaml title="Erzeugtes Frontmatter"
---
title: "Book"
summary: "A single book in the shelf, immutable once created."
tags: [api, javadoc]
---
```

Und diesem Body, hier nicht als Codeblock, sondern echt gerendert - das
*ist* der tatsächliche Body der erzeugten Seite, live:

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

Man beachte: Der erste Satz der Klasse wird zum Frontmatter-`summary`,
während der Seiten-Body den *vollständigen* Doc-Kommentar (beide Sätze)
trägt - das ist echte `DocCommentTree`-Extraktion des ersten Satzes,
keine Vereinfachung für dieses Beispiel.

## Was das nicht abdeckt

**Bewusst reduzierter Umfang für v1, kein vollständiger
Javadoc-zu-Markdown-Konverter** - dies ist der aufwendigste der drei
Spring-Boot-Generatoren. Verschachtelte und paketprivate Typen (sowie
Felder) werden vollständig übersprungen; es wird immer nur der *eigene*
Doc-Kommentar eines Members verwendet, niemals ein geerbter; Inline-HTML
in Doc-Kommentaren wird entfernt statt in Markdown umgewandelt;
`{@link}`/`{@see}` werden als Inline-Code ohne seitenübergreifende
Verlinkung gerendert; es wird keine Index-/Nav-Seite erzeugt. Ein
`record` bringt außerdem seine vom Compiler generierten
Accessor-Methoden/`toString`/`equals`/`hashCode` mit, genau wie das
Standard-`javadoc`-Tool selbst - `Book` oben ist eine einfache Klasse,
damit dieses Beispiel auf den Standardfall fokussiert bleibt. Siehe die
oben verlinkten Gradle-/Maven-Guides für die vollständige Liste.
