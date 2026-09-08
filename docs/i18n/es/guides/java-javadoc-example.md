---
title: Ejemplo de salida Javadoc
order: 6.6
icon: phosphor-duotone:code
tags: [guías, java, spring-boot, javadoc, ejemplo]
---

# Ejemplo de salida Javadoc

Un ejemplo real de lo que `bxSitesJavadocDoc` (Gradle) /
`bxsites:javadoc` (Maven) produce realmente - ver
[Plugin de Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin de Maven](maven-plugin.md#spring-boot-doc-generation) para saber
cómo activar esto en tu propio proyecto.

Apúntalo a las fuentes `.java` de tu propio proyecto y recorre cada tipo
público de nivel superior, generando una página por tipo. Dada esta
clase:

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

...el generador escribe `api/javadoc/com/example/Book.md` con este
frontmatter:

```yaml title="Frontmatter generado"
---
title: "Book"
summary: "A single book in the shelf, immutable once created."
tags: [api, javadoc]
---
```

Y este cuerpo, reproducido abajo no como un bloque de código sino
renderizado de verdad - este *es* el cuerpo real de la página generada,
en vivo:

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

Fíjate en que la primera frase de la clase se convierte en el `summary`
del frontmatter, mientras que el cuerpo de la página lleva el comentario
de documentación *completo* (ambas frases) - eso es extracción real de la
primera frase de `DocCommentTree`, no una simplificación hecha para este
ejemplo.

## Lo que esto no cubre

**Alcance deliberadamente reducido para v1, no un conversor completo de
Javadoc a Markdown** - este es el más pesado de los tres generadores de
Spring Boot. Los tipos anidados y de paquete privado (y los campos) se
omiten por completo; solo se usa el comentario de documentación *propio*
de cada miembro, nunca uno heredado; el HTML incrustado en los
comentarios de documentación se elimina en vez de convertirse a Markdown;
`{@link}`/`{@see}` se renderizan como código en línea sin resolución de
hipervínculo entre páginas; no se genera ninguna página de índice/nav. Un
`record` también añade sus accesores generados por el compilador/
`toString`/`equals`/`hashCode`, igual que el propio comportamiento de la
herramienta `javadoc` estándar - `Book` de arriba es una clase sencilla
para que este ejemplo se mantenga centrado en el caso común. Consulta las
guías de Gradle/Maven enlazadas arriba para la lista completa.
