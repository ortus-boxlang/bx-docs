---
title: Provider CLI
order: 6.2
icon: phosphor-duotone:terminal-window
tags: [guide, plugin, cli]
---

# Provider CLI

Un [plugin](plugins.md) si aggancia al ciclo di vita della *build* -
config, nav, markdown/HTML di pagina, post-build. Un **provider CLI** è
il punto di estensione gemello per il ciclo di vita dei *comandi*:
consente a un modulo BoxLang installato e attivato di registrare propri
comandi `bxSites <verbo>`, senza toccare `bx-sites` stesso.

Stesso modello di attivazione di un plugin - un modulo aderisce per nome,
tramite l'array [`plugins`](../configuration.md#plugins) proprio di
`bxsites.yaml`:

```yaml title="bxsites.yaml"
plugins: [ myBxSitesAddon ]
```

Un modulo può implementare `models/BxSitesPlugin.bx`,
`models/BxSitesCliProvider.bx`, entrambi, o nessuno dei due - installare/
attivare un modulo è un passaggio; quali contratti implementa decide cosa
estende realmente.

## Scrivere un provider CLI

Un provider CLI richiede esattamente una cosa oltre ai soliti
`box.json`/`ModuleConfig.bx`: una classe `models/BxSitesCliProvider.bx`
che espone un singolo metodo `verbs()`, che restituisce uno struct nome
verbo → informazioni di dispatch:

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

Ogni classe di dispatch segue esattamente la stessa forma di una classe
di verbo del core sotto il proprio `models/cli/` di `bx-sites` - una
`struct function run( struct options )` che restituisce
`{ exitCode, message }`:

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

### Il suffisso `@myBxSitesAddon` è obbligatorio

Un semplice percorso puntato come `"models.cli.cloud.Publish"` si risolve
solo relativamente al root di modulo proprio di `bx-sites` - è così che i
verbi del core fanno riferimento a `models/cli/Build.bx` e simili, ma
**non può** raggiungere un modulo diverso. Le proprie classi di verbo di
un provider devono sempre fornire da sé il proprio suffisso `@<mapping>`
di modulo, esattamente come mostrato sopra. Questo è anche il motivo per
cui registrarsi nella tabella letterale dei verbi propria di `bx-sites`
non è qualcosa in cui un provider possa intrufolarsi - il percorso della
classe deve nominare esplicitamente il proprio modulo.

## Nomi dei verbi: a token singolo e a due token ("composto")

Un nome di verbo viene registrato come un'unica stringa unita da due
punti, la stessa convenzione già usata dal core per
`post:new`/`i18n:status`/`page:rename`. `bxSites` accetta anche
l'equivalente di **due token argv separati da spazio** come zucchero
sintattico sulla stessa registrazione - `bxSites cloud publish` e
`bxSites cloud:publish` fanno dispatch in modo identico, non appena
`"cloud:publish"` è un nome di verbo registrato. Non c'è un meccanismo di
registrazione a due parole separato da imparare; registra
`"cloud:publish"`, ed entrambe le grafie funzionano gratuitamente.

## Precedenza e modalità di fallimento

- **Il core vince sempre.** Se un provider registra un nome di verbo che
  entra in collisione con uno dei verbi incorporati di `bx-sites`, viene
  fatto il dispatch del verbo del core e la voce del provider viene
  ignorata silenziosamente - un provider può aggiungere nuovi comandi,
  mai oscurarne uno esistente.
- **In caso di collisione tra due provider, vince il primo.** Se due
  moduli attivati diversi registrano lo stesso nome di verbo, vince
  quello elencato per primo nell'array `plugins` di `bxsites.yaml`.
- **La discovery non rompe mai il dispatch del core.** Un `bxsites.yaml`
  mancante o malformato, un progetto che non esiste ancora (es. eseguire
  `--help` fuori da qualsiasi progetto), un modulo elencato in `plugins`
  senza un proprio `BxSitesCliProvider.bx`, o un provider il cui
  `verbs()` genera un errore - nessuno di questi casi è un errore.
  Vengono trattati tutti allo stesso modo di un plugin non attivato: la
  tabella dei verbi del core resta semplicemente intatta, e ogni comando
  incorporato di `bxSites` continua a funzionare da solo.

## Un esempio minimo

```text title="Struttura di myBxSitesAddon/"
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

Vedi [Plugin](plugins.md) per il lato ciclo di vita della build dello
stesso modulo, e la [guida di riferimento CLI](../cli-reference.md) per
ogni verbo che il core stesso include.
