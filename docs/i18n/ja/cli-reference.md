---
title: CLI リファレンス
order: 3
icon: phosphor-duotone:terminal-window
summary: bxSites のすべての動詞とフラグ。
tags: [リファレンス, cli]
---

# CLI リファレンス

```bash title="Usage"
bxSites <verb> [options]
```

`box install bx-sites` を実行すると、スタンドアロンの `bxSites` スクリプトが `PATH` に配置されます
（`box.json` の `boxlang.executable` 経由）。そのため以下の各動詞は、その短い形式でも、
`boxlang bxSites <verb>` としても実行できます - どちらも全く同じことを実行します。
`PATH` シムが設定されていない環境（CI ランナー、手動登録のモジュールなど）では長い形式を使用してください:

```bash title="Usage (no PATH shim)"
boxlang bxSites <verb> [options]
```

すべての動詞は `--projectRoot=<path>`（または先頭の位置引数）で、カレントディレクトリ以外の
プロジェクトを対象にできます。以下の2つのグローバルフラグはどの動詞の前にも指定できます。

以下で言及する `docs/` はすべて、代わりに `src/` を使うプロジェクトにも同様に適用されます -
`docs/`-か-`src/` かの慣習については [はじめに](getting-started.md#ページの追加) を参照してください。
`new` は常に `docs/` をスキャフォールドします。

## グローバルオプション

| フラグ | 説明 |
|---|---|
| `-h`, `--help` | 使用方法を表示して終了 |
| `-v`, `--version` | モジュールバージョンを表示して終了 |

## `new`

docs プロジェクトをスキャフォールドします。

```bash title="Usage"
bxSites new [path] [--name=...] [--theme=<全10種は guides/themes.md を参照>] [--description=...] [--format=yaml|json]
```

- `--name` - サイト設定に書き込まれるサイト名（デフォルトはターゲットディレクトリ名）
- `--theme` - デフォルトは `bootstrap`
- `--description` - サイト設定に書き込まれるサイトの説明
- `--format` - `yaml`（デフォルト、`bxsites.yaml` をスキャフォールド）または `json`（`bxsites.json` をスキャフォールド）- [設定](configuration.md) を参照

## `build`

`docs/**.md` を `site/` の静的サイトとしてレンダリングします。検索インデックスのビルドも
行います（サイト設定で `search` が `false` の場合、または `searchProvider` が
`algolia`/`pagefind` のようにインデックスを使わないプロバイダに設定されている場合を除く -
[検索](guides/search.md) を参照）。`searchProvider.provider` が `"pagefind"` の場合は、
完成した `site/` に対して `pagefind` CLI を実行し、テーマ + `docs/assets/**` を `site/` に
コピーします。

```bash frame="terminal" title="Terminal"
bxSites build
```

## `serve`

サイトをビルドし、ライブリロード付きでローカルで配信します。

```bash title="Usage"
bxSites serve [--port=8080] [--host=127.0.0.1]
```

Ctrl+C で中断するまでフォアグラウンドで実行されます。

## `search-index`

ページの再レンダリングやアセットのコピーを行わずに、`site/search-index.json` を単独で再ビルドします。
`build` はこの同じステップを自動的に実行します - この動詞は、インデックスだけを更新したい
場合のために存在します。`docs/versions/`/`docs/i18n/` を持つプロジェクトでも、対象になるのは
常にメインの `docs/` ツリーだけです - 実際の `build` は代わりに各ツリー専用のスコープ付き
インデックスを書き出します（[バージョニング](guides/versioning.md#whats-out-of-scope-for-now) を参照）。

```bash frame="terminal" title="Terminal"
bxSites search-index
```

## `clean`

`docs/` とサイト設定はそのままで、`site/` とビルドキャッシュを削除します。

```bash frame="terminal" title="Terminal"
bxSites clean
```

## `gh-deploy`

サイトをビルドし、`gh-pages` スタイルのブランチに強制プッシュします。
デプロイごとに1コミット、そのブランチに蓄積された履歴はなく、mkdocs 独自の
`mkdocs gh-deploy` の慣習に合わせています。プロジェクトがリモートを設定した
git リポジトリである必要があります。現在のブランチや作業ツリーには一切触れません
（プッシュは使い捨ての `git worktree` から行います）。

```bash title="Usage"
bxSites gh-deploy [--branch=gh-pages] [--remote=origin] [--message="..."]
```

- `--branch` - デフォルトは `gh-pages`
- `--remote` - デフォルトは `origin`
- `--message` - ブランチの単一コミットメッセージ。デフォルトは `"Deploy site via bxSites gh-deploy"`

完全な GitHub Pages の設定（Pages のブランチ有効化、`baseURL` など）については
[デプロイ](guides/deployment.md) を参照してください。

## `deploy`

サイトをビルドし、実際のデプロイターゲットへ配信します - S3（および
DigitalOcean Spaces、Cloudflare R2、Backblaze B2、MinIO などの S3 互換サービス）、
Azure Blob Storage、Google Cloud Storage、Firebase Hosting、FTP、SFTP、
rsync-over-SSH、Netlify、Vercel、Cloudflare Pages、ローカルディレクトリ、
または GitHub Pages（`gh-deploy` と同じプッシュに、この統合コマンドからも
到達できます）。

```bash title="Usage"
bxSites deploy --entry=<name> [--verbose]
bxSites deploy [--target=local|github-pages] [target-specific flags] [--verbose]
bxSites deploy [--verbose]
```

呼び出し方は3通りあります:

1. **`--entry=<name>`** - `deployments/<name>.json` ファイルが宣言している
   ターゲットへディスパッチします（詳細は後述）。`local`/`github-pages` 以外の
   すべてのターゲットはこちらが必要です - いくつかのフラグだけでは持ちきれない
   量の設定があるためです。
2. **`--target=<name>` と専用フラグ** - 最も単純な2つのターゲット向けの、
   `deployments/` フォルダを一切必要としないフラグのみのショートハンドです:
   `local`（`--destination=<path>`）と `github-pages`（`[--branch]
   [--remote] [--message]`、すべて省略可能で `gh-deploy` と同じデフォルト値）。
3. **どちらのフラグも指定しない場合 - すべてにデプロイ。** `deployments/*.json`
   の各エントリーが、単一の共有ビルド（サイトはターゲットごとではなく一度だけ
   ビルドされます）を使って順番にデプロイされます。少なくとも1つの
   `deployments/*.json` エントリーが存在している必要があります。1つの
   ターゲットが失敗しても他は止まりません - すべてのエントリーが試行され、
   コマンドは少なくとも1つが失敗した場合にのみ非ゼロで終了します。最終的な
   サマリーには成功した数が報告されます（例: `Deployed to 2/3 target(s) (1
   failed)`）。

`--verbose` を付けると、最終的な1行サマリーだけでなく、ビルドの開始/終了時と
各ターゲットの開始/終了時にも進捗行が出力されます。

各ターゲットの設定内容と、それぞれの `deployments/*.json` の実例については
[デプロイ](guides/deployment.md#deploy) を参照してください。

## `package`

サイトをビルドし、単一の配布可能なアーカイブへ圧縮します - ルートがビルド
されたサイト自身の内容になっている（`site/` フォルダで包まれていない）単純な
zip ファイルで、リリースへの添付や、zip アップロードのみを受け付けるホストへの
受け渡しにすぐ使えます。

```bash title="Usage"
bxSites package [--output=<path>]
```

`--output` のデフォルトは `<projectRoot>/site.zip` です（相対パスを指定した
場合はプロジェクトルートを基準に解決されます）。ネストした出力先を指定した
場合、親ディレクトリは自動的に作成されます。

## `migrate`

既存の docs プロジェクトをこの形式に変換します - `--from` で変換元の形式を指定します:
`gitbook`（デフォルト）、`mkdocs`、`markdown-zip`、または `notion` です。

```bash frame="terminal" title="Terminal" linenums="1"
bxSites migrate --source=/path/to/gitbook-export
bxSites migrate --source=/path/to/mkdocs-project --from=mkdocs
bxSites migrate --source=/path/to/export.zip --from=markdown-zip
bxSites migrate --source=/path/to/notion-export --from=notion
```

- `--source`（必須）- エクスポート/プロジェクトのルートディレクトリへのパス（`gitbook` の場合は `SUMMARY.md`、`mkdocs` の場合は `mkdocs.yml` を含む必要があります）、または `.zip` ファイル（`markdown-zip`；`notion` は `.zip` または展開済みのフォルダのどちらも受け付けます）
- `--from` - `gitbook`（デフォルト）、`mkdocs`、`markdown-zip`、または `notion`

### `--from=gitbook`（デフォルト）

GitBook のエクスポート（`SUMMARY.md` の目次とその `.md` ファイル、GitBook 独自のオンディスク
同期形式）をこのプロジェクトの `docs/` ツリーに変換します。`SUMMARY.md` は `docs/nav.json` に、
`{% block %}` 構文は bx-sites の同等物（`::: name` ディレクティブ、または既により近い一致が
ある場合は `=== "Title"` タブ / `!!! type` admonition 構文 - [コンテンツブロック](guides/content-blocks.md)
を参照）になります。`README.md` ファイルは `index.md` になり、`.gitbook/assets/**` は
`docs/assets/gitbook/` にコピーされます。

### `--from=mkdocs`

mkdocs プロジェクト（`mkdocs.yml` とその `docs/` フォルダ）を完全な bx-sites プロジェクトに
変換します。`mkdocs.yml` は `bxsites.yaml` + `docs/nav.json` になり、各ページはほぼそのまま
コピーされます。mkdocs-material 独自の admonition/tabs/math/コードアノテーション構文が、
すでに bx-sites 自身のネイティブ構文と *一致している* ためです -
[mkdocs からの移行](guides/migrating-from-mkdocs.md) を参照してください。`.md` 以外の
アセット（画像は通常それを使うページの隣に置かれており、mkdocs には単一のアセットフォルダの
慣習がありません）は `docs/assets/mkdocs/` に再配置され、その参照も書き換えられます。

### `--from=markdown-zip`

Markdown ファイルのプレーンな `.zip` です - フォルダ自身のネストがすでに bx-sites 自身の
ナビゲーション規約 *そのもの* であり、ページ間の相対 `.md` リンクもすでに bx-sites が
期待するとおりに解決されるため、翻訳すべき独自のエクスポート形式は存在しません。
ほぼそのままのコピーです: `.md` 以外のすべてのファイル（画像など）は
`docs/assets/imported/` に再配置され、それを参照している各ページ自身の参照もそれに
合わせて書き換えられます。`bxsites.yaml`/`docs/nav.json` は書き出されません -
プレーンな zip はサイト名やナビゲーション構造を独自に持たないため、変換すべきものが
ないからです。

### `--from=notion`

Notion の「Export as Markdown & CSV」アーカイブ（`.zip`、または展開済みのフォルダ）です -
ここで変換元にできる他のどの形式にもない、Notion 独自の2つの癖を処理します:
すべてのページ/サブページフォルダには、スペースと32文字の id が付加されており
（同名ページを区別するためのもので、人が読むことは想定されていません）、
ページのタイトルはフロントマターではなく、先頭の文字どおりの `# Heading` として
繰り返されています。どちらもクリーンアップされます: id サフィックスは取り除かれ、
残った名前は出力ファイル名としてスラグ化され、先頭の見出しは重複した1行目ではなく
本物の `title` フロントマターフィールドになり、（Notion が URL エンコードして書き出し、
依然として元の id 付きの名前を指している）すべてのリンク/画像のターゲットが
それに合わせて書き換えられます。`.md` 以外のファイルは、上記の `markdown-zip` と
同様に `docs/assets/imported/` に再配置されます。

### 4つすべてに共通

変換されたページ数（mkdocs/markdown-zip/notion の場合はアセット数も）のサマリと、
自動変換できなかったものがあればその詳細リストを出力します - 何も黙って破棄される
ことはありません。すでに存在する変換先ファイル、`bxsites.yaml`、または
`docs/nav.json` は上書きされます（これも報告されます）。移行済みの出力を
コミットする前に確認してください。

## `site:check`

すでにビルド済みの `site/` に対する CI グレードのコンテンツ品質ゲートです - 先に `build` を
実行してください。以下をチェックします:

- **内部リンク/画像切れ** - `site/` に存在しないページやアセットを指す `<a href>`/`<img src>`。
  このチェックは失敗として扱われます。
- **alt テキストの欠落** - `alt` 属性が全くない `<img>`。空の `alt=""`（純粋に装飾目的の画像に
  対する正しいマークアップ）はフラグされません。このチェックは失敗として扱われます。
- **孤立ページ** - `site/` には存在するが、どのツリー自身のホームページ（メインサイトの
  `index.html`、および各バージョン/ロケール自身のもの）からリンクをたどっても到達できない
  ページ。情報提供のみで、このチェックが失敗することはありません - プロジェクトが意図的に
  自身の nav から除外したページ（フロントマターの `hidden: true` など）は、直接リンクからしか
  到達できないのが *想定通り* だからです。

```bash frame="terminal" title="Terminal" linenums="1"
bxSites build
bxSites site:check
```

リンク/画像切れや alt 欠落画像が1つでもあれば `1` で終了し、それ以外は `0` で終了します
（孤立ページは終了コードに影響しません）。意図的に内部リンクのみを対象としています - 外部
URL を確認するための HTTP リクエストは行いません。それは専用のリンクチェックツールを
独立したジョブとして実行すべき領域です。

## `stats`

すでにビルド済みの `site/` に対する読み取り専用のサマリレポートです - 先に `build` を
実行してください。以下を報告します:

- **ページ数と単語数** - 総ページ数とおおまかな単語数（タグは除外、ブログ自身の読了時間の
  数字と同じ「見積もりとして十分」という基準）に加え、複数ツリー（バージョンや非デフォルト
  ロケール）が存在する場合はツリーごとの内訳。
- **バージョンとロケール** - すべての `docs/versions/`/非デフォルトの `docs/i18n/` フォルダの名前。
- **ブログ** - 投稿数/カテゴリ数/著者数/アクティブな年数を、`site/blog/` 自身のフォルダ構造から
  直接カウント（下書きは除外されるため、実際に公開されたものと常に一致します）- ブログが
  なければ `none`。
- **タグ** - サイト全体での異なるタグの数。
- **検索インデックス** - `search-index.json` のエントリ数とファイルサイズ、または検索がオフか
  非ローカルプロバイダが有効な場合は `none`。
- **サイト出力** - ビルド済み `site/` の総ファイル数とディスク上のサイズ。

```bash
bxSites build
bxSites stats
```

常に `0` で終了します - 純粋に情報提供であり、ここには合格/不合格のゲートはありません
（それは `site:check` の役割です）。

## `doctor`

環境/設定のヘルスチェックをワンショットで行う動詞です - 「バグ報告の前にまずこれを実行する」
ための動詞です。JVM のバージョン、`docs/` が存在すること、`bxsites.yaml`/`.json` が実際に
パースでき検証できること、必要な BoxLang モジュール（`bx-markdown`、`bx-esapi`、`bx-yaml`、
`bx-image`）がインストールされ有効化されていること、そして - プロジェクトレベルの `theme/`
オーバーライドが存在する場合は - それが `layout.bxm`/`page.bxm` の2ファイル必須の契約を
満たしていることをチェックします。

```bash frame="terminal" title="Terminal"
bxSites doctor
```

いずれかのチェックが失敗すれば `1`、それ以外は `0` で終了します。ここではプロジェクトへの
変更は一切行われません - 純粋に診断のみです。

## `post:new`

`docs/blog/posts/<slug>.md` に新しいブログ投稿をスキャフォールドします。

```bash title="Usage"
bxSites post:new --title="My New Post" [--slug=...] [--date=...] [--authors=...] [--categories=...] [--tags=...] [--draft]
```

- `--title`（必須）- 投稿のフロントマター `title` にもなります
- `--slug` - デフォルトはスラグ化された `--title`
- `--date` - デフォルトは本日（`yyyy-MM-dd`）
- `--authors`、`--categories`、`--tags` - カンマ区切り
- `--draft` - デフォルトは `true`（すぐに公開する場合は `--!draft` を渡します）

完全なフロントマターリファレンスは [ブログ](guides/blog.md) を参照してください。

## `version:new`

現在の `docs/` ツリーを `docs/versions/<name>/` にスナップショットします。`assets/`、
`versions/`、`i18n/`、`blog/` は除外されます（それぞれ個別に読み込まれる独自のツリーであり、
スナップショットの一部ではありません）。

```bash title="Usage"
bxSites version:new --name=1.0
```

- `--name`（必須）- バージョンのフォルダ名/ラベル。例: `1.0`

[設定の「バージョニング」セクション](configuration.md#versioning) を参照してください。

## `i18n:status`

ロケールごとの翻訳カバレッジを報告します - 設定済みの各ロケールについて、デフォルトツリーの
ページのうち何個が（同じ相対パスで）`docs/i18n/<code>/` 配下に存在するか、そしてどれがまだ
不足しているかを示します。

```bash frame="terminal" title="Terminal"
bxSites i18n:status
```

常に `0` で終了します - 純粋に情報提供です。

## `i18n:new`

新しい `docs/i18n/<code>/` ロケールフォルダをスキャフォールドします。デフォルトロケールに
`index.md` が存在する場合は、それをコピーしてシードします。

```bash title="Usage"
bxSites i18n:new --code=es
```

- `--code`（必須）- ロケールコード。例: `es`、`fr`、`pt-BR`

新しいロケールを `bxsites.yaml` の `i18n.locales` に組み込む方法は
[国際化](guides/i18n.md) を参照してください。

## `page:new`

`docs/` 配下の任意のパスに単一の docs ページをスキャフォールドし、リクエストされた
フロントマターをあらかじめ埋め込みます。

```bash title="Usage"
bxSites page:new --path=guides/setup.md [--title=...] [--description=...] [--icon=...] [--tags=...] [--order=...]
```

- `--path`（必須）- `docs/` 相対パス。`.md` で終わる必要があります
- `--title`、`--description`、`--icon`、`--order` - フロントマターに書き込まれます
- `--tags` - カンマ区切り

## `plugin:new`

`examples/hello-plugin/` を模した、プラグインモジュールのスケルトン（`box.json`、
`ModuleConfig.bx`、すべてのフックをスタブ化した `models/BxSitesPlugin.bx`）をスキャフォールドします。

```bash title="Usage"
bxSites plugin:new --name=my-analytics-plugin [--dest=...]
```

- `--name`（必須）- プラグインのモジュール名/スラグ
- `--dest` - デフォルトは `<projectRoot>/<name>`

フックのリファレンスと、完成したプラグインを `bxsites.yaml` の `plugins` 配列に組み込む方法は
[プラグイン](guides/plugins.md) を参照してください。

## `install:plugin`

ForgeBox から公開済みのプラグインをダウンロードし、プロジェクト自身の `boxlang_modules/`
に直接配置します - BoxLang 独自の自動読み込みされるローカルモジュールの慣習であるため、
`bxSites` バイナリ自体以外には何も必要ありません（`box`/CommandBox は関与しません）。

```bash title="Usage"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

- `--name`（必須）- インストールする ForgeBox スラグ
- `--version` - 特定のバージョン。省略時は最新版

読み込まれたモジュールの実際に登録されたマッピング名を出力します - この名前を
`bxsites.yaml` の `plugins` 配列に追加してプラグインを有効化してください
（インストールしただけではプラグインは有効化されません - [プラグイン](guides/plugins.md) を参照）。

## `theme:new`

組み込みテーマの1つをプロジェクト自身の `theme/` フォルダにイジェクトし、カスタマイズできる
ようにします。mkdocs の `--theme` イジェクトワークフローと同様です。

```bash title="Usage"
bxSites theme:new --theme=material
```

- `--theme`（必須）- `bootstrap`、`material`、`tailwind`、`docsy`、`slate`、`docusaurus`、
  `justthedocs`、`vuepress`、`gitbook`、`notion` のいずれか -
  [テーマ](guides/themes.md#built-in) を参照

既存の `theme/` を上書きすることはなく、代わりに失敗します。オーバーライドの契約
（`layout.bxm` + `page.bxm`）については [テーマ](guides/themes.md) を参照してください。

## `install:theme`

ForgeBox から公開済みのテーマをダウンロードし、プロジェクト自身の `themes/<name>/` に
配置します - `install:plugin` と同様、`bxSites` バイナリ以外には何も必要ありません。

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

- `--name`（必須）- インストールする ForgeBox スラグ
- `--version` - 特定のバージョン。省略時は最新版

完了する前に、ダウンロードしたパッケージを `ThemeProvider` 契約（`layout.bxm` + `page.bxm`）
に照らして検証します。そのため壊れたパッケージはインストール時点で失敗し、次の `build` まで
持ち越されることはありません。インストールしたテーマを使用するには `bxsites.yaml` の
`theme.name` にインストール名を設定してください -
[テーマ](guides/themes.md#installing-a-published-theme) を参照。

## `theme:import`

他の静的サイトジェネレーターのエコシステム（`mkdocs`/`jekyll`/`hugo`）のテーマを、
`themes/<name>/` 配下の bx-sites テーマスキャフォールドへとベストエフォートで変換します -
これは出発点であり、ロスレスなワンコマンド移植ではありません。

```bash title="Usage"
bxSites theme:import --source=mkdocs --path=/path/to/theme --name=my-imported-theme
```

- `--source`（必須）- `mkdocs`、`jekyll`、`hugo` のいずれか
- `--path`（必須）- 変換元テーマ自身のルートフォルダ
- `--name`（必須）- 変換先の名前。`themes/<name>/` に書き込まれます

同じ `--name` に対して再実行しても安全です - `layout.bxm`/`page.bxm` は上書きされ、
新しく見つかったアセットフォルダはマージされます。何が変換され何が変換されないか、
実行後に何を確認すべきかについては [テーマのインポート](guides/theme-import.md) を参照してください。

## `page:rename`

docs ページをあるパスから別のパスへ移動し、`docs/**` 全体で旧パスを指していたすべての
相対 Markdown リンクを書き換えます - ビルド済み HTML 側がすでに解決しているのと同じ
ファイル相対リンク切れの問題（`site:check`）を、リネーム時点の生の Markdown ソースに対して
適用したものです。

```bash title="Usage"
bxSites page:rename --from=guides/old-name.md --to=guides/new-name.md
```

- `--from`（必須）- ページの現在の `docs/` 相対パス
- `--to`（必須）- ページの新しい `docs/` 相対パス

書き換えられるのは `[text](relative/path.md)` 形式の裸のリンクのみです - 絶対 URL、
`mailto:`、純粋なページ内アンカーはそのままです。`docs/assets/**` はスキャンされません。

また、移動したページ自身のフロントマターに `redirect_from` として旧 URL を刻印します。
そのため、ビルド時（[リダイレクト](guides/redirects.md)）にはこのプロジェクトがソースを
管理していない外部リンクをリネームのたびに 404 にしてしまうのではなく、引き続き応答できます。

## `blog:drafts`

フロントマターで `draft: true` が設定されているすべてのブログ投稿を一覧表示します -
`build` は常に下書きをスキップするため、その存在を確認できるのはここだけです。

```bash frame="terminal" title="Terminal"
bxSites blog:drafts
```

常に `0` で終了します。

## `blog:find`

完全な `build` を実行せずに、著者/カテゴリ/タグ/日付範囲でブログ投稿を絞り込みます。

```bash title="Usage"
bxSites blog:find [--author=...] [--category=...] [--tag=...] [--since=...] [--until=...] [--drafts]
```

- `--author`、`--category`、`--tag` - 投稿自身の値のいずれかと大文字小文字を区別しない完全一致
- `--since`、`--until` - 日付。`--since` 以降および/または `--until` 以前の投稿のみが一致します
- `--drafts` - 下書き投稿も含めます（デフォルトでは除外されます）

すべてのフィルタは任意かつ独立しています - 何も渡さなければ公開済みのすべての投稿を一覧表示します。

## `search:query`

すでにビルド済みの `site/search-index.json` に対してキーワードクエリを実行します -
先に `build` または `search-index` を実行してください。クライアントサイドの検索ウィジェットが
使うのと同じ相対フィールド重み付け（タイトル、次にタグ、次に見出し、次に本文）で結果を
ランク付けするため、ブラウザを開かなくても実際の訪問者の検索が何を表示するかを
チェックできます。

```bash title="Usage"
bxSites search:query --query="getting started" [--limit=10]
```

- `--query`（必須）- スペース区切りの検索語
- `--limit` - 返す結果の最大数。デフォルトは `10`

## `lint`

生の `docs/` Markdown ソースに対するビルド前のコンテンツ品質パスです。すでにビルド済みの
`site/` のみを検査する `site:check` とは異なります。以下をチェックします:

- **見出しレベルのスキップ** - ページ本文が `##` から `###` を挟まずに直接 `####` に
  ジャンプしている（構造が分かりにくく、アクセシビリティにも良くありません）。フェンス付き
  コードブロック内の行が見出しと誤認されることはありません。
- **ブログ投稿の日付の問題** - `docs/blog/posts/**` の投稿にフロントマター `date` が
  欠落しているか無効な場合（`build` 自体は投稿を読み込んだ瞬間にこれをスローしますが、
  `lint` は代わりにこれを検出結果として表示します）。

```bash frame="terminal" title="Terminal"
bxSites lint
```

いずれかのチェックで何か見つかれば `1`、それ以外は `0` で終了します。
