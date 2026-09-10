---
title: OpenAPI 出力例
order: 6.5
icon: phosphor-duotone:plug
tags: [ガイド, java, spring-boot, openapi, 例]
---

# OpenAPI 出力例

`bxSitesOpenApiDoc`（Gradle）/ `bxsites:openapi`（Maven）が実際に生成する
ものの実例です - 自分のプロジェクトでこれを有効にする方法は
[Gradle プラグイン](gradle-plugin.md#spring-boot-doc-generation) または
[Maven プラグイン](maven-plugin.md#spring-boot-doc-generation) を参照して
ください。

`specFile` を自分の springdoc が生成した OpenAPI 仕様に向けると、
ジェネレーターはそれを `assets/openapi/` にコピーし、次にちょうどこの
フロントマターとボディを持つページを書き出します:

```yaml title="生成されるフロントマター"
---
title: "Bookshelf API"
tags: [api, openapi]
---
```

```markdown title="生成されるボディ"
# Bookshelf API

::: openapi src="assets/openapi/openapi.yaml" title="Bookshelf API"
:::
```

そしてこちらが、まさにこのページ上で、この
リポジトリがすでに同梱している小さな「Bookshelf API」仕様
（[`assets/openapi/example.yaml`](../../assets/openapi/example.yaml) -
[OpenAPI / Swagger](openapi.md) 自身が示しているものと同じファイル）を
使って実際にレンダリングされたものです:

---

# Bookshelf API

::: openapi src="assets/openapi/example.yaml" title="Bookshelf API"
:::

---

これはスクリーンショットでもモックアップでもありません - bx-sites が
同梱する本物のインタラクティブな Swagger UI ウィジェットが、上記の
仕様ファイルによって動いています。自分のプロジェクトの仕様に向ければ、
`bxSitesOpenApiDoc`/`bxsites:openapi` はバイト単位で同一の出力を生成し
ます。

## これがカバーしないもの

Swagger UI は完全にクライアント側でレンダリングされるため、
（「List books」のような）エンドポイントごとのテキストは bx-sites 自身
の検索インデックスには一切届きません - インデックスされるのはこのページ
自体のタイトル/フロントマターだけです。v1 の残りのスコープについては
上記でリンクした Gradle/Maven ガイドを参照してください（springdoc は
引き続き自分で仕様ファイルを生成する必要があります。`openapi: true` も
`bxsites.yaml` で、手動または `autoPatchConfig` 経由のいずれかで、
引き続き設定しておく必要があります）。
