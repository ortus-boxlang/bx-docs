---
title: Plugins
order: 6
icon: phosphor-duotone:puzzle-piece
tags: [anleitungen, plugins]
---

# Plugins

Ein BX-Sites-Plugin ist nichts weiter als ein weiteres BoxLang-Modul -
eine eigene `box.json` + `ModuleConfig.bx`, installiert als Geschwister
von `bx-sites` in derselben Laufzeitumgebung (`box install` ins Projekt,
genauso wie `bx-markdown`/`bx-esapi` bereits installiert sind). Keine
Plugin-API zum Importieren, keine separate Registry - BoxLangs eigenes
Modulsystem *ist* das Plugin-System.

Ein Modul allein zu installieren aktiviert es allerdings nie als Plugin -
ein Projekt bindet eines explizit über den BoxLang-Modulnamen im Array
[`plugins`](../configuration.md#plugins) von `bxsites.yaml` ein:

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    plugins = [ "myBxSitesPlugin" ]
    ```

## Ein veröffentlichtes Plugin installieren

Ein auf ForgeBox veröffentlichtes Plugin installiert sich mit nichts
weiter als der `bxSites`-Binary selbst - kein `box`/CommandBox nötig.
Durchsuche die bereits veröffentlichten Pakete unter der Kategorie
[`bxsites-plugins`](https://www.forgebox.io/type/bxsites-plugins) auf
ForgeBox:

```bash title="Usage"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

Das lädt das ZIP des Pakets von ForgeBox herunter und entpackt es nach
`boxlang_modules/bx-sites-plugin-analytics/` im Projekt-Wurzelverzeichnis
- BoxLangs eigene automatisch geladene lokale-Modul-Konvention (jeder
Modulordner dort wird genauso erkannt wie ein projektlokales
`node_modules/` bei npm), sodass es ohne jeden
`BOXLANG_HOME`-/globalen Installationsschritt in der laufenden
BoxLang-Modul-Registry aktiv ist. `install:plugin` lädt es sofort in die
Laufzeitumgebung und gibt seinen echten registrierten
Modul-Mapping-Namen zurück (der, laut dem Hinweis unten, nicht immer
mit dem ForgeBox-Slug übereinstimmt) - füge *diesen* Namen zum
`plugins`-Array von `bxsites.yaml` hinzu, um es zu aktivieren, genau wie
bei jedem anderen installierten Modul. Siehe
[`install:plugin`](../cli-reference.md#installplugin) in der
CLI-Referenz.

## Ein Plugin schreiben

Ein Plugin-Modul braucht genau eine Sache zusätzlich zu der üblichen
`box.json`/`ModuleConfig.bx`, die ein BoxLang-Modul bereits hat: eine
Klasse `models/BxSitesPlugin.bx`. Jede Methode darauf ist optional -
implementiere nur die Hooks, die du brauchst, BxSites prüft vor jedem
Aufruf, ob sie existiert:

```bx title="models/BxSitesPlugin.bx" linenums="1"
// models/BxSitesPlugin.bx
class {

	struct function onConfig( required struct config ) {
		// Mutate/return the site config, right after bxsites.yaml is loaded.
		return arguments.config
	}

	string function onPageMarkdown( required string markdown, required struct page, required struct config ) {
		// Mutate a page's raw markdown before conversion - the same
		// pre-processing seam BxSites' own content tabs/math/code
		// annotations use internally (TabsProcessor.bx et al.).
		return arguments.markdown
	}

	string function onPageHtml( required string html, required struct page, required struct config ) {
		// Mutate a page's rendered HTML after conversion.
		return arguments.html
	}

	array function onNav( required array nav, required struct config ) {
		// Mutate the nav tree (NavBuilder.build()'s own shape: an array of
		// { title, url, order, children } nodes).
		return arguments.nav
	}

	void function onBuildComplete( required string siteDir, required struct config ) {
		// Fires once, after everything is written to siteDir - no return value.
	}

}
```

Hooks laufen in der Reihenfolge, in der sie im eigenen `plugins`-Array
von `bxsites.yaml` stehen, und (außer bei `onBuildComplete`) ersetzt der
Rückgabewert jedes Hooks das, was der nächste Hook (oder BxSites selbst)
sieht - ein Plugin muss nur das zurückgeben, was es erhalten hat, wenn es
nichts zu ändern hat.

`onPageMarkdown`/`onPageHtml` laufen einmal pro Seite, für jeden Doc-Baum,
den BxSites baut (den Haupt-`docs/`-Baum und jeden
`docs/versions/<name>/`-Baum). `onConfig`/`onNav`/`onBuildComplete`
werden, wo relevant, auch vom eigenständigen `search-index`-Verb
angewendet (`onConfig`, da es `markdown`/andere Einstellungen ändern
kann, von denen der Index-Build abhängt).

## Wann jeder Hook feuert

```mermaid
sequenceDiagram
    participant Build as build verb
    participant Plugin as your plugin
    Build->>Plugin: onConfig(config)
    Build->>Build: build the nav tree
    Build->>Plugin: onNav(nav, config)
    loop every page
        Build->>Plugin: onPageMarkdown(markdown, page, config)
        Build->>Build: Markdown() + built-in extensions
        Build->>Plugin: onPageHtml(html, page, config)
    end
    Build->>Build: write site/
    Build->>Plugin: onBuildComplete(siteDir, config)
```

## Ein minimales Beispiel

`examples/hello-plugin/` in diesem Repo ist ein vollständiges,
funktionierendes Plugin-Modul - so, wie es ist, per `box install`
installierbar - das jeder Seite einen Kommentar
`<!-- rendered by hello-plugin -->` hinzufügt und nach Abschluss des
Builds eine Build-Zusammenfassungszeile an `site/hello-plugin.txt`
anhängt. Nutze es als Ausgangs-Skelett, oder lies es dir als
durchgearbeitetes Beispiel für den Ordneraufbau durch:

```text title="hello-plugin/ layout"
hello-plugin/
├── box.json              # boxlang.moduleName is what bxsites.yaml's [plugins] references
├── ModuleConfig.bx        # a normal, otherwise-empty BoxLang module descriptor
└── models/
    └── BxSitesPlugin.bx    # onPageHtml() + onBuildComplete()
```

## Fehler

- `BxSites.PluginNotFound` - ein Name im `plugins`-Array von
  `bxsites.yaml` ist kein installiertes/aktiviertes BoxLang-Modul.
- `BxSites.InvalidPlugin` - das Modul existiert, hat aber keine Klasse
  `models/BxSitesPlugin.bx`.
