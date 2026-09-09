---
title: Variablen & Magische Funktionen
order: 12
icon: phosphor-duotone:magic-wand
tags: [anleitungen, variablen, funktionen]
---

# Variablen & Magische Funktionen

Zwei kleine, verwandte Funktionen, um wiederkehrende Fakten und
wiederkehrende Logik aus deinem Markdown herauszuhalten: **wiederverwendbare
Variablen**, einmal in `bxsites.yaml` definiert und mit `{{ }}` in jede Seite
eingesetzt, und **magische Funktionen**, kleine BoxLang-Helfer, die du einmal
in `docs/functions.bxs` schreibst und auf dieselbe Weise aufrufst - überall,
ganz ohne Import, ohne Plugin, ohne Verdrahtung.

Beide teilen sich eine Syntax:

```text
{{ dotted.path }}          # a reusable variable
{{ $name(arg1, arg2) }}    # a magic function call
```

## Wiederverwendbare Variablen

Füge `bxsites.yaml` einen `variables`-Block hinzu - in beliebiger Form, flach
oder verschachtelt:

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product:
        name: "BoxLang"
        supportEmail: "support@example.com"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": {
    			"name": "BoxLang",
    			"supportEmail": "support@example.com"
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [variables]
    company = "Ortus Solutions"

    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

Referenziere davon dann alles, per Punktpfad, von jeder Markdown-Seite aus:

```markdown title="docs/index.md"
# Welcome to {{ company }}

We build {{ product.name }} tools. Need help? Write us at
{{ product.supportEmail }}.
```

baut zu:

```html
<h1>Welcome to Ortus Solutions</h1>
<p>We build BoxLang tools. Need help? Write us at support@example.com.</p>
```

