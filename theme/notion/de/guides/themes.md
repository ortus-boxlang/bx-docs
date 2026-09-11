---
title: Themes
order: 1
icon: phosphor-duotone:palette
tags: [anleitungen, themes]
---

# Themes

Themes sind native BoxLang-`.bxm`-Templates - es gibt keine separate
Template-Engine oder einen eigenen Build-Schritt.

## Integriert

| Theme | Basis | Hinweise |
|---|---|---|
| `bootstrap` (Standard) | [Bootstrap 5](https://getbootstrap.com/), vendoriert | Poppins-Schriftart, Navbar mit Marken-Gradient |
| `material` | Handgefertigtes Material-artiges CSS | Card-Layout, Elevation-Schatten, Roboto-Schriftart |
| `tailwind` | [Tailwind Play CDN](https://tailwindcss.com/) | Utility-Class-getrieben, kein Build-Schritt |
| `docsy` | Handgefertigtes CSS, geforkt von `material` | An Read the Docs/Docsy angelehnter, marineblauer Referenzhandbuch-Look |
| `slate` | Handgefertigtes CSS, geforkt von `material` | An Stripe/Slate angelehnt - eine dauerhaft dunkle Sidebar, unabhängig vom Hell-/Dunkelmodus |
| `docusaurus` | Handgefertigtes CSS, geforkt von `material` | An Docusaurus angelehnte, kräftige, volle Breite farbige Navbar, abgerundete Cards |
| `justthedocs` | Handgefertigtes CSS, geforkt von `material` | An Just the Docs angelehnter Minimalismus - die Suchbox sitzt oben in der Sidebar |
| `vuepress` | Handgefertigtes CSS, geforkt von `material` | An VuePress angelehnter grüner Akzent, weich abgerundete Ecken |
| `gitbook` | Handgefertigtes CSS, geforkt von `material` | An GitBook angelehnte zentrierte Lesespalte, Serifen-Überschriften |
| `notion` | Handgefertigtes CSS, geforkt von `material` | An Notion angelehnte randlose Sidebar, nahezu grauskalige UI, großzügiger Weißraum |

Die sieben oben von `material` geforkten Themes verwenden `material`s
exakte BoxLang-Templates (layout.bxm/page.bxm/search.bxm) unverändert,
bis auf eine begrenzte CSS-Klassenpräfix-Umbenennung - nur
`assets/style.css` (und bei `justthedocs` eine verschobene
`<bx:include>`-Zeile, die die Suchbox in die Sidebar verlegt)
unterscheidet sich, sodass sie denselben vollständigen Funktionsumfang
und dasselbe air-gapped-fähige Verhalten erben, das `material` bereits
hat.

Das eigene CSS/JS jedes integrierten Themes (Bootstraps CSS/JS-Bundle,
highlight.js, Alpine.js, lunr.js für den standardmäßigen `local`-Suchanbieter,
und Mermaid, wenn `mermaid` aktiviert ist) wird vendoriert mit diesem Modul
ausgeliefert und direkt in jede gebaute `site/` kopiert - kein CDN, kein
Internetzugang nötig, um eine gebaute Website anzusehen. Die eigene
Utility-Engine des `tailwind`-Themes (ein clientseitiger JIT-Compiler, kein
statisches Stylesheet) und andere optionale Funktionen, die du selbst
aktivierst (`math`, Algolia-Suche, Google Analytics), laden weiterhin von
einem CDN oder einer gehosteten API - siehe
[Air-gapped/Offline-Websites](#air-gappedoffline-websites) unten.

`bootstrap`, `material` und `tailwind` verwenden dieselbe
BoxLang-Markenpalette (einen `#00FF78 -> #00DBFF`-Gradient und einen
`#FFF500`-Akzent); die sieben Galerie-Themes darunter verwenden stattdessen
jeweils ihre eigene, unterschiedliche Palette, angelehnt an die Plattform,
von der sie ihren Look entlehnen - siehe die Tabelle oben. Jedes der zehn
bringt unabhängig von der Palette denselben Satz an Seitenfunktionen mit:

- **Ein Inhaltsverzeichnis "Auf dieser Seite"**, erzeugt aus den eigenen
  `h2`/`h3`-Überschriften jeder Seite.
- **Breadcrumbs**, die die Ahnenkette einer Seite zeigen, wenn sie mehr
  als eine Ebene tief unter einem verlinkten Vorfahren verschachtelt ist.
- **Vorherige/Nächste-Seite-Links** am Ende des Artikels, entlang der
  eigenen Leserichtung der Navigation.
- **Syntax-hervorgehobene Codeblöcke**, über [highlight.js](https://highlightjs.org/)
  plus eine eigene BoxLang-Grammatik (` ```bx `/` ```boxlang `/` ```cfscript `),
  jeweils mit einem **Kopieren-Button** - bei Hover sichtbar auf Geräten,
  die das unterstützen, auf Touch-Geräten immer sichtbar (dort gibt es
  kein Hover, um ihn einzublenden). Siehe
  [Markdown-Erweiterungen](markdown.md#codeblöcke).
- **Selbst gehostete Webfonts** - keine Anfrage an `fonts.googleapis.com`
  zur Anzeigezeit.
- **Ein Dunkel-/Hell-Modus-Umschalter**, angetrieben von
  [Alpine.js](https://alpinejs.dev/) für die Reaktivität. Die Wahl des
  Besuchers wird in `localStorage` gemerkt (mit Fallback auf die
  Betriebssystem-Präferenz) und noch vor dem ersten Rendern angewendet, um
  ein kurzes Aufblitzen des falschen Themes zu vermeiden.
- **Eine responsive Kopfzeile**, die bei jeder Breite einreihig bleibt -
  ein schmales Viewport schrumpft die Suchbox, statt sie in eine eigene
  Zeile umbrechen zu lassen - plus eine einklappbare Sidebar-Navigation
  (ein Hamburger-Umschalter in `bootstrap`/`material`/`tailwind`
  gleichermaßen).
- **Tastaturkürzel** in der Suchbox: `/` fokussiert die Suche von überall
  auf der Seite, und `Escape` schließt die Ergebnisse. Siehe
  [Suche](search.md).
- **Ein Repo-Link und eine "Diese Seite bearbeiten"-/"Zuletzt
  aktualisiert"-Zeile**, wenn die Optionen `repo`/`lastUpdated` in
  `bxsites.yaml` gesetzt sind. Siehe
  [Konfiguration](../configuration.md#repo).
- **Ein "Markdown herunterladen"-Link**, neben "Diese Seite bearbeiten" -
  die rohe `.md`-Quelle jeder Seite wird zusammen mit ihrem gebauten HTML
  veröffentlicht (`guides/themes.md` liegt neben
  `guides/themes/index.html`), sodass sie (oder eine KI) die Seite direkt
  als reines Markdown lesen kann, statt gerendertes HTML zu parsen. Immer
  aktiv, keine Konfiguration nötig. Siehe
  [Erste Schritte](../getting-started.md#eine-seite-als-markdown-herunterladen).
- **Eine optionale Fußzeile** (Copyright, `social`-Links, ein "Built with
  BxSites"-Hinweis), wenn `footer` in `bxsites.yaml` `true` ist. Siehe
  [Konfiguration](../configuration.md#footer).
- **Ein Versionsumschalter**, der automatisch erscheint, sobald ein
  Projekt einen `docs/versions/`-Ordner mit mehr als einer Version hat.
  Siehe [Konfiguration](../configuration.md#versionierung).
- **Eine themenspezifische `404.html`**, automatisch ausgeliefert von den
  meisten statischen Hosts (einschließlich GitHub Pages) für jeden nicht
  gefundenen Pfad. Lege eine `404.md` im Stammverzeichnis von `docs/`
  (oder `src/`) an, um Titel und Inhalt selbst zu bestimmen - sie wird nie
  als reguläre Seite gebaut (kein Nav-Eintrag, keine `sitemap.xml`-URL),
  sondern stattdessen direkt in `site/404.html` gerendert.
- **Ein eigenes Logo und Favicon**, wenn `theme.logo`/`theme.favicon` in
  `bxsites.yaml` gesetzt sind. Siehe
  [Konfiguration](../configuration.md#theme).
- **Eine einklappbare Sidebar-Navigation**, opt-in über
  `theme.options.navCollapsible` - jeder Abschnitt mit Kindern (verlinkt
  oder nicht) erhält eine Umschalt-Schaltfläche, statt seine Kinder immer
  inline anzuzeigen, und der Abschnitt, der die aktuelle Seite enthält,
  startet immer geöffnet. Siehe
  [Konfiguration](../configuration.md#theme).
- **Google Analytics**, wenn `analytics` in `bxsites.yaml` konfiguriert
  ist. Siehe [Konfiguration](../configuration.md#analytics).
- **Social-Share-Cards** (Open-Graph- + Twitter-Card-Meta-Tags), erzeugt
  aus der `description`-Frontmatter jeder Seite (oder der websiteweiten
  `description`) und ihrem eigenen `ogImage` (oder dem websiteweiten) -
  optional automatisch pro Seite erzeugt über `generateOgImages` in
  `bxsites.yaml`. Siehe [Konfiguration](../configuration.md#ogimage).
- **Seiten-Tags, ein Icon und eine Zusammenfassungszeile**, alle opt-in
  über die eigene Frontmatter einer Seite - Tags werden als Badges
  gerendert, die zu einem websiteweiten `/tags/`-Index verlinken. Siehe
  [Erste Schritte](../getting-started.md#seiten-hinzufügen).
- **Eine explizite Navigations-Überschreibung**, in `bxsites.yaml` oder der
  eigenen `docs/nav.json`, die die Ordner-Ableitung für große Websites
  ersetzt. Siehe [Konfiguration](../configuration.md#nav).
- **Zusätzliches CSS/JS**, eingebunden über `extraCss`/`extraJs` in
  `bxsites.yaml`. Siehe
  [Konfiguration](../configuration.md#extracss--extrajs).
- **Admonition-Callout-Boxen** (Hinweis/Warnung/Tipp/...), standardmäßig
  im Markdown jeder Seite aktiv, einschließlich einklappbarer Varianten -
  keine Konfiguration nötig. Siehe
  [Markdown-Erweiterungen](markdown.md#admonitions).
- **Fußnoten und Definitionslisten**, opt-in über `markdown` in
  `bxsites.yaml`. Siehe
  [Markdown-Erweiterungen](markdown.md#fußnoten).
- **Content-Tabs**, **Code-Zeilennummern/hervorgehobene Zeilen/Titel**
  und **Diff-Markierungen/Terminal-Rahmen** für Codeblöcke, keine
  Konfiguration nötig. Siehe
  [Markdown-Erweiterungen](markdown.md#content-tabs).
- **Mermaid-Diagramme**, opt-in über `mermaid` in `bxsites.yaml`. Siehe
  [Markdown-Erweiterungen](markdown.md#diagramme).
- **Mathematik** (KaTeX), opt-in über `math` in `bxsites.yaml`. Siehe
  [Markdown-Erweiterungen](markdown.md#mathematik).

Lege in `bxsites.yaml` fest, welches Theme ein Projekt verwendet:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: material }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "material" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "material"
    ```

## Ein veröffentlichtes Theme installieren

Ein auf ForgeBox veröffentlichtes Theme installiert sich mit nichts
weiter als der `bxSites`-Binary selbst - kein `box`/CommandBox nötig.
Durchsuche die bereits veröffentlichten Pakete unter der Kategorie
[`bxsites-themes`](https://www.forgebox.io/type/bxsites-themes) auf
ForgeBox:

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

Das lädt das ZIP des Pakets herunter und entpackt es nach
`themes/bx-sites-theme-blog1/` im Projekt-Wurzelverzeichnis, wobei vor
Abschluss geprüft wird, ob es den `ThemeProvider`-Vertrag unten erfüllt.
Ein Projekt kann auf diese Weise mehrere installierte Themes
nebeneinander vorhalten und rein per Name zwischen ihnen wechseln:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: bx-sites-theme-blog1 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "bx-sites-theme-blog1" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "bx-sites-theme-blog1"
    ```

Ein Theme braucht überhaupt keine BoxLang-Modul-/Klassenlader-Beteiligung
(anders als ein Plugin) - es sind reine Dateien, es gibt also keinen
separaten Aktivierungsschritt, wie ihn `install:plugin` hat; das Setzen
von `theme.name` ist die einzige nötige Verdrahtung. Siehe
[`install:theme`](../cli-reference.md#installtheme) in der CLI-Referenz.

Startest du stattdessen von einem Theme, das für einen anderen
statischen Site-Generator gebaut wurde? Siehe
[Ein Theme importieren](theme-import.md) - `theme:import` wandelt die
eigenen Template-Dateien eines mkdocs-/jekyll-/hugo-Themes mechanisch in
ein bestmögliches `themes/<name>/`-Gerüst um.

## Air-gapped/Offline-Websites

Eine gebaute Website funktioniert standardmäßig völlig ohne Internetzugang,
für `bootstrap`, `material` und die sieben von `material` geforkten
Themes (`docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`,
`gitbook`, `notion`) mit dem standardmäßigen `local`-Suchanbieter:
Bootstraps eigenes CSS/JS, highlight.js, Alpine.js und
lunr.js sind alle mit diesem Modul vendoriert (`resources/assets/vendor/`)
und werden zur Build-Zeit direkt nach `site/assets/vendor/` kopiert -
nirgends im erzeugten HTML taucht dafür ein CDN-`<script>`-/`<link>`-Tag
auf. Aktivierst du den Schlüssel `mermaid` in `bxsites.yaml`, wird Mermaid
auf dieselbe Weise vendoriert - sein `mermaid.min.js`-Bundle wird nach
`site/assets/vendor/mermaid/` kopiert, und jedes integrierte Theme lädt es
von dort, sodass Diagramme weiterhin ganz ohne ausgehende Anfragen
gerendert werden.

Ein paar Dinge greifen weiterhin nach außen ins Netzwerk, aber nur, wenn
du sie selbst aktivierst:

- Die eigene Utility-Engine des `tailwind`-Themes ist ein clientseitiger
  JIT-Compiler, geladen von `cdn.tailwindcss.com` - kein statisches
  Stylesheet, das dieses Modul auf dieselbe Weise vendorieren könnte,
  daher ist dieses Theme (noch) nicht air-gapped-fähig.
- Mermaids eigene Layout-Engine lädt bei Bedarf einen zusätzlichen Chunk
  nach, `elk-api.js`, von jsDelivr - aber nur für Diagrammtypen, die den
  `elk`-Layout-Algorithmus verwenden; das vendorierte `mermaid.min.js`
  rendert jeden anderen Diagrammtyp vollständig eigenständig.
- Die Option `math` in `bxsites.yaml` lädt KaTeX (sowohl dessen JS als auch
  die eigenen Font-Dateien) von einem CDN, wenn sie aktiviert ist.
- `searchProvider.provider: "algolia"` und `analytics.provider: "google"`
  sprechen naturgemäß mit einer gehosteten API/einem Tracking-Endpunkt -
  das Vendorieren der JS-Datei würde diese Abhängigkeit nicht beseitigen.

Wenn dein Einsatzziel wirklich null Internetzugang hat, bleib bei
`bootstrap`/`material`/einem der sieben von `material` geforkten Themes,
dem standardmäßigen `local`-Suchanbieter, vermeide
`elk`-Layout-Mermaid-Diagramme, falls `mermaid` aktiviert ist, und lass
`math`/Algolia/Analytics aus.

Siehe [Icons](icons.md) dafür, wie die eigene `icon`-Frontmatter einer
Seite (oder das eigene `icon` eines `nav.json`-Eintrags) zu einem Emoji,
einem benannten Icon aus einer von acht mitgelieferten Bibliotheken oder
einer eigenen, projekteigenen SVG-Datei aufgelöst wird.

## Der `ThemeProvider`-Vertrag

Ein Theme ist einfach ein Ordner mit:

- **`layout.bxm`** (erforderlich) - die äußere HTML-Hülle + Navigation.
  Erhält `variables.page`, `variables.nav`, `variables.siteConfig`,
  `variables.themeDir` und `variables.basePath` im Scope, und bindet das
  benachbarte `page.bxm` über `#variables.themeDir#/page.bxm` ein.
  `variables.basePath` ist immer ein root-relativer, auf `/` endender Pfad
  (standardmäßig `/`, `/my-docs/`, wenn `baseURL` in `bxsites.yaml` das
  überschreibt) - stelle diesen jedem internen `href`/`src` voran, statt
  ein führendes `/` fest zu codieren, damit das Theme auch funktioniert,
  wenn die Website aus einem Unterpfad ausgeliefert wird.
- **`page.bxm`** (erforderlich) - der Artikelinhalt. Rendert
  `variables.page.contentHtml` - das bereits konvertierte Markdown.
- **`search.bxm`** (optional) - das Markup der Suchbox, von `layout.bxm`
  nur eingebunden, wenn `search` in `bxsites.yaml` `true` ist. Siehe
  [Suche](search.md).
- **`assets/`** (optional) - Theme-CSS/JS, zur Build-Zeit nach
  `site/assets/theme/` kopiert.

`variables.page.editUrl`/`.lastUpdated` (leere Zeichenketten, wenn nicht
konfiguriert) und `variables.siteConfig.repo`/`.social`/`.footer` sind
ebenfalls immer verfügbar und stützen die oben beschriebenen
Repo-Link-/Bearbeiten-Link-/Zuletzt-aktualisiert-/Fußzeilen-Funktionen -
ein eigenes Theme entscheidet selbst, ob und wie es sie rendert, wie auch
alles andere. `variables.versions` (`[ { label, url } ]`, "Latest" zuerst)
und `variables.currentVersion` (das gerade gerenderte `label`) stützen den
Versionsumschalter - leer/`"Latest"` für ein nicht versioniertes Projekt,
sodass ein Theme nur dann einen Umschalter rendern muss, wenn
`variables.versions.len() gt 1`. Jedes integrierte Theme bezieht seine
Repo-/Social-Icons aus einem kleinen gemeinsamen SVG-Lookup,
`<bx:include template="#variables.moduleAssetsDir#/icons.bxm">`
(definiert `bxsitesIcon( name )`, eines von `github`, `twitter`/`x`, `rss`,
`youtube`, `linkedin`, `facebook`, `bluesky`, `threads`, `slack`,
`patreon`, `email`, `edit`, `clock`, mit einem generischen Link-Symbol als
Fallback) - ein eigenes Theme kann das auf dieselbe Weise einbinden, oder
seine Icons vollständig selbst bereitstellen.

Ein Theme-Ordner, dem eine der beiden erforderlichen Dateien fehlt,
schlägt sofort mit einem klaren `BxSites.InvalidTheme`-Fehler zur Build-Zeit
fehl, statt mit einem verwirrenden Template-Fehler tief im Rendering.

## Farben anpassen, ohne ein Theme zu überschreiben

Für eine kleine Farb-/Schriftanpassung ist ein ganzes Theme zu forken
Overkill - jedes integrierte Theme liest seine Palette aus einer Handvoll
CSS-Custom-Properties auf `:root`, erneut deklariert unter
`[data-theme="dark"]` für den Dunkelmodus. Das
[`extraCss`](../configuration.md#extracss--extrajs) von `bxsites.yaml` wird
*nach* dem eigenen Stylesheet des Themes geladen, sodass eine erneute
Deklaration mit gleicher Spezifität darin gewinnt, ohne `resources/themes/`
überhaupt anzurühren:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/brand.css ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/brand.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/brand.css" ]
    ```

```css title="docs/assets/brand.css" linenums="1"
/* docs/assets/brand.css - copied to site/assets/brand.css at build time */
:root {
	--bxsites-gradient-start: #7C3AED;
	--bxsites-gradient-end: #DB2777;
	--bxsites-accent: #FBBF24;
	--bxsites-link: #7C3AED;
	--bxsites-link-hover: #9F5AF0;
}

[data-theme="dark"] {
	--bxsites-link: #C4B5FD;
	--bxsites-link-hover: #DDD6FE;
}
```

Das eigene Set des `bootstrap`-Themes
(`resources/themes/bootstrap/assets/style.css`) ist
`--bxsites-gradient-start`/`-end`, `--bxsites-accent`, `--bxsites-bg`,
`--bxsites-text`, `--bxsites-sidebar-bg`, `--bxsites-sidebar-text`,
`--bxsites-border`, `--bxsites-link`, `--bxsites-link-hover`,
`--bxsites-code-bg`, `--bxsites-step-marker-bg`, `--bxsites-step-marker-text`,
`--bxsites-step-line`, `--bxsites-step-success-bg`/`-text`,
`--bxsites-step-warning-bg`/`-text`, `--bxsites-step-danger-bg`/`-text` und
das [`--bxsites-table-*`-Set](tables.md#theming), das bei einer gerenderten
Tabelle Karte, Kopfleiste, Trennlinien und Zebra-/Hover-Tönung zeichnet.
Jedes integrierte Theme garantiert `--bxsites-gradient-start`/`-end`,
`--bxsites-accent` sowie die Sets `--bxsites-step-*` und
`--bxsites-table-*` unter genau diesen Namen, sodass `extraCss` unabhängig vom Theme immer die
Markenfarbe/Stepper-Akzente umlenken kann - aber nur `bootstrap`, `slate`
und `notion` legen zusätzlich auch
`--bxsites-bg`/`-text`/`-sidebar-bg`/`-sidebar-text`/`-border`/`-link`/`-link-hover`/`-code-bg`
unter diesen Namen offen (`justthedocs` aliast alle bis auf die beiden
`-sidebar-*` auf dieselbe Weise). Jedes andere integrierte Theme
(`material`, `tailwind`, `docsy`, `docusaurus`, `vuepress`, `gitbook`)
verwendet für diese zweite Gruppe stattdessen seine eigenen internen
Custom-Property-Namen (z. B. verwendet materials eigenes
`assets/style.css` `--md-bg`/`--md-ink`/`--md-link`/...) - öffne das
eigene `assets/style.css` dieses Themes, um seine echten Namen zu finden,
bevor du eines davon über `extraCss` überschreibst. Alles über
Farbe/Schriftart hinaus (Layout, Chrome hinzufügen/entfernen) braucht
eine echte Überschreibung oder ein eigenes Theme - siehe unten.

Der Rest stützt den [`::: stepper`/`::: step`](content-blocks.md#stepper)-Direktiv-Block
- `--bxsites-step-marker-bg`/`-text` sind die Hintergrund-/Textfarbe des
standardmäßigen nummerierten Kreises (`bootstrap`/`material` setzen ihn
standardmäßig auf den eigenen `--bxsites-accent` des Themes; `tailwind`
verwendet ein eigenes Türkis-/Minz-Paar, da es keinen einzelnen
gemeinsamen Akzent-Token hat), `--bxsites-step-line` ist die
Verbindungslinie zwischen den Schritten, und die
`-success`/`-warning`/`-danger`-Paare stützen das eigene, optionale
`color="..."`-Attribut eines Schritts - anders als der Standard-Marker
sind diese drei dasselbe feste Hintergrund-/Text-Paar sowohl im
Hell- als auch im Dunkelmodus (ein in sich geschlossenes Badge, nicht an
die eigene Markenfarbe des Themes gebunden), es gibt also keine
`[data-theme="dark"]`-Überschreibung neu zu deklarieren:

```css title="docs/assets/brand.css" linenums="1"
:root {
	--bxsites-step-marker-bg: #7C3AED;
	--bxsites-step-marker-text: #fff;
	--bxsites-step-success-bg: #059669;
	--bxsites-step-success-text: #fff;
}

[data-theme="dark"] {
	--bxsites-step-marker-bg: #C4B5FD;
	--bxsites-step-marker-text: #1b1f21;
}
```

## Homepage-Hero-Banner

Jedes integrierte Theme liefert CSS für ein Homepage-Banner in voller
Breite mit Titelbild und Call-to-Action-Buttons - genau diese Website
verwendet es in ihrer eigenen `docs/index.md`. Es gibt dafür keinen
Direktiv-Block und keine Konfiguration, nur schlichtes HTML, das jede
Seite einfügen kann (eine Homepage ist einfach eine normale Seite,
`order: 1` oder sonst als Erste in der Navigation):

```markdown title="docs/index.md"
<div class="bxsites-hero">
	<img class="bxsites-hero__banner" src="assets/home-banner.jpg" alt="...">
	<div class="bxsites-hero__actions">
		<a class="bxsites-hero__btn bxsites-hero__btn--primary" href="getting-started.md">Get Started</a>
		<a class="bxsites-hero__btn bxsites-hero__btn--secondary" href="https://github.com/your/repo">View on GitHub</a>
	</div>
</div>
```

`bxsites-hero__btn--primary`/`--secondary` sind dieselben zwei
Akzent-Stile, die jedes Theme bereits an anderer Stelle verwendet -
tausche, entferne oder füge Buttons nach Belieben hinzu, und
skaliere/ersetze das eigene Bild von `bxsites-hero__banner` über ein zu
`docs/assets/`-relatives `src`, auf dieselbe Weise, wie jedes andere Bild
aufgelöst wird.

## Ein Theme überschreiben

Lege deine eigenen `layout.bxm` + `page.bxm` (und optional `search.bxm` /
`assets/`) in einen `theme/`-Ordner im Wurzelverzeichnis deines Projekts.
BxSites bevorzugt eine projektweite `theme/`-Überschreibung sowohl
gegenüber einem installierten `themes/<name>/`-Theme als auch gegenüber
jedem integrierten Theme, solange sie den obigen Vertrag erfüllt - die
integrierten Themes unter `resources/themes/` dieses Moduls sind ein
guter Ausgangspunkt zum Kopieren und Anpassen. Vollständige
Auflösungsreihenfolge: `theme/` (dieser Abschnitt) -> `themes/theme.name/`
(ein [installiertes Theme](#ein-veröffentlichtes-theme-installieren),
falls `theme.name` zu einem passt) -> ein integriertes Theme namens
`theme.name`.

Ein durchgearbeitetes Beispiel - starte mit `bootstrap` und tausche seine
Markenpalette und Überschriften-Schriftart gegen deine eigenen aus, wobei
alles andere (Navigation, Suche, Dunkelmodus, Code-Hervorhebung, ...)
genau so weiterläuft, wie es bereits funktioniert:

```text title="Project structure"
my-project/
├── bxsites.yaml
├── docs/
└── theme/                    ← project-level override, checked before any built-in theme
    ├── layout.bxm             ← copied from resources/themes/bootstrap/layout.bxm
    ├── page.bxm                ← copied from resources/themes/bootstrap/page.bxm, unchanged
    ├── search.bxm               ← copied unchanged
    └── assets/
        └── style.css              ← copied from bootstrap's assets/style.css, then edited
```

1. Kopiere die drei `.bxm`-Dateien und `assets/style.css` aus
   `resources/themes/bootstrap/` dieses Moduls in das `theme/` deines
   Projekts.
2. Ändere nur das, was du wirklich ändern musst. Um Markenpalette und
   Schriftart zu tauschen, genügt der obere Teil von
   `theme/assets/style.css`:

   ```css title="theme/assets/style.css" linenums="1"
   :root {
   	--bxsites-gradient-start: #7C3AED;  /* was #00FF78 */
   	--bxsites-gradient-end: #DB2777;    /* was #00DBFF */
   	--bxsites-accent: #FBBF24;          /* was #FFF500 */
   }

   body {
   	font-family: "Inter", system-ui, sans-serif;  /* was "Poppins" */
   }
   ```

3. Führe `bxSites build` aus (oder `serve` während der
   Iteration) - BxSites übernimmt `theme/` automatisch, keine Änderung an
   `bxsites.yaml` nötig (ein projektweiter `theme/`-Ordner hat immer
   Vorrang vor dem im `theme.name` genannten integrierten Theme). Alles,
   was du nicht angefasst hast - Navigations-Rendering, Suche, der
   Dunkelmodus-Umschalter, Code-Annotationen - funktioniert genau so
   weiter, wie es im ursprünglichen `bootstrap`-Theme funktioniert hat, da
   es darunter immer noch genau dasselbe `layout.bxm`/`page.bxm`-Markup
   ist.

Ein projektweiter `theme/`-Ordner ist allerdings alles-oder-nichts -
sobald BxSites einen findet, wird er anstelle des integrierten Themes
vollständig verwendet, braucht also trotzdem seine eigenen `layout.bxm` +
`page.bxm`, selbst wenn du nur `assets/style.css` geändert hast (ein
Ordner, dem eine der beiden fehlt, schlägt sofort mit
`BxSites.InvalidTheme` fehl, statt stillschweigend zurückzufallen). Für
eine reine CSS-Anpassung ohne `.bxm`, nutze stattdessen
[`extraCss`](#farben-anpassen-ohne-ein-theme-zu-überschreiben) von oben - es
legt sich über das Theme, das `bxsites.yaml` benennt, ganz ohne
`theme/`-Ordner. `theme/` ist für den Fall, dass du auch das Markup
selbst ändern musst, siehe als Nächstes.

## Ein Theme von Grund auf schreiben

Ein Theme braucht nur die beiden erforderlichen Dateien, hier also ein
wirklich minimales Beispiel - kein Bootstrap/Tailwind, kein Dunkelmodus,
keine Such-UI - um genau zu zeigen, was erforderlich ist im Vergleich zu
dem, was die integrierten Themes zusätzlich bieten. Speichere beide als
`theme/layout.bxm` und `theme/page.bxm` in deinem Projekt - ein
projektweiter `theme/`-Ordner wird automatisch übernommen (wie oben),
keine Änderung an `bxsites.yaml` nötig:

```bx title="theme/layout.bxm" linenums="1"
<!-- theme/layout.bxm -->
<bx:script>
	function renderNav( required array nodes ) {
		var html = "<ul>"
		for ( var node in arguments.nodes ) {
			html &= "<li>"
			html &= len( node.url )
				? '<a href="' & variables.basePath & node.url & '">' & encodeForHTML( node.title ) & '</a>'
				: encodeForHTML( node.title )
			if ( node.children.len() ) {
				html &= renderNav( node.children )
			}
			html &= "</li>"
		}
		return html & "</ul>"
	}
</bx:script>
<bx:output>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>#encodeForHTML( variables.page.title )# - #encodeForHTML( variables.siteConfig.name )#</title>
	<link rel="stylesheet" href="#variables.basePath#assets/theme/style.css">
</head>
<body>
	<header><a href="#variables.basePath#">#encodeForHTML( variables.siteConfig.name )#</a></header>
	<nav>#renderNav( variables.nav )#</nav>
	<main>
</bx:output>
<bx:include template="#variables.themeDir#/page.bxm">
<bx:output>
	</main>
</body>
</html>
</bx:output>
```

```bx title="theme/page.bxm" linenums="1"
<!-- theme/page.bxm -->
<bx:output>
<article>
	<h1>#encodeForHTML( variables.page.title )#</h1>
	#variables.page.contentHtml#
</article>
</bx:output>
```

Das ist ein vollständiges, funktionierendes Theme -
`variables.page.contentHtml` ist das bereits konvertierte Markdown
(Syntax-Hervorhebung, Admonitions, Tabs, Mathematik und alles), es bleibt
also nichts mehr zu parsen, nur noch zu layouten. Von hier aus füge
hinzu, was die integrierten Themes haben und du tatsächlich willst:
`search.bxm` (nur eingebunden, wenn `search` in `bxsites.yaml` `true` ist
- siehe [Suche](search.md)), einen Dunkelmodus-Umschalter (kopiere das
`x-data`/`x-init`-Alpine.js-Paar vom `<body>`-Tag von
`resources/themes/bootstrap/layout.bxm` und den passenden
`[data-theme="dark"]`-CSS-Block), Breadcrumbs/Tags/Vorherige-Nächste-Links
(`page.bxm` in jedem integrierten Theme zeigt das Muster - jedes ist nur
ein `if` um eine kleine Render-Funktion, alle gespeist aus Feldern, die
bereits auf `variables.page` vorhanden sind), oder einen `assets/`-Ordner
für dein eigenes CSS/JS, automatisch zur Build-Zeit nach
`site/assets/theme/` kopiert.
