---
title: Server MCP
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Attiva mcp per scrivere un site/mcp-index.json completo e non troncato a ogni build (oltre a site/mcp-manifest.json e a un mcp-nav.json per ogni albero), così bxSites Cloud può esporre il tuo sito pubblicato tramite un server MCP pubblico e di sola lettura per agenti IA - indipendente da search/searchProvider.
tags: [guide, mcp, ai]
---

# Server MCP

Attiva `mcp: true` in `bxsites.yaml` e ogni `build` scriverà tre file
accanto alle tue pagine renderizzate - una copia completa e leggibile da
macchine del contenuto, della nav e della struttura ad albero del tuo
sito, che [bxSites Cloud](https://bxsites.io/cloud) legge per esporre un
server [MCP](https://modelcontextprotocol.io/) pubblico e di sola lettura
per il tuo sito pubblicato, la stessa idea di
["MCP servers for published docs" di GitBook](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Una volta pubblicato, gli agenti e gli assistenti IA (Claude, ChatGPT e
qualsiasi altro client compatibile con MCP) possono cercare, sfogliare e
recuperare il contenuto del tuo sito direttamente, invece di fare
scraping dell'HTML renderizzato:

- `site/mcp-index.json` - una voce full-text per pagina, per ogni albero
- `site/mcp-nav.json` - la struttura di navigazione propria di
  quell'albero, per ogni albero
- `site/mcp-manifest.json` - un indice a livello di sito di ogni albero
  (sito principale, versioni, lingue) e di dove trovare i rispettivi
  file sopra elencati

BxSites stesso produce sempre e solo i file descritti in questa pagina -
servirli effettivamente come server MCP in rete è compito di bxSites
Cloud, non qualcosa che questo modulo fa da solo.

!!! note "Richiede un piano a pagamento di bxSites Cloud"
    Generare `mcp-index.json` di per sé è gratuito e funziona con il solo
    `bxSites build`, senza bisogno di un account Cloud. Esporlo davvero
    come server MCP attivo per un sito pubblicato è una funzionalità di
    bxSites Cloud disponibile solo nei suoi piani a pagamento - non è
    inclusa nel piano gratuito. Consulta
    [bxsites.io/cloud](https://bxsites.io/cloud) per i dettagli aggiornati
    sui piani.

## Attivarlo

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    mcp = true
    ```

`false` (predefinito) salta l'intero passaggio - non viene scritto
nessuno tra `mcp-index.json`, `mcp-nav.json` o `mcp-manifest.json`, e
`build` non ha alcun costo aggiuntivo oltre al controllo del flag.

## Indipendente da `search`

`mcp` non ha nulla a che fare con [`search`/`searchProvider`](search.md) -
i due sono interruttori indipendenti:

- `mcp-index.json` viene generato indipendentemente dal fatto che
  `search` sia `true` o `false`, e indipendentemente da
  `searchProvider.provider` (`"local"`, `"algolia"`, `"pagefind"` o uno
  personalizzato).
- Attivare `mcp` non cambia mai nulla della casella di ricerca del tuo
  sito rivolta ai visitatori, e disattivare `search` non disabilita mai
  `mcp-index.json`.

Per questo `mcp-index.json` non può semplicemente riutilizzare
`search-index.json`: quel file viene costruito solo per il provider di
ricerca `"local"` (un sito basato su Algolia o Pagefind non produce
alcun `search-index.json`, poiché ciascuno mantiene il proprio indice
altrove), e il suo campo `body` è deliberatamente troncato -
`search-index.json` viene inviato al browser di ogni visitatore per la
casella di ricerca in pagina, quindi mantenerlo piccolo è importante.
Nessuno di questi vincoli si applica a `mcp-index.json`: viene
recuperato lato server da bxSites Cloud, non inviato ai visitatori, e un
corpo troncato rischia di far dare a un agente IA una risposta
incompleta o errata.

## Il formato di `mcp-index.json`

Una voce per ogni pagina non nascosta (la stessa convenzione "le pagine
nascoste sono escluse" usata sia da `search-index.json` sia dalla
navigazione), che copre sia le normali pagine di documentazione sia i
post del [blog](blog.md):

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ...",
    "type": "page",
    "categories": [],
    "updatedAt": "2026-08-18T10:15:00.000Z"
  },
  {
    "title": "Announcing bxSites 2.0",
    "url": "blog/announcing-bxsites-2/index.html",
    "tags": ["release"],
    "headings": ["Announcing bxSites 2.0"],
    "body": "Announcing bxSites 2.0 Today we're shipping...",
    "type": "post",
    "categories": ["Releases"],
    "updatedAt": "2026-08-15"
  }
]
```

| Campo | Descrizione |
|---|---|
| `title` | Il titolo della pagina (frontmatter `title`, come in `search-index.json`) |
| `url` | Il percorso URL della pagina, relativo alla radice del sito |
| `tags` | L'array `tags` del frontmatter della pagina |
| `headings` | Il testo semplice di ogni intestazione `h1`-`h6` della pagina, nell'ordine del documento |
| `body` | Il contenuto testuale **completo** della pagina, senza tag HTML - mai troncato |
| `type` | `"page"` per una normale pagina di documentazione, `"post"` per un post del [blog](blog.md) |
| `categories` | L'array `categories` del frontmatter proprio di un post; sempre `[]` per una pagina di documentazione (le pagine di documentazione non hanno categorie) |
| `updatedAt` | Il `date` del frontmatter proprio di un post; il `date` del frontmatter proprio di una pagina di documentazione quando lo imposta, altrimenti l'orario dell'ultima modifica del suo file sorgente come istante ISO-8601. `""` quando nessuno dei due è disponibile. |

A parte `type`/`categories`/`updatedAt`, questa è la stessa forma di
voce usata da `search-index.json`, con una differenza: lì `body` viene
tagliato a 400 caratteri con puntini di sospensione finali; qui è la
pagina intera.

## Il formato di `mcp-manifest.json`

Scritto una sola volta per build del sito (non una volta per albero),
`site/mcp-manifest.json` elenca ogni albero che ha ottenuto il proprio
`mcp-index.json`/`mcp-nav.json`, così il server MCP di bxSites Cloud può
offrire strumenti consapevoli di versione e lingua senza dover indovinare
le convenzioni di directory proprie di bx-sites:

```json title="site/mcp-manifest.json"
[
  { "path": "", "label": "1.0.x", "version": "1.0.x", "locale": "en", "default": true },
  { "path": "next", "label": "Next", "version": null, "locale": "en", "default": false },
  { "path": "versions/0.9", "label": "0.9", "version": "0.9", "locale": "en", "default": false },
  { "path": "es", "label": "Español", "version": "1.0.x", "locale": "es", "default": false }
]
```

| Campo | Descrizione |
|---|---|
| `path` | Il percorso proprio di questo albero, relativo alla radice - `""` per l'albero principale alla radice del sito, `"next"`/`"versions/<name>"`/`"<localeCode>"`/`"versions/<name>/<localeCode>"` altrimenti. Uniscilo a `/mcp-index.json` (o `/mcp-nav.json`) per ottenere il file proprio di quell'albero, ad es. `"versions/0.9/mcp-index.json"` - e semplicemente `"mcp-index.json"` per quello della radice (`path` lì è `""`). |
| `label` | Un'etichetta leggibile per questo albero - l'etichetta propria del selettore di versione (il nome di `versions.default`, il nome proprio di una versione semplice, oppure `"Latest"`/`"Next"`), combinata con l'etichetta propria della lingua per un sotto-albero di lingua |
| `version` | Il nome di `docs/versions/<name>/` che questo albero renderizza, oppure `null` quando non è una versione con nome (l'albero principale non versionato, oppure `/next/`) |
| `locale` | Il codice di lingua proprio di questo albero - `i18n.defaultLocale.code` per un albero senza suffisso di lingua, altrimenti il codice proprio di quella lingua |
| `default` | `true` per l'unico albero che viene renderizzato alla radice del sito - quello su cui atterra un visitatore/agente IA senza specificare versione o lingua |

## Il formato di `mcp-nav.json`

Scritto insieme al `mcp-index.json` proprio di ogni albero (radice del
sito, `/next/`, ogni albero `/versions/<name>/`, ogni sotto-albero di
lingua), `mcp-nav.json` è la nav propria di quell'albero - la stessa
identica struttura annidata `{ title, url, order, icon, children }` da
cui il tema stesso renderizza la sidebar - così il server MCP di
bxSites Cloud può offrire uno strumento `get_nav`/indice dei contenuti
senza inventare una seconda forma di nav propria:

```json title="site/mcp-nav.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "order": 1,
    "icon": "phosphor-duotone:rocket-launch",
    "children": []
  },
  {
    "title": "Guides",
    "url": "",
    "order": 2,
    "icon": "",
    "children": [
      { "title": "Search", "url": "guides/search/index.html", "order": 1, "icon": "", "children": [] }
    ]
  }
]
```

Un nodo gruppo-cartella senza un proprio `index.md` (come `"Guides"`
sopra) ha `url` vuoto - non è una pagina, è solo un'intestazione per i
suoi `children`.

## Siti con più versioni e lingue

Proprio come `search-index.json`, `mcp-index.json`/`mcp-nav.json`
vengono scritti una volta per ogni albero renderizzato - il sito
principale, `/next/` (quando `versions.default` è impostato), ogni
albero `/versions/<name>/` e ogni sotto-albero di lingua - così che ogni
albero pubblicato ottenga la propria coppia di file che copre solo le
pagine e la nav di quell'albero. `mcp-manifest.json`, invece, viene
scritto una sola volta per build, solo alla radice del sito, ed elenca
ognuno di questi alberi.
