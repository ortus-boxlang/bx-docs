---
title: デプロイ
order: 3
icon: phosphor-duotone:cloud-arrow-up
tags: [ガイド, デプロイ]
---

# デプロイ

`site/` はプレーンな静的サイトです - 静的ファイルを配信できる場所であればどこにでも
ホストできます。[`bxSites deploy`](../cli-reference.md#deploy) を使えば、一つの
コマンドで直接そこへ配信できます: S3（および DigitalOcean Spaces、Cloudflare R2、
Backblaze B2、MinIO などの S3 互換サービス）、Azure Blob Storage、Google Cloud
Storage、Firebase Hosting、FTP、SFTP、rsync-over-SSH、Netlify、Vercel、
Cloudflare Pages、ローカルディレクトリ、または GitHub Pages です。

## `deploy` コマンド

最も単純な2つのターゲット（`local` / `github-pages` - フラグだけで動作します。
詳しくは [CLI リファレンス](../cli-reference.md#deploy) を参照）を除き、すべての
ターゲットはプロジェクトルートの `deployments/<name>.json` ファイルで設定します -
実際に使用するデプロイターゲットごとに1ファイルで、それぞれがどの `target` かと、
そのターゲット固有のフィールドを指定します:

```bash frame="terminal" title="Terminal"
bxSites deploy --entry=production
```

```json title="deployments/production.json"
{ "target": "s3", "bucket": "my-docs-site", "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID", "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY" }
```

**シークレットは必ず環境変数から取得され、`deployments/*.json` 内のリテラル値から
取得されることはありません。** `EnvVar` で終わるすべてのフィールドは、実際の
シークレット（アクセスキー、パスワード、API トークン）を保持している*環境変数*の
名前を指定します - デプロイ時にその都度解決されるため、`deployments/*.json` 自体は
常にコミットしても安全です。自分ですでに管理している認証情報ファイル（SSH 秘密鍵、
ダウンロードした GCP サービスアカウントの JSON キーなど）への*パス*を指定する
フィールドだけは例外です - こちらは普通のフィールドのままにします。バージョン
管理から外すべきなのはパスではなくファイル自体だからです。ローカルでは、
これらの環境変数をシェルへ手作業でエクスポートする代わりに `.env` ファイルから
読み込むこともできます（BoxLang が自動的に1つを読み込み、各ターゲットが
それらの解決に使う `getSystemSetting()` が透過的にそれを参照します） - CI では、
ランナー上の実際のシークレットとして設定してください。

### すべてのターゲットへ一括デプロイ

`--entry` も `--target` も指定せずに `bxSites deploy` を実行すると、単一の
共有ビルドを使って `deployments/*.json` の各エントリーが順番にデプロイ
されます:

```bash frame="terminal" title="Terminal"
bxSites deploy
```

エントリーの数に関わらず、サイトのビルドは一度だけ行われます。1つの
ターゲットが失敗しても他は止まりません - すべてのエントリーが試行され、
コマンドは少なくとも1つが失敗した場合にのみ非ゼロで終了します。サマリーには
成功した数が報告されます（例: `Deployed to 2/3 target(s) (1 failed)`）。
`--verbose`（`--entry`/`--target` と組み合わせても使えます）を付けると、
最終的なサマリーだけでなく、ビルドおよび各ターゲットの開始/終了時にも進捗行が
出力されます。

### `local`

ビルドしたサイトを任意のディレクトリ（共有ドライブ、ステージング用フォルダ、その他
どこでも）にコピーします。`deployments/` のエントリを一切必要としない唯一の
ターゲットです。

```bash frame="terminal" title="Terminal"
bxSites deploy --target=local --destination=/path/to/somewhere
```

### `github-pages`

[`gh-deploy`](../cli-reference.md#gh-deploy) が行うのと同じプッシュに、この統合
コマンドからも到達できます - こちらも `deployments/` のエントリは不要です:

```bash frame="terminal" title="Terminal"
bxSites deploy --target=github-pages [--branch=gh-pages] [--remote=origin] [--message="..."]
```

### `s3`

本物の AWS S3、または任意の S3 互換サービス - AWS 自体以外では `endpoint` を
設定し、AWS 以外のほとんどのプロバイダでは `forcePathStyle: true` も設定します。

```json title="deployments/production.json"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "region": "us-east-1",
  "prefix": "",
  "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID",
  "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY"
}
```

```json title="deployments/spaces.json (DigitalOcean Spaces)"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "endpoint": "https://nyc3.digitaloceanspaces.com",
  "forcePathStyle": true,
  "accessKeyIdEnvVar": "SPACES_KEY",
  "secretAccessKeyEnvVar": "SPACES_SECRET"
}
```

同じ形（カスタム `endpoint` + `forcePathStyle: true`）は、Cloudflare R2
（`https://<accountid>.r2.cloudflarestorage.com`）、Backblaze B2、MinIO/Wasabi も
カバーします。

### `azure`

Azure Blob Storage。SAS トークン、アカウントキー、完全な接続文字列のいずれか一つで
認証します。

```json title="deployments/production.json"
{
  "target": "azure",
  "account": "mystorageaccount",
  "container": "site",
  "accountKeyEnvVar": "AZURE_STORAGE_KEY"
}
```

### `gcs`

Google Cloud Storage。ダウンロードしたサービスアカウントの JSON キー（Google Cloud
Console -> IAM と管理 -> サービスアカウント -> 鍵）で認証します。

```json title="deployments/production.json"
{
  "target": "gcs",
  "bucket": "my-docs-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `firebase`

Firebase Hosting。`gcs` と同種のサービスアカウントキーを使用します。

```json title="deployments/production.json"
{
  "target": "firebase",
  "siteId": "my-firebase-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `ftp` / `sftp`

サイト全体を FTP または SFTP でリモートサーバーにアップロードし、フォルダ構造を
維持します。SFTP はパスワードまたは SSH 鍵に対応しています。

```json title="deployments/production.json"
{
  "target": "sftp",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "key": "/home/me/.ssh/id_rsa"
}
```

### `rsync`

実際の `rsync` バイナリを使い、SSH 経由でサイトをリモートサーバーに同期します -
変更分だけを転送するため、フルリビルドでは FTP/SFTP より高速です。`bxSites` を
実行するマシンに `rsync` と `ssh` が必要です。

```json title="deployments/production.json"
{
  "target": "rsync",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "identityFile": "/home/me/.ssh/id_rsa"
}
```

### `netlify`

```json title="deployments/production.json"
{
  "target": "netlify",
  "siteId": "my-site-id-or-name.netlify.app",
  "authTokenEnvVar": "NETLIFY_AUTH_TOKEN"
}
```

### `vercel`

```json title="deployments/production.json"
{
  "target": "vercel",
  "projectId": "my-project",
  "authTokenEnvVar": "VERCEL_TOKEN"
}
```

### `cloudflare-pages`

Cloudflare には direct-upload デプロイ向けの公式ドキュメント化された REST API が
ありません - あるのは `wrangler` CLI だけです。このターゲットは Wrangler 自身の
アップロードフローをリバースエンジニアリングしたもので、デフォルトの Java
インストールの多くには含まれていない BLAKE3 ハッシュ実装を JVM のクラスパス上に
必要とします - この点の粗さについての詳細で正直な説明は
[CLI リファレンス](../cli-reference.md#deploy) とターゲット自身のソースを
参照してください。

```json title="deployments/production.json"
{
  "target": "cloudflare-pages",
  "accountId": "your-account-id",
  "projectName": "my-project",
  "apiTokenEnvVar": "CLOUDFLARE_API_TOKEN"
}
```

## `package` コマンド

上記のどのターゲットよりも単純なアーカイブの方が良い場合 - ビルドを GitHub
リリースに添付する、zip アップロードしか受け付けないホストに渡す、あるいは
どのプラガブルターゲットも届かない場所へ配送する、といったケースです。
[`bxSites package`](../cli-reference.md#package) はサイトをビルドしてから、
ルートがビルドされたサイト自身の内容になっている（`site/` フォルダで包まれて
いない）単一のファイルへ圧縮します:

```bash frame="terminal" title="Terminal"
bxSites package
bxSites package --output=dist/my-site.zip
```

`--output` のデフォルトは `<projectRoot>/site.zip` です。相対パスを指定した
場合はプロジェクトルートを基準に解決され、まだ存在しない親ディレクトリは
自動的に作成されます。

## GitHub Actions（複数バージョン公開）

手動で実行する `bxSites deploy`/`gh-deploy` の代わりに、プッシュのたびに自動で
公開したい場合のために、このモジュールには `main` と `development` を GitHub
Pages 上で同じサイトの独立して稼働する 2 つのバージョンとして公開する、すぐに
使える GitHub Actions ワークフロー（`.github/workflows/pages.yml`）が付属して
います。このガイドの残りは、このリポジトリ自体のドキュメントが使用している
そのワークフローについて説明します。

## 動作内容

`docs/`、`bxsites.yaml`、またはモジュール自体のソース（テーマ/パイプラインの変更）に
触れる `main` または `development` へのすべての Push で、ワークフローが:

1. BoxLang + [bx-markdown](https://github.com/ortus-boxlang/bx-markdown) をインストール
2. このリポジトリをモジュールとして登録して `boxlang bxSites build` を解決
3. `main` 以外のブランチでは、このビルドのみ `baseURL` を `.../<branch-name>/` に設定
   （[以下を参照](#2-つのバージョンを同時に公開)）
4. `boxlang bxSites build` を実行
5. `site/` を `gh-pages` ブランチに Push（`main` はサイトルート、`development` は `/development/`）、
   もう一方のバージョンには触れません

Actions タブから手動トリガー（`workflow_dispatch`）でも利用可能で、
新しいコミットなしに一回限りの再公開ができます。

## 初回セットアップ

ワークフローが公開できるようにする前に、GitHub Pages を `gh-pages` ブランチに向ける必要があります。
これはリポジトリの設定で行うもので、ワークフローファイルだけでは有効にできません。
最初の成功したランが `gh-pages` を作成するため、ワークフローが少なくとも一度実行されてから行います:

1. **Settings → Pages**
2. **Build and deployment → Source** で **Deploy from a branch** を選択
3. **Branch** で **gh-pages** と **/ (root)** を選択

それ以降、一致するすべての Push が自動的にビルドおよびデプロイされます。
最初のデプロイ完了後に **Settings → Pages** に公開 URL が表示されます。

## 2 つのバージョンを同時に公開

`main` はサイトルート（`https://<user>.github.io/<repo>/`）に公開されます（安定版/リリース済みドキュメントとして扱います）。
`development` は `/development/`（`https://<user>.github.io/<repo>/development/`）に公開されます（最新の未リリースドキュメント）。
両方が同時に稼働し続けます: 各ブランチのジョブは `keep_files: true` と独自の `destination_dir` を使用して
`gh-pages` にのみ Push するため、`development` のデプロイが `main` のコンテンツを上書きすることはなく、
その逆も同様です。

3 番目のブランチを追加するには（例: `release/2.0` プレビュー）、`on.push.branches` リストに追加し、
`destination_dir: release-2.0`（または類似）を使用した `if: github.ref_name == '...'` デプロイステップを追加します。

## 独自プロジェクトへの応用

`.github/workflows/pages.yml` を独自のプロジェクトにコピーし（プロジェクトが `bx-markdown` 以外を
必要とする場合は `modules:` 行を調整し）、上記のように Pages を有効化すると、
`main`/`development` への Push が同様に公開されます。単一の公開バージョンのみが必要な場合は、
必要のないブランチを `on.push.branches` とその対応するデプロイステップから削除します。

## プロジェクト Pages のサブパスからの配信

GitHub の *プロジェクト* Pages サイト（*ユーザー* サイトとは異なり）は
`https://<user>.github.io/<repo>/` から配信されます（ドメインルートではありません）。
`bxsites.yaml` の `baseURL` をその完全な URL に設定して、すべての内部リンク、アセット、
ナビゲーションエントリに必要な `/<repo>/` プレフィックスが付くようにしてください。
また、実際の `sitemap.xml` も生成されます:

=== "YAML"
    ```yaml title="bxsites.yaml"
    baseURL: "https://<user>.github.io/<repo>/"
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "baseURL": "https://<user>.github.io/<repo>/" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    baseURL = "https://<user>.github.io/<repo>/"
    ```

`baseURL` が何をするかの完全な詳細については [設定](../configuration.md#baseurl) を参照してください。
`<user>.github.io` ユーザーサイト、またはサイトルートにマッピングされたカスタムドメインの
場合は、`baseURL` をデフォルト（`/`）のままにしておけます。

## サイトへのアクセスを制限する

ここには組み込みのアクセス制御はありません - bx-sites が生成するのはあくまで
プレーンな静的 `site/` であり、静的ファイルには「誰がリクエストしているか」という
概念がありません。`bxsites.json` の [`robots: false`](../configuration.md#robotstxt) は、
行儀の良いクローラーにビルドをインデックスしないよう伝えます（検索結果に出したくない
ステージング/プレビューデプロイに便利です）が、これはあくまで丁重なお願いであって
鍵ではありません - URL を持っている人には引き続き機能します。実際にアクセスを
制限する必要がある場合、それは静的ファイルの手前、つまりそれを配信しているホスト側で
行う必要があります - よくある、静的サイトに適した選択肢をいくつか挙げます:

- **Cloudflare Pages/Access** - デプロイしたサイトを
  [Cloudflare Access](https://developers.cloudflare.com/cloudflare-one/policies/access/)
  ポリシー（メールアドレスの許可リスト、SSO、またはワンタイム PIN）の背後に置きます -
  アプリケーションコードは一切不要です。
- **Netlify** - サイト設定だけで使える組み込みの
  [パスワード保護](https://docs.netlify.com/manage/security/secure-access-to-sites/site-protection/)
  で、サイト単位/デプロイ単位のどちらでも設定できます。
- **小さなリバースプロキシ**（任意のホスト）- 静的ファイルの手前に HTTP Basic 認証
  （`.htpasswd` 形式のルール、または一つのファイルで済む Cloudflare Worker/Netlify
  Edge Function）を置けば、「検索エンジンや無関係な人を締め出す」目的には十分です -
  ただし、サインイン済みアプリが持つような本物のユーザーごとの識別ではありません。

これらはいずれも bx-sites 自体の機能ではありません - `site/` の配信先ホストの側で
有効にする、ホストレベルの設定です。
