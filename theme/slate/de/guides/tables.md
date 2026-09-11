---
title: Tabellen
order: 4.6
icon: phosphor-duotone:table
tags: [anleitungen, markdown]
---

# Tabellen

Standard-[GFM](https://github.github.com/gfm/#tables-extension-)-Pipe-Tabellen,
zusätzlich zu allem in [Markdown-Erweiterungen](markdown.md) - keine
`bxsites.yaml`-Konfiguration nötig, immer aktiv:

```markdown title="Example" linenums="1"
| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |
```

Was so gerendert wird:

| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |

Eine Zeile aus `---` unter der Kopfzeile schaltet die Tabelle ein; setze
Doppelpunkte in diese Trennzeile, um die Ausrichtung pro Spalte zu steuern
- `:---` links, `:---:` zentriert, `---:` rechts (keine Doppelpunkte lässt
jede Spalte standardmäßig linksbündig).

## Zellinhalt ist reguläres Inline-Markdown

`code`, **fett**, *kursiv* und [Links](../index.md) funktionieren alle
innerhalb einer Zelle genau so, wie sie es überall sonst auf der Seite
tun:

```markdown title="Example" linenums="1"
| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |
```

Was so gerendert wird:

| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |

## Ein Pipe-Zeichen innerhalb einer Zelle escapen

Ein literales `|` im reinen Text einer Zelle braucht einen Backslash,
`\|` - ein nicht escapetes `|` wird stattdessen als Trenner der nächsten
Spalte gelesen:

```markdown title="Example" linenums="1"
| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |
```

Was so gerendert wird:

| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |

Ein `|` innerhalb von Inline-Code muss überhaupt nicht escaped werden -
der Code-Span (`` `a | b` ``) schützt es bereits:

| Expression | Meaning |
| --- | --- |
| `a | b` | bitwise OR |

## Kurze und lange Zeilen

Eine Datenzeile muss nicht exakt der Spaltenzahl der Kopfzeile
entsprechen - eine zu kurze Zeile wird mit leeren Zellen aufgefüllt, und
eine zu lange Zeile verliert ihre überzähligen Zellen stillschweigend,
beides gesteuert über
`tableOptions.appendMissingColumns`/`discardExtraColumns` weiter unten:

```markdown title="Example" linenums="1"
| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |
```

Was so gerendert wird:

| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |

## Das Parsen konfigurieren

Der Umgang mit kurzen/langen Zeilen, die eigene Strenge der
`---`-Trennzeile und die CSS-Klasse, mit der jede `<table>` gerendert
wird, werden alle über
[`markdown.tableOptions`](../configuration.md#markdown) in `bxsites.yaml`
gesteuert; die auf dieser Seite durchgehend gezeigten Standardwerte sind
fast immer das, was du willst.

## Responsives Scrollen und eine fixierte Kopfzeile

Jede gerenderte Tabelle wird automatisch in ein `.bxsites-table-wrap`-Div
eingepackt - keine `bxsites.yaml`-Konfiguration, kein zusätzliches
Markdown nötig. Das gibt einer breiten Tabelle ihre eigene horizontale
Scrollleiste, statt die Seite zu sprengen, und begrenzt hohe Tabellen (ab
einer `max-height`) auf eine feste Höhe mit eigener vertikaler
Scrollleiste, wobei die Kopfzeile fixiert bleibt, während der Rumpf
darunter scrollt - eine kurze Tabelle wie die obigen bekommt nie eine
Scrollleiste, weil sie ohnehin schon passt. Ein eigenes `theme/`-Override
kann `.bxsites-table-wrap` (insbesondere seine `max-height`) genau wie
jede andere CSS-Klasse umgestalten.

## Theming

Jedes integrierte Theme rendert eine Tabelle als eigenständige Karte -
ein abgerundeter, umrandeter Wrapper, eine getönte Kopfleiste,
waagerechte Zeilentrenner ohne senkrechte Gitterlinien und eine
Zebra-/Hover-Tönung auf den Zeilen - und zeichnet all das aus sechs
CSS-Custom-Properties, die pro Modus (`:root` und `[data-theme="dark"]`)
im eigenen `assets/style.css` des Themes deklariert sind:

| Token | Was es zeichnet |
| --- | --- |
| `--bxsites-table-bg` | Die Oberfläche der Karte, hinter jeder Zeile |
| `--bxsites-table-head-bg` | Die Kopfzeile und das Filterfeld darüber |
| `--bxsites-table-head-text` | Die Beschriftungen der Kopfzeile |
| `--bxsites-table-border` | Die Kontur der Karte und die Zeilentrenner |
| `--bxsites-table-stripe-bg` | Gerade Zeilen - eine Alpha-Tönung über der Kartenoberfläche |
| `--bxsites-table-hover-bg` | Die Zeile unter dem Mauszeiger - dieselbe, etwas kräftiger |

Da jedes davon in beiden Modi deklariert ist, folgt eine Tabelle dem
Theme-Umschalter wie der Rest der Seite, statt auf einer dunklen Seite
eine weiße Platte im Light-Mode zurückzulassen. Jedes davon lässt sich
über [`extraCss`](themes.md#farben-anpassen-ohne-ein-theme-zu-überschreiben) umlenken
- ohne Theme-Override:

```css title="docs/assets/brand.css" linenums="1"
[data-theme="dark"] {
	--bxsites-table-head-bg: #241b2e;
	--bxsites-table-hover-bg: rgba(167, 139, 250, 0.12);
}
```

Halte `--bxsites-table-stripe-bg`/`-hover-bg` als Alpha-Farben: Sie
werden über das gezeichnet, was `--bxsites-table-bg` setzt, ein deckender
Wert überdeckt die Kartenoberfläche also, statt sie zu tönen. Alles
jenseits von Farbe - Innenabstände, der Eckenradius, die
Großbuchstaben-Beschriftungen der Kopfzeile - ist ein echter
`theme/`-Override, genau wie jedes andere CSS in einem Theme.

## Über reine Daten hinaus

Zwei weitere Rezepte bauen direkt auf einer einfachen Tabelle wie den
obigen auf:

- Brauchst du einen Status-Chip oder eine Sternebewertung in einer
  Zelle? Siehe [Visualizer-Rezepte](variables-and-functions.md#visualizer-rezepte).
- Soll ein Leser eine Tabelle tatsächlich clientseitig sortieren oder
  filtern können, statt sie nur zu lesen? Siehe [Eine sortierbare,
  filterbare Tabelle](interactivity.md#eine-sortierbare-filterbare-tabelle).
