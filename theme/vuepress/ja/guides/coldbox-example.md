---
title: ColdBox 出力例
order: 6.10
icon: phosphor-duotone:code
tags: [ガイド, boxlang, coldbox, example]
---

# ColdBox 出力例

`bxSites coldbox` が実際に生成するものの例です。以下は
すべて、ここに示す小さな Bookshelf アプリケーションに対する実際の実行結果
であり、手書きの説明ではありません。

このアプリケーションは、ルーター、ハンドラー1つ、モデル1つ、WireBox
バインダー、スケジューラー、インターセプター、そして独自のルーターと
ハンドラーを持つ `api` モジュールを宣言しています。

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

`bxSites coldbox` を1回実行すると11ページが生成されます。そのうち5ページを
コードブロックではなく実際にレンダリングして示します。

生成されたページは相互にリンクしますが、この抜粋ではリンク先が生成後の
サイト内にしか存在しないため、プレーンなコードとして示しています。

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

この表で注目すべき点: `resources( "books" )` は ColdBox が生成する7本の
ルートに展開され、モジュールの `apiResources()` はさらに5本に展開されて
`ModuleConfig` が宣言する `/api` エントリーポイントを反映し、規約ルートは
元のパターンをそのまま保っています。

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

ルートのセクションは、ルートのターゲットをハンドラーに突き合わせて構成され
ます。ハンドラーのページを開いた読者は、どう到達されるか、どの URL がどの
アクションを呼ぶかをすぐに把握できます。`preHandler` はライフサイクル
フックとして別扱いになり、private の `findOr404` は現れません。

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

スケジュールタスクの処理内容はクロージャーであってリテラルではないため、
静的に解決できるものはありません。もっとも有用な列を空にする代わりに、
クロージャーのソーステキストをそのまま持ち込んでいます。これはスケジュー
ラーが実行する内容そのものです。

設定と、静的な読み取りが意図的に見ないものについては
[ColdBoxアプリケーション](coldbox.md) を、同じプロジェクトのクラスが生む
API リファレンスについては [DocBox 出力例](docbox-example.md) を参照して
ください。
