---
title: 設定
order: 4
icon: phosphor-duotone:gear-six
summary: サイト設定のすべてのキー、デフォルト値、および動作。
tags: [リファレンス, 設定]
---

# 設定

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    name = "My Docs"
    description = ""
    baseURL = "/"
    search = true
    mcp = false
    nav = []
    social = []
    footer = false
    lastUpdated = false
    extraCss = []
    extraJs = []
    plugins = []
    variables = {}

    [theme]
    name = "bootstrap"
    options = {}
    logo = ""
    favicon = ""

    [markdown]
    enableAdmonition = true

    [repo]
    url = ""
    editUri = ""
    ```

すべてのプロジェクトはルートに1つのサイト設定ファイルを持ちます - デフォルトかつ推奨の形式である
`bxsites.yaml`（または `.yml`）か、そのまま使い続けたいプロジェクト向けの `bxsites.json` の
いずれかです。どちらも完全にサポートされており、まったく同じ結果になります。
`bxSites new` は `--format=json` を指定しない限り `bxsites.yaml` をスキャフォールドします
（[はじめに](getting-started.md#設定ファイルの形式) を参照）。プロジェクトに何らかの理由で
複数の設定ファイルが存在する場合は、`bxsites.yaml` が優先され、次に `bxsites.yml`、
最後に `bxsites.json` の順で使用されます。

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    name: "My Docs"
    description: ""
    baseURL: "/"
    theme:
      name: bootstrap
      options: {}
      logo: ""
      favicon: ""
    search: true
    mcp: false
    searchProvider:
      provider: local
      algolia: { appId: "", apiKey: "", indexName: "", insights: false }
    nav: []
    markdown:
      enableAdmonition: true
    repo:
      url: ""
      editUri: ""
    social: []
    footer: false
    lastUpdated: false
    mermaid: false
    math: false
    analytics:
      provider: ""
      id: ""
    ogImage: ""
    generateOgImages: false
    extraCss: []
    extraJs: []
    assets:
      fingerprint: true
      bundle: true
      images: { enabled: true, widths: [400, 800, 1200, 1600], formats: [original, webp] }
    plugins: []
    i18n:
      defaultLocale: { code: en, label: English }
      locales: []
    blog:
      postsPerPage: 10
      feed: true
    variables: {}
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"name": "My Docs",
    	"description": "",
    	"baseURL": "/",
    	"theme": {
    		"name": "bootstrap",
    		"options": {},
    		"logo": "",
    		"favicon": ""
    	},
    	"search": true,
    	"mcp": false,
    	"searchProvider": {
    		"provider": "local",
    		"algolia": { "appId": "", "apiKey": "", "indexName": "", "insights": false }
    	},
    	"nav": [],
    	"markdown": { "enableAdmonition": true },
    	"repo": {
    		"url": "",
    		"editUri": ""
    	},
    	"social": [],
    	"footer": false,
    	"lastUpdated": false,
    	"mermaid": false,
    	"math": false,
    	"analytics": {
    		"provider": "",
    		"id": ""
    	},
    	"ogImage": "",
    	"generateOgImages": false,
    	"extraCss": [],
    	"extraJs": [],
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": { "enabled": true, "widths": [400, 800, 1200, 1600], "formats": ["original", "webp"] }
    	},
    	"plugins": [],
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": []
    	},
    	"blog": {
    		"postsPerPage": 10,
    		"feed": true
    	},
    	"variables": {}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Main Components"
    children = [
      { title = "Quick Start", path = "guides/setup.md" },
      { path = "guides/deployment.md" }
    ]
    ```

必須なのは `name` のみで、それ以外はすべて上記のデフォルト値にフォールバックします。
`theme` オブジェクトは1階層のみマージされるため、`{theme: {name: material}}` だけを
指定してもデフォルトの（空の）`options` は保持されます。以下の各キーはどちらの形式でも
名前と構造が同じです - 下のどの例でもタブを切り替えれば、もう一方の形式で確認できます。

## `name`

ヘッダーのブランドマークとページタイトルに表示されるサイト名。必須。

## `description`

