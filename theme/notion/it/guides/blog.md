---
title: Blog
order: 10
icon: phosphor-duotone:newspaper
tags: [guide, blog]
---

# Blog

Un blog è un'altra funzionalità per convenzione, con la stessa forma di
[versioni](../configuration.md#versionamento)/[i18n](i18n.md) o
dell'[indice dei tag](../getting-started.md#add-pages) - metti i post
sotto `docs/blog/posts/`, e BxSites compila `/blog/` (paginato), una
pagina categoria per ogni categoria, una pagina archivio per ogni anno
solare, una pagina autore per ogni autore, un feed RSS per categoria più
uno per l'intero blog, e una pagina `/blog/stats/`, senza alcuna
configurazione richiesta. Un progetto senza la cartella
`docs/blog/posts/` semplicemente non ha un blog - nient'altro cambia.

## Scrivere un post

Ogni file `.md` sotto `docs/blog/posts/`, a qualsiasi profondità, è un
post - le sottocartelle sono del tutto opzionali e servono solo alla tua
comodità di redazione. Una cartella piatta va benissimo per una manciata
di post; una volta arrivato alle centinaia, archiviare i post sotto
`docs/blog/posts/2026/` (o `docs/blog/posts/2026/03/`, o qualsiasi schema
preferisci) mantiene navigabile l'albero file del tuo editor senza dover
rinominare nulla o toccare una convenzione di prefisso data nel
frontmatter. Niente di tutto ciò influisce sul sito compilato - l'ordine
di un post, il suo archivio annuale, e il suo URL (`blog/<slug>/`) sono
tutti derivati esclusivamente dal frontmatter, mai da dove il file si
trova effettivamente, quindi la cartella di un post e la sua `date`
effettiva sono sempre libere di non coincidere:

```text title="Struttura del progetto"
docs/blog/posts/
├── hello-world.md              (va bene anche piatta)
├── 2026/
│   ├── announcing-2-0.md
│   └── 03/
│       └── a-deep-dive.md
```

Frontmatter, per qualsiasi post indipendentemente da dove sia archiviato:

```markdown title="docs/blog/posts/announcing-2-0.md" linenums="1"
---
title: Announcing BoxLang 2.0
date: 2026-08-15
authors: [lmajano]
categories: [Releases]
tags: [boxlang, release]
summary: A faster runtime, a smaller footprint, and a few surprises.
image: assets/blog/boxlang-2-cover.png
---

A short intro paragraph or two.

<!-- more -->

The rest of the post - everything below the `<!-- more -->` marker is left
out of the excerpt shown on `/blog/` and category pages, but still renders
in full on the post's own page.
```

- `date` (obbligatorio) - qualsiasi formato che BxSites sa interpretare
  (`2026-08-15`, o una data-ora completa). Determina l'ordine proprio del
  post (più recente per primo) e il suo
  `<pubDate>`/`article:published_time`.
- `authors` - un elenco di id corrispondenti a voci di
  [`docs/blog/authors.yml`](#authors), oppure un semplice nome senza voce
  corrispondente (renderizzato come testo non collegato invece di far
  fallire il build - comodo per un guest post occasionale).
- `categories` - la tassonomia propria di un post, ognuna con la propria
  pagina `/blog/category/<slug>/` (e il proprio feed RSS
  `/blog/category/<slug>/feed.xml` - vedi [Feed](#feed)). Non correlato a
  `tags`, sotto.
- `tags` - lo stesso frontmatter `tags` a livello di sito che ha già ogni
  altra pagina (vedi [Per iniziare](../getting-started.md#add-pages)) - i
  tag di un post vengono renderizzati come badge e confluiscono
  nell'indice principale `/tags/` insieme a ogni altra pagina taggata.
- `summary` - un estratto di una riga mostrato su `/blog/`/le pagine
  categoria e nel feed RSS, usato quando un post non ha un marcatore
  `<!-- more -->`. Senza nessuno dei due, BxSites ricade su un
  troncamento in testo semplice del corpo stesso del post.
- `image` - un'immagine in evidenza (un percorso relativo a
  `docs/assets/`, o un URL completo) - mostrata in cima al post e come
  miniatura in ogni card lista/categoria. Diventa anche l'`og:image`/
  Twitter card proprio del post a meno che `ogImage` non lo sovrascriva
  separatamente. Un'immagine relativa a `docs/assets/` (e l'`avatar`
  proprio di un autore, sotto) riceve lo stesso trattamento responsivo
  `<picture>`/`srcset`/WebP di qualsiasi altra immagine sotto
  `docs/assets/` - vedi [Immagini](images.md).
- `slug` - sovrascrive il segmento URL (`/blog/<slug>/`) - ricavato dal
  nome file per impostazione predefinita.
- `draft: true` - esclude il post da un vero `bxSites build` del tutto.
  `bxSites serve` lo mostra comunque in anteprima (con un banner "🚧
  Bozza" visibile sul post stesso e una card con bordo tratteggiato
  ovunque sia elencato), così puoi rileggere una bozza in locale prima
  che sia pronta - vedi [Vedere in anteprima le bozze](#previewing-drafts).

Ogni altra chiave di frontmatter a livello di pagina già documentata in
[Per iniziare](../getting-started.md#add-pages) (`icon`, `description`,
`ogImage`, `toc`) funziona anche su un post.

## Immagini in evidenza e altri asset del blog

`docs/assets/blog/` non è nulla di speciale oltre a una normale
sottocartella di `docs/assets/` (già copiata interamente in
`site/assets/`) - è solo dove questa guida (e la ricerca per convenzione
dell'avatar di un autore, sotto) si aspetta che vivano le copertine dei
post/foto degli autori, così il `docs/assets/` proprio di un progetto non
si affolla mescolando immagini del blog con il resto dei suoi diagrammi e
icone. Nulla impone questa posizione - qualsiasi percorso `docs/assets/**`
funziona in `image`/`avatar`.

## Autori

`docs/blog/authors.yml` è opzionale - una voce per ogni id autore,
referenziata dall'elenco `authors` proprio di un post:

```yaml title="docs/blog/authors.yml" linenums="1"
lmajano:
  name: Luis Majano
  title: CEO, Ortus Solutions
  bio: >
    Founder of Ortus Solutions and creator of ColdBox, WireBox, and
    BoxLang. Building developer tools since 2005.
  url: https://github.com/lmajano
  email: lmajano@ortussolutions.com
  socials:
    github: https://github.com/lmajano
    twitter: https://x.com/lmajano
```

Solo `name` è obbligatorio. Ogni autore referenziato da almeno un post
ottiene la propria pagina `/blog/authors/<id>/` (bio, social, ogni post
che ha scritto) - un autore non ancora accreditato non ottiene una
pagina, anche se è nell'elenco.

**Avatar, per convenzione** - metti un file in
`docs/assets/blog/authors/<id>.{jpg,jpeg,png,webp,svg}` e viene recepito
automaticamente, nessuna chiave `avatar:` necessaria. Un `avatar` esplicito
in `authors.yml` (un URL o un percorso relativo a `docs/assets/`)
sovrascrive sempre la ricerca per convenzione.

## Categorie, archivi, paginazione, e la voce nav "Blog"

Ogni valore distinto di `categories` in tutti i post ottiene la propria
pagina `/blog/category/<slug>/`, che elenca solo i post di quella
categoria. Ogni anno solare con almeno un post ottiene anche la propria
pagina `/blog/archive/<year>/` (`/blog/archive/2026/`,
`/blog/archive/2025/`, ...), derivata interamente dal frontmatter `date`
di ogni post - nessuna struttura di cartelle o convenzione di nome file
richiesta, quindi dove il file `.md` di un post si trova effettivamente
sotto `docs/blog/posts/` (piatto, o suddiviso in tue sottocartelle per una
navigazione più semplice durante la scrittura) non deve mai corrispondere
alla sua `date`. La lista principale `/blog/` ottiene automaticamente
blocchi di link "Sfoglia per anno"/"Sfoglia per categoria", ognuno con un
conteggio dei post per anno/categoria, non appena i post coprono più di un
anno/categoria - un singolo anno o categoria da solo non merita un blocco
di link, quindi viene omesso in entrambi i casi.

La lista principale `/blog/`, ogni pagina categoria, e ogni pagina
archivio annuale si paginano tutte allo stesso modo -
`blog.postsPerPage` nella configurazione del sito controlla quanti post
per pagina (predefinito `10`); dalla pagina 2 in poi si passa a
`.../page/2/`, `.../page/3/`, ecc.

Una singola voce "Blog" viene aggiunta automaticamente alla nav
principale, non appena `docs/blog/posts/` ha almeno un post non-bozza -
nessuna modifica a `nav`/`docs/nav.json` necessaria. Per impostazione
predefinita viene aggiunta per ultima, dopo tutto il resto. Per metterla
in un punto specifico invece, aggiungi la tua voce con un `url` esplicito
(bypassa la regola solita per cui `path` deve corrispondere a una pagina
reale, dato che il blog non è una pagina di `docs/`) al tuo array `nav` o
a `docs/nav.json` - farlo sopprime del tutto quella aggiunta
automaticamente, quindi non c'è mai un duplicato:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - path: index.md
      - title: Blog
        url: blog/index.html
        icon: lucide:newspaper
      - path: about.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		{ "path": "index.md" },
    		{ "title": "Blog", "url": "blog/index.html", "icon": "lucide:newspaper" },
    		{ "path": "about.md" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Blog"
    url = "blog/index.html"
    icon = "lucide:newspaper"

    [[nav]]
    path = "about.md"
    ```

I singoli post non vengono aggiunti loro stessi alla nav (come l'indice
dei tag) - sono raggiungibili da `/blog/`, dalla propria pagina categoria,
dal proprio archivio annuale, dalla pagina del proprio autore, dalla
ricerca, e dai link precedente/successivo reciproci (post cronologicamente
adiacenti tra loro, indipendenti dalla catena precedente/successivo
propria della nav regolare).

La riga meta propria di ogni post (sulla sua card e sulla sua pagina di
dettaglio) mostra anche un tempo di lettura stimato accanto alla data -
una stima approssimativa conteggio-parole / 200 parole al minuto, la
stessa cifra indicativa che usano la maggior parte delle funzionalità di
tempo di lettura, non configurabile.

## Feed

`/blog/feed.xml` - un feed RSS 2.0 standard dei post più recenti, dal più
nuovo, scritto ogni volta che la configurazione del sito risolve un
`baseURL` assoluto (stesso requisito di `sitemap.xml`) e `blog.feed` non
è impostato a `false`. Ogni categoria ottiene anche il proprio feed
filtrato su `/blog/category/<slug>/feed.xml`. Entrambi sono limitati a
`blog.feedLimit` post (predefinito `25`) - la maggior parte dei lettori
di feed si interessa solo a ciò che è nuovo, quindi un feed illimitato su
un blog grande spreca solo banda a ogni polling; impostalo a `0` per
tutti i post, senza limite:

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [blog]
    postsPerPage = 10
    feed = true
    feedLimit = 25
    ```

## Vedere in anteprima le bozze

`draft: true` tiene un post fuori da un vero `bxSites build` del tutto -
ma `bxSites serve` lo include comunque, così puoi leggere una bozza
(cliccare ogni link, controllare l'immagine in evidenza, vedere come
compare su `/blog/`) prima che sia pronta. Una bozza in anteprima porta
sempre un banner "🚧 Bozza" visibile - sulla propria pagina di dettaglio,
e come card con bordo tratteggiato ovunque sia elencata (la lista
principale `/blog/`, le proprie pagine categoria/archivio/autore) - così
non c'è mai ambiguità su cosa sia effettivamente pubblicato. Ferma
`bxSites serve` ed esegui `bxSites build` e la stessa bozza sparisce,
esattamente come se non esistesse.

## Statistiche

`/blog/stats/` - una manciata di card aggregate sul blog nel suo insieme:
post totali, parole totali scritte, tempo di lettura medio, conteggi di
categorie/collaboratori/anni, e tre card "in evidenza" (post più lungo,
categoria più attiva, autore più attivo) ognuna collegata alla pagina
reale a cui si riferisce. Calcolate esclusivamente a partire dai post già
caricati per questo build - nessun analytics separato, nessun tracciamento,
niente persistito tra un build e l'altro - e sempre compilata, anche per
un blog nuovo di zecca con zero post. Collegata in fondo alla lista
principale `/blog/`.

## SEO e social

Ogni post ottiene già tutto ciò che ottiene una pagina normale
(`<meta name="description">`, `og:description`, `og:image`+
`twitter:card` quando è impostata un'immagine - vedi
[Configurazione: `ogImage`](../configuration.md#ogimage)) più alcuni tag
specifici per i post che ogni tema integrato aggiunge automaticamente:
`og:type` è `"article"` invece di `"website"`, e
`article:published_time`/`article:author` (uno per ogni autore
accreditato che ha un `url` impostato in `authors.yml`) sono inclusi
nell'`<head>` della pagina.

## Ricerca

I post sono indicizzati nello stesso `search-index.json` di ogni altra
pagina (sezione 7 della specifica del modulo) - nessuna UI di ricerca
separata per il blog, il box di ricerca già esistente trova già i post
insieme alle pagine di documentazione.

## Personalizzare l'aspetto del blog

Non c'è un "tema del blog" separato da scrivere - ogni pagina del blog
(la lista principale `/blog/`, una pagina categoria/archivio/autore,
`/blog/stats/`, e la pagina di dettaglio propria di ogni post) viene
renderizzata attraverso esattamente lo stesso `layout.bxm`/`page.bxm` di
qualsiasi altra pagina del tuo sito, quindi un blog assomiglia
automaticamente al resto della tua documentazione, e qualsiasi
sovrascrittura di tema che hai già fatto (vedi
[Temi](themes.md#overriding-a-theme)) si applica ad esso senza modifiche,
senza alcun collegamento extra.

Il markup specifico del blog stesso (card dei post, la riga meta
data/autore/tempo-di-lettura, il paginatore, il blocco profilo di un
autore, le liste di link "Sfoglia per anno"/"Sfoglia per categoria")
viene costruito come semplice HTML con una manciata di nomi di classe
fissi, poi inserito in `page.contentHtml` proprio come una pagina Markdown
convertita:

| Classe | Dove compare |
|---|---|
| `blog-post-card` / `blog-post-card--draft` | La card di ogni post su `/blog/`, una pagina categoria, o una pagina archivio |
| `blog-post-meta` | La riga data/autore/tempo-di-lettura, su una card e sulla pagina propria di un post |
| `blog-post-featured-image` | Il frontmatter `image` di un post, sulla sua pagina di dettaglio |
| `blog-draft-badge` | Il banner "🚧 Bozza" (solo `bxSites serve`) |
| `blog-pager` | Link di paginazione precedente/successivo su una lista paginata |
| `blog-author-profile` | Il blocco bio/social di un autore sulla sua pagina `/blog/authors/<id>/` |
| `blog-archive-links` / `blog-category-links` | I blocchi di link "Sfoglia per anno"/"Sfoglia per categoria" su `/blog/` |

Due modi per ridisegnarlo, come qualsiasi altra pagina:

- **Una piccola modifica visiva** - punta a queste classi dal tuo
  [`extraCss`](../configuration.md#extracss--extrajs), allo stesso modo
  in cui [personalizzeresti i colori di un tema](themes.md#customizing-colors-without-a-theme-override).
  Le regole proprie di un tema integrato per queste classi vivono nel suo
  `assets/style.css` (ad es.
  `resources/themes/bootstrap/assets/style.css`) se vuoi un punto di
  partenza da cui sovrascrivere.
- **Modifiche strutturali** - dato che le pagine del blog condividono
  `layout.bxm`/`page.bxm` con tutto il resto,
  [sovrascrivere un tema](themes.md#overriding-a-theme) (oppure
  [scriverne uno da zero](themes.md#writing-a-theme-from-scratch)) cambia
  gli elementi di contorno del blog (header, nav, footer, wrapper
  dell'articolo) insieme a ogni altra pagina - non c'è un template
  separato del blog da copiare.

Quello che non puoi fare è sostituire tu stesso il markup di
card-post/paginatore/profilo-autore con uno tuo - viene generato una sola
volta da `BlogBuilder.bx`, non letto da un file template in `theme/`,
quindi ridisegnarlo con CSS (sopra) è il percorso supportato invece di una
sovrascrittura per singolo componente.
