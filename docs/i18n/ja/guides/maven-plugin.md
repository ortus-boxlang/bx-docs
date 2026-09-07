---
title: Maven プラグイン
order: 6.4
icon: phosphor-duotone:puzzle-piece
tags: [ガイド, java, maven, integration]
---

# Maven プラグイン

Java・Spring Boot 開発者は、自分のプロジェクトに bx-sites のサイトを
追加するために CommandBox やシステム全体への BoxLang インストールを
必要としません - `io.boxlang:bxsites-maven-plugin` Maven プラグインは、
初回実行時に必要なもの（BoxLang ランタイムと bx-sites 本体）をすべて
ローカルキャッシュにダウンロードします。前提条件は JDK 21 だけです。
[Gradle プラグイン](gradle-plugin.md) の Maven 版に相当し、どちらの
プラグインも同じ基盤ロジックをラップしているため、verb のカバレッジと
挙動は両方のビルドツールで同一に保たれます。

> **ステータス:** バージョン1.0未満で、まだ Maven Central には公開されて
> いません - ソースコードと現時点のビルド/テスト手順は bx-sites
> リポジトリの
> [`maven-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/maven-plugin)
> を参照してください。このページは公開後の動作を説明しています。以下の
> メカニズムはすでに実装され検証済みですが、まだ Maven Central の
> 座標として利用できるわけではありません。

## クイックスタート

```xml title="pom.xml"
<build>
  <plugins>
    <plugin>
      <groupId>io.boxlang</groupId>
      <artifactId>bxsites-maven-plugin</artifactId>
      <version>&lt;version&gt;</version>
    </plugin>
  </plugins>
</build>
```

```bash
mvn bxsites:new     # docs/ + bxsites.yaml を生成
mvn bxsites:build   # docs/**.md を site/ にレンダリング
mvn bxsites:serve   # ビルドしてライブリロード付きでローカル配信
```

短縮形の `bxsites:<goal>`（動作確認済み）を使うには、上記の
`<plugin>` ブロックが `<pluginManagement>` だけでなく、具体的に
`<build><plugins>` の下に宣言されている必要があります - それによって
現在のプロジェクトで `io.boxlang` が解決可能な goal プレフィックスとして
登録されます。そこに宣言されていない場合は、完全修飾形式を使ってください:
`mvn io.boxlang:bxsites-maven-plugin:build`。

デフォルト設定であれば、これ以上の設定は不要です - プラグインが
コンテンツディレクトリ（`docs/`、なければ `src/`）と出力ディレクトリ
（常に `<projectRoot>/site/`）を自動的に検出します。サイト自体の見た目、
テーマ、ナビゲーション、その他すべての設定はプロジェクトルートの
`bxsites.yaml`/`.toml`/`.json` で完全に制御されます。
[設定](../configuration.md) に記載されているとおりで、プラグインはこの
スキーマを一切複製せず、bx-sites を自分のビルドから*どのように*・
*いつ*実行するかだけを担います。

## Goal

| Goal | 内容 |
|---|---|
| `bxsites:new` | 新しい bx-sites プロジェクト（コンテンツディレクトリ + 設定ファイル）を生成します。 |
| `bxsites:build` | サイトを `<projectRoot>/site/` にレンダリングします。前回のビルド以降、コンテンツディレクトリや設定ファイルの下で何も変更されていない場合はサブプロセスの再実行をスキップします - 詳細は下記の[ビルドの Staleness チェック](#ビルドの-staleness-チェック)を参照してください。 |
| `bxsites:serve` | サイトをビルドしてライブリロード付きでローカル配信します。停止するまで（Ctrl+C）フォアグラウンドで実行され続けます。 |
| `bxsites:clean` | `<projectRoot>/site/` を削除します。単純なディレクトリ削除で、サブプロセスは使いません。 |
| `bxsites:search-index` | サイト全体をビルドせずに `site/search-index.json` を再構築します。 |
| `bxsites:lint` | docs/ 配下の Markdown ソースを lint します。 |
| `bxsites:deploy` | サイトをビルドし、設定済みのターゲットへデプロイします。 |
| `bxsites:publish` | サイトをビルドし、bxSites Cloud に公開します。 |
| `bxsites:package` | サイトをビルドし、`site.zip` に圧縮します。 |
| `bxsites:stats` | ビルド済みサイトのページ数・単語数などの統計を報告します。 |
| `bxsites:doctor` | bx-sites 自身のプロジェクトヘルス診断を実行します。 |

各 goal は初回実行時に、自分で必要なものをプロビジョニング
（ダウンロード/キャッシュ）します - Gradle プラグインとは異なり、事前に
実行すべき別個の「provision」goal はありません。

デフォルトでは、どの goal も Maven のライフサイクルフェーズに紐付けられ
ていません - 明示的に実行してください。`bxsites:build` を自動実行したい
場合は、`<executions>` ブロックで自分でバインドしてください。例えば
`pre-site` に紐付けるのは、Maven 自身の `site` ライフサイクルとの
自然な組み合わせです。

## 設定

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <projectRoot>${project.basedir}</projectRoot>
    <boxlangMiniserverVersion>1.18.0-snapshot</boxlangMiniserverVersion>
    <bxSitesVersion>1.0.0-snapshot</bxSitesVersion>
    <boxlangHomeDir>${project.build.directory}/bxsites/boxlang-home</boxlangHomeDir>
  </configuration>
</plugin>
```

どのパラメータにも適切なデフォルト値があり、新規プロジェクトでは
どれも設定する必要はありません。出力ディレクトリはここでは一切設定
できません - bx-sites 自体が `<projectRoot>/site/` に固定しているため、
実際には反映されない設定項目を用意する代わりに、プラグインがそれを
導出するだけです。

## ビルドの Staleness チェック

Maven には Gradle のような組み込みの増分ビルドエンジンがないため、
`bxsites:build` は独自の軽量なチェックを実装しています - コンテンツ
ディレクトリ配下（および存在すれば設定ファイル）の最新の更新時刻
（mtime）と、`<projectRoot>/site/` にすでに存在する最新の更新時刻を
比較します。何も新しくなければ、goal はスキップする旨をログに記録し、
bx-sites を一切再呼び出しせずに終了します。それでも強制的にリビルド
するには:

```bash
mvn bxsites:build -Dbxsites.build.forceRebuild=true
```

## まだ実装されていないもの

- **Spring Boot ドキュメント生成**（OpenAPI、Javadoc、コントローラースキャン）- 計画中。
- **`bxsites:serve` のライブ出力ストリーミング** - 現在は出力をバッファリングし30分のタイムアウトを適用していますが、どちらも無期限に実行され続けるべき goal としては誤った挙動です。

Gradle 側の対応物については [Gradle プラグイン](gradle-plugin.md) の
ガイドを参照してください - どちらのプラグインも同じ基盤ロジックを
ラップしているため、verb のカバレッジと挙動は両方のビルドツールで
同一に保たれます。
