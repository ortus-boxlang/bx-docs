---
title: Distribuzione
order: 3
icon: phosphor-duotone:cloud-arrow-up
tags: [guide, distribuzione]
---

# Distribuzione

`site/` è un semplice sito statico - ospitalo ovunque vengano serviti
file statici. [`bxSites deploy`](../cli-reference.md#deploy) lo invia lì
direttamente, con un solo comando: S3 (e qualsiasi servizio compatibile
con S3 - DigitalOcean Spaces, Cloudflare R2, Backblaze B2, MinIO), Azure
Blob Storage, Google Cloud Storage, Firebase Hosting, FTP, SFTP, rsync
via SSH, Netlify, Vercel, Cloudflare Pages, una directory locale, oppure
GitHub Pages.

## Il comando `deploy`

Ogni target tranne i due più semplici (`local`/`github-pages`, che
funzionano soltanto con dei flag - vedi
[Riferimento CLI](../cli-reference.md#deploy)) viene configurato tramite
un file `deployments/<nome>.json` nella radice del progetto - un file per
ogni target di deploy che usi realmente, ciascuno indicando quale
`target` è, oltre ai campi propri di quel target:

```bash frame="terminal" title="Terminal"
bxSites deploy --entry=production
```

```json title="deployments/production.json"
{ "target": "s3", "bucket": "my-docs-site", "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID", "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY" }
```

**I segreti provengono sempre da una variabile d'ambiente, mai da un
valore letterale in `deployments/*.json`.** Ogni campo che termina in
`EnvVar` indica il nome della *variabile d'ambiente* che contiene il
segreto vero e proprio (una chiave d'accesso, una password, un token
API) - risolta dal vivo al momento del deploy, quindi `deployments/*.json`
di per sé è sempre sicuro da mettere sotto controllo di versione. Un
campo che è invece un *percorso* verso un file di credenziali che già
gestisci tu stesso (una chiave privata SSH, una chiave JSON di service
account GCP scaricata) è la sola eccezione - un campo semplice, dato che
è il file stesso a dover restare fuori dal controllo di versione, non il
suo percorso. In locale, quelle variabili d'ambiente possono provenire
anche da un file `.env` (BoxLang ne carica uno automaticamente e
`getSystemSetting()` - quello che ogni target usa per risolverle - lo
controlla in modo trasparente) invece di esportarle a mano nella tua
shell; in CI, impostale come veri segreti sul runner.

### Distribuire su tutti i target contemporaneamente

Esegui `bxSites deploy` senza `--entry` né `--target` e ogni voce
`deployments/*.json` viene distribuita a turno, a partire da un'unica
build condivisa:

```bash frame="terminal" title="Terminal"
bxSites deploy
```

Il sito viene compilato una sola volta, indipendentemente da quante voci
hai. Il fallimento di un target non ferma gli altri - ogni voce viene
tentata, e il comando esce con un codice diverso da zero solo se almeno
una di esse è fallita; il riepilogo riporta quante sono andate a buon
fine (ad es. `Deployed to 2/3 target(s) (1 failed)`). Aggiungi
`--verbose` (funziona anche con `--entry`/`--target`) per stampare una
riga di avanzamento quando la build e ciascun target iniziano e
finiscono, invece del solo riepilogo finale.

### `local`

Copia il sito compilato in qualsiasi directory - un'unità condivisa, una
cartella di staging, ovunque. È l'unico target che non necessita affatto
di una voce in `deployments/`.

```bash frame="terminal" title="Terminal"
bxSites deploy --target=local --destination=/percorso/di/destinazione
```

### `github-pages`

Lo stesso push che fa [`gh-deploy`](../cli-reference.md#gh-deploy),
raggiungibile anche da questo comando unificato - anch'esso non
necessita di alcuna voce in `deployments/`:

```bash frame="terminal" title="Terminal"
bxSites deploy --target=github-pages [--branch=gh-pages] [--remote=origin] [--message="..."]
```

### `s3`

Vero AWS S3, oppure qualsiasi servizio compatibile con S3 - imposta
`endpoint` per qualsiasi cosa diversa da AWS stesso, e
`forcePathStyle: true` per la maggior parte dei provider non-AWS.

```json title="deployments/production.json"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "region": "us-east-1",
  "prefix": "",
  "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID",
  "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY"
}
```

```json title="deployments/spaces.json (DigitalOcean Spaces)"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "endpoint": "https://nyc3.digitaloceanspaces.com",
  "forcePathStyle": true,
  "accessKeyIdEnvVar": "SPACES_KEY",
  "secretAccessKeyEnvVar": "SPACES_SECRET"
}
```

La stessa struttura (`endpoint` personalizzato + `forcePathStyle: true`)
copre anche Cloudflare R2 (`https://<accountid>.r2.cloudflarestorage.com`),
Backblaze B2, e MinIO/Wasabi.

### `azure`

Azure Blob Storage, autenticato con un token SAS, una chiave d'account, o
una connection string completa - esattamente uno dei tre.

```json title="deployments/production.json"
{
  "target": "azure",
  "account": "mystorageaccount",
  "container": "site",
  "accountKeyEnvVar": "AZURE_STORAGE_KEY"
}
```

### `gcs`

Google Cloud Storage, autenticato con una chiave JSON di service account
scaricata (Google Cloud Console -> IAM & Admin -> Service Accounts ->
Keys).

```json title="deployments/production.json"
{
  "target": "gcs",
  "bucket": "my-docs-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `firebase`

Firebase Hosting, che utilizza lo stesso tipo di chiave di service
account di `gcs`.

```json title="deployments/production.json"
{
  "target": "firebase",
  "siteId": "my-firebase-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `ftp` / `sftp`

Carica l'intero sito su un server remoto via FTP o SFTP, preservandone la
struttura delle cartelle. SFTP accetta una password oppure una chiave
SSH.

```json title="deployments/production.json"
{
  "target": "sftp",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "key": "/home/me/.ssh/id_rsa"
}
```

### `rsync`

Sincronizza il sito con un server remoto via SSH usando il vero binario
`rsync` - più veloce di FTP/SFTP per una ricompilazione completa, dato
che trasferisce solo ciò che è cambiato. Richiede `rsync` e `ssh` sulla
macchina che esegue `bxSites`.

```json title="deployments/production.json"
{
  "target": "rsync",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "identityFile": "/home/me/.ssh/id_rsa"
}
```

### `netlify`

```json title="deployments/production.json"
{
  "target": "netlify",
  "siteId": "my-site-id-or-name.netlify.app",
  "authTokenEnvVar": "NETLIFY_AUTH_TOKEN"
}
```

### `vercel`

```json title="deployments/production.json"
{
  "target": "vercel",
  "projectId": "my-project",
  "authTokenEnvVar": "VERCEL_TOKEN"
}
```

### `cloudflare-pages`

Cloudflare non dispone di un'API REST ufficialmente documentata per i
deploy a caricamento diretto - solo della propria CLI `wrangler`. Questo
target replica, per ingegneria inversa, lo stesso flusso di upload di
Wrangler, e richiede un'implementazione dell'hash BLAKE3 nel classpath
della JVM che la maggior parte delle installazioni Java predefinite non
include - vedi [Riferimento CLI](../cli-reference.md#deploy) e il codice
sorgente stesso del target per il quadro completo e onesto sulle
asperità di questo target.

```json title="deployments/production.json"
{
  "target": "cloudflare-pages",
  "accountId": "your-account-id",
  "projectName": "my-project",
  "apiTokenEnvVar": "CLOUDFLARE_API_TOKEN"
}
```

## Il comando `package`

Preferisci un semplice archivio rispetto a uno qualsiasi dei target qui
sopra - allegare una build a una release GitHub, consegnarla a un host
che accetta solo il caricamento di uno zip, oppure spedirla in un posto
che nessuno dei target collegabili raggiunge?
[`bxSites package`](../cli-reference.md#package) compila il sito, poi lo
comprime in un unico file la cui radice è il contenuto stesso del sito
compilato (non una cartella `site/` che lo racchiude):

```bash frame="terminal" title="Terminal"
bxSites package
bxSites package --output=dist/my-site.zip
```

`--output` ha come valore predefinito `<projectRoot>/site.zip`; un
valore relativo viene risolto rispetto alla radice del progetto, e le
sue cartelle padre vengono create automaticamente se non esistono già.

## GitHub Actions (pubblicazione multi-versione)

Per la pubblicazione automatica a ogni push, invece di un
`bxSites deploy`/`gh-deploy` eseguito manualmente, questo modulo include
un workflow GitHub Actions già pronto all'uso
(`.github/workflows/pages.yml`) che pubblica `main` e `development` come
due versioni dello stesso sito su GitHub Pages, live in modo indipendente.
Il resto di questa guida copre questo workflow, usato dalla
documentazione di questo stesso repository.

## Cosa fa

A ogni push su `main` o `development` che tocca `docs/`, `bxsites.yaml`, o
il sorgente stesso del modulo (modifiche a tema/pipeline), il workflow:

1. Installa BoxLang + [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
2. Registra questo repository come modulo così che `boxlang bxSites build` si risolva
3. Su qualsiasi branch diverso da `main`, punta `baseURL` a
   `.../<nome-branch>/` solo per questo build (vedi
   [sotto](#publishing-two-versions-at-once))
4. Esegue `boxlang bxSites build`
5. Invia `site/` al branch `gh-pages` - `main` alla radice del sito,
   `development` a `/development/` - senza toccare l'altra versione

È disponibile anche come trigger manuale (`workflow_dispatch`) dalla
scheda Actions, per una ripubblicazione una tantum senza un nuovo commit.

## Configurazione iniziale

GitHub Pages deve essere puntato sul branch `gh-pages` prima che il
workflow possa pubblicare qualcosa - questa è un'impostazione del
repository, non qualcosa che un file di workflow può attivare da solo. La
prima esecuzione riuscita crea `gh-pages` per te, quindi fai questo
*dopo* che il workflow è stato eseguito almeno una volta:

1. **Settings -> Pages**
2. Sotto **Build and deployment -> Source**, scegli **Deploy from a branch**
3. Sotto **Branch**, scegli **gh-pages** e **/ (root)**

Dopo di che, ogni push corrispondente compila e distribuisce
automaticamente. L'URL pubblicato compare sotto **Settings -> Pages** una
volta completato il primo deploy.

## Pubblicare due versioni contemporaneamente

`main` pubblica sulla radice del sito
(`https://<user>.github.io/<repo>/`) - trattala come la documentazione
stabile/rilasciata. `development` pubblica su `/development/`
(`https://<user>.github.io/<repo>/development/`) - la documentazione più
recente, non ancora rilasciata. Entrambe restano live simultaneamente:
il job di ciascun branch effettua il push su `gh-pages` solo con
`keep_files: true` e la propria `destination_dir`, quindi un deploy di
`development` non sovrascrive mai il contenuto di `main` e viceversa.

Il `bxsites.yaml` proprio di `main` dovrebbe avere `baseURL` impostato
sulla radice del sito (`https://<user>.github.io/<repo>/`); il workflow
lo sovrascrive per ogni altro branch al momento del build, quindi il
`bxsites.yaml` di `development` non ha bisogno di una propria voce
`baseURL` perché tutto funzioni.

Per aggiungere un terzo branch (ad es. un'anteprima `release/2.0`),
aggiungilo all'elenco `on.push.branches` e dagli un proprio passo di
deploy con `if: github.ref_name == '...'` e
`destination_dir: release-2.0` (o simile) - lo schema è lo stesso di
quello di `development`.

## Usare questo per il tuo progetto

Copia `.github/workflows/pages.yml` nel tuo progetto (modifica la riga
`modules:` se il tuo progetto necessita di qualcosa oltre a
`bx-markdown`), attiva Pages come sopra, e i push su `main`/`development`
pubblicheranno allo stesso modo. Se vuoi solo una singola versione
pubblicata, elimina il branch che non ti serve da `on.push.branches` e il
relativo passo di deploy.

## Servire da un sotto-percorso di Pages di progetto

Un sito GitHub Pages di *progetto* (a differenza di un sito *utente*
`<user>.github.io`) viene servito da
`https://<user>.github.io/<repo>/`, non dalla radice del dominio. Imposta
`baseURL` in `bxsites.yaml` su quell'URL completo così che ogni link
interno, asset e voce di nav ottenga il prefisso `/<repo>/` di cui ha
bisogno - e così che venga generato anche un vero `sitemap.xml`:

=== "YAML"
    ```yaml title="bxsites.yaml"
    baseURL: "https://<user>.github.io/<repo>/"
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "baseURL": "https://<user>.github.io/<repo>/" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    baseURL = "https://<user>.github.io/<repo>/"
    ```

Vedi [Configurazione](../configuration.md#baseurl) per il quadro completo
di cosa fa `baseURL`. Un sito utente `<user>.github.io`, o qualsiasi
dominio personalizzato mappato sulla radice del sito, può lasciare
`baseURL` al suo valore predefinito (`/`).

## Limitare chi può raggiungere il tuo sito

Qui non c'è alcun controllo degli accessi integrato - bx-sites produce
sempre e soltanto un semplice `site/` statico, e un file statico non ha
alcun concetto di "chi sta chiedendo". Il `robots: false` di
[`bxsites.json`](../configuration.md#robotstxt) dice ai crawler ben
educati di non indicizzare un build (utile per un deploy di
staging/anteprima che non vuoi veder comparire nei risultati di ricerca),
ma è una richiesta cortese, non un lucchetto - l'URL continua a
funzionare per chiunque lo abbia. Se hai davvero bisogno di limitare
l'accesso, questo deve avvenire davanti ai file statici, a qualunque host
li stia servendo - alcune opzioni comuni, adatte a siti statici:

- **Cloudflare Pages/Access** - metti il sito distribuito dietro una
  policy [Cloudflare Access](https://developers.cloudflare.com/cloudflare-one/policies/access/)
  (lista email consentite, SSO, o un PIN monouso), senza bisogno di
  codice applicativo.
- **Netlify** - [protezione con password](https://docs.netlify.com/manage/security/secure-access-to-sites/site-protection/)
  integrata, per sito o per singolo deploy, dalle sole impostazioni del
  sito.
- **Un piccolo reverse-proxy** (qualsiasi host) - l'HTTP Basic Auth
  davanti ai file statici (una regola in stile `.htpasswd`, oppure una
  Cloudflare Worker/Netlify Edge Function di un solo file) basta per
  "tenere fuori i motori di ricerca e i visitatori a caso", anche se non
  è una vera identità per-utente come l'avrebbe un'app con login.

Nessuna di queste è una funzionalità di bx-sites - sono impostazioni a
livello di host, che attivi ovunque finisca per essere servito `site/`.
