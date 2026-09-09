---
title: Deployment
order: 3
icon: phosphor-duotone:cloud-arrow-up
tags: [anleitungen, deployment]
---

# Deployment

`site/` ist eine reine statische Website - hoste sie überall, wo statische
Dateien ausgeliefert werden können. [`bxSites deploy`](../cli-reference.md#deploy)
bringt sie mit einem einzigen Befehl direkt dorthin: S3 (und jeder
S3-kompatible Dienst - DigitalOcean Spaces, Cloudflare R2, Backblaze B2,
MinIO), Azure Blob Storage, Google Cloud Storage, Firebase Hosting, FTP,
SFTP, rsync über SSH, Netlify, Vercel, Cloudflare Pages, ein lokales
Verzeichnis oder GitHub Pages.

## Der Befehl `deploy`

Jedes Ziel außer den beiden einfachsten (`local`/`github-pages`, die allein
mit Flags auskommen - siehe [CLI-Referenz](../cli-reference.md#deploy))
wird über eine `deployments/<name>.json`-Datei im Projekt-Wurzelverzeichnis
konfiguriert - eine Datei pro tatsächlich genutztem Deploy-Ziel, jede
benennt, welches `target` es ist, plus die eigenen Felder dieses Ziels:

```bash frame="terminal" title="Terminal"
bxSites deploy --entry=production
```

```json title="deployments/production.json"
{ "target": "s3", "bucket": "my-docs-site", "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID", "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY" }
```

**Secrets kommen immer aus einer Umgebungsvariable, nie aus einem
literalen Wert in `deployments/*.json`.** Jedes Feld, das auf `EnvVar`
endet, benennt die *Umgebungsvariable*, die das eigentliche Secret enthält
(ein Access Key, ein Passwort, ein API-Token) - zur Deploy-Zeit live
aufgelöst, sodass `deployments/*.json` selbst immer gefahrlos committet
werden kann. Ein Feld, das ein *Pfad* zu einer Credential-Datei ist, die
du bereits selbst verwaltest (ein privater SSH-Schlüssel, ein
heruntergeladener GCP-Service-Account-JSON-Key), ist die eine Ausnahme -
ein einfaches Feld, da die Datei selbst aus der Versionskontrolle
herausgehalten wird, nicht ihr Pfad. Lokal können diese
Umgebungsvariablen auch aus einer `.env`-Datei stammen (BoxLang lädt
automatisch eine, und `getSystemSetting()` - was jedes Ziel zu ihrer
Auflösung nutzt - prüft sie transparent), statt sie von Hand in deine
Shell zu exportieren; in CI werden sie als echte Secrets auf dem Runner
gesetzt.

### Alle Ziele auf einmal deployen

`bxSites deploy` ohne `--entry` und ohne `--target` ausgeführt, deployt
jeden `deployments/*.json`-Eintrag der Reihe nach, ausgehend von einem
einzigen gemeinsamen Build:

```bash frame="terminal" title="Terminal"
bxSites deploy
```

Die Website wird nur einmal gebaut, egal wie viele Einträge du hast.
Schlägt ein Ziel fehl, stoppt das die übrigen nicht - jeder Eintrag wird
versucht, und der Befehl liefert nur dann einen von null verschiedenen
Exit-Code, wenn mindestens einer davon fehlgeschlagen ist; die
Zusammenfassung meldet, wie viele erfolgreich waren (z. B. `Deployed to
2/3 target(s) (1 failed)`). Mit `--verbose` (funktioniert auch mit
`--entry`/`--target`) wird eine Fortschrittszeile ausgegeben, wenn der
Build und jedes Ziel starten und enden, statt nur der abschließenden
Zusammenfassung.

### `local`

Kopiert die gebaute Website in ein beliebiges Verzeichnis - ein
Netzlaufwerk, einen Staging-Ordner, egal wohin. Das einzige Ziel, das
überhaupt keinen `deployments/`-Eintrag braucht.

```bash frame="terminal" title="Terminal"
bxSites deploy --target=local --destination=/path/to/somewhere
```

### `github-pages`

Derselbe Push, den [`gh-deploy`](../cli-reference.md#gh-deploy) macht,
auch über diesen einheitlichen Befehl erreichbar - braucht ebenfalls
keinen `deployments/`-Eintrag:

```bash frame="terminal" title="Terminal"
bxSites deploy --target=github-pages [--branch=gh-pages] [--remote=origin] [--message="..."]
```

### `s3`

Echtes AWS S3, oder jeder S3-kompatible Dienst - setze `endpoint` für
alles außer AWS selbst, und `forcePathStyle: true` für die meisten
Nicht-AWS-Provider.

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

Dieselbe Form (eigener `endpoint` + `forcePathStyle: true`) deckt auch
Cloudflare R2 (`https://<accountid>.r2.cloudflarestorage.com`), Backblaze
B2 und MinIO/Wasabi ab.

### `azure`

Azure Blob Storage, authentifiziert mit einem SAS-Token, einem
Account-Key oder einem vollständigen Connection String - genau eines der
drei.

```json title="deployments/production.json"
{
  "target": "azure",
  "account": "mystorageaccount",
  "container": "site",
  "accountKeyEnvVar": "AZURE_STORAGE_KEY"
}
```

### `gcs`

Google Cloud Storage, authentifiziert mit einem heruntergeladenen
Service-Account-JSON-Key (Google Cloud Console -> IAM & Admin -> Service
Accounts -> Keys).

```json title="deployments/production.json"
{
  "target": "gcs",
  "bucket": "my-docs-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `firebase`

Firebase Hosting, mit derselben Art von Service-Account-Key wie `gcs`.

```json title="deployments/production.json"
{
  "target": "firebase",
  "siteId": "my-firebase-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `ftp` / `sftp`

Lädt die gesamte Website per FTP oder SFTP auf einen entfernten Server
hoch, unter Beibehaltung ihrer Ordnerstruktur. SFTP akzeptiert ein
Passwort oder einen SSH-Schlüssel.

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

Synchronisiert die Website per SSH mit der echten `rsync`-Binary auf
einen entfernten Server - schneller als FTP/SFTP bei einem vollständigen
Rebuild, da nur die Änderungen übertragen werden. Erfordert `rsync` und
`ssh` auf der Maschine, die `bxSites` ausführt.

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

Cloudflare hat keine offiziell dokumentierte REST-API für
Direct-Upload-Deploys - nur seine `wrangler`-CLI. Dieses Ziel
reverse-engineert Wranglers eigenen Upload-Flow und braucht eine
BLAKE3-Hash-Implementierung auf dem JVM-Classpath, die die meisten
Standard-Java-Installationen nicht mitbringen - siehe
[CLI-Referenz](../cli-reference.md#deploy) und den eigenen Quellcode
dieses Ziels für das vollständige, ehrliche Detail zu diesen rauen Kanten.

```json title="deployments/production.json"
{
  "target": "cloudflare-pages",
  "accountId": "your-account-id",
  "projectName": "my-project",
  "apiTokenEnvVar": "CLOUDFLARE_API_TOKEN"
}
```

## Der Befehl `package`

Bevorzugst du ein reines Archiv gegenüber einem der obigen Ziele - einen
Build an ein GitHub-Release anhängen, ihn an einen Host übergeben, der nur
einen Zip-Upload akzeptiert, oder ihn irgendwohin ausliefern, wo keines
der anbindbaren Ziele hinreicht? [`bxSites package`](../cli-reference.md#package)
baut die Website und packt sie dann in eine einzige Datei, deren Wurzel
der Inhalt der gebauten Website selbst ist (kein umschließender
`site/`-Ordner):

```bash frame="terminal" title="Terminal"
bxSites package
bxSites package --output=dist/my-site.zip
```

`--output` ist standardmäßig `<projectRoot>/site.zip`; ein relativer Wert
wird gegenüber dem Projekt-Root aufgelöst, und seine übergeordneten
Verzeichnisse werden automatisch angelegt, falls sie noch nicht
existieren.

## GitHub Actions (Multi-Versions-Veröffentlichung)

Für eine automatische Veröffentlichung bei jedem Push, statt eines manuell
ausgeführten `bxSites deploy`/`gh-deploy`, bringt dieses Modul einen
einsatzbereiten GitHub-Actions-Workflow mit
(`.github/workflows/pages.yml`), der `main` und `development` als zwei
unabhängig live geschaltete Versionen derselben Website auf GitHub Pages
veröffentlicht. Der Rest dieser Anleitung behandelt diesen Workflow, den
die eigenen Docs dieses Repos verwenden.

## Was er tut

Bei jedem Push nach `main` oder `development`, der `docs/`, `bxsites.yaml`
oder die eigene Quelle des Moduls (Theme-/Pipeline-Änderungen) berührt,
macht der Workflow Folgendes:

1. Installiert BoxLang + [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
2. Registriert dieses Repo als Modul, damit `boxlang bxSites build` aufgelöst werden kann
3. Setzt auf jedem Branch außer `main` `baseURL` auf `.../<branch-name>/`
   nur für diesen einen Build (siehe [unten](#publishing-two-versions-at-once))
4. Führt `boxlang bxSites build` aus
5. Pusht `site/` in den `gh-pages`-Branch - `main` in die Wurzel der
   Website, `development` nach `/development/` - ohne die jeweils andere
   Version anzurühren

Er ist außerdem als manueller Auslöser (`workflow_dispatch`) über den
Reiter Actions verfügbar, für eine einmalige Neuveröffentlichung ohne
neuen Commit.

## Einmalige Einrichtung

GitHub Pages muss auf den `gh-pages`-Branch verweisen, bevor der Workflow
etwas veröffentlichen kann - das ist eine Repository-Einstellung, kein
Schalter, den eine Workflow-Datei von selbst umlegen kann. Der erste
erfolgreiche Lauf erstellt `gh-pages` für dich, tu dies also, *nachdem*
der Workflow mindestens einmal gelaufen ist:

1. **Settings -> Pages**
2. Unter **Build and deployment -> Source** wähle **Deploy from a branch**
3. Unter **Branch** wähle **gh-pages** und **/ (root)**

Danach wird jeder passende Push automatisch gebaut und veröffentlicht.
Die veröffentlichte URL erscheint unter **Settings -> Pages**, sobald das
erste Deploy abgeschlossen ist.

## Zwei Versionen gleichzeitig veröffentlichen

`main` wird in die Wurzel der Website veröffentlicht
(`https://<user>.github.io/<repo>/`) - betrachte das als die
stabilen/veröffentlichten Docs. `development` wird nach `/development/`
veröffentlicht (`https://<user>.github.io/<repo>/development/`) - die
neuesten, unveröffentlichten Docs. Beide bleiben gleichzeitig live: der
Job jedes Branches pusht nur mit `keep_files: true` und seinem eigenen
`destination_dir` nach `gh-pages`, sodass ein `development`-Deploy nie den
Inhalt von `main` überschreibt und umgekehrt.

Die eigene `bxsites.yaml` von `main` sollte `baseURL` auf die Wurzel der
Website gesetzt haben (`https://<user>.github.io/<repo>/`); der Workflow
überschreibt das für jeden anderen Branch zur Build-Zeit, sodass die
`bxsites.yaml` von `development` dafür keinen eigenen `baseURL`-Eintrag
braucht.

Um einen dritten Branch hinzuzufügen (z. B. eine `release/2.0`-Vorschau),
füge ihn zur `on.push.branches`-Liste hinzu und gib ihm einen eigenen
`if: github.ref_name == '...'`-Deploy-Schritt mit
`destination_dir: release-2.0` (oder ähnlich) - das Muster ist dasselbe
wie bei `development`.

## Für dein eigenes Projekt verwenden

Kopiere `.github/workflows/pages.yml` in dein eigenes Projekt (passe die
`modules:`-Zeile an, falls dein Projekt mehr als `bx-markdown` braucht),
aktiviere Pages wie oben, und Pushes nach `main`/`development` werden auf
dieselbe Weise veröffentlicht. Willst du nur eine einzige veröffentlichte
Version, lösche den nicht benötigten Branch aus `on.push.branches` und
seinen passenden Deploy-Schritt.

## Aus einem Projekt-Pages-Unterpfad ausliefern

Eine GitHub-*Projekt*-Pages-Website (im Gegensatz zu einer
`<user>.github.io`-*Benutzer*-Website) wird unter
`https://<user>.github.io/<repo>/` ausgeliefert, nicht von der
Domain-Wurzel. Setze `baseURL` in `bxsites.yaml` auf diese vollständige
URL, damit jeder interne Link, jedes Asset und jeder Navigationseintrag
das nötige `/<repo>/`-Präfix bekommt - und damit auch eine echte
`sitemap.xml` erzeugt wird:

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

Siehe [Konfiguration](../configuration.md#baseurl) für die vollständige
Aufschlüsselung dessen, was `baseURL` bewirkt. Eine
`<user>.github.io`-Benutzer-Website, oder jede eigene Domain, die auf die
Wurzel der Website zeigt, kann `baseURL` bei seinem Standardwert (`/`)
belassen.

## Zugriff auf deine Website einschränken

Eine eingebaute Zugriffskontrolle gibt es hier nicht - bx-sites erzeugt
immer nur ein schlichtes statisches `site/`, und eine statische Datei hat
kein Konzept von "wer fragt hier gerade an". Das
[`robots: false`](../configuration.md#robotstxt) von `bxsites.json` sagt
wohlerzogenen Crawlern, ein Build nicht zu indexieren (nützlich für ein
Staging-/Vorschau-Deploy, das nicht in Suchergebnissen auftauchen soll),
aber das ist eine höfliche Bitte, kein Schloss - die URL funktioniert
weiterhin für jeden, der sie hat. Musst du den Zugriff tatsächlich
sperren, muss das vor den statischen Dateien passieren, bei welchem Host
auch immer sie ausliefert - ein paar gängige, für statische Websites
geeignete Optionen:

- **Cloudflare Pages/Access** - stelle die veröffentlichte Website hinter
  eine
  [Cloudflare-Access](https://developers.cloudflare.com/cloudflare-one/policies/access/)-Richtlinie
  (E-Mail-Allowlist, SSO oder eine Einmal-PIN) - ohne eigenen
  Anwendungscode.
- **Netlify** - eingebauter
  [Passwortschutz](https://docs.netlify.com/manage/security/secure-access-to-sites/site-protection/)
  pro Website oder pro Deploy, allein aus den Website-Einstellungen heraus.
- **Ein kleiner Reverse-Proxy** (beliebiger Host) - HTTP-Basic-Auth vor den
  statischen Dateien (eine `.htpasswd`-artige Regel, oder ein
  Ein-Datei-Cloudflare-Worker/Netlify-Edge-Function) reicht, um Suchmaschinen
  und Zufallsbesucher fernzuhalten, auch wenn das keine echte
  Pro-Benutzer-Identität ist, wie sie eine eingeloggte Anwendung hätte.

Nichts davon sind bx-sites-Funktionen - es sind Host-Einstellungen, die du
dort einschaltest, wo `site/` am Ende ausgeliefert wird.
