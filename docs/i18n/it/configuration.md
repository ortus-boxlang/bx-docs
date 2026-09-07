---
title: Configurazione
order: 4
icon: phosphor-duotone:gear-six
summary: Ogni chiave della configurazione del sito, il suo valore predefinito e cosa fa.
tags: [riferimento, configurazione]
---

# Configurazione

Ogni progetto ha un'unica configurazione del sito alla radice -
`bxsites.yaml` (o `.yml`), il formato predefinito e preferito, oppure
`bxsites.json` per un progetto che desidera restare su di esso. Entrambi
sono pienamente supportati e producono esattamente lo stesso risultato;
`bxSites new` genera lo scheletro di `bxsites.yaml` a meno che non venga
passato `--format=json` (vedi
[Per iniziare](getting-started.md#formato-del-file-di-configurazione)). Se
un progetto ne ha in qualche modo più di uno, vince `bxsites.yaml`, poi
`bxsites.yml`, poi `bxsites.json`.

=== "YAML"
    ```yaml
    name: "My Docs"
    description: ""
    baseURL: "/"
    theme:
      name: bootstrap
      options: {}
      logo: ""
      favicon: ""
    search: true
    mcp: false
    searchProvider:
      provider: local
      algolia: { appId: "", apiKey: "", indexName: "", insights: false }
    nav: []
    markdown:
      enableAdmonition: true
    repo:
      url: ""
      editUri: ""
    social: []
    footer: false
    lastUpdated: false
    mermaid: false
    math: false
    analytics:
      provider: ""
      id: ""
    ogImage: ""
    generateOgImages: false
    extraCss: []
    extraJs: []
    plugins: []
    i18n:
      defaultLocale: { code: en, label: English }
      locales: []
    variables: {}
    ```

=== "JSON"
    ```json
    {
    	"name": "My Docs",
    	"description": "",
    	"baseURL": "/",
    	"theme": {
    		"name": "bootstrap",
    		"options": {},
    		"logo": "",
    		"favicon": ""
    	},
    	"search": true,
    	"mcp": false,
    	"searchProvider": {
    		"provider": "local",
    		"algolia": { "appId": "", "apiKey": "", "indexName": "", "insights": false }
    	},
    	"nav": [],
    	"markdown": { "enableAdmonition": true },
    	"repo": {
    		"url": "",
    		"editUri": ""
    	},
    	"social": [],
    	"footer": false,
    	"lastUpdated": false,
    	"mermaid": false,
    	"math": false,
    	"analytics": {
    		"provider": "",
    		"id": ""
    	},
    	"ogImage": "",
    	"generateOgImages": false,
    	"extraCss": [],
    	"extraJs": [],
    	"plugins": [],
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": []
    	},
    	"variables": {}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Main Components"
    children = [
      { title = "Quick Start", path = "guides/setup.md" },
      { path = "guides/deployment.md" }
    ]
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    name = "My Docs"
    description = ""
    baseURL = "/"
    search = true
    mcp = false
    nav = []
    social = []
    footer = false
    lastUpdated = false
    extraCss = []
    extraJs = []
    plugins = []
    variables = {}

    [theme]
    name = "bootstrap"
    options = {}
    logo = ""
    favicon = ""

    [markdown]
    enableAdmonition = true

    [repo]
    url = ""
    editUri = ""
    ```

Solo `name` è obbligatorio - tutto il resto ricade sui valori predefiniti
mostrati sopra. Un oggetto `theme` parziale viene unito con quello di
default per un livello, quindi `{"theme":{"name":"material"}}` da solo
mantiene comunque le `options` predefinite (vuote). Ogni chiave ha lo
stesso nome e la stessa forma in entrambi i formati - cambia scheda in
qualsiasi esempio qui sotto per vederlo nell'altra forma.

## `name`

Il nome del sito, mostrato nel marchio dell'header/brand e nei titoli
delle pagine. Obbligatorio.

## `description`

Una descrizione opzionale del sito, usata come `<meta name="description">`
e `og:description` di riserva per qualsiasi pagina che non imposti una
propria `description` nel frontmatter (vedi [Per iniziare](getting-started.md#add-pages)).

## `baseURL`

Controlla come viene prefissato ogni link interno, percorso di asset e voce
di nav, e funge anche da URL canonico del sito per `sitemap.xml`,
`robots.txt`, `llms.txt` e per il tag `<link rel="canonical">` di ogni
pagina.

- Lasciato vuoto o `"/"` (il valore predefinito) - i link restano relativi
  alla radice (`/page/`), e non viene generato né `sitemap.xml`, né una
  riga `Sitemap:` in `robots.txt`, né un `llms.txt` con URL assoluti, né un
  tag `<link rel="canonical">` (non c'è un dominio canonico da cui
  costruirli).
- Un percorso semplice, ad es. `"my-docs"` o `"/my-docs/"` - si assume che
  il sito venga servito da quel sotto-percorso, e ogni link interno, voce
  di nav e asset viene prefissato con esso (`/my-docs/page/`). Ancora
  nessun `sitemap.xml`/tag canonici, dato che manca comunque un dominio
  assoluto.
- Un URL completo, ad es. `"https://docs.example.com/"` - la parte del
  percorso (qui `/`) viene usata allo stesso modo di un percorso semplice,
  **e** `sitemap.xml` viene scritto al momento del `build` con l'URL
  assoluto di ogni pagina non nascosta sotto quel dominio, `robots.txt`
  guadagna una riga `Sitemap:` che vi punta, e ogni pagina ottiene il
  proprio `<link rel="canonical">` corretto (la pagina di un albero di
  versione/locale punta comunque all'URL *di quello stesso albero*, non a
  quello del sito principale).

`llms.txt` (vedi [sotto](#llmstxt)) viene sempre scritto; preferisce
semplicemente un URL assoluto quando `baseURL` ne fornisce uno.

## `robots.txt`

Ogni build scrive un `robots.txt` alla radice del sito - nessuna chiave di
configurazione necessaria, a meno che tu non voglia cambiarne il
comportamento predefinito, permissivo:

=== "YAML"
    ```yaml title="bxsites.yaml"
    robots: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "robots": false }
    ```

- `true` (il valore predefinito) - `Allow: /` per ogni crawler, più una
  riga `Sitemap:` che punta a `sitemap.xml` quando `baseURL` è un URL
  completo (vedi sopra).
- `false` - `Disallow: /` per ogni crawler al suo posto, e nessuna riga
  `Sitemap:` - la comune esigenza di "non indicizzare affatto questo
  deploy di staging/interno". Questa è solo un'esclusione per i
  *crawler*, non un controllo degli accessi - il sito resta comunque
  pienamente raggiungibile da chiunque ne abbia l'URL; vedi
  [Distribuzione](guides/deployment.md#limitare-chi-può-raggiungere-il-tuo-sito)
  se hai davvero bisogno di limitare chi può raggiungerlo.

Ti serve più del semplice interruttore on/off - percorsi specifici da
escludere, più righe `Sitemap:`, un `Crawl-delay`, regole per singolo
user-agent? Metti il tuo `robots.txt` proprio accanto a `index.md`
(`docs/robots.txt`, o `src/robots.txt` per un progetto basato su `src/` -
vedi [`docs/` o `src/`](getting-started.md#aggiungere-pagine)) e viene copiato
byte per byte al posto di quello generato, a ogni build - la chiave
`robots` sopra viene del tutto ignorata una volta che questo file esiste.

## `llms.txt`

Ogni build scrive un `llms.txt` alla radice del sito - un indice Markdown
semplice di ogni pagina non nascosta, seguendo la convenzione emergente di
[llms.txt](https://llmstxt.org) per aiutare gli strumenti basati su LLM a
orientarsi in un sito senza doverne effettuare la scansione dell'HTML
renderizzato. Non c'è una chiave di configurazione per questo; viene
generato automaticamente, usando un URL assoluto per link quando `baseURL`
è un URL completo, oppure uno relativo a `basePath` in caso contrario.

## `sitemap.xml`

Scritto alla radice del sito, ma solo quando `baseURL` è un URL completo
(vedi sopra) - una sitemap ha bisogno di un dominio assoluto per avere
senso. Elenca ogni pagina non nascosta secondo il protocollo
[sitemaps.org](https://www.sitemaps.org/).

## `theme`

- `theme.name` - uno dei temi integrati (`bootstrap`, `material`,
  `tailwind`), oppure il nome di un tema personalizzato che fornisci tu
  tramite una cartella `theme/` alla radice del progetto (vedi
  [Temi](guides/themes.md))
- `theme.logo` - percorso/URL a un'immagine mostrata accanto al nome del
  sito nel marchio dell'header (al posto del glifo predefinito "⚡") - un
  percorso relativo (ad es. `"assets/logo.svg"`, risolto rispetto a
  `docs/assets/`) viene prefissato con `baseURL` come qualsiasi altro
  asset interno; un URL assoluto viene usato così com'è. Lasciato vuoto
  (il valore predefinito), l'header mostra "⚡ &lt;nome del sito&gt;".
- `theme.favicon` - percorso/URL a una favicon, risolto allo stesso modo di
  `theme.logo`. Lasciato vuoto (il valore predefinito), non viene reso
  alcun `<link rel="icon">` (ricadendo sul comportamento predefinito del
  browser).
- `theme.options` - opzioni specifiche del tema, lette da ogni tema
  integrato:
  - `theme.options.colorMode` - `"auto"` (il valore predefinito),
    `"light"` o `"dark"`. Controlla quale modalità vede un visitatore alla
    prima visita, prima che ne scelga una propria tramite l'interruttore
    scuro/chiaro nell'header - `"auto"` segue la preferenza del suo
    sistema operativo, `"light"`/`"dark"` fissa un default fisso. Una
    volta che un visitatore attiva l'interruttore, la sua scelta
    (memorizzata in `localStorage`) prevale sempre nelle visite
    successive, indipendentemente da questa impostazione.

    === "YAML"
        ```yaml
        theme: { options: { colorMode: dark } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "colorMode": "dark" } } }
        ```
  - `theme.options.navCollapsible` - `false` (il valore predefinito)
    mostra ogni intestazione di sezione della nav sempre espansa, come
    oggi. `true` mostra ogni sezione della nav (una cartella senza
    `index.md`) come un elemento nativo `<details>`/`<summary>` che il
    visitatore può comprimere - nessun framework JS coinvolto.
  - `theme.options.navExpandAll` - rilevante solo quando `navCollapsible`
    è `true`. `true` (il valore predefinito) avvia ogni sezione aperta;
    `false` avvia ogni sezione compressa.

    === "YAML"
        ```yaml
        theme: { options: { navCollapsible: true, navExpandAll: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "navCollapsible": true, "navExpandAll": false } } }
        ```

## `search`

`true` (il valore predefinito) compila un indice di ricerca statico e
attiva il box di ricerca; `false` salta entrambi del tutto - nessun
`search-index.json`, nessuna UI di ricerca, nessun JS extra distribuito.
Vedi [Ricerca](guides/search.md).

## `searchProvider`

Quale UI di ricerca `search: true` attiva:

- `provider` - `"local"` (il valore predefinito) è la ricerca
  statica/lato client propria di bx-sites (`search-index.json` + lunr.js,
  vedi [Ricerca](guides/search.md#local-the-default)). `"algolia"` attiva
  invece [Algolia DocSearch](guides/search.md#algolia), e `"pagefind"`
  attiva [Pagefind](guides/search.md#pagefind). Qualsiasi altro valore è un
  provider personalizzato del progetto, collegato tramite una
  sovrascrittura `theme/` - vedi
  [Ricerca](guides/search.md#other-search-providers).
- `algolia` - obbligatorio quando `provider` è `"algolia"`: `appId`,
  `apiKey` (la chiave API pubblica *solo per la ricerca*, non una chiave
  da amministratore) e `indexName`, esattamente come li richiede il
  client DocSearch di Algolia stesso. `insights` (`false` di default)
  attiva l'analytics di click/conversione proprio di DocSearch.

  === "YAML"
      ```yaml title="bxsites.yaml" linenums="1"
      search: true
      searchProvider:
        provider: algolia
        algolia:
          appId: ABC123
          apiKey: a1b2c3d4e5f6...
          indexName: my-docs
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
      			"indexName": "my-docs"
      		}
      	}
      }
      ```

- `pagefind` - entrambe le chiavi opzionali quando `provider` è
  `"pagefind"`: `bin` (predefinito `"pagefind"`) è il nome/percorso
  dell'eseguibile CLI, risolto rispetto a `PATH` quando è un nome nudo;
  `options` è un array di flag CLI grezzi extra passati direttamente. La
  CLI `pagefind` stessa deve essere già installata e su `PATH` - BxSites
  ci esegue uno shell out (come `git` per `lastUpdated`/`gh-deploy`), non
  la installa al posto tuo.

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

## `mcp`

`false` (predefinito) lo salta del tutto. `true` scrive
`site/mcp-index.json` a ogni `build` - un indice dei contenuti completo e
non troncato per il server MCP pubblico e di sola lettura di
[bxSites Cloud](https://bxsites.io/cloud) per il tuo sito pubblicato.
Del tutto indipendente da `search`/`searchProvider` sopra - viene
prodotto indipendentemente dal provider di ricerca configurato (o
dall'assenza di uno). Generare il file in sé è gratuito; bxSites Cloud lo
espone realmente come server MCP attivo solo nei piani a pagamento, non
nel piano gratuito. Vedi [Server MCP](guides/mcp.md).

## `nav`

Per impostazione predefinita, la nav viene dedotta dalla struttura stessa
di cartelle/file di `docs/` (con il frontmatter `order`/`hidden`) - va
bene per siti piccoli, ma un sito grande può superarla: una nav esplicita
ti permette di dare titolo, raggruppare e ordinare le pagine come vuoi,
indipendentemente da dove si trovino effettivamente i loro file.

Un array vuoto (il valore predefinito) significa "deduci dalla struttura
delle cartelle". Un array non vuoto sostituisce del tutto quella
deduzione - l'ordine dell'array diventa l'ordine della nav, e una pagina
non referenziata da nessuna parte al suo interno viene comunque compilata,
solo non collegata dalla nav (come `hidden: true`). Ogni voce è una delle
seguenti:

- una semplice stringa di percorso relativa a docs/, ad es.
  `"guides/setup.md"` - il titolo proviene dal frontmatter/nome del file
  di quella pagina, lo stesso che darebbe la deduzione dalla struttura
  delle cartelle
- un oggetto `{ "title", "path", "icon", "children" }` - `path`, `icon` e
  `children` sono tutti opzionali; una voce con solo `title` e senza
  `path` è un'intestazione di gruppo senza link (come una cartella senza
  `index.md` oggi), e un `title`/`icon` esplicito sovrascrive sempre il
  titolo/icona propri della pagina collegata nella nav (l'`<h1>`/`<title>`
  reale della pagina resta intatto - cambia solo l'etichetta/icona nella
  nav) - vedi [Icone](guides/icons.md) per cosa può essere un
  valore `icon`

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - index.md
      - title: Guides
        children:
          - title: Quick Start
            path: guides/setup.md
          - guides/deployment.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		"index.md",
    		{
    			"title": "Guides",
    			"children": [
    				{ "title": "Quick Start", "path": "guides/setup.md" },
    				"guides/deployment.md"
    			]
    		}
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"
    ```

Per una nav abbastanza grande da appesantire `bxsites.yaml`, spostala nel
proprio file `docs/nav.json` - stessa forma di array, solo come contenuto
principale dell'intero file:

```json
[
	"index.md",
	{ "title": "Guides", "children": [ "guides/setup.md" ] }
]
```

Il `nav` proprio di `bxsites.yaml`, quando non vuoto, prevale sempre su
`docs/nav.json`. Solo l'albero principale rispetta l'uno o l'altro - un
albero `docs/versions/<name>/` deduce sempre la propria nav dalla propria
struttura di cartelle, anche quando l'albero principale ne ha una
esplicita.

## `redirects`

`[]` (il valore predefinito) - redirect `from`/`to` per URL vecchi validi
per tutto il sito, applicati solo all'albero principale:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [markdown]
    enableFootnotes = true
    enableDefinitionLists = true
    anchorLinks = false
    enableYouTubeTransformer = true
    ```

- `redirects[].from` - il vecchio segmento URL (senza slash
  iniziale/finale, senza estensione) su cui viene scritto uno stub di
  redirect statico
- `redirects[].to` - un percorso relativo alla radice (risolto rispetto a
  `baseURL`) oppure un URL `https://` completo

Il proprio `redirect_from` nel frontmatter di una pagina è l'alternativa
per pagina/per albero (funziona anche dentro un albero di
versione/locale) - consulta [Redirect](guides/redirects.md) per il
quadro completo, incluso come `page:rename` lo aggiunge automaticamente.

## `markdown`

Inoltrato così com'è alle opzioni del modulo
[bx-markdown](https://github.com/ortus-boxlang/bx-markdown) prima della
compilazione di ogni pagina. BxSites non ridefinisce né valida queste
chiavi; qualunque cosa metti qui è il set di opzioni proprio di
bx-markdown, passato direttamente - quindi questo elenco può divergere da
quello di bx-markdown man mano che si evolve. Tabelle, `~~barrato~~`,
checkbox `- [ ]` per le liste di attività e il sommario in pagina sono
sempre attivi, senza alcun interruttore. L'unica eccezione è
`enableAdmonition` - bx-markdown stesso lo imposta a `false` di default,
ma BxSites lo imposta a `true` di default (vedi la
[guida alle Estensioni Markdown](guides/markdown.md)).

| Chiave | Predefinito | Effetto |
|---|---|---|
| `enableAdmonition` | `true` *(predefinito di BxSites; il predefinito di bx-markdown è `false`)* | Blocchi di richiamo `!!!`/`???`/`???+` - vedi la [guida alle Estensioni Markdown](guides/markdown.md#admonitions) |
| `enableFootnotes` | `false` | Riferimenti a note a piè di pagina `[^label]` - vedi la [guida alle Estensioni Markdown](guides/markdown.md#footnotes) |
| `enableDefinitionLists` | `false` | Liste `Term\n:   Definition` - vedi la [guida alle Estensioni Markdown](guides/markdown.md#definition-lists) |
| `autoLinkUrls` | `true` | Collega automaticamente URL e indirizzi email nudi |
| `anchorLinks` | `true` | Aggiunge un link di ancoraggio cliccabile a ogni intestazione |
| `anchorSetId` | `true` | Imposta un attributo `id` su ogni intestazione |
| `achorSetName` *(sic)* | `true` | Imposta un attributo `name` su ogni intestazione |
| `anchorWrapText` | `false` | Avvolge l'intero testo dell'intestazione nel link di ancoraggio, invece di un semplice marcatore |
| `anchorClass` | `"anchor"` | Classe CSS sull'`<a>` di ancoraggio |
| `anchorPrefix` / `anchorSuffix` | `""` | HTML grezzo iniettato immediatamente prima/dopo il testo dell'intestazione |
| `enableYouTubeTransformer` | `false` | Incorpora automaticamente i link YouTube nudi come player |
| `codeStyleHTMLOpen` / `codeStyleHTMLClose` | `"<code>"` / `"</code>"` | HTML di contorno intorno agli span di codice inline |
| `fencedCodeLanguageClassPrefix` | `"language-"` | Prefisso di classe da cui dipendono l'evidenziatore di sintassi lato client di bx-sites (e Mermaid, vedi sotto), ad es. ` ```js ` -> `class="language-js"` |
| `tableOptions.columnSpans` | `true` | Rispetta le celle di tabella unite in stile `colspan` |
| `tableOptions.appendMissingColumns` | `true` | Completa una riga più corta fino al numero di colonne dell'intestazione |
| `tableOptions.discardExtraColumns` | `true` | Elimina le celle in eccesso in una riga troppo lunga |
| `tableOptions.className` | `"table"` | Classe CSS su ogni `<table>` renderizzata |
| `tableOptions.headerSeparationColumnMatch` | `true` | Richiede che la riga separatore `---` corrisponda al numero di colonne dell'intestazione |

Ogni tabella renderizzata riceve anche automaticamente un wrapper per lo scroll responsive e un'intestazione fissa, senza una propria chiave di configurazione - vedi [Tabelle](guides/tables.md#scorrimento-responsive-e-intestazione-fissa).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    markdown:
      enableFootnotes: true
      enableDefinitionLists: true
      anchorLinks: false
      enableYouTubeTransformer: true
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"markdown": {
    		"enableFootnotes": true,
    		"enableDefinitionLists": true,
    		"anchorLinks": false,
    		"enableYouTubeTransformer": true
    	}
    }
    ```

## `repo`

Aggiunge un link con icona del repository nell'header (in tutti e tre i
temi integrati) e, quando entrambe le chiavi sono impostate, un link
"Modifica questa pagina" su ogni pagina.

- `repo.url` - l'URL del tuo repository, ad es.
  `"https://github.com/acme/docs"`. Da solo, mostra il link con icona
  nell'header; lascialo vuoto per ometterlo del tutto.
- `repo.editUri` - il segmento di percorso tra l'URL del repository e il
  percorso sorgente proprio di una pagina, ad es. `"edit/main/docs/"` (la
  convenzione URL di "edit" propria di GitHub). Combinato con `repo.url` e
  il percorso sorgente di una pagina relativo a `docs/` per costruirne il
  link di modifica - ad es. con l'esempio sopra, `docs/guides/setup.md`
  ottiene `https://github.com/acme/docs/edit/main/docs/guides/setup.md`.
  Richiede anche `repo.url`; lascialo vuoto per omettere i link di
  modifica pur mostrando l'icona nell'header.

=== "YAML"
    ```yaml title="bxsites.yaml"
    repo: { url: "https://github.com/acme/docs", editUri: "edit/main/docs/" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "repo": { "url": "https://github.com/acme/docs", "editUri": "edit/main/docs/" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [repo]
    url = "https://github.com/acme/docs"
    editUri = "edit/main/docs/"
    ```

## `social`

Un array di link social/esterni renderizzati nel footer (vedi
[`footer`](#footer) - non ha effetto a meno che non sia anch'esso
attivato). Ogni voce richiede un `url`; `icon` seleziona da un piccolo set
di icone integrato (`github`, `twitter`/`x`, `youtube`, `linkedin`,
`facebook`, `bluesky`, `threads`, `slack`, `patreon`, `rss`, `email`, con
un glifo di link generico come ripiego per qualsiasi altra cosa), e
`label` imposta il nome accessibile/tooltip del link (predefinito su
`icon`, poi `"Link"`).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    social:
      - { url: "https://twitter.com/acme", icon: twitter, label: Twitter }
      - { url: "https://acme.com/rss.xml", icon: rss, label: RSS }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"social": [
    		{ "url": "https://twitter.com/acme", "icon": "twitter", "label": "Twitter" },
    		{ "url": "https://acme.com/rss.xml", "icon": "rss", "label": "RSS" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[social]]
    url = "https://twitter.com/acme"
    icon = "twitter"
    label = "Twitter"

    [[social]]
    url = "https://acme.com/rss.xml"
    icon = "rss"
    label = "RSS"
    ```

## `footer`

`false` (il valore predefinito) - nessun footer. `true` ne aggiunge uno a
ogni pagina: una riga di copyright (`© <anno> <nome del sito>`), i link
`social` (se presenti), e un credito "Built with BxSites".

=== "YAML"
    ```yaml title="bxsites.yaml"
    footer: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "footer": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    footer = true
    ```

## `lastUpdated`

`false` (il valore predefinito) - nessuna data di ultimo aggiornamento.
`true` aggiunge una riga "Last updated" accanto al link di modifica (o da
sola, se `repo.editUri` non è impostato), ricavata dal `git log` del file
Markdown proprio di ogni pagina al momento del `build`. Omessa
silenziosamente per una pagina di cui git non ha storia - un `git init`
appena fatto senza ancora commit, un build eseguito da uno zip scaricato
senza alcun `.git`, oppure git non installato sulla macchina di build -
piuttosto che interrompere il build.

=== "YAML"
    ```yaml title="bxsites.yaml"
    lastUpdated: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "lastUpdated": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    lastUpdated = true
    ```

## `analytics`

Attiva l'analisi delle visualizzazioni di pagina. Attualmente supporta
solo Google Analytics (`gtag.js`):

- `analytics.provider` - `"google"` per attivarlo; lasciato vuoto (il
  valore predefinito), non viene distribuito alcuno script di analytics.
- `analytics.id` - l'ID di misurazione di Google Analytics (ad es.
  `"G-ABC123"`). Obbligatorio quando `provider` è `"google"`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    analytics: { provider: google, id: "G-ABC123" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "analytics": { "provider": "google", "id": "G-ABC123" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [analytics]
    provider = "google"
    id = "G-ABC123"
    ```

## `ogImage`

Percorso/URL a un'immagine social-card predefinita, renderizzata come
`og:image` (abbinata a un `twitter:card` di tipo
`summary_large_image`) su ogni pagina che non la sovrascrive - risolta
allo stesso modo di `theme.logo` (i percorsi relativi vengono prefissati
con `baseURL`, gli URL assoluti vengono usati così come sono). Lasciato
vuoto (il valore predefinito) e con `generateOgImages` disattivato, non
viene renderizzato alcun tag `og:image`/`twitter:card`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    ogImage: assets/social-card.png
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "ogImage": "assets/social-card.png" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    ogImage = "assets/social-card.png"
    ```

Un `ogImage` proprio del frontmatter di una pagina (vedi
[Per iniziare](getting-started.md#add-pages)) prevale sempre su questo
valore predefinito a livello di sito per quella singola pagina.

### `generateOgImages`

`false` (il valore predefinito) - nessuna card per singola pagina. `true`
renderizza una vera immagine PNG 1200x630 come social card per ogni
pagina che non ha già un proprio `ogImage` nel frontmatter - il titolo
della pagina sul gradiente del brand, scritto in
`site/assets/og/<page>.png` - invece di condividere tutte le pagine
un'unica immagine generica a livello di sito. Solo `java.awt`/
`javax.imageio` dietro le quinte (parte di qualsiasi JVM su cui gira
BoxLang), quindi non serve alcun browser headless, servizio esterno, o
accesso alla rete al momento del build.

=== "YAML"
    ```yaml title="bxsites.yaml"
    generateOgImages: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "generateOgImages": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    generateOgImages = true
    ```

## `extraCss` / `extraJs`

Array di URL di fogli di stile/script extra da includere in ogni pagina,
aggiunti dopo gli asset propri del tema - ogni voce viene risolta allo
stesso modo di `theme.logo` (un percorso relativo viene prefissato con
`baseURL`; un URL assoluto viene usato così com'è). Le voci di `extraJs`
vengono caricate con `defer`.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/custom.css ]
    extraJs: [ assets/custom.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/custom.css"],
    	"extraJs": ["assets/custom.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/custom.css" ]
    extraJs = [ "assets/custom.js" ]
    ```

Quando `assets.bundle` è attivo (il valore predefinito), un elenco locale
di `extraCss`/`extraJs` come quello sopra viene raggruppato in un unico
file con impronta digitale ciascuno, invece di un tag `<link>`/`<script>`
per ogni voce - vedi [`assets`](#assets) sotto.

## `assets`

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      fingerprint: true
      bundle: true
      images:
        enabled: true
        widths: [ 400, 800, 1200, 1600 ]
        formats: [ original, webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": {
    			"enabled": true,
    			"widths": [400, 800, 1200, 1600],
    			"formats": ["original", "webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [assets]
    fingerprint = true
    bundle = true

    [assets.images]
    enabled = true
    widths = [ 400, 800, 1200, 1600 ]
    formats = [ "original", "webp" ]
    ```

La pipeline degli asset - ridimensionamento immagini/WebP tramite
[bx-image](https://github.com/ortus-boxlang/bx-image) (una dipendenza
obbligatoria, installata insieme a bx-markdown/bx-esapi) e bundling
CSS/JS. Tutto qui è attivo di default con impostazioni ragionevoli - un
progetto appena generato con `bxSites new` non deve toccare nulla di
questo. Vedi [Immagini Responsive](guides/images.md) per il quadro
completo, incluso ciò che deliberatamente non è coperto (AVIF, GIF
animate, SVG).

- `assets.fingerprint` - `true` (il valore predefinito). Assegna un nome
  basato sull'hash del contenuto a ogni variante di immagine generata e a
  ogni bundle CSS/JS (ad es. `screenshot-800w.a3f9c2e1.webp`,
  `bundle.a3f9c2e1.css`) così possono essere serviti con header di cache
  sicuri a lunghissima scadenza - un build cambia il nome proprio del
  file solo quando il suo contenuto cambia davvero. Non rinomina i file
  originali di un progetto sotto `docs/assets/` - solo l'output generato
  dalla pipeline ottiene l'impronta digitale, quindi qualsiasi altra cosa
  che fa riferimento a un asset con il proprio nome file semplice (una
  card di download `::: file`, un link markdown grezzo) continua a
  funzionare senza modifiche.
- `assets.bundle` - `true` (il valore predefinito). Concatena
  `extraCss`/`extraJs` in un unico file con impronta digitale ciascuno -
  puro BoxLang/JVM, nessuna toolchain Node/esbuild. Ricade sull'esatto
  comportamento odierno per-URL con `<link>`/`<script>`, senza modifiche,
  nel momento in cui una qualsiasi voce dell'elenco è un URL esterno (un
  link CDN) o nomina un file che non esiste - vedi
  [Immagini Responsive](guides/images.md#css-js-bundling).
- `assets.images.enabled` - `true` (il valore predefinito). Ogni immagine
  idonea sotto `docs/assets/**` (`.png`/`.jpg`/`.jpeg`) ottiene varianti
  ridimensionate/WebP generate tramite bx-image, e ogni `<img>`
  corrispondente viene riscritto in un `<picture>` con `srcset`. Imposta
  `false` per ricadere sulla semplice copia non elaborata delle immagini,
  esattamente come prima che questa funzionalità esistesse.
- `assets.images.widths` - i breakpoint da generare, in pixel. Una
  larghezza pari o superiore a quella propria di una data immagine viene
  saltata automaticamente per quell'immagine - niente viene mai
  ingrandito.
- `assets.images.formats` - `"original"` mantiene il formato sorgente
  come ripiego dell'`<img>`; `"webp"` aggiunge una variante
  `<source type="image/webp">` alla stessa dimensione. Entrambi attivi di
  default.

## `mermaid`

`false` (il valore predefinito) - nessun supporto per i diagrammi
[Mermaid](https://mermaid.js.org/) distribuito. `true` carica
`mermaid.js` lato client e renderizza ogni blocco di codice delimitato
` ```mermaid ` come un diagramma. Vedi
[Estensioni Markdown](guides/markdown.md#diagrams) per la sintassi.

=== "YAML"
    ```yaml title="bxsites.yaml"
    mermaid: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mermaid": true }
    ```

## `math`

`false` (il valore predefinito) - nessun [KaTeX](https://katex.org/)
distribuito. `true` lo carica lato client e compone `$...$`/`$$...$$`
scritti direttamente nel markdown di una pagina. Vedi
[Estensioni Markdown](guides/markdown.md#math) per la sintassi.

=== "YAML"
    ```yaml title="bxsites.yaml"
    math: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "math": true }
    ```

Le ammonizioni (box di richiamo in stile nota/avviso/suggerimento), le
schede di contenuto e le annotazioni `hl_lines`/`linenums`/`title` sui
blocchi di codice delimitati sono sempre disponibili nel markdown di ogni
pagina, senza bisogno di configurazione - vedi
[Estensioni Markdown](guides/markdown.md#admonitions).

## `openapi`

`false` (il valore predefinito) - nessun
[Swagger UI](https://swagger.io/tools/swagger-ui/) distribuito. `true` lo
carica lato client e renderizza ogni blocco di contenuto
`::: openapi src="..."` come widget interattivo per la specifica
OpenAPI/Swagger referenziata (JSON o YAML). Vedi
[OpenAPI / Swagger](guides/openapi.md) per la
sintassi.

=== "YAML"
    ```yaml title="bxsites.yaml"
    openapi: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "openapi": true }
    ```

## `plugins`

`[]` (il valore predefinito) - un array di nomi di moduli BoxLang da
attivare come plugin. Installare un modulo plugin (`box install`) non lo
attiva mai da solo; deve essere nominato anche qui. Vedi
[Plugin](guides/plugins.md) per come scriverne uno.

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

## `i18n`

Metadati per la convenzione delle cartelle-locale
[`docs/i18n/<code>/`](guides/i18n.md) - una locale si compila
automaticamente una volta che la sua cartella esiste; `i18n` fornisce
solo la sua etichetta di visualizzazione/direzione per il selettore di
lingua.

- `i18n.defaultLocale` - `{ "code", "label", "strings" }` per l'albero
  `docs/` regolare del progetto, con valore predefinito
  `{ "code": "en", "label": "English" }`. Va impostato solo quando la tua
  locale predefinita non è l'inglese.
- `i18n.locales` - `[]` (il valore predefinito) - un array di
  `{ "code", "label", "dir", "strings" }` per ogni altra locale. `code`
  funge sia da nome della cartella `docs/i18n/<code>/` sia da prefisso
  dell'URL compilato - solo lettere/cifre/trattini (`es`, `pt-BR`,
  `zh-Hans`). `dir` è `"ltr"` (il valore predefinito) oppure `"rtl"`.
  `strings` sovrascrive le stringhe UI dell'interfaccia del tema proprie
  di quella locale (placeholder della ricerca, "In questa pagina," ...) -
  consulta [Internazionalizzazione](guides/i18n.md#interfaccia-del-tema-stringhe-ui)
  per l'elenco completo delle chiavi; `de`/`es`/`it`/`ja` includono già
  una traduzione integrata, quindi `strings` serve solo per sovrascrivere
  una chiave o aggiungere un'altra locale.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    i18n:
      defaultLocale: { code: en, label: English }
      locales:
        - { code: es, label: Español }
        - { code: ar, label: العربية, dir: rtl }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": [
    			{ "code": "es", "label": "Español" },
    			{ "code": "ar", "label": "العربية", "dir": "rtl" }
    		]
    	}
    }
    ```

Vedi [Internazionalizzazione](guides/i18n.md) per il quadro completo -
il fallback per le pagine non tradotte, il selettore di lingua, e cosa non
è ancora tradotto.

## `variables`

`{}` (il valore predefinito) - un oggetto di valori riutilizzabili, di
qualsiasi forma, referenziato da qualsiasi pagina Markdown come
`{{ dotted.path }}`. Vedi
[Variabili e Funzioni Magiche](guides/variables-and-functions.md).

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product: { name: "BoxLang", supportEmail: "support@example.com" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": { "name": "BoxLang", "supportEmail": "support@example.com" }
    	}
    }
    ```

```markdown title="docs/index.md"
Welcome to {{ company }}! We build {{ product.name }}.
```

Un file `docs/functions.bxs` (nessuna chiave di configurazione propria -
per convenzione, come `docs/nav.json`/`docs/blog/authors.yml`) aggiunge
"funzioni magiche" BoxLang accanto a `variables` - richiamabili allo
stesso modo, come `{{ $name(...) }}`. Vedi
[Variabili e Funzioni Magiche](guides/variables-and-functions.md#funzioni-magiche).

Una cartella `docs/data/*.yaml`/`.yml`/`.json` (anch'essa senza una
propria chiave di configurazione) aggiunge dati strutturati,
annidati/a forma di array - raggiungibili come
`{{ data.<file>.<key> }}` - per tutto ciò a cui la forma piatta propria
di `variables` non si adatta bene (un elenco del team, una tabella dei
prezzi). Vedi [File di dati](guides/data-files.md).

## Versionamento

I documenti versionati sono convenzione più che configurazione - non c'è
una chiave di `bxsites.yaml` per questo. Aggiungi una cartella
`docs/versions/`, e ogni sottocartella diretta al suo interno viene
compilata come un proprio albero di documenti completamente
autonomo, accanto al tuo `docs/` regolare (che si compila sempre come
"Latest"):

```
docs/
├── index.md
├── guides/
└── versions/
    ├── 1.0/
    │   ├── index.md
    │   └── guides/
    └── 2.0/
        ├── index.md
        └── guides/
```

Ogni cartella di versione è un albero normale a forma di `docs/` - il
proprio `index.md`, la propria nav, le proprie pagine - compilato in
`site/versions/<name>/` con ogni link interno prefissato di conseguenza,
e condividendo l'unico `bxsites.yaml` di configurazione/tema del progetto.
I nomi delle versioni si ordinano dal più recente, numericamente
piuttosto che alfabeticamente (quindi `2.0` si ordina prima di `10.0`), e
ogni tema renderizza automaticamente un menu a discesa per il cambio
versione nell'header non appena esiste più di una versione - niente da
attivare esplicitamente. Un file sciolto posizionato direttamente sotto
`docs/versions/` (non dentro una sottocartella) viene ignorato.

`sitemap.xml` e `llms.txt` includono le pagine di ogni versione insieme a
quelle del sito principale.
