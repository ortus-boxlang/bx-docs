---
title: Redirect
order: 11
icon: phosphor-duotone:signpost
tags: [guide, redirect]
---

# Redirect

Mantieni funzionante un vecchio URL dopo aver spostato, rinominato o
ristrutturato una pagina - viene scritto uno stub HTML statico al
vecchio percorso, così una voce di indice obsoleta di un motore di
ricerca o il vecchio segnalibro di qualcuno atterrano comunque sulla
pagina giusta invece di dare un 404. Non è coinvolta nessuna regola di
riscrittura lato server (un host statico non ha dove eseguirne una) - lo
stub è solo l'HTML sufficiente perché un browser si reindirizzi da solo
e un crawler apprenda il vero URL canonico.

## Per pagina: frontmatter `redirect_from`

Aggiungi uno o più vecchi percorsi al proprio frontmatter di una pagina:

```md title="docs/guides/new-setup.md"
---
title: New Setup
redirect_from:
  - guides/old-setup
  - setup
---
```

Ogni voce è un segmento URL "pretty" - senza slash iniziale/finale, senza
estensione `.md`/`.html` - la stessa forma che assume l'URL proprio della
pagina. Una build scrive quindi uno stub per ciascuno
(`site/guides/old-setup/index.html`, `site/setup/index.html` per
l'esempio sopra), entrambi che reindirizzano al vero URL proprio di
questa pagina.

`redirect_from` è limitato all'albero a cui appartiene la pagina stessa -
la pagina propria di una versione reindirizza all'interno di quella
versione (`site/versions/2.0/old-path/`), la pagina tradotta propria di
una locale reindirizza all'interno di quella locale
(`site/es/old-path/`), esattamente come già fa il vero URL proprio della
pagina. Non c'è nulla di extra da configurare per albero.

## A livello di sito: `bxsites.yaml` `redirects`

Per un vecchio URL che non è mai appartenuto a una pagina specifica - una
sezione ristrutturata, il percorso di un vecchio dominio, qualsiasi cosa
non sia naturalmente il proprio "vecchio nome" di una singola pagina -
elenca invece una coppia esplicita `from`/`to`:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
      - from: moved-to-another-site
        to: https://example.com/docs
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" },
    		{ "from": "moved-to-another-site", "to": "https://example.com/docs" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"

    [[redirects]]
    from = "moved-to-another-site"
    to = "https://example.com/docs"
    ```

- `from` - il vecchio segmento URL "pretty", stessa forma di
  `redirect_from` sopra
- `to` - un percorso relativo alla radice (risolto rispetto al proprio
  `baseURL` del sito, la stessa convenzione già usata da
  `theme.logo`/`ogImage`) oppure un URL `https://` completo, per
  reindirizzare completamente fuori dal sito

`redirects` si applica sempre solo all'albero principale del sito - un
`to` nudo è un percorso relativo alla radice che è univoco solo alla
radice del sito. Un albero di versione/locale che vuole la stessa mappatura
di vecchio URL ha bisogno invece del proprio `redirect_from` a livello di
pagina.

## `page:rename` lo imposta per te

Rinominare/spostare una pagina con [`page:rename`](../cli-reference.md#pagerename) aggiunge automaticamente
il suo vecchio percorso al proprio `redirect_from` della pagina spostata
- oltre a riscrivere ogni link Markdown relativo che vi puntava, anche il
vecchio URL stesso continua a funzionare:

```bash title="Utilizzo"
bxSites page:rename --from=guides/old-setup.md --to=guides/new-setup.md
```

Rinominare una pagina più di una volta continua semplicemente ad
aggiungere - l'elenco `redirect_from` di una pagina può contenere tutti i
vecchi percorsi che ha avuto nel tempo.

## Conflitti

Una build fallisce del tutto, invece di sovrascrivere silenziosamente
contenuto reale, se:

- Il proprio percorso `from` di un redirect collide con una pagina reale
  già costruita a quel percorso (`BxSites.RedirectConflict`)
- Due redirect (voci `redirect_from`, voci di configurazione `redirects`,
  o una di ciascuna) puntano entrambi allo stesso percorso `from`

## Cosa è fuori scopo (per ora)

- **I post del blog non ottengono `redirect_from`.** La chiave di
  frontmatter viene letta solo per le pagine regolari di `docs/`, non per
  `docs/blog/posts/**` - un post del blog spostato ha bisogno invece
  della propria voce di configurazione `redirects`.
- **Nessun redirect con wildcard/pattern.** Ogni `from` è un vecchio
  percorso esatto - non esiste un catch-all `guides/old/*`.
