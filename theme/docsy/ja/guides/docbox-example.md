---
title: DocBox 出力例
order: 6.9
icon: phosphor-duotone:code
tags: [ガイド, boxlang, docbox, example]
---

# DocBox 出力例

[`bxSites docbox`](docbox.md) が実際に生成するものの例です。以下のページ
はすべて、示したソースに対する実際の実行結果であり、手書きの説明では
ありません。

プロジェクトの `models/` フォルダーに次のクラスがあるとします。

```java title="models/Book.bx"
/**
 * A single book on the shelf, immutable once created.
 *
 * @author Ortus Solutions
 */
class singleton accessors=true {

	/**
	 * The book's title
	 */
	property name="title" type="string";

	/**
	 * Where book records are read from
	 */
	property name="datasource" type="string" inject="coldbox:setting:bookDatasource";

	/**
	 * Build a book.
	 *
	 * @title The book's title
	 * @author The book's author
	 */
	function init( required string title, string author = "Unknown" ) {
		return this
	}

	/**
	 * Return a copy of this book with a new title.
	 *
	 * @newTitle The replacement title
	 *
	 * @return a copy carrying the new title
	 */
	Book function withTitle( required string newTitle ) {}

	private function normalize() {}
}
```

...`bxSites docbox` は `api/docbox/models/Book.md` を次の frontmatter で
書き出します。

```yaml title="Frontmatter"
---
title: "Book"
summary: "A single book on the shelf, immutable once created."
tags: [api, docbox]
---
```

そして本文は次のとおりです。コードブロックではなく実際にレンダリングして
います。これが生成されたページの本文そのもので、フィルターバーも動作
します。Private チップを押すか、検索ボックスに "title" と入力してみて
ください。

---

## Book

`models.Book` &middot; Class

A single book on the shelf, immutable once created.

### Annotations

- `@accessors` true
- `@singleton`

### Properties

| Property | Type | Default | Description |
|---|---|---|---|
| `title` | `string` | &mdash; | The book's title |
| `datasource` | `string` | &mdash; | Where book records are read from `@inject coldbox:setting:bookDatasource` |

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
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='constructor' }" @click="k='constructor'">Constructor</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='public' }" @click="k='public'">Public</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='private' }" @click="k='private'">Private</button>
</div>


<div x-show="(k==='all'||k==='constructor')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Constructor

<div class="bx-filter-item" data-n="Any init( required string title, string author = &quot;Unknown&quot; )" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Any init( required string title, string author = "Unknown" )`

Build a book.

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `title` | `string` | yes | &mdash; | The book's title |
| `author` | `string` | no | `Unknown` | The book's author |


</div>



</div>


<div x-show="(k==='all'||k==='public')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Public methods

<div class="bx-filter-item" data-n="Book withTitle( required string newTitle )" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Book withTitle( required string newTitle )`

Return a copy of this book with a new title.

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `newTitle` | `string` | yes | &mdash; | The replacement title |

- **Returns** a copy carrying the new title


</div>



</div>


<div x-show="(k==='all'||k==='private')&&(!q||Array.from($el.querySelectorAll('.bx-filter-item')).some(el=>el.dataset.n.toLowerCase().includes(q.toLowerCase())))">


### Private methods

<div class="bx-filter-item" data-n="Any normalize()" x-show="!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase())">


#### `Any normalize()`


</div>



</div>


</div>

---

## 注目すべき点

- **プロパティ表が存在すること。** DocBox 自身の JSON ストラテジーは宣言
  された `property` ブロックを落とします。bx-sites は DocBox が使ったのと
  同じクラスメタデータから読み直すため、`title` と `datasource` が説明つき
  で並び、`datasource` の WireBox `inject` も残ります。
- **`@return` のテキストも同じ理由で残ります。** `withTitle` の下の
  Returns 行を見てください。
- **private メソッドも含まれます。** Javadoc ジェネレーターとは異なり、
  DocBox が報告するのでドキュメント化され、不要なときは Private チップで
  隠せます。
- **`init` はコンストラクター**として、メソッドより前の独立したセクション
  になります。

設定については [DocBox APIリファレンス](docbox.md) を、同じプロジェクトから
ColdBox verb が生成するものについては
[ColdBox 出力例](coldbox-example.md) を参照してください。
