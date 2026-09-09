---
title: 検索
order: 2
icon: phosphor-duotone:magnifying-glass
tags: [ガイド, 検索]
---

# 検索

BxSites はデフォルトで1つの検索プロバイダーを同梱しており、`bxsites.yaml` の
[`searchProvider`](../configuration.md#searchprovider) で他のプロバイダーに
切り替えることもできます - どのプロバイダーが有効であっても、`search: true`/`false`
がマスターのオン/オフスイッチであり続けます。

## Local（デフォルト）

BxSites の検索は完全に静的でクライアントサイドです。[mkdocs](https://www.mkdocs.org/)
がデフォルトで使用するのと同じアプローチです: `build` 時に一度作成されるインデックスと、
訪問者のブラウザで実際の検索を行う [lunr.js](https://lunrjs.com/) の組み合わせです。
サーバー、データベース、外部検索サービスは一切不要です。

## 仕組み

1. `build` 時に、`SearchIndexer` がすべての非隠しページを巡回して
   `site/search-index.json` を書き出します: 各ページの `title`、`url`、
   フロントマターの `tags`、ページ上のすべての見出しのテキスト、
   本文の切り詰めたプレーンテキストのコピー（HTML タグを除去）を 1 エントリとして格納します。
2. 各テーマの `search.bxm` パーシャルが検索ボックスをレンダリングします。
   `layout.bxm` は `bxsites.yaml` の `search` が `true` で、`searchProvider.provider`
   が `"local"`（デフォルト）の場合のみこれ（と `lunr.js` + 共有 `search.js`
   スクリプト）を含めます（別のプロバイダーでは何が変わるかについては、
   下記の [その他の検索プロバイダー](#その他の検索プロバイダー) を参照してください）。
3. ブラウザでは、共有の `assets/search.js` ウィジェットが `search-index.json` を
   一度取得し、`lunr` インデックスをビルドして（`title` を最高優先度として、
   フロントマターの `tags`、`headings`、本文テキストの順）、キーストロークごとに
   再検索します。クエリごとのネットワークラウンドトリップは発生しません。

## キーボードショートカット

- **`/`** は、他のフィールドに入力中でない限り、ページのどこからでも検索ボックスに
  フォーカスします - [mkdocs-material](https://squidfunk.github.io/mkdocs-material/)
  と同じ慣習です。検索ボックスにはプラットフォームを判定した小さな
  `Ctrl K`/`⌘K` のヒントが表示されるため、下記のショートカットに気付けるように
  なっています。
- **Cmd/Ctrl+K** は代わりに、独立したコマンドパレット風のオーバーレイを開きます -
  背景の上に中央揃えで表示されるモーダルで、完全に JS だけで構築されており
  （テーマテンプレートの変更は不要）、すべての組み込みテーマで共有されます。
  上下矢印キーで結果のハイライトを移動し、**Enter** でハイライトされている項目に
  移動し、**Escape**（または背景のクリック）で閉じます - Algolia DocSearch、
  Pagefind、VitePress、Docusaurus がいずれも共有している、同じ
  「クイックファインド」/⌘K の慣習です。
- **`Escape`** は、上のパレットとは独立して、サイドバーのボックス自身の結果
  ドロップダウンも閉じ、フォーカスを外します。

このパレットは、`search-index.json` を再度取得するのではなく、サイドバーの
ウィジェット自身がすでにビルド済みの `lunr` インデックスをそのまま再利用します -
これは `local`（デフォルトのプロバイダー）でのみ利用可能です。`algolia` は
DocSearch 自体から Cmd+K を無料で得られ（`keyboardShortcuts` のデフォルトは
`true`）、`pagefind` は、そのライブラリ自体がキーバインドを行わないため、
`layout.bxm` が Cmd+K を配線して自身の `PagefindUI` にフォーカスします -
どちらもこのモジュール自身のパレットは開きません。

## 無効化

=== "YAML"
    ```yaml title="bxsites.yaml"
    search: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "search": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    search = false
    ```

`search-index.json` のビルドを完全にスキップし、すべてのレンダリングされたページから
検索ボックス、同梱された（バンドルされた）`lunr.js` スクリプト、共有 `search.js` ウィジェットをスキップします。
検索をオフにしたプロジェクトは検索関連のものを一切送信しません。これはマスタースイッチです -
どの `searchProvider` が設定されていても適用されます。

## インデックスのみ再ビルド

```bash frame="terminal" title="Terminal"
bxSites search-index
```

`search-index.json` を更新するだけの場合に便利です。`build` はその手順の一つとして
これを自動的に実行するため、通常のビルドの後に別途実行する必要はありません。
ローカルインデックスを使用するプロバイダー（`"local"`、および bx-sites がそれ以外に
認識しないプロバイダー）でのみ実行されます - `searchProvider.provider` が
`"algolia"` または `"pagefind"` の場合は、どちらもこれを一切使用しないため、
何もしません（`skipped: true`）。

## Algolia

`searchProvider.provider` を `"algolia"` に設定すると、検索ボックスが
[Algolia DocSearch](https://docsearch.algolia.com/) に切り替わります -
mkdocs-material、VitePress、Starlight、Docusaurus がいずれもサポートしている、
同じクローラーホスト型の検索です:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: algolia
      algolia:
        appId: ABC123
        apiKey: a1b2c3d4e5f6...
        indexName: my-docs
        insights: false
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "algolia",
    		"algolia": {
    			"appId": "ABC123",
    			"apiKey": "a1b2c3d4e5f6...",
    			"indexName": "my-docs",
    			"insights": false
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "algolia"

    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    insights = false
    ```

`appId`、`apiKey`、`indexName` は必須です - `apiKey` は DocSearch が発行する
**検索専用**の公開 API キーです（管理キーでは決してありません。すべての
レンダリング済みページにそのまま出荷されます）。`insights`（デフォルトは
`false`）は DocSearch 自身のクリック/コンバージョン分析を有効にします。

`algolia` が有効な場合:

- `search-index.json` はビルドされず、共有の `lunr.js`/`search.js` ウィジェットも
  出荷されません - Algolia は、BxSites がビルド時に書き出す何かからではなく、
  [DocSearch のクローラー](https://docsearch.algolia.com/docs/what-is-docsearch/)
  や自身の
  [Algolia Crawler](https://www.algolia.com/products/search-and-discovery/crawler/)
  設定によって投入された、自身がホストするインデックスから結果を提供します。
  サイトを DocSearch に個別に登録する（または自身でクローラーを実行する）必要が
  依然としてあります - BxSites はクライアントウィジェットを配線するだけです。
- 各組み込みテーマは代わりに空の `#bxsites-search-algolia` コンテナをレンダリングし、
  `layout.bxm` が jsDelivr から `@docsearch/css`/`@docsearch/js` を読み込んで
  それに対して `docsearch({...})` を呼び出します - DocSearch がそのコンテナに
  独自の検索ボタンとモーダルをレンダリングします。

## Pagefind

`searchProvider.provider` を `"pagefind"` に設定すると、検索ボックスが
[Pagefind](https://pagefind.app/) に切り替わります - こちらも完全に静的/
サーバーレスな検索エンジンですが、Algolia のようにクロールされるのではなく、
*ビルド済み* の `site/` HTML からインデックス化されます:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: pagefind
      pagefind: { bin: pagefind, options: [] }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "pagefind",
    		"pagefind": { "bin": "pagefind", "options": [] }
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "pagefind"

    [searchProvider.pagefind]
    bin = "pagefind"
    options = []
    ```

`pagefind` の両方のキーは任意です - `bin`（デフォルト `"pagefind"`）は実行
ファイルの名前/パスで、単純な名前の場合は `PATH` を基準に解決されます。
`options` はそのまま渡される追加の生の CLI フラグの配列です
（例: `["--exclude-selectors", ".no-index"]`）。

`pagefind` が有効な場合:

- **`pagefind` CLI 自体は事前にインストールされ `PATH` 上にある必要があります** -
  BxSites はこれを呼び出すだけです（BoxLang ネイティブのバインディングはなく、
  `lastUpdated`/`gh-deploy` が `git` を呼び出すのと同じ理由です）。代わりに
  インストールすることはありません。
  [Pagefind のインストールドキュメント](https://pagefind.app/docs/installation/)
  を参照してください。`lastUpdated` とは異なり、バイナリが見つからない/失敗した
  場合は、黙って動作を落とすのではなく `build` を派手に失敗させます
  （`BxSites.PagefindFailed`）- 設定された検索プロバイダーが機能しないサイトを
  出荷することは、ビルドが失敗するより悪いことだからです。
- すべてのドキュメントツリー（メイン + バージョン + ロケール）が書き出され、
  `sitemap.xml`/`llms.txt` が生成された直後に、BxSites は *ビルド済みの*
  `site/` 全体に対して `pagefind --site <siteDir> [...options]` を実行します -
  そのため、マルチバージョン/マルチロケールサイトでも、bx-sites 自身のツリー
  ごとの `search-index.json` とは異なり、すべてが一度のパスでインデックス
  されます。Pagefind は自身のバンドルを `site/pagefind/` に直接書き出します -
  自己ホスト型で、CDN は関与しません。
- `search-index.json` はビルドされず、共有の `lunr.js`/`search.js` ウィジェットも
  出荷されません（`algolia` と同様）- 同じ理由で `bxSites search-index` も
  何もしません（上記参照）。
- 各組み込みテーマは空の `#bxsites-search-pagefind` コンテナをレンダリングし、
  `layout.bxm` が `site/pagefind/pagefind-ui.{css,js}` を読み込んで
  それに対して `new PagefindUI({...})` を呼び出します - Pagefind がそのコンテナに
  独自のインライン検索ボックスと結果をレンダリングします。

## その他の検索プロバイダー

`searchProvider.provider` は `"local"`/`"algolia"`/`"pagefind"` に限定されません -
それ以外の値も `bxsites.yaml` にそのまま受け入れられます（BxSites 自身の設定
検証は上記の3つのプロバイダーのみをチェックします）。これにはプラグインフックは
ありません - 組み込みテーマは、認識されないプロバイダー名に対して単に何も
レンダリングしません。4つ目の検索サービス（Meilisearch、Typesense など）を
配線するのは、プロジェクトレベルの
[テーマオーバーライド](themes.md#テーマのオーバーライド)です: 組み込みテーマの
1つをプロジェクト自身の `theme/` フォルダにコピーし、`siteConfig.searchProvider`
を読み取っていつレンダリングするか判断しながら、`layout.bxm`/`search.bxm` に
自分のプロバイダーのマークアップ/スクリプトを追加します - `search.bxm` の
マウントポイント用の `searchProviderName eq "..."` 分岐、その CSS/JS 用の
`layout.bxm` 内の対応する分岐、そして（Algolia のようにクローラーホスト型
でない場合は）`build` 後に `site/` に対して必要となるインデックス処理ステップを
用意します - これは、このモジュール自身の `layout.bxm`/`BuildPipeline.bx` が
`algolia`/`pagefind` に対してすでに使っているのと同じ形です。
