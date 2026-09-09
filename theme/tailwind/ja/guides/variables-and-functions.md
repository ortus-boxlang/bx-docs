---
title: 変数とマジック関数
order: 12
icon: phosphor-duotone:magic-wand
tags: [ガイド, 変数, 関数]
---

# 変数とマジック関数

Markdown から繰り返し出てくる事実や繰り返しのロジックを追い出すための、互いに関連する
小さな2つの機能です: `bxsites.yaml` に一度だけ定義し、どのページにも `{{ }}` で
埋め込める**再利用可能な変数**、そして `docs/functions.bxs` に一度だけ書き、
同じ方法で - どこからでも、import もプラグインも配線も不要で - 呼び出せる小さな
BoxLang ヘルパーである**マジック関数**です。

どちらも1つの構文を共有します:

```text
{{ dotted.path }}          # a reusable variable
{{ $name(arg1, arg2) }}    # a magic function call
```

## 再利用可能な変数

`bxsites.yaml` に `variables` ブロックを追加します - フラットでもネストしていても、
好きな形で構いません:

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product:
        name: "BoxLang"
        supportEmail: "support@example.com"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": {
    			"name": "BoxLang",
    			"supportEmail": "support@example.com"
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [variables]
    company = "Ortus Solutions"

    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

その後、どの Markdown ページからでも、ドット区切りのパスで参照できます:

```markdown title="docs/index.md"
# Welcome to {{ company }}

We build {{ product.name }} tools. Need help? Write us at
{{ product.supportEmail }}.
```

ビルドすると次のようになります:

```html
<h1>Welcome to Ortus Solutions</h1>
<p>We build BoxLang tools. Need help? Write us at support@example.com.</p>
```

