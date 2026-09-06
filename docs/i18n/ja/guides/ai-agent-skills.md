---
title: AI エージェントスキル
order: 6.3
icon: phosphor-duotone:robot
summary: Claude Code、Cursor、Codex などの AI コーディングアシスタントに bx-sites の詳細で構造化された知識を与えます - 公式スキルパックを npx、ColdBox CLI、または bxSites 独自の skills:install 動詞経由でインストールします。
tags: [ガイド, ai, スキル]
---

# AI エージェントスキル

**[Agent Skill](https://code.claude.com/docs/en/skills)** とは、AI
コーディングアシスタントに特定の一つのことをうまくこなす方法を教える、小さく自己完結した
`SKILL.md` ファイルです - アシスタントはタスクがそのスキルの説明に一致するたびに、必要に応じて自動的に
読み込みます。会話のたびに bx-sites 独自の規約（`::: card :::` ブロックの動き方、
`bxsites.yaml` の `redirects` キーが期待するもの、`page:new` が手書きファイルとどう違うか）を
アシスタントに説明し直す代わりに、スキルはその知識を、bx-sites 自体が動く通りに書いて、
あらかじめ渡しておきます。

[`ortus-boxlang/bx-sites-skills`](https://github.com/ortus-boxlang/bx-sites-skills)
はこのプロジェクトの公式スキルパックです - 新規プロジェクトのスキャフォールディングから
このリポジトリ自身の GitHub Actions のトラブルシューティングまでをカバーする13個のスキルです。
Agent Skills 形式をサポートするあらゆるアシスタント（Claude Code、Cursor、Codex など）で動作します。

## インストール

パックをインストールする方法は3通りあります - ワークフローに合うものを選んでください:

### `npx skills add`

[`skills` CLI](https://github.com/skillslib/skills) は言語やランタイムを問わずどんなプロジェクトでも
動作し、Node.js 以外は何も必要としません:

```bash title="すべてのスキルをインストール"
npx skills add ortus-boxlang/bx-sites-skills
```

```bash title="非対話型（CI、スクリプト）"
npx -y skills add ortus-boxlang/bx-sites-skills -y
```

パック全体ではなく単一のスキルだけをインストールするには、それを直接指定します:

```bash title="スキルを1つだけインストール"
npx skills add ortus-boxlang/bx-sites-skills/skills/bx-sites-deployment
```

### `coldbox ai skills install`

すでに ColdBox CLI をお持ちであれば、同じ GitHub ソースから直接インストールできます -
[BoxLang Skills Directory](https://skills.boxlang.io/) を参照してください:

```bash title="スキルを1つだけインストール"
coldbox ai skills install ortus-boxlang/bx-sites-skills/bx-sites-deployment
```

### `bxSites skills:install`

bx-sites には独自のワンショット動詞も用意されています - `npx skills add` の薄いラッパーで、
パック全体を現在のプロジェクトに直接インストールするので、新しくスキャフォールドしたプロジェクトの
AI アシスタントは最初のプロンプトから bx-sites を理解しています:

```bash title="使い方"
bxSites skills install
# または、同等に:
bxSites skills:install
```

```bash title="スキルを1つだけインストール"
bxSites skills:install --skill=bx-sites-deployment
```

Node.js/`npx` が `PATH` にある必要があります（`npx skills add` 自体が持つ要件と同じです） -
フラグの完全なリファレンスは [CLI リファレンス](../cli-reference.md#skillsinstall) を参照してください。

## 利用可能なスキル

各スキルは単一の自己完結した `SKILL.md`（バンドルされたリソースファイルなし）なので、
上記のどの経路でも正しくインストールされます:

| スキル | 説明 |
|---|---|
| `bx-sites-getting-started` | bx-sites のインストール、新規プロジェクトのスキャフォールディング（または既存の GitBook/mkdocs/Notion プロジェクトの移行）、プロジェクト構成、ページ frontmatter、リンク、build/serve/clean。 |
| `bx-sites-content-blocks` | リッチな `::: name :::` コンテンツブロック - カード、カラム、ステッパー、ボタン、埋め込み、ページリンク/リンクプレビュー、プロンプト、更新履歴、インクルード、条件付きコンテンツ、OpenAPI ウィジェット。 |
| `bx-sites-markdown` | アドモニション、脚注、定義リスト、コンテンツタブ、コードブロック注釈、Mermaid、数式、テーブル、アイコン、レスポンシブ画像、Alpine.js によるインタラクティビティ。 |
| `bx-sites-variables-functions` | 再利用可能な `{{ variables }}` と BoxLang マジック関数（`docs/functions.bxs`）、ステータスバッジ/評価/プログレスバーのビジュアライザーレシピを含む。 |
| `bx-sites-blog-versioning-i18n` | ブログ（`docs/blog/posts/`）、バージョン管理されたドキュメント（`docs/versions/`）、翻訳されたロケール（`docs/i18n/`）、リダイレクト。 |
| `bx-sites-content-quality` | フルビルドなしでのビルド前コンテンツチェック - `lint`、`blog:drafts`、`blog:find`、`search:query`。 |
| `bx-sites-build` | `build`/`serve`/`clean`/`search-index`、およびビルド済みサイトに対する `doctor`/`stats`/`check` 診断。 |
| `bx-sites-configuration` | `bxsites.yaml`/`bxsites.json` のキー完全リファレンス - `baseURL`、`nav`、`redirects`、`markdown`、アセットなど。 |
| `bx-sites-themes` | テーマの選択、カスタマイズ、上書き、インストール、または作成。`ThemeProvider` コントラクト。 |
| `bx-sites-search` | 検索プロバイダー - ローカル（MiniSearch）、Algolia DocSearch、Pagefind、カスタムプロバイダーの組み込み方。 |
| `bx-sites-plugins` | bx-sites プラグイン（ビルドライフサイクルフック)や CLI プロバイダー（新しい `bxSites` 動詞）の作成/インストール。 |
| `bx-sites-deployment` | デプロイ先（S3/Azure/GCS/Firebase/FTP/SFTP/rsync/Netlify/Vercel/Cloudflare Pages/GitHub Pages)、`package`、GitHub Actions 公開ワークフロー。 |
| `bx-sites-actions` | bx-sites 独自の GitHub Actions ワークフロー（tests、snapshot、release、docs、pages）の運用とトラブルシューティング。 |

## プロンプト例

インストール後は、欲しいものを説明するだけです - アシスタントが自動的に一致するスキルを選びます:

```text title="新しいサイトをスキャフォールド"
Scaffold a new bx-sites project called "acme-docs" using the gitbook
theme, add a Getting Started page, and start the dev server.
```

```text title="コンテンツを執筆"
Add a three-step ::: stepper ::: block to docs/getting-started.md walking
through install, scaffold, and build - and a ::: cards ::: grid linking
to the three main guides.
```

```text title="公開する"
Add a Netlify deploy target to bxsites.yaml and explain which
environment variable it expects for the auth token.
```

## よくある質問

??? faq "SKILL.md ファイルには実際に何が入っていますか？"
    短い YAML frontmatter ブロック（`name`、`description` - アシスタントが照合するトリガー）に続き、
    そのトピックについての通常の Markdown の指示、規約、例が書かれています。バンドルされたスクリプトや
    リソースファイルはありません - このパックの各スキルは意図的に単一ファイルになっているため、
    `npx skills add`、`coldbox ai skills install`、`bxSites skills:install` のいずれでも同じように
    インストールされます。

??? faq "13個すべてのスキルが必要ですか？"
    いいえ - どのインストール経路でも名前を指定して1つのスキルだけをインストールできます
    （上記の[インストール](#インストール)を参照）。とはいえ、ほとんどのプロジェクトではパック全体を
    インストールしても問題ありません。アシスタントはタスクが実際に一致したときにしかスキルの内容を
    読み込まないため、使わないスキルはプロンプト時に何のコストもかかりません。

??? faq "どの AI アシスタントで動作しますか？"
    Agent Skills 形式をサポートするあらゆるアシスタント - Claude Code、Cursor、Codex などです。
    `npx skills add`/`coldbox ai skills install` は、プロジェクトにすでに設定されているアシスタントを
    検出し、それぞれに自動的にインストールします。

??? faq "スキルが実際にインストールされたか確認するには？"
    アシスタント自身のスキルディレクトリに新しい `SKILL.md` があるか確認してください
    （Claude Code の場合は例えば `.claude/skills/bx-sites-getting-started/SKILL.md`）
    - あるいは、単にそのスキルがカバーすること（例えば「Netlify のデプロイ先を追加するには？」）を
    アシスタントに尋ねて、回答がこのドキュメントと一致するか確認してください。

??? faq "`bxSites skills:install` が npx/Node.js に関するエラーで失敗しました"
    内部では実際の `npx skills add` を呼び出しているため、そのコマンド自体と同様に `PATH` に
    Node.js が必要です - [nodejs.org](https://nodejs.org/) から Node.js をインストールして
    再試行してください。あるいは `bxSites` 独自のラッパーを完全にスキップして、
    `npx skills add ortus-boxlang/bx-sites-skills` を直接実行してください。

??? faq "まだ bx-sites プロジェクトではないプロジェクトにこれらをインストールできますか？"
    はい - `npx skills add`/`coldbox ai skills install` はどんなディレクトリでも動作します。
    `bxSites skills:install` は他のあらゆる `bxSites` 動詞と同様に、既存の `--projectRoot`
    （デフォルトでは現在のディレクトリ）を特に必要としますが、そのプロジェクトはまだビルドされている
    必要はありません - 1ページも書かないうちにスキルをインストールするのはまさに狙い通りで、
    アシスタントが最初のプロンプトからすでに bx-sites を理解しているようにするためです。

## ソース

- スキルリポジトリ: [ortus-boxlang/bx-sites-skills](https://github.com/ortus-boxlang/bx-sites-skills)
- bx-sites リポジトリ: [ortus-boxlang/bx-sites](https://github.com/ortus-boxlang/bx-sites)
- BoxLang Skills Directory: [skills.boxlang.io](https://skills.boxlang.io/)
