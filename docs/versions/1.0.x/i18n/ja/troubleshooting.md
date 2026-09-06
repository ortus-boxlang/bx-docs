---
title: トラブルシューティング
order: 2.4
icon: phosphor-duotone:lifebuoy
summary: セットアップ・ビルド・serve に関するよくある問題を診断します。このページで解決しない場合の問い合わせ先も掲載しています。
tags: [トラブルシューティング, faq]
---

# トラブルシューティング

## まず `doctor` を実行する

さらに調べる前に、組み込みのヘルスチェックを実行してください - この
ページに載っている問題のほとんどを一度に洗い出せます。

```bash frame="terminal" title="Terminal"
bxSites doctor
```

JVM のバージョン、`docs/`(または `src/`)が存在するかどうか、
`bxsites.yaml`/`.json` が実際にパース・検証できるかどうか、必要な
BoxLang モジュールがインストールされ有効化されているかどうか、そして
プロジェクトレベルの `theme/` オーバーライドが存在する場合はそれが
テーマの契約を満たしているかどうかをチェックします。いずれかのチェッ
クが失敗すると終了コード `1` を返し、何が問題かを出力します。プロジェ
クトに変更を加えることはありません。

## よくある問題

??? bug "`No docs/ directory found`"
    `build`/`serve`/`site:check` などは、現在のディレクトリを基準に `docs/`
    (見つからなければ `src/`)を探すか、`--projectRoot=<path>` を指定
    した場合はそれを基準に探します。プロジェクトのルートフォルダー内
    でコマンドを実行するか、`--projectRoot` を指定してください。

    ```bash frame="terminal" title="Terminal"
    bxSites build --projectRoot=/path/to/my-docs
    ```

??? bug "`bxsites.yaml`/`.json` のパースまたは検証が失敗する"
    `bxSites doctor` を実行すると、設定ローダーが拒否した正確なキー/
    行が確認できます。よくある原因: YAML のインデントでタブとスペース
    が混在している、JSON に余分なカンマがある、配列を期待するキー
    (`nav` や `i18n.locales` など)が単なる文字列として書かれている、
    などです。キーの完全なリファレンスは
    [設定](configuration.md) を参照してください。

??? bug "`bx-markdown`/`bx-esapi`/`bx-yaml`/`bx-image` がインストール/有効化されていない"
    `build`、`serve`、`search-index` はこれら 4 つの BoxLang モジュー
    ルをすべて必要とします。`bx-sites` 自体をインストールすると、
    `box.json` の依存関係として自動的にインストールされます
    (`install-bx-module bx-sites` または `box install bx-sites`)
    - このエラーが表示される場合は、インストールが完了していないか、
    依存関係なしでモジュールが手動で登録された可能性があります。プロ
    ジェクトのルートで `box install` を再実行するとすべて解決されま
    す。どのモジュールが(もしあれば)まだ不足しているかは
    `bxSites doctor` で確認できます。

??? bug "プロジェクトの `theme/` オーバーライドがビルドできない"
    独自の `theme/` フォルダーは `layout.bxm` と `page.bxm` の両方を
    提供する必要があります - どちらが不足しているかは `doctor` が
    報告します。完全な契約については[テーマ](guides/themes.md)を参照
    するか、ゼロから書く代わりに `bxSites theme:new` を実行して動作
    する組み込みテーマを出発点としてエクスポートしてください。

??? bug "`serve` が変更を検知しない"
    `serve` は `docs/`、`bxsites.yaml`/`.json`、およびプロジェクトレベ
    ルの `theme/` オーバーライドを監視します - それ以外の場所での変更
    (たとえば実際のプロジェクトではなくモジュールのチェックアウト内の
    `resources/` 配下のファイルを編集した場合など)は再ビルドをトリガ
    ーしません。実際の変更が反映されない場合は、`serve` を停止し、
    `bxSites clean` を実行して古いビルドキャッシュをクリアしてから、
    `bxSites serve` を再度実行してください。

??? bug "ビルドが古く見える、または CI は成功と報告するが何も変わっていない"
    `build` は、対応するソースページがもう存在しない、以前にビルドさ
    れた出力を削除しません。`build` の前に `bxSites clean` を実行して
    `site/` とビルドキャッシュを完全に削除し、ゼロから再ビルドしてく
    ださい。CI のステップが成功と報告しているのにデプロイされたサイト
    に反映されない場合は、実際のビルドステップのログを確認して
    `Error:` がないか探してください - 一部の CI 設定では、クラッシュ
    したビルドでも紛らわしい成功ステータスを返すことがあります。

??? bug "翻訳済みのページに未翻訳の通知が表示される"
    これは想定どおりの動作であり、バグではありません: あるロケールで
    は、使用可能になるためにすべてのページが翻訳されている必要はあり
    ません。`docs/i18n/<code>/` に存在しないページも、想定される URL
    でビルドされ、デフォルトロケールの内容がページ上部の小さな通知と
    ともに表示されます。詳しくは
    [国際化(i18n)](guides/i18n.md)を参照してください。

??? bug "`i18n:status` は 100% と報告しているのに翻訳が古いままに見える"
    `i18n:status` はロケールごとのページの*存在*のみをチェックし、
    ページごとの内容の一致は確認しません - ロケールのコピーが存在して
    いても、後からデフォルトロケールのページに追加されたセクションが
    抜けている可能性があります。疑わしい場合は、ロケールのファイルを
    デフォルトロケールの対応するファイルと直接比較してください。

## それでも解決しない場合

上記のいずれにも当てはまらない場合は、以下のサポートチャンネルからお
問い合わせください - 完全な一覧は[貢献する](contribute.md)を参照して
ください。

::: cards
::: card title="Ortus コミュニティ Discourse" icon="phosphor-duotone:chats-circle" href="https://community.ortussolutions.com"
質問したり、既存の議論を検索したりできます。
:::
::: card title="Box Slack チーム" icon="phosphor-duotone:slack-logo" href="https://boxteam.slack.com"
コミュニティやメンテナーとリアルタイムでチャットできます。
:::
::: card title="Issue を報告する" icon="phosphor-duotone:bug" href="https://github.com/ortus-boxlang/bx-sites/issues"
再現可能なバグの場合は、`bxSites doctor` の出力を添えてください。
:::
::: card title="プロフェッショナルサポート" icon="phosphor-duotone:handshake" href="services.md"
BxSites を開発しているチームに解決してもらいたいですか? Ortus
Solutions はプロフェッショナルサポートとコンサルティングを提供して
います。
:::
:::
