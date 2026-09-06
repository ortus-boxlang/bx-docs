---
title: CLI-Provider
order: 6.2
icon: phosphor-duotone:terminal-window
tags: [anleitungen, plugins, cli]
---

# CLI-Provider

Ein [Plugin](plugins.md) klinkt sich in den *Build*-Lifecycle ein - Config,
Nav, Seiten-Markdown/HTML, Post-Build. Ein **CLI-Provider** ist der
Schwester-Erweiterungspunkt für den *Command*-Lifecycle: Er erlaubt es
einem installierten, aktivierten BoxLang-Modul, eigene `bxSites <verb>`-
Befehle zu registrieren, ohne `bx-sites` selbst anzufassen.

Dasselbe Aktivierungsmodell wie ein Plugin - ein Modul meldet sich über
`bxsites.yaml`s eigenes [`plugins`](../configuration.md#plugins)-Array
namentlich an:

```yaml title="bxsites.yaml"
plugins: [ myBxSitesAddon ]
```

Ein Modul kann `models/BxSitesPlugin.bx`, `models/BxSitesCliProvider.bx`,
beides oder keines von beidem implementieren - ein Modul zu installieren/
aktivieren ist ein Schritt; welche Contracts es implementiert, entscheidet,
was es tatsächlich erweitert.

## Einen CLI-Provider schreiben

Ein CLI-Provider braucht genau eine Sache zusätzlich zu den üblichen
`box.json`/`ModuleConfig.bx`: eine Klasse `models/BxSitesCliProvider.bx`,
die eine einzelne Methode `verbs()` bereitstellt, die einen Struct
Verbname → Dispatch-Info zurückgibt:

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

Jede Dispatch-Klasse folgt genau derselben Form wie eine Core-Verb-Klasse
unter `bx-sites`s eigenem `models/cli/` - eine
`struct function run( struct options )`, die `{ exitCode, message }`
zurückgibt:

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

### Das Suffix `@myBxSitesAddon` ist erforderlich

Ein reiner Punktpfad wie `"models.cli.cloud.Publish"` löst sich nur
relativ zu `bx-sites`s eigenem Modul-Root auf - so verweisen Core-Verben
auf `models/cli/Build.bx` und ähnliche, aber er **kann nicht** in ein
anderes Modul hineinreichen. Die eigenen Verb-Klassen eines Providers
müssen daher immer selbst das Suffix `@<mapping>` ihres eigenen Moduls
mitliefern, genau wie oben gezeigt. Das ist auch der Grund, warum sich
kein Provider in `bx-sites`s eigene, wörtliche Verb-Tabelle einschmuggeln
kann - der Klassenpfad muss sein eigenes Modul explizit benennen.

## Verb-Namen: Ein-Token und Zwei-Token ("compound")

Ein Verb-Name wird als eine einzige, durch Doppelpunkt verbundene
Zeichenkette registriert - dieselbe Konvention, die Core bereits für
`post:new`/`i18n:status`/`page:rename` verwendet. `bxSites` akzeptiert
außerdem die äquivalenten **zwei durch Leerzeichen getrennten
Argv-Tokens** als Zucker über dieselbe Registrierung - `bxSites cloud
publish` und `bxSites cloud:publish` dispatchen identisch, sobald
`"cloud:publish"` ein registrierter Verb-Name ist. Es gibt keinen
separaten Zweiwort-Registrierungsmechanismus zu lernen; registrieren Sie
`"cloud:publish"`, und beide Schreibweisen funktionieren automatisch.

## Vorrang und Fehlerfälle

- **Core gewinnt immer.** Registriert ein Provider einen Verb-Namen, der
  mit einem der eingebauten Verben von `bx-sites` kollidiert, wird das
  Core-Verb dispatcht und der Eintrag des Providers wird stillschweigend
  ignoriert - ein Provider kann neue Befehle hinzufügen, aber nie einen
  bestehenden überschatten.
- **Bei einer Kollision zwischen zwei Providern gewinnt der erste.** Wenn
  zwei verschiedene aktivierte Module denselben Verb-Namen registrieren,
  gewinnt derjenige, der in `bxsites.yaml`s `plugins`-Array zuerst
  aufgeführt ist.
- **Discovery bricht nie den Core-Dispatch.** Eine fehlende/fehlerhafte
  `bxsites.yaml`, ein Projekt, das noch nicht existiert (z. B. `--help`
  außerhalb eines Projekts auszuführen), ein in `plugins` aufgeführtes
  Modul ohne eigenes `BxSitesCliProvider.bx`, oder ein Provider, dessen
  `verbs()` einen Fehler wirft - keiner dieser Fälle ist ein Fehler. Sie
  werden alle genauso behandelt wie ein nicht aktiviertes Plugin: Die
  Core-Verb-Tabelle bleibt einfach unangetastet, und jeder eingebaute
  `bxSites`-Befehl funktioniert weiterhin von selbst.

## Ein minimales Beispiel

```text title="myBxSitesAddon/-Struktur"
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

Siehe [Plugins](plugins.md) für die Build-Lifecycle-Seite desselben
Moduls, und die [CLI-Referenz](../cli-reference.md) für jedes Verb, das
Core selbst mitbringt.
