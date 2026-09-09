---
title: コントローラースキャン出力例
order: 6.7
icon: phosphor-duotone:signpost
tags: [ガイド, java, spring-boot, controller-scan, 例]
---

# コントローラースキャン出力例

`bxSitesControllerScanDoc`（Gradle）/ `bxsites:controller-scan`（Maven）
が実際に生成するものの実例です - 自分のプロジェクトでこれを有効にする
方法は [Gradle プラグイン](gradle-plugin.md#spring-boot-doc-generation)
または [Maven プラグイン](maven-plugin.md#spring-boot-doc-generation) を
参照してください（OpenAPI 生成がオフのときはデフォルトで有効です）。

フォークされた JVM の中で、自分のプロジェクトの実際のクラスパス上にある
本物の Spring アノテーション型を使い、すでにコンパイル済みのクラスを
リフレクションでスキャンして Spring MVC コントローラーを探します。
次のコントローラーがあるとすると:

```java title="com/example/BookController.java"
package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    public String list() {
        return "[]";
    }

    @GetMapping("/{id}")
    public String getOne(@PathVariable String id) {
        return "{}";
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "{}";
    }
}
```

...ジェネレーターは `api/controllers/com/example/BookController.md` を、
次のフロントマターで書き出します:

```yaml title="生成されるフロントマター"
---
title: "BookController"
tags: [api, controllers]
---
```

そしてこちらのボディを、コードブロックとしてではなく実際にレンダリング
した状態で以下に再現しています - これは生成されたページの実際のボディ
そのもの、ライブであり、プラグイン自身のテストスイートでもまさにこの
コントローラークラスに対して確認済みです:

---

# BookController

`com.example.BookController`

| Method | Path | Handler |
|---|---|---|
| GET | `/api/books` | `String list()` |
| POST | `/api/books` | `String create(String)` |
| GET | `/api/books/{id}` | `String getOne(String)` |

---

クラスレベルの `@RequestMapping("/api/books")` というベースパスが各
メソッド自身のマッピングと組み合わされて、各行の完全なパスを生成して
いる点に注目してください - これは推測による文字列連結ではなく、実際の
パス合成です。行の順序は、コンパイル済みクラスに対する
JVM 自身のリフレクションからそのまま来ています（必ずしもソース順とは
限りません - この例のために整えたものではなく、実際の挙動です）。

エンドポイントは意図的に素の Markdown パイプテーブルとして描画されま
す。bx-sites は 10 行以上のテーブルに独自のライブフィルターボックスを
付けるため、エンドポイントの多いコントローラーは検索とフィルターを自動
的に得られます。同時にパイプテーブルは生の Markdown のままでも読みやす
く、あらゆるテーマで表示され、検索インデックスにもそのまま入ります。
メンバーがテーブル行ではなくセクションである
[Javadoc](java-javadoc-example.md) のページには、ジェネレーター自身の
Alpine.js チップツールバーが付いています。

## これがカバーしないもの

Javadoc ジェネレーターと同じ精神で、**意図的にスコープを絞って**い
ます: 直接 `@Controller`/`@RestController` が付けられたクラスのみが
認識されます（どちらかの上に構築された独自のステレオタイプアノテー
ションは認識されません）。直接 `@RequestMapping`/`@GetMapping`/
`@PostMapping`/`@PutMapping`/`@DeleteMapping`/`@PatchMapping` が付けら
れたメソッドのみが認識されます。ネストされたクラスはスキップされます。
そしてリフレクションにはソースコードレベルのドキュメントコメントへの
アクセス手段がないため、上記の行にある「List books」のようなエンド
ポイントごとの説明文は存在せず、メソッド・パス・ハンドラーのシグネ
チャのみとなります。完全なスコープについては上記でリンクした
Gradle/Maven ガイドを参照してください。
