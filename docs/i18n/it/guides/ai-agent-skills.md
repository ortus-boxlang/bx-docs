---
title: Skill per Agenti IA
order: 6.3
icon: phosphor-duotone:robot
summary: Dai a Claude Code, Cursor, Codex e altri assistenti di coding IA una conoscenza approfondita e strutturata di bx-sites - installa il pacchetto di skill ufficiale tramite npx, la CLI di ColdBox, o il verbo skills:install di bxSites stesso.
tags: [guide, ai, skills]
---

# Skill per Agenti IA

Un **[Agent Skill](https://code.claude.com/docs/en/skills)** è un file
`SKILL.md` piccolo e autonomo che insegna a un assistente di coding IA a
fare bene una cosa specifica - l'assistente lo carica automaticamente, su
richiesta, ogni volta che un'attività corrisponde a ciò che lo skill
descrive. Invece di rispiegare le convenzioni proprie di bx-sites al tuo
assistente a ogni conversazione (come funzionano i blocchi
`::: card :::`, cosa si aspetta la chiave `redirects` di `bxsites.yaml`,
in cosa differisce `page:new` da un file scritto a mano), uno skill gli
fornisce quella conoscenza in anticipo, scritta esattamente come funziona
bx-sites stesso.

[`ortus-boxlang/bx-sites-skills`](https://github.com/ortus-boxlang/bx-sites-skills)
è il pacchetto di skill ufficiale per questo progetto - tredici skill che
coprono tutto, dall'impalcatura di un nuovo progetto alla risoluzione dei
problemi dei GitHub Actions propri di questo repository. Funzionano con
qualsiasi assistente che supporti il formato Agent Skills (Claude Code,
Cursor, Codex e altri).

## Installazione

Tre modi per installare il pacchetto - scegli quello più adatto al tuo
flusso di lavoro:

### `npx skills add`

La [CLI `skills`](https://github.com/skillslib/skills) funziona con
qualsiasi progetto, indipendentemente dal linguaggio o dal runtime, e non
richiede altro che Node.js:

```bash title="Installare tutti gli skill"
npx skills add ortus-boxlang/bx-sites-skills
```

```bash title="Non interattivo (CI, script)"
npx -y skills add ortus-boxlang/bx-sites-skills -y
```

Installa un singolo skill invece dell'intero set puntando direttamente ad
esso:

```bash title="Installare un solo skill"
npx skills add ortus-boxlang/bx-sites-skills/skills/bx-sites-deployment
```

### `coldbox ai skills install`

Se hai già la CLI di ColdBox, può installare direttamente dalla stessa
fonte GitHub - vedi la
[BoxLang Skills Directory](https://skills.boxlang.io/):

```bash title="Installare un solo skill"
coldbox ai skills install ortus-boxlang/bx-sites-skills/bx-sites-deployment
```

### `bxSites skills:install`

bx-sites include anche un proprio verbo tutto-in-uno - un wrapper sottile
su `npx skills add` che installa l'intero pacchetto direttamente nel
progetto corrente, così l'assistente IA di un progetto appena creato
conosce bx-sites fin dal primissimo prompt:

```bash title="Uso"
bxSites skills install
# oppure, in modo equivalente:
bxSites skills:install
```

```bash title="Installare un solo skill"
bxSites skills:install --skill=bx-sites-deployment
```

Richiede Node.js/`npx` nel `PATH` (lo stesso requisito che ha di per sé
`npx skills add`) - vedi la
[Guida di riferimento CLI](../cli-reference.md#skillsinstall) per il
riferimento completo dei flag.

## Skill disponibili

Ogni skill è un singolo `SKILL.md` autonomo (nessun file di risorse
raggruppato), quindi si installa correttamente tramite ognuna delle vie
sopra indicate:

| Skill | Descrizione |
|---|---|
| `bx-sites-getting-started` | Installare bx-sites, creare l'impalcatura di un nuovo progetto (o migrarne uno esistente da GitBook/mkdocs/Notion), struttura del progetto, frontmatter di pagina, collegamenti, build/serve/clean. |
| `bx-sites-content-blocks` | Blocchi di contenuto `::: name :::` ricchi - card, colonne, stepper, pulsanti, embed, card link-a-pagina/anteprima-link, prompt, updates, includes, contenuto condizionale, widget OpenAPI. |
| `bx-sites-markdown` | Ammonizioni, note a piè di pagina, liste di definizioni, tab di contenuto, annotazioni dei blocchi di codice, Mermaid, matematica, tabelle, icone, immagini responsive, interattività con Alpine.js. |
| `bx-sites-variables-functions` | `{{ variables }}` riutilizzabili e funzioni magiche di BoxLang (`docs/functions.bxs`), incluse ricette di visualizzatori per badge di stato/valutazioni/barre di avanzamento. |
| `bx-sites-blog-versioning-i18n` | Il blog (`docs/blog/posts/`), la documentazione versionata (`docs/versions/`), i locale tradotti (`docs/i18n/`) e i redirect. |
| `bx-sites-content-quality` | Controlli di contenuto pre-build senza una build completa - `lint`, `blog:drafts`, `blog:find`, `search:query`. |
| `bx-sites-build` | `build`/`serve`/`clean`/`search-index`, più diagnostiche `doctor`/`stats`/`check` su un sito già costruito. |
| `bx-sites-configuration` | Il riferimento completo delle chiavi di `bxsites.yaml`/`bxsites.json` - `baseURL`, `nav`, `redirects`, `markdown`, asset e altro. |
| `bx-sites-themes` | Scegliere, personalizzare, sovrascrivere, installare o scrivere un tema; il contratto `ThemeProvider`. |
| `bx-sites-search` | Provider di ricerca - locale (MiniSearch), Algolia DocSearch, Pagefind, e come collegare un provider personalizzato. |
| `bx-sites-plugins` | Scrivere/installare un plugin bx-sites (hook del ciclo di vita della build) o un provider CLI (nuovi verbi `bxSites`). |
| `bx-sites-deployment` | Target di deploy (S3/Azure/GCS/Firebase/FTP/SFTP/rsync/Netlify/Vercel/Cloudflare Pages/GitHub Pages), `package`, e il workflow di pubblicazione GitHub Actions. |
| `bx-sites-actions` | Gestire e risolvere i problemi dei workflow GitHub Actions propri di bx-sites (tests, snapshot, release, docs, pages). |

## Prompt di esempio

Una volta installato, basta descrivere cosa vuoi - il tuo assistente
sceglie autonomamente lo skill giusto:

```text title="Creare l'impalcatura di un nuovo sito"
Scaffold a new bx-sites project called "acme-docs" using the gitbook
theme, add a Getting Started page, and start the dev server.
```

```text title="Scrivere contenuti"
Add a three-step ::: stepper ::: block to docs/getting-started.md walking
through install, scaffold, and build - and a ::: cards ::: grid linking
to the three main guides.
```

```text title="Pubblicarlo"
Add a Netlify deploy target to bxsites.yaml and explain which
environment variable it expects for the auth token.
```

## FAQ

??? faq "Cosa contiene davvero un file SKILL.md?"
    Un breve blocco di frontmatter YAML (`name`, `description` - il
    trigger con cui un assistente confronta) seguito da istruzioni,
    convenzioni ed esempi in Markdown semplice per quell'unico argomento.
    Nessuno script o file di risorse raggruppato - ogni skill di questo
    pacchetto è un singolo file, per progetto, così si installa allo
    stesso modo tramite `npx skills add`, `coldbox ai skills install` e
    `bxSites skills:install`.

??? faq "Mi servono tutti e tredici gli skill?"
    No - ogni via di installazione supporta l'installazione di un solo
    skill per nome (vedi [Installazione](#installazione) sopra). La
    maggior parte dei progetti sta comunque bene installando l'intero
    pacchetto: un assistente carica il contenuto di uno skill solo quando
    un'attività vi corrisponde davvero, quindi gli skill inutilizzati non
    costano nulla in fase di prompt.

??? faq "Con quali assistenti IA funziona?"
    Con qualsiasi assistente che supporti il formato Agent Skills - Claude
    Code, Cursor, Codex e altri. `npx skills add`/`coldbox ai skills
    install` rilevano quali assistenti sono già configurati nel tuo
    progetto e installano automaticamente in ciascuno di essi.

??? faq "Come verifico che uno skill sia stato effettivamente installato?"
    Controlla se esiste un nuovo `SKILL.md` nella directory degli skill
    del tuo assistente (es. `.claude/skills/bx-sites-getting-started/SKILL.md`
    per Claude Code) - oppure chiedi semplicemente al tuo assistente
    qualcosa che lo skill copre (es. "come aggiungo un target di deploy
    Netlify?") e verifica se la sua risposta corrisponde a questa
    documentazione.

??? faq "`bxSites skills:install` è fallito con un errore su npx/Node.js"
    Dietro le quinte esegue il vero `npx skills add`, quindi richiede
    Node.js nel `PATH` allo stesso modo di quel comando - installa Node.js
    da [nodejs.org](https://nodejs.org/) e riprova. Oppure salta del
    tutto il wrapper proprio di `bxSites` ed esegui direttamente
    `npx skills add ortus-boxlang/bx-sites-skills`.

??? faq "Posso installarli in un progetto che non è ancora un progetto bx-sites?"
    Sì - `npx skills add`/`coldbox ai skills install` funzionano in
    qualsiasi directory. `bxSites skills:install` richiede nello
    specifico un `--projectRoot` esistente (la directory corrente per
    impostazione predefinita), come ogni altro verbo `bxSites`, ma quel
    progetto non deve ancora essere stato costruito - installare gli
    skill prima di aver scritto anche una sola pagina è esattamente il
    punto, così il tuo assistente conosce già bx-sites fin dal
    primissimo prompt.

## Fonte

- Repository degli skill: [ortus-boxlang/bx-sites-skills](https://github.com/ortus-boxlang/bx-sites-skills)
- Repository di bx-sites: [ortus-boxlang/bx-sites](https://github.com/ortus-boxlang/bx-sites)
- BoxLang Skills Directory: [skills.boxlang.io](https://skills.boxlang.io/)
