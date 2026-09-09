---
title: Applicazioni ColdBox
order: 6.8
icon: phosphor-duotone:tree-structure
tags: [guide, boxlang, coldbox, api, integration]
---

# Applicazioni ColdBox

`bxSites coldbox` documenta un'applicazione
[ColdBox](https://coldbox.ortusbooks.com) a partire dalle sue convenzioni
su disco: rotte, event handler, model e mapping WireBox, moduli,
interceptor e task pianificati.

L'applicazione non viene mai avviata. Non serve compilare niente, nessuna
datasource deve essere raggiungibile e nessuna variabile d'ambiente deve
essere impostata, quindi il comportamento su un runner CI è identico a
quello locale. Ciò che questo approccio non può vedere è elencato nei
[limiti](#limiti), più sotto.

## Avvio rapido

Dalla radice di un'app ColdBox che contiene anche un progetto bx-sites:

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites build
```

Le pagine finiscono in `docs/api/coldbox/`. Se l'app sta altrove:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app
```

## Configurazione

Tutto è opzionale; lo schema completo è in
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

`include` decide quali gruppi di pagine generare; se ometti un valore quel
gruppo viene saltato del tutto. Ogni chiave ha il suo flag:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app --include=routes,handlers \
	--pagePathPrefix=reference --tags=reference,api
```

## Cosa viene generato

```
docs/api/coldbox/
├── index.md               # panoramica dell'app e conteggi
├── routes.md              # tutte le rotte, in ordine di dichiarazione
├── handlers/
│   ├── index.md
│   ├── Main.md
│   └── api/Orders.md      # gli handler di modulo stanno sotto il modulo
├── models/
│   ├── index.md           # model e i mapping del binder
│   └── UserService.md
├── modules/
│   ├── index.md
│   └── api.md
├── interceptors.md
└── scheduled-tasks.md
```

### Rotte

Tutte le rotte dichiarate dal router dell'app e da ogni router di modulo,
nell'ordine in cui sono dichiarate: ColdBox prende la prima che
corrisponde, quindi l'ordine è significativo e la pagina lo conserva.
`resources()`/`apiResources()` vengono espansi nelle singole rotte che
ColdBox genera per essi, e le rotte di un modulo riportano l'entry point su
cui sono effettivamente montate.

### Handler

Una pagina per handler, con le rotte che lo raggiungono e l'azione che
ognuna colpisce, poi le sue azioni raggiungibili con commenti di
documentazione e argomenti. Gli hook del ciclo di vita di ColdBox
(`preHandler`, `aroundHandler`, `onError` e affini) hanno una sezione
propria invece di comparire tra le azioni raggiungibili via URL; `init` e i
metodi privati restano fuori.

### Model

Una pagina per model: il suo scope, cosa WireBox gli inietta, le sue
proprietà e i suoi metodi pubblici. Le dipendenze iniettate sono separate
dalle proprietà normali, perché `property name="x" inject="y"` è
cablaggio, non dato. L'indice aggiunge i mapping dichiarati dal binder.

### Moduli, interceptor e task pianificati

La pagina di un modulo riporta ciò che dichiara il suo `ModuleConfig` -
autore, versione, entry point, dipendenze - e ciò che il modulo aggiunge
all'applicazione, con ogni voce collegata alla pagina che la descrive. La
pagina degli interceptor copre entrambi i modi in cui ColdBox li trova: ciò
che registra `config/ColdBox` e ciò che dichiara `interceptors/`, con i
metodi pubblici di ogni classe come i punti di intercettazione che sono. La
pagina dei task legge `config/Scheduler` e quello di ogni modulo.

## Pagine più ricche con DocBox

Rotte, moduli, interceptor e la forma dell'app derivano dalle sole
convenzioni. Il dettaglio per classe - le azioni di un handler, i metodi di
un model, i loro argomenti e commenti - arriva da [DocBox](docbox.md),
quindi installare `bx-docbox` rende quelle pagine molto più ricche:

```bash frame="terminal" title="Terminal"
install-bx-module bx-docbox
```

Senza di esso il verbo funziona comunque ed elenca ancora ogni handler,
model, rotta e modulo; le pagine dicono semplicemente cosa manca e come
ottenerlo.

## Limiti

La lettura statica ha un limite netto, ed è meglio dichiararlo. Ciò che
un'applicazione decide a runtime non è su disco:

- Una rotta il cui pattern o target è costruito da una variabile, o
  registrata in un ciclo.
- Un mapping WireBox o un task pianificato con nome calcolato.
- Un modulo installato all'avvio invece che versionato sotto
  `modules_app/`.
- Ciò che `mapDirectory()` registrerà davvero, che dipende da cosa c'è su
  disco all'avvio dell'app.

Tutto questo viene saltato invece che indovinato: una pagina generata
preferisce dire meno che dire il falso.

## Non disponibile da Gradle o Maven

Di proposito. Un'applicazione ColdBox si costruisce e si esegue con
CommandBox, mai con uno strumento di build Java, quindi non esiste né un
task `bxSitesColdBoxDoc` né un goal `bxsites:coldbox`. I plugin
[Gradle](gradle-plugin.md) e [Maven](maven-plugin.md) espongono invece il
generatore [DocBox](docbox.md), che documenta classi BoxLang/CFML
realmente presenti nel progetto JVM in costruzione.
