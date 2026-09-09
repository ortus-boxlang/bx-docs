---
title: Esempio di output ColdBox
order: 6.10
icon: phosphor-duotone:code
tags: [guide, boxlang, coldbox, example]
---

# Esempio di output ColdBox

Un esempio reale di ciò che produce `bxSites coldbox`. Tutto
quello che segue proviene da un'esecuzione vera sulla piccola applicazione
Bookshelf abbozzata qui: niente è scritto a mano.

L'applicazione dichiara un router, un handler, un model, un binder WireBox,
uno scheduler, un interceptor e un modulo `api` con router e handler
propri:

```java title="config/Router.bx"
class {
	function configure() {
		route( "/" ).as( "home" ).to( "books.index" )
		route( "/books/:id" ).to( "books.show" )
		resources( "books" )
		route( "/old/shelf" ).toRedirect( "/books" )
		route( ":handler/:action?" ).end()
	}
}
```

```java title="handlers/Books.bx"
/**
 * The public bookshelf endpoints.
 */
class {

	/**
	 * List every book on the shelf.
	 *
	 * @event The request context
	 *
	 * @return the rendered listing
	 */
	function index( event, rc, prc ) {}

	/**
	 * Show one book by id.
	 *
	 * @id The book id
	 */
	function show( event, rc, prc ) {}

	/**
	 * Runs before every action in this handler.
	 */
	function preHandler( event, rc, prc, action, eventArguments ) {}

	private function findOr404( id ) {}
}
```

```java title="modules_app/api/ModuleConfig.bx"
class {
	this.title = "Bookshelf API"
	this.author = "Ortus Solutions"
	this.version = "1.0.0"
	this.entryPoint = "api"
	this.dependencies = [ "cbsecurity" ]
}
```

Una sola esecuzione di `bxSites coldbox` scrive undici pagine. Cinque sono
riprodotte qui sotto, renderizzate davvero anziché come blocchi di codice.

Le pagine generate si collegano tra loro; in questi estratti quei
riferimenti compaiono come codice semplice, perché i loro target esistono
solo dentro un sito generato.

---

## `routes.md`

## Routes

16 route(s), in the order the routers declare them - ColdBox matches the first one that fits, so order is meaningful.

| Verbs | Pattern | Target | Name | Module |
|---|---|---|---|---|
| ANY | `/` | `books.index` | `home` | &mdash; |
| ANY | `/books/:id` | `books.show` | &mdash; | &mdash; |
| GET | `/books` | `books.index` | &mdash; | &mdash; |
| GET | `/books/new` | `books.new` | &mdash; | &mdash; |
| POST | `/books` | `books.create` | &mdash; | &mdash; |
| GET | `/books/:id` | `books.show` | &mdash; | &mdash; |
| GET | `/books/:id/edit` | `books.edit` | &mdash; | &mdash; |
| PUT/PATCH | `/books/:id` | `books.update` | &mdash; | &mdash; |
| DELETE | `/books/:id` | `books.delete` | &mdash; | &mdash; |
| ANY | `/old/shelf` | `/books` *redirect* | &mdash; | &mdash; |
| ANY | `:handler/:action?` | *by convention* | &mdash; | &mdash; |
| GET | `/api/books` | `books.index` | &mdash; | `api` |
| POST | `/api/books` | `books.create` | &mdash; | `api` |
| GET | `/api/books/:id` | `books.show` | &mdash; | `api` |
| PUT/PATCH | `/api/books/:id` | `books.update` | &mdash; | `api` |
| DELETE | `/api/books/:id` | `books.delete` | &mdash; | `api` |

A route marked *by convention* has no explicit target: ColdBox resolves the handler and action from the URL pattern's own placeholders.

---

Da notare in quella tabella: `resources( "books" )` si è espanso nelle sette
rotte che ColdBox genera, l'`apiResources()` del modulo in altre cinque,
con l'entry point `/api` dichiarato dal suo `ModuleConfig`, e la rotta per
convenzione ha mantenuto il suo pattern letterale.

