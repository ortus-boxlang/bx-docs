---
title: ColdBox Output Example
order: 6.10
icon: phosphor-duotone:code
tags: [guides, boxlang, coldbox, example]
---

# ColdBox Output Example

A real example of what `bxSites coldbox` produces. Everything
below came out of an actual run against the small Bookshelf application
sketched here - nothing is hand-written illustration.

The application declares a router, one handler, one model, a WireBox
binder, a scheduler, an interceptor, and an `api` module with a router and
handler of its own:

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

One `bxSites coldbox` run over that writes eleven pages. Five of them are
reproduced below, rendered for real rather than as code blocks.

The generated pages cross-link each other; in these excerpts those
references are shown as plain code, since their targets only exist inside a
generated site.

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

Worth noticing in that table: `resources( "books" )` expanded into the
seven routes ColdBox generates for it, the module's `apiResources()`
expanded into five more and carried the `/api` entry point its
`ModuleConfig` declares, and the conventions route kept its literal
pattern.

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

The Routes section is built by matching route targets back to the handler,
so a reader landing on a handler page immediately sees how it is reached
and which action each URL hits. `preHandler` is separated out as the
lifecycle hook it is, and the private `findOr404` never appears.

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

A scheduled task's work is a closure, not a literal, so there is nothing to
resolve statically. Rather than leave the most useful column empty, the
closure's own source text is carried through as written - which is exactly
what the scheduler will run.

See [ColdBox Applications](coldbox.md) for configuration and for what
static reading deliberately cannot see, and
[DocBox Output Example](docbox-example.md) for the API reference the same
project's classes produce.
