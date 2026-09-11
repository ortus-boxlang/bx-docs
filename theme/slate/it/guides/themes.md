---
title: Temi
order: 1
icon: phosphor-duotone:palette
tags: [guide, temi]
---

# Temi

I temi sono template nativi BoxLang `.bxm` - non c'è alcun motore di
template separato o passaggio di build coinvolto.

## Integrati

| Tema | Base | Note |
|---|---|---|
| `bootstrap` (predefinito) | [Bootstrap 5](https://getbootstrap.com/), incluso localmente | Font Poppins, navbar con gradiente del brand |
| `material` | CSS in stile Material scritto a mano | Layout a card, ombre di elevazione, font Roboto |
| `tailwind` | [Tailwind Play CDN](https://tailwindcss.com/) | Guidato da classi utility, nessun passaggio di build |
| `docsy` | CSS scritto a mano, derivato da `material` | Look ispirato a Read the Docs/Docsy, blu navy da manuale di riferimento |
| `slate` | CSS scritto a mano, derivato da `material` | Ispirato a Stripe/Slate - una sidebar permanentemente scura indipendentemente dalla modalità chiara/scura |
| `docusaurus` | CSS scritto a mano, derivato da `material` | Ispirato a Docusaurus - navbar colorata a larghezza intera e in grassetto, card arrotondate |
| `justthedocs` | CSS scritto a mano, derivato da `material` | Minimalismo ispirato a Just the Docs - il box di ricerca vive in cima alla sidebar |
| `vuepress` | CSS scritto a mano, derivato da `material` | Accento verde ispirato a VuePress, angoli morbidi e arrotondati |
| `gitbook` | CSS scritto a mano, derivato da `material` | Colonna di lettura centrata ispirata a GitBook, intestazioni serif |
| `notion` | CSS scritto a mano, derivato da `material` | Sidebar senza bordi ispirata a Notion, UI quasi in scala di grigi, spazio bianco generoso |

I sette temi derivati da `material` sopra riutilizzano gli identici
template BoxLang di `material` (layout.bxm/page.bxm/search.bxm) invariati,
tranne per una rinomina con prefisso di classe CSS con ambito limitato -
solo `assets/style.css` (e, per `justthedocs`, una riga `<bx:include>`
riposizionata per spostare il box di ricerca nella sidebar) differisce,
quindi ereditano lo stesso set completo di funzionalità e lo stesso
comportamento air-gapped-capable che `material` ha già.

Il CSS/JS proprio di ogni tema integrato (il pacchetto CSS/JS di Bootstrap,
highlight.js, Alpine.js, lunr.js per il provider di ricerca `local`
predefinito, e Mermaid quando `mermaid` è attivato) viene incluso con
questo modulo e copiato direttamente in ogni `site/` compilato - nessuna
CDN, nessun accesso a internet necessario per visualizzare un sito
compilato. Il motore di utility proprio del tema `tailwind` (un
compilatore JIT lato client, non un foglio di stile statico) e altre
funzionalità opzionali che attivi tu stesso (`math`, ricerca Algolia,
Google Analytics) continuano a caricarsi da una CDN o da un'API ospitata -
vedi [Siti air-gapped/offline](#siti-air-gappedoffline) più sotto.

`bootstrap`, `material` e `tailwind` applicano la stessa palette del
brand BoxLang (un gradiente `#00FF78 -> #00DBFF` e un accento `#FFF500`);
i sette temi della galleria sotto di essi usano ciascuno la propria
palette distinta, ispirata alla piattaforma da cui prendono il proprio
look - vedi la tabella sopra. Ognuno dei dieci include lo stesso insieme
di funzionalità di pagina indipendentemente dalla palette:

- **Un sommario "In questa pagina" in pagina**, generato dalle intestazioni
  `h2`/`h3` proprie di ogni pagina.
- **Breadcrumb**, che mostrano la catena di antenati di una pagina quando è
  annidata più di un livello sotto un antenato collegato.
- **Link pagina precedente/successiva** in fondo all'articolo, seguendo
  l'ordine di lettura della nav stessa.
- **Blocchi di codice con evidenziazione della sintassi**, tramite
  [highlight.js](https://highlightjs.org/) più una grammatica BoxLang
  propria (` ```bx `/` ```boxlang `/` ```cfscript `), ciascuno con un
  **pulsante di copia** - mostrato al passaggio del mouse sui dispositivi
  che lo supportano, sempre visibile sui dispositivi touch (dove non c'è
  hover per rivelarlo). Vedi
  [Estensioni Markdown](markdown.md#code-blocks).
- **Webfont autoospitati** - nessuna richiesta a `fonts.googleapis.com` al
  momento della visualizzazione.
- **Un interruttore modalità scura/chiara**, alimentato da
  [Alpine.js](https://alpinejs.dev/) per la reattività. La scelta del
  visitatore viene ricordata in `localStorage` (ricadendo sulla
  preferenza del suo sistema operativo), e applicata prima del primo
  rendering per evitare un lampo del tema sbagliato.
- **Un header responsivo** che resta su una sola riga a qualsiasi
  larghezza - un viewport stretto restringe il box di ricerca invece di
  farlo andare a capo - più una nav laterale comprimibile (un interruttore
  a hamburger in tutti e dieci i temi).
- **Scorciatoie da tastiera** nel box di ricerca: `/` porta il focus sulla
  ricerca da qualsiasi punto della pagina, e `Escape` chiude i risultati.
  Vedi [Ricerca](search.md).
- **Un link al repository e una riga "Modifica questa pagina"/"Ultimo
  aggiornamento"**, quando le opzioni `repo`/`lastUpdated` di
  `bxsites.yaml` sono impostate. Vedi
  [Configurazione](../configuration.md#repo).
- **Un link "Scarica Markdown"**, accanto a "Modifica questa pagina" - il
  sorgente `.md` grezzo di ogni pagina viene pubblicato accanto al proprio
  HTML compilato (`guides/themes.md` si trova accanto a
  `guides/themes/index.html`), così una persona (o un LLM) può leggere la
  pagina come Markdown puro direttamente invece di analizzare l'HTML
  renderizzato. Sempre attivo, nessuna configurazione necessaria. Vedi
  [Per iniziare](../getting-started.md#downloading-a-page-as-markdown).
- **Un footer opzionale** (copyright, link `social`, un credito "Built
  with BxSites") quando `footer` di `bxsites.yaml` è `true`. Vedi
  [Configurazione](../configuration.md#footer).
- **Un selettore di versione**, che appare automaticamente non appena un
  progetto ha una cartella `docs/versions/` con più di una versione al
  suo interno. Vedi [Configurazione](../configuration.md#versionamento).
- **Un `404.html` con tema applicato**, servito automaticamente dalla
  maggior parte degli host statici (incluso GitHub Pages) per qualsiasi
  percorso non corrispondente. Aggiungi un `404.md` alla radice di
  `docs/` (o `src/`) per sostituirne titolo e contenuto con i tuoi - non
  viene mai compilato come una pagina normale (nessuna voce nel nav,
  nessun URL in `sitemap.xml`), ma renderizzato al suo posto in
  `site/404.html`.
- **Un logo e una favicon personalizzati**, quando `theme.logo`/
  `theme.favicon` di `bxsites.yaml` sono impostati. Vedi
  [Configurazione](../configuration.md#theme).
- **Una nav laterale comprimibile**, opzionale tramite
  `theme.options.navCollapsible`. Vedi
  [Configurazione](../configuration.md#theme).
- **Google Analytics**, quando `analytics` di `bxsites.yaml` è configurato.
  Vedi [Configurazione](../configuration.md#analytics).
- **Social share card** (meta tag Open Graph + Twitter Card), ricavate
  dal frontmatter `description` di ogni pagina (o dalla `description` a
  livello di sito) e dal suo `ogImage` (o da quello a livello di sito) -
  generate automaticamente per pagina in modo opzionale tramite
  `generateOgImages` di `bxsites.yaml`. Vedi
  [Configurazione](../configuration.md#ogimage).
- **Tag di pagina, un'icona e una riga di riepilogo**, tutti opzionali
  tramite il frontmatter di una pagina - i tag vengono renderizzati come
  badge che collegano a un indice `/tags/` a livello di sito. Vedi
  [Per iniziare](../getting-started.md#add-pages).
- **Una nav esplicita in sostituzione**, in `bxsites.yaml` o nel proprio
  `docs/nav.json`, che sostituisce la deduzione dalle cartelle per siti di
  grandi dimensioni. Vedi [Configurazione](../configuration.md#nav).
- **CSS/JS extra**, iniettati tramite `extraCss`/`extraJs` di
  `bxsites.yaml`. Vedi
  [Configurazione](../configuration.md#extracss--extrajs).
- **Box di richiamo per ammonizioni (nota/avviso/suggerimento/...)**,
  attivi di default nel markdown di qualsiasi pagina, incluse le varianti
  comprimibili - nessuna configurazione necessaria. Vedi
  [Estensioni Markdown](markdown.md#admonitions).
- **Note a piè di pagina e liste di definizioni**, opzionali tramite
  `markdown` di `bxsites.yaml`. Vedi
  [Estensioni Markdown](markdown.md#footnotes).
- **Schede di contenuto**, **numeri di riga/righe evidenziate/titoli del
  codice** e **indicatori di diff/cornici terminale** per i blocchi di
  codice, nessuna configurazione necessaria. Vedi
  [Estensioni Markdown](markdown.md#content-tabs).
- **Immagini responsive** - varianti ridimensionate + WebP e una
  riscrittura `<picture>` per ogni immagine idonea in `docs/assets/**`,
  attiva di default. Vedi [Immagini Responsive](images.md).
- **Diagrammi Mermaid**, opzionali tramite `mermaid` di `bxsites.yaml`.
  Vedi [Estensioni Markdown](markdown.md#diagrams).
- **Matematica** (KaTeX), opzionale tramite `math` di `bxsites.yaml`. Vedi
  [Estensioni Markdown](markdown.md#math).

Imposta quale tema usa un progetto in `bxsites.yaml`:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: material }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "material" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "material"
    ```

## Installare un tema pubblicato

Un tema pubblicato su ForgeBox si installa con nient'altro che il
binario `bxSites` stesso - non serve `box`/CommandBox. Sfoglia i pacchetti
già pubblicati nella categoria
[`bxsites-themes`](https://www.forgebox.io/type/bxsites-themes) su
ForgeBox:

```bash title="Utilizzo"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

Questo scarica lo zip del pacchetto e lo estrae in
`themes/bx-sites-theme-blog1/` alla radice del progetto, verificando che
soddisfi il contratto `ThemeProvider` sotto prima di terminare. Un
progetto può portare con sé più temi installati fianco a fianco in
questo modo e passare dall'uno all'altro semplicemente per nome:

=== "YAML"
    ```yaml title="bxsites.yaml"
    theme: { name: bx-sites-theme-blog1 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "theme": { "name": "bx-sites-theme-blog1" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [theme]
    name = "bx-sites-theme-blog1"
    ```

Un tema non necessita di alcun coinvolgimento del modulo BoxLang/
class-loader (a differenza di un plugin) - sono puri file, quindi non
c'è un passaggio di attivazione separato come per `install:plugin`;
impostare `theme.name` è l'unico collegamento necessario. Vedi
[`install:theme`](../cli-reference.md#installtheme) nel riferimento CLI.

Parti da un tema costruito per un altro generatore di siti statici
invece? Vedi [Importare un tema](theme-import.md) - `theme:import`
converte meccanicamente, con il massimo impegno possibile, i file
template propri di un tema mkdocs/jekyll/hugo in uno scheletro
`themes/<name>/`.

## Siti air-gapped/offline

Un sito compilato funziona senza alcun accesso a internet per
impostazione predefinita, per `bootstrap`, `material` e i sette temi
derivati da `material` (`docsy`, `slate`, `docusaurus`, `justthedocs`,
`vuepress`, `gitbook`, `notion`) con il provider di ricerca `local`
predefinito: il CSS/JS proprio di Bootstrap, highlight.js, Alpine.js e
lunr.js sono tutti inclusi con questo modulo
(`resources/assets/vendor/`) e copiati direttamente in
`site/assets/vendor/` al momento del build - nessun tag `<script>`/
`<link>` verso una CDN in nessun punto dell'HTML generato per nessuno di
questi. Attivare la chiave `mermaid` di `bxsites.yaml` include Mermaid
nello stesso modo - il suo pacchetto `mermaid.min.js` viene copiato in
`site/assets/vendor/mermaid/` e ogni tema integrato lo carica da lì, così
i diagrammi continuano a essere renderizzati con zero richieste in
uscita.

Alcune cose comunicano ancora con la rete, solo quando le attivi tu
stesso:

- Il motore di utility proprio del tema `tailwind` è un compilatore JIT
  lato client caricato da `cdn.tailwindcss.com` - non è un foglio di
  stile statico che questo modulo può includere allo stesso modo, quindi
  questo tema non è ancora predisposto per l'uso air-gapped.
- Il motore di layout proprio di Mermaid carica in modo differito un
  chunk aggiuntivo, `elk-api.js`, da jsDelivr - ma solo per i tipi di
  diagramma che optano per l'algoritmo di layout `elk`; il pacchetto
  incluso `mermaid.min.js` renderizza da solo ogni altro tipo di
  diagramma.
- L'opzione `math` di `bxsites.yaml` carica KaTeX (sia il suo JS sia i
  propri file font) da una CDN quando è attivata.
- `searchProvider.provider: "algolia"` e `analytics.provider: "google"`
  comunicano intrinsecamente con un'API ospitata/un endpoint di
  tracciamento - includere il file JS localmente non eliminerebbe questa
  dipendenza.

Se il tuo ambiente di distribuzione non ha davvero alcun accesso a
internet, limitati a `bootstrap`/`material`/uno dei sette temi derivati
da `material`, al provider di ricerca `local` predefinito, evita i
diagrammi Mermaid con layout `elk` se `mermaid` è attivo, e lascia
disattivati `math`/Algolia/analytics.

Vedi [Icone](icons.md) per come il frontmatter `icon` proprio di una
pagina (o l'`icon` proprio di una voce di `nav.json`) viene risolto in
un'emoji, un'icona con nome da una delle otto librerie incluse, oppure
un SVG del tuo progetto.

## Il contratto `ThemeProvider`

Un tema è semplicemente una cartella con:

- **`layout.bxm`** (obbligatorio) - il guscio HTML esterno + la nav.
  Riceve `variables.page`, `variables.nav`, `variables.siteConfig`,
  `variables.themeDir` e `variables.basePath` nello scope, e include il
  file gemello `page.bxm` tramite `#variables.themeDir#/page.bxm`.
  `variables.basePath` è sempre un percorso relativo alla radice che
  termina con `/` (`/` di default, `/my-docs/` quando `baseURL` di
  `bxsites.yaml` lo sovrascrive) - anteponilo a ogni `href`/`src` interno,
  invece di codificare un `/` iniziale fisso, così il tema continua a
  funzionare quando il sito viene servito da un sotto-percorso.
- **`page.bxm`** (obbligatorio) - il corpo dell'articolo. Renderizza
  `variables.page.contentHtml` - il markdown già convertito.
- **`search.bxm`** (opzionale) - il markup del box di ricerca, incluso da
  `layout.bxm` solo quando `search` di `bxsites.yaml` è `true`. Vedi
  [Ricerca](search.md).
- **`assets/`** (opzionale) - CSS/JS del tema, copiati in
  `site/assets/theme/` al momento del build.

`variables.page.editUrl`/`.lastUpdated` (stringhe vuote quando non
configurati) e `variables.siteConfig.repo`/`.social`/`.footer` sono
sempre disponibili anch'essi, a supporto delle funzionalità di link al
repository/link di modifica/ultimo aggiornamento/footer sopra descritte -
un tema personalizzato decide da sé se e come renderizzarli, come
tutto il resto. `variables.versions` (`[ { label, url } ]`, con "Latest"
per primo) e `variables.currentVersion` (l'etichetta `label` in fase di
rendering in questo momento) sono a supporto del selettore di versione -
vuoti/`"Latest"` per un progetto non versionato, quindi un tema deve
renderizzare un selettore solo quando `variables.versions.len() gt 1`.
Ogni tema integrato ottiene le proprie icone repository/social da una
piccola tabella SVG condivisa,
`<bx:include template="#variables.moduleAssetsDir#/icons.bxm">`
(definisce `bxsitesIcon( name )`, uno tra `github`, `twitter`/`x`, `rss`,
`youtube`, `linkedin`, `facebook`, `bluesky`, `threads`, `slack`,
`patreon`, `email`, `edit`, `clock`, con un glifo di link generico come
ripiego) - un tema personalizzato può includerla nello stesso modo,
oppure fornire le proprie icone del tutto.

Una cartella tema a cui manca uno dei due file obbligatori fallisce
subito con un chiaro errore `BxSites.InvalidTheme` al momento del build,
invece di un confuso errore di template nel profondo del rendering.

## Personalizzare i colori senza sovrascrivere un tema

Per una piccola modifica di colore/font, forkare un intero tema è
eccessivo - ogni tema integrato legge la propria palette da una manciata
di proprietà CSS personalizzate su `:root`, ridichiarate sotto
`[data-theme="dark"]` per la modalità scura. L'
[`extraCss`](../configuration.md#extracss--extrajs) di `bxsites.yaml` si
carica *dopo* il foglio di stile proprio del tema, quindi una
ridichiarazione con la stessa specificità al suo interno vince senza
toccare affatto `resources/themes/`:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/brand.css ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/brand.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/brand.css" ]
    ```

```css title="docs/assets/brand.css" linenums="1"
/* docs/assets/brand.css - copiato in site/assets/brand.css al momento del build */
:root {
	--bxsites-gradient-start: #7C3AED;
	--bxsites-gradient-end: #DB2777;
	--bxsites-accent: #FBBF24;
	--bxsites-link: #7C3AED;
	--bxsites-link-hover: #9F5AF0;
}

[data-theme="dark"] {
	--bxsites-link: #C4B5FD;
	--bxsites-link-hover: #DDD6FE;
}
```

L'insieme proprio del tema `bootstrap`
(`resources/themes/bootstrap/assets/style.css`) è composto da
`--bxsites-gradient-start`/`-end`, `--bxsites-accent`, `--bxsites-bg`,
`--bxsites-text`, `--bxsites-sidebar-bg`, `--bxsites-sidebar-text`,
`--bxsites-border`, `--bxsites-link`, `--bxsites-link-hover`,
`--bxsites-code-bg`, `--bxsites-step-marker-bg`, `--bxsites-step-marker-text`,
`--bxsites-step-line`, `--bxsites-step-success-bg`/`-text`,
`--bxsites-step-warning-bg`/`-text`, `--bxsites-step-danger-bg`/`-text` e
l'[insieme `--bxsites-table-*`](tables.md#temi), che disegna la card, la
fascia di intestazione, i separatori e le tinte zebra/hover di una tabella
renderizzata.
Ogni tema integrato garantisce `--bxsites-gradient-start`/`-end`,
`--bxsites-accent` e gli insiemi `--bxsites-step-*` e `--bxsites-table-*`
con questi nomi esatti,
quindi `extraCss` può sempre ridefinire il colore del brand/gli accenti
dello stepper indipendentemente dal tema - ma solo `bootstrap`, `slate` e
`notion` espongono anche `--bxsites-bg`/`-text`/`-sidebar-bg`/`-sidebar-text`/`-border`/`-link`/`-link-hover`/`-code-bg`
con questi nomi (`justthedocs` alias tutti tranne i due `-sidebar-*` allo
stesso modo). Ogni altro tema integrato (`material`, `tailwind`,
`docsy`, `docusaurus`, `vuepress`, `gitbook`) usa invece i propri nomi di
proprietà personalizzata interni per quel secondo gruppo (ad es. il
proprio `assets/style.css` di material usa `--md-bg`/`--md-ink`/`--md-link`/...)
- apri il proprio `assets/style.css` di quel tema per trovare i suoi
nomi reali prima di sovrascriverne uno tramite `extraCss`. Qualsiasi cosa
oltre a colore/font (layout, aggiungere/rimuovere elementi di contorno)
richiede una vera sovrascrittura o un tema personalizzato - vedi sotto.

Il resto supporta il blocco di direttive
[`::: stepper`/`::: step`](content-blocks.md#stepper) -
`--bxsites-step-marker-bg`/`-text` sono lo sfondo/colore testo del
cerchio numerato predefinito (`bootstrap`/`material` lo impostano di
default sull'`--bxsites-accent` proprio del tema; `tailwind` usa una
coppia dedicata verde acqua/menta dato che non ha un unico token di
accento condiviso), `--bxsites-step-line` è la linea di collegamento tra
i passi, e le coppie `-success`/`-warning`/`-danger` supportano
l'attributo opzionale `color="..."` di un passo - a differenza del
marcatore predefinito, queste tre sono la stessa coppia fissa di
sfondo/testo sia in modalità chiara sia scura (un badge autonomo, non
legato all'accento del brand del tema), quindi non c'è alcuna
sovrascrittura `[data-theme="dark"]` da ridichiarare:

```css title="docs/assets/brand.css" linenums="1"
:root {
	--bxsites-step-marker-bg: #7C3AED;
	--bxsites-step-marker-text: #fff;
	--bxsites-step-success-bg: #059669;
	--bxsites-step-success-text: #fff;
}

[data-theme="dark"] {
	--bxsites-step-marker-bg: #C4B5FD;
	--bxsites-step-marker-text: #1b1f21;
}
```

## Banner hero della homepage

Ogni tema integrato include CSS per un banner homepage a larghezza
intera con un'immagine principale e pulsanti di call-to-action - il
proprio `docs/index.md` di questo stesso sito lo usa. Non c'è alcun
blocco di direttiva o configurazione per questo, solo puro HTML che
qualsiasi pagina può inserire (una homepage è solo una pagina normale,
`order: 1` o comunque la prima nella nav):

```markdown title="docs/index.md"
<div class="bxsites-hero">
	<img class="bxsites-hero__banner" src="assets/home-banner.jpg" alt="...">
	<div class="bxsites-hero__actions">
		<a class="bxsites-hero__btn bxsites-hero__btn--primary" href="getting-started.md">Get Started</a>
		<a class="bxsites-hero__btn bxsites-hero__btn--secondary" href="https://github.com/your/repo">View on GitHub</a>
	</div>
</div>
```

`bxsites-hero__btn--primary`/`--secondary` sono gli stessi due stili di
accento che ogni tema usa già altrove - scambia, rimuovi o aggiungi
pulsanti liberamente, e ridimensiona/sostituisci l'immagine propria di
`bxsites-hero__banner` tramite un `src` relativo a `docs/assets/` nello
stesso modo in cui si risolve qualsiasi altra immagine.

## Sovrascrivere un tema

Metti il tuo `layout.bxm` + `page.bxm` (e opzionalmente `search.bxm` /
`assets/`) in una cartella `theme/` alla radice del tuo progetto. BxSites
preferisce una sovrascrittura `theme/` a livello di progetto sia rispetto
a un tema `themes/<name>/` installato sia rispetto a qualsiasi tema
integrato, purché soddisfi il contratto sopra descritto - i temi
integrati sotto `resources/themes/` proprio di questo modulo sono un
buon punto di partenza da copiare e adattare. Ordine di risoluzione
completo: `theme/` (questa sezione) -> `themes/theme.name/` (un
[tema installato](#installare-un-tema-pubblicato), se `theme.name`
corrisponde a uno) -> un tema integrato chiamato `theme.name`.

Un esempio pratico - parti da `bootstrap` e sostituisci la sua palette del
brand e il font delle intestazioni con i tuoi, mantenendo tutto il resto
(nav, ricerca, modalità scura, evidenziazione del codice, ...) esattamente
come già funziona:

```text title="Struttura del progetto"
my-project/
├── bxsites.yaml
├── docs/
└── theme/                    ← sovrascrittura a livello di progetto, verificata prima di qualsiasi tema integrato
    ├── layout.bxm             ← copiato da resources/themes/bootstrap/layout.bxm
    ├── page.bxm                ← copiato da resources/themes/bootstrap/page.bxm, invariato
    ├── search.bxm               ← copiato invariato
    └── assets/
        └── style.css              ← copiato da assets/style.css di bootstrap, poi modificato
```

1. Copia i tre file `.bxm` e `assets/style.css` da
   `resources/themes/bootstrap/` di questo modulo nel `theme/` del tuo
   progetto.
2. Modifica solo ciò che serve cambiare. Per sostituire la palette del
   brand e il font, basta agire sulla parte iniziale di
   `theme/assets/style.css`:

   ```css title="theme/assets/style.css" linenums="1"
   :root {
   	--bxsites-gradient-start: #7C3AED;  /* era #00FF78 */
   	--bxsites-gradient-end: #DB2777;    /* era #00DBFF */
   	--bxsites-accent: #FBBF24;          /* era #FFF500 */
   }

   body {
   	font-family: "Inter", system-ui, sans-serif;  /* era "Poppins" */
   }
   ```

3. Esegui `bxSites build` (o `serve` mentre iteri) - BX
   Sites recepisce `theme/` automaticamente, nessuna modifica a
   `bxsites.yaml` necessaria (una cartella `theme/` a livello di progetto
   ha sempre la precedenza sul tema integrato nominato in `theme.name`).
   Tutto ciò che non hai toccato - rendering della nav, ricerca,
   interruttore modalità scura, annotazioni del codice - continua a
   funzionare esattamente come nel tema `bootstrap` originale, dato che è
   sempre lo stesso identico markup `layout.bxm`/`page.bxm` sottostante.

Una cartella `theme/` di progetto è comunque tutto-o-niente - una volta
che BxSites ne trova una, viene usata al posto del tema integrato per
intero, quindi necessita comunque del proprio `layout.bxm` + `page.bxm`
anche se l'unica cosa cambiata è `assets/style.css` (una cartella priva
di uno dei due file fallisce subito con `BxSites.InvalidTheme` invece di
ricadere silenziosamente su un altro tema). Per una modifica solo CSS
senza `.bxm`, usa
[`extraCss`](#personalizzare-i-colori-senza-sovrascrivere-un-tema) come
sopra invece - si sovrappone a qualunque tema `bxsites.yaml` nomini,
senza alcuna cartella `theme/` coinvolta. `theme/` serve per quando devi
anche cambiare il markup stesso, argomento trattato di seguito.

## Scrivere un tema da zero

Un tema necessita solo dei due file obbligatori, quindi ecco un esempio
davvero minimale - niente Bootstrap/Tailwind, niente modalità scura,
niente UI di ricerca - per mostrare esattamente cosa è richiesto rispetto
a cosa aggiungono in più i temi integrati. Salva entrambi come
`theme/layout.bxm` e `theme/page.bxm` nel tuo progetto - una cartella
`theme/` a livello di progetto viene recepita automaticamente (come
sopra), nessuna modifica a `bxsites.yaml` necessaria:

```bx title="theme/layout.bxm" linenums="1"
<!-- theme/layout.bxm -->
<bx:script>
	function renderNav( required array nodes ) {
		var html = "<ul>"
		for ( var node in arguments.nodes ) {
			html &= "<li>"
			html &= len( node.url )
				? '<a href="' & variables.basePath & node.url & '">' & encodeForHTML( node.title ) & '</a>'
				: encodeForHTML( node.title )
			if ( node.children.len() ) {
				html &= renderNav( node.children )
			}
			html &= "</li>"
		}
		return html & "</ul>"
	}
</bx:script>
<bx:output>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>#encodeForHTML( variables.page.title )# - #encodeForHTML( variables.siteConfig.name )#</title>
	<link rel="stylesheet" href="#variables.basePath#assets/theme/style.css">
</head>
<body>
	<header><a href="#variables.basePath#">#encodeForHTML( variables.siteConfig.name )#</a></header>
	<nav>#renderNav( variables.nav )#</nav>
	<main>
</bx:output>
<bx:include template="#variables.themeDir#/page.bxm">
<bx:output>
	</main>
</body>
</html>
</bx:output>
```

```bx title="theme/page.bxm" linenums="1"
<!-- theme/page.bxm -->
<bx:output>
<article>
	<h1>#encodeForHTML( variables.page.title )#</h1>
	#variables.page.contentHtml#
</article>
</bx:output>
```

Questo è un tema completo e funzionante -
`variables.page.contentHtml` è il markdown già convertito (evidenziazione
della sintassi, ammonizioni, schede, matematica e tutto il resto), quindi
non c'è più nulla da analizzare, solo da impaginare. Da qui, aggiungi
qualsiasi cosa dei temi integrati che vuoi davvero: `search.bxm` (incluso
solo quando `search` di `bxsites.yaml` è `true` - vedi [Ricerca](search.md)),
un interruttore modalità scura (copia la coppia `x-data`/`x-init` di
Alpine.js dal tag `<body>` di `resources/themes/bootstrap/layout.bxm` e il
blocco CSS `[data-theme="dark"]` corrispondente), breadcrumb/tag/link
pagina precedente-successiva (`page.bxm` in qualsiasi tema integrato
mostra lo schema - ognuno è solo un `if` intorno a una piccola funzione di
rendering, tutti guidati da campi già presenti su `variables.page`),
oppure una cartella `assets/` per il tuo CSS/JS, copiata automaticamente
in `site/assets/theme/` al momento del build.
