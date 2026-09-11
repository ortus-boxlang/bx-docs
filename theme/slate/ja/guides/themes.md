---
title: テーマ
order: 1
icon: phosphor-duotone:palette
tags: [ガイド, テーマ]
---

# テーマ

テーマはネイティブの BoxLang `.bxm` テンプレートです。専用のテンプレートエンジンや
ビルドステップは不要です。

## 組み込み

| テーマ | ベース | 備考 |
|---|---|---|
| `bootstrap`（デフォルト） | [Bootstrap 5](https://getbootstrap.com/)（バンドル同梱） | Poppins フォント、ブランドグラデーション navbar |
| `material` | 手作りの Material スタイル CSS | カードレイアウト、エレベーションシャドウ、Roboto フォント |
| `tailwind` | [Tailwind Play CDN](https://tailwindcss.com/) | ユーティリティクラス駆動、ビルドステップ不要 |
| `docsy` | 手作りの CSS（`material` からフォーク） | Read the Docs/Docsy にインスパイアされた、ネイビーブルーのリファレンスマニュアル調 |
| `slate` | 手作りの CSS（`material` からフォーク） | Stripe/Slate にインスパイアされた - ライト/ダークモードに関わらず常にダークなサイドバー |
| `docusaurus` | 手作りの CSS（`material` からフォーク） | Docusaurus にインスパイアされた、大胆な全幅カラー navbar と角丸カード |
| `justthedocs` | 手作りの CSS（`material` からフォーク） | Just the Docs にインスパイアされたミニマリズム - 検索ボックスがサイドバー上部にあります |
| `vuepress` | 手作りの CSS（`material` からフォーク） | VuePress にインスパイアされたグリーンアクセント、柔らかい角丸 |
| `gitbook` | 手作りの CSS（`material` からフォーク） | GitBook にインスパイアされた中央寄せの読み物向けカラム、セリフ体の見出し |
| `notion` | 手作りの CSS（`material` からフォーク） | Notion にインスパイアされたボーダーレスなサイドバー、ほぼグレースケールの UI、ゆとりのある余白 |

上記の `material` からフォークされた 7 つのテーマは、`material` と全く同じ BoxLang
テンプレート（layout.bxm/page.bxm/search.bxm）を、スコープ付き CSS クラスプレフィックスの
リネームを除いて無変更のまま再利用しています - 異なるのは `assets/style.css` だけです
（`justthedocs` については、検索ボックスをサイドバーに移動する `<bx:include>` 行が
1 つ再配置されている点も異なります）。そのため、これらは `material` がすでに持つのと
同じ完全な機能セットと、同じエアギャップ対応の挙動を受け継ぎます。

各組み込みテーマ自身の CSS/JS（Bootstrap の CSS/JS バンドル、highlight.js、Alpine.js、
デフォルトの `local` 検索プロバイダー用の lunr.js、および `mermaid` を有効にした場合の
Mermaid）は、このモジュールに同梱（バンドル）された状態で出荷され、ビルドされた `site/`
にそのままコピーされます - ビルド済みサイトの閲覧に CDN もインターネットアクセスも
一切不要です。`tailwind` テーマ自身のユーティリティエンジン（静的なスタイルシートでは
なく、クライアントサイドの JIT コンパイラ）と、自分で有効にしたその他のオプション機能
（`math`、Algolia 検索、Google Analytics）は、依然として CDN やホスト型 API から読み込
まれます - 詳細は下記の
[エアギャップ/オフラインサイト](#エアギャップオフラインサイト) を参照してください。

`bootstrap`、`material`、`tailwind` は同じ BoxLang ブランドパレット（`#00FF78 → #00DBFF`
グラデーションと `#FFF500` アクセント）を適用します。その下にある 7 つのギャラリーテーマは
それぞれ、参考にしたプラットフォームの見た目に着想を得た独自の異なるパレットを使用します -
詳しくは上の表を参照してください。パレットに関わらず、10 個すべてが同じページ機能一式を
搭載しています:

- **ページ内「このページの内容」目次** - 各ページの `h2`/`h3` 見出しから生成されます。
- **パンくずリスト** - リンクされた祖先の1つ以上深くネストされたページで、その祖先チェーンを表示します。
- **前/次ページリンク** - 記事の下部に、ナビゲーションの読み取り順に従って表示されます。
- **構文ハイライトされたコードブロック** - [highlight.js](https://highlightjs.org/) と
  独自の BoxLang グラマー（` ```bx `/` ```boxlang `/` ```cfscript `）による構文ハイライト。
  それぞれに**コピーボタン**付き - 対応デバイスではホバー時に表示され、タッチデバイスでは
  （ホバーで表示するという概念がないため）常に表示されます。
  [Markdown 拡張機能](markdown.md#コードブロック) を参照してください。
- **自己ホスト型 Web フォント** - 表示時に `fonts.googleapis.com` へのリクエストなし。
- **ダーク/ライトモード切替** - リアクティビティに [Alpine.js](https://alpinejs.dev/) を使用。
  訪問者の選択は `localStorage` に記憶され（未選択時は OS の設定にフォールバック）、
  誤ったテーマがちらつくのを防ぐため初回描画前に適用されます。
- **レスポンシブヘッダー** - あらゆる幅で常に1行に収まります - 狭いビューポートでは
  検索ボックスを自身の行に折り返すのではなく縮小します - さらに折りたたみ可能な
  サイドバーナビ（`bootstrap`/`material`/`tailwind` いずれもハンバーガートグル）付き。
- **検索ボックスのキーボードショートカット** - `/` でページのどこからでも検索にフォーカスし、
  `Escape` で結果を閉じます。[検索](search.md) を参照してください。
- **リポジトリリンクと「このページを編集」/「最終更新」行** - `bxsites.yaml` の
  `repo`/`lastUpdated` オプションが設定されている場合。[設定](../configuration.md#repo)
  を参照してください。
- **「Markdown をダウンロード」リンク** - 「このページを編集」の隣に表示されます -
  各ページの生の `.md` ソースは、ビルドされた HTML と一緒に公開されるため
  （`guides/themes.md` は `guides/themes/index.html` の隣に置かれます）、人間（や LLM）は
  レンダリング済みの HTML を解析する代わりに、プレーンな Markdown としてページを
  直接読めます。設定不要で常に有効です。
  [はじめに](../getting-started.md#markdown-としてページをダウンロード) を参照してください。
- **オプトインのフッター**（著作権、`social` リンク、「Built with BxSites」クレジット）-
  `bxsites.yaml` の `footer` が `true` の場合。[設定](../configuration.md#footer) を参照してください。
- **バージョンスイッチャー** - プロジェクトが複数バージョンを含む `docs/versions/` フォルダを
  持つと自動的に表示されます。[設定](../configuration.md#versioning) を参照してください。
- **テーマ化された `404.html`** - ほとんどの静的ホスト（GitHub Pages を含む）で、
  一致しないパスに対して自動的に提供されます。`docs/`（または `src/`）の直下に
  `404.md` を置くと、タイトルと本文を独自の内容で上書きできます - 通常のページ
  としてはビルドされず（ナビ項目も `sitemap.xml` の URL も生成されません）、
  代わりに `site/404.html` としてレンダリングされます。
- **カスタムロゴとファビコン** - `bxsites.yaml` の `theme.logo`/`theme.favicon` が
  設定されている場合。[設定](../configuration.md#theme) を参照してください。
- **折りたたみ可能なサイドバーナビ** - `theme.options.navCollapsible` でオプトイン -
  子要素を持つすべてのセクション（リンク付き/なしを問わず）が、常にインラインで
  子要素を表示する代わりにトグルボタンを持つようになり、現在のページを含むセクションは
  常に開いた状態で始まります。[設定](../configuration.md#theme) を参照してください。
- **Google Analytics** - `bxsites.yaml` の `analytics` が設定されている場合。
  [設定](../configuration.md#analytics) を参照してください。
- **ソーシャルシェアカード**（Open Graph + Twitter Card メタタグ）- 各ページの
  `description` フロントマター（またはサイト全体の `description`）と、それ自身の
  `ogImage`（またはサイト全体のもの）から生成されます - `bxsites.yaml` の
  `generateOgImages` でページごとに自動生成することも任意で選べます。
  [設定](../configuration.md#ogimage) を参照してください。
- **ページタグ、アイコン、サマリ行** - すべてページ自身のフロントマターでオプトインします -
  タグはサイト全体の `/tags/` インデックスにリンクするバッジとしてレンダリングされます。
  [はじめに](../getting-started.md#ページの追加) を参照してください。
- **明示的な nav オーバーライド** - `bxsites.yaml` または独自の `docs/nav.json` で、
  大規模サイト向けにフォルダ推定を置き換えます。[設定](../configuration.md#nav) を参照してください。
- **追加の CSS/JS** - `bxsites.yaml` の `extraCss`/`extraJs` で注入されます。
  [設定](../configuration.md#extracss--extrajs) を参照してください。
- **Admonition（note/warning/tip/...）コールアウトボックス** - どのページの Markdown でも
  デフォルトで有効で、折りたたみ可能なバリアントも含みます - 設定不要です。
  [Markdown 拡張機能](markdown.md#admonition) を参照してください。
- **脚注と定義リスト** - `bxsites.yaml` の `markdown` でオプトインします。
  [Markdown 拡張機能](markdown.md#脚注) を参照してください。
- **コンテンツタブ**、**コードの行番号/ハイライト行/タイトル**、コードブロック用の
  **差分マーカー/ターミナル風フレーム** - 設定不要です。
  [Markdown 拡張機能](markdown.md#コンテンツタブ) を参照してください。
- **レスポンシブ画像** - 対象となるすべての `docs/assets/**` 画像に対する、リサイズ済み +
  WebP バリアントと `<picture>` への書き換え - デフォルトで有効です。
  [レスポンシブ画像](images.md) を参照してください。
- **Mermaid ダイアグラム** - `bxsites.yaml` の `mermaid` でオプトインします。
  [Markdown 拡張機能](markdown.md#ダイアグラム) を参照してください。
- **数式**（KaTeX） - `bxsites.yaml` の `math` でオプトインします。
  [Markdown 拡張機能](markdown.md#数式) を参照してください。

プロジェクトが使用するテーマは `bxsites.yaml` で設定します:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: material }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "material" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "material"
    ```

## 公開済みテーマのインストール

ForgeBox に公開されたテーマは、`bxSites` バイナリ自体だけでインストールできます -
`box`/CommandBox は不要です。すでに公開されているパッケージは、ForgeBox の
[`bxsites-themes`](https://www.forgebox.io/type/bxsites-themes) カテゴリーから
探せます:

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

これはパッケージの zip をダウンロードし、プロジェクトルートの
`themes/bx-sites-theme-blog1/` に展開し、完了する前に下記の `ThemeProvider` 契約を
満たしていることを検証します。この方法で、プロジェクトは複数のインストール済み
テーマを並行して保持し、純粋に名前だけで切り替えることができます:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: bx-sites-theme-blog1 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "bx-sites-theme-blog1" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "bx-sites-theme-blog1"
    ```

テーマは（プラグインとは異なり）BoxLang モジュール/クラスローダーの関与を一切必要と
しません - 単なるファイル群であるため、`install:plugin` にあるような別途の有効化
ステップはなく、`theme.name` を設定するだけで配線が完了します。CLI リファレンスの
[`install:theme`](../cli-reference.md#installtheme) を参照してください。

代わりに他の静的サイトジェネレーター向けに作られたテーマから始めたいですか？
[テーマのインポート](theme-import.md) を参照してください - `theme:import` は
mkdocs/jekyll/hugo テーマ自身のテンプレートファイルを、ベストエフォートで
`themes/<name>/` スキャフォールドへと機械的に変換します。

## エアギャップ/オフラインサイト

ビルドされたサイトは、`bootstrap`、`material`、そして `material` からフォークされた
7 つのテーマ（`docsy`、`slate`、`docusaurus`、`justthedocs`、`vuepress`、`gitbook`、
`notion`）をデフォルトの `local` 検索プロバイダーと組み合わせた場合、デフォルトで
インターネットアクセスが一切なくても動作します。Bootstrap 自身の CSS/JS、
highlight.js、Alpine.js、lunr.js はすべてこのモジュールに同梱されており
（`resources/assets/vendor/`）、ビルド時に `site/assets/vendor/` へそのままコピー
されます - これらのどれについても、生成される HTML のどこにも CDN の
`<script>`/`<link>` タグは一切含まれません。`bxsites.yaml` の `mermaid` キーを
有効にすると、Mermaid も同様に同梱され、`mermaid.min.js` バンドルが
`site/assets/vendor/mermaid/` にコピーされて、組み込みのすべてのテーマがそこから
読み込むため、図表も外部への通信なしで描画されます。

ただし、自分で有効にした場合に限り、いくつかの機能はまだネットワークに
アクセスします:

- `tailwind` テーマ自身のユーティリティエンジンは、`cdn.tailwindcss.com` から
  読み込まれるクライアントサイドの JIT コンパイラです - 静的なスタイルシートでは
  ないため、このモジュールが同様の方法で同梱することができず、このテーマは
  今のところエアギャップ環境には対応していません。
- Mermaid 自身のレイアウトエンジンは、追加のチャンクである `elk-api.js` を
  jsDelivr から遅延読み込みしますが、これは `elk` レイアウトアルゴリズムを
  使用するダイアグラムタイプに限られます。同梱された `mermaid.min.js` は、
  それ以外のすべてのダイアグラムタイプを完全に単独でレンダリングします。
- `bxsites.yaml` の `math` オプションを有効にすると、KaTeX（JS 本体とその独自
  フォントファイルの両方）を CDN から読み込みます。
- `searchProvider.provider: "algolia"` と `analytics.provider: "google"` は、
  その性質上ホスト型の API／トラッキングエンドポイントと通信します - JS
  ファイルを同梱しても、この依存関係はなくなりません。

デプロイ先が本当に一切インターネットにアクセスできない環境である場合は、
`bootstrap`/`material`、または `material` からフォークされた 7 つのテーマのいずれかを、
デフォルトの `local` 検索プロバイダーとともに使い、`mermaid` を有効にしている場合は
`elk` レイアウトの Mermaid ダイアグラムを避け、`math`/Algolia/analytics はオフの
ままにしてください。

ページ自身の `icon` フロントマター（または `nav.json` エントリ自身の `icon`）が、
絵文字、8 つの同梱ライブラリのいずれかからの名前付きアイコン、またはプロジェクト
独自のカスタム SVG にどのように解決されるかについては、[アイコン](icons.md) を
参照してください。

## `ThemeProvider` コントラクト

テーマは単なる以下を含むフォルダです:

- **`layout.bxm`**（必須） - 外側の HTML シェル + ナビゲーション。スコープ内に
  `variables.page`、`variables.nav`、`variables.siteConfig`、`variables.themeDir`、
  `variables.basePath` を受け取り、`#variables.themeDir#/page.bxm` 経由で兄弟の
  `page.bxm` をインクルードします。`variables.basePath` は常に `/` で終わる
  ルート相対パスです（デフォルトは `/`。`bxsites.yaml` の `baseURL` が上書きしている
  場合は `/my-docs/` など）- 先頭に `/` をハードコードするのではなく、すべての内部
  `href`/`src` にこれをプレフィックスしてください。そうすることで、サイトがサブパス
  から配信されてもテーマは動作し続けます。
- **`page.bxm`**（必須） - 記事本文。すでに変換済みの Markdown である
  `variables.page.contentHtml` をレンダリングします。
- **`search.bxm`**（省略可） - 検索ボックスのマークアップ。`bxsites.yaml` の `search`
  が `true` の場合のみ `layout.bxm` にインクルードされます。[検索](search.md) を
  参照してください。
- **`assets/`**（省略可） - テーマの CSS/JS。ビルド時に `site/assets/theme/` に
  コピーされます。

`variables.page.editUrl`/`.lastUpdated`（未設定時は空文字列）と
`variables.siteConfig.repo`/`.social`/`.footer` も常に利用可能で、上記のリポジトリ
リンク/編集リンク/最終更新/フッター機能を支えています - カスタムテーマは、他の
すべてと同様に、これらをレンダリングするかどうか、どのようにレンダリングするかを
自分で決めます。`variables.versions`（`[ { label, url } ]`、「Latest」が先頭）と
`variables.currentVersion`（現在レンダリング中の `label`）はバージョンスイッチャーを
支えます - バージョン管理されていないプロジェクトでは空/`"Latest"` になるため、
テーマは `variables.versions.len() gt 1` のときだけスイッチャーをレンダリングすれば
済みます。すべての組み込みテーマは、小さな共有 SVG ルックアップ
`<bx:include template="#variables.moduleAssetsDir#/icons.bxm">`（`bxsitesIcon( name )`
を定義し、`github`、`twitter`/`x`、`rss`、`youtube`、`linkedin`、`facebook`、
`bluesky`、`threads`、`slack`、`patreon`、`email`、`edit`、`clock` のいずれか、
それ以外は汎用のリンクグリフにフォールバック）からリポジトリ/ソーシャルアイコンを
取得します - カスタムテーマも同じ方法でこれをインクルードするか、完全に独自の
アイコンを提供できます。

必須ファイルのいずれかが欠けているテーマフォルダは、レンダリングの奥深くで分かりにくい
テンプレートエラーになるのではなく、ビルド時に明確な `BxSites.InvalidTheme` エラーで
即座に失敗します。

## テーマオーバーライドなしの色のカスタマイズ

色やフォントの微調整のためにテーマ全体をフォークするのは過剰です - 各組み込みテーマは、
`:root` 上の少数の CSS カスタムプロパティからパレットを読み込み、ダークモード用に
`[data-theme="dark"]` の下で再宣言しています。`bxsites.yaml` の
[`extraCss`](../configuration.md#extracss--extrajs) はテーマ自身のスタイルシートの
*後に* 読み込まれるため、そこで同じ詳細度で再宣言すれば、`resources/themes/` に
一切触れることなく優先されます:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/brand.css ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/brand.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/brand.css" ]
    ```

```css title="docs/assets/brand.css" linenums="1"
/* docs/assets/brand.css - ビルド時に site/assets/brand.css にコピーされます */
:root {
	--bxsites-gradient-start: #7C3AED;
	--bxsites-gradient-end: #DB2777;
	--bxsites-accent: #FBBF24;
	--bxsites-link: #7C3AED;
	--bxsites-link-hover: #9F5AF0;
}

[data-theme="dark"] {
	--bxsites-link: #C4B5FD;
	--bxsites-link-hover: #DDD6FE;
}
```

`bootstrap` テーマ自身のセット（`resources/themes/bootstrap/assets/style.css`）は
`--bxsites-gradient-start`/`-end`、`--bxsites-accent`、`--bxsites-bg`、
`--bxsites-text`、`--bxsites-sidebar-bg`、`--bxsites-sidebar-text`、
`--bxsites-border`、`--bxsites-link`、`--bxsites-link-hover`、`--bxsites-code-bg`、
`--bxsites-step-marker-bg`、`--bxsites-step-marker-text`、`--bxsites-step-line`、
`--bxsites-step-success-bg`/`-text`、`--bxsites-step-warning-bg`/`-text`、
`--bxsites-step-danger-bg`/`-text`、そしてレンダリングされたテーブルのカード、
ヘッダー帯、区切り線、ゼブラ/ホバーの色味を描画する
[`--bxsites-table-*` セット](tables.md#テーマ)です。すべての組み込みテーマは
`--bxsites-gradient-start`/`-end`、`--bxsites-accent`、`--bxsites-step-*`
セット、`--bxsites-table-*` セットをこれらの正確な名前で保証しているため、`extraCss` はテーマに関わらず常に
ブランドカラー/ステッパーアクセントを再ターゲットできます - ただし
`--bxsites-bg`/`-text`/`-sidebar-bg`/`-sidebar-text`/`-border`/`-link`/`-link-hover`/`-code-bg`
をこれらの名前で公開しているのは `bootstrap`、`slate`、`notion` だけです
（`justthedocs` は 2 つの `-sidebar-*` 系以外すべてを同じ方法でエイリアスします）。
それ以外の組み込みテーマ（`material`、`tailwind`、`docsy`、`docusaurus`、
`vuepress`、`gitbook`）は、この2番目のグループに独自の内部カスタムプロパティ名を
使用します（例えば material 自身の `assets/style.css` は
`--md-bg`/`--md-ink`/`--md-link`/... を使用します）- `extraCss` 経由でそれらの1つを
上書きする前に、そのテーマ自身の `assets/style.css` を開いて実際の名前を確認して
ください。色/フォント以外のもの（レイアウト、クロームの追加/削除）には、本物の
オーバーライドかカスタムテーマが必要です - 下記を参照してください。

残りは [`::: stepper`/`::: step`](content-blocks.md#ステッパー) ディレクティブ
ブロックを支えています - `--bxsites-step-marker-bg`/`-text` はデフォルトの番号付き
円のバックグラウンド/テキスト色です（`bootstrap`/`material` はこれをテーマ自身の
`--bxsites-accent` にデフォルトします。`tailwind` は単一の共有アクセントトークンを
持たないため、専用のティール/ミントのペアを使用します）。`--bxsites-step-line` は
ステップ間の接続線、`-success`/`-warning`/`-danger` のペアはステップ自身の任意の
`color="..."` 属性を支えます - デフォルトのマーカーとは異なり、これら3つは
ライト/ダークモードいずれでも同じ固定の bg/text ペアです（テーマ自身のブランド
アクセントに紐づかない自己完結型のバッジ）。そのため再宣言すべき
`[data-theme="dark"]` オーバーライドはありません:

```css title="docs/assets/brand.css" linenums="1"
:root {
	--bxsites-step-marker-bg: #7C3AED;
	--bxsites-step-marker-text: #fff;
	--bxsites-step-success-bg: #059669;
	--bxsites-step-success-text: #fff;
}

[data-theme="dark"] {
	--bxsites-step-marker-bg: #C4B5FD;
	--bxsites-step-marker-text: #1b1f21;
}
```

## ホームページのヒーローバナー

すべての組み込みテーマは、見出し画像と CTA ボタンを持つ全幅のホームページバナー用の
CSS を同梱しています - このサイト自身の `docs/index.md` もこれを使用しています。
専用のディレクティブブロックや設定はなく、どのページでも入れられるプレーンな HTML
です（ホームページも普通のページであり、`order: 1` か、それ以外の形で nav の
先頭にあるだけです）:

```markdown title="docs/index.md"
<div class="bxsites-hero">
	<img class="bxsites-hero__banner" src="assets/home-banner.jpg" alt="...">
	<div class="bxsites-hero__actions">
		<a class="bxsites-hero__btn bxsites-hero__btn--primary" href="getting-started.md">Get Started</a>
		<a class="bxsites-hero__btn bxsites-hero__btn--secondary" href="https://github.com/your/repo">View on GitHub</a>
	</div>
</div>
```

`bxsites-hero__btn--primary`/`--secondary` は、すでにどのテーマも他の場所で使っている
のと同じ2つのアクセントスタイルです - ボタンは自由に入れ替え、削除、追加できます。
`bxsites-hero__banner` 自身の画像も、他のどの画像でも解決されるのと同じ方法で、
`docs/assets/` 相対の `src` を通じてリサイズ/差し替えできます。

## テーマのオーバーライド

自分の `layout.bxm` + `page.bxm`（オプションで `search.bxm`/`assets/`）を、
プロジェクトルートの `theme/` フォルダに配置します。BxSites は、下記の契約を
満たしている限り、インストール済みの `themes/<name>/` テーマとどの組み込みテーマ
よりも、プロジェクトレベルの `theme/` オーバーライドを優先します -
このモジュール自身の `resources/themes/` にある組み込みテーマは、コピーして
調整するための良い出発点になります。完全な解決順序: `theme/`（このセクション）
→ `themes/theme.name/`（`theme.name` が一致する場合の
[インストール済みテーマ](#公開済みテーマのインストール)）→ `theme.name` という
名前の組み込みテーマ。

実例を見てみましょう - `bootstrap` から始めて、ブランドパレットと見出しフォントを
独自のものに差し替え、それ以外のすべて（ナビ、検索、ダークモード、コードハイライト
など）はすでに動作している通りに保ちます:

```text title="Project structure"
my-project/
├── bxsites.yaml
├── docs/
└── theme/                    ← プロジェクトレベルのオーバーライド。どの組み込みテーマより先にチェックされます
    ├── layout.bxm             ← resources/themes/bootstrap/layout.bxm からコピー
    ├── page.bxm                ← resources/themes/bootstrap/page.bxm から無変更でコピー
    ├── search.bxm               ← 無変更でコピー
    └── assets/
        └── style.css              ← bootstrap の assets/style.css からコピーし、その後編集
```

1. このモジュールの `resources/themes/bootstrap/` から、3つの `.bxm` ファイルと
   `assets/style.css` をプロジェクトの `theme/` にコピーします。
2. 変更が必要な部分だけを編集します。ブランドパレットとフォントを差し替えるだけなら、
   `theme/assets/style.css` の先頭部分だけです:

   ```css title="theme/assets/style.css" linenums="1"
   :root {
   	--bxsites-gradient-start: #7C3AED;  /* was #00FF78 */
   	--bxsites-gradient-end: #DB2777;    /* was #00DBFF */
   	--bxsites-accent: #FBBF24;          /* was #FFF500 */
   }

   body {
   	font-family: "Inter", system-ui, sans-serif;  /* was "Poppins" */
   }
   ```

3. `bxSites build`（反復作業中は `serve`）を実行します - BxSites は `theme/` を
   自動的に検出するため、`bxsites.yaml` の変更は不要です（プロジェクトレベルの
   `theme/` フォルダは常に `theme.name` で指定された組み込みテーマより優先されます）。
   触れなかった部分 - ナビのレンダリング、検索、ダークモードトグル、コード
   アノテーション - は、元の `bootstrap` テーマとまったく同じように動作し続けます。
   その裏側にあるのは、まったく同じ `layout.bxm`/`page.bxm` のマークアップだからです。

ただし、プロジェクトの `theme/` フォルダはオール・オア・ナッシングです - BxSites が
1つでも見つけると、組み込みテーマの代わりに完全にそちらが使われます。そのため、
変更したのが `assets/style.css` だけであっても、独自の `layout.bxm` + `page.bxm` は
必要です（どちらかが欠けているフォルダは、黙ってフォールバックするのではなく
`BxSites.InvalidTheme` で即座に失敗します）。CSS だけの/`.bxm` を伴わない調整には、
代わりに上記の [`extraCss`](#テーマオーバーライドなしの色のカスタマイズ) を
使用してください - `bxsites.yaml` が指すどのテーマの上にも重なり、`theme/` フォルダは
一切関与しません。`theme/` は、マークアップ自体も変更する必要がある場合のためのもので、
次のセクションで扱います。

## ゼロからテーマを書く

テーマに必要なのは2つの必須ファイルだけなので、ここでは本当に最小限のもの -
Bootstrap も Tailwind も、ダークモードも検索 UI もない - を示し、組み込みテーマが
その上に追加しているものと、本当に必須なものとを正確に示します。両方を
プロジェクトの `theme/layout.bxm` と `theme/page.bxm` として保存してください -
プロジェクトレベルの `theme/` フォルダは（上記と同様に）自動的に検出され、
`bxsites.yaml` の変更は不要です:

```bx title="theme/layout.bxm" linenums="1"
<!-- theme/layout.bxm -->
<bx:script>
	function renderNav( required array nodes ) {
		var html = "<ul>"
		for ( var node in arguments.nodes ) {
			html &= "<li>"
			html &= len( node.url )
				? '<a href="' & variables.basePath & node.url & '">' & encodeForHTML( node.title ) & '</a>'
				: encodeForHTML( node.title )
			if ( node.children.len() ) {
				html &= renderNav( node.children )
			}
			html &= "</li>"
		}
		return html & "</ul>"
	}
</bx:script>
<bx:output>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>#encodeForHTML( variables.page.title )# - #encodeForHTML( variables.siteConfig.name )#</title>
	<link rel="stylesheet" href="#variables.basePath#assets/theme/style.css">
</head>
<body>
	<header><a href="#variables.basePath#">#encodeForHTML( variables.siteConfig.name )#</a></header>
	<nav>#renderNav( variables.nav )#</nav>
	<main>
</bx:output>
<bx:include template="#variables.themeDir#/page.bxm">
<bx:output>
	</main>
</body>
</html>
</bx:output>
```

```bx title="theme/page.bxm" linenums="1"
<!-- theme/page.bxm -->
<bx:output>
<article>
	<h1>#encodeForHTML( variables.page.title )#</h1>
	#variables.page.contentHtml#
</article>
</bx:output>
```

これで完全に動作するテーマの完成です - `variables.page.contentHtml` はすでに
変換済みの Markdown（シンタックスハイライト、Admonition、タブ、数式などすべて
込み）なので、パースすべきものは何も残っておらず、レイアウトするだけです。
ここから、組み込みテーマが持っていて実際に欲しいものを何でも追加してください:
`search.bxm`（`bxsites.yaml` の `search` が `true` の場合のみインクルードされます -
[検索](search.md) を参照）、ダークモードトグル
（`resources/themes/bootstrap/layout.bxm` の `<body>` タグから `x-data`/`x-init` の
Alpine.js ペアと、対応する `[data-theme="dark"]` の CSS ブロックをコピー）、
パンくずリスト/タグ/前後リンク（どの組み込みテーマの `page.bxm` にもパターンが
示されています - それぞれ小さなレンダリング関数を囲む `if` にすぎず、すべて
`variables.page` にすでに存在するフィールドによって駆動されます）、あるいは
独自の CSS/JS のための `assets/` フォルダ（ビルド時に自動的に `site/assets/theme/`
にコピーされます）。
