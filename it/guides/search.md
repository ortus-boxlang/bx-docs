---
title: Ricerca
order: 2
icon: phosphor-duotone:magnifying-glass
tags: [guide, ricerca]
---

# Ricerca

BxSites distribuisce un provider di ricerca di default e può essere
puntato su altri tramite [`searchProvider`](../configuration.md#searchprovider)
di `bxsites.yaml` - `search: true`/`false` resta l'interruttore master
on/off indipendentemente da quale provider è attivo.

## Locale (il predefinito)

La ricerca di BxSites è completamente statica e lato client - lo stesso
approccio che usa [mkdocs](https://www.mkdocs.org/) di default: un
indice costruito una sola volta al momento del `build`, e
[lunr.js](https://lunrjs.com/) che effettua la ricerca vera e propria nel
browser del visitatore. Non è coinvolto alcun server, database o servizio
di ricerca esterno.

## Come funziona

1. Al momento del `build`, `SearchIndexer` percorre ogni pagina non
   nascosta e scrive `site/search-index.json`: una voce per pagina con il
   suo `title`, `url`, i `tags` del frontmatter, il testo di ogni
   intestazione nella pagina, e una copia in testo semplice troncata del
   suo corpo (con i tag HTML rimossi).
2. Il parziale `search.bxm` di ogni tema renderizza un box di ricerca;
   `layout.bxm` lo include (insieme agli script `lunr.js` + `search.js`
   condiviso) solo quando `search` di `bxsites.yaml` è `true` e
   `searchProvider.provider` è `"local"` (il predefinito - vedi
   [Altri provider](#other-search-providers) sotto per cosa cambia con
   uno diverso).
3. Nel browser, il widget condiviso `assets/search.js` recupera
   `search-index.json` una sola volta, costruisce a partire da esso un
   indice `lunr` (con `title` pesato di più, poi i `tags` del
   frontmatter, poi gli `headings`, poi il testo del corpo semplice), e
   ricerca di nuovo a ogni tasto premuto - nessuna richiesta di rete per
   ogni query.

## Scorciatoie da tastiera

- **`/`** porta il focus sul box di ricerca nella barra laterale da
  qualsiasi punto della pagina (a meno che tu non stia già digitando in
  un altro campo) - la stessa convenzione usata da
  [mkdocs-material](https://squidfunk.github.io/mkdocs-material/). Il box
  di ricerca mostra un piccolo suggerimento `Ctrl K`/`⌘K` (rilevato in
  base alla piattaforma) così la scorciatoia qui sotto è scopribile.
- **Cmd/Ctrl+K** apre invece un overlay separato in stile
  command-palette - una modale centrata su uno sfondo oscurato, costruita
  interamente in JS (nessuna modifica ai template del tema necessaria) e
  condivisa da ogni tema integrato. Le frecce Su/Giù spostano
  l'evidenziazione tra i risultati, **Invio** apre quello evidenziato, ed
  **Escape** (oppure un clic sullo sfondo) la chiude - la stessa
  convenzione "Quick Find"/⌘K che condividono Algolia DocSearch,
  Pagefind, VitePress e Docusaurus.
- **`Escape`** chiude anche il menu a discesa dei risultati proprio del
  box nella barra laterale e gli toglie il focus, indipendentemente
  dalla palette qui sopra.

La palette riutilizza esattamente lo stesso indice `lunr` già costruito
dal widget della barra laterale, invece di recuperare di nuovo
`search-index.json` - disponibile solo per `local` (il predefinito);
`algolia` ottiene il proprio Cmd+K gratuitamente da DocSearch stesso
(`keyboardShortcuts` predefinito a `true`), e `pagefind` ottiene Cmd+K
collegato da `layout.bxm` per portare il focus sul proprio `PagefindUI`,
dato che quella libreria non lo collega da sé - nessuno dei due apre la
palette propria di questo modulo.

## Disattivarla

=== "YAML"
    ```yaml title="bxsites.yaml"
    search: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "search": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    search = false
    ```

Salta del tutto la compilazione di `search-index.json`, e salta il box di
ricerca, lo script incluso `lunr.js`, e il widget condiviso `search.js` in
ogni pagina renderizzata - un progetto con la ricerca disattivata non
distribuisce assolutamente nulla legato alla ricerca. Questo è
l'interruttore master - si applica indipendentemente da quale
`searchProvider` sia configurato.

## Ricompilare solo l'indice

```bash frame="terminal" title="Terminal"
bxSites search-index
```

Utile se serve solo aggiornare `search-index.json` - `build` esegue già
questo passaggio come uno dei propri, quindi non serve eseguirlo
separatamente dopo un build normale. Funziona solo per i provider che
usano l'indice locale (`"local"`, e qualsiasi provider che bx-sites non
conosce altrimenti) - è un no-op (`skipped: true`) quando
`searchProvider.provider` è `"algolia"` o `"pagefind"`, dato che nessuno
dei due lo usa mai.

## Algolia

Imposta `searchProvider.provider` su `"algolia"` per sostituire il box di
ricerca con [Algolia DocSearch](https://docsearch.algolia.com/) - la
stessa ricerca ospitata dal crawler che supportano mkdocs-material,
VitePress, Starlight e Docusaurus:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: algolia
      algolia:
        appId: ABC123
        apiKey: a1b2c3d4e5f6...
        indexName: my-docs
        insights: false
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "algolia",
    		"algolia": {
    			"appId": "ABC123",
    			"apiKey": "a1b2c3d4e5f6...",
    			"indexName": "my-docs",
    			"insights": false
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "algolia"

    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    insights = false
    ```

`appId`, `apiKey` e `indexName` sono obbligatori - `apiKey` è la chiave
API pubblica **solo per la ricerca** che ti fornisce DocSearch (mai una
chiave da amministratore; viene distribuita direttamente in ogni pagina
renderizzata). `insights` (`false` di default) attiva l'analytics di
click/conversione proprio di DocSearch.

Con `algolia` attivo:

- Nessun `search-index.json` viene compilato, e il widget condiviso
  `lunr.js`/`search.js` non viene distribuito - Algolia serve i risultati
  dal proprio indice ospitato, popolato dal
  [crawler di DocSearch](https://docsearch.algolia.com/docs/what-is-docsearch/)
  o dal tuo stesso [Algolia Crawler](https://www.algolia.com/products/search-and-discovery/crawler/),
  non da qualcosa che BxSites scrive al momento del build. Devi comunque
  registrare il sito con DocSearch (o eseguire il tuo crawler)
  separatamente - BxSites collega solo il widget client.
- Ogni tema integrato renderizza invece un contenitore vuoto
  `#bxsites-search-algolia`, e `layout.bxm` carica `@docsearch/css`/
  `@docsearch/js` da jsDelivr e chiama `docsearch({...})` contro di esso -
  DocSearch renderizza il proprio pulsante di ricerca e la propria
  modale dentro quel contenitore.

## Pagefind

Imposta `searchProvider.provider` su `"pagefind"` per sostituire il box
di ricerca con [Pagefind](https://pagefind.app/) - un altro motore di
ricerca completamente statico/senza server, ma indicizzato a partire
dall'HTML *compilato* di `site/` invece che esplorato come Algolia:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: pagefind
      pagefind: { bin: pagefind, options: [] }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "pagefind",
    		"pagefind": { "bin": "pagefind", "options": [] }
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "pagefind"

    [searchProvider.pagefind]
    bin = "pagefind"
    options = []
    ```

Entrambe le chiavi `pagefind` sono opzionali - `bin` (predefinito
`"pagefind"`) è il nome/percorso dell'eseguibile, risolto rispetto a
`PATH` quando è un nome nudo; `options` è un array di flag CLI grezzi
extra passati direttamente (ad es.
`["--exclude-selectors", ".no-index"]`).

Con `pagefind` attivo:

- **La CLI `pagefind` deve essere già installata e su `PATH`** - BX
  Sites ci esegue uno shell out (non c'è alcun binding nativo BoxLang,
  lo stesso motivo per cui `lastUpdated`/`gh-deploy` eseguono uno shell
  out verso `git`), non la installa al posto tuo. Vedi la
  [documentazione di installazione di Pagefind](https://pagefind.app/docs/installation/).
  A differenza di `lastUpdated`, un binario mancante/fallito fa fallire
  `build` in modo rumoroso (`BxSites.PagefindFailed`) invece di
  degradare in silenzio - distribuire un sito il cui provider di ricerca
  configurato non funziona è peggio di un build fallito.
- Subito dopo che ogni albero di documenti (principale + versioni +
  locale) è stato scritto e `sitemap.xml`/`llms.txt` sono generati, BX
  Sites esegue `pagefind --site <siteDir> [...opzioni]` contro l'intero
  `site/` compilato - così un sito multi-versione/multi-locale ottiene
  tutto indicizzato in un solo passaggio, a differenza del
  `search-index.json` per-albero proprio di bx-sites. Pagefind scrive il
  proprio bundle direttamente in `site/pagefind/` - autoospitato,
  nessuna CDN coinvolta.
- Nessun `search-index.json` viene compilato, e il widget condiviso
  `lunr.js`/`search.js` non viene distribuito (come per `algolia`) - e
  `bxSites search-index` è un no-op per lo stesso motivo (vedi sopra).
- Ogni tema integrato renderizza un contenitore vuoto
  `#bxsites-search-pagefind`, e `layout.bxm` carica
  `site/pagefind/pagefind-ui.{css,js}` e chiama `new PagefindUI({...})`
  contro di esso - Pagefind renderizza il proprio box di ricerca inline e
  i propri risultati dentro quel contenitore.

## Altri provider di ricerca

`searchProvider.provider` non è limitato a `"local"`/`"algolia"`/
`"pagefind"` - qualsiasi altro valore viene accettato da `bxsites.yaml`
così com'è (la validazione della configurazione propria di BxSites
controlla solo i tre provider sopra). Non c'è alcun hook plugin per
questo caso - i temi integrati semplicemente non renderizzano nulla per
un nome di provider non riconosciuto, e collegare un quarto servizio di
ricerca (Meilisearch, Typesense, ecc.) è una
[sovrascrittura di tema](themes.md#overriding-a-theme) a livello di
progetto: copia un tema integrato nel `theme/` proprio del tuo progetto e
aggiungi il markup/gli script del tuo provider al suo `layout.bxm`/
`search.bxm`, leggendo `siteConfig.searchProvider` per decidere quando
renderizzarli - rami `searchProviderName eq "..."` per il punto di
montaggio in `search.bxm`, rami corrispondenti in `layout.bxm` per il suo
CSS/JS, e (se non è ospitato da un crawler come Algolia) qualsiasi
passaggio di indicizzazione richieda quel prodotto contro `site/` dopo
`build` - la stessa forma che già usano il `layout.bxm`/`BuildPipeline.bx`
propri di questo modulo per `algolia`/`pagefind`.
