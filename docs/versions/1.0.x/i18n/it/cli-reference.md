---
title: Riferimento CLI
order: 3
icon: phosphor-duotone:terminal-window
summary: Ogni verbo di bxSites e i suoi flag.
tags: [riferimento, cli]
---

# Riferimento CLI

```bash title="Utilizzo"
bxSites <verbo> [opzioni]
```

`box install bx-sites` crea uno script `bxSites` autonomo sul tuo `PATH`
(tramite `boxlang.executable` di `box.json`), quindi ogni verbo qui sotto
può essere eseguito sia in quella forma breve, sia come
`boxlang bxSites <verbo>` - entrambe eseguono esattamente la stessa
cosa; usa la forma più lunga ovunque lo shim sul `PATH` non sia impostato
(un runner di CI, un modulo registrato a mano):

```bash title="Utilizzo (senza shim sul PATH)"
boxlang bxSites <verbo> [opzioni]
```

Ogni verbo accetta `--projectRoot=<percorso>` (oppure un percorso
posizionale semplice) per puntare a un progetto diverso dalla cartella
corrente, e i due flag globali qui sotto possono comparire prima di
qualsiasi verbo.

Ogni `docs/` menzionato qui sotto si applica ugualmente a un progetto che
usa `src/` invece - vedi [Per iniziare](getting-started.md#add-pages) per
la convenzione `docs/`-o-`src/`. `new` genera sempre lo scheletro di
`docs/`.

## Opzioni globali

| Flag | Descrizione |
|---|---|
| `-h`, `--help` | Mostra l'utilizzo ed esce |
| `-v`, `--version` | Mostra la versione del modulo ed esce |

## `new`

Genera lo scheletro di un progetto di documentazione.

```bash title="Utilizzo"
bxSites new [path] [--name=...] [--theme=<see guides/themes.md for all 10>] [--description=...] [--format=yaml|json]
```

- `--name` - il nome del sito scritto nella configurazione del sito (per default, il nome della cartella di destinazione)
- `--theme` - il valore predefinito è `bootstrap`
- `--description` - la descrizione del sito scritta nella configurazione del sito
- `--format` - `yaml` (predefinito, genera lo scheletro di `bxsites.yaml`) oppure `json` (genera lo scheletro di `bxsites.json`)

## `build`

Genera `docs/**.md` in un sito statico dentro `site/`. Compila anche
l'indice di ricerca (a meno che `search` non sia `false` nella
configurazione del sito, o che `searchProvider` sia impostato su un
provider - come `algolia`/`pagefind` - che non lo usa, vedi
[Ricerca](guides/search.md)), esegue la CLI di `pagefind` sul `site/`
finito quando `searchProvider.provider` è `"pagefind"`, e copia il tema +
`docs/assets/**` in `site/`.

```bash frame="terminal" title="Terminal"
bxSites build
```

## `serve`

Compila e serve il sito in locale con ricaricamento automatico.

```bash title="Utilizzo"
bxSites serve [--port=8080] [--host=127.0.0.1]
```

Gira in primo piano finché non viene interrotto (Ctrl+C).

## `search-index`

