---
title: Riferimento API con DocBox
order: 6.7
icon: phosphor-duotone:brackets-curly
tags: [guide, boxlang, docbox, api, integration]
---

# Riferimento API con DocBox

`bxSites docbox` trasforma le tue classi BoxLang/CFML in un riferimento API
a tema e ricercabile dentro il tuo sito: la controparte BoxLang dei
generatori Javadoc che i plugin [Gradle](gradle-plugin.md) e
[Maven](maven-plugin.md) offrono ai progetti Java.

Non analizza da sé una sola riga di BoxLang.
[DocBox](https://docbox.ortusbooks.com) fa già bene quel lavoro, quindi qui
viene eseguita la sua strategia JSON e il risultato viene convertito in
normali pagine Markdown. Finiscono nella tua directory dei contenuti come
qualsiasi altra pagina, così la `build` successiva le applica il tema, le
indicizza per la ricerca e le serve come se le avessi scritte a mano.

## Prerequisito

Il modulo `bx-docbox` deve essere installato nel runtime BoxLang:

```bash frame="terminal" title="Terminal"
# Binario di sistema
install-bx-module bx-docbox

# CommandBox
box install bx-docbox
```

Se manca, il verbo termina con un messaggio utile invece che con uno stack
trace.

## Avvio rapido

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites build
```

Senza alcuna configurazione, `docbox` documenta le cartelle sorgente
BoxLang convenzionali che il progetto possiede davvero - `models`,
`handlers`, `bifs`, `components`, `interceptors` - e scrive le pagine in
`docs/api/docbox/`.

## Configurazione

Tutto è opzionale; lo schema completo è in
[`docbox`](../configuration.md#docbox).

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "La mia API"
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
    		"projectTitle": "La mia API",
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
    projectTitle = "La mia API"
    excludes = "tests|build"
    pagePathPrefix = "api/docbox"
    tags = [ "api", "docbox" ]

    [docbox.mappings]
    models = "models"
    bifs = "bifs"
    ```

Ogni chiave ha un flag che la sovrascrive per una singola esecuzione:

```bash frame="terminal" title="Terminal"
bxSites docbox --mappings:models=models --projectTitle="La mia API" \
	--pagePathPrefix=api/classes --tags=api,classes --excludes=tests
```

`--jsonDir=<percorso>` conserva l'output JSON di DocBox invece di
scartarlo.

## Cosa viene generato

Tre tipi di pagina: un indice generale, un indice per package e una pagina
per classe.

```
docs/api/docbox/
├── index.md                    # tutti i package e le classi
├── models/
│   ├── index.md                # le classi di questo package
│   ├── UserService.md
│   └── security/
│       ├── index.md
│       └── Auth.md
```

La pagina di una classe riporta il suo docblock, i blocchi `property`
dichiarati e le funzioni raggruppate per accesso - prima il costruttore,
poi public, package e private - ognuna con firma completa, descrizione,
tabella dei parametri e testo di `@return`. I membri stanno nella stessa
barra di filtro Alpine.js usata dalle pagine Javadoc, così anche una classe
lunga resta leggibile a colpo d'occhio.

## Collegarle alla navigazione

Le pagine generate sono contenuto normale, quindi compaiono da sole nella
navigazione automatica per directory. Per collocarle deliberatamente,
indica l'indice nella tua [`nav`](../configuration.md#nav):

=== "YAML"
    ```yaml title="bxsites.yaml"
    nav:
      - title: Riferimento
        children:
          - api/docbox/index.md
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"nav": [
    		{ "title": "Riferimento", "children": [ "api/docbox/index.md" ] }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [[nav]]
    title = "Riferimento"
    children = [ "api/docbox/index.md" ]
    ```

## Cosa non fa di proposito

- **Nessun collegamento tra classi.** Un target di `extends`/`implements`,
  o un tipo citato in una firma, appare come codice inline anche se quella
  classe ha una pagina generata propria.
- **Nessun membro ereditato.** Solo ciò che la classe dichiara da sé, come
  nell'output JSON di DocBox.
- **Nessun cablaggio della navigazione.** Le pagine finiscono sotto
  `pagePathPrefix`; collocarle sta a te.

## Da dove arrivano i metadati

La strategia JSON di DocBox serializza nome, package, tipo, `extends` e
funzioni di una classe, ma non i suoi blocchi `property` dichiarati, le
interfacce implementate, le annotazioni di classe o il testo `@return` di
una funzione. Quei quattro contano in un riferimento BoxLang, e le
proprietà soprattutto, perché portano ogni campo `accessors` e ogni
`inject` di WireBox. Per questo bx-sites li rilegge dagli stessi metadati
di classe che ha usato DocBox e li unisce prima del rendering. Nulla viene
analizzato due volte.

## Da Gradle o Maven

Entrambi i plugin di build espongono anche questo generatore, per un
progetto JVM le cui sorgenti includano classi BoxLang/CFML - vedi
[Plugin Gradle](gradle-plugin.md#boxlang-doc-generation) e
[Plugin Maven](maven-plugin.md#boxlang-doc-generation).

Documentare un'applicazione ColdBox è un verbo a parte - vedi
[Applicazioni ColdBox](coldbox.md).
