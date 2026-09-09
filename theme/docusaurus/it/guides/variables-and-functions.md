---
title: Variabili e Funzioni Magiche
order: 12
icon: phosphor-duotone:magic-wand
tags: [guide, variabili, funzioni]
---

# Variabili e Funzioni Magiche

Due funzionalità piccole e correlate per tenere fuori dal tuo Markdown i
fatti ripetuti e la logica ripetuta: le **variabili riutilizzabili**,
definite una sola volta in `bxsites.yaml` e inserite in qualsiasi pagina
con `{{ }}`, e le **funzioni magiche**, piccoli helper BoxLang che scrivi
una sola volta in `docs/functions.bxs` e richiami allo stesso modo -
ovunque, senza import, senza plugin, senza alcun collegamento da
configurare.

Entrambe condividono un'unica sintassi:

```text
{{ dotted.path }}          # a reusable variable
{{ $name(arg1, arg2) }}    # a magic function call
```

## Variabili riutilizzabili

Aggiungi un blocco `variables` a `bxsites.yaml` - con qualsiasi forma tu
preferisca, piatta o annidata:

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product:
        name: "BoxLang"
        supportEmail: "support@example.com"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": {
    			"name": "BoxLang",
    			"supportEmail": "support@example.com"
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [variables]
    company = "Ortus Solutions"

    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

Poi fai riferimento a uno qualsiasi dei suoi valori, per percorso puntato,
da qualsiasi pagina Markdown:

```markdown title="docs/index.md"
# Welcome to {{ company }}

We build {{ product.name }} tools. Need help? Write us at
{{ product.supportEmail }}.
```

viene compilata in:

```html
<h1>Welcome to Ortus Solutions</h1>
<p>We build BoxLang tools. Need help? Write us at support@example.com.</p>
```

