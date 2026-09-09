---
title: リダイレクト
order: 11
icon: phosphor-duotone:signpost
tags: [ガイド, リダイレクト]
---

# リダイレクト

ページを移動、リネーム、または再構成した後も、古い URL を動作させ続け
ます - 古いパスに静的な HTML スタブが書き込まれるため、検索エンジンの
古くなったインデックスエントリや誰かの古いブックマークが 404 になる
代わりに、引き続き正しいページに到達します。サーバー側のリライトルール
は一切関与しません（静的ホストにはそれを実行する場所がありません）-
スタブは、ブラウザが自分でリダイレクトし、クローラーが本当の正規 URL
を知るのに十分な HTML にすぎません。

## ページ単位: フロントマター `redirect_from`

ページ自身のフロントマターに、1 つ以上の古いパスを追加します:

```md title="docs/guides/new-setup.md"
---
title: New Setup
redirect_from:
  - guides/old-setup
  - setup
---
```

各エントリはプリティ URL セグメントです - 先頭/末尾のスラッシュなし、
`.md`/`.html` 拡張子なし - ページ自身の URL と同じ形式です。ビルドは
それぞれについてスタブを書き込みます（上記の例では
`site/guides/old-setup/index.html`、`site/setup/index.html`）、どちら
もこのページ自身の本当の URL にリダイレクトします。

`redirect_from` は、ページ自身が属するツリーに限定されます -
バージョン自身のページはそのバージョン内でリダイレクトし
（`site/versions/2.0/old-path/`）、ロケール自身の翻訳されたページは
そのロケール内でリダイレクトします（`site/es/old-path/`）、ページ自身
の本当の URL がすでにそうしているのとまったく同じです。ツリーごとに
追加で設定するものは何もありません。

## サイト全体: `bxsites.yaml` の `redirects`

特定のページに属したことがない古い URL - 再構成されたセクション、古い
ドメインのパス、単一ページ自身の「古い名前」として自然ではない何か -
の場合は、代わりに明示的な `from`/`to` のペアを列挙します:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
      - from: moved-to-another-site
        to: https://example.com/docs
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" },
    		{ "from": "moved-to-another-site", "to": "https://example.com/docs" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"

    [[redirects]]
    from = "moved-to-another-site"
    to = "https://example.com/docs"
    ```

- `from` - 上記の `redirect_from` と同じ形式の古いプリティ URL セグメント
- `to` - ルート相対パス（サイト自身の `baseURL` に対して解決されます、
  `theme.logo`/`ogImage` がすでに使用しているのと同じ規約）、または
  完全に別サイトへリダイレクトするための完全な `https://` URL

`redirects` は常にメインのサイトツリーにのみ適用されます - 素の `to`
はルート相対パスであり、サイトのルートでのみ曖昧さがありません。同じ
古い URL のマッピングを望むバージョン/ロケールツリーは、代わりに自身
のページ単位の `redirect_from` が必要です。

## `page:rename` が自動的に設定します

`page:rename` でページをリネーム/移動すると、移動したページ自身の
`redirect_from` に古いパスが自動的に追加されます - それを指していた
すべての相対 Markdown リンクを書き換えることに加えて、古い URL 自体も
引き続き機能します:

```bash title="使い方"
bxSites page:rename --from=guides/old-setup.md --to=guides/new-setup.md
```

ページを複数回リネームしても、単に追加され続けます - ページの
`redirect_from` リストは、時間の経過とともに持っていた古いパスをいくつ
でも保持できます。

## 競合

以下の場合、実際のコンテンツを黙って上書きするのではなく、ビルドは
完全に失敗します:

- リダイレクト自身の `from` パスが、その同じパスにすでにビルドされた
  実際のページと衝突する場合（`BxSites.RedirectConflict`）
- 2 つのリダイレクト（`redirect_from` エントリ、`redirects` 設定エント
  リ、またはそれぞれ 1 つずつ）が同じ `from` パスを対象にする場合

## 現在対象外の機能

- **ブログ投稿は `redirect_from` を取得しません。** フロントマターの
  キーは通常の `docs/` ページに対してのみ読み込まれ、
  `docs/blog/posts/**` には読み込まれません - 移動したブログ投稿には
  代わりに独自の `redirects` 設定エントリが必要です。
- **ワイルドカード/パターンのリダイレクトはありません。** すべての
  `from` は正確な 1 つの古いパスです - `guides/old/*` のような
  キャッチオールはありません。
