---
title: Konfiguration
order: 4
icon: phosphor-duotone:gear-six
summary: Jeder Konfigurationsschlüssel, sein Standardwert und was er bewirkt.
tags: [referenz, konfiguration]
---

# Konfiguration

Jedes Projekt hat eine Website-Konfiguration in seinem Wurzelverzeichnis -
`bxsites.yaml` (oder `.yml`), das Standard- und bevorzugte Format, oder
`bxsites.json` für ein Projekt, das dabei bleiben möchte. Beide werden
vollständig unterstützt und liefern exakt dasselbe Ergebnis; `bxSites new`
erzeugt `bxsites.yaml`, sofern nicht `--format=json` übergeben wird (siehe
[Erste Schritte](getting-started.md#format-der-konfigurationsdatei)). Hat
ein Projekt aus irgendeinem Grund mehr als eine, gewinnt `bxsites.yaml`,
dann `bxsites.yml`, dann `bxsites.json`.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    name: "My Docs"
    description: ""
    baseURL: "/"
    theme:
      name: bootstrap
      options: {}
      logo: ""
      favicon: ""
    search: true
    mcp: false
    searchProvider:
      provider: local
      algolia: { appId: "", apiKey: "", indexName: "", insights: false }
    nav: []
    markdown:
      enableAdmonition: true
    repo:
      url: ""
      editUri: ""
    social: []
    footer: false
    lastUpdated: false
    mermaid: false
    math: false
    analytics:
      provider: ""
      id: ""
    ogImage: ""
    generateOgImages: false
    extraCss: []
    extraJs: []
    assets:
      fingerprint: true
      bundle: true
      images: { enabled: true, widths: [400, 800, 1200, 1600], formats: [original, webp] }
    plugins: []
    i18n:
      defaultLocale: { code: en, label: English }
      locales: []
    blog:
      postsPerPage: 10
      feed: true
    variables: {}
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"name": "My Docs",
    	"description": "",
    	"baseURL": "/",
    	"theme": {
    		"name": "bootstrap",
    		"options": {},
    		"logo": "",
    		"favicon": ""
    	},
    	"search": true,
    	"mcp": false,
    	"searchProvider": {
    		"provider": "local",
    		"algolia": { "appId": "", "apiKey": "", "indexName": "", "insights": false }
    	},
    	"nav": [],
    	"markdown": { "enableAdmonition": true },
    	"repo": {
    		"url": "",
    		"editUri": ""
    	},
    	"social": [],
    	"footer": false,
    	"lastUpdated": false,
    	"mermaid": false,
    	"math": false,
    	"analytics": {
    		"provider": "",
    		"id": ""
    	},
    	"ogImage": "",
    	"generateOgImages": false,
    	"extraCss": [],
    	"extraJs": [],
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": { "enabled": true, "widths": [400, 800, 1200, 1600], "formats": ["original", "webp"] }
    	},
    	"plugins": [],
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": []
    	},
    	"blog": {
    		"postsPerPage": 10,
    		"feed": true
    	},
    	"variables": {}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    name = "My Docs"
    description = ""
    baseURL = "/"
    search = true
    mcp = false
    nav = []
    social = []
    footer = false
    lastUpdated = false
    extraCss = []
    extraJs = []
    plugins = []
    variables = {}

    [theme]
    name = "bootstrap"
    options = {}
    logo = ""
    favicon = ""

    [markdown]
    enableAdmonition = true

    [repo]
    url = ""
    editUri = ""
    ```

Nur `name` ist erforderlich - alles andere fällt auf die oben gezeigten
Standardwerte zurück. Ein teilweise angegebenes `theme`-Objekt wird eine
Ebene tief zusammengeführt, sodass `{theme: {name: material}}` allein
weiterhin die Standard-(leeren)-`options` behält. Jeder Schlüssel unten
heißt in beiden Formaten gleich und hat dieselbe Form - wechsle bei jedem
Beispiel unten einfach den Tab, um es in der jeweils anderen Form zu sehen.

## `name`

Der Name der Website, angezeigt in der Kopfzeile/Marke und in Seitentiteln.
Erforderlich.

## `description`

Eine optionale Website-Beschreibung, verwendet als Fallback für
`<meta name="description">` und `og:description` für jede Seite, die keine
eigene `description`-Frontmatter setzt (siehe
[Erste Schritte](getting-started.md#seiten-hinzufügen)).

## `baseURL`

Steuert, wie jeder interne Link, Asset-Pfad und Navigationseintrag
vorangestellt wird, und dient zugleich als kanonische URL der Website für
`sitemap.xml`, `robots.txt`, `llms.txt` und das eigene
`<link rel="canonical">`-Tag jeder Seite.

- Leer gelassen oder `"/"` (Standard) - Links bleiben root-relativ
  (`/page/`), und weder `sitemap.xml`, eine `Sitemap:`-Zeile in
  `robots.txt`, eine `llms.txt` mit absoluten URLs, noch ein
  `<link rel="canonical">`-Tag wird erzeugt (es gibt keine kanonische
  Domain, aus der sie gebaut werden könnten).
- Ein reiner Pfad, z. B. `"my-docs"` oder `"/my-docs/"` - es wird
  angenommen, dass die Website unter diesem Unterpfad ausgeliefert wird,
  und jeder interne Link, Navigationseintrag und jedes Asset wird damit
  vorangestellt (`/my-docs/page/`). Immer noch keine `sitemap.xml`/
  Canonical-Tags, da es weiterhin keine absolute Domain gibt.
- Eine vollständige URL, z. B. `"https://docs.example.com/"` - der
  Pfadanteil (hier `/`) wird genauso verwendet wie ein reiner Pfad, **und**
  `sitemap.xml` wird zur Build-Zeit mit der absoluten URL jeder nicht
  versteckten Seite unter dieser Domain geschrieben, `robots.txt` erhält
  eine `Sitemap:`-Zeile, die darauf zeigt, und jede Seite bekommt ihr
  eigenes korrektes `<link rel="canonical">` (die eigene Seite eines
  Versions-/Locale-Baums zeigt weiterhin auf *die URL dieses Baums selbst*,
  nicht auf die der Haupt-Website).

`llms.txt` (siehe [unten](#llmstxt)) wird immer geschrieben; sie bevorzugt
lediglich eine absolute URL, wenn `baseURL` eine solche liefert.

## `llms.txt`

Jeder Build schreibt eine `llms.txt` in die Wurzel der Website - ein
einfacher Markdown-Index jeder nicht versteckten Seite, gemäß der
aufkommenden [llms.txt](https://llmstxt.org)-Konvention, die
LLM-basierten Tools hilft, sich auf einer Website zurechtzufinden, ohne
deren gerendertes HTML zu crawlen. Es gibt dafür keinen Konfigurationsschlüssel;
sie wird automatisch erzeugt, mit einer absoluten URL pro Link, wenn
`baseURL` eine vollständige URL ist, andernfalls relativ zum `basePath`.

## `sitemap.xml`

Wird in der Wurzel der Website geschrieben, aber nur, wenn `baseURL` eine
vollständige URL ist (siehe oben) - eine Sitemap braucht eine absolute
Domain, um sinnvoll zu sein. Listet jede nicht versteckte Seite gemäß dem
[sitemaps.org](https://www.sitemaps.org/)-Protokoll auf.

## `robots.txt`

Jeder Build schreibt eine `robots.txt` in die Wurzel der Website - kein
Konfigurationsschlüssel nötig, es sei denn, du möchtest ihr standardmäßig
erlaubendes Verhalten ändern:

=== "YAML"
    ```yaml title="bxsites.yaml"
    robots: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "robots": false }
    ```

- `true` (Standard) - `Allow: /` für jeden Crawler, plus eine
  `Sitemap:`-Zeile, die auf `sitemap.xml` zeigt, wenn `baseURL` eine
  vollständige URL ist (siehe oben).
- `false` - stattdessen `Disallow: /` für jeden Crawler, und keine
  `Sitemap:`-Zeile - der übliche Bedarf "dieses Staging-/internes Deploy
  überhaupt nicht indexieren lassen". Das ist nur ein *Crawler*-Opt-out,
  keine Zugriffskontrolle - die Website bleibt für jeden, der die URL hat,
  weiterhin vollständig erreichbar; siehe
  [Deployment](guides/deployment.md#zugriff-auf-deine-website-einschränken),
  falls du tatsächlich einschränken musst, wer überhaupt darauf zugreifen
  kann.

Brauchst du mehr als den Ein-/Aus-Schalter - bestimmte gesperrte Pfade,
mehrere `Sitemap:`-Zeilen, ein `Crawl-delay`, Regeln pro User-Agent? Lege
deine eigene `robots.txt` direkt neben `index.md` ab (`docs/robots.txt`,
oder `src/robots.txt` für ein `src/`-basiertes Projekt - siehe
[`docs/` oder `src/`](getting-started.md#seiten-hinzufügen)), und sie wird
byte-für-byte durchgereicht statt der generierten, bei jedem Build - der
`robots`-Schlüssel oben wird vollständig ignoriert, sobald diese Datei
existiert.

## `theme`

- `theme.name` - eines der integrierten Themes (`bootstrap`, `material`,
  `tailwind`, `docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`,
  `gitbook`, `notion`) oder der Name eines eigenen Themes, das du über
  einen `theme/`-Ordner im Projekt-Wurzelverzeichnis bereitstellst (siehe
  [Themes](guides/themes.md))
- `theme.logo` - Pfad/URL zu einem Bild, das neben dem Website-Namen in der
  Kopfzeilen-Marke angezeigt wird (anstelle des standardmäßigen
  "⚡"-Symbols) - ein relativer Pfad (z. B. `"assets/logo.svg"`, aufgelöst
  relativ zu `docs/assets/`) wird mit `baseURL` vorangestellt, wie jedes
  andere interne Asset auch; eine absolute URL wird unverändert verwendet.
  Leer gelassen (Standard), zeigt die Kopfzeile "⚡ &lt;Website-Name&gt;".
- `theme.favicon` - Pfad/URL zu einem Favicon, aufgelöst auf dieselbe Weise
  wie `theme.logo`. Leer gelassen (Standard), wird gar kein
  `<link rel="icon">` gerendert (es greift dann das Standardverhalten des
  Browsers).
- `theme.options` - themenspezifische Optionen, von jedem integrierten
  Theme gelesen:
  - `theme.options.colorMode` - `"auto"` (Standard), `"light"` oder
    `"dark"`. Steuert, welchen Modus ein Erstbesucher sieht, bevor er
    selbst über den Dunkel-/Hell-Umschalter in der Kopfzeile eine Wahl
    getroffen hat - `"auto"` folgt der Betriebssystem-Präferenz,
    `"light"`/`"dark"` legt einen festen Standard fest. Sobald ein Besucher
    den Schalter umlegt, gewinnt seine eigene Wahl (in `localStorage`
    gespeichert) bei späteren Besuchen immer, unabhängig von dieser
    Einstellung.

    === "YAML"
        ```yaml
        theme: { options: { colorMode: dark } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "colorMode": "dark" } } }
        ```
  - `theme.options.navCollapsible` - `false` (Standard) rendert jeden
    Navigationsabschnitt stets ausgeklappt, wie bisher. `true` gibt jedem
    Abschnitt mit Kindern eine Umschalt-Schaltfläche, mit der der Besucher
    ihn ein-/ausklappen kann - egal, ob dieser Abschnitt eine reine
    Gruppenüberschrift ist (ein Ordner ohne `index.md`) oder auf eine
    eigene Seite verlinkt. Der Abschnitt, der die gerade angezeigte Seite
    enthält, startet immer geöffnet, unabhängig von `navExpandAll`, damit
    das Navigieren dorthin nie den eigenen Link verdeckt, auf dem man
    gerade steht.
  - `theme.options.navExpandAll` - nur relevant, wenn `navCollapsible`
    `true` ist. `true` (Standard) startet jeden Abschnitt geöffnet;
    `false` startet jeden Abschnitt eingeklappt, außer dem, der die
    aktuelle Seite enthält.

    === "YAML"
        ```yaml
        theme: { options: { navCollapsible: true, navExpandAll: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "navCollapsible": true, "navExpandAll": false } } }
        ```
  - `theme.options.tocPosition` - wo das eigene Inhaltsverzeichnis ("Auf
    dieser Seite") einer Seite gerendert wird. `"top"` (Standard) rendert
    es inline, oben im Artikel, wie bisher. `"sticky"` verschiebt es in
    eine eigene rechte Spalte, die beim Scrollen des darunterliegenden
    Artikels sichtbar bleibt - dieselbe "Auf dieser Seite"-Liste, nur
    fixiert, was bei langen Seiten hilft. Die fixierte Spalte passt nur
    auf breite Viewports (sie ist unterhalb des Punkts ausgeblendet, an
    dem ein dreispaltiges Layout zu eng würde); unterhalb dieser Breite
    rendert der `sticky`-Modus stattdessen eine einklappbare "Auf dieser
    Seite"-Leiste, die beim Scrollen oben am Viewport fixiert bleibt -
    antippen, um die Liste auszuklappen, eine gängige Behandlung für
    schmale Viewports - sodass das Inhaltsverzeichnis bei jeder
    Viewport-Breite erreichbar bleibt, nur seine Form ändert sich je nach
    verfügbarem Platz.

    === "YAML"
        ```yaml
        theme: { options: { tocPosition: sticky } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "tocPosition": "sticky" } } }
        ```
  - `theme.options.pageMetaPosition` - wo die Zeile mit
    Bearbeiten-diese-Seite/Markdown-herunterladen/Zuletzt-aktualisiert
    relativ zum eigenen Inhalt einer Seite gerendert wird. `"bottom"`
    (Standard) rendert sie als kleine Fußnotiz kurz vor Ende des Artikels.
    `"top"` rendert sie stattdessen oben in der Nähe des Titels, an
    derselben Stelle, an der sie immer gerendert wurde, bevor es diese
    Option gab.

    === "YAML"
        ```yaml
        theme: { options: { pageMetaPosition: top } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "pageMetaPosition": "top" } } }
        ```

## `search`

`true` (Standard) baut einen statischen Suchindex und verdrahtet die
Suchbox; `false` überspringt beides vollständig - keine
`search-index.json`, keine Such-UI, kein zusätzliches JS ausgeliefert.
Siehe [Suche](guides/search.md).

## `searchProvider`

Welche Such-UI `search: true` verdrahtet:

- `provider` - `"local"` (Standard) ist bx-sites' eigene statische/
  clientseitige Suche (`search-index.json` + lunr.js, siehe
  [Suche](guides/search.md#local-der-standard)). `"algolia"` verdrahtet
  stattdessen [Algolia DocSearch](guides/search.md#algolia), und
  `"pagefind"` verdrahtet [Pagefind](guides/search.md#pagefind). Jeder
  andere Wert ist ein eigener, projektspezifischer Provider, verdrahtet
  über ein `theme/`-Override - siehe
  [Suche](guides/search.md#andere-suchprovider).
- `algolia` - erforderlich, wenn `provider` `"algolia"` ist: `appId`,
  `apiKey` (der *reine Such*-öffentliche API-Schlüssel, kein
  Administrator-Schlüssel) und `indexName`, genau wie es Algolias eigener
  DocSearch-Client erwartet. `insights` (standardmäßig `false`) schaltet
  DocSearchs Klick-/Konversions-Analytics ein.

  === "YAML"
      ```yaml title="bxsites.yaml" linenums="1"
      search: true
      searchProvider:
        provider: algolia
        algolia:
          appId: ABC123
          apiKey: a1b2c3d4e5f6...
          indexName: my-docs
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
      			"indexName": "my-docs"
      		}
      	}
      }
      ```

- `pagefind` - beide Schlüssel optional, wenn `provider` `"pagefind"`
  ist: `bin` (Standard `"pagefind"`) ist der Name/Pfad der CLI-Binary,
  aufgelöst gegen `PATH`, wenn es ein reiner Name ist; `options` ist ein
  Array zusätzlicher, roher CLI-Flags, die direkt durchgereicht werden.
  Die `pagefind`-CLI selbst muss bereits installiert und im `PATH`
  vorhanden sein - BxSites ruft sie extern auf (wie `git` für
  `lastUpdated`/`gh-deploy`), es installiert sie nicht für dich.

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

## `mcp`

`false` (Standard) überspringt es vollständig. `true` schreibt bei jedem
`build` die Datei `site/mcp-index.json` - einen vollständigen,
unabgeschnittenen Inhaltsindex für den öffentlichen, schreibgeschützten
MCP-Server von [bxSites Cloud](https://bxsites.io/cloud) für deine
veröffentlichte Website. Vollständig unabhängig von `search`/
`searchProvider` oben - er wird unabhängig davon erzeugt, welcher
Such-Provider (oder keiner) konfiguriert ist. Das Erzeugen der Datei
selbst ist kostenlos; bxSites Cloud liefert sie nur in den
kostenpflichtigen Plänen tatsächlich als laufenden MCP-Server aus, nicht
im kostenlosen Plan. Siehe [MCP-Server](guides/mcp.md).

## `nav`

Standardmäßig wird die Navigation aus der eigenen Ordner-/Dateistruktur von
`docs/` abgeleitet (mit `order`/`hidden`-Frontmatter) - für kleine
Websites ausreichend, aber eine große Website kann daraus herauswachsen:
eine explizite Navigation erlaubt es dir, Seiten zu betiteln, zu gruppieren
und zu ordnen, ganz unabhängig davon, wo ihre Dateien tatsächlich liegen.

Ein leeres Array (Standard) bedeutet "aus der Ordnerstruktur ableiten". Ein
nicht leeres Array ersetzt diese Ableitung vollständig - die Array-Reihenfolge
wird zur Navigationsreihenfolge, und eine Seite, auf die darin nirgendwo
verwiesen wird, wird trotzdem gebaut, nur nicht aus der Navigation
verlinkt (wie bei `hidden: true`). Jeder Eintrag ist entweder:

- eine reine, docs/-relative Pfad-Zeichenkette, z. B. `"guides/setup.md"` -
  der Titel stammt aus der eigenen Frontmatter/dem Dateinamen dieser Seite,
  genau wie bei der Ordner-Ableitung
- ein Objekt `{ "title", "path", "icon", "children" }` - `path`, `icon`
  und `children` sind alle optional; ein Eintrag nur mit `title` und ohne
  `path` ist eine unverlinkte Gruppenüberschrift (wie ein Ordner ohne
  `index.md` heute), und ein explizites `title`/`icon` überschreibt immer
  den eigenen Titel/das eigene Icon der verlinkten Seite in der Navigation
  (das echte `<h1>`/`<title>` der Seite bleibt unangetastet - nur das
  Navigations-Label/-Icon ändert sich) - siehe
  [Icons](guides/icons.md) dafür, was ein `icon`-Wert sein kann

Ein Eintrag nur mit `title`, mit `children` und ohne `path` ist genau ein
Menü-Container/Abschnitts-Label - eine nicht klickbare Überschrift, die
nur ihre Kinder gruppiert:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - index.md
      - title: Main Components
        children:
          - title: Quick Start
            path: guides/setup.md
          - guides/deployment.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		"index.md",
    		{
    			"title": "Main Components",
    			"children": [
    				{ "title": "Quick Start", "path": "guides/setup.md" },
    				"guides/deployment.md"
    			]
    		}
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Main Components"
    children = [
      { title = "Quick Start", path = "guides/setup.md" },
      { path = "guides/deployment.md" }
    ]
    ```