Una variabile `{{ }}` viene risolta una sola volta, al momento del build,
rispetto a ciò che il blocco `variables` proprio di `bxsites.yaml`
contiene in quel preciso istante - rinomina un prodotto, aggiorna un
indirizzo di supporto o cambia un anno in un unico punto, e ogni pagina
che la usa recepisce la modifica al build successivo. Vedi
[`variables`](../configuration.md#variables) nel riferimento di
configurazione.

## Funzioni magiche

Aggiungi un file `docs/functions.bxs` (o `src/functions.bxs`, se il tuo
progetto usa `src/` - vedi [Per iniziare](../getting-started.md)) - un
semplice script BoxLang. Ogni funzione che nomini con un `$` iniziale
diventa una *funzione magica*: richiamabile da `{{ }}` nel Markdown, e
richiamabile allo stato puro, direttamente, dalle sovrascritture `.bxm`
del proprio [`theme/`](themes.md#sovrascrivere-un-tema) di un progetto.

```bx title="docs/functions.bxs" linenums="1"
function $shout( text ) {
	return uCase( arguments.text ) & "!"
}

function $badge( label, kind = "info" ) {
	return '<span class="badge bg-' & arguments.kind & '">' & arguments.label & '</span>'
}
```

```markdown title="docs/index.md"
{{ $shout('this is important') }}

Status: {{ $badge('Stable', 'success') }}
```

viene compilata in:

```html
<p>THIS IS IMPORTANT!</p>
<p>Status: <span class="badge bg-success">Stable</span></p>
```

Una funzione magica può restituire qualsiasi cosa convertibile con
`toString()` - testo semplice, HTML, un numero - e viene inserita
direttamente nel Markdown della pagina prima che questo venga convertito,
quindi restituire vero HTML (come `$badge()` sopra) funziona esattamente
come ci si aspetterebbe.

Una funzione dichiarata *senza* un `$` iniziale nello stesso
`functions.bxs` è solo un helper privato, pensato per essere richiamato
unicamente dalle tue altre funzioni con prefisso `$` nello stesso file
(vengono tutte caricate nello stesso scope, quindi una può richiamare
l'altra allo stato puro) - `{{ }}` non può mai richiamarne una
direttamente (viene riconosciuto solo un bersaglio di chiamata
`$name(...)`), e non fa nemmeno parte della superficie pubblica
documentata che una sovrascrittura di tema dovrebbe richiamare, anche se
tecnicamente risulta raggiungibile anche lì:

```bx title="docs/functions.bxs"
private string function formatPrice( amount ) {
	return "$" & numberFormat( arguments.amount, "9.99" )
}

function $price( amount ) {
	return formatPrice( arguments.amount )
}
```

### Richiamare una funzione magica da una sovrascrittura di tema

Poiché una funzione magica viene collegata direttamente nello scope del
template, il proprio `theme/page.bxm` (o `layout.bxm`) di un progetto può
richiamarla allo stato puro, senza alcun prefisso - esattamente come già
legge `variables.page`/`variables.siteConfig`:

```bx title="theme/page.bxm (excerpt)"
<p class="build-banner">#$shout( 'built with boxlang' )#</p>
```

### Variabili di contesto

Il corpo di ogni funzione magica può anche leggere un insieme fisso di
"variabili di supporto" - allo stato puro, senza bisogno di alcun
argomento - a prescindere dal fatto che venga richiamata da `{{ }}` nel
Markdown oppure allo stato puro da una sovrascrittura di tema:

| Variabile | Cos'è |
|---|---|
| `siteConfig` | La configurazione `bxsites.yaml` propria del sito (già impostata sui valori predefiniti/validata) |
| `page` | La pagina corrente (vedi la nota sotto - non tutti i campi sono già popolati quando viene richiamata dal Markdown) |
| `nav` | L'albero di navigazione proprio di questo albero |
| `basePath` | Il percorso base relativo alla radice, che termina con `/` |
| `versions` | Le voci del selettore di versione - `[ { label, url } ]` |
| `currentVersion` | Quale voce di `versions` viene renderizzata in questo momento |
| `locales` | Le voci del selettore di lingua - `[ { code, label, url, dir, flag } ]` |
| `currentLocale` | Il codice di quale voce di `locales` viene renderizzato in questo momento |
| `currentLocaleDir` | `"ltr"`/`"rtl"` per il locale corrente |
| `data` | I [file di dati](data-files.md) propri di questo progetto - `docs/data/*.yaml`/`.json`, una chiave per ogni file - `{}` quando il progetto non ne ha |

```bx title="docs/functions.bxs"
function $sitename() {
	return siteConfig.name
}

function $pagetitle() {
	return page.title
}
```

```markdown title="docs/index.md"
Site: {{ $sitename() }}
Page: {{ $pagetitle() }}
```

**`page` non è ugualmente completa in entrambi i casi.** Se richiamata dal
Markdown, `page` è lo struct proprio di questa specifica pagina *così come
caricato da disco* - `title`/`description`/`tags`/`icon`/`summary`/
`ogImage`/`urlPath`/`relativePath`/`body`/ecc. sono già presenti, ma i
campi noti solo dopo che ogni pagina dell'albero ha terminato la
conversione - `toc`, `prevPage`/`nextPage`, `breadcrumbs`,
`editUrl`/`lastUpdated`, `iconHtml`, `markdownUrl`, `canonicalUrl`,
`course` - non esistono ancora su di essa. Se richiamata allo stato puro
da `page.bxm`, `page` è invece lo struct completamente arricchito, con
tutti questi campi inclusi.

`page.course` è sempre presente (uno struct completamente vuoto
`{id:"", courseTitle:"", lessonIndex:0, lessonTotal:0,
prevLesson:{title:"",url:""}, nextLesson:{title:"",url:""}, indexUrl:""}`
per una pagina che non fa parte di alcun [corso](courses.md)) -
`prevLesson`/`nextLesson` sono limitati a quel singolo corso, in modo
indipendente dai `prevPage`/`nextPage` a livello di sito visti sopra, che
invece percorrono sempre l'intero albero di navigazione. Ogni altra
variabile di supporto (`siteConfig`, `nav`, `basePath`, `versions`,
`currentVersion`, `locales`, `currentLocale`, `currentLocaleDir`) è
identica in entrambi i casi.

### Sintassi degli argomenti

Gli argomenti di una chiamata a funzione magica sono semplici letterali o
riferimenti a variabili separati da virgola - nessuna chiamata a funzione
annidata né espressione in questa prima versione:

- Numeri: `{{ $discount(20) }}`
- Stringhe tra virgolette: `{{ $greet('World') }}` oppure
  `{{ $greet("World") }}`
- Booleani: `{{ $badge('Beta', true) }}`
- Un riferimento a variabile puntato senza `{{ }}`:
  `{{ $greet(product.name) }}`

## Ricette per i visualizzatori

Una funzione magica che restituisce HTML non si limita a un badge di
stato - è un modo generico per ottenere celle visive (una valutazione a
stelle, un chip colorato, una barra di progresso) senza bisogno di alcun
selettore di colonne basato su database - il codice sorgente di
bx-sites, basato su git e Markdown puro, non ne ha un equivalente. Le
quattro funzioni qui sotto sono lo stesso
[`docs/functions.bxs`](https://github.com/ortus-boxlang/bx-sites/blob/development/docs/functions.bxs)
di questo sito, renderizzate dal vivo proprio in questa pagina.

### Valutazioni

```bx title="docs/functions.bxs"
function $stars( required numeric rating, numeric max = 5 ) {
	var filled = min( max( round( arguments.rating ), 0 ), arguments.max )
	var stars = repeatString( "★", filled ) & repeatString( "☆", arguments.max - filled )
	return '<span title="' & arguments.rating & ' out of ' & arguments.max & '" style="color:##f5a623;letter-spacing:2px">' & stars & '</span>'
}
```

`` `{{ $stars(4) }}` `` viene renderizzato come: {{ $stars(4) }}

### Chip di stato

```bx title="docs/functions.bxs"
function $badge( required string label, string kind = "info" ) {
	var palette = {
		"info"    : { "bg" : "##e0edff", "fg" : "##1d4ed8" },
		"success" : { "bg" : "##dcfce7", "fg" : "##15803d" },
		"danger"  : { "bg" : "##fee2e2", "fg" : "##b91c1c" },
		"warning" : { "bg" : "##fef9c3", "fg" : "##854d0e" }
	}
	var pick = palette.keyExists( arguments.kind ) ? palette[ arguments.kind ] : { "bg" : "##f1f5f9", "fg" : "##475569" }
	return '<span style="display:inline-block;padding:0.1em 0.6em;border-radius:999px;font-size:0.85em;font-weight:600;background:'
		& pick.bg & ";color:" & pick.fg & '">' & encodeForHTML( arguments.label ) & "</span>"
}
```

`` `{{ $badge('Stable', 'success') }}` `` viene renderizzato come: {{ $badge('Stable', 'success') }} - e `` `{{ $badge('Beta', 'info') }}` ``: {{ $badge('Beta', 'info') }}

### Barre di progresso

```bx title="docs/functions.bxs"
function $progress( required numeric percent ) {
	var pct = min( max( arguments.percent, 0 ), 100 )
	return '<span style="display:inline-block;width:120px;height:8px;background:##e5e7eb;border-radius:999px;overflow:hidden;vertical-align:middle"><span style="display:block;height:100%;width:'
		& pct & '%;background:##2563eb"></span></span> ' & pct & "%"
}
```

`` `{{ $progress(72) }}` `` viene renderizzato come: {{ $progress(72) }}

### Indicatori di tendenza

```bx title="docs/functions.bxs"
function $trend( required numeric value ) {
	var isUp = arguments.value >= 0
	var arrow = isUp ? "▲" : "▼"
	var color = isUp ? "##16a34a" : "##dc2626"
	var sign = isUp ? "+" : ""
	return '<span style="color:' & color & ';font-weight:600">' & arrow & " " & sign & numberFormat( arguments.value, "0.0" ) & "%</span>"
}
```

`` `{{ $trend(4.2) }}` `` viene renderizzato come: {{ $trend(4.2) }} - `` `{{ $trend(-1.8) }}` ``: {{ $trend(-1.8) }}

### Dentro una cella di tabella

`{{ }}` si risolve rispetto al Markdown grezzo prima ancora che le
[tabelle](tables.md) vengano analizzate, quindi qualsiasi
elemento tra quelli sopra funziona anche dentro le celle di una tabella a
pipe, esattamente come in qualsiasi altro punto della pagina:

```markdown title="Example" linenums="1"
| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |
```

Che viene renderizzato così:

| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |

## Mostrare la sintassi in modo letterale

Un `{{ }}` mostrato dentro un blocco di codice delimitato (tre o più
backtick, come ogni esempio in questa pagina) viene lasciato del tutto
intatto invece di essere risolto - la stessa convenzione che questo
modulo usa già per la matematica `$...$` e per le schede di contenuto
`=== "Tab"`. A differenza di queste due, un `{{ }}` mostrato in codice
*inline* (`` `{{ example }}` ``, con singolo o doppio backtick) è
protetto anch'esso - ogni punto elenco più sopra che mostra
`` `{{ $discount(20) }}` `` inline è un esempio reale e funzionante di
questo.

Un `{{ }}` il cui contenuto non assomiglia né a un percorso di variabile
né a una chiamata `$name(...)` - la sintassi `{{ }}` propria di un altro
motore di template, mostrata in un testo discorsivo, ad esempio - viene
lasciato intatto invece di essere trattato come un errore. Solo un token
che *sembra* una variabile o una chiamata a funzione magica, ma che non
si risolve, fa fallire il build (vedi [Errori](#errori) sotto) - questo è
deliberato, per intercettare un vero errore di battitura senza
interpretare erroneamente un testo `{{ }}` non correlato come sintassi
rotta.

## Ambito

- `functions.bxs` è a livello di progetto - un solo file, caricato una
  volta, con lo stesso insieme di funzioni magiche disponibile su ogni
  pagina, sia nell'albero principale sia in ogni albero di
  [versione](versioning.md)/[locale](i18n.md). Non è necessario
  duplicarlo in `docs/versions/<name>/` o `docs/i18n/<code>/`.
- `variables` è allo stesso modo un unico blocco di `bxsites.yaml` a
  livello di progetto - non è di per sé traducibile per locale. Un
  progetto multilingue che desideri un testo di variabile diverso per
  ogni lingua può invece ricorrere a una funzione magica che smisti in
  base a `siteConfig.i18n.defaultLocale.code` (oppure, semplicemente,
  mantenere il valore neutro rispetto alla lingua - un nome di prodotto,
  un indirizzo di supporto).

## Nomi riservati

Una sovrascrittura `theme/page.bxm`/`layout.bxm` che richiama una
funzione magica allo stato puro (`$name(...)`) funziona perché ogni
funzione caricata - con prefisso `$` o helper privato indifferentemente -
viene collegata direttamente nello stesso scope di rendering di quel
template, proprio accanto ai `variables.page`/`variables.siteConfig`/ecc.
integrati che ogni tema già legge. Questo significa che una funzione di
`functions.bxs` che condivide un nome con uno di questi ne ha già uno:
evita `page`, `nav`, `siteConfig`, `themeDir`, `basePath`,
`moduleAssetsDir`, `versions`, `currentVersion`, `locales`,
`currentLocale`, `currentLocaleDir`, `strings`, `requiredFiles`,
`stringsResolver` e `data` come nome proprio di un helper privato (una
funzione magica con prefisso `$` non può mai entrare in collisione con
nessuno di questi, dato che nessuno di essi inizia con `$`). Vedi
[File di dati: Ambito](data-files.md#scope) per la nota sui nomi
riservati propria di `data`.

## Errori

- `BxSites.UnknownVariable` - un `{{ dotted.path }}` (o un argomento di
  `$name(...)` che assomiglia a un riferimento a variabile) non
  corrisponde a nulla nel blocco `variables` di `bxsites.yaml`.
- `BxSites.UnknownFunction` - una chiamata `{{ $name(...) }}` non
  corrisponde a nessuna funzione con prefisso `$` in
  `docs/functions.bxs`.
- `BxSites.InvalidFunctions` - `docs/functions.bxs` non è riuscito a
  caricarsi (un errore di sintassi BoxLang nel file stesso).
- `BxSites.InvalidConfig` - la chiave `variables` di `bxsites.yaml` è
  presente, ma non è un oggetto.
