---
title: Blocchi di contenuto
order: 4.5
icon: phosphor-duotone:squares-four
tags: [guide, markdown]
---

# Blocchi di contenuto

Oltre a tutto quanto in [Estensioni Markdown](markdown.md), BxSites
supporta una famiglia di ricchi blocchi di contenuto per cose di cui il
CommonMark puro non ha alcun concetto - card, sequenze a schede di
passaggi, download, embed e altro ancora. Ognuno usa la stessa sintassi
contenitore `::: name ... :::` (un `:::` nudo su una riga a sé chiude
qualsiasi blocco attualmente aperto, oppure scrivi il `:::` di chiusura
direttamente sulla stessa riga per un blocco senza un proprio corpo -
`::: file src="assets/spec.pdf" :::` funziona esattamente come la forma
su due righe) - nessuna configurazione di `bxsites.yaml` necessaria,
sempre disponibile. Un blocco può essere annidato dentro un altro (un
espandibile che contiene un gruppo di card, per esempio) - ognuno viene
analizzato di nuovo per ulteriori blocchi al proprio interno. Stai
migrando da GitBook? Ogni blocco qui corrisponde direttamente al proprio
omologo GitBook dello stesso nome - vedi
[Migrare da GitBook](migrating-from-gitbook.md).

## Espandibile

