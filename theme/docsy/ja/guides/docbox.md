---
title: DocBox APIリファレンス
order: 6.7
icon: phosphor-duotone:brackets-curly
tags: [ガイド, boxlang, docbox, api, integration]
---

# DocBox APIリファレンス

`bxSites docbox` は BoxLang/CFML のクラスを、テーマが適用された検索可能な
APIリファレンスとして自分のサイト内に生成します。[Gradle](gradle-plugin.md)
プラグインと [Maven](maven-plugin.md) プラグインが Java プロジェクトに提供
している Javadoc ジェネレーターの、BoxLang 版にあたります。

BoxLang の解析自体は行いません。その仕事は
[DocBox](https://docbox.ortusbooks.com) がすでに十分にこなしているため、
ここでは DocBox 自身の JSON ストラテジーを実行し、その結果を通常の Markdown
ページへ変換します。ページはコンテンツディレクトリに他のページと同じように
配置されるので、次の `build` がテーマを適用し、検索インデックスに登録し、
手書きのページと同じように配信します。

## 前提条件

BoxLang ランタイムに `bx-docbox` モジュールがインストールされている必要が
あります。

```bash frame="terminal" title="Terminal"
# OSバイナリ
install-bx-module bx-docbox

# CommandBox
box install bx-docbox
```

インストールされていない場合、スタックトレースではなく対処方法を示す
メッセージで終了します。

## クイックスタート

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites build
```

設定が一切なくても、`docbox` はプロジェクトに実際に存在する慣例的な BoxLang
ソースフォルダー（`models`、`handlers`、`bifs`、`components`、
`interceptors`）を対象にし、`docs/api/docbox/` 以下にページを書き出します。

## 設定

すべて省略可能です。完全なスキーマは
[`docbox`](../configuration.md#docbox) を参照してください。

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "My API"
      mappings:
        models: models
        bifs: bifs
      excludes: "tests|build"
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "My API",
    		"mappings": { "models": "models", "bifs": "bifs" },
    		"excludes": "tests|build",
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [docbox]
    projectTitle = "My API"
    excludes = "tests|build"
    pagePathPrefix = "api/docbox"
    tags = [ "api", "docbox" ]

    [docbox.mappings]
    models = "models"
    bifs = "bifs"
    ```

各キーには、その実行だけ上書きするフラグがあります。

```bash frame="terminal" title="Terminal"
bxSites docbox --mappings:models=models --projectTitle="My API" \
	--pagePathPrefix=api/classes --tags=api,classes --excludes=tests
```

`--jsonDir=<パス>` を渡すと、DocBox の JSON 出力を破棄せずに残します。

## 生成されるもの

ページは3種類です。全体のインデックス、パッケージごとのインデックス、
そしてクラスごとのページです。

```
docs/api/docbox/
├── index.md                    # すべてのパッケージとクラス
├── models/
│   ├── index.md                # このパッケージのクラス
│   ├── UserService.md
│   └── security/
│       ├── index.md
│       └── Auth.md
```

クラスページには、そのクラスの docblock、宣言された `property` ブロック、
そしてアクセス修飾子ごとにまとめられた関数（コンストラクター、public、
package、private の順）が並び、それぞれ完全なシグネチャ、説明、パラメーター
表、`@return` のテキストを備えます。メンバーは Javadoc ページと同じ
Alpine.js のフィルターツールバーの中に置かれるため、長いクラスでも見通しが
保たれます。

## ナビゲーションへの組み込み

生成されたページは通常のコンテンツなので、ディレクトリ自動ナビゲーションに
そのまま現れます。意図した場所に置きたい場合は、自分の
[`nav`](../configuration.md#nav) にインデックスを指定します。

=== "YAML"
    ```yaml title="bxsites.yaml"
    nav:
      - title: リファレンス
        children:
          - api/docbox/index.md
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"nav": [
    		{ "title": "リファレンス", "children": [ "api/docbox/index.md" ] }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [[nav]]
    title = "リファレンス"
    children = [ "api/docbox/index.md" ]
    ```

## 意図的に行わないこと

- **クラス間のリンクは張りません。** `extends`/`implements` の対象や
  シグネチャに現れる型は、そのクラスに生成ページがあってもインラインコード
  として表示されます。
- **継承したメンバーは扱いません。** DocBox 自身の JSON 出力と同じく、その
  クラスが自ら宣言したものだけです。
- **ナビゲーションの自動配線は行いません。** ページは `pagePathPrefix` の
  下に置かれ、配置は利用者が決めます。

## メタデータの出どころ

DocBox の JSON ストラテジーは、クラスの名前・パッケージ・種別・`extends`・
関数をシリアライズしますが、宣言された `property` ブロック、実装している
インターフェース、クラスレベルのアノテーション、関数ごとの `@return`
テキストは含めません。この4つは BoxLang のリファレンスでは重要で、とくに
プロパティは `accessors` のフィールドと WireBox の `inject` をすべて担って
います。そのため bx-sites は、DocBox 自身が使ったのと同じクラスメタデータ
からこれらを読み直し、レンダリング前に統合します。二重に解析することは
ありません。

## Gradle や Maven から

どちらのビルドプラグインもこのジェネレーターを提供しています。BoxLang/CFML
のクラスを含む JVM プロジェクト向けです。
[Gradleプラグイン](gradle-plugin.md#boxlang-doc-generation) と
[Mavenプラグイン](maven-plugin.md#boxlang-doc-generation) を参照してくださ
い。

ColdBox アプリケーションのドキュメント化は別の verb です。
[ColdBoxアプリケーション](coldbox.md) を参照してください。
