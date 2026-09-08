---
title: Esempio di output Javadoc
order: 6.6
icon: phosphor-duotone:code
tags: [guide, java, spring-boot, javadoc, esempio]
---

# Esempio di output Javadoc

Un esempio reale di ciò che `bxSitesJavadocDoc` (Gradle) /
`bxsites:javadoc` (Maven) produce davvero - vedi
[Plugin Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin Maven](maven-plugin.md#spring-boot-doc-generation) per sapere come
attivarlo nel tuo progetto.

Puntalo ai sorgenti `.java` del tuo progetto e percorre ogni tipo pubblico
di primo livello, generando una pagina per tipo. Data questa classe:

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

...il generatore scrive `api/javadoc/com/example/Book.md` con questo
frontmatter:

```yaml title="Frontmatter generato"
---
title: "Book"
summary: "A single book in the shelf, immutable once created."
tags: [api, javadoc]
---
```

E questo corpo, riprodotto qui sotto non come un blocco di codice ma
renderizzato per davvero - questo *è* il corpo reale della pagina
generata, live:

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

Nota come la prima frase della classe diventi il `summary` del
frontmatter, mentre il corpo della pagina porta il commento di
documentazione *completo* (entrambe le frasi) - questa è vera estrazione
della prima frase tramite `DocCommentTree`, non una semplificazione fatta
per questo esempio.

## Cosa non copre

**Ambito deliberatamente ridotto per la v1, non un convertitore completo
da Javadoc a Markdown** - questo è il più pesante dei tre generatori
Spring Boot. I tipi annidati e package-private (e i campi) vengono
saltati del tutto; viene usato solo il commento di documentazione
*proprio* di ciascun membro, mai uno ereditato; l'HTML inline nei
commenti di documentazione viene rimosso anziché convertito in Markdown;
`{@link}`/`{@see}` vengono renderizzati come codice inline senza
risoluzione di collegamento ipertestuale tra pagine; non viene generata
alcuna pagina indice/nav. Un `record` porta con sé anche i suoi accessori
generati dal compilatore/`toString`/`equals`/`hashCode`, proprio come il
comportamento dello strumento `javadoc` standard - `Book` sopra è una
classe semplice, così che questo esempio resti concentrato sul caso
comune. Consulta le guide Gradle/Maven collegate sopra per l'elenco
completo.
