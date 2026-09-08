---
title: ColdBoxアプリケーション
order: 6.8
icon: phosphor-duotone:tree-structure
tags: [ガイド, boxlang, coldbox, api, integration]
---

# ColdBoxアプリケーション

`bxSites coldbox` は [ColdBox](https://coldbox.ortusbooks.com) アプリケー
ションを、ディスク上の規約からドキュメント化します。ルート、イベント
ハンドラー、モデルと WireBox マッピング、モジュール、インターセプター、
スケジュールタスクが対象です。

アプリケーションを起動することはありません。コンパイルも不要、データソース
への到達も不要、環境変数の設定も不要なので、CI ランナーでもローカルと同じ
ように動きます。この方式で見えないものは、下の[限界](#限界)に明記して
あります。

## クイックスタート

bx-sites プロジェクトを兼ねている ColdBox アプリのルートで実行します。

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites build
```

ページは `docs/api/coldbox/` 以下に生成されます。アプリが別の場所にある
場合は次のように指定します。

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app
```

## 設定

すべて省略可能です。完全なスキーマは
[`coldbox`](../configuration.md#coldbox) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

`include` は生成するページ群を決めます。トークンを外すと、そのページ群は
まったく生成されません。各キーにはフラグがあります。

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app --include=routes,handlers \
	--pagePathPrefix=reference --tags=reference,api
```

## 生成されるもの

```
docs/api/coldbox/
├── index.md               # アプリの概要と件数
├── routes.md              # すべてのルート（宣言順）
├── handlers/
│   ├── index.md
│   ├── Main.md
│   └── api/Orders.md      # モジュールのハンドラーはモジュール配下
├── models/
│   ├── index.md           # モデルとバインダーのマッピング
│   └── UserService.md
├── modules/
│   ├── index.md
│   └── api.md
├── interceptors.md
└── scheduled-tasks.md
```

### ルート

アプリのルーターと各モジュールのルーターが宣言するすべてのルートを、宣言
された順に並べます。ColdBox は最初に一致したパターンを採用するため順序に
意味があり、ページもその順序を保ちます。`resources()`/`apiResources()` は
ColdBox が生成する個々のルートに展開され、モジュールのルートは実際に
マウントされているエントリーポイントを反映します。

### ハンドラー

ハンドラーごとに1ページ。そこへ到達するルートと、それぞれが呼び出す
アクションを示し、続けて呼び出し可能なアクションをドキュメントコメントと
引数つきで並べます。ColdBox 自身のライフサイクルフック（`preHandler`、
`aroundHandler`、`onError` など）は URL から到達できるアクションとしてでは
なく、独立したセクションにまとめます。`init` と private メソッドは含めま
せん。

### モデル

モデルごとに1ページ。スコープ、WireBox が注入するもの、モデル自身の
プロパティ、public メソッドを示します。注入された依存関係は通常の
プロパティとは分けて表示します。`property name="x" inject="y"` は配線で
あってデータではないからです。インデックスにはバインダーが宣言した
マッピングも並びます。

### モジュール、インターセプター、スケジュールタスク

モジュールページには、その `ModuleConfig` が宣言する内容（作者、バージョン、
エントリーポイント、依存関係）と、そのモジュールがアプリに加えるものが
並び、それぞれ説明ページへリンクします。インターセプターのページは
ColdBox が見つける2つの経路の両方、つまり `config/ColdBox` が登録するものと
`interceptors/` が宣言するものを扱い、各クラスの public メソッドを
インターセプションポイントとして列挙します。タスクのページは
`config/Scheduler` と各モジュールのスケジューラーを読み取ります。

## DocBox でより詳細なページに

ルート、モジュール、インターセプター、アプリの全体像は規約だけから得られ
ます。クラス単位の詳細（ハンドラーのアクション、モデルのメソッド、その
引数やコメント）は [DocBox](docbox.md) から取得するため、`bx-docbox` を
入れるとこれらのページは大幅に充実します。

```bash frame="terminal" title="Terminal"
install-bx-module bx-docbox
```

なくても verb は動作し、ハンドラー・モデル・ルート・モジュールはすべて
列挙されます。ページには不足している情報と、その入手方法が明記されます。

## 限界

静的な読み取りには明確な限界があり、それは隠さず示すべきものです。
アプリケーションが実行時に決めることは、ディスク上には存在しません。

- パターンやターゲットを変数から組み立てるルート、ループ内で登録される
  ルート。
- 名前が計算される WireBox マッピングやスケジュールタスク。
- `modules_app/` にコミットされず、起動時にインストールされるモジュール。
- `mapDirectory()` が実際に登録する内容。これは起動時にディスク上に何が
  あるかで決まります。

これらはいずれも推測せずスキップします。生成されたページは、誤ったことを
書くより控えめに書きます。

## Gradle や Maven からは利用できません

これは意図的です。ColdBox アプリケーションは CommandBox でビルド・実行する
ものであり、Java のビルドツールで扱うものではないため、
`bxSitesColdBoxDoc` タスクも `bxsites:coldbox` ゴールも存在しません。
[Gradle](gradle-plugin.md) と [Maven](maven-plugin.md) のプラグインが提供
するのは [DocBox](docbox.md) ジェネレーターで、こちらはビルド対象の JVM
プロジェクトに実際に含まれる BoxLang/CFML クラスを対象とします。