Ricompila `site/search-index.json` in modo autonomo, senza rirenderizzare
le pagine o ricopiare gli asset. `build` esegue già automaticamente
questo stesso passaggio - questo verbo esiste per i casi in cui serve
solo aggiornare l'indice. Copre sempre e solo l'albero `docs/` principale,
anche su un progetto con `docs/versions/`/`docs/i18n/` - una vera `build`
scrive invece l'indice con ambito proprio di ogni albero (vedi
[Versionamento](guides/versioning.md#cosa-è-fuori-scopo-per-ora)).

```bash frame="terminal" title="Terminal"
bxSites search-index
```

## `clean`

Rimuove `site/` e qualsiasi cache di compilazione, lasciando intatti
`docs/` e la configurazione del sito.

```bash frame="terminal" title="Terminal"
bxSites clean
```

## `gh-deploy`

Compila il sito, poi lo forza (force-push) su un branch in stile
`gh-pages` - un commit per ogni deploy, senza storia accumulata su quel
branch, seguendo la stessa convenzione di `mkdocs gh-deploy` propria di
mkdocs. Richiede che il progetto sia un repository git con un remote
configurato; non tocca mai il tuo branch corrente o la working tree
(effettua il push da una `git worktree` usa e getta).

```bash title="Utilizzo"
bxSites gh-deploy [--branch=gh-pages] [--remote=origin] [--message="..."]
```

- `--branch` - il valore predefinito è `gh-pages`
- `--remote` - il valore predefinito è `origin`
- `--message` - il messaggio dell'unico commit del branch, il valore predefinito è `"Deploy site via bxSites gh-deploy"`

Vedi [Distribuzione](guides/deployment.md) per la configurazione completa
di GitHub Pages (attivare Pages per il branch, `baseURL`, ecc.).

## `deploy`

Compila il sito, poi lo invia a un vero target di deploy - S3 (e
qualsiasi servizio compatibile con S3 - DigitalOcean Spaces, Cloudflare
R2, Backblaze B2, MinIO), Azure Blob Storage, Google Cloud Storage,
Firebase Hosting, FTP, SFTP, rsync via SSH, Netlify, Vercel, Cloudflare
Pages, una directory locale, oppure GitHub Pages (lo stesso push che fa
`gh-deploy`, semplicemente raggiungibile anche da questo unico comando
unificato).

```bash title="Utilizzo"
bxSites deploy --entry=<nome> [--verbose]
bxSites deploy [--target=local|github-pages] [flag specifici del target] [--verbose]
bxSites deploy [--verbose]
```

Tre modi per invocarlo:

1. **`--entry=<nome>`** - smista verso qualunque target dichiarato da un
   file `deployments/<nome>.json` (vedi sotto). Ogni target eccetto
   `local`/`github-pages` lo richiede - c'è più configurazione di quanta
   ne possano ragionevolmente portare un paio di flag.
2. **`--target=<nome>` con i propri flag** - una scorciatoia a soli flag
   per i due target più semplici, che non necessitano affatto di una
   cartella `deployments/`: `local` (`--destination=<percorso>`) e
   `github-pages` (`[--branch] [--remote] [--message]`, ogni campo
   opzionale, con gli stessi valori predefiniti di `gh-deploy`).
3. **Nessun flag - distribuisce tutto.** Ogni voce `deployments/*.json`
   viene distribuita a turno, a partire da un'unica build condivisa (il
   sito viene compilato una sola volta, non una volta per target).
   Richiede che esista almeno una voce `deployments/*.json`. Il
   fallimento di un target non ferma gli altri - ogni voce viene tentata,
   e il comando esce con un codice diverso da zero solo se almeno una di
   esse è fallita; il riepilogo finale riporta quante sono andate a buon
   fine (ad es. `Deployed to 2/3 target(s) (1 failed)`).

`--verbose` stampa una riga di avanzamento quando la build inizia/finisce
e quando ciascun target inizia/finisce, invece del solo riepilogo finale
su una riga.

Vedi [Distribuzione](guides/deployment.md) per la struttura di
configurazione propria di ogni target e un esempio reale di
`deployments/*.json` per ciascuno.

## `package`

Compila il sito, poi lo comprime in un unico archivio distribuibile - uno
zip semplice la cui radice è il contenuto stesso del sito compilato (non
una cartella `site/` che lo racchiude), pronto per essere allegato a una
release o consegnato a qualsiasi host che accetta solo il caricamento di
uno zip.

```bash title="Utilizzo"
bxSites package [--output=<percorso>]
```

`--output` ha come valore predefinito `<projectRoot>/site.zip` (un valore
relativo viene risolto rispetto alla radice del progetto); le cartelle
padre di una destinazione annidata vengono create automaticamente.

## `migrate`

Converte un progetto di documentazione esistente in questo - `--from`
sceglie il formato sorgente: `gitbook` (predefinito), `mkdocs`,
`markdown-zip` o `notion`.

```bash linenums="1"
bxSites migrate --source=/percorso/dell/export-gitbook
bxSites migrate --source=/percorso/del/progetto-mkdocs --from=mkdocs
bxSites migrate --source=/percorso/dell/export.zip --from=markdown-zip
bxSites migrate --source=/percorso/dell/export-notion --from=notion
```

- `--source` (obbligatorio) - percorso alla cartella radice dell'export/progetto (`SUMMARY.md` per `gitbook`, `mkdocs.yml` per `mkdocs`), oppure un file `.zip` (`markdown-zip`; `notion` accetta sia uno `.zip` sia una cartella già estratta)
- `--from` - `gitbook` (predefinito), `mkdocs`, `markdown-zip` o `notion`

### `--from=gitbook` (predefinito)