`{{ }}` 変数はビルド時に一度だけ、その時点での `bxsites.yaml` 自身の `variables`
ブロックに対して解決されます - 製品名を変更したり、サポートアドレスを更新したり、
年号を1箇所だけ変えたりすれば、それを使っているすべてのページが次のビルドで
その変更を反映します。設定リファレンスの
[`variables`](../configuration.md#variables) を参照してください。

## マジック関数

`docs/functions.bxs` ファイル（プロジェクトが `src/` を使っている場合は
`src/functions.bxs` - [はじめに](../getting-started.md) を参照）を追加します -
ただの BoxLang スクリプトです。先頭に `$` を付けて名前を付けた関数はすべて
*マジック関数* になります: Markdown の `{{ }}` から呼び出せるほか、プロジェクト自身の
[`theme/`](themes.md#テーマのオーバーライド) の `.bxm` オーバーライドから、裸のまま
直接呼び出すこともできます。

```bx title="docs/functions.bxs" linenums="1"
function $shout( text ) {
	return uCase( arguments.text ) & "!"
}

function $badge( label, kind = "info" ) {
	return '<span class="badge bg-' & arguments.kind & '">' & arguments.label & '</span>'
}
```

```markdown title="docs/index.md"
{{ $shout('this is important') }}

Status: {{ $badge('Stable', 'success') }}
```

ビルドすると次のようになります:

```html
<p>THIS IS IMPORTANT!</p>
<p>Status: <span class="badge bg-success">Stable</span></p>
```

マジック関数は `toString()` 可能なものなら何でも返せます - プレーンテキスト、HTML、
数値など - そしてそれは Markdown への変換より前に、そのページの Markdown へ
そのまま差し込まれるため、（上の `$badge()` のように）実際の HTML を返すことも
期待どおりに動作します。

同じ `functions.bxs` の中で先頭に `$` を*付けずに*宣言された関数は、単なる
プライベートヘルパーであり、同じファイル内の他の `$` 付き関数からのみ呼び出される
想定です（すべて同じスコープに読み込まれるため、一方が他方を裸のまま呼び出せます）
- `{{ }}` がそれを直接呼び出すことは決してできません（`$name(...)` という呼び出し先
だけが認識されます）し、そこも同じように技術的には到達可能であるとはいえ、
テーマオーバーライドが呼び出すべきドキュメント化された公開面の一部でもありません:

```bx title="docs/functions.bxs"
private string function formatPrice( amount ) {
	return "$" & numberFormat( arguments.amount, "9.99" )
}

function $price( amount ) {
	return formatPrice( arguments.amount )
}
```

### テーマオーバーライドからマジック関数を呼び出す

マジック関数はテンプレートスコープに直接バインドされているため、プロジェクト自身の
`theme/page.bxm`（または `layout.bxm`）は、すでに `variables.page`/
`variables.siteConfig` を読み取っているのと同じように、プレフィックスなしの裸の形で
それを呼び出せます:

```bx title="theme/page.bxm (excerpt)"
<p class="build-banner">#$shout( 'built with boxlang' )#</p>
```

### コンテキスト変数

マジック関数自身の本体もまた、固定された「サポート変数」のセットを - 引数なしで
裸のまま - 読み取ることができます。Markdown の `{{ }}` から呼び出された場合でも、
テーマオーバーライドから裸のまま呼び出された場合でも、これは同じです:

| 変数 | それが何か |
|---|---|
| `siteConfig` | サイト自身の `bxsites.yaml` 設定（すでにデフォルト適用/検証済み） |
| `page` | 現在のページ（下記の注記を参照 - Markdown から呼び出された場合、まだすべてのフィールドが埋まっているわけではありません） |
| `nav` | このツリー自身のナビゲーションツリー |
| `basePath` | ルート相対のベースパス。`/` で終わります |
| `versions` | バージョン切り替えのエントリ - `[ { label, url } ]` |
| `currentVersion` | 現在レンダリングされているのが `versions` のどのエントリか |
| `locales` | 言語切り替えのエントリ - `[ { code, label, url, dir, flag } ]` |
| `currentLocale` | 現在レンダリングされているのが `locales` のどのエントリのコードか |
| `currentLocaleDir` | 現在のロケールに対する `"ltr"`/`"rtl"` |
| `data` | このプロジェクト自身の[データファイル](data-files.md) - `docs/data/*.yaml`/`.json`、ファイルごとに1つのキー - プロジェクトが1つも持たない場合は `{}` |

```bx title="docs/functions.bxs"
function $sitename() {
	return siteConfig.name
}

function $pagetitle() {
	return page.title
}
```

```markdown title="docs/index.md"
Site: {{ $sitename() }}
Page: {{ $pagetitle() }}
```

**`page` はどちらの場所でも同じように完全というわけではありません。** Markdown から
呼び出された場合、`page` はこの特定のページ自身の構造体で、*ディスクから読み込まれた
ままの状態*です - `title`/`description`/`tags`/`icon`/`summary`/`ogImage`/`urlPath`/
`relativePath`/`body` などはすでに存在していますが、ツリー内のすべてのページの
変換が完了して初めて分かるフィールド - `toc`、`prevPage`/`nextPage`、`breadcrumbs`、
`editUrl`/`lastUpdated`、`iconHtml`、`markdownUrl`、`canonicalUrl`、`course` -
はまだ存在しません。`page.bxm` から裸のまま呼び出された場合、`page` はそれら
すべてを含む、完全に拡充された構造体です。それ以外のサポート変数
（`siteConfig`、`nav`、`basePath`、`versions`、`currentVersion`、`locales`、
`currentLocale`、`currentLocaleDir`）はどちらの場所でも同一です。

`page.course` は常に存在します（いかなる[コース](courses.md)にも属さない
ページでは、すべて空の `{id:"", courseTitle:"", lessonIndex:0, lessonTotal:0,
prevLesson:{title:"",url:""}, nextLesson:{title:"",url:""}, indexUrl:""}`
構造体になります）- `prevLesson`/`nextLesson` はその1つのコースだけに
スコープされており、上記のサイト全体の `prevPage`/`nextPage` とは独立して
います。こちらは常にナビツリー全体を歩きます。

### 引数の構文

マジック関数呼び出しの引数は、単純なカンマ区切りのリテラルまたは変数参照です -
このバージョンではまだネストした関数呼び出しや式は使えません:

- 数値: `{{ $discount(20) }}`
- クォート付き文字列: `{{ $greet('World') }}` または `{{ $greet("World") }}`
- ブール値: `{{ $badge('Beta', true) }}`
- `{{ }}` なしのドット区切り変数参照: `{{ $greet(product.name) }}`

## ビジュアライザーのレシピ

HTML を返すマジック関数はステータスバッジだけに限られません - ビジュアルセル
（スターレーティング、色付きチップ、プログレスバー）を、データベース駆動の
列ピッカーなしで得るための、汎用的な手段です - bx-sites の Git ベースで
プレーンな Markdown ソースには、そうした列ピッカーに相当するものが
ありません。以下の4つは、このサイト自身の
[`docs/functions.bxs`](https://github.com/ortus-boxlang/bx-sites/blob/development/docs/functions.bxs)
であり、まさにこのページ上でライブにレンダリングされています。

### 評価

```bx title="docs/functions.bxs"
function $stars( required numeric rating, numeric max = 5 ) {
	var filled = min( max( round( arguments.rating ), 0 ), arguments.max )
	var stars = repeatString( "★", filled ) & repeatString( "☆", arguments.max - filled )
	return '<span title="' & arguments.rating & ' out of ' & arguments.max & '" style="color:##f5a623;letter-spacing:2px">' & stars & '</span>'
}
```

`` `{{ $stars(4) }}` `` は次のようにレンダリングされます: {{ $stars(4) }}

### ステータスチップ

```bx title="docs/functions.bxs"
function $badge( required string label, string kind = "info" ) {
	var palette = {
		"info"    : { "bg" : "##e0edff", "fg" : "##1d4ed8" },
		"success" : { "bg" : "##dcfce7", "fg" : "##15803d" },
		"danger"  : { "bg" : "##fee2e2", "fg" : "##b91c1c" },
		"warning" : { "bg" : "##fef9c3", "fg" : "##854d0e" }
	}
	var pick = palette.keyExists( arguments.kind ) ? palette[ arguments.kind ] : { "bg" : "##f1f5f9", "fg" : "##475569" }
	return '<span style="display:inline-block;padding:0.1em 0.6em;border-radius:999px;font-size:0.85em;font-weight:600;background:'
		& pick.bg & ";color:" & pick.fg & '">' & encodeForHTML( arguments.label ) & "</span>"
}
```

`` `{{ $badge('Stable', 'success') }}` `` は次のようにレンダリングされます: {{ $badge('Stable', 'success') }} - そして `` `{{ $badge('Beta', 'info') }}` `` は: {{ $badge('Beta', 'info') }}

### プログレスバー

```bx title="docs/functions.bxs"
function $progress( required numeric percent ) {
	var pct = min( max( arguments.percent, 0 ), 100 )
	return '<span style="display:inline-block;width:120px;height:8px;background:##e5e7eb;border-radius:999px;overflow:hidden;vertical-align:middle"><span style="display:block;height:100%;width:'
		& pct & '%;background:##2563eb"></span></span> ' & pct & "%"
}
```

`` `{{ $progress(72) }}` `` は次のようにレンダリングされます: {{ $progress(72) }}

### トレンドインジケーター

```bx title="docs/functions.bxs"
function $trend( required numeric value ) {
	var isUp = arguments.value >= 0
	var arrow = isUp ? "▲" : "▼"
	var color = isUp ? "##16a34a" : "##dc2626"
	var sign = isUp ? "+" : ""
	return '<span style="color:' & color & ';font-weight:600">' & arrow & " " & sign & numberFormat( arguments.value, "0.0" ) & "%</span>"
}
```

`` `{{ $trend(4.2) }}` `` は次のようにレンダリングされます: {{ $trend(4.2) }} - `` `{{ $trend(-1.8) }}` `` は: {{ $trend(-1.8) }}

### テーブルセルの中で

`{{ }}` は [テーブル](tables.md) がパースされるよりも前に、生の
Markdown に対して解決されます。そのため、上記のいずれも、ページ内の他の場所と
まったく同じように、パイプテーブルのセルの中でも動作します:

```markdown title="Example" linenums="1"
| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |
```

次のようにレンダリングされます:

| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |

## 構文をそのまま表示する

フェンス付きコードブロック（3つ以上のバックティック、このページのすべての例と同様）
の中に書かれた `{{ }}` は、解決されることなく完全にそのまま残されます - これは
このモジュールがすでに `$...$` の数式や `=== "Tab"` のコンテンツタブに対して
採用しているのと同じ規約です。この2つとは異なり、*インライン* コード
（`` `{{ example }}` ``、シングルまたはダブルのバックティック）で示された `{{ }}` も
同様に保護されます - 上記の各箇条書きでインラインで示されている
`` `{{ $discount(20) }}` `` は、実際に動作する本物の実例です。

変数パスにも `$name(...)` 呼び出しにも見えない中身を持つ `{{ }}` - 例えば、地の文で
示された別のテンプレートエンジン自身の `{{ }}` 構文など - は、エラーとして扱われずに
そのまま残されます。変数やマジック関数呼び出しに*見える*のに解決できないトークンだけが
ビルドを失敗させます（下記の[エラー](#エラー)を参照）- これは意図的な挙動で、
関係のない `{{ }}` テキストを壊れた構文として誤読することなく、実際のタイプミスを
きちんと捕捉するためです。

## スコープ

- `functions.bxs` はプロジェクト全体のものです - 1つのファイルが一度だけ読み込まれ、
  同じマジック関数のセットが、メインツリーとすべての
  [バージョン](versioning.md)/[ロケール](i18n.md) ツリーを横断して、すべてのページで
  利用可能になります。`docs/versions/<name>/` や `docs/i18n/<code>/` へ複製する
  必要はありません。
- `variables` も同様に、単一の、プロジェクト全体の `bxsites.yaml` ブロックです -
  それ自体はロケールごとに翻訳できるものではありません。言語ごとに異なる変数テキストを
  求める多言語プロジェクトは、代わりに `siteConfig.i18n.defaultLocale.code` で
  分岐するマジック関数を使うか、単に値自体をロケールに依存しないもの
  （製品名、サポートメールなど）にしておくことができます。

## 予約された名前

`theme/page.bxm`/`layout.bxm` のオーバーライドがマジック関数を裸の形
（`$name(...)`）で呼び出せるのは、読み込まれたすべての関数 - `$` 付きであれ
プライベートヘルパーであれ - が、どのテーマもすでに読み取っている組み込みの
`variables.page`/`variables.siteConfig`/その他と並んで、まさにそのテンプレート自身の
レンダリングスコープに直接バインドされているからです。つまり、`functions.bxs`
の関数がそれらのいずれかと同じ名前を持つと、すでにその名前は使われています:
プライベートヘルパー自身の名前として `page`、`nav`、`siteConfig`、`themeDir`、
`basePath`、`moduleAssetsDir`、`versions`、`currentVersion`、`locales`、
`currentLocale`、`currentLocaleDir`、`strings`、`requiredFiles`、
`stringsResolver`、`data` は避けてください（`$` 付きの
マジック関数がこれらのいずれかと衝突することは決してありません。これらはどれも
`$` から始まらないためです）。`data` 自身の予約名についての注記は、
[データファイル: スコープ](data-files.md#scope) を参照してください。

## エラー

- `BxSites.UnknownVariable` - `{{ dotted.path }}`（または変数参照に見える
  `$name(...)` の引数）が `bxsites.yaml` の `variables` ブロック内の何とも
  一致しない場合。
- `BxSites.UnknownFunction` - `{{ $name(...) }}` の呼び出しが `docs/functions.bxs`
  内のどの `$` 付き関数とも一致しない場合。
- `BxSites.InvalidFunctions` - `docs/functions.bxs` の読み込みに失敗した場合
  （ファイル自体の BoxLang 構文エラー）。
- `BxSites.InvalidConfig` - `bxsites.yaml` の `variables` キーは存在するが、
  オブジェクトでない場合。
