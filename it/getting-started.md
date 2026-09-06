---
title: Per iniziare
order: 2
icon: phosphor-duotone:rocket-launch
summary: Installa il modulo, genera lo scheletro di un progetto e compila il tuo primo sito.
tags: [guide, configurazione-iniziale]
---

# Per iniziare

## Prerequisito: installare BoxLang

Tutto quanto segue presuppone che il runtime di BoxLang sia già presente
sulla tua macchina - `install-bx-module` è il comando CLI nativo di
BoxLang che installa un modulo in un'installazione già esistente, e anche
`box install` di CommandBox richiede un motore BoxLang già presente su cui
girare. Se non hai ancora BoxLang, installalo prima con uno di questi due
metodi:

- il **quick installer** (una singola versione, il modo più semplice per
  iniziare):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install.boxlang.io/ | bash
  ```

- oppure **BVM**, il BoxLang Version Manager (installa più versioni di
  BoxLang fianco a fianco e permette di passare dall'una all'altra):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install-bvm.boxlang.io/ | bash
  bvm install latest && bvm use latest
  ```

Gli installer per Windows e Homebrew, oltre al riferimento completo dei
comandi BVM, sono trattati nella
[documentazione di installazione ufficiale di BoxLang](https://boxlang.ortusbooks.com/getting-started/installation).

## Installazione

BxSites dipende da [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
per il rendering del Markdown, da [bx-esapi](https://github.com/ortus-boxlang/bx-esapi)
per la codifica HTML, da [bx-yaml](https://github.com/ortus-boxlang/bx-yaml)
per leggere `bxsites.yaml`, e da [bx-image](https://github.com/ortus-boxlang/bx-image)
per la pipeline di immagini responsive (vedi
[Immagini responsive](guides/images.md)) - tutte e quattro vengono
installate automaticamente come dipendenze di `box.json`, quindi
installare `bx-sites` è l'unico comando necessario, sia tramite
l'installer nativo del sistema operativo di BoxLang:

```bash frame="terminal" title="Terminal"
install-bx-module bx-sites
```

sia tramite [CommandBox](https://commandbox.ortusbooks.com/):

```bash frame="terminal" title="Terminal"
box install bx-sites
```

Entrambi leggono `boxlang.executable` da `box.json` e creano uno script
`bxSites` sul tuo `PATH` (in `~/.boxlang/bin`), così ogni comando qui
sotto funziona sia nella forma breve e autonoma:

```bash title="Usage"
bxSites <verbo> [opzioni]
```

sia, ovunque BoxLang sia disponibile ma quello shim sul `PATH` non lo sia
(un runner di CI, un modulo registrato a mano invece che installato) -
entrambe le forme eseguono esattamente la stessa cosa:

```bash title="Usage (no PATH shim)"
boxlang bxSites <verbo> [opzioni]
```

Il resto di questa guida usa la forma breve.

## Generare lo scheletro di un progetto

```bash
bxSites new my-docs
cd my-docs
```

Questo crea:

```
my-docs/
├── docs/
│   ├── assets/
│   └── index.md
└── bxsites.yaml
```

Passa `--theme=material` o `--theme=tailwind` per generare lo scheletro con
un tema predefinito diverso, e `--name="My Project Docs"` per impostare
subito il nome del sito - altrimenti `new` lo ricava dal nome della
cartella di destinazione.

### Formato del file di configurazione

`bxsites.yaml` è il formato predefinito e preferito - è quello che `new`
genera a meno che non venga detto diversamente, ed è quello mostrato per
primo in ogni esempio di questa guida e di [Configurazione](configuration.md).
`bxsites.json` è anch'esso pienamente supportato, per un progetto che lo
preferisce: passa `--format=json` per generarne uno al suo posto, oppure
scrivilo/rinominalo a mano tu stesso - ConfigLoader risolve qualunque tra
`bxsites.yaml`/`.yml`/`.json` sia effettivamente presente, in quest'ordine,
senza bisogno di altra configurazione per passare dall'uno all'altro. Vedi
[Configurazione](configuration.md) per il riferimento completo delle
chiavi in entrambi i formati.

Hai già dei contenuti su GitBook? `bxSites migrate --source=/percorso/dell/export`
converte un export di GitBook direttamente in `docs/` - vedi
[Migrare da GitBook](guides/migrating-from-gitbook.md) - e puoi passare
direttamente a [Compilazione](#build).

## Aggiungere pagine

Ogni file `.md` sotto `docs/` diventa una pagina. L'annidamento delle
cartelle diventa automaticamente annidamento nella navigazione:

!!! note "docs/ o src/"
    `docs/` è ciò che `new` genera e ciò che usa ogni esempio in questa
    guida, ma un progetto che nello spirito non è davvero "docs" (un sito
    marketing, un portfolio) può usare `src/` al suo posto, senza nessun
    altro cambiamento: ogni verbo (`build`, `serve`, `audit`, `lint`,
    `page:new`, ...) cerca prima `docs/` e ripiega su `src/` quando è
    quello che esiste davvero. L'output della build finisce comunque
    sempre in `site/` - i due non entrano mai in conflitto, dato che
    `site/` stesso non è mai un nome valido per la cartella sorgente.

```
docs/
├── index.md              -> /
├── guides/
│   ├── index.md          -> /guides/
│   └── deployment.md     -> /guides/deployment/
```

(Un sito di grandi dimensioni può sovrascrivere del tutto questo ordine/
raggruppamento dedotto automaticamente con una nav esplicita - vedi
[`nav`](configuration.md#nav).)

### Collegare le pagine tra loro

Collegati a un'altra pagina nel modo classico di mkdocs - un percorso
relativo al file verso il suo sorgente `.md`, esattamente come se i due
file si trovassero uno accanto all'altro sul disco (perché è proprio
così):

```markdown
Vedi [Distribuzione](guides/deployment.md) oppure, da quella stessa
guida, [torna a Per iniziare](../getting-started.md#add-pages).
```

BxSites riscrive ogni link di questo tipo nel suo URL "pulito" già
compilato al momento del `build` (`guides/deployment.md` ->
`/guides/deployment/index.html`, con ancore e query string preservate),
risolto rispetto alla cartella della pagina *che contiene il link* - `../`
e i riferimenti tra file fratelli funzionano esattamente come qualsiasi
altro percorso relativo. Questo è anche il motivo per cui il link continua
a funzionare se leggi il file direttamente su GitHub invece che sul sito
compilato: è comunque un percorso relativo reale e valido verso un file
reale. Gli URL assoluti, i `mailto:` e i link che iniziano già con `/`
vengono lasciati intatti.

### Scaricare una pagina come Markdown

Ogni pagina compilata ottiene anche la pubblicazione del proprio sorgente
`.md` originale accanto a sé - `docs/guides/deployment.md` finisce
copiato in `site/guides/deployment.md`, proprio accanto a
`site/guides/deployment/index.html` - con un link "Scarica Markdown"
sulla pagina stessa, vicino a "Modifica questa pagina". Nessuna
configurazione necessaria, sempre attivo.

Questa è la stessa motivazione di [`llms.txt`](../configuration.md#llmstxt) -
una persona (o un LLM) può recuperare il Markdown grezzo di una pagina
direttamente, invece di estrarlo dall'HTML già renderizzato - e poiché
l'intero albero `docs/` viene rispecchiato 1:1, anche i link relativi di
una pagina continuano a funzionare leggendola in questo modo.

Ogni pagina può iniziare con un piccolo blocco di frontmatter:

```markdown
---
title: Deployment
order: 2
hidden: false
description: How to deploy a built BxSites site.
tags: [guides, deployment]
icon: 🚀
summary: Everything you need to publish a built site.
ogImage: assets/deployment-card.png
---

# Deployment

Your content here.
```

- `title` - sovrascrive il titolo della pagina/nav (altrimenti ricavato dal nome del file)
- `order` - controlla l'ordinamento tra pagine sorelle nella nav (i valori più bassi vengono prima; le pagine senza `order` vengono ordinate per ultime, in ordine alfabetico)
- `hidden` - `true` esclude la pagina dalla nav (e dalla ricerca) senza escluderla dalla compilazione
- `description` - la descrizione meta/social-card di questa pagina (vedi
  [`ogImage`](configuration.md#ogimage)); se omessa, ricade sulla
  `description` a livello di sito nella configurazione del sito
- `tags` - un array di tag per questa pagina, mostrati come badge
  cliccabili sotto il titolo e raccolti in una pagina indice `/tags/` a
  livello di sito (compilata solo una volta che almeno una pagina ha dei
  tag); aumenta anche la rilevanza nei risultati di ricerca per le query
  corrispondenti
- `icon` - mostrata accanto al titolo della pagina e alla sua voce nella
  nav - un'emoji semplice, oppure un'icona con nome da una libreria
  integrata (`rocket`, `lucide:rocket`, `tabler:rocket`, o un
  `custom:my-icon` proprio del progetto) - vedi
  [Icone](guides/icons.md)
- `summary` - una riga di presentazione mostrata sotto il titolo (distinta
  da `description`, che è solo per i meta tag e non viene mai mostrata
  sulla pagina stessa)
- `ogImage` - sovrascrive l'immagine social-card di questa singola pagina -
  vedi [`ogImage`](configuration.md#ogimage)

I valori del frontmatter possono essere liste inline (`tags: [a, b, c]`),
liste in stile YAML a blocchi (`tags:` seguito da righe `- elemento`
indentate), oppure scalari a blocco `>`/`|` per un valore multi-riga - è
comunque un piccolo parser scritto a mano, non YAML completo, quindi
oggetti/mappe annidati non sono supportati.

## Compilazione

```bash
bxSites build
```

Genera ogni pagina di `docs/` in un sito statico dentro `site/`, pronto
per essere ospitato ovunque si servano file statici.

## Servire in locale

```bash
bxSites serve
```

Compila il progetto, serve `site/` su `http://127.0.0.1:8080/`, e
ricompila automaticamente ogni volta che salvi una modifica sotto
`docs/`, la configurazione del sito `bxsites.yaml`/`.json`, o una
personalizzazione di `theme/` a livello di progetto - il browser si
ricarica da solo. Passa `--port=3000` o `--host=0.0.0.0` per cambiare come
si aggancia.

## Pulizia

```bash
bxSites clean
```

Rimuove `site/` e qualsiasi cache di compilazione, senza toccare il tuo
sorgente `docs/`.