Un export di GitBook - un sommario `SUMMARY.md` più i suoi file `.md`, il
formato di sincronizzazione su disco proprio di GitBook - nell'albero
`docs/` di questo progetto: `SUMMARY.md` diventa `docs/nav.json`, la
sintassi `{% block %}` diventa il proprio equivalente in bx-sites
(direttive `::: name`, oppure la sintassi nativa `=== "Title"` per le
schede / `!!! type` per le ammonizioni dove esiste già una corrispondenza
più stretta - vedi [Blocchi di contenuto](guides/content-blocks.md)), i
file `README.md` diventano `index.md`, e `.gitbook/assets/**` viene
copiato in `docs/assets/gitbook/`.

### `--from=mkdocs`

Un progetto mkdocs - `mkdocs.yml` più la sua cartella `docs/` - in un
progetto bx-sites completo: `mkdocs.yml` diventa `bxsites.yaml` +
`docs/nav.json`, e ogni pagina viene copiata praticamente invariata, dato
che la sintassi di ammonizioni/schede/matematica/annotazioni di codice
propria di mkdocs-material *è* già la sintassi nativa di bx-sites - vedi
[Migrare da mkdocs](guides/migrating-from-mkdocs.md). Gli asset non-`.md`
(immagini che comunemente si trovano accanto alla pagina che le usa,
mkdocs non ha un'unica convenzione di cartella asset) vengono spostati in
`docs/assets/mkdocs/` e i loro riferimenti riscritti.

### `--from=markdown-zip`

Un semplice `.zip` di file Markdown - nessun formato di export
proprietario da tradurre, dato che l'annidamento delle cartelle è già di
per sé la stessa convenzione di nav propria di bx-sites, e un link `.md`
relativo da pagina a pagina si risolve già nel modo in cui bx-sites se
lo aspetta. Per lo più una semplice copia: ogni file non-`.md` (
un'immagine, ad esempio) viene spostato in `docs/assets/imported/` e ogni
riferimento ad esso nelle pagine viene riscritto di conseguenza. Non
viene scritto alcun `bxsites.yaml`/`docs/nav.json` - un semplice zip non
porta con sé un nome del sito o una struttura di nav propria da tradurre.

### `--from=notion`

Un archivio Notion "Export as Markdown & CSV" (uno `.zip`, o una cartella
già estratta) - gestisce le due particolarità proprie di Notion che
nessun'altra fonte qui migrata ha: ogni cartella di pagina/sottopagina è
seguita da uno spazio e un id di 32 caratteri (per distinguere pagine con
lo stesso titolo, mai pensato per essere letto), e il titolo di una
pagina viene ripetuto come un letterale `# Heading` iniziale invece di
essere portato nel frontmatter. Entrambe le cose vengono ripulite: il
suffisso id viene rimosso e il nome restante trasformato in slug per il
nome del file di output, l'intestazione iniziale diventa un vero campo
`title` nel frontmatter invece di una prima riga duplicata, e ogni target
di link/immagine (che Notion scrive con codifica URL, puntando comunque
ai nomi originali con suffisso id) viene riscritto di conseguenza. I file
non-`.md` vengono spostati in `docs/assets/imported/`, come per
`markdown-zip` sopra.

### Tutte e quattro

Stampa un riepilogo delle pagine (e, per mkdocs/markdown-zip/notion,
degli asset) convertiti e, quando qualcosa non ha potuto essere
convertito automaticamente, un elenco esatto di cosa richiede un
controllo manuale - niente viene mai scartato in silenzio. Un file di
destinazione, `bxsites.yaml`, o `docs/nav.json` già esistenti vengono
sovrascritti (anche questo segnalato), quindi rivedi l'output migrato
prima di fare il commit.

## `audit`

Un controllo di qualità del contenuto di livello CI su un `site/` già
compilato - esegui prima `build`. Verifica:

- **Link/immagini interni rotti** - qualsiasi `<a href>`/`<img src>` che
  punta a una pagina o un asset che non esiste in `site/`. Fa fallire il
  controllo.
- **Testo alternativo mancante** - qualsiasi `<img>` senza alcun
  attributo `alt`. Un `alt=""` vuoto (il markup corretto per un'immagine
  puramente decorativa) non viene segnalato. Fa fallire il controllo.
- **Pagine orfane** - pagine che esistono in `site/` ma non sono
  raggiungibili seguendo i link dalla home page propria di qualsiasi
  albero (l'`index.html` del sito principale, e quello proprio di ogni
  versione/locale). Solo informativo - non fa mai fallire il controllo,
  dato che una pagina che un progetto ha deliberatamente lasciato fuori
  dalla propria nav (ad es. frontmatter `hidden: true`) *deve* essere
  raggiungibile solo tramite un link diretto.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites build
bxSites audit
```

Esce con `1` quando ci sono link/immagini rotti o immagini senza alt,
`0` altrimenti (le pagine orfane non influiscono mai sul codice di
uscita). Deliberatamente limitato ai soli link interni - non effettua
richieste HTTP per verificare URL esterni, cosa che spetta a uno
strumento dedicato di controllo link eseguito come proprio job separato.

## `stats`

Un report di riepilogo di sola lettura su un `site/` già compilato -
esegui prima `build`. Riporta:

- **Pagine e parole** - conteggio totale delle pagine e un conteggio
  approssimativo delle parole (tag rimossi, lo stesso standard "abbastanza
  buono per una stima" della cifra di tempo di lettura propria del blog),
  più una ripartizione per albero non appena ce n'è più di uno (una
  versione, o una locale non predefinita).
- **Versioni e locale** - nomi di ogni cartella `docs/versions/`/
  `docs/i18n/` non predefinita.
- **Blog** - conteggi di post/categorie/autori/anni attivi, presi
  direttamente dalla forma della cartella propria di `site/blog/` (così
  corrisponde sempre a ciò che è stato effettivamente pubblicato, bozze
  escluse) - `none` quando non c'è alcun blog.
- **Tag** - il numero di tag distinti in tutto il sito.
- **Indice di ricerca** - numero di voci e dimensione file di
  `search-index.json`, oppure `none` quando la ricerca è disattivata o è
  attivo un provider non locale.
- **Output del sito** - conteggio totale dei file e dimensione su disco
  del `site/` compilato.

```bash
bxSites build
bxSites stats
```

Esce sempre con `0` - puramente informativo, niente qui è un gate di
superamento/fallimento (quello è compito di `audit`).

## `doctor`

Un controllo di salute dell'ambiente/configurazione in un solo passaggio
- il verbo "esegui questo prima di aprire una segnalazione di bug".
Verifica la versione della JVM, che `docs/` esista, che
`bxsites.yaml`/`.json` venga effettivamente analizzato e validato, che i
moduli BoxLang richiesti (`bx-markdown`, `bx-esapi`, `bx-yaml`,
`bx-image`) siano installati e attivati e - se esiste una sovrascrittura
`theme/` a livello di progetto - che soddisfi il contratto dei due file
obbligatori `layout.bxm`/`page.bxm`.

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Esce con `1` se un controllo fallisce, `0` altrimenti. Niente qui
modifica un progetto - è puramente diagnostico.

## `post:new`

Genera lo scheletro di un nuovo post del blog in `docs/blog/posts/<slug>.md`.

```bash title="Utilizzo"
bxSites post:new --title="My New Post" [--slug=...] [--date=...] [--authors=...] [--categories=...] [--tags=...] [--draft]
```

- `--title` (obbligatorio) - diventa anche il `title` nel frontmatter del post
- `--slug` - il valore predefinito è uno slug generato da `--title`
- `--date` - il valore predefinito è oggi (`yyyy-MM-dd`)
- `--authors`, `--categories`, `--tags` - separati da virgola
- `--draft` - il valore predefinito è `true` (passa `--!draft` per pubblicare immediatamente)

Vedi [Blog](guides/blog.md) per il riferimento completo del frontmatter.

## `version:new`

Fa un'istantanea dell'albero `docs/` corrente in `docs/versions/<name>/`,
escludendo `assets/`, `versions/`, `i18n/` e `blog/` (ognuno è un proprio
albero caricato separatamente, non parte dell'istantanea).

```bash title="Utilizzo"
bxSites version:new --name=1.0
```

- `--name` (obbligatorio) - la cartella/etichetta di versione, ad es. `1.0`

Vedi [la sezione "Versionamento" di Configurazione](configuration.md#versionamento).

## `i18n:status`

Riporta la copertura della traduzione per locale - per ogni locale
configurata, quante pagine dell'albero predefinito esistono (allo stesso
percorso relativo) sotto `docs/i18n/<code>/`, e quali mancano ancora.

```bash frame="terminal" title="Terminal"
bxSites i18n:status
```

Esce sempre con `0` - puramente informativo.

## `i18n:new`

Genera lo scheletro di una nuova cartella di locale `docs/i18n/<code>/`,
seminandola con un `index.md` copiato dall'`index.md` proprio della
locale predefinita, quando ne esiste uno.

```bash title="Utilizzo"
bxSites i18n:new --code=es
```

- `--code` (obbligatorio) - il codice della locale, ad es. `es`, `fr`, `pt-BR`

Vedi [Internazionalizzazione](guides/i18n.md) per collegare la nuova
locale a `i18n.locales` di `bxsites.yaml`.

## `page:new`

Genera lo scheletro di una singola pagina di documentazione a un percorso
arbitrario sotto `docs/`, con il frontmatter richiesto già compilato.

```bash title="Utilizzo"
bxSites page:new --path=guides/setup.md [--title=...] [--description=...] [--icon=...] [--tags=...] [--order=...]
```

- `--path` (obbligatorio) - relativo a `docs/`, deve terminare in `.md`
- `--title`, `--description`, `--icon`, `--order` - scritti nel frontmatter
- `--tags` - separati da virgola

## `plugin:new`

Genera lo scheletro di un modulo plugin (`box.json`, `ModuleConfig.bx`, un
`models/BxSitesPlugin.bx` con ogni hook già stubato) rispecchiando
`examples/hello-plugin/`.

```bash title="Utilizzo"
bxSites plugin:new --name=my-analytics-plugin [--dest=...]
```

- `--name` (obbligatorio) - il nome/slug del modulo del plugin
- `--dest` - il valore predefinito è `<projectRoot>/<name>`

Vedi [Plugin](guides/plugins.md) per il riferimento degli hook e come
collegare il plugin finito all'array `plugins` di `bxsites.yaml`.

## `install:plugin`

Scarica un plugin pubblicato da ForgeBox e lo deposita direttamente nel
proprio `boxlang_modules/` del progetto - la convenzione nativa di
BoxLang per i moduli locali caricati automaticamente, quindi non serve
nulla oltre al binario `bxSites` stesso (nessun coinvolgimento di
`box`/CommandBox).

```bash title="Utilizzo"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

- `--name` (obbligatorio) - lo slug ForgeBox da installare
- `--version` - una versione specifica; ometti per l'ultima

Stampa il vero nome di mapping del modulo registrato una volta caricato -
aggiungi quel nome all'array `plugins` di `bxsites.yaml` per attivarlo
(l'installazione da sola non attiva mai un plugin - vedi
[Plugin](guides/plugins.md)).

## `theme:new`

Estrae uno dei temi integrati nella cartella `theme/` propria del
progetto per personalizzarlo, rispecchiando il flusso di eject `--theme`
di mkdocs.

```bash title="Utilizzo"
bxSites theme:new --theme=material
```

- `--theme` (obbligatorio) - `bootstrap`, `material`, `tailwind`, `docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook`, o `notion` - vedi [Temi](guides/themes.md#integrati)

Fallisce invece di sovrascrivere un `theme/` già esistente. Vedi
[Temi](guides/themes.md) per il contratto di sovrascrittura
(`layout.bxm` + `page.bxm`).

## `install:theme`

Scarica un tema pubblicato da ForgeBox nel proprio `themes/<name>/` del
progetto - nient'altro che il binario `bxSites` serve, come per
`install:plugin`.

```bash title="Utilizzo"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

- `--name` (obbligatorio) - lo slug ForgeBox da installare
- `--version` - una versione specifica; ometti per l'ultima

Valida il pacchetto scaricato rispetto al contratto `ThemeProvider`
(`layout.bxm` + `page.bxm`) prima di terminare, così un pacchetto rotto
fallisce al momento dell'installazione invece che alla prossima `build`.
Imposta `theme.name` di `bxsites.yaml` sul nome installato per usarlo -
vedi [Temi](guides/themes.md#installare-un-tema-pubblicato).

## `theme:import`

Conversione con il massimo impegno possibile di un tema proveniente
dall'ecosistema di un altro generatore di siti statici
(`mkdocs`/`jekyll`/`hugo`) in uno scheletro di tema bx-sites sotto
`themes/<name>/` - un punto di partenza, non un porting senza perdite in
un solo comando.

```bash title="Utilizzo"
bxSites theme:import --source=mkdocs --path=/path/to/theme --name=my-imported-theme
```

- `--source` (obbligatorio) - `mkdocs`, `jekyll`, o `hugo`
- `--path` (obbligatorio) - la cartella radice propria del tema sorgente
- `--name` (obbligatorio) - il nome di destinazione, scritto in `themes/<name>/`

Rieseguire il comando con lo stesso `--name` è sicuro - `layout.bxm`/
`page.bxm` vengono sovrascritti e qualsiasi cartella di asset appena
trovata viene unita. Vedi [Importare un tema](guides/theme-import.md)
per sapere esattamente cosa viene tradotto e cosa no, e cosa controllare
in seguito.

## `page:rename`

Sposta una pagina di documentazione da un percorso a un altro,
riscrivendo ogni link Markdown relativo in `docs/**` che puntava al
vecchio percorso - lo stesso problema di link rot relativo ai file che il
lato HTML compilato già risolve (`audit`), applicato invece al sorgente
Markdown grezzo al momento della rinomina.

```bash title="Utilizzo"
bxSites page:rename --from=guides/old-name.md --to=guides/new-name.md
```

- `--from` (obbligatorio) - il percorso attuale della pagina, relativo a `docs/`
- `--to` (obbligatorio) - il suo nuovo percorso, relativo a `docs/`

Vengono riscritti solo i link nudi in stile `[text](relative/path.md)` -
gli URL assoluti, `mailto:`, e le pure ancore in pagina vengono lasciati
stare. `docs/assets/**` non viene mai analizzato.

Imprime anche il frontmatter `redirect_from` della pagina spostata con il
suo vecchio URL, così una build ([Redirect](guides/redirects.md))
continua a rispondere per esso invece di lasciare che la rinomina dia un
404 a ogni link esterno di cui questo progetto non controlla la fonte.

## `blog:drafts`

Elenca ogni post del blog il cui frontmatter imposta `draft: true` -
`build` salta sempre le bozze, quindi questo è l'unico posto dove la loro
esistenza viene mostrata.

```bash frame="terminal" title="Terminal"
bxSites blog:drafts
```

Esce sempre con `0`.

## `blog:find`

Filtra i post del blog per autore/categoria/tag/intervallo di date, senza
eseguire una `build` completa.

```bash title="Utilizzo"
bxSites blog:find [--author=...] [--category=...] [--tag=...] [--since=...] [--until=...] [--drafts]
```

- `--author`, `--category`, `--tag` - corrispondenza esatta senza distinzione tra maiuscole/minuscole con uno qualsiasi dei valori propri del post
- `--since`, `--until` - una data; corrispondono solo i post a partire da/fino a `--since`/`--until`
- `--drafts` - includi anche i post in bozza (esclusi per default)

Ogni filtro è opzionale e indipendente - non passandone nessuno vengono
elencati tutti i post pubblicati.

## `search:query`

Esegue una query per parole chiave su un `site/search-index.json` già
compilato - esegui prima `build` o `search-index`. Classifica i risultati
usando la stessa ponderazione relativa dei campi che usa il widget di
ricerca lato client (titolo, poi tag, poi intestazioni, poi corpo), così
puoi verificare rapidamente cosa mostrerebbe la ricerca di un visitatore
reale senza aprire un browser.

```bash title="Utilizzo"
bxSites search:query --query="getting started" [--limit=10]
```

- `--query` (obbligatorio) - termini di ricerca separati da spazio
- `--limit` - risultati massimi da restituire, il valore predefinito è `10`

## `lint`

Un passaggio di qualità del contenuto pre-build sul sorgente Markdown
grezzo di `docs/`, distinto da `audit` (che ispeziona solo un `site/` già
compilato). Verifica:

- **Salti di livello delle intestazioni** - un corpo di pagina che salta
  direttamente da `##` a `####` senza un `###` in mezzo (struttura
  confusa, e negativo per l'accessibilità). Le righe dentro un blocco di
  codice recintato non vengono mai scambiate per intestazioni.
- **Problemi di data dei post del blog** - un post in `docs/blog/posts/**`
  con una `date` di frontmatter mancante o non valida (`build` stesso
  lancia un errore su questo nel momento in cui carica i post - `lint` lo
  mostra invece come un risultato).

```bash frame="terminal" title="Terminal"
bxSites lint
```

Esce con `1` quando uno dei due controlli trova qualcosa, `0` altrimenti.
