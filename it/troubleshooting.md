---
title: Risoluzione dei problemi
order: 2.4
icon: phosphor-duotone:lifebuoy
summary: Diagnostica i problemi più comuni di setup, build e serve - e dove trovare aiuto se questa pagina non li copre.
tags: [risoluzione-problemi, faq]
---

# Risoluzione dei problemi

## Esegui prima `doctor`

Prima di approfondire, esegui il controllo di salute integrato - risolve
la maggior parte dei problemi di questa pagina in un solo colpo:

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Controlla la versione della JVM, che `docs/` (o `src/`) esista, che
`bxsites.yaml`/`.json` venga effettivamente analizzato e validato, che i
moduli BoxLang richiesti siano installati e attivi, e - se esiste un
override `theme/` a livello di progetto - che soddisfi il contratto del
tema. Termina con codice `1` se un controllo fallisce e stampa cosa non
va; nulla di tutto ciò modifica il tuo progetto.

## Problemi comuni

??? bug "`No docs/ directory found`"
    `build`/`serve`/`audit`/ecc. cercano `docs/` (con fallback su `src/`)
    relativamente alla directory corrente, o a `--projectRoot=<path>` se
    specificato. Esegui il comando dalla cartella radice del progetto,
    oppure passa `--projectRoot`:

    ```bash frame="terminal" title="Terminal"
    bxSites build --projectRoot=/percorso/ai/miei-docs
    ```

??? bug "`bxsites.yaml`/`.json` non viene analizzato o validato"
    Esegui `bxSites doctor` per vedere esattamente quale chiave/riga ha
    rifiutato il config loader. Cause comuni: mescolare tab e spazi
    nell'indentazione YAML, una virgola in eccesso nel JSON, o una
    chiave che si aspetta un array (come `nav` o `i18n.locales`) scritta
    come semplice stringa. Vedi [Configurazione](configuration.md) per
    il riferimento completo delle chiavi.

??? bug "`bx-markdown`/`bx-esapi`/`bx-yaml`/`bx-image` non installato/attivo"
    `build`, `serve` e `search-index` richiedono tutti e quattro questi
    moduli BoxLang. Installare `bx-sites` stesso li installa
    automaticamente come dipendenze di `box.json`
    (`install-bx-module bx-sites` oppure `box install bx-sites`) - se
    vedi questo errore, o l'installazione non è andata a buon fine, o il
    modulo è stato registrato manualmente senza le sue dipendenze.
    Rieseguire `box install` dalla radice del progetto risolve tutto di
    nuovo; `bxSites doctor` conferma quale modulo (se presente) manca
    ancora.

??? bug "Un override `theme/` di progetto non si costruisce"
    Una cartella `theme/` personalizzata deve fornire sia `layout.bxm`
    che `page.bxm` - `doctor` segnala quale manca. Vedi
    [Temi](guides/themes.md) per il contratto completo, oppure esegui
    `bxSites theme:new` per esportare un tema integrato come punto di
    partenza funzionante invece di scriverne uno da zero.

??? bug "`serve` non rileva una modifica"
    `serve` osserva `docs/`, il tuo `bxsites.yaml`/`.json`, e un override
    `theme/` a livello di progetto - una modifica altrove (ad esempio
    modificare un file sotto `resources/` in un checkout di un modulo,
    non un progetto reale) non attiva una ricostruzione. Se una modifica
    reale non viene comunque riflessa, ferma `serve`, esegui
    `bxSites clean` per svuotare qualsiasi cache di build obsoleta, e
    riavvia `bxSites serve`.

??? bug "Una build sembra obsoleta, o CI segnala successo ma nulla è cambiato"
    `build` non elimina l'output precedentemente costruito che non ha
    più una pagina sorgente corrispondente. Esegui `bxSites clean` prima
    di `build` per rimuovere completamente `site/` e qualsiasi cache di
    build, poi ricostruisci da zero. Se uno step CI segnala successo ma
    il sito distribuito non lo riflette, controlla il log effettivo dello
    step di build cercando `Error:` - una build che va in crash può
    comunque restituire uno stato di successo fuorviante in alcune
    configurazioni CI.

??? bug "Una pagina tradotta mostra un avviso di pagina non tradotta"
    È previsto, non un bug: una lingua non deve avere ogni pagina
    tradotta per essere utilizzabile. Una pagina mancante in
    `docs/i18n/<codice>/` viene comunque costruita al suo URL previsto,
    mostrando il contenuto della lingua predefinita con un piccolo
    avviso in cima alla pagina. Vedi
    [Internazionalizzazione (i18n)](guides/i18n.md).

??? bug "`i18n:status` segnala 100% ma una traduzione sembra comunque non aggiornata"
    `i18n:status` verifica solo la *presenza* della pagina per lingua,
    non la parità di contenuto per pagina - una copia in una lingua può
    esistere ma mancare comunque di una sezione aggiunta successivamente
    alla pagina della lingua predefinita. Confronta direttamente il file
    della lingua con la sua controparte nella lingua predefinita se
    sospetti questo.

## Ancora bloccato?

Se nulla di quanto sopra copre il tuo caso, contatta uno dei seguenti
canali di supporto - vedi [Contribuisci](contribute.md) per l'elenco
completo:

::: cards
::: card title="Ortus Community Discourse" icon="phosphor-duotone:chats-circle" href="https://community.ortussolutions.com"
Fai domande e cerca tra le discussioni esistenti.
:::
::: card title="Box Slack Team" icon="phosphor-duotone:slack-logo" href="https://boxteam.slack.com"
Chatta in tempo reale con la community e i maintainer.
:::
::: card title="Apri un issue" icon="phosphor-duotone:bug" href="https://github.com/ortus-boxlang/bx-sites/issues"
Per un bug riproducibile, allegando l'output di `bxSites doctor`.
:::
::: card title="Supporto professionale" icon="phosphor-duotone:handshake" href="services.md"
Preferisci che sia il team dietro BxSites a risolverlo per te? Ortus
Solutions offre supporto professionale e consulenza.
:::
:::