Gib demselben Gruppeneintrag stattdessen einen `path`, und er wird zu
einem normal verlinkten Abschnitt (mit eigener Landingpage plus Kindern)
statt eines reinen Labels - beide Formen verschachteln sich unter
`theme.options.navCollapsible` auf dieselbe Weise (siehe oben).

Für eine Navigation, die groß genug ist, um `bxsites.yaml` zu überladen,
verschiebe sie in eine eigene `docs/nav.json`-Datei - dieselbe Array-Form,
nur als kompletter Top-Level-Inhalt der Datei:

```json title="docs/nav.json" linenums="1"
[
	"index.md",
	{ "title": "Guides", "children": [ "guides/setup.md" ] }
]
```

Das eigene `nav` von `bxsites.yaml` gewinnt, wenn es nicht leer ist, immer
gegenüber `docs/nav.json`. Nur der Hauptbaum berücksichtigt eines von
beiden - ein `docs/versions/<name>/`-Baum leitet seine Navigation immer aus
seiner eigenen Ordnerstruktur ab, selbst wenn der Hauptbaum eine explizite
Navigation hat.

## `redirects`

`[]` (Standard) - websiteweite `from`/`to`-Weiterleitungen für alte URLs,
nur auf den Hauptbaum angewendet:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"
    ```

- `redirects[].from` - das alte hübsche URL-Segment, an dem eine
  statische Weiterleitungs-Stub-Datei geschrieben wird (kein
  führender/abschließender Schrägstrich, keine Dateiendung)
- `redirects[].to` - ein root-relativer Pfad (aufgelöst gegen `baseURL`)
  oder eine vollständige `https://`-URL

