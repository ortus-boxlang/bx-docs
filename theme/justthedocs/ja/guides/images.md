---
title: レスポンシブ画像
order: 5
icon: phosphor-duotone:image
tags: [ガイド, 画像, パフォーマンス]
---

# レスポンシブ画像

`docs/assets/` 配下の対象となる画像はすべて自動的にリサイズ/WebP バリアントが
生成され、ページ内の一致する `<img>` はすべてレスポンシブな `<picture>` に
書き換えられます - 新しい Markdown 構文は不要で、有効にするための設定も必要ありません。
これは [bx-image](https://github.com/ortus-boxlang/bx-image) の上に構築されており、
bx-markdown/bx-esapi/bx-yaml と並ぶ必須の依存関係です
（[はじめに](../getting-started.md#インストール) を参照）。

## 仕組み

画像は通常通りに記述します - Markdown 構文でも生の HTML でも、
[ページリンク](markdown.md) と同じくページからのファイル相対パスで指定します:

```markdown title="Example"
![ビルドされたばかりのサイト](../assets/screenshot.png)
```

ビルド時、`screenshot.png` は自身の幅より狭い設定済みの各幅にリサイズされ
（アップスケールは行われません）、同サイズの WebP 再エンコードも生成され、
ビルド済みページには次が出力されます:

```html title="Rendered output" linenums="1"
<picture>
	<source type="image/webp" srcset="/assets/screenshot-400w.a3f9c2e1.webp 400w, /assets/screenshot-800w.a3f9c2e1.webp 800w, ...">
	<img src="/assets/screenshot.png" srcset="/assets/screenshot-400w.a3f9c2e1.png 400w, /assets/screenshot-800w.a3f9c2e1.png 800w, ..." sizes="(min-width: 800px) 800px, 100vw" alt="ビルドされたばかりのサイト">
</picture>
```

ブラウザは `sizes` を満たす最小のバリアントを選び、対応していれば WebP を、
そうでなければプレーンな元の `src`（以前とまったく同じように配信されます）を
選びます。記述した他のすべての属性 - `alt`、`class`、その他何であれ - は、
書き換えられた `<img>` にそのまま引き継がれます。

自身の幅より狭い設定済み幅がない画像（小さなアイコンなど）でも、
`assets.images.formats` に `"webp"` が含まれていればフルサイズの WebP
再エンコードは生成されます - レスポンシブなブレークポイントを提供しなくても、
ファイルサイズの実質的な削減になります。

## キャプション、配置、フレーミング

キャプション、フレーム、複数画像のギャラリーは、すべて単なるブロックレベルの
HTML です - bx-markdown/Flexmark はこれを完全にそのまま通過させる
（CommonMark 自身の「HTML ブロック」規則）ため、bx-sites 独自の構文はまったく
必要ありません:

```markdown title="Example" linenums="1"
<figure>
  <img src="../assets/screenshot.png" alt="ビルド結果">
  <figcaption>ビルドされたばかりのサイト</figcaption>
</figure>

<div data-with-frame="true">
  <img src="../assets/screenshot.png" alt="フレーム付き">
</div>

<div class="bxsites-gallery">
  <img src="../assets/one.png" alt="">
  <img src="../assets/two.png" alt="">
  <img src="../assets/three.png" alt="">
</div>
```

`x-data`/`x-show`/`@click` などその他の Alpine.js 属性についても同様です -
[Alpine.js によるインタラクティビティ](interactivity.md) を参照してください。

## リサイズされないもの

- **SVG** - すでに解像度非依存であるため、無変更でそのままコピーされます。
- **アニメーション GIF** - bx-image のリサイズ処理はフレームを認識しないため、
  リサイズすると単一フレームに潰れてしまいます。この機能が存在する以前と
  まったく同じく、無変更でそのままコピーされます。
- **`docs/assets/` 以外にあるもの** - リモート画像 URL
  （`<img src="https://...">`）は完全に手つかずのまま残されます。
  [`extraCss`/`extraJs`](../configuration.md#extracss--extrajs) が絶対 URL を
  「そのまま使用」として扱うのと同じです。
- **すでに設定済みのどの幅よりも狭い画像** - 生成するものが何もないため、
  プレーンな `<img>` は以前とまったく同じようにレンダリングされます
  （`"webp"` が有効な場合を除きます。上記参照）。

AVIF 対応もまだありません - 本稿執筆時点で bx-image はこの形式を書き出しません。
WebP だけでもサイズ削減の大部分は得られ、対応するツール/ブラウザの幅もはるかに
広いです。bx-image が上流で AVIF に対応すれば、この点は見直す価値があります。

## 無効化する

=== "YAML"
    ```yaml title="bxsites.yaml"
    assets: { images: { enabled: false } }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "assets": { "images": { "enabled": false } } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [assets]

    [assets.images]
    enabled = false
    ```

プレーンで未加工の `docs/assets/**` コピーにフォールバックします -
この機能が存在する以前にすべての画像が扱われていたのとまったく同じです。

## 自分でブレークポイントを選ぶ

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      images:
        widths: [ 480, 960, 1440 ]
        formats: [ webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"images": {
    			"widths": [480, 960, 1440],
    			"formats": ["webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [assets]

    [assets.images]
    widths = [ 480, 960, 1440 ]
    formats = [ "webp" ]
    ```

`widths` のデフォルトは `[400, 800, 1200, 1600]`、`formats` のデフォルトは
`["original", "webp"]` です - `"original"` を外すと、ソース形式でのリサイズ済み
コピー生成を完全にスキップできます（`<img>` のフォールバックとしてプレーンな
フルサイズの元画像は引き続き保持されます）。`"webp"` を外すと WebP の `<source>`
自体をまるごとスキップします。`assets.images` のすべてのキーについては
[設定](../configuration.md#assets) を参照してください。

## CSS/JS バンドリング

`extraCss`/`extraJs` も同じ方法でバンドルされ、デフォルトで有効です
（`assets.bundle`）:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/a.css, assets/b.css ]
    extraJs: [ assets/app.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/a.css", "assets/b.css"],
    	"extraJs": ["assets/app.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/a.css", "assets/b.css" ]
    extraJs = [ "assets/app.js" ]
    ```

エントリごとに 1 つの `<link>`/`<script>` タグを出す代わりに、フィンガープリント
付きの `assets/bundle.<hash>.css`（記載順）と `assets/bundle.<hash>.js` を
それぞれ 1 つずつビルドします。CSS はコメントが除去され空白が圧縮されますが、
JS では意図的に安全で構造的な空白整理のみを行い、コメント除去は一切行いません -
単純な正規表現では文字列内の `//`（`"http://example.com"` など）と本物の
コメントを区別できず、判定を誤るとプロジェクト自身のスクリプトを黙って
壊してしまうためです。これはバンドリングと軽い整理であって、本格的な
ミニファイアではありません - これだけでは不十分な場合、Java 製のミニファイ
ライブラリを同梱するのは妥当な将来のアップグレードです。

バンドリングは、リスト内の**すべての**エントリがローカルのプロジェクトファイル
である場合にのみ有効化されます。外部 URL（CDN リンク）が 1 つでも混ざっていると、
プロジェクトが依存している CSS のカスケード順を黙って並べ替えてしまう危険を
冒すよりも、リスト全体を今日のまったく同じ URL ごとの挙動にフォールバックさせます:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/custom.css, "https://cdn.example.com/lib.css" ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/custom.css", "https://cdn.example.com/lib.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/custom.css", "https://cdn.example.com/lib.css" ]
    ```

この機能が存在する以前とまったく同じく、バンドルされない 2 つの個別な
`<link>` タグとしてレンダリングされます。

## フィンガープリンティングとキャッシュ

生成されるすべての画像バリアントと CSS/JS バンドルはコンテンツハッシュで
命名されます（`assets.fingerprint`、デフォルトで有効）- ビルドは、ソースの
コンテンツが実際に変更されたときにのみバリアントのファイル名を変える、という
ことです。これにより、静的ホストで遠い未来を指す `Cache-Control` ヘッダーを
安全に設定できます。`docs/assets/` 配下のプロジェクト自身のオリジナルファイルは、
いずれの場合もそのままのプレーンな名前を保ちます - フィンガープリントが付くのは
パイプラインが生成した出力だけなので、`::: file` のダウンロードカードや、
ファイル名で画像を直接指すプレーンなリンクは、これまでどおり動作し続けます。

生成されたすべてのバリアントは、プロジェクト自身の `.cache/images/`
（[`bxSites clean`](../cli-reference.md#clean) で `site/` と共に削除されます）
配下にディスクキャッシュされ、*ソース*画像自身のコンテンツハッシュをキーに
しています。そのため、`build` の再実行（バージョン/ロケールツリーごとに 1 回、
すべて同じ `docs/assets/` を共有）や、無関係な編集後の `bxSites serve` が、
プロジェクト内の変更されていないスクリーンショットまで毎回デコード/リサイズ/
再エンコードすることはなく、実際に変更されたものだけが処理されます。
