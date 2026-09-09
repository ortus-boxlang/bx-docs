---
title: Immagini Responsive
order: 5
icon: phosphor-duotone:image
tags: [guide, immagini, prestazioni]
---

# Immagini Responsive

Ogni immagine idonea sotto `docs/assets/` ottiene automaticamente varianti
ridimensionate/WebP, e ogni `<img>` corrispondente nelle tue pagine viene
riscritto in un `<picture>` responsivo - nessuna nuova sintassi Markdown,
nessuna configurazione necessaria per attivarlo. È costruito su
[bx-image](https://github.com/ortus-boxlang/bx-image), una dipendenza
obbligatoria insieme a bx-markdown/bx-esapi/bx-yaml (vedi
[Per iniziare](../getting-started.md#install)).

## Come funziona

Scrivi un'immagine nel modo consueto - sintassi Markdown o HTML grezzo,
relativa al file della pagina esattamente come già funziona un
[link a pagina](markdown.md):

```markdown title="Esempio"
![Un sito appena compilato](../assets/screenshot.png)
```

Al momento del build, `screenshot.png` viene ridimensionata verso ogni
larghezza configurata più stretta della propria (mai ingrandita), più una
ricodifica WebP alla stessa dimensione, e la pagina compilata ottiene:

```html title="Output renderizzato" linenums="1"
<picture>
	<source type="image/webp" srcset="/assets/screenshot-400w.a3f9c2e1.webp 400w, /assets/screenshot-800w.a3f9c2e1.webp 800w, ...">
	<img src="/assets/screenshot.png" srcset="/assets/screenshot-400w.a3f9c2e1.png 400w, /assets/screenshot-800w.a3f9c2e1.png 800w, ..." sizes="(min-width: 800px) 800px, 100vw" alt="Un sito appena compilato">
</picture>
```

Un browser sceglie la variante più piccola che soddisfa `sizes`, in WebP
quando supporta il formato, ricadendo altrimenti sull'originale semplice
`src` (servito esattamente come prima). Ogni altro attributo che hai
scritto - `alt`, `class`, qualsiasi altra cosa - viene riportato intatto
sull'`<img>` riscritto.

Un'immagine senza alcuna larghezza configurata più stretta della propria
(una piccola icona, per esempio) ottiene comunque una ricodifica WebP a
piena dimensione quando `"webp"` è presente in `assets.images.formats` -
un vero risparmio di peso anche senza alcun breakpoint responsivo da
offrire.

## Didascalie, allineamento e cornici

Una didascalia, una cornice, o una galleria multi-immagine sono tutte
semplicemente HTML a livello di blocco - che bx-markdown/Flexmark lascia
passare completamente intatto (la regola "HTML block" propria di
CommonMark), quindi non serve alcuna sintassi specifica di bx-sites:

```markdown title="Esempio" linenums="1"
<figure>
  <img src="../assets/screenshot.png" alt="The build output">
  <figcaption>A freshly built site</figcaption>
</figure>

<div data-with-frame="true">
  <img src="../assets/screenshot.png" alt="Framed">
</div>

<div class="bxsites-gallery">
  <img src="../assets/one.png" alt="">
  <img src="../assets/two.png" alt="">
  <img src="../assets/three.png" alt="">
</div>
```

Lo stesso vale per `x-data`/`x-show`/`@click` e qualsiasi altro attributo
Alpine.js - vedi [Interattività con Alpine.js](interactivity.md).

## Cosa non viene ridimensionato

- **Gli SVG** - già indipendenti dalla risoluzione, copiati senza
  modifiche.
- **Le GIF animate** - il percorso di ridimensionamento proprio di
  bx-image non è consapevole dei frame; ridimensionarne una la
  appiattirebbe a un singolo frame. Copiate senza modifiche, esattamente
  come prima che questa funzionalità esistesse.
- **Qualsiasi cosa fuori da `docs/assets/`** - un URL di immagine remota
  (`<img src="https://...">`) viene lasciato completamente intatto, allo
  stesso modo in cui [`extraCss`/`extraJs`](../configuration.md#extracss--extrajs)
  trattano già un URL assoluto come "usato così com'è."
- **Un'immagine già più stretta di ogni larghezza configurata** - niente
  da generare; il semplice `<img>` viene renderizzato esattamente come
  prima, a meno che `"webp"` non sia attivato (vedi sopra).

Non c'è ancora supporto AVIF - bx-image non scrive quel formato al momento
in cui scriviamo. Il solo WebP ottiene comunque la maggior parte del
risparmio di peso, con un supporto di strumenti/browser molto più ampio -
vale la pena rivederlo se bx-image aggiunge AVIF a monte.

## Disattivarlo

=== "YAML"
    ```yaml title="bxsites.yaml"
    assets: { images: { enabled: false } }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "assets": { "images": { "enabled": false } } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [assets]

    [assets.images]
    enabled = false
    ```

Ricade sulla semplice copia non elaborata di `docs/assets/**` - esattamente
come veniva gestita ogni immagine prima che questa funzionalità esistesse.

## Scegliere i propri breakpoint

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      images:
        widths: [ 480, 960, 1440 ]
        formats: [ webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"images": {
    			"widths": [480, 960, 1440],
    			"formats": ["webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [assets]

    [assets.images]
    widths = [ 480, 960, 1440 ]
    formats = [ "webp" ]
    ```

`widths` ha come predefinito `[400, 800, 1200, 1600]`; `formats` ha come
predefinito `["original", "webp"]` - togli `"original"` per saltare del
tutto la generazione di copie ridimensionate nel formato sorgente
(mantenendo comunque l'originale semplice a piena dimensione come ripiego
dell'`<img>`), oppure togli `"webp"` per saltare del tutto il `<source>`
WebP. Vedi [Configurazione](../configuration.md#assets) per ogni chiave
`assets.images`.

## CSS/JS bundling

`extraCss`/`extraJs` vengono raggruppati allo stesso modo, attivo di
default (`assets.bundle`):

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/a.css, assets/b.css ]
    extraJs: [ assets/app.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/a.css", "assets/b.css"],
    	"extraJs": ["assets/app.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/a.css", "assets/b.css" ]
    extraJs = [ "assets/app.js" ]
    ```

compila un unico `assets/bundle.<hash>.css` con impronta digitale
(nell'ordine elencato) e un unico `assets/bundle.<hash>.js`, invece di un
tag `<link>`/`<script>` per ogni voce. Il CSS ha i propri commenti
rimossi e gli spazi bianchi compattati; il JS riceve deliberatamente solo
una pulizia sicura e strutturale degli spazi bianchi - mai la rimozione
dei commenti, dato che un'espressione regolare ingenua non ha modo di
distinguere un `//` dentro una stringa (`"http://example.com"`) da un
vero commento, e sbagliarsi corromperebbe silenziosamente lo script
stesso di un progetto. Questo è bundling e pulizia leggera, non un vero
minificatore - una libreria Java di minificazione inclusa è un
ragionevole aggiornamento futuro se questo non bastasse.

Il bundling si attiva solo quando *ogni* voce nell'elenco è un file di
progetto locale. Un singolo URL esterno (un link CDN) mescolato fa
ricadere l'intero elenco sull'esatto comportamento odierno per-URL,
invece di rischiare di riordinare silenziosamente una cascata CSS da cui
un progetto dipendeva:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/custom.css, "https://cdn.example.com/lib.css" ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/custom.css", "https://cdn.example.com/lib.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/custom.css", "https://cdn.example.com/lib.css" ]
    ```

renderizza due tag `<link>` separati, non raggruppati, esattamente come
prima che questa funzionalità esistesse.

## Impronta digitale e cache

Ogni variante di immagine generata e ogni bundle CSS/JS ha un nome basato
sull'hash del contenuto (`assets.fingerprint`, attivo di default) - un
build cambia il nome file di una variante solo quando il suo contenuto
sorgente cambia davvero, ed è proprio questo che rende sicuro impostare
un header `Cache-Control` a lunghissima scadenza su un host statico. I
file originali di un progetto sotto `docs/assets/` mantengono comunque i
propri nomi semplici invariati - solo l'output generato dalla pipeline
ottiene l'impronta digitale, quindi una card di download `::: file` o un
link grezzo a un'immagine con il proprio nome file continua a funzionare
esattamente come sempre.

Ogni variante generata viene messa in cache su disco sotto la propria
`.cache/images/` del progetto (rimossa da
[`bxSites clean`](../cli-reference.md#clean), insieme a `site/`) -
indicizzata in base all'hash del contenuto dell'immagine *sorgente*,
quindi rieseguire `build` (una volta per ogni albero versione/locale,
tutti condividendo la stessa `docs/assets/`) o `bxSites serve` dopo una
modifica non correlata non ridecodifica/ridimensiona/ricodifica ogni
screenshot del progetto, solo quelli effettivamente cambiati.