Una sezione comprimibile semplice - nessuna icona/colore di richiamo, a
differenza di un'ammonizione comprimibile (`???`, vedi
[Ammonizioni](markdown.md#collapsible-admonitions)):

```markdown title="Esempio" linenums="1"
::: expandable "Is this different from a collapsible admonition?"
Yes - this has no type/icon/color, just a plain expand/collapse section.
Add `open="true"` to start it expanded.
:::
```

::: expandable "È diverso da un'ammonizione comprimibile?"
Sì - questa non ha tipo/icona/colore, solo una semplice sezione
espandi/comprimi. Aggiungi `open="true"` per farla iniziare espansa.
:::

## Card

Una griglia di card di collegamento, ognuna un proprio `::: card` dentro
un wrapper `::: cards` - `title`, `icon`, `image` e `href` sono tutti
opzionali (una card senza `href` viene renderizzata come una card
semplice, non cliccabile). `icon` viene risolta allo stesso modo dei
valori `icon` di frontmatter/nav - una semplice emoji, oppure un'icona
con nome da una libreria inclusa
(`icon="phosphor-duotone:rocket-launch"`, `icon="lucide:rocket"`, ...) -
vedi [Icone](icons.md):

```markdown title="Esempio" linenums="1"
::: cards
::: card title="Getting Started" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Install, scaffold and build your first site.
:::
::: card title="Themes" icon="phosphor-duotone:palette" href="themes.md"
Customize a built-in theme or write your own.
:::
:::
```

::: cards
::: card title="Per iniziare" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Installa, genera lo scheletro e compila il tuo primo sito.
:::
::: card title="Temi" icon="phosphor-duotone:palette" href="themes.md"
Personalizza un tema integrato oppure scrivine uno tuo.
:::
:::

## Colonne

Un layout affiancato - `::: column` accetta un `width` opzionale (una
lunghezza/percentuale CSS semplice, ad es. `"40%"`); le colonne senza una
larghezza esplicita condividono la riga in parti uguali:

```markdown title="Esempio" linenums="1"
::: columns
::: column width="60%"
The wider column.
:::
::: column
The narrower one.
:::
:::
```

::: columns
::: column width="60%"
La colonna più larga.
:::
::: column
Quella più stretta.
:::
:::

## Stepper

Una sequenza numerata e collegata di passaggi:

```markdown title="Esempio" linenums="1"
::: stepper
::: step "Install"
`install-bx-module bx-sites`
:::
::: step "Scaffold"
`bxSites new`
:::
:::
```

::: stepper
::: step "Installazione"
`install-bx-module bx-sites`
:::
::: step "Scheletro del progetto"
`bxSites new`
:::
:::

L'attributo opzionale `color` di un passo segna il proprio marcatore con
uno di quattro colori semantici - il predefinito (nessun `color`),
`success`, `warning` o `danger` - indipendentemente dalla posizione del
passo nella sequenza:

```markdown title="Esempio" linenums="1"
::: stepper
::: step "Back up your data" color="success"
Routine, safe to run any time.
:::
::: step "Optional: enable telemetry" color="warning"
Skip this one if you're not sure.
:::
::: step "Delete the old install" color="danger"
Irreversible - make sure the backup above finished first.
:::
:::
```

::: stepper
::: step "Fai un backup dei tuoi dati" color="success"
Operazione di routine, sicura da eseguire in qualsiasi momento.
:::
::: step "Opzionale: attiva la telemetria" color="warning"
Salta questo passo se non sei sicuro.
:::
::: step "Elimina la vecchia installazione" color="danger"
Irreversibile - assicurati che il backup sopra sia terminato per primo.
:::
:::

Il marcatore numerato, la linea di collegamento, e ognuna delle tre
palette `color` sopra sono personalizzabili indipendentemente dal resto
della palette del sito, tramite proprietà CSS personalizzate - vedi
[Personalizzare i colori](themes.md#customizing-colors-without-a-theme-override).

## File

Una card di download per un PDF, un video, o qualsiasi altro asset di
progetto - `src` viene risolto allo stesso modo in cui lo sono già
`theme.logo`/frontmatter `ogImage` (relativo a `docs/assets/`):

```markdown title="Esempio" linenums="1"
::: file src="assets/spec.pdf" title="API Specification" :::
```

::: file src="assets/og-image.png" title="Immagine di anteprima del sito" :::

## Pulsanti

Un pulsante di call-to-action - un `::: button` da solo,
oppure diversi allineati in riga dentro un wrapper `::: buttons`.
L'etichetta iniziale tra virgolette e l'attributo `href` sono le uniche
parti di cui la maggior parte dei pulsanti ha bisogno:

```markdown title="Esempio" linenums="1"
::: button "Get Started" href="../getting-started.md" style="primary" :::
```

::: button "Per iniziare" href="../getting-started.md" style="primary" :::

Alcuni attributi opzionali danno a ogni pulsante le proprie capacità:

- `style="primary"` oppure `style="secondary"` (il valore predefinito) -
  tinta piena vs. contorno.
- `size="small"`, `"medium"` (il valore predefinito) oppure `"large"`.
- `icon="..."` - risolta allo stesso modo dell'`icon` di una card (una
  semplice emoji, oppure un'icona con nome come
  `icon="phosphor-duotone:rocket-launch"` - vedi
  [Temi: Icone](themes.md#icons)).
- `target="_blank"` - apre il link in una nuova scheda invece che nella
  stessa (`rel="noopener noreferrer"` viene aggiunto automaticamente).
- `disabled="true"` - renderizza un pulsante inerte, non cliccabile (non
  serve `href`) per una call-to-action del tipo "prossimamente".

```markdown title="Esempio" linenums="1"
::: buttons
::: button "Read the docs" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Star on GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Coming soon" disabled="true" :::
:::
```

::: buttons
::: button "Leggi la documentazione" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Metti una stella su GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Prossimamente" disabled="true" :::
:::

## Embed

Un embed responsivo in iframe per un provider riconosciuto - attualmente
YouTube, Vimeo, CodePen, Spotify, Loom e Figma. Un URL da qualsiasi altra
fonte ricade su una semplice card di link "visita ↗" invece di un iframe
che si rifiuterebbe comunque di renderizzarsi (la maggior parte dei siti
blocca l'essere incorniciata):

```markdown title="Esempio" linenums="1"
::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::
```

::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="Una demo" :::

## Link a pagina

Una card di anteprima ricca che rimanda a un'altra pagina - `href` segue
la stessa convenzione relativa al file di un normale
[link a pagina](../getting-started.md#linking-between-pages). A
differenza di una card, il suo titolo/icona/riepilogo vengono ricavati
automaticamente dal frontmatter proprio della pagina di destinazione,
così resta sincronizzato se quella pagina viene rinominata o il suo
riepilogo cambia:

```markdown title="Esempio" linenums="1"
::: page-link href="../getting-started.md" :::
```

::: page-link href="../getting-started.md" :::

## Anteprima link

Una card di anteprima ricca per un URL *esterno* - la stessa forma di
card di `::: page-link`, ma per un link che non è una delle pagine del
sito stesso, quindi non c'è alcuna pagina da cui ricavare automaticamente
titolo/riepilogo. Ogni campo proviene dagli attributi propri della
direttiva: solo `url` è obbligatorio, `title` ricade sull'URL nudo quando
omesso, e `description`/`image` sono entrambi opzionali. Non c'è alcun
recupero dell'URL di destinazione al momento del build per riempirli
automaticamente - lo stesso ragionamento che mantiene
[`site:check`](../cli-reference.md#sitecheck) limitato ai soli link interni si
applica anche qui, così un sito di terze parti lento o irraggiungibile
non influisce mai sul tempo di build:

```markdown title="Esempio" linenums="1"
::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::
```

::: link-preview url="https://boxlang.io" title="BoxLang" description="Un linguaggio JVM dinamico e multi-paradigma." :::

## Prompt AI

Un contenitore stilizzato per un prompt AI riutilizzabile. Il corpo del blocco *è* il testo del prompt, scritto come
normale Markdown (quindi intestazioni, elenchi e codice al suo interno
ottengono comunque la propria formattazione); ogni prompt ottiene un
pulsante "Copy" che copia esattamente quel testo sorgente, markup di
formattazione incluso, pronto da incollare in qualsiasi strumento AI tu
stia usando. `description` (un riepilogo opzionale su una riga) e `icon`
(risolta allo stesso modo dell'`icon` propria di `::: card` - per
impostazione predefinita, se omessa, un glifo a stellina) sono entrambi
opzionali:

```markdown title="Esempio" linenums="1"
::: prompt description="Summarizes a pull request for a changelog entry" icon="phosphor-duotone:git-pull-request"
Summarize the following pull request diff as a single changelog entry,
written for an end user rather than a developer. Group related changes
together and skip anything purely internal (refactors, tests, CI).
:::
```

::: prompt description="Summarizes a pull request for a changelog entry" icon="phosphor-duotone:git-pull-request"
Summarize the following pull request diff as a single changelog entry,
written for an end user rather than a developer. Group related changes
together and skip anything purely internal (refactors, tests, CI).
:::

Aggiungi `expanded="preview"` per far vedere un prompt lungo in una
breve anteprima con dissolvenza, finché chi legge non fa clic su "Show
more", oppure `expanded="hidden"` per farlo iniziare completamente
compresso dietro un pulsante "Show prompt" - comodo per una pagina che
elenca più prompt uno di seguito all'altro. Ometti `expanded` (oppure
impostalo su `"full"`, il valore predefinito) per mostrare sempre il
prompt per intero:

```markdown title="Esempio" linenums="1"
::: prompt description="A longer, multi-step prompt" expanded="preview"
1. Read the attached error log line by line.
2. For each stack trace, identify the failing module.
3. Group failures by root cause, not by timestamp.
4. Propose one fix per root cause, not per failure.
5. Skip anything that already has an open issue - list those separately.
:::
```

::: prompt description="A longer, multi-step prompt" expanded="preview"
1. Read the attached error log line by line.
2. For each stack trace, identify the failing module.
3. Group failures by root cause, not by timestamp.
4. Propose one fix per root cause, not per failure.
5. Skip anything that already has an open issue - list those separately.
:::

Qui non c'è alcun menu "Open in AI providers" - bx-sites non comunica mai
con un provider AI di terze parti, quindi il pulsante "Copy" proprio di un
prompt è l'unico modo per portarlo nello strumento che stai usando.

## Aggiornamenti (changelog)

Una lista di changelog datata e taggabile - `::: update` accetta
`date="YYYY-MM-DD"` e un `tags` opzionale separato da virgole:

```markdown title="Esempio" linenums="1"
::: updates
::: update date="2026-01-15" tags="feature,fix"
Added dark mode and fixed a footer alignment bug.
:::
::: update date="2026-01-01"
Initial release.
:::
:::
```

::: updates
::: update date="2026-01-15" tags="funzionalità,correzione"
Aggiunta la modalità scura e corretto un bug di allineamento del footer.
:::
::: update date="2026-01-01"
Prima release.
:::
:::

Una pagina con un blocco `::: updates` ottiene anche il proprio
`feed.xml` (RSS 2.0) scritto accanto a sé una volta che `baseURL` di
`bxsites.yaml` è un URL completo - lo stesso requisito di `sitemap.xml` -
così i lettori possono iscriversi solo al changelog di quella pagina.

## Contenuto riutilizzabile (include)

`::: include src="..."` inserisce il Markdown grezzo di un altro file in
quel punto. A differenza di ogni blocco sopra, questo diventa vero
contenuto di pagina (intestazioni, paragrafi, i propri blocchi
annidati), non qualcosa avvolto in un widget - utile per un avviso/nota
ripetuto su più pagine. Metti il partial stesso sotto `docs/includes/` -
la stessa convenzione di cartella riservata di `assets/`/`versions/`/
`i18n/`/`blog/`. Un file sotto `includes/` non viene mai compilato come
propria pagina e non compare mai in nav/ricerca/sitemap/tag - esiste solo
per essere inserito in altre pagine:

```text title="Struttura di docs/"
docs/
├── index.md
├── includes/
│   ├── beta-notice.md
│   └── legal/
│       └── terms.md
└── guides/
    └── deep/
        └── setup.md
```

Uno `src` **nudo** (senza `./` o `../` iniziale) si risolve sempre
rispetto al proprio `docs/includes/` dell'albero corrente, non importa
quanto in profondità sia annidata la pagina che include - `guides/deep/setup.md`
sopra raggiunge lo stesso file che raggiunge `index.md`, entrambi con
esattamente lo stesso `src`:

```markdown title="Da index.md oppure da guides/deep/setup.md"
::: include src="beta-notice.md"
```

Uno `src` nudo può anche puntare a una sottocartella di `includes/`
stessa:

```markdown title="Esempio"
::: include src="legal/terms.md"
```

Anteponi invece `./` o `../` a `src` per raggiungere un frammento
adiacente alla pagina che non è pensato per vivere nella cartella
centralizzata `includes/` - quella forma si risolve in modo relativo al
file rispetto alla cartella propria della pagina *che include*, la
stessa convenzione di un normale link a pagina:

```markdown title="Da guides/deep/setup.md, un livello sopra invece che centralizzato"
::: include src="../local-note.md"
```

Un albero versione/locale ottiene il proprio `includes/` allo stesso
modo - una pagina sotto `docs/versions/2.0/` risolve uno `src` nudo
rispetto a `docs/versions/2.0/includes/`, e una sotto `docs/i18n/es/`
rispetto a `docs/i18n/es/includes/` - i partial di ogni albero sono
propri, non condivisi con il `docs/includes/` dell'albero principale.

Un file incluso può a sua volta includerne un altro (una catena
circolare genera `BxSites.CircularInclude` al momento del build invece
di ripetersi all'infinito).

## Contenuto condizionale

Mostra una tra più varianti di un blocco in base a una scelta fatta dal
lettore stesso - istruzioni "Free" contro "Pro" nella stessa pagina, ad
esempio. Questo è un sito interamente statico senza alcuna identità del
visitatore, quindi a differenza di una piattaforma con un vero backend,
non c'è un "chi è questo lettore" valutato lato server - è il lettore
stesso a scegliere, e la sua scelta viene semplicemente ricordata nel
proprio browser (`localStorage`) anche per ogni pagina successiva:

```markdown title="Esempio" linenums="1"
::: audience-switcher key="plan" options="free:Free,pro:Pro" :::

::: conditional key="plan" value="free"
The Free plan includes basic search.
:::

::: conditional key="plan" value="pro"
The Pro plan adds AI-assisted search and unlimited team seats.
:::
```

::: audience-switcher key="plan" options="free:Free,pro:Pro" :::

::: conditional key="plan" value="free"
The Free plan includes basic search.
:::

::: conditional key="plan" value="pro"
The Pro plan adds AI-assisted search and unlimited team seats.
:::

`::: conditional key="..." value="..."` segna una variante; `key` è
qualsiasi nome di preferenza tu stia usando per commutare (`"plan"` qui
sopra, ma potrebbe altrettanto bene essere `"os"`, `"language"`,
qualsiasi cosa), e `value` è l'impostazione per cui questo particolare
blocco deve essere mostrato. Ogni variante viene sempre renderizzata
nell'HTML - nascosta lato client, mai omessa - così un lettore con
JavaScript disattivato (o un crawler di ricerca) vede comunque ogni
variante invece di nessuna.

`::: audience-switcher key="..." options="valore:Etichetta,valore:Etichetta,..."`
è un controllo opzionale, già pronto all'uso - un pulsante per ogni
opzione, che commuta immediatamente ogni blocco `::: conditional` che
condivide quella stessa `key`, ovunque nella pagina. Non ti serve affatto:
un link che termina con `?plan=pro` imposta automaticamente la stessa
preferenza al caricamento (comodo per condividere un link diretto verso
"la versione Pro di questa pagina"), e una sovrascrittura di tema propria
di un progetto può chiamare direttamente
`window.bxSitesSetPreference( key, value )` per pilotarla da
un'interfaccia personalizzata invece.

## Loop e condizionale (basati sui dati)

`::: for` e `::: if` renderizzano il proprio contenuto rispetto ai
[dati riutilizzabili](data-files.md) - il valore proprio di un file
`docs/data/*.yaml`/`.json`, indirizzato per percorso puntato. A differenza
di ogni blocco sopra, questi due accettano un'espressione nuda invece di
attributi `key="value"` - deliberatamente ristretti, la stessa filosofia
basata solo sul percorso puntato che `{{ }}` stesso già usa (nessun
operatore di confronto in questa prima versione):

```markdown title="Esempio" linenums="1"
::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::
```

::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::

`::: for <item>, <index> in <dotted.path>` associa `<item>`/`<index>`
nello stesso modo in cui lo fa il loop `for` a due variabili proprio di
BoxLang, qualunque cosa risolva il percorso - elemento + indice a base 1
per un array (come sopra), oppure chiave + valore per uno struct, con la
sintassi identica in entrambi i casi:

```markdown title="Esempio" linenums="1"
::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::
```

::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::

`::: if <dotted.path>` renderizza il proprio contenuto solo quando il
valore risolto è veritiero - un array/struct/stringa vuoto, `0` e `false`
contano tutti come falsi:

```markdown title="Esempio" linenums="1"
::: if data.flags.betaBanner
Beta features are enabled on this build.
:::
```

::: if data.flags.betaBanner
Le funzionalità beta sono attive in questa build.
:::

Concatena `::: elseif <dotted.path>` (un numero qualsiasi) e un
`::: else` nudo finale dopo un `::: if` per una vera semantica
`if`/`elseif`/`else` - la prima condizione veritiera vince, `::: else`
(senza una propria condizione) cattura tutto ciò che resta, e una
condizione successiva a quella vincente non viene nemmeno risolta, quindi
un percorso `::: elseif` con un errore di battitura interrompe il build
solo una volta che il proprio ramo viene effettivamente raggiunto.
L'intera catena si chiude con **un solo** `:::` finale - `::: elseif`/
`::: else` stessi segnano dove finisce il ramo precedente, quindi non
serve alcun `:::` prima di ciascuno di essi:

```markdown title="Esempio" linenums="1"
::: if data.flags.darkModeDefault
Dark mode is on by default.
::: elseif data.flags.betaBanner
Beta features are enabled, though dark mode isn't on by default.
::: else
Nothing special about this build.
:::
```

::: if data.flags.darkModeDefault
La modalità scura è attiva per impostazione predefinita.
::: elseif data.flags.betaBanner
Le funzionalità beta sono attive, anche se la modalità scura non è attiva
per impostazione predefinita.
::: else
Niente di speciale in questa build.
:::

Un `:::` prima di un `::: elseif`/`::: else` funziona comunque, se
preferisci chiudere esplicitamente ogni ramo - entrambe le forme vengono
analizzate in modo identico.

Entrambi i corpi possono contenere normale Markdown e persino altri
blocchi di contenuto - incluso un altro `::: for`/`::: if`, annidato
esattamente come qualsiasi blocco sopra. Vedi
[File di dati: Usare i dati](data-files.md#consuming-data) per il quadro
completo di loop/condizionali, inclusi gli altri due modi per lavorare
con `data.*` - una sovrascrittura di tema, oppure una funzione magica.

## Indice del corso

`::: course id="..." :::` renderizza tutte le lezioni di un intero
[corso](courses.md) come un unico indice numerato e collegato - un grande
elenco "1. Introduzione, 2. Installazione su Windows, 3. Installazione su
Mac..." in cui ogni numero è un link vero e proprio, costruito a partire
da un manifest `docs/data/courses.yaml` invece che redatto a mano:

```markdown title="Esempio" linenums="1"
::: course id="getting-started" :::
```

A differenza di ogni blocco sopra, questo accetta solo un `id` nudo,
senza un proprio `href`/corpo di contenuto - le lezioni stesse, e il loro
ordine, provengono interamente dal manifest. Vedi [Corsi](courses.md) per
il formato del manifest, la navigazione lezione-per-lezione limitata al
corso, e come vengono tracciati i progressi del lettore una volta che
l'indice è presente sulla pagina.
