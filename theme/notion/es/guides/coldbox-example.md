---
title: Ejemplo de salida de ColdBox
order: 6.10
icon: phosphor-duotone:code
tags: [guías, boxlang, coldbox, example]
---

# Ejemplo de salida de ColdBox

Un ejemplo real de lo que produce `bxSites coldbox`. Todo lo
de abajo salió de una ejecución real contra la pequeña aplicación Bookshelf
que se esboza aquí: nada es una ilustración escrita a mano.

La aplicación declara un router, un handler, un modelo, un binder de
WireBox, un scheduler, un interceptor y un módulo `api` con router y
handler propios:

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

Una sola ejecución de `bxSites coldbox` escribe once páginas. Cinco están
reproducidas abajo, renderizadas de verdad en vez de como bloques de
código.

Las páginas generadas se enlazan entre sí; en estos extractos esas
referencias aparecen como código plano, porque sus destinos solo existen
dentro de un sitio generado.

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

Vale la pena notar en esa tabla: `resources( "books" )` se expandió en las
siete rutas que ColdBox genera, el `apiResources()` del módulo en cinco
más, llevando el entry point `/api` que declara su `ModuleConfig`, y la
ruta por convención conservó su patrón literal.

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

La sección de rutas se construye emparejando los destinos de las rutas con
el handler, así que quien abre una página de handler ve enseguida cómo se
alcanza y qué acción golpea cada URL. `preHandler` queda aparte como el
hook de ciclo de vida que es, y el privado `findOr404` no aparece nunca.

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

El trabajo de una tarea programada es un closure, no un literal, así que no
hay nada que resolver estáticamente. En vez de dejar vacía la columna más
útil, se conserva el texto fuente del closure tal cual: es exactamente lo
que ejecutará el scheduler.

Consulta [Aplicaciones ColdBox](coldbox.md) para la configuración y para lo
que la lectura estática deliberadamente no puede ver, y
[Ejemplo de salida de DocBox](docbox-example.md) para la referencia de API
que producen las clases del mismo proyecto.