サイトの説明（省略可）。独自の `description` フロントマターを持たないページの
フォールバック `<meta name="description">` および `og:description` として使用されます
（[はじめに](getting-started.md#ページの追加) を参照）。

## `baseURL`

すべての内部リンク、アセットパス、ナビゲーションエントリのプレフィックスの付け方を制御し、
`sitemap.xml`、`robots.txt`、`llms.txt`、そして各ページ自身の
`<link rel="canonical">` タグのためのサイト正規 URL としても機能します。

- 空白または `"/"`（デフォルト）- リンクはルート相対のまま（`/page/`）で、`sitemap.xml` も、
  `robots.txt` 内の `Sitemap:` 行も、絶対 URL の `llms.txt` も、
  `<link rel="canonical">` タグも生成されません（それらを構築するための正規ドメインが
  ないため）。
- パスのみ（例: `"my-docs"` や `"/my-docs/"`）- サイトがそのサブパスから配信されると見なされ、
  すべての内部リンク、ナビゲーションエントリ、アセットにそのプレフィックスが付きます
  （`/my-docs/page/`）。この場合も絶対ドメインがないため `sitemap.xml`/正規タグは
  生成されません。
- 完全な URL（例: `"https://docs.example.com/"`）- パス部分（ここでは `/`）がベアパスと
  同様に使用され、**さらに** ビルド時にそのドメイン配下の非隠しページすべての絶対 URL を
  含む `sitemap.xml` が書き出され、`robots.txt` にそれを指す `Sitemap:` 行が追加され、
  各ページには正しい `<link rel="canonical">` が付きます（バージョン/ロケールツリー自身の
  ページは、メインサイトのではなく *そのツリー自身の* URL を指し続けます）。

`llms.txt`（[下記](#llmstxt) 参照）は常に書き出されます - `baseURL` が絶対 URL を
提供する場合はそちらが優先して使われるだけです。

## `llms.txt`

すべてのビルドで、サイトルートに `llms.txt` が書き出されます - これは、LLM ベースの
ツールがレンダリング済み HTML をクロールせずにサイトをナビゲートできるようにするための、
新しく登場した [llms.txt](https://llmstxt.org) 規約に従った、すべての非隠しページの
プレーンな Markdown インデックスです。専用の設定キーはなく、自動的に生成されます。
`baseURL` が完全な URL の場合は各リンクに絶対 URL を、そうでない場合は `basePath` 相対の
リンクを使用します。

## `sitemap.xml`

サイトルートに書き出されますが、`baseURL` が完全な URL の場合のみです（上記参照）-
サイトマップが意味を持つには絶対ドメインが必要だからです。
[sitemaps.org](https://www.sitemaps.org/) プロトコルに従って、すべての非隠しページを
列挙します。

## `robots.txt`

すべてのビルドで、サイトルートに `robots.txt` が書き出されます - デフォルトの許可的な
動作を変更したい場合を除き、設定キーは不要です:

=== "YAML"
    ```yaml title="bxsites.yaml"
    robots: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "robots": false }
    ```

- `true`（デフォルト）- すべてのクローラーに対して `Allow: /`、さらに `baseURL` が
  完全な URL の場合は `sitemap.xml` を指す `Sitemap:` 行が追加されます（上記参照）。
- `false` - 代わりにすべてのクローラーに対して `Disallow: /` となり、`Sitemap:` 行も
  付きません - 「このステージング/内部デプロイをまったくインデックスさせたくない」
  というよくあるニーズに対応します。これはあくまで *クローラー* のオプトアウトであり、
  アクセス制御ではありません - URL を知っている人には引き続きサイト全体が到達可能です。
  本当にアクセス自体を制限する必要がある場合は
  [デプロイ](guides/deployment.md#サイトへのアクセスを制限する) を参照してください。

オン/オフの切り替え以上のもの - 特定の disallow パス、複数の `Sitemap:` 行、
`Crawl-delay`、ユーザーエージェントごとのルールなど - が必要な場合は、独自の
`robots.txt` を `index.md` のすぐ隣に置いてください（`docs/robots.txt`、または
`src/` ベースのプロジェクトでは `src/robots.txt` -
[`docs/` または `src/`](getting-started.md#ページの追加) を参照）。生成されたものの
代わりにバイト単位でそのままコピーされ、ビルドのたびに使われます - このファイルが
存在する時点で、上記の `robots` キーは完全に無視されます。

## `theme`

- `theme.name` - 組み込みテーマのいずれか（`bootstrap`、`material`、`tailwind` など）、
  またはプロジェクトルートの `theme/` フォルダで提供するカスタムテーマの名前
  （[テーマ](guides/themes.md) を参照）
- `theme.logo` - ヘッダーのブランドマークでサイト名の横に表示される画像への
  パス/URL（デフォルトの「⚡」グリフの代わり）- 相対パス（例: `"assets/logo.svg"`、
  `docs/assets/` を基準に解決されます）には他の内部アセットと同様に `baseURL` が
  プレフィックスされます。絶対 URL はそのまま使用されます。空白（デフォルト）の場合、
  ヘッダーには「⚡ &lt;サイト名&gt;」が表示されます。
- `theme.favicon` - ファビコンへのパス/URL。`theme.logo` と同じ方法で解決されます。
  空白（デフォルト）の場合、`<link rel="icon">` は一切レンダリングされません
  （ブラウザ自身のデフォルト動作にフォールバックします）。
- `theme.options` - すべての組み込みテーマが読み取る、テーマ固有のオプション:
  - `theme.options.colorMode` - `"auto"`（デフォルト）、`"light"` または `"dark"`。
    初めての訪問者がヘッダーのダーク/ライトトグルで自分で選択する前に見るモードを
    制御します - `"auto"` は OS の設定に従い、`"light"`/`"dark"` は固定のデフォルトを
    設定します。訪問者がいったんトグルを切り替えると、その選択（`localStorage` に
    保存されます）は、この設定に関わらず以降の訪問で常に優先されます。

    === "YAML"
        ```yaml
        theme: { options: { colorMode: dark } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "colorMode": "dark" } } }
        ```
  - `theme.options.navCollapsible` - `false`（デフォルト）は、今日と同様にすべての
    ナビゲーションセクションを常に展開して表示します。`true` にすると、子要素を持つ
    すべてのセクションに、訪問者がクリックして折りたたみ/展開できるトグルボタンが
    付きます - そのセクションが（`index.md` のないフォルダの）単なるグループ見出しで
    あっても、自身のページにリンクしていても同様です。現在表示中のページを含む
    セクションは、`navExpandAll` の値に関わらず常に開いた状態で始まるため、
    そこへナビゲートしたときに今いるページへのリンク自体が埋もれてしまうことは
    ありません。
  - `theme.options.navExpandAll` - `navCollapsible` が `true` の場合のみ関係します。
    `true`（デフォルト）はすべてのセクションを展開した状態で開始し、`false` は
    現在のページを含むセクションを除くすべてのセクションを折りたたんだ状態で
    開始します。

    === "YAML"
        ```yaml
        theme: { options: { navCollapsible: true, navExpandAll: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "navCollapsible": true, "navExpandAll": false } } }
        ```
  - `theme.options.tocPosition` - ページ自身の「このページの内容」目次がどこに
    レンダリングされるか。`"top"`（デフォルト）は、今日と同様に記事の先頭にインラインで
    レンダリングします。`"sticky"` にすると、記事がその下でスクロールしている間も
    表示され続ける独自の右カラムに移動します - 同じ「このページの内容」リストが、
    ただ固定表示されるだけで、長いページで役立ちます。この固定カラムは広いビューポート
    でのみ収まります（3カラムレイアウトが窮屈になる幅を下回ると非表示になります）。
    その幅を下回ると、`sticky` モードは代わりに、スクロール中もビューポート上部に
    固定される折りたたみ可能な「このページの内容」バーをレンダリングします -
    タップするとリストが展開する、狭いビューポート向けによくある扱いです。
    そのため TOC はどのビューポート幅でも到達可能なままで、
    利用できるスペースに応じて形を変えるだけです。

    === "YAML"
        ```yaml
        theme: { options: { tocPosition: sticky } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "tocPosition": "sticky" } } }
        ```
  - `theme.options.pageMetaPosition` - このページを編集/Markdown をダウンロード/
    最終更新の行が、ページ自身のコンテンツに対してどこにレンダリングされるか。
    `"bottom"`（デフォルト）は、記事が終わる直前に小さなフッターの注記として
    レンダリングします。`"top"` にすると、タイトルの近くにレンダリングされます -
    このオプションが存在する前は常にそこにレンダリングされていた場所です。

    === "YAML"
        ```yaml
        theme: { options: { pageMetaPosition: top } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "pageMetaPosition": "top" } } }
        ```

## `search`

`true`（デフォルト）は静的検索インデックスをビルドし、検索ボックスを接続します。
`false` は両方とも完全にスキップします - `search-index.json` も検索 UI も追加の JS も
一切ありません。[検索](guides/search.md) を参照してください。

## `searchProvider`

`search: true` がどの検索 UI を接続するかを制御します:

- `provider` - `"local"`（デフォルト）は bx-sites 独自の静的/クライアントサイド検索
  （`search-index.json` + lunr.js、[検索](guides/search.md#localデフォルト) 参照）です。
  `"algolia"` は代わりに [Algolia DocSearch](guides/search.md#algolia) を、`"pagefind"`
  は [Pagefind](guides/search.md#pagefind) を接続します。それ以外の値は、`theme/`
  オーバーライドによって配線されるプロジェクト独自のカスタムプロバイダです -
  [検索](guides/search.md#その他の検索プロバイダー) を参照してください。
- `algolia` - `provider` が `"algolia"` の場合に必須: `appId`、`apiKey`
  （*検索専用* の公開 API キーで、管理キーではありません）、`indexName` を、
  Algolia 自身の DocSearch クライアントが期待する形式そのままで指定します。
  `insights`（デフォルトは `false`）は DocSearch のクリック/コンバージョン分析を
  有効にします。

  === "YAML"
      ```yaml title="bxsites.yaml" linenums="1"
      search: true
      searchProvider:
        provider: algolia
        algolia:
          appId: ABC123
          apiKey: a1b2c3d4e5f6...
          indexName: my-docs
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
      			"indexName": "my-docs"
      		}
      	}
      }
      ```

- `pagefind` - `provider` が `"pagefind"` の場合、両方のキーとも省略可能です:
  `bin`（デフォルトは `"pagefind"`）は CLI 実行ファイルの名前/パスで、単純な名前の
  場合は `PATH` を基準に解決されます。`options` はそのまま渡される追加の生の CLI
  フラグの配列です。`pagefind` CLI 自体は事前にインストールされ `PATH` 上にある
  必要があります - BxSites は（`lastUpdated`/`gh-deploy` の `git` と同様に）
  これを呼び出すだけで、代わりにインストールすることはありません。

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

## `mcp`

`false`(デフォルト)は完全にスキップします。`true` にすると、`build`
のたびに3つのファイルが書き出されます - 完全かつ切り詰められていない
`mcp-index.json`、ツリーごとの `mcp-nav.json`、そしてすべてのツリーを
一覧化するサイト全体の `mcp-manifest.json` です。これらは公開した
サイト向けの、[bxSites Cloud](https://bxsites.io/cloud) の公開・
読み取り専用 MCP サーバー用のものです。上記の `search`/
`searchProvider` とは完全に独立しており、どの検索プロバイダーが
設定されていても(あるいは何も設定されていなくても)生成されます。
ファイル自体の生成は無料です。bxSites Cloud が実際に稼働する MCP
サーバーとして提供するのは有料プランのみであり、無料プランには
含まれません。[MCPサーバー](guides/mcp.md) を参照してください。

## `nav`

デフォルトでは、ナビゲーションは `docs/` 自身のフォルダ/ファイル構造から推定されます
（`order`/`hidden` フロントマター付き）- 小規模なサイトには十分ですが、大規模なサイトでは
これでは足りなくなります。明示的な nav を使用すると、ファイルの実際の場所に関係なく、
ページのタイトル、グループ、順序を自由に設定できます。

空の配列（デフォルト）は「フォルダ構造から推定する」ことを意味します。非空の配列は
この推定を完全に置き換えます - 配列の順序がそのままナビゲーションの順序になり、
どこにも参照されていないページもビルドはされますが、nav からはリンクされません
（`hidden: true` と同じ扱いです）。各エントリは以下のいずれかです:

- 裸の docs/ 相対パス文字列（例: `"guides/setup.md"`）- タイトルはそのページ自身の
  フロントマター/ファイル名から取得され、フォルダ推定の場合と同じです
- `{ "title", "path", "icon", "children" }` オブジェクト - `path`、`icon`、`children`
  はすべて省略可能です。`path` のない `title` のみのエントリは、リンクのないグループ
  見出し（今日でいう `index.md` のないフォルダのようなもの）になります。明示的な
  `title`/`icon` は、常にリンク先ページ自身のタイトル/アイコンより優先されて nav に
  表示されます（ページ自身の実際の `<h1>`/`<title>` は変更されません - nav のラベル/
  アイコンだけが変わります）- `icon` に指定できる値については
  [アイコン](guides/icons.md) を参照してください

`path` を持たず `children` を持つ `title` のみのエントリは、まさにメニューコンテナ/
セクションラベルです - その子要素をまとめるだけの、クリックできない見出しです:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - index.md
      - title: メインコンポーネント
        children:
          - title: クイックスタート
            path: guides/setup.md
          - guides/deployment.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		"index.md",
    		{
    			"title": "メインコンポーネント",
    			"children": [
    				{ "title": "クイックスタート", "path": "guides/setup.md" },
    				"guides/deployment.md"
    			]
    		}
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"
    ```

同じグループエントリに `path` を与えると、単なるラベルではなく（自身のランディング
ページと子要素を持つ）通常のリンク付きセクションになります - どちらの形でも
`theme.options.navCollapsible` の下では同じようにネストされます（上記参照）。

`bxsites.yaml` が煩雑になるほど nav が大きい場合は、代わりに独自の `docs/nav.json`
ファイルに移動できます - 配列の形は同じで、ファイル全体のトップレベルの内容として
そのまま置くだけです:

```json title="docs/nav.json" linenums="1"
[
	"index.md",
	{ "title": "ガイド", "children": [ "guides/setup.md" ] }
]
```

`bxsites.yaml` 自身の `nav` が非空の場合、常に `docs/nav.json` より優先されます。
どちらもメインツリーのみに適用されます - `docs/versions/<name>/` ツリーは、
メインツリーに明示的な nav があっても、常に自身のフォルダ構造から nav を推定します。

## `redirects`

`[]`（デフォルト）- サイト全体の `from`/`to` 古い URL リダイレクトで、
メインツリーにのみ適用されます:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [markdown]
    enableFootnotes = true
    enableDefinitionLists = true
    anchorLinks = false
    enableYouTubeTransformer = true
    ```

- `redirects[].from` - 静的なリダイレクトスタブが書き込まれる古いプリティ URL
  セグメント（先頭/末尾のスラッシュなし、拡張子なし）
- `redirects[].to` - ルート相対パス（`baseURL` を基準に解決されます）または
  完全な `https://` URL

ページ自身のフロントマター `redirect_from` は、ページ単位・ツリー単位の代替手段です
（バージョン/ロケールツリー内でも機能します）- `page:rename` がどのように自動的に
これを刻印するかを含む全体像については [リダイレクト](guides/redirects.md) を
参照してください。

## `markdown`

各ページがレンダリングされる前に、[bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
独自のモジュール設定としてそのまま転送されます。BxSites はこれらのキーを再定義したり
検証したりしません。ここに書いたものはそのまま bx-markdown 自身のオプションセットに
なるため、このリストは bx-markdown 自体の進化とともにずれていく可能性があります。
テーブル、`~~取り消し線~~`、`- [ ]` タスクリストのチェックボックス、ページ内目次は
常に有効で、トグルはありません。唯一の例外は `enableAdmonition` です - bx-markdown
自体のデフォルトは `false` ですが、BxSites のデフォルトは `true` です
（[Markdown 拡張ガイド](guides/markdown.md) を参照）。

| キー | デフォルト | 効果 |
|---|---|---|
| `enableAdmonition` | `true` *（BxSites のデフォルト。bx-markdown 自体のデフォルトは `false`）* | `!!!`/`???`/`???+` コールアウトブロック - [Markdown 拡張ガイド](guides/markdown.md#admonitions) を参照 |
| `enableFootnotes` | `false` | `[^label]` 脚注参照 - [Markdown 拡張ガイド](guides/markdown.md#footnotes) を参照 |
| `enableDefinitionLists` | `false` | `Term\n:   Definition` リスト - [Markdown 拡張ガイド](guides/markdown.md#definition-lists) を参照 |
| `autoLinkUrls` | `true` | 裸の URL とメールアドレスを自動リンク |
| `anchorLinks` | `true` | すべての見出しにクリック可能なアンカーリンクを追加 |
| `anchorSetId` | `true` | すべての見出しに `id` 属性を刻印 |
| `achorSetName` *(原文ママ)* | `true` | すべての見出しに `name` 属性を刻印 |
| `anchorWrapText` | `false` | 単なるマーカーだけでなく、見出しテキスト全体をアンカーリンクで囲む |
| `anchorClass` | `"anchor"` | アンカー `<a>` の CSS クラス |
| `anchorPrefix` / `anchorSuffix` | `""` | 見出しテキストの直前/直後に挿入される生の HTML |
| `enableYouTubeTransformer` | `false` | 裸の YouTube リンクをプレイヤーとして自動埋め込み |
| `codeStyleHTMLOpen` / `codeStyleHTMLClose` | `"<code>"` / `"</code>"` | インラインコードスパンを囲むラッパー HTML |
| `fencedCodeLanguageClassPrefix` | `"language-"` | bx-sites のクライアントサイドシンタックスハイライター（および下記の Mermaid）が使用するクラスプレフィックス。例: ` ```js ` → `class="language-js"` |
| `tableOptions.columnSpans` | `true` | `colspan` 形式の結合されたテーブルセルを尊重 |
| `tableOptions.appendMissingColumns` | `true` | 短い行をヘッダーの列数まで埋める |
| `tableOptions.discardExtraColumns` | `true` | 長すぎる行の余分なセルを破棄 |
| `tableOptions.className` | `"table"` | レンダリングされるすべての `<table>` の CSS クラス |
| `tableOptions.headerSeparationColumnMatch` | `true` | `---` の区切り行がヘッダーの列数と一致することを要求 |

レンダリングされる各テーブルには、専用の設定キーなしでレスポンシブスクロールと固定ヘッダーのラッパーが自動的に付与されます - 詳細は[テーブル](guides/tables.md#レスポンシブなスクロールと固定ヘッダー)を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    markdown:
      enableFootnotes: true
      enableDefinitionLists: true
      anchorLinks: false
      enableYouTubeTransformer: true
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"markdown": {
    		"enableFootnotes": true,
    		"enableDefinitionLists": true,
    		"anchorLinks": false,
    		"enableYouTubeTransformer": true
    	}
    }
    ```

## `repo`

ヘッダー（すべての組み込みテーマ）にリポジトリアイコンリンクを追加し、両方のキーが
設定されている場合は各ページに「このページを編集」リンクも追加します。

- `repo.url` - リポジトリの URL（例: `"https://github.com/acme/docs"`）。単独でも
  ヘッダーアイコンリンクをレンダリングします。完全に省略するには空白のままにします。
- `repo.editUri` - リポジトリ URL とページ自身のソースパスの間のパスセグメント
  （例: `"edit/main/docs/"`、GitHub 自身の「edit」URL 規約）。`repo.url` とページの
  `docs/` 相対ソースパスを組み合わせて編集リンクを構築します - 例えば上記の例では、
  `docs/guides/setup.md` は `https://github.com/acme/docs/edit/main/docs/guides/setup.md`
  になります。これにも `repo.url` が必要です。ヘッダーアイコンは表示したまま編集
  リンクだけを省略するには空白のままにします。

=== "YAML"
    ```yaml title="bxsites.yaml"
    repo: { url: "https://github.com/acme/docs", editUri: "edit/main/docs/" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "repo": { "url": "https://github.com/acme/docs", "editUri": "edit/main/docs/" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [repo]
    url = "https://github.com/acme/docs"
    editUri = "edit/main/docs/"
    ```

## `social`

フッターにレンダリングされるソーシャル/外部リンクの配列（[`footer`](#footer) を参照 -
それも有効にしない限り効果はありません）。各エントリには `url` が必要です。`icon` は
組み込みの小さなアイコンセット（`github`、`twitter`/`x`、`youtube`、`linkedin`、
`facebook`、`bluesky`、`threads`、`slack`、`patreon`、`rss`、`email`、それ以外は
汎用のリンクグリフにフォールバック）から選択し、`label` はリンクのアクセシブルな
名前/ツールチップを設定します（デフォルトは `icon`、それもなければ `"Link"`）。

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    social:
      - { url: "https://twitter.com/acme", icon: twitter, label: Twitter }
      - { url: "https://acme.com/rss.xml", icon: rss, label: RSS }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"social": [
    		{ "url": "https://twitter.com/acme", "icon": "twitter", "label": "Twitter" },
    		{ "url": "https://acme.com/rss.xml", "icon": "rss", "label": "RSS" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[social]]
    url = "https://twitter.com/acme"
    icon = "twitter"
    label = "Twitter"

    [[social]]
    url = "https://acme.com/rss.xml"
    icon = "rss"
    label = "RSS"
    ```

## `footer`

`false`（デフォルト）- フッターは一切ありません。`true` にすると各ページにフッターが
追加されます: 著作権行（`© <year> <site name>`）、`social` リンク（あれば）、
「Built with BxSites」のクレジット。

=== "YAML"
    ```yaml title="bxsites.yaml"
    footer: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "footer": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    footer = true
    ```

## `lastUpdated`

`false`（デフォルト）- 最終更新日は表示されません。`true` にすると、編集リンクの
横（`repo.editUri` が設定されていない場合は単独で）に「Last updated」行が追加され、
ビルド時に各ページ自身の Markdown ファイルに対する `git log` から取得されます。
git が履歴を持たないページ（まだコミットのない新しい `git init`、`.git` の全くない
ダウンロード済み zip から実行されたビルド、ビルドマシンに git がインストールされて
いない場合など）では、ビルドを壊すことなく黙って省略されます。

=== "YAML"
    ```yaml title="bxsites.yaml"
    lastUpdated: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "lastUpdated": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    lastUpdated = true
    ```

## `analytics`

ページビュー分析を接続します。現在は Google Analytics（`gtag.js`）のみをサポートしています:

- `analytics.provider` - `"google"` にすると有効化されます。空白（デフォルト）の場合、
  分析スクリプトは一切送信されません。
- `analytics.id` - Google Analytics の測定 ID（例: `"G-ABC123"`）。`provider` が
  `"google"` の場合は必須です。

=== "YAML"
    ```yaml title="bxsites.yaml"
    analytics: { provider: google, id: "G-ABC123" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "analytics": { "provider": "google", "id": "G-ABC123" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [analytics]
    provider = "google"
    id = "G-ABC123"
    ```

## `ogImage`

デフォルトのソーシャルカード画像へのパス/URL。独自の `description` を上書きしない
すべてのページで `og:image`（`summary_large_image` の `twitter:card` と対になります）
としてレンダリングされます - `theme.logo` と同じ方法で解決されます（相対パスには
`baseURL` がプレフィックスされ、絶対 URL はそのまま使用されます）。空白（デフォルト）で
`generateOgImages` もオフの場合、`og:image`/`twitter:card` タグは一切レンダリングされません。

=== "YAML"
    ```yaml title="bxsites.yaml"
    ogImage: assets/social-card.png
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "ogImage": "assets/social-card.png" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    ogImage = "assets/social-card.png"
    ```

そのページのフロントマター `ogImage`（[はじめに](getting-started.md#ページの追加) を
参照）は、常にこのサイト全体のデフォルトよりそのページで優先されます。

### `generateOgImages`

`false`（デフォルト）- ページごとのカードはありません。`true` にすると、独自の
フロントマター `ogImage` をまだ持たないすべてのページに対して、実際の 1200x630 の
PNG ソーシャルカードがレンダリングされます - ブランドグラデーション上にページの
タイトルが書かれ、`site/assets/og/<page>.png` に出力されます - すべてのページが
1つの汎用的なサイト全体の画像を共有する代わりです。純粋な `java.awt`/`javax.imageio`
（BoxLang が動作するあらゆる JVM の一部）で実装されているため、ビルド時にヘッドレス
ブラウザや外部サービス、ネットワークアクセスは一切必要ありません。

=== "YAML"
    ```yaml title="bxsites.yaml"
    generateOgImages: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "generateOgImages": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    generateOgImages = true
    ```

## `extraCss` / `extraJs`

すべてのページに含める、追加のスタイルシート/スクリプト URL の配列で、テーマ自身の
アセットの後に追加されます - 各エントリは `theme.logo` と同じ方法で解決されます
（相対パスには `baseURL` がプレフィックスされ、絶対 URL はそのまま使用されます）。
`extraJs` のエントリは `defer` 付きで読み込まれます。

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/custom.css ]
    extraJs: [ assets/custom.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/custom.css"],
    	"extraJs": ["assets/custom.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/custom.css" ]
    extraJs = [ "assets/custom.js" ]
    ```

`assets.bundle` が有効な場合（デフォルト）、上記のようなローカルの `extraCss`/`extraJs`
リストはエントリごとに1つの `<link>`/`<script>` タグを出す代わりに、それぞれ1つの
フィンガープリント付きファイルにバンドルされます - 下記の [`assets`](#assets) を
参照してください。

## `assets`

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      fingerprint: true
      bundle: true
      images:
        enabled: true
        widths: [ 400, 800, 1200, 1600 ]
        formats: [ original, webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": {
    			"enabled": true,
    			"widths": [400, 800, 1200, 1600],
    			"formats": ["original", "webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [assets]
    fingerprint = true
    bundle = true

    [assets.images]
    enabled = true
    widths = [ 400, 800, 1200, 1600 ]
    formats = [ "original", "webp" ]
    ```

アセットパイプライン - [bx-image](https://github.com/ortus-boxlang/bx-image)
（bx-markdown/bx-esapi と並んでインストールされる必須の依存関係）による画像リサイズ/WebP
変換と、CSS/JS のバンドルです。ここにあるものはすべて、妥当な設定でデフォルトで
有効になっています - 新規の `bxSites new` プロジェクトはここに一切触れる必要が
ありません。全体像（AVIF、アニメーション GIF、SVG が意図的にカバーされていない点を
含む）については [レスポンシブ画像](guides/images.md) を参照してください。

- `assets.fingerprint` - `true`（デフォルト）。生成されるすべての画像バリアントと
  CSS/JS バンドルにコンテンツハッシュの名前を付けます（例: `screenshot-800w.a3f9c2e1.webp`、
  `bundle.a3f9c2e1.css`）。これにより、安全で長期のキャッシュヘッダー付きで配信できます -
  プロジェクトのビルドは、ファイル自身の内容が実際に変わった場合にのみファイル名を
  変更します。`docs/assets/` 配下のプロジェクト自身のオリジナルファイルの名前は
  変更しません - パイプラインが生成した出力だけがフィンガープリントされるため、
  素のファイル名でアセットを参照する他のもの（`::: file` ダウンロードカード、
  生の Markdown リンク）は変更なく機能し続けます。
- `assets.bundle` - `true`（デフォルト）。`extraCss`/`extraJs` をそれぞれ1つの
  フィンガープリント付きファイルに連結します - 純粋な BoxLang/JVM で、
  Node/esbuild のツールチェーンは不要です。リスト内のいずれかのエントリが
  外部 URL（CDN リンク）であるか、存在しないファイルを指している場合は、
  即座に今日と全く同じエントリごとの `<link>`/`<script>` の挙動にフォールバックします -
  [レスポンシブ画像](guides/images.md#css-js-bundling) を参照してください。
- `assets.images.enabled` - `true`（デフォルト）。対象となるすべての `docs/assets/**`
  画像（`.png`/`.jpg`/`.jpeg`）が bx-image によってリサイズ/WebP バリアントを
  生成され、一致する `<img>` はすべて `srcset` 付きの `<picture>` に書き換えられます。
  この機能が存在する前とまったく同じ、プレーンで未処理の画像コピーにフォールバック
  するには `false` を設定します。
- `assets.images.widths` - 生成するブレークポイント（ピクセル単位）。ある画像自身の
  幅以上の幅は、その画像については自動的にスキップされます - アップスケールされる
  ことは決してありません。
- `assets.images.formats` - `"original"` はソース形式を `<img>` のフォールバックとして
  維持します。`"webp"` は同じサイズの `<source type="image/webp">` バリアントを
  追加します。どちらもデフォルトで有効です。

## `mermaid`

`false`（デフォルト）- [Mermaid](https://mermaid.js.org/) ダイアグラムのサポートは
一切出荷されません。`true` にするとクライアントサイドで `mermaid.js` が読み込まれ、
` ```mermaid ` フェンス付きコードブロックがすべてダイアグラムとしてレンダリングされます。
構文については [Markdown 拡張](guides/markdown.md#diagrams) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    mermaid: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mermaid": true }
    ```

## `math`

`false`（デフォルト）- [KaTeX](https://katex.org/) は一切出荷されません。`true` に
するとクライアントサイドで読み込まれ、ページの Markdown に直接書かれた
`$...$`/`$$...$$` が組版されます。構文については
[Markdown 拡張](guides/markdown.md#math) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    math: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "math": true }
    ```

admonition（note/warning/tip 形式のコールアウトボックス）、コンテンツタブ、
フェンス付きコードの `hl_lines`/`linenums`/`title` アノテーションは、設定不要で
すべてのページの Markdown で常に利用できます -
[Markdown 拡張](guides/markdown.md#admonitions) を参照してください。

## `openapi`

`false`（デフォルト）- [Swagger UI](https://swagger.io/tools/swagger-ui/) は一切
出荷されません。`true` にするとクライアントサイドで読み込まれ、`::: openapi src="..."`
コンテンツブロックを、参照先の OpenAPI/Swagger 仕様（JSON または YAML）のための
インタラクティブなウィジェットとしてレンダリングします。構文については
[OpenAPI / Swagger](guides/openapi.md) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    openapi: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "openapi": true }
    ```

## `plugins`

`[]`（デフォルト）- プラグインとして有効化する BoxLang モジュール名の配列です。
プラグインモジュールをインストールする（`box install`）だけでは、それ単独で
有効化されることはありません - ここにも名前を書く必要があります。プラグインの
書き方については [プラグイン](guides/plugins.md) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

## `i18n`

[`docs/i18n/<code>/`](guides/i18n.md) のロケールフォルダ規約のためのメタデータです -
ロケールはそのフォルダが存在すれば自動的にビルドされます。`i18n` は、言語スイッチャーの
表示ラベル/方向を提供するだけです。

- `i18n.defaultLocale` - プロジェクト自身の通常の `docs/` ツリーの
  `{ "code", "label", "flag", "strings" }`。デフォルトは
  `{ "code": "en", "label": "English" }`。デフォルトロケールが英語でない場合にのみ
  設定が必要です。
- `i18n.locales` - `[]`（デフォルト）- 他のすべてのロケールの
  `{ "code", "label", "dir", "flag", "strings" }` の配列。`code` は
  `docs/i18n/<code>/` フォルダ名とビルド後の URL プレフィックスを兼ねます -
  英数字とハイフンのみ（`es`、`pt-BR`、`zh-Hans`）。`dir` は `"ltr"`（デフォルト）
  または `"rtl"`。`flag` は言語スイッチャーの旗アイコンを上書きする任意の絵文字です -
  一般的なコードのほとんどは、指定しなくても妥当な旗に自動的に解決されます。
  `strings` はそのロケール自身のテーマクローム UI テキスト（検索プレースホルダー、
  「このページの内容」、404 ページなど）を上書きします - キーの全リストは
  [国際化](guides/i18n.md#theme-chrome-ui-strings) を参照してください。
  `de`/`es`/`it`/`ja` にはすでに組み込みの翻訳が付属しているため、`strings` は
  特定のキーを上書きするか、別のロケールを追加する場合にのみ必要です。

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    i18n:
      defaultLocale: { code: en, label: English }
      locales:
        - { code: es, label: Español }
        - { code: ar, label: العربية, dir: rtl }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": [
    			{ "code": "es", "label": "Español" },
    			{ "code": "ar", "label": "العربية", "dir": "rtl" }
    		]
    	}
    }
    ```

未翻訳ページのフォールバック、言語スイッチャー、まだ翻訳されていない部分を含む
全体像については [国際化](guides/i18n.md) を参照してください。

## `blog`

[ブログ](guides/blog.md) 機能自体のオプションです - ブログ自体は規約ベースの機能
（`docs/blog/posts/`）であり、有効化するために必要なキーはここにはありません。

- `blog.postsPerPage` - `10`（デフォルト）- `/blog/`、各カテゴリページ、各
  `/blog/archive/<year>/` ページで、`.../page/2/` に移る前に1ページに表示する
  投稿数。
- `blog.feed` - `true`（デフォルト）- `/blog/feed.xml`（RSS 2.0）を書き出すかどうか。
  `sitemap.xml` と同じ要件で、絶対 `baseURL` の場合にのみ意味を持ちます。
- `blog.feedLimit` - `25`（デフォルト）- `/blog/feed.xml` を直近のこの件数の投稿に
  制限します。`0` は無制限（すべての投稿を全文）を意味します。ほとんどのフィード
  リーダーは新しいものにしか関心がないため、数百件の投稿があるブログで無制限の
  フィードを配信すると、ポーリングのたびに帯域を無駄にするだけです -
  [ブログ: フィード](guides/blog.md#feed) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

投稿/著者のフロントマター、カテゴリ、注目画像、SEO/ソーシャルメタデータについては
[ブログ](guides/blog.md) を参照してください。

## `variables`

`{}`（デフォルト）- 再利用可能な値のオブジェクトで、形は自由です。どの Markdown
ページからも `{{ dotted.path }}` として参照できます。
[変数とマジック関数](guides/variables-and-functions.md) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product: { name: "BoxLang", supportEmail: "support@example.com" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": { "name": "BoxLang", "supportEmail": "support@example.com" }
    	}
    }
    ```

```markdown title="docs/index.md"
Welcome to {{ company }}! We build {{ product.name }}.
```

`docs/functions.bxs` ファイル（`docs/nav.json`/`docs/blog/authors.yml` と同様、
専用の設定キーはなく規約ベースです）は、`variables` と並んで BoxLang の
「マジック関数」を追加します - `{{ $name(...) }}` として同じ方法で呼び出せます。
[変数とマジック関数](guides/variables-and-functions.md#マジック関数) を参照してください。

`docs/data/*.yaml`/`.yml`/`.json` フォルダ（これも専用の設定キーはありません）は、
`{{ data.<file>.<key> }}` として参照できる、構造化された、ネスト/配列形式の
データを追加します - `variables` 自身のフラットな形にうまく収まらないもの
（チームの名簿、料金表など）向けです。[データファイル](guides/data-files.md)
を参照してください。

## バージョニング

バージョン管理されたドキュメントは設定より規約を重視します - `bxsites.yaml` に
専用キーはありません。`docs/versions/<name>/` フォルダを追加すると、それは独自の
ドキュメントツリーとして自動的にビルドされ、複数のバージョンが存在するようになると
すべてのテーマが自動的にバージョンスイッチャーをレンダリングします。`version:new` で
新しいバージョンを切り出す方法、バージョンがどのように並び替えられビルドされるか、
そして対象外の部分（ツリーごとの検索スコープ、非推奨/EOL フラグがないことなど）を
含む全体像については [バージョニング](guides/versioning.md) を参照してください。


## `docbox`

DocBox から BoxLang/CFML の API リファレンスを生成する
[`bxSites docbox`](cli-reference.md#docbox) の設定です。
[DocBox APIリファレンス](guides/docbox.md) を参照してください。すべてのキーは
省略可能で、`docbox` ブロックがない場合は慣例的なソースフォルダー
（`models`、`handlers`、`bifs`、`components`、`interceptors`）が対象に
なります。

- `projectTitle` - 概要ページのタイトル。既定はサイトの `name` に
  `" API"` を付けたもの。
- `mappings` - マッピング名とソースディレクトリ。この名前が生成ページ上の
  パッケージ名になります。
- `excludes` - DocBox が除外するパスの正規表現。
- `pagePathPrefix` - ページの出力先。既定は `api/docbox`。
- `tags` - 生成される各ページの frontmatter タグ。既定は
  `[ "api", "docbox" ]`。

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "My API"
      mappings:
        models: models
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "My API",
    		"mappings": { "models": "models" },
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [docbox]
    projectTitle = "My API"
    pagePathPrefix = "api/docbox"
    tags = [ "api", "docbox" ]

    [docbox.mappings]
    models = "models"
    ```

## `coldbox`

ColdBox アプリケーションを規約からドキュメント化する
[`bxSites coldbox`](cli-reference.md#coldbox) の設定です。
[ColdBoxアプリケーション](guides/coldbox.md) を参照してください。すべての
キーは省略可能です。

- `appRoot` - ColdBox アプリケーションの場所（プロジェクトルートからの
  相対）。既定はプロジェクトルート自身。
- `pagePathPrefix` - ページの出力先。既定は `api/coldbox`。
- `tags` - 生成される各ページの frontmatter タグ。既定は
  `[ "api", "coldbox" ]`。
- `include` - 生成するページ群: `routes`、`handlers`、`models`、
  `modules`、`interceptors`、`scheduler`。既定ではすべて。

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [coldbox]
    appRoot = "."
    pagePathPrefix = "api/coldbox"
    tags = [ "api", "coldbox" ]
    include = [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    ```
