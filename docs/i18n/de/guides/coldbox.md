---
title: ColdBox-Anwendungen
order: 6.8
icon: phosphor-duotone:tree-structure
tags: [anleitungen, boxlang, coldbox, api, integration]
---

# ColdBox-Anwendungen

`bxSites coldbox` dokumentiert eine
[ColdBox](https://coldbox.ortusbooks.com)-Anwendung anhand ihrer
Konventionen auf der Festplatte: Routen, Event-Handler, Models und
WireBox-Mappings, Module, Interceptors und geplante Tasks.

Die Anwendung wird dabei nie gestartet. Nichts muss kompiliert sein, keine
Datasource erreichbar, keine Umgebungsvariable gesetzt - auf einem
CI-Runner läuft es also genauso wie lokal. Was dieser Ansatz nicht sehen
kann, steht unten unter [Grenzen](#grenzen).

## Schnellstart

Im Wurzelverzeichnis einer ColdBox-App, die auch ein bx-sites-Projekt
enthält:

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites build
```

Die Seiten landen unter `docs/api/coldbox/`. Liegt die Anwendung woanders:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app
```

## Konfiguration

Alles ist optional; das vollständige Schema steht unter
[`coldbox`](../configuration.md#coldbox).

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [coldbox]
    appRoot = "."
    pagePathPrefix = "api/coldbox"
    tags = [ "api", "coldbox" ]
    include = [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    ```

`include` bestimmt, welche Seitengruppen erzeugt werden; ein weggelassener
Eintrag wird komplett übersprungen. Zu jedem Schlüssel gibt es ein Flag:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app --include=routes,handlers \
	--pagePathPrefix=reference --tags=reference,api
```

## Was erzeugt wird

```
docs/api/coldbox/
├── index.md               # Überblick und Kennzahlen
├── routes.md              # alle Routen in Deklarationsreihenfolge
├── handlers/
│   ├── index.md
│   ├── Main.md
│   └── api/Orders.md      # Modul-Handler liegen unter ihrem Modul
├── models/
│   ├── index.md           # Models plus die Mappings des Binders
│   └── UserService.md
├── modules/
│   ├── index.md
│   └── api.md
├── interceptors.md
└── scheduled-tasks.md
```

### Routen

Alle Routen des App-Routers und jedes Modul-Routers, in der Reihenfolge
ihrer Deklaration - ColdBox nimmt die erste passende, die Reihenfolge ist
also bedeutungstragend und die Seite behält sie bei.
`resources()`/`apiResources()` werden in die einzelnen Routen aufgelöst,
die ColdBox daraus erzeugt, und Modul-Routen tragen den Entry Point, unter
dem sie tatsächlich hängen.

### Handler

Eine Seite je Handler mit den Routen, die ihn erreichen, und der Action,
auf die jede zeigt, dann seine aufrufbaren Actions mit Doc-Kommentaren und
Argumenten. ColdBox' eigene Lifecycle-Hooks (`preHandler`,
`aroundHandler`, `onError` und Verwandte) bekommen einen eigenen Abschnitt,
statt als per URL erreichbare Actions gelistet zu werden; `init` und
private Methoden bleiben außen vor.

### Models

Eine Seite je Model: Scope, was WireBox hineininjiziert, die eigenen
Properties und die öffentlichen Methoden. Injizierte Abhängigkeiten stehen
getrennt von einfachen Properties, denn `property name="x" inject="y"` ist
Verdrahtung, keine Daten. Der Index ergänzt die deklarierten Mappings des
Binders.

### Module, Interceptors und geplante Tasks

Eine Modulseite trägt, was ihre `ModuleConfig` deklariert - Autor, Version,
Entry Point, Abhängigkeiten - und was das Modul zur Anwendung beisteuert,
jeweils verlinkt auf die beschreibende Seite. Die Interceptor-Seite deckt
beide Wege ab, auf denen ColdBox sie findet: was `config/ColdBox`
registriert und was `interceptors/` deklariert, mit den öffentlichen
Methoden jeder Klasse als den Interception Points, die sie sind. Die
Task-Seite liest `config/Scheduler` und den Scheduler jedes Moduls.

## Reichhaltigere Seiten mit DocBox

Routen, Module, Interceptors und die Struktur der App kommen allein aus den
Konventionen. Details je Klasse - die Actions eines Handlers, die Methoden
eines Models, deren Argumente und Doc-Kommentare - kommen aus
[DocBox](docbox.md). Mit installiertem `bx-docbox` werden diese Seiten
deutlich reichhaltiger:

```bash frame="terminal" title="Terminal"
install-bx-module bx-docbox
```

Ohne das Modul läuft der Verb trotzdem und listet weiterhin jeden Handler,
jedes Model, jede Route und jedes Modul; die Seiten sagen dann schlicht,
was fehlt und wie man es bekommt.

## Grenzen

Statisches Lesen hat eine harte Grenze, und die sollte man benennen. Was
eine Anwendung erst zur Laufzeit entscheidet, steht nicht auf der Platte:

- Eine Route, deren Pattern oder Ziel aus einer Variablen gebaut oder in
  einer Schleife registriert wird.
- Ein WireBox-Mapping oder ein geplanter Task mit berechnetem Namen.
- Ein Modul, das beim Start installiert wird, statt unter `modules_app/`
  eingecheckt zu sein.
- Was `mapDirectory()` tatsächlich registriert - das hängt davon ab, was
  beim Start auf der Platte liegt.

All das wird übersprungen statt geraten: Eine erzeugte Seite untertreibt
lieber, als etwas Falsches zu behaupten.

## Nicht aus Gradle oder Maven verfügbar

Und zwar bewusst. Eine ColdBox-Anwendung wird über CommandBox gebaut und
betrieben, nie über ein Java-Build-Tool - es gibt daher weder einen
`bxSitesColdBoxDoc`-Task noch ein `bxsites:coldbox`-Goal. Den
[DocBox](docbox.md)-Generator bieten [Gradle-](gradle-plugin.md) und
[Maven-Plugin](maven-plugin.md) hingegen an, denn der dokumentiert
BoxLang-/CFML-Klassen, die wirklich im gebauten JVM-Projekt liegen.