Eine `{{ }}`-Variable wird einmal, zur Build-Zeit, gegen das aufgelöst, was
`bxsites.yaml`s eigener `variables`-Block gerade dann enthält - benenne ein
Produkt um, aktualisiere eine Support-Adresse oder erhöhe ein Jahr an einer
einzigen Stelle, und jede Seite, die die Variable nutzt, übernimmt die
Änderung beim nächsten Build. Siehe
[`variables`](../configuration.md#variables) in der
Konfigurationsreferenz.

## Magische Funktionen

Füge eine Datei `docs/functions.bxs` hinzu (oder `src/functions.bxs`, wenn
dein Projekt `src/` verwendet - siehe
[Erste Schritte](../getting-started.md)) - ein einfaches BoxLang-Skript.
Jede Funktion, die du mit einem führenden `$` benennst, wird zu einer
*magischen Funktion*: aufrufbar aus `{{ }}` in Markdown, und aufrufbar bar,
direkt, aus den eigenen [`theme/`](themes.md#ein-theme-überschreiben)
`.bxm`-Überschreibungen eines Projekts.

```bx title="docs/functions.bxs" linenums="1"
function $shout( text ) {
	return uCase( arguments.text ) & "!"
}

function $badge( label, kind = "info" ) {
	return '<span class="badge bg-' & arguments.kind & '">' & arguments.label & '</span>'
}
```

```markdown title="docs/index.md"
{{ $shout('this is important') }}

Status: {{ $badge('Stable', 'success') }}
```

baut zu:

```html
<p>THIS IS IMPORTANT!</p>
<p>Status: <span class="badge bg-success">Stable</span></p>
```

Eine magische Funktion kann alles `toString()`-Fähige zurückgeben - reinen
Text, HTML, eine Zahl - und wird direkt in das Markdown der Seite
eingesetzt, bevor es konvertiert wird, sodass die Rückgabe von echtem HTML
(wie bei `$badge()` oben) genau so funktioniert, wie man es erwarten würde.

Eine Funktion, die in derselben `functions.bxs` *ohne* führendes `$`
deklariert wird, ist nur ein privater Helfer, gedacht dafür, nur von den
eigenen `$`-präfixierten Funktionen in derselben Datei aufgerufen zu werden
(sie werden alle in denselben Scope geladen, sodass eine die andere bar
aufrufen kann) - `{{ }}` kann eine solche Funktion niemals direkt aufrufen
(nur ein `$name(...)`-Aufrufziel wird überhaupt erkannt), und sie ist auch
nicht Teil der dokumentierten öffentlichen Oberfläche, die ein
Theme-Override aufrufen sollte, auch wenn sie dort technisch zufällig
ebenfalls erreichbar ist:

```bx title="docs/functions.bxs"
private string function formatPrice( amount ) {
	return "$" & numberFormat( arguments.amount, "9.99" )
}

function $price( amount ) {
	return formatPrice( arguments.amount )
}
```

### Eine magische Funktion aus einem Theme-Override aufrufen

Weil eine magische Funktion direkt in den Template-Scope eingebunden wird,
kann das eigene `theme/page.bxm` (oder `layout.bxm`) eines Projekts sie bar
aufrufen, ganz ohne Präfix - auf dieselbe Weise, wie es bereits
`variables.page`/`variables.siteConfig` liest:

```bx title="theme/page.bxm (excerpt)"
<p class="build-banner">#$shout( 'built with boxlang' )#</p>
```

### Kontextvariablen

Der eigene Rumpf jeder magischen Funktion kann außerdem einen festen Satz
von "unterstützenden Variablen" lesen - bar, ganz ohne benötigtes Argument -
unabhängig davon, ob sie aus `{{ }}` in Markdown oder bar aus einem
Theme-Override aufgerufen wird:

| Variable | Was sie ist |
|---|---|
| `siteConfig` | Die eigene `bxsites.yaml`-Konfiguration der Site (bereits mit Standardwerten versehen/validiert) |
| `page` | Die aktuelle Seite (siehe den Hinweis unten - nicht jedes Feld ist schon befüllt, wenn sie aus Markdown aufgerufen wird) |
| `nav` | Der eigene Navigationsbaum dieses Baums |
| `basePath` | Root-relativer Basispfad, endet mit `/` |
| `versions` | Einträge des Versionsumschalters - `[ { label, url } ]` |
| `currentVersion` | Welcher `versions`-Eintrag gerade gerendert wird |
| `locales` | Einträge des Sprachumschalters - `[ { code, label, url, dir, flag } ]` |
| `currentLocale` | Der Code welchen `locales`-Eintrags gerade gerendert wird |
| `currentLocaleDir` | `"ltr"`/`"rtl"` für die aktuelle Locale |
| `data` | Die eigenen [Datendateien](data-files.md) dieses Projekts - `docs/data/*.yaml`/`.json`, ein Schlüssel pro Datei - `{}`, wenn das Projekt keine hat |

```bx title="docs/functions.bxs"
function $sitename() {
	return siteConfig.name
}

function $pagetitle() {
	return page.title
}
```

```markdown title="docs/index.md"
Site: {{ $sitename() }}
Page: {{ $pagetitle() }}
```

**`page` ist an beiden Stellen nicht gleich vollständig.** Aus Markdown
aufgerufen, ist `page` das eigene Struct dieser konkreten Seite *wie von der
Festplatte geladen* - `title`/`description`/`tags`/`icon`/`summary`/
`ogImage`/`urlPath`/`relativePath`/`body`/etc. sind bereits vorhanden, aber
die Felder, die erst bekannt sind, sobald jede Seite im Baum fertig
konvertiert wurde - `toc`, `prevPage`/`nextPage`, `breadcrumbs`,
`editUrl`/`lastUpdated`, `iconHtml`, `markdownUrl`, `canonicalUrl`,
`course` - existieren darauf noch nicht. Bar aus `page.bxm` aufgerufen,
ist `page` das vollständig angereicherte Struct, all das eingeschlossen.

`page.course` ist immer vorhanden (ein durchweg leeres `{id:"",
courseTitle:"", lessonIndex:0, lessonTotal:0,
prevLesson:{title:"",url:""}, nextLesson:{title:"",url:""},
indexUrl:""}`-Struct für eine Seite, die nicht Teil irgendeines
[Kurses](courses.md) ist) - `prevLesson`/`nextLesson` sind auf genau
diesen einen Kurs beschränkt, unabhängig vom websiteweiten
`prevPage`/`nextPage` oben, das stattdessen immer den gesamten Nav-Baum
durchläuft. Jede andere unterstützende Variable (`siteConfig`, `nav`,
`basePath`, `versions`, `currentVersion`, `locales`, `currentLocale`,
`currentLocaleDir`) ist an beiden Stellen identisch.

### Argumentsyntax

Die Argumente eines magischen Funktionsaufrufs sind einfache, durch Kommas
getrennte Literale oder Variablenreferenzen - keine verschachtelten
Funktionsaufrufe oder Ausdrücke in dieser ersten Version:

- Zahlen: `{{ $discount(20) }}`
- Zeichenketten in Anführungszeichen: `{{ $greet('World') }}` oder
  `{{ $greet("World") }}`
- Booleans: `{{ $badge('Beta', true) }}`
- Eine `{{ }}`-lose Punktpfad-Variablenreferenz: `{{ $greet(product.name) }}`

## Visualizer-Rezepte

Eine magische Funktion, die HTML zurückgibt, ist nicht auf ein
Status-Badge beschränkt - sie ist ein universeller Weg, um visuelle
Zellen (eine Sternebewertung, einen farbigen Chip, einen
Fortschrittsbalken) zu bekommen, ganz ohne einen datenbankgestützten
Spalten-Picker - für den die git-basierte, reine Markdown-Quelle von
bx-sites kein Äquivalent hat. Die folgenden vier stammen aus der eigenen
[`docs/functions.bxs`](https://github.com/ortus-boxlang/bx-sites/blob/development/docs/functions.bxs)
dieser Site und werden live auf genau dieser Seite gerendert.

### Bewertungen

```bx title="docs/functions.bxs"
function $stars( required numeric rating, numeric max = 5 ) {
	var filled = min( max( round( arguments.rating ), 0 ), arguments.max )
	var stars = repeatString( "★", filled ) & repeatString( "☆", arguments.max - filled )
	return '<span title="' & arguments.rating & ' out of ' & arguments.max & '" style="color:##f5a623;letter-spacing:2px">' & stars & '</span>'
}
```

`` `{{ $stars(4) }}` `` rendert als: {{ $stars(4) }}

### Status-Chips

```bx title="docs/functions.bxs"
function $badge( required string label, string kind = "info" ) {
	var palette = {
		"info"    : { "bg" : "##e0edff", "fg" : "##1d4ed8" },
		"success" : { "bg" : "##dcfce7", "fg" : "##15803d" },
		"danger"  : { "bg" : "##fee2e2", "fg" : "##b91c1c" },
		"warning" : { "bg" : "##fef9c3", "fg" : "##854d0e" }
	}
	var pick = palette.keyExists( arguments.kind ) ? palette[ arguments.kind ] : { "bg" : "##f1f5f9", "fg" : "##475569" }
	return '<span style="display:inline-block;padding:0.1em 0.6em;border-radius:999px;font-size:0.85em;font-weight:600;background:'
		& pick.bg & ";color:" & pick.fg & '">' & encodeForHTML( arguments.label ) & "</span>"
}
```

`` `{{ $badge('Stable', 'success') }}` `` rendert als: {{ $badge('Stable', 'success') }} - und `` `{{ $badge('Beta', 'info') }}` ``: {{ $badge('Beta', 'info') }}

### Fortschrittsbalken

```bx title="docs/functions.bxs"
function $progress( required numeric percent ) {
	var pct = min( max( arguments.percent, 0 ), 100 )
	return '<span style="display:inline-block;width:120px;height:8px;background:##e5e7eb;border-radius:999px;overflow:hidden;vertical-align:middle"><span style="display:block;height:100%;width:'
		& pct & '%;background:##2563eb"></span></span> ' & pct & "%"
}
```

`` `{{ $progress(72) }}` `` rendert als: {{ $progress(72) }}

### Trendanzeigen

```bx title="docs/functions.bxs"
function $trend( required numeric value ) {
	var isUp = arguments.value >= 0
	var arrow = isUp ? "▲" : "▼"
	var color = isUp ? "##16a34a" : "##dc2626"
	var sign = isUp ? "+" : ""
	return '<span style="color:' & color & ';font-weight:600">' & arrow & " " & sign & numberFormat( arguments.value, "0.0" ) & "%</span>"
}
```

`` `{{ $trend(4.2) }}` `` rendert als: {{ $trend(4.2) }} - `` `{{ $trend(-1.8) }}` ``: {{ $trend(-1.8) }}

### Innerhalb einer Tabellenzelle

`{{ }}` wird gegen das rohe Markdown aufgelöst, bevor
[Tabellen](tables.md) überhaupt geparst werden, sodass jede der
obigen Funktionen innerhalb der Zellen einer Pipe-Tabelle genauso
funktioniert wie überall sonst auf der Seite:

```markdown title="Example" linenums="1"
| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |
```

Was so gerendert wird:

| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |

## Die Syntax wörtlich anzeigen

Ein `{{ }}`, das innerhalb eines eingezäunten Codeblocks angezeigt wird (drei
oder mehr Backticks, wie jedes Beispiel auf dieser Seite), bleibt völlig
unangetastet, statt aufgelöst zu werden - dieselbe Konvention, die dieses
Modul bereits für `$...$`-Mathematik und `=== "Tab"`-Content-Tabs verwendet.
Anders als bei diesen beiden ist ein `{{ }}` auch innerhalb von
*Inline*-Code geschützt (`` `{{ example }}` ``, mit einfachen oder doppelten
Backticks) - jeder Aufzählungspunkt oben, der `` `{{ $discount(20) }}` ``
inline zeigt, ist ein echtes, funktionierendes Beispiel dafür.

Ein `{{ }}`, dessen Inhalt weder wie ein Variablenpfad noch wie ein
`$name(...)`-Aufruf aussieht - etwa die eigene `{{ }}`-Syntax einer anderen
Templating-Engine, in Fließtext gezeigt - bleibt unangetastet, statt als
Fehler behandelt zu werden. Nur ein Token, das *aussieht wie* eine Variable
oder ein magischer Funktionsaufruf, aber nicht aufgelöst werden kann, lässt
den Build fehlschlagen (siehe [Fehler](#fehler) unten) - das ist
beabsichtigt, um einen echten Tippfehler zu erkennen, ohne unbeteiligten
`{{ }}`-Text als defekte Syntax misszudeuten.

## Geltungsbereich

- `functions.bxs` gilt projektweit - eine Datei, einmal geladen, derselbe
  Satz magischer Funktionen auf jeder Seite verfügbar, über den Haupt-Baum
  und jeden [Versions](versioning.md)-/[Locale](i18n.md)-Baum hinweg. Du
  musst sie nicht nach `docs/versions/<name>/` oder `docs/i18n/<code>/`
  duplizieren.
- `variables` ist ebenso ein einzelner, projektweiter `bxsites.yaml`-Block -
  er ist selbst nicht pro Locale übersetzbar. Ein mehrsprachiges Projekt,
  das unterschiedlichen Variablentext pro Sprache möchte, kann stattdessen
  zu einer magischen Funktion greifen, die auf
  `siteConfig.i18n.defaultLocale.code` verzweigt (oder den Wert einfach
  locale-neutral halten - einen Produktnamen, eine Support-E-Mail).

## Reservierte Namen

Ein `theme/page.bxm`/`layout.bxm`-Override, das eine magische Funktion bar
aufruft (`$name(...)`), funktioniert, weil jede geladene Funktion -
`$`-präfigiert oder privater Helfer gleichermaßen - direkt in denselben
eigenen Rendering-Scope dieses Templates eingebunden wird, genau neben den
eingebauten `variables.page`/`variables.siteConfig`/etc., die jedes Theme
bereits liest. Das bedeutet, dass eine Funktion aus `functions.bxs`, die
sich einen Namen mit einem davon teilt, bereits einen hat: vermeide `page`,
`nav`, `siteConfig`, `themeDir`, `basePath`, `moduleAssetsDir`, `versions`,
`currentVersion`, `locales`, `currentLocale`, `currentLocaleDir`, `strings`,
`requiredFiles`, `stringsResolver` und `data` als eigenen Namen für einen
privaten Helfer (eine `$`-präfigierte magische Funktion kann mit keinem
davon je kollidieren, da keiner von ihnen mit `$` beginnt). Siehe
[Datendateien: Geltungsbereich](data-files.md#geltungsbereich) für den
eigenen Hinweis zum reservierten Namen `data`.

## Fehler

- `BxSites.UnknownVariable` - ein `{{ dotted.path }}` (oder ein
  `$name(...)`-Argument, das wie eine Variablenreferenz aussieht) passt zu
  nichts in `bxsites.yaml`s `variables`-Block.
- `BxSites.UnknownFunction` - ein `{{ $name(...) }}`-Aufruf passt zu keiner
  `$`-präfigierten Funktion in `docs/functions.bxs`.
- `BxSites.InvalidFunctions` - `docs/functions.bxs` konnte nicht geladen
  werden (ein BoxLang-Syntaxfehler in der Datei selbst).
- `BxSites.InvalidConfig` - der Schlüssel `variables` von `bxsites.yaml` ist
  vorhanden, ist aber kein Objekt.
