---
title: CLI プロバイダー
order: 6.2
icon: phosphor-duotone:terminal-window
tags: [ガイド, プラグイン, cli]
---

# CLI プロバイダー

[プラグイン](plugins.md)は*ビルド*ライフサイクル - 設定、nav、ページの
markdown/HTML、ビルド後処理 - にフックします。**CLI プロバイダー**は
*コマンド*ライフサイクルのための姉妹拡張ポイントです。インストール済みで有効化された
BoxLang モジュールが、`bx-sites` 自体には手を加えずに、独自の
`bxSites <verb>` コマンドを登録できるようにします。

プラグインと同じ有効化モデルです - モジュールは `bxsites.yaml` 独自の
[`plugins`](../configuration.md#plugins) 配列を通じて、名前でオプトインします:

```yaml title="bxsites.yaml"
plugins: [ myBxSitesAddon ]
```

モジュールは `models/BxSitesPlugin.bx`、`models/BxSitesCliProvider.bx`、
両方、またはどちらも実装しないことができます - モジュールのインストール/有効化は
1つのステップであり、どちらのコントラクトを実装するかによって、実際に何を拡張するかが
決まります。

## CLI プロバイダーを書く

CLI プロバイダーに必要なのは、通常の `box.json`/`ModuleConfig.bx` に加えて
たった一つだけです: 単一の `verbs()` メソッドを公開する
`models/BxSitesCliProvider.bx` クラスで、動詞名 → ディスパッチ情報の
struct を返します:

```bx title="models/BxSitesCliProvider.bx" linenums="1"
// models/BxSitesCliProvider.bx
class {

	struct function verbs() {
		return {
			"cloud:publish" : {
				class       : "models.cli.cloud.Publish@myBxSitesAddon",
				description : "Build (if needed) and publish via the configured deploy target"
			},
			"cloud:status" : {
				class       : "models.cli.cloud.Status@myBxSitesAddon",
				description : "Show license/entitlement and last deploy status"
			}
		}
	}

}
```

各ディスパッチクラスは、`bx-sites` 自身の `models/cli/` 配下にあるコアの動詞クラスと
まったく同じ形 - `{ exitCode, message }` を返す
`struct function run( struct options )` - に従います:

```bx title="models/cli/cloud/Publish.bx" linenums="1"
class {
	struct function run( struct options ) {
		// arguments.options carries the same parsed-flags-plus-projectRoot
		// shape every core verb receives - see the CLI reference's
		// "How dispatch works" section.
		return { exitCode : 0, message : "Published #arguments.options.projectRoot#" }
	}
}
```

### `@myBxSitesAddon` サフィックスは必須です

`"models.cli.cloud.Publish"` のような単純なドット区切りパスは、`bx-sites`
自身のモジュールルートに対してのみ解決されます - コアの動詞が
`models/cli/Build.bx` などを参照する方法はこれですが、これでは別のモジュールに
**到達できません**。プロバイダー自身の動詞クラスは、上記の通り、常に自分自身の
モジュールの `@<mapping>` サフィックスを自分で付ける必要があります。これはまた、
プロバイダーが `bx-sites` 自身のリテラルな動詞テーブルに紛れ込むことができない理由でも
あります - クラスパスは自分自身のモジュールを明示的に指定しなければなりません。

## 動詞名: シングルトークンと2トークン（「複合」）

動詞名は、コアがすでに `post:new`/`i18n:status`/`page:rename` に使っているのと
同じ規約で、コロンで結合された1つの文字列として登録されます。`bxSites` は、
同じ登録に対する糖衣構文として、**スペースで区切られた2つの argv トークン**の
同等表現も受け付けます - `"cloud:publish"` が登録済みの動詞名であれば、
`bxSites cloud publish` と `bxSites cloud:publish` はまったく同じように
ディスパッチされます。学ぶべき別の2単語登録メカニズムはありません。
`"cloud:publish"` を登録すれば、両方の書き方が無料で動きます。

## 優先順位と失敗時の挙動

- **コアが常に勝ちます。** プロバイダーが `bx-sites` 自身の組み込み動詞の
  いずれかと衝突する動詞名を登録した場合、コアの動詞がディスパッチされ、
  プロバイダー側のエントリは黙って無視されます - プロバイダーは新しいコマンドを
  追加できますが、既存のものを覆い隠すことは決してできません。
- **2つのプロバイダー間で衝突した場合は最初のものが勝ちます。** 2つの異なる
  有効化済みモジュールが同じ動詞名を登録した場合、`bxsites.yaml` の `plugins`
  配列でより先に列挙されている方が勝ちます。
- **ディスカバリーがコアのディスパッチを壊すことは決してありません。** 欠落または
  不正な形式の `bxsites.yaml`、まだ存在しないプロジェクト（例えばどのプロジェクトの
  外でも `--help` を実行する場合）、`plugins` に列挙されているが自身の
  `BxSitesCliProvider.bx` を持たないモジュール、あるいは `verbs()` がエラーを
  スローするプロバイダー - これらはいずれもエラーとして扱われません。すべて、
  有効化されていないプラグインと同じように扱われます: コアの動詞テーブルは単に
  そのまま変更されず、すべての組み込み `bxSites` コマンドはそれ自体で動作し続けます。

## 最小限の例

```text title="myBxSitesAddon/ の構成"
myBxSitesAddon/
├── box.json                          # boxlang.moduleName is what bxsites.yaml's [plugins] references
├── ModuleConfig.bx                    # a normal BoxLang module descriptor
└── models/
    ├── BxSitesPlugin.bx               # optional - build-lifecycle hooks, see plugins.md
    ├── BxSitesCliProvider.bx          # verbs()
    └── cli/
        └── cloud/
            ├── Publish.bx             # run( options )
            └── Status.bx              # run( options )
```

同じモジュールのビルドライフサイクル側については[プラグイン](plugins.md)を、
コア自身が同梱するすべての動詞については
[CLI リファレンス](../cli-reference.md)を参照してください。