Die eigene Frontmatter `redirect_from` einer Seite ist die
Alternative pro Seite/pro Baum (funktioniert auch innerhalb eines
Versions-/Locale-Baums) - siehe [Weiterleitungen](guides/redirects.md)
für das vollständige Bild, einschließlich wie `page:rename` sie
automatisch setzt.

## `markdown`

Wird unverändert an [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)s
eigene Modul-Einstellungen weitergereicht, bevor jede Seite gerendert
wird. BxSites definiert oder validiert diese Schlüssel nicht neu; was auch
immer du hier einträgst, ist direkt bx-markdowns eigenes Optionsset - diese
Liste kann sich also im Lauf der Zeit von bx-markdowns eigener
unterscheiden. Tabellen, `~~Durchgestrichen~~`, `- [ ]`-Task-List-Checkboxen
und das Inhaltsverzeichnis innerhalb der Seite sind immer aktiv, ohne
Umschaltmöglichkeit. Die eine Ausnahme ist `enableAdmonition` - bx-markdown
selbst setzt es standardmäßig auf `false`, aber BxSites setzt es
standardmäßig auf `true` (siehe die
[Markdown-Erweiterungen-Anleitung](guides/markdown.md)).

| Schlüssel | Standard | Wirkung |
|---|---|---|
| `enableAdmonition` | `true` *(BX-Sites-Standard; bx-markdowns eigener Standard ist `false`)* | `!!!`/`???`/`???+`-Callout-Blöcke - siehe die [Markdown-Erweiterungen-Anleitung](guides/markdown.md#admonitions) |
| `enableFootnotes` | `false` | `[^label]`-Fußnotenverweise - siehe die [Markdown-Erweiterungen-Anleitung](guides/markdown.md#fußnoten) |
| `enableDefinitionLists` | `false` | `Term\n:   Definition`-Listen - siehe die [Markdown-Erweiterungen-Anleitung](guides/markdown.md#definitionslisten) |
| `autoLinkUrls` | `true` | Verlinkt nackte URLs und E-Mail-Adressen automatisch |
| `anchorLinks` | `true` | Fügt jeder Überschrift einen klickbaren Anker-Link hinzu |
| `anchorSetId` | `true` | Setzt ein `id`-Attribut auf jede Überschrift |
| `achorSetName` *(sic)* | `true` | Setzt ein `name`-Attribut auf jede Überschrift |
| `anchorWrapText` | `false` | Umschließt den gesamten Überschriftentext mit dem Anker-Link, statt nur eines schlichten Markers |
| `anchorClass` | `"anchor"` | CSS-Klasse auf dem Anker-`<a>` |
| `anchorPrefix` / `anchorSuffix` | `""` | Rohes HTML, unmittelbar vor/nach dem Überschriftentext eingefügt |
| `enableYouTubeTransformer` | `false` | Bettet nackte YouTube-Links automatisch als Player ein |
| `codeStyleHTMLOpen` / `codeStyleHTMLClose` | `"<code>"` / `"</code>"` | Wrapper-HTML um Inline-Code-Spans |
| `fencedCodeLanguageClassPrefix` | `"language-"` | Klassenpräfix, an dem sich bx-sites' clientseitiger Syntax-Highlighter (und Mermaid, siehe unten) orientieren, z. B. ` ```js ` -> `class="language-js"` |
| `tableOptions.columnSpans` | `true` | Berücksichtigt `colspan`-artig zusammengeführte Tabellenzellen |
| `tableOptions.appendMissingColumns` | `true` | Füllt eine zu kurze Zeile bis zur Spaltenzahl der Kopfzeile auf |
| `tableOptions.discardExtraColumns` | `true` | Verwirft überzählige Zellen in einer zu langen Zeile |
| `tableOptions.className` | `"table"` | CSS-Klasse auf jeder gerenderten `<table>` |
| `tableOptions.headerSeparationColumnMatch` | `true` | Verlangt, dass die `---`-Trennzeile der Spaltenzahl der Kopfzeile entspricht |

Jede gerenderte Tabelle erhält außerdem automatisch einen Wrapper für responsives Scrollen und einen fixierten Kopfbereich, ohne eigenen Konfigurationsschlüssel - siehe [Tabellen](guides/tables.md#responsives-scrollen-und-eine-fixierte-kopfzeile).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    markdown:
      enableFootnotes: true
      enableDefinitionLists: true
      anchorLinks: false
      enableYouTubeTransformer: true
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"markdown": {
    		"enableFootnotes": true,
    		"enableDefinitionLists": true,
    		"anchorLinks": false,
    		"enableYouTubeTransformer": true
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [markdown]
    enableFootnotes = true
    enableDefinitionLists = true
    anchorLinks = false
    enableYouTubeTransformer = true
    ```

## `repo`

Fügt der Kopfzeile (in jedem integrierten Theme) einen
Repository-Icon-Link hinzu und, wenn beide Schlüssel gesetzt sind, jeder
Seite einen "Diese Seite bearbeiten"-Link.

- `repo.url` - die URL deines Repos, z. B. `"https://github.com/acme/docs"`.
  Rendert für sich genommen bereits den Icon-Link in der Kopfzeile; leer
  lassen, um ihn ganz wegzulassen.
- `repo.editUri` - das Pfadsegment zwischen der Repo-URL und dem eigenen
  Quellpfad einer Seite, z. B. `"edit/main/docs/"` (GitHubs eigene
  "edit"-URL-Konvention). Wird mit `repo.url` und dem `docs/`-relativen
  Quellpfad einer Seite kombiniert, um ihren Bearbeiten-Link zu bauen -
  z. B. mit dem Beispiel oben ergibt `docs/guides/setup.md`
  `https://github.com/acme/docs/edit/main/docs/guides/setup.md`. Erfordert
  ebenfalls `repo.url`; leer lassen, um Bearbeiten-Links wegzulassen und
  trotzdem das Icon in der Kopfzeile zu zeigen.

=== "YAML"
    ```yaml title="bxsites.yaml"
    repo: { url: "https://github.com/acme/docs", editUri: "edit/main/docs/" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "repo": { "url": "https://github.com/acme/docs", "editUri": "edit/main/docs/" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [repo]
    url = "https://github.com/acme/docs"
    editUri = "edit/main/docs/"
    ```

## `social`

Ein Array von Social-/externen Links, die in der Fußzeile gerendert werden
(siehe [`footer`](#footer) - hat ohne diesen keine Wirkung). Jeder Eintrag
braucht eine `url`; `icon` wählt aus einem kleinen integrierten Icon-Set
(`github`, `twitter`/`x`, `youtube`, `linkedin`, `facebook`, `bluesky`,
`threads`, `slack`, `patreon`, `rss`, `email`, mit einem generischen
Link-Symbol als Fallback für alles andere), und `label` setzt den
barrierefreien Namen/Tooltip des Links (Standard ist `icon`, dann
`"Link"`).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    social:
      - { url: "https://twitter.com/acme", icon: twitter, label: Twitter }
      - { url: "https://acme.com/rss.xml", icon: rss, label: RSS }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"social": [
    		{ "url": "https://twitter.com/acme", "icon": "twitter", "label": "Twitter" },
    		{ "url": "https://acme.com/rss.xml", "icon": "rss", "label": "RSS" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[social]]
    url = "https://twitter.com/acme"
    icon = "twitter"
    label = "Twitter"

    [[social]]
    url = "https://acme.com/rss.xml"
    icon = "rss"
    label = "RSS"
    ```

## `footer`

`false` (Standard) - überhaupt keine Fußzeile. `true` fügt jeder Seite eine
hinzu: eine Copyright-Zeile (`© <Jahr> <Website-Name>`), die `social`-Links
(falls vorhanden) und einen "Built with BxSites"-Hinweis.

=== "YAML"
    ```yaml title="bxsites.yaml"
    footer: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "footer": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    footer = true
    ```

## `lastUpdated`

`false` (Standard) - kein Datum der letzten Aktualisierung. `true` fügt
eine "Zuletzt aktualisiert"-Zeile neben dem Bearbeiten-Link hinzu (oder für
sich allein, wenn `repo.editUri` nicht gesetzt ist), ermittelt aus dem
`git log` der eigenen Markdown-Datei jeder Seite zur Build-Zeit. Wird
stillschweigend ausgelassen für eine Seite, für die Git keine Historie
hat - ein frisches `git init` ohne bisherige Commits, ein Build aus einem
heruntergeladenen ZIP ohne `.git` überhaupt, oder Git ist auf der
Build-Maschine nicht installiert - statt den Build abzubrechen.

=== "YAML"
    ```yaml title="bxsites.yaml"
    lastUpdated: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "lastUpdated": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    lastUpdated = true
    ```

## `analytics`

Bindet Pageview-Analytics ein. Unterstützt derzeit nur Google Analytics
(`gtag.js`):

- `analytics.provider` - `"google"`, um es zu aktivieren; leer gelassen
  (Standard), wird gar kein Analytics-Skript ausgeliefert.
- `analytics.id` - die Google-Analytics-Measurement-ID (z. B.
  `"G-ABC123"`). Erforderlich, wenn `provider` `"google"` ist.

=== "YAML"
    ```yaml title="bxsites.yaml"
    analytics: { provider: google, id: "G-ABC123" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "analytics": { "provider": "google", "id": "G-ABC123" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [analytics]
    provider = "google"
    id = "G-ABC123"
    ```

## `ogImage`

Pfad/URL zu einem Standard-Social-Card-Bild, gerendert als `og:image`
(gepaart mit einem `twitter:card` von `summary_large_image`) auf jeder
Seite, die es nicht überschreibt - aufgelöst auf dieselbe Weise wie
`theme.logo` (relative Pfade werden mit `baseURL` vorangestellt, absolute
URLs unverändert verwendet). Leer gelassen (Standard) und `generateOgImages`
deaktiviert, werden gar keine `og:image`/`twitter:card`-Tags gerendert.

=== "YAML"
    ```yaml title="bxsites.yaml"
    ogImage: assets/social-card.png
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "ogImage": "assets/social-card.png" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    ogImage = "assets/social-card.png"
    ```

Die eigene `ogImage`-Frontmatter einer Seite (siehe
[Erste Schritte](getting-started.md#seiten-hinzufügen)) gewinnt für diese eine
Seite immer gegenüber diesem websiteweiten Standard.

### `generateOgImages`

`false` (Standard) - keine seitenweisen Cards. `true` rendert für jede
Seite, die noch keine eigene `ogImage`-Frontmatter hat, eine echte
1200x630-PNG-Social-Card - den Seitentitel auf dem Marken-Gradienten,
geschrieben nach `site/assets/og/<page>.png` - statt dass sich alle Seiten
ein generisches, websiteweites Bild teilen. Rein `java.awt`/`javax.imageio`
unter der Haube (Teil jeder JVM, auf der BoxLang läuft), sodass dafür kein
Headless-Browser, kein externer Dienst und kein Netzwerkzugriff zur
Build-Zeit nötig ist.

=== "YAML"
    ```yaml title="bxsites.yaml"
    generateOgImages: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "generateOgImages": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    generateOgImages = true
    ```

## `extraCss` / `extraJs`

Arrays zusätzlicher Stylesheet-/Skript-URLs, die auf jeder Seite
eingebunden werden, nach den eigenen Assets des Themes - jeder Eintrag
wird auf dieselbe Weise aufgelöst wie `theme.logo` (ein relativer Pfad
wird mit `baseURL` vorangestellt; eine absolute URL wird unverändert
verwendet). `extraJs`-Einträge werden mit `defer` geladen.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/custom.css ]
    extraJs: [ assets/custom.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/custom.css"],
    	"extraJs": ["assets/custom.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/custom.css" ]
    extraJs = [ "assets/custom.js" ]
    ```

Wenn `assets.bundle` aktiv ist (Standard), wird eine lokale
`extraCss`/`extraJs`-Liste wie die obige jeweils zu einer fingerprinted
Datei gebündelt, statt einem `<link>`/`<script>`-Tag pro Eintrag - siehe
[`assets`](#assets) unten.

## `assets`

=== "TOML"
  ```toml title="bxsites.toml" linenums="1"
  [assets]
  fingerprint = true
  bundle = true

  [assets.images]
  enabled = true
  widths = [ 400, 800, 1200, 1600 ]
  formats = [ "original", "webp" ]
  ```

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      fingerprint: true
      bundle: true
      images:
        enabled: true
        widths: [ 400, 800, 1200, 1600 ]
        formats: [ original, webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": {
    			"enabled": true,
    			"widths": [400, 800, 1200, 1600],
    			"formats": ["original", "webp"]
    		}
    	}
    }
    ```

Die Asset-Pipeline - Bildskalierung/WebP über
[bx-image](https://github.com/ortus-boxlang/bx-image) (eine erforderliche
Abhängigkeit, mitinstalliert neben bx-markdown/bx-esapi) und
CSS/JS-Bundling. Hier ist standardmäßig alles mit sinnvollen Einstellungen
aktiv - ein frisches `bxSites new`-Projekt muss davon nichts anfassen.
Siehe [Responsive Bilder](guides/images.md) für das vollständige Bild,
einschließlich dessen, was bewusst nicht abgedeckt wird (AVIF, animierte
GIFs, SVGs).

- `assets.fingerprint` - `true` (Standard). Benennt jede erzeugte
  Bildvariante und jedes CSS/JS-Bundle nach einem Inhalts-Hash (z. B.
  `screenshot-800w.a3f9c2e1.webp`, `bundle.a3f9c2e1.css`), sodass sie mit
  sicheren, weit in der Zukunft liegenden Cache-Headern ausgeliefert
  werden können - der Build eines Projekts ändert den eigenen Dateinamen
  nur, wenn sich dessen Inhalt tatsächlich ändert. Benennt die eigenen
  Originaldateien eines Projekts unter `docs/assets/` nicht um - nur von
  der Pipeline erzeugte Ausgabe wird fingerprinted, sodass alles andere,
  was ein Asset über seinen einfachen Dateinamen referenziert (eine
  `::: file`-Download-Card, ein roher Markdown-Link), unverändert
  weiterfunktioniert.
- `assets.bundle` - `true` (Standard). Verkettet `extraCss`/`extraJs` je
  zu einer fingerprinted Datei - reines BoxLang/JVM, keine
  Node/esbuild-Toolchain. Fällt exakt auf das heutige Pro-URL-
  `<link>`/`<script>`-Verhalten zurück, unverändert, sobald irgendein
  Eintrag in der Liste eine externe URL (ein CDN-Link) ist oder eine
  nicht existierende Datei benennt - siehe
  [Responsive Bilder](guides/images.md#css-js-bundling).
- `assets.images.enabled` - `true` (Standard). Jedes geeignete
  `docs/assets/**`-Bild (`.png`/`.jpg`/`.jpeg`) erhält über bx-image
  erzeugte Skalierungs-/WebP-Varianten, und jedes passende `<img>` wird zu
  einem `<picture>` mit `srcset` umgeschrieben. Auf `false` setzen, um auf
  einfaches, unverarbeitetes Kopieren von Bildern zurückzufallen, genau
  wie vor Einführung dieser Funktion.
- `assets.images.widths` - zu erzeugende Breakpoints, in Pixeln. Eine
  Breite, die der eigenen Breite eines Bildes entspricht oder darüber
  liegt, wird für dieses Bild automatisch übersprungen - nichts wird
  jemals hochskaliert.
- `assets.images.formats` - `"original"` behält das Quellformat als
  `<img>`-Fallback bei; `"webp"` fügt eine gleich große
  `<source type="image/webp">`-Variante hinzu. Beide standardmäßig aktiv.

## `mermaid`

`false` (Standard) - überhaupt keine
[Mermaid](https://mermaid.js.org/)-Diagrammunterstützung ausgeliefert.
`true` lädt `mermaid.js` clientseitig und rendert jeden mit ` ```mermaid `
eingezäunten Codeblock als Diagramm. Siehe
[Markdown-Erweiterungen](guides/markdown.md#diagramme) für die Syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    mermaid: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mermaid": true }
    ```

## `math`

`false` (Standard) - kein [KaTeX](https://katex.org/) ausgeliefert. `true`
lädt es clientseitig und setzt `$...$`/`$$...$$` direkt aus dem Markdown
einer Seite. Siehe [Markdown-Erweiterungen](guides/markdown.md#mathematik) für
die Syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    math: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "math": true }
    ```

Admonitions (Hinweis-/Warnungs-/Tipp-artige Callout-Boxen), Content-Tabs
und die Fenced-Code-Annotationen `hl_lines`/`linenums`/`title` sind im
Markdown jeder Seite immer verfügbar, ohne Konfiguration nötig - siehe
[Markdown-Erweiterungen](guides/markdown.md#admonitions).

## `openapi`

`false` (Standard) - kein [Swagger UI](https://swagger.io/tools/swagger-ui/)
ausgeliefert. `true` lädt es clientseitig und rendert jeden
`::: openapi src="..."`-Content-Block als interaktives Widget für die
referenzierte OpenAPI-/Swagger-Spezifikation (JSON oder YAML). Siehe
[OpenAPI / Swagger](guides/openapi.md) für die Syntax.

=== "YAML"
    ```yaml title="bxsites.yaml"
    openapi: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "openapi": true }
    ```

## `plugins`

`[]` (Standard) - ein Array von BoxLang-Modulnamen, die als Plugins
aktiviert werden. Das Installieren eines Plugin-Moduls (`box install`)
aktiviert es niemals von selbst als Plugin; es muss auch hier benannt
werden. Siehe [Plugins](guides/plugins.md) dafür, wie man eines schreibt.

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

## `i18n`

Metadaten für die Locale-Ordner-Konvention
[`docs/i18n/<code>/`](guides/i18n.md) - eine Locale wird automatisch
gebaut, sobald ihr Ordner existiert; `i18n` liefert nur ihr
Anzeige-Label/ihre Schreibrichtung für den Sprachumschalter.

- `i18n.defaultLocale` - `{ "code", "label", "flag", "strings" }` für den
  eigenen regulären `docs/`-Baum des Projekts, standardmäßig
  `{ "code": "en", "label": "English" }`. Muss nur gesetzt werden, wenn
  deine Standard-Locale nicht Englisch ist.
- `i18n.locales` - `[]` (Standard) - ein Array aus
  `{ "code", "label", "dir", "flag", "strings" }` für jede andere Locale.
  `code` dient zugleich als Ordnername in `docs/i18n/<code>/` und als
  gebautes URL-Präfix - nur Buchstaben/Ziffern/Bindestriche (`es`,
  `pt-BR`, `zh-Hans`). `dir` ist `"ltr"` (Standard) oder `"rtl"`. `flag`
  ist ein optionales Emoji-Override für das Flaggen-Icon des
  Sprachumschalters - die meisten gängigen Codes lösen bereits von selbst
  in eine sinnvolle Flagge auf. `strings` überschreibt die eigenen
  Theme-Chrome-UI-Texte dieser Locale (Suchplatzhalter, "Auf dieser
  Seite", die 404-Seite, ...) - siehe
  [Internationalisierung](guides/i18n.md#theme-chrome-ui-texte) für die
  vollständige Liste der Schlüssel; `de`/`es`/`it`/`ja` bringen bereits
  eine eingebaute Übersetzung mit, `strings` wird also nur zum
  Überschreiben eines Schlüssels oder für eine weitere Locale gebraucht.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    i18n:
      defaultLocale: { code: en, label: English }
      locales:
        - { code: es, label: Español }
        - { code: ar, label: العربية, dir: rtl }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": [
    			{ "code": "es", "label": "Español" },
    			{ "code": "ar", "label": "العربية", "dir": "rtl" }
    		]
    	}
    }
    ```

Siehe [Internationalisierung](guides/i18n.md) für das vollständige Bild -
Fallback bei nicht übersetzten Seiten, den Sprachumschalter und was noch
nicht übersetzt wird.

## `blog`

Optionen für den [Blog](guides/blog.md) - selbst eine
Convention-over-Configuration-Funktion (`docs/blog/posts/`), dafür ist
kein Schlüssel hier nötig, um ihn einzuschalten.

- `blog.postsPerPage` - `10` (Standard) - wie viele Beiträge pro Seite auf
  `/blog/`, jeder Kategorieseite und jeder `/blog/archive/<year>/`-Seite
  angezeigt werden, bevor es zu `.../page/2/` weitergeht.
- `blog.feed` - `true` (Standard) - ob `/blog/feed.xml` (RSS 2.0)
  geschrieben wird. Nur sinnvoll mit einer absoluten `baseURL`, dieselbe
  Voraussetzung wie bei `sitemap.xml`.
- `blog.feedLimit` - `25` (Standard) - begrenzt `/blog/feed.xml` auf so
  viele der neuesten Beiträge. `0` bedeutet unbegrenzt (jeder Beitrag,
  vollständig). Die meisten Feed-Reader interessieren sich nur für das
  Neueste, daher verschwendet ein unbegrenzter Feed bei einem Blog mit
  Hunderten Beiträgen bei jedem Abruf nur Bandbreite - siehe
  [Blog: Feed](guides/blog.md#feed).

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

Siehe [Blog](guides/blog.md) für Beitrags-/Autoren-Frontmatter,
Kategorien, Featured Images und SEO-/Social-Metadaten.

## `variables`

`{}` (Standard) - ein Objekt wiederverwendbarer Werte, in beliebiger Form,
referenziert von jeder Markdown-Seite aus als `{{ dotted.path }}`. Siehe
[Variablen & Magische Funktionen](guides/variables-and-functions.md).

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product: { name: "BoxLang", supportEmail: "support@example.com" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": { "name": "BoxLang", "supportEmail": "support@example.com" }
    	}
    }
    ```

```markdown title="docs/index.md"
Welcome to {{ company }}! We build {{ product.name }}.
```

Eine Datei `docs/functions.bxs` (kein eigener Konfigurationsschlüssel - per
Konvention, genau wie `docs/nav.json`/`docs/blog/authors.yml`) fügt neben
`variables` BoxLang-"magische Funktionen" hinzu - aufrufbar auf dieselbe
Weise, als `{{ $name(...) }}`. Siehe
[Variablen & Magische Funktionen](guides/variables-and-functions.md#magische-funktionen).

Ein Ordner `docs/data/*.yaml`/`.yml`/`.json` (ebenfalls kein eigener
Konfigurationsschlüssel) fügt strukturierte, verschachtelte/array-förmige
Daten hinzu - erreichbar als `{{ data.<file>.<key> }}` - für alles, wofür
die eigene flache Form von `variables` nicht gut passt (eine Teamliste,
eine Preistabelle). Siehe [Datendateien](guides/data-files.md).

## Versionierung

Versionierte Docs sind Convention over Configuration - dafür gibt es
keinen `bxsites.yaml`-Schlüssel. Füge einen `docs/versions/<name>/`-Ordner
hinzu, und er wird automatisch als eigener Doc-Baum gebaut, mit einem
Versionsumschalter, den jedes Theme kostenlos rendert, sobald mehr als
eine Version existiert. Siehe [Versionierung](guides/versioning.md) für
das vollständige Bild - eine neue Version mit `version:new` anlegen, wie
Versionen sortieren und bauen, und was außen vor bleibt (Suche pro Baum
begrenzt, kein Deprecated-/EOL-Flag).
