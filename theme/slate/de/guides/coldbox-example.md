---
title: ColdBox-Ausgabebeispiel
order: 6.10
icon: phosphor-duotone:code
tags: [anleitungen, boxlang, coldbox, example]
---

# ColdBox-Ausgabebeispiel

Ein echtes Beispiel dafür, was `bxSites coldbox` erzeugt.
Alles unten stammt aus einem tatsächlichen Lauf gegen die hier skizzierte
kleine Bookshelf-Anwendung - nichts ist von Hand geschrieben.

Die Anwendung deklariert einen Router, einen Handler, ein Model, einen
WireBox-Binder, einen Scheduler, einen Interceptor und ein `api`-Modul mit
eigenem Router und Handler:

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

Ein einziger `bxSites coldbox`-Lauf schreibt elf Seiten. Fünf davon sind
unten wiedergegeben, wirklich gerendert statt als Codeblock.

Die erzeugten Seiten verlinken einander; in diesen Auszügen stehen die
Verweise als reiner Code, da ihre Ziele nur in einer erzeugten Site
existieren.

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

Bemerkenswert in dieser Tabelle: `resources( "books" )` wurde in die sieben
Routen aufgelöst, die ColdBox daraus erzeugt, das `apiResources()` des
Moduls in fünf weitere - mit dem `/api`-Entry-Point aus seiner
`ModuleConfig` -, und die Konventionsroute behielt ihr wörtliches Pattern.

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

Der Routen-Abschnitt entsteht, indem Routenziele auf den Handler
zurückgeführt werden: Wer eine Handler-Seite öffnet, sieht sofort, wie er
erreicht wird und welche Action jede URL trifft. `preHandler` steht separat
als der Lifecycle-Hook, der er ist, und das private `findOr404` taucht nie
auf.

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

Die Arbeit eines geplanten Tasks ist eine Closure, kein Literal - statisch
gibt es da nichts aufzulösen. Statt die interessanteste Spalte leer zu
lassen, wird der Quelltext der Closure unverändert übernommen; genau das
führt der Scheduler aus.

Siehe [ColdBox-Anwendungen](coldbox.md) für die Konfiguration und die
bewussten Grenzen des statischen Lesens sowie
[DocBox-Ausgabebeispiel](docbox-example.md) für die API-Referenz, die die
Klassen desselben Projekts ergeben.
