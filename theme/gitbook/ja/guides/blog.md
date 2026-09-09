---
title: ブログ
order: 10
icon: phosphor-duotone:newspaper
tags: [ガイド, ブログ]
---

# ブログ

ブログもまた、[バージョン](../configuration.md#バージョニング)/[i18n](i18n.md) や
[タグ索引](../getting-started.md#ページの追加) と同じ形の、規約ベースの機能です -
`docs/blog/posts/` の下に投稿を置くだけで、BxSites は `/blog/`
（ページネーション付き）、カテゴリごとのページ、暦年ごとのアーカイブページ、
著者ごとのページ、カテゴリごとの RSS フィードとブログ全体用のフィード、
そして `/blog/stats/` ページを、設定ゼロで構築します。`docs/blog/posts/`
フォルダがないプロジェクトには単にブログがありません - それ以外は何も
変わりません。

## 投稿を書く

`docs/blog/posts/` の下にある、どんな深さの `.md` ファイルもすべて投稿です -
サブフォルダは完全に任意で、あくまで自分の編集の利便性のためのものです。
投稿が数件のうちはフラットなフォルダで問題なく、数百件になってきたら
`docs/blog/posts/2026/`（あるいは `docs/blog/posts/2026/03/`、好きな方式で）
の下にファイルを収めることで、何もリネームしたりフロントマターの日付
プレフィックス規約に触れたりすることなく、エディタのファイルツリーを
見やすく保てます。これはビルド済みサイトには一切影響しません - 投稿の
並び順、年次アーカイブ、URL（`blog/<slug>/`）はすべてフロントマターのみから
導出され、ファイルが実際にどこに置かれているかからは決して導出されません。
そのため、投稿のフォルダと実際の `date` は常に食い違っていて構いません:

```text title="Project structure"
docs/blog/posts/
├── hello-world.md              (フラットでも問題ありません)
├── 2026/
│   ├── announcing-2-0.md
│   └── 03/
│       └── a-deep-dive.md
```

どこに置かれた投稿であっても、フロントマターは次の通りです:

```markdown title="docs/blog/posts/announcing-2-0.md" linenums="1"
---
title: Announcing BoxLang 2.0
date: 2026-08-15
authors: [lmajano]
categories: [Releases]
tags: [boxlang, release]
summary: A faster runtime, a smaller footprint, and a few surprises.
image: assets/blog/boxlang-2-cover.png
---

A short intro paragraph or two.

<!-- more -->

The rest of the post - everything below the `<!-- more -->` marker is left
out of the excerpt shown on `/blog/` and category pages, but still renders
in full on the post's own page.
```

- `date`（必須）- BxSites が解析できる任意の形式（`2026-08-15`、または完全な
  日時）。投稿自身の並び順（新しい順）と `<pubDate>`/`article:published_time`
  を決めます。
- `authors` - [`docs/blog/authors.yml`](#著者) のエントリに対応する id の
  リスト、またはエントリと一致しないプレーンな名前（リンクされないテキスト
  としてレンダリングされ、ビルドは失敗しません - ゲスト投稿単発時に便利です）。
- `categories` - 投稿自身の分類法で、それぞれが独自の `/blog/category/<slug>/`
  ページ（と独自の `/blog/category/<slug>/feed.xml` RSS フィード -
  [フィード](#フィード) を参照）を持ちます。以下の `tags` とは無関係です。
- `tags` - 他のすべてのページがすでに持つ、サイト全体共通の `tags`
  フロントマターと同じもの（[はじめに](../getting-started.md#ページの追加) を
  参照）- 投稿のタグはバッジとしてレンダリングされ、他のタグ付きページと
  並んでメインの `/tags/` 索引に統合されます。
- `summary` - `/blog/`/カテゴリページと RSS フィードに表示される一行の抜粋。
  投稿に `<!-- more -->` マーカーがない場合に使われます。どちらもない場合、
  BxSites は投稿本文のプレーンテキストの切り詰めにフォールバックします。
- `image` - フィーチャー画像（`docs/assets/` 相対パス、または完全な URL）-
  投稿の先頭と、すべてのリスト/カテゴリカードのサムネイルとして表示されます。
  `ogImage` が個別に上書きしない限り、投稿自身の `og:image`/Twitter カードにも
  なります。`docs/assets/` 相対の画像（および下記の著者自身の `avatar`）は、
  `docs/assets/` 配下の他の画像と同じレスポンシブな `<picture>`/`srcset`/WebP
  処理を受けます - [画像](images.md) を参照してください。
- `slug` - URL セグメント（`/blog/<slug>/`）を上書きします - デフォルトでは
  ファイル名から導出されます。
- `draft: true` - 実際の `bxSites build` から投稿を完全に除外します。
  `bxSites serve` はそれでもプレビューします（投稿自身に目立つ「🚧 Draft」
  バナー、リストされる場所には破線ボーダーのカード付きで）。これにより、
  準備が整う前にドラフトをローカルで校正できます -
  [ドラフトのプレビュー](#ドラフトのプレビュー) を参照してください。

[はじめに](../getting-started.md#ページの追加) ですでに解説した他のすべての
ページレベルのフロントマターキー（`icon`、`description`、`ogImage`、`toc`）は、
投稿でもそのまま機能します。

## フィーチャー画像とその他のブログアセット

`docs/assets/blog/` は、`docs/assets/` の普通のサブフォルダである
（すでにまるごと `site/assets/` にコピーされる）以上の特別な扱いはありません -
このガイド（と下記の規約ベースの著者アバター検索）が、投稿のカバー画像や
著者の写真が置かれることを期待する場所というだけです。こうしておくことで、
プロジェクト自身の `docs/assets/` が他の図表やアイコンとブログ画像で
ごちゃ混ぜにならずに済みます。この場所を強制するものは何もありません -
`image`/`avatar` にはどんな `docs/assets/**` パスでも使えます。

## 著者

`docs/blog/authors.yml` は任意で、著者 id ごとに 1 エントリを持ち、投稿自身の
`authors` リストから参照されます:

```yaml title="docs/blog/authors.yml" linenums="1"
lmajano:
  name: Luis Majano
  title: CEO, Ortus Solutions
  bio: >
    Founder of Ortus Solutions and creator of ColdBox, WireBox, and
    BoxLang. Building developer tools since 2005.
  url: https://github.com/lmajano
  email: lmajano@ortussolutions.com
  socials:
    github: https://github.com/lmajano
    twitter: https://x.com/lmajano
```

必須なのは `name` だけです。少なくとも 1 件の投稿から参照された著者は、
それぞれ独自の `/blog/authors/<id>/` ページ（略歴、ソーシャルリンク、
執筆したすべての投稿）を持ちます - まだ誰の投稿からもクレジットされていない
著者は、名簿にいてもページを持ちません。

**アバターは規約ベースです** -
`docs/assets/blog/authors/<id>.{jpg,jpeg,png,webp,svg}` にファイルを置けば、
`avatar:` キーなしで自動的に検出されます。`authors.yml` の明示的な `avatar`
（URL または `docs/assets/` 相対パス）は、常に規約ベースの検索より優先されます。

## カテゴリ、アーカイブ、ページネーション、そして「ブログ」のナビ項目

すべての投稿を通じて異なる `categories` の値は、それぞれ独自の
`/blog/category/<slug>/` ページを持ち、そのカテゴリ自身の投稿だけを
一覧表示します。少なくとも 1 件の投稿がある暦年もそれぞれ独自の
`/blog/archive/<year>/` ページ（`/blog/archive/2026/`、
`/blog/archive/2025/`、...）を持ち、これは各投稿の `date` フロントマター
だけから導出されます - フォルダ構造やファイル名の規約は一切不要なので、
投稿の `.md` ファイルが `docs/blog/posts/` の下で実際にどこにあるか
（フラット、または編集しやすいように独自のサブフォルダに分割）は、その
`date` と一致している必要はありません。メインの `/blog/` 一覧には、投稿が
複数の年/カテゴリにまたがるようになった時点で自動的に「年別で見る」/
「カテゴリ別で見る」のリンクブロックが、年/カテゴリごとの投稿数とともに
追加されます - 単一の年やカテゴリしかない場合はリンクブロックを出す価値が
ないため、どちらの場合も省かれます。

メインの `/blog/` 一覧、すべてのカテゴリページ、すべての年次アーカイブ
ページは同じ方法でページネーションされます - サイト設定の
`blog.postsPerPage` が 1 ページあたりの投稿数を制御し（デフォルト `10`）、
2 ページ目以降は `.../page/2/`、`.../page/3/` などに進みます。

`docs/blog/posts/` にドラフトでない投稿が 1 件でもあると、メインナビに
「Blog」エントリが自動的に追加されます - `nav`/`docs/nav.json` の変更は
不要です。デフォルトでは他のすべての後、最後に追加されます。特定の場所に
置きたい場合は、明示的な `url` を持つ独自のエントリ（ブログは `docs/`
ページではないため、通常の「`path` が実在するページと一致していなければ
ならない」ルールを回避します）を `nav` 配列または `docs/nav.json` に
追加してください - そうすると自動追加分が完全に抑制されるため、重複が
発生することはありません:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - path: index.md
      - title: Blog
        url: blog/index.html
        icon: lucide:newspaper
      - path: about.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		{ "path": "index.md" },
    		{ "title": "Blog", "url": "blog/index.html", "icon": "lucide:newspaper" },
    		{ "path": "about.md" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Blog"
    url = "blog/index.html"
    icon = "lucide:newspaper"

    [[nav]]
    path = "about.md"
    ```

個々の投稿自体はナビには追加されません（タグ索引と同様）- それらは
`/blog/`、自分のカテゴリページ、自分の年次アーカイブページ、著者ページ、
検索、そして互いの前後リンク（通常のナビ自体の前後チェーンとは独立した、
時系列で隣り合う投稿同士のリンク）から到達できます。

すべての投稿のメタ行（カード上と詳細ページ上の両方）には、日付の横に
推定読了時間も表示されます - おおよその単語数 ÷ 200wpm という、ほとんどの
読了時間機能が使うのと同じざっくりした見積もりで、設定はできません。

## フィード

`/blog/feed.xml` - 最新の投稿を新しい順に並べた標準の RSS 2.0 フィードで、
サイト設定が絶対 URL の `baseURL` を解決でき（`sitemap.xml` と同じ要件）、
かつ `blog.feed` が `false` に設定されていない場合に書き出されます。
各カテゴリもそれぞれ独自のフィルタ済みフィード
`/blog/category/<slug>/feed.xml` を持ちます。どちらも `blog.feedLimit`
件の投稿数（デフォルト `25`）を上限とします - ほとんどのフィードリーダーは
新着にしか関心がないため、大きなブログで無制限のフィードを出すのは
ポーリングのたびに帯域を無駄にするだけです。すべての投稿を無制限にするには
`0` を設定します:

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [blog]
    postsPerPage = 10
    feed = true
    feedLimit = 25
    ```

## ドラフトのプレビュー

`draft: true` は投稿を実際の `bxSites build` から完全に除外しますが、
`bxSites serve` はそれでも含めるため、公開前にドラフトを一通り読み
（すべてのリンクをクリックし、フィーチャー画像を確認し、`/blog/` での
表示を見て）確認できます。プレビューされたドラフトには常に目立つ
「🚧 Draft」バナーが表示されます - 自身の詳細ページと、リストされる
あらゆる場所（メインの `/blog/` 一覧、自分のカテゴリ/アーカイブ/著者
ページ）での破線ボーダーカードとして。これにより、実際に公開されている
ものが何かについて曖昧さが生じることはありません。`bxSites serve` を
止めて `bxSites build` を実行すれば、そのドラフトはまるで存在しなかった
かのように消えます。

## 統計

`/blog/stats/` - ブログ全体についての集計カードのひとまとまりです:
総投稿数、総執筆単語数、平均読了時間、カテゴリ/寄稿者/年ごとの件数、
そして 3 つの「スポットライト」カード（最長投稿、最も活発なカテゴリ、
最も活発な著者）で、それぞれ実際の該当ページにリンクされています。
このビルドですでに読み込まれた投稿だけから純粋に計算されます - 別途の
アナリティクスもトラッキングもなく、ビルド間で永続化されるものも何も
ありません - そして投稿がまだゼロ件の真新しいブログでも常にビルド
されます。メインの `/blog/` 一覧の下部からリンクされています。

## SEO とソーシャル

すべての投稿は通常のページと同じもの（`<meta name="description">`、
`og:description`、画像が設定されていれば `og:image`+`twitter:card` -
[設定: `ogImage`](../configuration.md#ogimage) を参照）に加えて、すべての
組み込みテーマが自動的に追加する投稿固有のタグをいくつか受け取ります:
`og:type` は `"website"` の代わりに `"article"` になり、
`article:published_time`/`article:author`（`authors.yml` に `url` を
設定しているクレジット済み著者ごとに 1 つ）がページの `<head>` に
含まれます。

## 検索

投稿は他のすべてのページと同じ `search-index.json` にインデックスされます
（モジュール仕様セクション 7）- 別のブログ検索 UI はなく、既存の検索ボックスが
そのままドキュメントページと並んで投稿も見つけます。

## ブログの見た目をカスタマイズする

書くべき別個の「ブログテーマ」はありません - すべてのブログページ
（メインの `/blog/` 一覧、カテゴリ/アーカイブ/著者ページ、`/blog/stats/`、
各投稿自身の詳細ページ）は、サイトの他のページとまったく同じ
`layout.bxm`/`page.bxm` を通してレンダリングされます。そのため、ブログは
自動的にドキュメントの他の部分と同じ見た目になり、すでに行った
テーマオーバーライド（[テーマ](themes.md) を参照）が
追加配線なしでそのまま適用されます。

ブログ固有のマークアップ自体（投稿カード、日付/著者/読了時間のメタ行、
ページャー、著者のプロフィールブロック、「年別で見る」/「カテゴリ別で
見る」のリンクリスト）は、固定のクラス名をいくつか持つプレーンな HTML
として構築され、変換済み Markdown ページとまったく同じように
`page.contentHtml` に流し込まれます:

| クラス | 表示される場所 |
|---|---|
| `blog-post-card` / `blog-post-card--draft` | `/blog/`、カテゴリページ、またはアーカイブページ上の各投稿のカード |
| `blog-post-meta` | カード上と投稿自身のページ上の、日付/著者/読了時間の行 |
| `blog-post-featured-image` | 投稿の `image` フロントマター、投稿自身の詳細ページ上 |
| `blog-draft-badge` | 「🚧 Draft」バナー（`bxSites serve` のみ） |
| `blog-pager` | ページネーションされた一覧上の前/次ページネーションリンク |
| `blog-author-profile` | `/blog/authors/<id>/` ページ上の著者の略歴/ソーシャルブロック |
| `blog-archive-links` / `blog-category-links` | `/blog/` 上の「年別で見る」/「カテゴリ別で見る」のリンクブロック |

他のページと同様、再スタイリングする方法は 2 つあります:

- **手早い見た目の調整** - 自分自身の
  [`extraCss`](../configuration.md#extracss--extrajs) からこれらの
  クラスをターゲットにします。[テーマの色をカスタマイズする](themes.md#テーマオーバーライドなしの色のカスタマイズ)
  のと同じ方法です。組み込みテーマ自身のこれらのクラス向けルールは、
  その `assets/style.css`（例: `resources/themes/bootstrap/assets/style.css`）
  にあります。出発点として上書きしたい場合はそちらを参照してください。
- **構造的な変更** - ブログページは他のすべてと `layout.bxm`/`page.bxm`
  を共有しているため、[テーマをオーバーライドする](themes.md)
  （またはゼロからテーマを書く）ことで、他のすべてのページと同じくブログのクロム（ヘッダー、ナビ、
  フッター、記事ラッパー）も変わります - コピーすべき別個のブログ
  テンプレートはありません。

できないのは、投稿カード/ページャー/著者プロフィールのマークアップ自体を
独自のものに差し替えることです - これは `theme/` 内のテンプレート
ファイルから読み込まれるのではなく、`BlogBuilder.bx` によって一度だけ
生成されるため、（上記の）CSS による再スタイリングがサポートされている
方法であり、コンポーネントごとの上書きではありません。
