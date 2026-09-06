---
title: はじめに
order: 2
icon: phosphor-duotone:rocket-launch
summary: モジュールをインストールし、プロジェクトをスキャフォールドして、最初のサイトをビルドします。
tags: [ガイド, セットアップ]
---

# はじめに

## 前提条件: BoxLang のインストール

以下はすべて、BoxLang ランタイム自体がすでにマシンにインストールされて
いることを前提としています - `install-bx-module` は既存のインストール
にモジュールをインストールする BoxLang 自身の CLI コマンドであり、
CommandBox の `box install` も実行対象となる BoxLang エンジンがすでに
存在している必要があります。まだ BoxLang をインストールしていない場合は、
以下のいずれかの方法で先にインストールしてください:

- **クイックインストーラー**（単一バージョン、最も手軽に始められます）:

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install.boxlang.io/ | bash
  ```

- または **BVM**（BoxLang Version Manager。複数の BoxLang バージョンを
  並行してインストールし、切り替えられるようにします）:

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install-bvm.boxlang.io/ | bash
  bvm install latest && bvm use latest
  ```

Windows 版と Homebrew 版のインストーラー、および BVM コマンドの完全な
リファレンスは、
[BoxLang 公式のインストールドキュメント](https://boxlang.ortusbooks.com/getting-started/installation)
に掲載されています。

## インストール

BxSites は、Markdown レンダリングに [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)、
HTML エンコードに [bx-esapi](https://github.com/ortus-boxlang/bx-esapi)、
`bxsites.yaml` の読み込みに [bx-yaml](https://github.com/ortus-boxlang/bx-yaml)、
レスポンシブ画像パイプライン（[レスポンシブ画像](guides/images.md) を参照）に
[bx-image](https://github.com/ortus-boxlang/bx-image) を必要とします -
これら4つはすべて `box.json` の依存関係として自動的にインストールされるため、
`bx-sites` 自体をインストールするコマンドだけで済みます。BoxLang 独自の OS
バイナリインストーラーを使う場合:

```bash frame="terminal" title="Terminal"
install-bx-module bx-sites
```

または [CommandBox](https://commandbox.ortusbooks.com/) を使う場合:

```bash frame="terminal" title="Terminal"
box install bx-sites
```

どちらの方法でも `box.json` の `boxlang.executable` を参照し、`PATH` 上
（`~/.boxlang/bin`）に `bxSites` スクリプトを配置します。そのため、以下のコマンドは
いずれも短い単独コマンドとして実行できます:

```bash title="Usage"
bxSites <verb> [options]
```

または、BoxLang は使えるがその `PATH` シムが設定されていない環境（CI ランナー、
インストールではなく手動で登録したモジュールなど）でも - どちらの形式もまったく
同じことを実行します:

```bash title="Usage (no PATH shim)"
boxlang bxSites <verb> [options]
```

このガイドの以降では短縮形を使用します。

## プロジェクトのスキャフォールド

```bash frame="terminal" title="Terminal" linenums="1"
bxSites new my-docs
cd my-docs
```

以下の構造が作成されます:

```text title="Project structure"
my-docs/
├── docs/
│   ├── assets/
│   └── index.md
└── bxsites.yaml
```

`--theme=material` や `--theme=tailwind` で別のデフォルトテーマを指定してスキャフォールドでき、
`--name="My Project Docs"` を渡せば最初からサイト名を設定できます -
省略した場合、`new` はターゲットディレクトリ名からサイト名を導出します。

### 設定ファイルの形式

`bxsites.yaml` はデフォルトかつ推奨の形式です - 特に指定がない限り `new` はこの形式を
スキャフォールドし、このガイドと [設定](configuration.md) の例もすべてまずこの形式で
示されます。`bxsites.json` も完全にサポートされており、そちらを好むプロジェクトでは
`--format=json` を渡してスキャフォールドするか、自分で手書き/リネームするだけで構いません。
ConfigLoader は `bxsites.yaml`/`.yml`/`.json` のうち実際に存在するものをこの順序で解決するため、
切り替えに他の設定は一切必要ありません。両方の形式の完全なキーリファレンスは
[設定](configuration.md) を参照してください。

GitBook のコンテンツがある場合は、`bxSites migrate --source=/path/to/export` で
GitBook エクスポートをそのまま `docs/` に変換できます - 詳しくは
[GitBook からの移行](guides/migrating-from-gitbook.md) を参照してください。
変換が済んだら [ビルド](#ビルド) まで読み飛ばしてもかまいません。

## ページの追加

`docs/` 配下のすべての `.md` ファイルがページになります。フォルダのネストがそのままナビゲーションのネストになります:

!!! note "docs/ または src/"
    `docs/` は `new` が生成し、このガイドのすべての例で使われているものですが、
    「docs」というより一般的なサイト（マーケティングサイトやポートフォリオなど）
    には、代わりに `src/` を使うこともできます - 他に何も変更する必要はありません。
    すべてのバーブ（`build`、`serve`、`audit`、`lint`、`page:new` など）が
    まず `docs/` を探し、実際に存在するのが `src/` であればそちらにフォールバック
    します。ビルド出力はどちらの場合でも常に `site/` に置かれます - `site/` 自体が
    ソースフォルダ名として有効になることは決してないため、両者が衝突することは
    ありません。

```text title="docs/ → nav"
docs/
├── index.md              -> /
├── guides/
│   ├── index.md          -> /guides/
│   └── deployment.md     -> /guides/deployment/
```

（大規模なサイトでは、明示的な nav を使ってこの推定順序やグループを完全に上書きできます。
[`nav`](configuration.md#nav) を参照してください。）

### ページ間のリンク

別のページへのリンクは mkdocs と同様に、相手の `.md` ソースへのファイル相対パスを使います -
まるでディスク上で2つのファイルが隣り合っているかのように（実際そうなので）:

```markdown title="Example link"
[デプロイ](guides/deployment.md) を参照するか、そのガイドから
[はじめにに戻る](../getting-started.md#ページの追加) ことができます。
```

BxSites はビルド時にすべてのリンクをビルド後のキレイな URL に書き換えます
（`guides/deployment.md` → `/guides/deployment/index.html`、アンカーとクエリ文字列を保持）。
これは *リンク元* ページ自身のフォルダを基準に解決されます - `../` や兄弟参照は、
他の相対パスを解決するのとまったく同じように機能します。これは、ビルドされたサイトではなく
GitHub 上で直接ファイルを読んだ場合でもリンクが機能し続ける理由でもあります -
どちらの場合でも、実在するファイルへの本物の有効な相対パスだからです。
絶対 URL、`mailto:`、すでに `/` で始まるリンクはそのまま保持されます。

### Markdown としてページをダウンロード

ビルドされた各ページには、元の `.md` ソースも一緒に公開されます
（`docs/guides/deployment.md` は `site/guides/deployment.md` としてコピーされ、
`site/guides/deployment/index.html` の隣に置かれます）- ページ自体には
「Edit this page」の隣に「Markdown をダウンロード」リンクが表示されます。
設定不要で常に有効です。

これは [`llms.txt`](configuration.md#llmstxt) と同じ動機によるものです -
人間（あるいは LLM）が、レンダリング済みの HTML をスクレイピングする代わりに、
ページの生の Markdown を直接取得できます。また `docs/` ツリー全体が 1:1 でミラーされるため、
この方法で読んでもページ自身の相対リンクは機能し続けます。

各ページは小さなフロントマターブロックから始めることができます:

```markdown title="docs/guides/deployment.md" linenums="1"
---
title: デプロイ
order: 2
hidden: false
description: ビルドした BxSites サイトのデプロイ方法。
tags: [ガイド, デプロイ]
icon: 🚀
summary: サイトを公開するために必要なすべてのこと。
ogImage: assets/deployment-card.png
toc: true
---

# デプロイ

コンテンツをここに。
```

- `title` - ナビゲーションやページタイトルを上書きします（省略時はファイル名から導出）
- `order` - ナビゲーション内の兄弟ページの並び順を制御します（小さい値が先、省略されたページはアルファベット順で最後になります）
- `hidden` - `true` にするとナビゲーションと検索から除外されますが、ビルドからは除外されません
- `description` - このページのソーシャルカード/メタ説明（[`ogImage`](configuration.md#ogimage) を参照）。省略時はサイト設定の全体 `description` にフォールバック
- `tags` - このページのタグ配列。タイトル下にクリック可能なバッジとして表示され、
  サイト全体の `/tags/` インデックスページに収集されます（少なくとも1ページに
  タグが付くまではこのページ自体ビルドされません）。一致するクエリに対する
  検索の関連度も高めます
- `icon` - ページタイトルとナビゲーションエントリの横に表示されます - 単純な絵文字、
  またはバンドルされたライブラリの名前付きアイコン（`rocket`、`lucide:rocket`、
  `tabler:rocket`、プロジェクト独自の `custom:my-icon`）を指定できます。
  [アイコン](guides/icons.md) を参照してください
- `summary` - タイトルの下に表示される1行のリードイン（ページ自体には
  レンダリングされないメタタグ専用の `description` とは別物です）
- `ogImage` - このページのソーシャルカード画像を上書きします -
  [`ogImage`](configuration.md#ogimage) を参照してください
- `toc` - `false` にすると、見出しが2つ以上あっても（通常はこれが表示のトリガーです）
  このページ自身の「このページの内容」目次を非表示にします - フローティング TOC を
  自分のコンテンツと競合させたくないランディング/ヒーローページに便利です。
  デフォルトは `true` です

フロントマターの値には、インラインリスト（`tags: [a, b, c]`）、YAML 形式のブロック
リスト（`tags:` の後にインデントされた `- item` 行を続ける）、複数行値のための `>`/`|`
ブロックスカラーを使用できます - ただしこれは小さな自作パーサーであり完全な YAML
ではないため、ネストしたオブジェクト/マップはサポートされません。

## ビルド

```bash frame="terminal" title="Terminal"
bxSites build
```

`docs/` のすべてのページを `site/` の静的サイトとしてレンダリングします。静的ファイルを配信できる場所であればどこにでもホストできます。

## ローカルで配信

```bash frame="terminal" title="Terminal"
bxSites serve
```

プロジェクトをビルドし、`http://127.0.0.1:8080/` で `site/` を配信します。
`docs/`、`bxsites.yaml`/`.json` のサイト設定、またはプロジェクトレベルの `theme/` オーバーライドを保存するたびに自動的に再ビルドされ、ブラウザも自動的にリロードされます。
バインドの変更には `--port=3000` や `--host=0.0.0.0` を使用します。

## クリーン

```bash frame="terminal" title="Terminal"
bxSites clean
```

`docs/` ソースを変更せずに `site/` とビルドキャッシュを削除します。
