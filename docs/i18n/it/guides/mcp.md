---
title: Server MCP
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Attiva mcp per scrivere un site/mcp-index.json completo e non troncato a ogni build, così bxSites Cloud può esporre il tuo sito pubblicato tramite un server MCP pubblico e di sola lettura per agenti IA - indipendente da search/searchProvider.
tags: [guide, mcp, ai]
---

# Server MCP

Attiva `mcp: true` in `bxsites.yaml` e ogni `build` scriverà
`site/mcp-index.json` accanto alle tue pagine renderizzate - una copia
completa e leggibile da macchine del contenuto del tuo sito, che
[bxSites Cloud](https://bxsites.io/cloud) legge per esporre un server
[MCP](https://modelcontextprotocol.io/) pubblico e di sola lettura per
il tuo sito pubblicato, la stessa idea di
["MCP servers for published docs" di GitBook](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Una volta pubblicato, gli agenti e gli assistenti IA (Claude, ChatGPT e
qualsiasi altro client compatibile con MCP) possono cercare e recuperare
il contenuto del tuo sito direttamente, invece di fare scraping
dell'HTML renderizzato.

BxSites stesso produce solo il file `mcp-index.json` descritto in questa
pagina - servirlo effettivamente come server MCP in rete è compito di
bxSites Cloud, non qualcosa che questo modulo fa da solo.

## Attivarlo

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

`false` (predefinito) salta l'intero passaggio - non viene scritto
alcun `mcp-index.json`, e `build` non ha alcun costo aggiuntivo oltre
al controllo del flag.

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
navigazione):

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ..."
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

L'unica differenza rispetto alla forma delle voci di
`search-index.json` è `body`: lì viene tagliato a 400 caratteri con
puntini di sospensione finali; qui è la pagina intera.

## Siti con più versioni e lingue

Proprio come `search-index.json`, `mcp-index.json` viene scritto una
volta per ogni albero renderizzato - il sito principale, `/next/`
(quando `versions.default` è impostato), ogni albero
`/versions/<name>/` e ogni sotto-albero di lingua - così che ogni
albero pubblicato ottenga il proprio `mcp-index.json` che copre solo le
pagine di quell'albero.
