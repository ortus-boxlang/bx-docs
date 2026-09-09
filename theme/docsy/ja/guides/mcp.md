---
title: MCPサーバー
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: mcp を有効にすると、build のたびに完全で切り詰められていない site/mcp-index.json(および site/mcp-manifest.json とツリーごとの mcp-nav.json)を書き出し、bxSites Cloud が公開・読み取り専用の MCP サーバーを通じて AI エージェントにサイトを公開できるようになります - search/searchProvider とは独立しています。
tags: [ガイド, mcp, ai]
---

# MCPサーバー

`bxsites.yaml` で `mcp: true` を有効にすると、`build` のたびにレンダリング
されたページと並んで3つのファイルが書き出されます - サイトのコンテンツ、
ナビゲーション、ツリー構造の完全かつ機械可読なコピーであり、
[bxSites Cloud](https://bxsites.io/cloud) が公開したサイト向けに公開・
読み取り専用の [MCP](https://modelcontextprotocol.io/) サーバーを提供する
ために読み込みます。これは
[GitBook の「公開ドキュメント向け MCP サーバー」](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs)
と同じ発想です。公開後は、AI エージェントやアシスタント(Claude、
ChatGPT、その他 MCP 対応のクライアント)が、レンダリングされた HTML を
スクレイピングする代わりに、サイトのコンテンツを直接検索・閲覧・取得
できるようになります。

- `site/mcp-index.json` - ツリーごとに、ページ単位の全文エントリを1つ
- `site/mcp-nav.json` - ツリーごとに、そのツリー自身のナビゲーション構造
- `site/mcp-manifest.json` - すべてのツリー(メインサイト、バージョン、
  ロケール)と、それぞれの上記ファイルの所在を示す、サイト全体の
  インデックス1つ

bxSites 自体は、このページで説明するファイルを生成するだけです -
それをネットワーク越しに実際に MCP サーバーとして提供するのは
bxSites Cloud の仕事であり、このモジュール自体が行うものではありません。

!!! note "bxSites Cloud の有料プランが必要です"
    `mcp-index.json` の生成自体は無料で、Cloud アカウントなしで `bxSites
    build` だけで動作します。公開したサイト向けに実際に稼働する MCP
    サーバーとして提供することは、bxSites Cloud の有料プランのみで
    利用できる機能であり、無料プランには含まれていません。最新の
    プラン詳細は [bxsites.io/cloud](https://bxsites.io/cloud) を
    参照してください。

## 有効にする

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    mcp = true
    ```

`false`(デフォルト)はこのステップ全体をスキップします - `mcp-index.json`、
`mcp-nav.json`、`mcp-manifest.json` のいずれも書き出されず、`build` は
フラグの確認以上の追加コストを負いません。

## `search` からは独立

`mcp` は [`search`/`searchProvider`](search.md) とは無関係です - 両者は
互いに独立したスイッチです。

- `mcp-index.json` は `search` が `true` か `false` かに関わらず、また
  `searchProvider.provider`(`"local"`、`"algolia"`、`"pagefind"`、
  または独自のもの)に関わらず生成されます。
- `mcp` を有効にしても、サイト自体の訪問者向け検索ボックスには一切影響
  せず、`search` を無効にしても `mcp-index.json` が無効になることは
  決してありません。

このため、`mcp-index.json` は `search-index.json` を単純に再利用する
ことはできません。`search-index.json` は `"local"` 検索プロバイダー
の場合にのみビルドされ(Algolia や Pagefind を使うサイトはそれぞれ
独自のインデックスを別の場所に持つため、`search-index.json` はまったく
生成されません)、また `body` フィールドは意図的に切り詰められています
- `search-index.json` はページ内検索ボックスのためにすべての訪問者の
ブラウザに送られるため、サイズを小さく保つことが重要だからです。
`mcp-index.json` にはこれらの制約はいずれも当てはまりません。これは
bxSites Cloud によってサーバー側で取得されるものであり、訪問者には
送られませんし、本文が切り詰められていると AI エージェントが不完全
または誤った回答をしてしまう恐れがあります。

## `mcp-index.json` のフォーマット

非表示でない各ページにつき1エントリ(`search-index.json` やナビゲー
ションが使う「非表示ページは除外する」という同じ規約に従います)。
通常のドキュメントページと[ブログ](blog.md)記事の両方が対象です。

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ...",
    "type": "page",
    "categories": [],
    "updatedAt": "2026-08-18T10:15:00.000Z"
  },
  {
    "title": "Announcing bxSites 2.0",
    "url": "blog/announcing-bxsites-2/index.html",
    "tags": ["release"],
    "headings": ["Announcing bxSites 2.0"],
    "body": "Announcing bxSites 2.0 Today we're shipping...",
    "type": "post",
    "categories": ["Releases"],
    "updatedAt": "2026-08-15"
  }
]
```

| フィールド | 説明 |
|---|---|
| `title` | ページのタイトル(フロントマターの `title`、`search-index.json` と同じ) |
| `url` | サイトルートからの相対 URL パス |
| `tags` | ページのフロントマター `tags` 配列 |
| `headings` | ページ内のすべての `h1`-`h6` のプレーンテキスト(文書内の出現順) |
| `body` | HTML タグを除去した、ページの**完全な**プレーンテキストコンテンツ - 決して切り詰められません |
| `type` | 通常のドキュメントページには `"page"`、[ブログ](blog.md)記事には `"post"` |
| `categories` | 記事自身のフロントマター `categories` 配列。ドキュメントページには常に `[]`(ドキュメントページにはカテゴリーがないため) |
| `updatedAt` | 記事自身のフロントマター `date`。ドキュメントページの場合は、設定されていればそのページ自身のフロントマター `date`、設定されていなければソースファイルの最終更新日時を ISO-8601 形式の日時として使用。どちらも取得できない場合は `""` |

`type`/`categories`/`updatedAt` を除けば、これは `search-index.json` と
同じエントリ形式です。唯一の違いは `body` で、`search-index.json` では
400 文字で末尾に省略記号を付けて切り詰められますが、ここではページ
全体になります。

## `mcp-manifest.json` のフォーマット

サイトのビルドごとに一度だけ(ツリーごとではなく)書き出される
`site/mcp-manifest.json` は、独自の `mcp-index.json`/`mcp-nav.json` を
持つすべてのツリーを一覧化します。これにより、bxSites Cloud の MCP
サーバーは bx-sites 自身のディレクトリ規約を推測することなく、
バージョンとロケールを意識したツールを提供できます。

```json title="site/mcp-manifest.json"
[
  { "path": "", "label": "1.0.x", "version": "1.0.x", "locale": "en", "default": true },
  { "path": "next", "label": "Next", "version": null, "locale": "en", "default": false },
  { "path": "versions/0.9", "label": "0.9", "version": "0.9", "locale": "en", "default": false },
  { "path": "es", "label": "Español", "version": "1.0.x", "locale": "es", "default": false }
]
```

| フィールド | 説明 |
|---|---|
| `path` | このツリー自身のルート相対パス - サイトルートのメインツリーは `""`、それ以外は `"next"`/`"versions/<name>"`/`"<localeCode>"`/`"versions/<name>/<localeCode>"`。`/mcp-index.json`(または `/mcp-nav.json`)と結合するとそのツリー自身のファイルが得られます。例: `"versions/0.9/mcp-index.json"` - ルート自身の場合は単に `"mcp-index.json"`(`path` はそこでは `""`) |
| `label` | このツリーの人間可読なラベル - バージョン切替の自身のラベル(`versions.default` の名前、通常のバージョン自身の名前、または `"Latest"`/`"Next"`)に、ロケールサブツリーの場合はそのロケール自身のラベルを組み合わせたもの |
| `version` | このツリーがレンダリングする `docs/versions/<name>/` の名前。名前付きバージョンでない場合(バージョン管理されていないメインツリー、または `/next/`)は `null` |
| `locale` | このツリー自身のロケールコード - ロケール接尾辞のないツリーでは `i18n.defaultLocale.code`、それ以外はそのロケール自身のコード |
| `default` | サイトルートでレンダリングされる1つのツリーにのみ `true` - バージョンやロケールを指定しなかった訪問者/AI エージェントが行き着くツリー |

## `mcp-nav.json` のフォーマット

各ツリー自身の `mcp-index.json`(サイトルート、`/next/`、各
`/versions/<name>/` ツリー、各ロケールサブツリー)と並んで書き出される
`mcp-nav.json` は、そのツリー自身のナビゲーションです - テーマ自身が
サイドバーをレンダリングするために使うのと全く同じ、ネストされた
`{ title, url, order, icon, children }` 構造です。これにより、bxSites
Cloud の MCP サーバーは独自の第二のナビゲーション形式を発明することなく
`get_nav`/目次ツールを提供できます。

```json title="site/mcp-nav.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "order": 1,
    "icon": "phosphor-duotone:rocket-launch",
    "children": []
  },
  {
    "title": "Guides",
    "url": "",
    "order": 2,
    "icon": "",
    "children": [
      { "title": "Search", "url": "guides/search/index.html", "order": 1, "icon": "", "children": [] }
    ]
  }
]
```

`"Guides"` のように自身の `index.md` を持たないフォルダーグループ
ノードは `url` が空です - これはページではなく、単に `children` の
見出しです。

## マルチバージョン・多言語サイト

`search-index.json` と同様に、`mcp-index.json`/`mcp-nav.json` もレンダ
リングされるツリーごとに一度書き出されます - メインサイト、
(`versions.default` が設定されている場合の)`/next/`、各
`/versions/<name>/` ツリー、各ロケールサブツリーです。したがって、
公開される各ツリーはそのツリー自身のページとナビゲーションのみを
カバーする、独自のファイルのペアを持ちます。一方 `mcp-manifest.json`
は、ビルドごとに一度だけ、サイトルートにのみ書き出され、これら
すべてのツリーを一覧化します。
