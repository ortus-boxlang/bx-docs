---
title: Gradle プラグイン
order: 6.3
icon: phosphor-duotone:gear-six
tags: [ガイド, java, gradle, integration]
---

# Gradle プラグイン

Java・Spring Boot 開発者は、自分の Java プロジェクトに bx-sites の
サイトを追加するために CommandBox やシステム全体への BoxLang インストール
を必要としません - `io.boxlang.bxsites` Gradle プラグインは、初回実行時
に必要なもの（BoxLang ランタイムと bx-sites 本体）をすべてローカルキャッシュ
にダウンロードします。前提条件は JDK 21 だけです。

> **ステータス:** バージョン1.0未満で、まだ Gradle Plugin Portal には
> 公開されていません - ソースコードと現時点のビルド/テスト手順は
> bx-sites リポジトリの
> [`gradle-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/gradle-plugin)
> を参照してください。このページは公開後の動作を説明しています。以下の
> メカニズムはすでに実装され検証済みですが、まだ `plugins { }` の
> 1行依存として利用できるわけではありません。

## クイックスタート

```kotlin title="build.gradle.kts"
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # docs/ + bxsites.yaml を生成
./gradlew bxSitesBuild   # docs/**.md を site/ にレンダリング
./gradlew bxSitesServe   # ビルドしてライブリロード付きでローカル配信
```

デフォルト設定であれば、これ以上の設定は不要です - プラグインが
コンテンツディレクトリ（`docs/`、なければ `src/`）と出力ディレクトリ
（常に `<projectRoot>/site/`）を自動的に検出します。サイト自体の見た目、
テーマ、ナビゲーション、その他すべての設定はプロジェクトルートの
`bxsites.yaml`/`.toml`/`.json` で完全に制御されます。
[設定](../configuration.md) に記載されているとおりで、プラグインはこの
スキーマを一切複製せず、bx-sites を自分のビルドから*どのように*・
*いつ*実行するかだけを担います。

## タスク

| タスク | 内容 |
|---|---|
| `bxSitesNew` | 新しい bx-sites プロジェクトを生成します。どのライフサイクルにも組み込まれていません - 明示的に一度だけ実行してください。 |
| `bxSitesBuild` | サイトをレンダリングします。実際の up-to-date チェックを行い、コンテンツ・設定・固定バージョンが実際に変わった場合のみ再実行されます。 |
| `bxSitesServe` | サイトをビルドしてライブリロード付きでローカル配信します。停止するまでフォアグラウンドで実行され続けます。 |
| `bxSitesClean` | ビルド済みの `site/` ディレクトリを削除します。 |

`bxSitesBuild` は、明示的に有効化しない限り（下記の `hookIntoAssemble`
を参照）`assemble` の一部として自動実行されることはありません - ドキュメント
のビルドは、実際のコードのコンパイルとは別の、しばしばより時間の
かかる関心事です。

## 設定

```kotlin title="build.gradle.kts"
bxSites {
    projectRoot.set(layout.projectDirectory)
    boxlangMiniserverVersion.set("1.18.0-snapshot")   // 固定された BoxLang ランタイムバージョン
    bxSitesVersion.set("1.0.0-snapshot")               // 固定された bx-sites バージョン
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                        // オプトイン: bxSitesBuild を assemble の一部として実行する
    hookIntoCheck.set(true)                            // lint/check 系の verb はデフォルトで有効（今後追加予定、まだ未実装）
}
```

どのプロパティにも適切なデフォルト値があります。出力ディレクトリは
ここでは一切設定できません - bx-sites 自体が `<projectRoot>/site/` に
固定しているため、実際には反映されない設定項目を用意する代わりに、
プラグインがそれを導出するだけです。

## まだ実装されていないもの

- **Spring Boot ドキュメント生成**（OpenAPI、Javadoc、コントローラースキャン）- 計画中。
- **`bxSitesServe` のライブ出力ストリーミング** - 現在は出力をバッファリングし30分のタイムアウトを適用していますが、どちらも無期限に実行され続けるべきタスクとしては誤った挙動です。
- 上記の中核となる4つを超える、bx-sites の他の verb（`deploy`、`publish`、`package`、`lint`、`check` など）のラッパータスク。

Maven 側の対応物については [Maven プラグイン](maven-plugin.md) のガイドを
参照してください - どちらのプラグインも同じ基盤ロジックをラップしている
ため、verb のカバレッジと挙動は両方のビルドツールで同一に保たれます。
