---
title: Tabelle
order: 4.6
icon: phosphor-duotone:table
tags: [guides, markdown]
---

# Tabelle

Tabelle a pipe [GFM](https://github.github.com/gfm/#tables-extension-)
standard, in aggiunta a tutto quanto in [Estensioni
Markdown](markdown.md) - nessuna configurazione di `bxsites.yaml`
necessaria, sempre attive:

```markdown title="Example" linenums="1"
| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |
```

Che viene renderizzato così:

| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |

Una riga di `---` sotto l'intestazione attiva la tabella; metti i due
punti su quella riga separatrice per controllare l'allineamento per
colonna - `:---` sinistra, `:---:` centro, `---:` destra (nessun due
punti fa ricadere ogni colonna sull'allineamento a sinistra).

## Il contenuto delle celle è normale markdown inline

`code`, **grassetto**, *corsivo*, e [link](../index.md) funzionano tutti
dentro una cella esattamente come in qualsiasi altro punto della pagina:

```markdown title="Example" linenums="1"
| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |
```

Che viene renderizzato così:

| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |

## Escape di una pipe dentro una cella

Un `|` letterale nel testo semplice di una cella richiede un backslash,
`\|` - uno non sfuggito viene invece letto come il separatore della
colonna successiva:

```markdown title="Example" linenums="1"
| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |
```

Che viene renderizzato così:

| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |

Un `|` dentro il codice inline non ha affatto bisogno di essere sfuggito -
lo span di codice (`` `a | b` ``) lo protegge già:

| Expression | Meaning |
| --- | --- |
| `a | b` | bitwise OR |

## Righe corte e lunghe

Una riga di dati non deve corrispondere esattamente al numero di colonne
dell'intestazione - una riga corta viene riempita con celle vuote, e una
riga lunga ha le proprie celle in eccesso scartate silenziosamente,
entrambi i comportamenti controllati da
`tableOptions.appendMissingColumns`/`discardExtraColumns` qui sotto:

```markdown title="Example" linenums="1"
| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |
```

Che viene renderizzato così:

| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |

## Configurare il parsing

La gestione delle righe corte/lunghe, il rigore della riga separatrice
`---` stessa, e la classe CSS con cui viene renderizzata ogni `<table>`
sono tutti controllati dalla chiave
[`markdown.tableOptions`](../configuration.md#markdown) di
`bxsites.yaml`; i valori predefiniti mostrati in questa pagina sono
quasi sempre quello che vuoi.

## Scorrimento responsive e intestazione fissa

Ogni tabella renderizzata viene automaticamente racchiusa in un div
`.bxsites-table-wrap` - nessuna configurazione di `bxsites.yaml`, nessun
markdown aggiuntivo. Dà a una tabella larga una propria barra di
scorrimento orizzontale invece di farla traboccare oltre la pagina, e
limita le tabelle alte (oltre una `max-height`) a un'altezza fissa con una
propria barra di scorrimento verticale, con la riga di intestazione
fissata mentre il corpo scorre sotto di essa - una tabella corta come
quelle qui sopra non sviluppa mai una barra di scorrimento, dato che ci
sta già. Una sovrascrittura personalizzata in `theme/` può ridefinire lo
stile di `.bxsites-table-wrap` (in particolare la sua `max-height`), come
qualsiasi altra classe CSS.

## Temi

Ogni tema integrato rende una tabella come una card a sé stante - un
wrapper arrotondato e bordato, una fascia di intestazione colorata,
separatori orizzontali di riga senza linee verticali di griglia e una
tinta zebra/hover sulle righe - e disegna tutto questo a partire da sei
proprietà personalizzate CSS, dichiarate per modalità (`:root` e
`[data-theme="dark"]`) nel `assets/style.css` del tema stesso:

| Token | Cosa disegna |
| --- | --- |
| `--bxsites-table-bg` | La superficie della card, dietro ogni riga |
| `--bxsites-table-head-bg` | La riga di intestazione e il campo filtro sopra di essa |
| `--bxsites-table-head-text` | Il testo delle etichette di intestazione |
| `--bxsites-table-border` | Il contorno della card e i separatori di riga |
| `--bxsites-table-stripe-bg` | Le righe pari - una tinta alfa sopra la superficie della card |
| `--bxsites-table-hover-bg` | La riga sotto il cursore - la stessa, un po' più marcata |

Poiché ognuna è dichiarata in entrambe le modalità, una tabella segue
l'interruttore del tema come il resto della pagina, invece di lasciare
una lastra bianca in modalità chiara su una pagina scura. Puoi
ridefinirne una qualsiasi da
[`extraCss`](themes.md#personalizzare-i-colori-senza-sovrascrivere-un-tema) -
senza alcun override del tema:

```css title="docs/assets/brand.css" linenums="1"
[data-theme="dark"] {
	--bxsites-table-head-bg: #241b2e;
	--bxsites-table-hover-bg: rgba(167, 139, 250, 0.12);
}
```

Mantieni `--bxsites-table-stripe-bg`/`-hover-bg` come colori alfa: sono
disegnati sopra ciò che imposta `--bxsites-table-bg`, quindi un valore
opaco copre la superficie della card invece di tingerla. Tutto ciò che va
oltre il colore - il padding, il raggio degli angoli, le etichette
maiuscole dell'intestazione - è un vero override `theme/`, esattamente
come qualsiasi altro CSS in un tema.

## Oltre i dati semplici

Altre due ricette si costruiscono direttamente sopra una tabella
semplice come quelle sopra:

- Serve un chip di stato o una valutazione a stelle in una cella? Vedi [Ricette
  per i visualizzatori](variables-and-functions.md#ricette-per-i-visualizzatori).
- Serve che un lettore possa davvero ordinare o filtrare una tabella lato
  client, invece di limitarsi a leggerla? Vedi [Una tabella ordinabile e
  filtrabile](interactivity.md#una-tabella-ordinabile-e-filtrabile).
