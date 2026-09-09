---
title: DocBox-API-Referenz
order: 6.7
icon: phosphor-duotone:brackets-curly
tags: [anleitungen, boxlang, docbox, api, integration]
---

# DocBox-API-Referenz

`bxSites docbox` macht aus deinen BoxLang-/CFML-Klassen eine
durchsuchbare, im Theme gestaltete API-Referenz innerhalb der eigenen
Website - das BoxLang-Gegenstück zu den Javadoc-Generatoren, die das
[Gradle-](gradle-plugin.md) und das [Maven-Plugin](maven-plugin.md)
Java-Projekten bieten.

BoxLang wird dabei nicht selbst geparst.
[DocBox](https://docbox.ortusbooks.com) erledigt das bereits gut, also
läuft hier DocBox' eigene JSON-Strategie, deren Ergebnis in ganz normale
Markdown-Seiten übersetzt wird. Sie landen im Content-Verzeichnis wie jede
andere Seite, der nächste `build` gestaltet sie im Theme, nimmt sie in den
Suchindex auf und liefert sie wie handgeschriebene Inhalte aus.

## Voraussetzung

Das Modul `bx-docbox` muss in der BoxLang-Laufzeit installiert sein:

```bash frame="terminal" title="Terminal"
# OS-Binary
install-bx-module bx-docbox

# CommandBox
box install bx-docbox
```

Fehlt es, bricht der Verb mit einer verständlichen Meldung ab statt mit
einem Stacktrace.

## Schnellstart

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites build
```

Ganz ohne Konfiguration dokumentiert `docbox` diejenigen der üblichen
BoxLang-Quellordner, die dein Projekt tatsächlich hat - `models`,
`handlers`, `bifs`, `components`, `interceptors` - und schreibt die Seiten
nach `docs/api/docbox/`.

## Konfiguration

Alles ist optional; das vollständige Schema steht unter
[`docbox`](../configuration.md#docbox).

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "Meine API"
      mappings:
        models: models
        bifs: bifs
      excludes: "tests|build"
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "Meine API",
    		"mappings": { "models": "models", "bifs": "bifs" },
    		"excludes": "tests|build",
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [docbox]
    projectTitle = "Meine API"
    excludes = "tests|build"
    pagePathPrefix = "api/docbox"
    tags = [ "api", "docbox" ]

    [docbox.mappings]
    models = "models"
    bifs = "bifs"
    ```

Zu jedem Schlüssel gibt es ein Flag, das ihn für einen Lauf überschreibt:

```bash frame="terminal" title="Terminal"
bxSites docbox --mappings:models=models --projectTitle="Meine API" \
	--pagePathPrefix=api/classes --tags=api,classes --excludes=tests
```

`--jsonDir=<pfad>` behält DocBox' eigene JSON-Ausgabe, statt sie zu
verwerfen.

## Was erzeugt wird

Drei Arten von Seiten: ein Überblicksindex, ein Index je Package und eine
Seite je Klasse.

```
docs/api/docbox/
├── index.md                    # alle Packages und Klassen
├── models/
│   ├── index.md                # Klassen dieses Packages
│   ├── UserService.md
│   └── security/
│       ├── index.md
│       └── Auth.md
```

Eine Klassenseite zeigt den Docblock der Klasse, ihre deklarierten
`property`-Blöcke und ihre Funktionen nach Sichtbarkeit gruppiert -
Konstruktor zuerst, dann public, package und private - jeweils mit
vollständiger Signatur, Hinweistext, Parametertabelle und `@return`-Text.
Die Mitglieder stecken in derselben Alpine.js-Filterleiste wie die
Javadoc-Seiten, damit auch eine lange Klasse überschaubar bleibt.

## In die Navigation einbinden

Die erzeugten Seiten sind normaler Inhalt und erscheinen von selbst in der
automatischen Verzeichnisnavigation. Für eine bewusste Platzierung nimm den
Index in deine eigene [`nav`](../configuration.md#nav) auf:

=== "YAML"
    ```yaml title="bxsites.yaml"
    nav:
      - title: Referenz
        children:
          - api/docbox/index.md
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"nav": [
    		{ "title": "Referenz", "children": [ "api/docbox/index.md" ] }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [[nav]]
    title = "Referenz"
    children = [ "api/docbox/index.md" ]
    ```

## Was bewusst fehlt

- **Keine Querverweise zwischen Klassen.** Ein `extends`/`implements`-Ziel
  oder ein in einer Signatur genannter Typ erscheint als Inline-Code, auch
  wenn diese Klasse eine eigene Seite hat.
- **Keine geerbten Mitglieder.** Nur was eine Klasse selbst deklariert -
  genau wie in DocBox' eigener JSON-Ausgabe.
- **Keine Navigationsverdrahtung.** Die Seiten landen unter
  `pagePathPrefix`; die Platzierung bleibt dir überlassen.

## Woher die Metadaten kommen

DocBox' JSON-Strategie serialisiert Name, Package, Typ, `extends` und
Funktionen einer Klasse - aber nicht ihre deklarierten `property`-Blöcke,
die implementierten Interfaces, ihre Annotationen auf Klassenebene oder den
`@return`-Text einer Funktion. Diese vier sind für eine BoxLang-Referenz
wichtig, Properties vor allem, weil sie jedes `accessors`-Feld und jede
WireBox-`inject`-Angabe tragen. bx-sites liest sie deshalb aus denselben
Klassenmetadaten nach, die DocBox selbst verwendet hat, und führt sie vor
dem Rendern wieder zusammen. Nichts wird doppelt geparst.

## Aus Gradle oder Maven

Beide Build-Plugins bieten diesen Generator ebenfalls an, für ein
JVM-Projekt mit BoxLang-/CFML-Quellen - siehe
[Gradle-Plugin](gradle-plugin.md#boxlang-doc-generation) und
[Maven-Plugin](maven-plugin.md#boxlang-doc-generation).

Eine ColdBox-Anwendung zu dokumentieren ist ein eigener Verb - siehe
[ColdBox-Anwendungen](coldbox.md).
