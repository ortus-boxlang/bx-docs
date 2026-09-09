---
title: Suche
order: 2
icon: phosphor-duotone:magnifying-glass
tags: [anleitungen, suche]
---

# Suche

BxSites liefert standardmäßig einen Suchprovider mit und lässt sich über
[`searchProvider`](../configuration.md#searchprovider) in `bxsites.yaml`
auf andere ausrichten - `search: true`/`false` bleibt dabei der zentrale
An-/Aus-Schalter, unabhängig davon, welcher Provider aktiv ist.

## Local (der Standard)

Die Suche von BxSites ist vollständig statisch und clientseitig -
derselbe Ansatz, den [mkdocs](https://www.mkdocs.org/) standardmäßig
verwendet: ein einmal zur `build`-Zeit erstellter Index, und
[lunr.js](https://lunrjs.com/) übernimmt die eigentliche Suche im Browser
des Besuchers. Es gibt keinen Server, keine Datenbank und keinen
externen Suchdienst.

## Wie es funktioniert

1. Zur `build`-Zeit durchläuft `SearchIndexer` jede nicht versteckte Seite
   und schreibt `site/search-index.json`: ein Eintrag pro Seite mit ihrem
   `title`, ihrer `url`, den `tags` aus der Frontmatter, dem Text jeder
   Überschrift auf der Seite und einer gekürzten Klartext-Kopie ihres
   Inhalts (HTML-Tags entfernt).
2. Das `search.bxm`-Partial jedes Themes rendert eine Suchbox;
   `layout.bxm` bindet sie (und die Skripte `lunr.js` + gemeinsames
   `search.js`) nur ein, wenn `search` in `bxsites.yaml` `true` ist und
   `searchProvider.provider` `"local"` ist (der Standard - siehe
   [Andere Provider](#andere-suchprovider) unten dafür, was sich mit
   einem anderen ändert).
3. Im Browser lädt das gemeinsame `assets/search.js`-Widget einmalig
   `search-index.json`, baut daraus einen `lunr`-Index (`title` am
   höchsten gewichtet, dann `tags` aus der Frontmatter, dann `headings`,
   dann der reine Fließtext) und durchsucht ihn bei jedem Tastendruck neu
   - kein Netzwerk-Roundtrip pro Anfrage.

## Tastaturkürzel

- **`/`** fokussiert die Suchbox in der Seitenleiste von überall auf der
  Seite (außer du tippst bereits in ein anderes Feld) - dieselbe
  Konvention, die
  [mkdocs-material](https://squidfunk.github.io/mkdocs-material/)
  verwendet. Die Suchbox zeigt einen kleinen `Ctrl K`/`⌘K`-Hinweis
  (plattformerkannt), damit das Tastenkürzel unten auffindbar ist.
- **Cmd/Ctrl+K** öffnet stattdessen ein eigenständiges,
  Command-Palette-artiges Overlay - ein zentriertes Modal über einem
  Hintergrund, vollständig in JS gebaut (keine Theme-Template-Änderungen
  nötig) und von jedem integrierten Theme gemeinsam genutzt. Pfeil-hoch/
  -runter bewegt eine Hervorhebung über die Ergebnisse, **Enter**
  navigiert zum hervorgehobenen Ergebnis, und **Escape** (oder ein Klick
  auf den Hintergrund) schließt es - dieselbe "Quick Find"/⌘K-Konvention,
  die Algolia DocSearch, Pagefind, VitePress und Docusaurus alle
  teilen.
- **`Escape`** schließt außerdem das eigene Ergebnis-Dropdown der
  Seitenleisten-Box und entfernt deren Fokus, unabhängig von der Palette
  oben.

Die Palette nutzt exakt denselben, bereits aufgebauten `lunr`-Index
weiter, den auch das Seitenleisten-Widget selbst baut, statt
`search-index.json` ein zweites Mal abzurufen - nur je für `local` (den
Standard-Provider) verfügbar; `algolia` erhält sein eigenes Cmd+K
kostenlos von DocSearch selbst (`keyboardShortcuts` ist standardmäßig
`true`), und `pagefind` bekommt Cmd+K von `layout.bxm` verdrahtet, um
seine eigene `PagefindUI` zu fokussieren, da diese Bibliothek es nicht
von selbst bindet - keiner der beiden öffnet die eigene Palette dieses
Moduls.

## Ausschalten

=== "YAML"
    ```yaml title="bxsites.yaml"
    search: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "search": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    search = false
    ```

Überspringt den Aufbau von `search-index.json` vollständig und
überspringt die Suchbox, das vendorierte `lunr.js`-Skript sowie das
gemeinsame `search.js`-Widget auf jeder gerenderten Seite - ein Projekt
mit deaktivierter Suche liefert überhaupt nichts Suchbezogenes aus. Das
ist der zentrale Schalter - er gilt unabhängig davon, welcher
`searchProvider` konfiguriert ist.

## Nur den Index neu aufbauen

```bash frame="terminal" title="Terminal"
bxSites search-index
```

Nützlich, wenn du nur `search-index.json` auffrischen musst - `build`
erledigt das bereits als einen seiner eigenen Schritte, du musst dies
also nach einem normalen Build nicht separat ausführen. Läuft nur für
Provider, die den lokalen Index nutzen (`"local"`, sowie jeder Provider,
den bx-sites sonst nicht kennt) - es ist ein No-op (`skipped: true`),
wenn `searchProvider.provider` `"algolia"` oder `"pagefind"` ist, da
keiner der beiden ihn jemals nutzt.

## Algolia

Setze `searchProvider.provider` auf `"algolia"`, um die Suchbox gegen
[Algolia DocSearch](https://docsearch.algolia.com/) auszutauschen -
dieselbe crawler-gehostete Suche, die mkdocs-material, VitePress,
Starlight und Docusaurus alle unterstützen:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: algolia
      algolia:
        appId: ABC123
        apiKey: a1b2c3d4e5f6...
        indexName: my-docs
        insights: false
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "algolia",
    		"algolia": {
    			"appId": "ABC123",
    			"apiKey": "a1b2c3d4e5f6...",
    			"indexName": "my-docs",
    			"insights": false
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "algolia"

    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    insights = false
    ```

`appId`, `apiKey` und `indexName` sind erforderlich - `apiKey` ist der
**reine Such**-öffentliche API-Schlüssel, den DocSearch dir gibt (nie
ein Administrator-Schlüssel; er wird direkt in jede gerenderte Seite
ausgeliefert). `insights` (standardmäßig `false`) schaltet DocSearchs
eigenes Klick-/Konversions-Analytics ein.

Mit aktivem `algolia`:

- Es wird keine `search-index.json` gebaut, und das gemeinsame
  `lunr.js`/`search.js`-Widget wird nicht ausgeliefert - Algolia liefert
  Ergebnisse aus seinem eigenen gehosteten Index, befüllt vom
  [DocSearch-Crawler](https://docsearch.algolia.com/docs/what-is-docsearch/)
  oder deiner eigenen
  [Algolia-Crawler](https://www.algolia.com/products/search-and-discovery/crawler/)-Konfiguration,
  nicht von irgendetwas, das BxSites zur Build-Zeit schreibt. Du musst
  die Website trotzdem separat bei DocSearch registrieren (oder deinen
  eigenen Crawler betreiben) - BxSites verdrahtet nur das Client-Widget.
- Jedes integrierte Theme rendert stattdessen einen leeren
  `#bxsites-search-algolia`-Container, und `layout.bxm` lädt
  `@docsearch/css`/`@docsearch/js` von jsDelivr und ruft `docsearch({...})`
  dagegen auf - DocSearch rendert seinen eigenen Such-Button und sein
  eigenes Modal in diesen Container.

## Pagefind

Setze `searchProvider.provider` auf `"pagefind"`, um die Suchbox gegen
[Pagefind](https://pagefind.app/) auszutauschen - eine weitere
vollständig statische Suchmaschine ohne Server, aber indiziert aus dem
*gebauten* `site/`-HTML statt gecrawlt wie bei Algolia:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: pagefind
      pagefind: { bin: pagefind, options: [] }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "pagefind",
    		"pagefind": { "bin": "pagefind", "options": [] }
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "pagefind"

    [searchProvider.pagefind]
    bin = "pagefind"
    options = []
    ```

Beide `pagefind`-Schlüssel sind optional - `bin` (Standard `"pagefind"`)
ist der Name/Pfad der Binary, aufgelöst gegen `PATH`, wenn es ein reiner
Name ist; `options` ist ein Array zusätzlicher, roher CLI-Flags, die
direkt durchgereicht werden (z. B.
`["--exclude-selectors", ".no-index"]`).

Mit aktivem `pagefind`:

- **Die `pagefind`-CLI muss bereits installiert und im `PATH` vorhanden
  sein** - BxSites ruft sie extern auf (es gibt keine
  BoxLang-native Anbindung, derselbe Grund, aus dem
  `lastUpdated`/`gh-deploy` `git` extern aufrufen), es installiert sie
  nicht für dich. Siehe
  [Pagefinds Installationsanleitung](https://pagefind.app/docs/installation/).
  Anders als bei `lastUpdated` lässt eine fehlende/fehlschlagende Binary
  den `build` laut fehlschlagen (`BxSites.PagefindFailed`), statt
  stillschweigend zu degradieren - eine Website auszuliefern, deren
  konfigurierter Suchprovider nicht funktioniert, ist schlimmer als ein
  fehlgeschlagener Build.
- Direkt nachdem jeder Doc-Baum (Haupt- + Versionen + Locales)
  geschrieben und `sitemap.xml`/`llms.txt` erzeugt wurden, führt BX
  Sites `pagefind --site <siteDir> [...options]` gegen die *gesamte*
  gebaute `site/` aus - sodass eine Multi-Versions-/Multi-Locale-Website
  in einem Durchgang vollständig indiziert wird, anders als bei
  bx-sites' eigener `search-index.json` pro Baum. Pagefind schreibt sein
  eigenes Bundle direkt nach `site/pagefind/` - selbst gehostet, kein
  CDN beteiligt.
- Es wird keine `search-index.json` gebaut, und das gemeinsame
  `lunr.js`/`search.js`-Widget wird nicht ausgeliefert (wie bei
  `algolia`) - und `bxSites search-index` ist aus demselben Grund ein
  No-op (siehe oben).
- Jedes integrierte Theme rendert einen leeren
  `#bxsites-search-pagefind`-Container, und `layout.bxm` lädt
  `site/pagefind/pagefind-ui.{css,js}` und ruft `new PagefindUI({...})`
  dagegen auf - Pagefind rendert seine eigene Inline-Suchbox und
  Ergebnisse in diesen Container.

## Andere Suchprovider

`searchProvider.provider` ist nicht auf
`"local"`/`"algolia"`/`"pagefind"` beschränkt - jeder andere Wert wird
von `bxsites.yaml` unverändert akzeptiert (die eigene
Konfigurationsvalidierung von BxSites prüft nur die drei oben genannten
Provider). Dafür gibt es keinen Plugin-Hook - die integrierten Themes
rendern für einen nicht erkannten Providernamen einfach nichts, und das
Verdrahten eines vierten Suchdienstes (Meilisearch, Typesense usw.) ist
ein projektweites
[Theme-Override](themes.md#ein-theme-überschreiben): kopiere ein
integriertes Theme in den eigenen `theme/`-Ordner deines Projekts und
füge das Markup/die Skripte deines Providers zu dessen
`layout.bxm`/`search.bxm` hinzu, wobei du `siteConfig.searchProvider`
ausliest, um zu entscheiden, wann sie gerendert werden -
`searchProviderName eq "..."`-Verzweigungen für den Mount-Punkt in
`search.bxm`, passende Verzweigungen in `layout.bxm` für dessen CSS/JS,
und (falls es nicht crawler-gehostet ist wie Algolia) welchen
Indizierungsschritt dieses Produkt auch immer gegen `site/` nach `build`
braucht - dieselbe Form, die das `layout.bxm`/`BuildPipeline.bx` dieses
Moduls bereits für `algolia`/`pagefind` verwendet.
