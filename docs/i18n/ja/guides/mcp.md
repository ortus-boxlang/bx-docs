---
title: MCPサーバー
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: mcp を有効にすると、build のたびに完全で切り詰められていない site/mcp-index.json を書き出し、bxSites Cloud が公開・読み取り専用の MCP サーバーを通じて AI エージェントにサイトを公開できるようになります - search/searchProvider とは独立しています。
tags: [ガイド, mcp, ai]
---

# MCPサーバー

`bxsites.yaml` で `mcp: true` を有効にすると、`build` のたびにレンダリング
されたページと並んで `site/mcp-index.json` が書き出されます - これは、
[bxSites Cloud](https://bxsites.io/cloud) が公開したサイト向けに公開・
読み取り専用の [MCP](https://modelcontextprotocol.io/) サーバーを提供する
ために読み込む、サイトのコンテンツの完全かつ機械可読なコピーです。これは
[GitBook の「公開ドキュメント向け MCP サーバー」](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs)
と同じ発想です。公開後は、AI エージェントやアシスタント(Claude、
ChatGPT、その他 MCP 対応のクライアント)が、レンダリングされた HTML を
スクレイピングする代わりに、サイトのコンテンツを直接検索・取得できるよう
になります。

bxSites 自体は、このページで説明する `mcp-index.json` ファイルを生成する
だけです - それをネットワーク越しに実際に MCP サーバーとして提供するのは
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

`false`(デフォルト)はこのステップ全体をスキップします - `mcp-index.json`
は書き出されず、`build` はフラグの確認以上の追加コストを負いません。

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

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ..."
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

`search-index.json` 自身のエントリ形式との唯一の違いは `body` です。
`search-index.json` では 400 文字で末尾に省略記号を付けて切り詰められ
ますが、ここではページ全体になります。

## マルチバージョン・多言語サイト

`search-index.json` と同様に、`mcp-index.json` もレンダリングされる
ツリーごとに一度書き出されます - メインサイト、(`versions.default` が
設定されている場合の)`/next/`、各 `/versions/<name>/` ツリー、各言語
サブツリーです。したがって、公開される各ツリーはそのツリー自身のページ
のみをカバーする独自の `mcp-index.json` を持ちます。