---

## `handlers/Books.md`

## Books

`handlers.Books`

The public bookshelf endpoints.

### Routes

| Verbs | Pattern | Action |
|---|---|---|
| ANY | `/` | `index` |
| ANY | `/books/:id` | `show` |
| GET | `/books` | `index` |
| GET | `/books/new` | `new` |
| POST | `/books` | `create` |
| GET | `/books/:id` | `show` |
| GET | `/books/:id/edit` | `edit` |
| PUT/PATCH | `/books/:id` | `update` |
| DELETE | `/books/:id` | `delete` |

### Actions

#### `Any index( event, rc, prc )`

List every book on the shelf.

Reached by `/`, `/books`

| Parameter | Type | Required | Description |
|---|---|---|---|
| `event` | `Any` | no | The request context |
| `rc` | `Any` | no |  |
| `prc` | `Any` | no |  |

- **Returns** the rendered listing

#### `Any show( event, rc, prc )`

Show one book by id.

Reached by `/books/:id`, `/books/:id`

| Parameter | Type | Required | Description |
|---|---|---|---|
| `event` | `Any` | no |  |
| `rc` | `Any` | no |  |
| `prc` | `Any` | no |  |

### Lifecycle hooks

ColdBox calls these itself around the handler's actions - they aren't reachable as events of their own.

#### `Any preHandler( event, rc, prc, action, eventArguments )`

Runs before every action in this handler.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `event` | `Any` | no |  |
| `rc` | `Any` | no |  |
| `prc` | `Any` | no |  |
| `action` | `Any` | no |  |
| `eventArguments` | `Any` | no |  |

---

La sezione Rotte nasce associando i target delle rotte all'handler: chi apre
una pagina di handler vede subito come viene raggiunto e quale azione
colpisce ogni URL. `preHandler` resta a parte come l'hook di ciclo di vita
che è, e il privato `findOr404` non compare mai.

---

## `models/index.md`

## Models

| Model | Module | Scope | Injects |
|---|---|---|---|
| `Book` | &mdash; | `singleton` | 1 |

### WireBox mappings

Declared in the application's binder. A model with no mapping here is still injectable - WireBox maps the models directory by convention.

| Alias | Maps to | Kind | Scope | Module |
|---|---|---|---|---|
| `BookService` | `models.BookService` | class | `singleton` | &mdash; |
| `models.services` | `models.services` | directory | &mdash; | &mdash; |

---

## `modules/api.md`

## Bookshelf API

`api` &middot; mounted at `/api`

| | |
|---|---|
| Author | Ortus Solutions |
| Version | 1.0.0 |
| Entry point | api |
| Depends on | `cbsecurity` |

### Routes

| Verbs | Pattern | Target |
|---|---|---|
| GET | `/api/books` | `books.index` |
| POST | `/api/books` | `books.create` |
| GET | `/api/books/:id` | `books.show` |
| PUT/PATCH | `/api/books/:id` | `books.update` |
| DELETE | `/api/books/:id` | `books.delete` |

### Handlers

- `Books`

---

## `scheduled-tasks.md`

## Scheduled tasks

| Task | Schedule | Runs | Constraints | Module |
|---|---|---|---|---|
| Reindex the shelf | `every day at 02:00` | `runEvent( "books.reindex" )` | one server only | &mdash; |

---

Il lavoro di un task pianificato è una closure, non un letterale: staticamente
non c'è nulla da risolvere. Invece di lasciare vuota la colonna più utile,
il testo sorgente della closure viene riportato così com'è, ed è
esattamente ciò che lo scheduler eseguirà.

Vedi [Applicazioni ColdBox](coldbox.md) per la configurazione e per ciò che
la lettura statica non può vedere, e
[Esempio di output DocBox](docbox-example.md) per il riferimento API che
producono le classi dello stesso progetto.
