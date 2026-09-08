---
title: Javadoc 出力例
order: 6.6
icon: phosphor-duotone:code
tags: [ガイド, java, spring-boot, javadoc, 例]
---

# Javadoc 出力例

`bxSitesJavadocDoc`（Gradle）/ `bxsites:javadoc`（Maven）が実際に生成する
ものの実例です - 自分のプロジェクトでこれを有効にする方法は
[Gradle プラグイン](gradle-plugin.md#spring-boot-doc-generation) または
[Maven プラグイン](maven-plugin.md#spring-boot-doc-generation) を参照して
ください。

自分のプロジェクトの `.java` ソースに向けると、すべての public な
トップレベル型を走査し、型ごとに1ページを生成します。次のクラスが
あるとすると:

```java title="com/example/Book.java"
package com.example;

/**
 * A single book in the shelf, immutable once created. Two books are
 * considered equal only by reference, not by content.
 */
public class Book {

    /**
     * Creates a book with the given title and author.
     *
     * @param title  the book's title
     * @param author the book's author
     */
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    /**
     * Returns a copy of this book with a new title.
     *
     * @param newTitle the new title
     * @return a copy of this book with the given title
     */
    public Book withTitle(String newTitle) {
        return new Book(newTitle, author);
    }
}
```

...ジェネレーターは `api/javadoc/com/example/Book.md` を、次の
フロントマターで書き出します:

```yaml title="生成されるフロントマター"
---
title: "Book"
summary: "A single book in the shelf, immutable once created."
tags: [api, javadoc]
---
```

そしてこちらのボディを、コードブロックとしてではなく実際にレンダリング
した状態で以下に再現しています - これは生成されたページの実際のボディ
そのもの、ライブです:

---

# Book

`com.example.Book`

A single book in the shelf, immutable once created. Two books are
considered equal only by reference, not by content.

## Constructors

### `Book(java.lang.String title, java.lang.String author)`

Creates a book with the given title and author.

- **Parameter** `title` - the book's title
- **Parameter** `author` - the book's author

## Methods

### `com.example.Book withTitle(java.lang.String newTitle)`

Returns a copy of this book with a new title.

- **Parameter** `newTitle` - the new title
- **Returns** a copy of this book with the given title

---

クラスの最初の文がフロントマターの `summary` になり、ページのボディには
（両方の文を含む）ドキュメントコメント*全体*が載っている点に注目して
ください - これはこの例のために単純化したものではなく、
`DocCommentTree` による実際の「最初の文」抽出です。

## これがカバーしないもの

**v1 では意図的にスコープを絞っており、完全な Javadoc → Markdown
コンバーターではありません** - これは3つの Spring Boot ジェネレーターの
中で最も重いものです。ネストされた型やパッケージプライベートな型
（およびフィールド）は完全にスキップされます。使用されるのは常に各
メンバー*自身*のドキュメントコメントのみで、継承されたものは使われま
せん。ドキュメントコメント内のインライン HTML は Markdown に変換される
のではなく取り除かれます。`{@link}`/`{@see}` はページ間のハイパーリンク
解決なしにインラインコードとしてレンダリングされます。索引/ナビゲー
ションページは生成されません。`record` の場合はコンパイラが生成する
アクセサ/`toString`/`equals`/`hashCode` も含まれ、標準の `javadoc`
ツール自身の挙動と一致します - 上記の `Book` は、この例を一般的な
ケースに絞るためにあえて通常のクラスにしています。完全な一覧は上記で
リンクした Gradle/Maven ガイドを参照してください。
