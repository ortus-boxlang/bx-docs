---
title: CLI-Referenz
order: 3
icon: phosphor-duotone:terminal-window
summary: Jedes bxSites-Verb und seine Optionen.
tags: [referenz, cli]
---

# CLI-Referenz

```bash title="Usage"
bxSites <verb> [options]
```

`box install bx-sites` legt ein eigenständiges `bxSites`-Skript in deinem
`PATH` ab (über `boxlang.executable` in `box.json`), sodass jedes Verb
unten entweder auf diese kurze Art ausgeführt werden kann, oder als
`boxlang bxSites <verb>` - beide führen genau dasselbe aus; nutze die
längere Form überall dort, wo der `PATH`-Shim nicht eingerichtet ist (ein
CI-Runner, ein von Hand registriertes Modul):

```bash title="Usage (no PATH shim)"
boxlang bxSites <verb> [options]
```

Jedes Verb akzeptiert `--projectRoot=<path>` (oder einen einfachen
positionalen Pfad), um ein anderes Projekt als das aktuelle Verzeichnis
anzusprechen, und die beiden globalen Flags unten können vor jedem Verb
stehen.

Jedes unten erwähnte `docs/` gilt genauso für ein Projekt, das stattdessen
`src/` verwendet - siehe [Erste Schritte](getting-started.md#seiten-hinzufügen)
für die `docs/`-oder-`src/`-Konvention. `new` erzeugt immer ein Gerüst mit
`docs/`.

## Globale Optionen

| Flag | Beschreibung |
|---|---|
| `-h`, `--help` | Nutzung anzeigen und beenden |
| `-v`, `--version` | Modulversion anzeigen und beenden |

## `new`

Ein Docs-Projekt aufsetzen.

```bash title="Usage"
bxSites new [path] [--name=...] [--theme=<siehe guides/themes.md für alle 10>] [--description=...] [--format=yaml|json]
```

- `--name` - der in die Website-Konfiguration geschriebene Website-Name (Standard: der Name des Zielverzeichnisses)
- `--theme` - Standard ist `bootstrap`
- `--description` - die in die Website-Konfiguration geschriebene Website-Beschreibung
- `--format` - `yaml` (Standard, erzeugt `bxsites.yaml`) oder `json` (erzeugt `bxsites.json`) - siehe [Konfiguration](configuration.md)

## `build`

Rendert `docs/**.md` zu einer statischen Website in `site/`. Baut
außerdem den Suchindex (sofern `search` in der Website-Konfiguration nicht
`false` ist, oder `searchProvider` nicht auf einen Provider gesetzt ist -
wie `algolia`/`pagefind` -, der ihn nicht nutzt, siehe
[Suche](guides/search.md)), führt die `pagefind`-CLI gegen die fertige
`site/` aus, wenn `searchProvider.provider` auf `"pagefind"` steht, und
kopiert Theme + `docs/assets/**` nach `site/`.

```bash frame="terminal" title="Terminal"
bxSites build
```

## `serve`

Baut und liefert die Website lokal mit Live-Reload aus.

```bash title="Usage"
bxSites serve [--port=8080] [--host=127.0.0.1]
```

Läuft im Vordergrund, bis es unterbrochen wird (Strg+C).

## `search-index`

Baut `site/search-index.json` eigenständig neu, ohne Seiten neu zu
rendern oder Assets zu kopieren. `build` führt diesen Schritt bereits
automatisch mit aus - dieses Verb existiert für den Fall, dass du nur den
Index auffrischen musst. Deckt immer nur den Haupt-`docs/`-Baum ab, auch
bei einem Projekt mit `docs/versions/`/`docs/i18n/` - ein echter `build`
schreibt stattdessen den eigenen, begrenzten Index jedes Baums (siehe
[Versionierung](guides/versioning.md#was-vorerst-außen-vor-bleibt)).

```bash frame="terminal" title="Terminal"
bxSites search-index
```

## `clean`

Entfernt `site/` und jeglichen Build-Cache, lässt `docs/` und die
Website-Konfiguration unangetastet.

```bash frame="terminal" title="Terminal"
bxSites clean
```

## `gh-deploy`

Baut die Website und pusht sie dann per Force-Push in einen
`gh-pages`-artigen Branch - ein Commit pro Deploy, keine angesammelte
Historie auf diesem Branch, passend zu mkdocs' eigener
`mkdocs gh-deploy`-Konvention. Erfordert, dass das Projekt ein
Git-Repository mit konfiguriertem Remote ist; rührt niemals deinen
eigenen aktuellen Branch oder dein Arbeitsverzeichnis an (der Push erfolgt
aus einem temporären `git worktree`).

```bash title="Usage"
bxSites gh-deploy [--branch=gh-pages] [--remote=origin] [--message="..."]
```

- `--branch` - Standard ist `gh-pages`
- `--remote` - Standard ist `origin`
- `--message` - die einzelne Commit-Nachricht des Branches, Standard ist `"Deploy site via bxSites gh-deploy"`

Siehe [Deployment](guides/deployment.md) für die vollständige
GitHub-Pages-Einrichtung (Pages für den Branch aktivieren, `baseURL` usw.).

## `deploy`

Baut die Website und liefert sie dann an ein echtes Deployment-Ziel aus -
S3 (und jeden S3-kompatiblen Dienst - DigitalOcean Spaces, Cloudflare R2,
Backblaze B2, MinIO), Azure Blob Storage, Google Cloud Storage, Firebase
Hosting, FTP, SFTP, rsync über SSH, Netlify, Vercel, Cloudflare Pages, ein
lokales Verzeichnis oder GitHub Pages (derselbe Push, den auch `gh-deploy`
macht, nur eben auch über diesen einen einheitlichen Befehl erreichbar).

```bash title="Usage"
bxSites deploy --entry=<name> [--verbose]
bxSites deploy [--target=local|github-pages] [target-specific flags] [--verbose]
bxSites deploy [--verbose]
```

Drei Arten, es aufzurufen:

1. **`--entry=<name>`** - leitet an das Ziel weiter, das eine
   `deployments/<name>.json`-Datei deklariert (siehe unten). Jedes Ziel
   außer `local`/`github-pages` braucht das - es gibt mehr Konfiguration,
   als ein paar Flags sinnvoll tragen könnten.
2. **`--target=<name>` mit seinen eigenen Flags** - eine reine
   Flag-Kurzform für die beiden einfachsten Ziele, die überhaupt keinen
   `deployments/`-Ordner brauchen: `local` (`--destination=<path>`) und
   `github-pages` (`[--branch] [--remote] [--message]`, jedes Feld
   optional, dieselben Standardwerte wie bei `gh-deploy`).
3. **Kein Flag - alles deployen.** Jeder `deployments/*.json`-Eintrag wird
   der Reihe nach deployt, ausgehend von einem einzigen gemeinsamen Build
   (die Website wird einmal gebaut, nicht einmal pro Ziel). Setzt voraus,
   dass mindestens ein `deployments/*.json`-Eintrag existiert. Schlägt ein
   Ziel fehl, stoppt das die übrigen nicht - jeder Eintrag wird versucht,
   und der Befehl liefert nur dann einen von null verschiedenen Exit-Code,
   wenn mindestens einer davon fehlgeschlagen ist; die abschließende
   Zusammenfassung meldet, wie viele erfolgreich waren (z. B. `Deployed to
   2/3 target(s) (1 failed)`).

`--verbose` gibt eine Fortschrittszeile aus, wenn der Build startet/endet
und wenn jedes Ziel startet/endet, statt nur der abschließenden
einzeiligen Zusammenfassung.

Siehe [Deployment](guides/deployment.md) für die eigene Konfigurationsform
jedes Ziels und ein echtes `deployments/*.json`-Beispiel für jedes davon.

## `package`

Baut die Website und packt sie dann in ein einziges, verteilbares Archiv -
ein einfaches Zip, dessen Wurzel der Inhalt der gebauten Website selbst
ist (kein umschließender `site/`-Ordner), bereit zum Anhängen an ein
Release oder zur Übergabe an jeden Host, der nur einen Zip-Upload
akzeptiert.

```bash title="Usage"
bxSites package [--output=<path>]
```

`--output` ist standardmäßig `<projectRoot>/site.zip` (ein relativer Wert
wird gegenüber dem Projekt-Root aufgelöst); die übergeordneten
Verzeichnisse eines verschachtelten Ziels werden automatisch angelegt.

## `migrate`

Wandelt ein bestehendes Docs-Projekt in dieses um - `--from` wählt das
Quellformat: `gitbook` (Standard), `mkdocs`, `markdown-zip` oder `notion`.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites migrate --source=/path/to/gitbook-export
bxSites migrate --source=/path/to/mkdocs-project --from=mkdocs
bxSites migrate --source=/path/to/export.zip --from=markdown-zip
bxSites migrate --source=/path/to/notion-export --from=notion
```

- `--source` (erforderlich) - Pfad zum Wurzelverzeichnis des Exports/Projekts (`SUMMARY.md` für `gitbook`, `mkdocs.yml` für `mkdocs`), oder eine `.zip`-Datei (`markdown-zip`; `notion` akzeptiert sowohl eine `.zip` als auch einen bereits entpackten Ordner)
- `--from` - `gitbook` (Standard), `mkdocs`, `markdown-zip` oder `notion`

### `--from=gitbook` (Standard)

Ein GitBook-Export - ein `SUMMARY.md`-Inhaltsverzeichnis plus dessen
`.md`-Dateien, GitBooks eigenes Sync-Format auf der Festplatte - wird in
den `docs/`-Baum dieses Projekts umgewandelt: `SUMMARY.md` wird zu
`docs/nav.json`, `{% block %}`-Syntax wird zu ihrem bx-sites-Äquivalent
(`::: name`-Direktiven, oder die native `=== "Title"`-Tab- bzw.
`!!! type`-Admonition-Syntax, wo bereits eine treffendere Entsprechung
existiert - siehe [Content-Blöcke](guides/content-blocks.md)),
`README.md`-Dateien werden zu `index.md`, und `.gitbook/assets/**` wird
nach `docs/assets/gitbook/` kopiert.

### `--from=mkdocs`

Ein mkdocs-Projekt - `mkdocs.yml` plus dessen `docs/`-Ordner - wird in ein
vollständiges bx-sites-Projekt umgewandelt: `mkdocs.yml` wird zu
`bxsites.yaml` + `docs/nav.json`, und jede Seite wird weitgehend
unverändert übernommen, da mkdocs-materials eigene Admonition-/Tabs-/
Mathematik-/Code-Annotation-Syntax bereits bx-sites' eigene native Syntax
*ist* - siehe [Migration von mkdocs](guides/migrating-from-mkdocs.md).
Nicht-`.md`-Assets (Bilder, die bei mkdocs meist direkt neben der Seite
liegen, die sie verwendet, da mkdocs keine einheitliche
Asset-Ordner-Konvention hat) werden nach `docs/assets/mkdocs/` verschoben
und ihre Referenzen entsprechend umgeschrieben.

### `--from=markdown-zip`

Ein schlichtes `.zip` aus Markdown-Dateien - kein proprietäres
Exportformat zu übersetzen, da die eigene Verschachtelung eines Ordners
bereits genau bx-sites' eigener Nav-Konvention *entspricht* und ein
Seite-zu-Seite-relativer `.md`-Link bereits so aufgelöst wird, wie
bx-sites es erwartet. Meist eine reine Kopie: jede Nicht-`.md`-Datei
(etwa ein Bild) wird nach `docs/assets/imported/` verschoben und jede
Referenz einer Seite darauf entsprechend umgeschrieben. Es wird keine
`bxsites.yaml`/`docs/nav.json` geschrieben - ein reines Zip bringt weder
einen eigenen Website-Namen noch eine eigene Nav-Struktur mit, die
übersetzt werden könnten.

### `--from=notion`

Ein Notion-"Export as Markdown & CSV"-Archiv (eine `.zip`, oder ein
bereits entpackter Ordner) - behandelt Notions eigene zwei Eigenheiten,
die sonst nichts hier Migriertes betreffen: jeder Seiten-/Unterseiten-
Ordner trägt ein angehängtes Leerzeichen plus eine 32-stellige ID
(zur Unterscheidung gleichnamiger Seiten, nie zum Lesen gedacht), und der
Titel einer Seite wird als literale, führende `# Überschrift` wiederholt,
statt in der Frontmatter zu stehen. Beides wird bereinigt: das
ID-Suffix wird entfernt und der verbleibende Name für den
Ausgabe-Dateinamen slugifiziert, die führende Überschrift wird zu einem
echten `title`-Frontmatter-Feld statt einer doppelten ersten Zeile, und
jedes Link-/Bild-Ziel (das Notion URL-kodiert schreibt, weiterhin auf die
ursprünglichen ID-Suffix-Namen zeigend) wird entsprechend umgeschrieben.
Nicht-`.md`-Dateien werden nach `docs/assets/imported/` verschoben, genau
wie bei `markdown-zip` oben.

### Alle vier

Gibt eine Zusammenfassung der konvertierten Seiten (und, bei mkdocs/
markdown-zip/notion, der Assets) aus, und - wenn etwas nicht automatisch
konvertiert werden konnte - eine Liste dessen, was manuell geprüft werden
muss - nichts wird stillschweigend verworfen. Eine bereits existierende
Zieldatei, `bxsites.yaml`, oder `docs/nav.json` wird überschrieben
(ebenfalls gemeldet), prüfe daher die migrierte Ausgabe, bevor du sie
committest.

## `audit`

Ein CI-taugliches Qualitäts-Gate über eine bereits gebaute `site/` -
führe zuerst `build` aus. Prüft:

- **Defekte interne Links/Bilder** - jedes `<a href>`/`<img src>`, das auf
  eine Seite oder ein Asset zeigt, das in `site/` nicht existiert. Lässt
  die Prüfung fehlschlagen.
- **Fehlender Alt-Text** - jedes `<img>` ganz ohne `alt`-Attribut. Ein
  leeres `alt=""` (die korrekte Auszeichnung für ein rein dekoratives
  Bild) wird nicht bemängelt. Lässt die Prüfung fehlschlagen.
- **Verwaiste Seiten** - Seiten, die in `site/` existieren, aber von
  keiner Baum-eigenen Startseite aus über Links erreichbar sind (die
  `index.html` der Hauptseite, sowie die jeder Version/jedes Locale). Rein
  informativ - lässt die Prüfung nie fehlschlagen, da eine Seite, die ein
  Projekt bewusst aus der eigenen Navigation ausgeschlossen hat (z. B.
  per Frontmatter `hidden: true`), *soll* nur über einen direkten Link
  erreichbar sein.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites build
bxSites audit
```

Beendet sich mit `1`, wenn es defekte Links/Bilder oder Bilder ohne
Alt-Text gibt, sonst mit `0` (verwaiste Seiten beeinflussen den Exit-Code
nie). Bewusst nur auf interne Links beschränkt - es werden keine
HTTP-Anfragen zur Prüfung externer URLs gestellt, das gehört in ein
dediziertes Link-Checking-Tool als eigener Job.

## `stats`

Ein rein lesender Zusammenfassungsbericht über eine bereits gebaute
`site/` - führe zuerst `build` aus. Meldet:

- **Seiten und Wörter** - Gesamtanzahl der Seiten und eine grobe
  Wortanzahl (Tags entfernt, derselbe "für eine Schätzung ausreichende"
  Standard wie bei der Lesezeit des Blogs), plus eine Aufschlüsselung pro
  Baum, sobald es mehr als einen Baum gibt (eine Version oder ein
  Nicht-Standard-Locale).
- **Versionen und Locales** - Namen jedes `docs/versions/`-/
  Nicht-Standard-`docs/i18n/`-Ordners.
- **Blog** - Anzahl an Beiträgen/Kategorien/Autoren/aktiven Jahren,
  direkt aus der eigenen Ordnerstruktur von `site/blog/`  (entspricht
  daher immer dem, was tatsächlich veröffentlicht wurde, Entwürfe
  ausgeschlossen) - `none`, wenn es keinen Blog gibt.
- **Tags** - die Anzahl unterschiedlicher Tags über die gesamte Website.
- **Suchindex** - Anzahl der Einträge und Dateigröße von
  `search-index.json`, oder `none`, wenn Suche deaktiviert ist oder ein
  Nicht-lokaler Provider aktiv ist.
- **Website-Ausgabe** - Gesamtanzahl der Dateien und Größe auf der
  Festplatte der gebauten `site/`.

```bash
bxSites build
bxSites stats
```

Beendet sich immer mit `0` - rein informativ, hier gibt es kein
Bestehen/Durchfallen-Gate (das ist die Aufgabe von `audit`).

## `doctor`

Eine einmalige Umgebungs-/Konfigurations-Gesundheitsprüfung - das Verb,
das man "vor dem Melden eines Bugs ausführt". Prüft die JVM-Version, dass
`docs/` existiert, dass `bxsites.yaml`/`.json` tatsächlich geparst wird
und gültig ist, dass die erforderlichen BoxLang-Module (`bx-markdown`,
`bx-esapi`, `bx-yaml`, `bx-image`) installiert und aktiviert sind, und -
falls ein projektweites `theme/`-Override existiert - dass es den
Zwei-Pflichtdateien-Vertrag (`layout.bxm`/`page.bxm`) erfüllt.

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Beendet sich mit `1`, wenn eine Prüfung fehlschlägt, sonst mit `0`. Nichts
hier verändert ein Projekt - rein diagnostisch.

## `post:new`

Einen neuen Blogbeitrag unter `docs/blog/posts/<slug>.md` als Gerüst
anlegen.

```bash title="Usage"
bxSites post:new --title="My New Post" [--slug=...] [--date=...] [--authors=...] [--categories=...] [--tags=...] [--draft]
```

- `--title` (erforderlich) - wird auch zum Frontmatter-`title` des Beitrags
- `--slug` - Standard ist ein aus `--title` erzeugter Slug
- `--date` - Standard ist heute (`yyyy-MM-dd`)
- `--authors`, `--categories`, `--tags` - kommagetrennt
- `--draft` - Standard ist `true` (übergib `--!draft`, um sofort zu veröffentlichen)

Siehe [Blog](guides/blog.md) für die vollständige Frontmatter-Referenz.

## `version:new`

Erstellt einen Snapshot des aktuellen `docs/`-Baums nach
`docs/versions/<name>/`, ausgenommen `assets/`, `versions/`, `i18n/` und
`blog/` (jeder davon ist ein eigener, separat geladener Baum, nicht Teil
des Snapshots).

```bash title="Usage"
bxSites version:new --name=1.0
```

- `--name` (erforderlich) - der Versionsordner/-name, z. B. `1.0`

Siehe [Konfiguration, Abschnitt "Versionierung"](configuration.md#versioning).

## `i18n:status`

Meldet die Übersetzungsabdeckung pro Locale - für jedes konfigurierte
Locale, wie viele Seiten des Standard-Baums (unter demselben relativen
Pfad) unter `docs/i18n/<code>/` existieren, und welche noch fehlen.

```bash frame="terminal" title="Terminal"
bxSites i18n:status
```

Beendet sich immer mit `0` - rein informativ.

## `i18n:new`

Erzeugt das Gerüst eines neuen `docs/i18n/<code>/`-Locale-Ordners und
sät dabei eine `index.md`, kopiert von der `index.md` des Standard-Locale,
sofern eine existiert.

```bash title="Usage"
bxSites i18n:new --code=es
```

- `--code` (erforderlich) - der Locale-Code, z. B. `es`, `fr`, `pt-BR`

Siehe [Internationalisierung](guides/i18n.md), um das neue Locale in
`bxsites.yaml`s `i18n.locales` einzubinden.

## `page:new`

Erzeugt das Gerüst einer einzelnen Docs-Seite an einem beliebigen Pfad
unter `docs/`, mit bereits ausgefülltem angefordertem Frontmatter.

```bash title="Usage"
bxSites page:new --path=guides/setup.md [--title=...] [--description=...] [--icon=...] [--tags=...] [--order=...]
```

- `--path` (erforderlich) - relativ zu `docs/`, muss auf `.md` enden
- `--title`, `--description`, `--icon`, `--order` - werden ins Frontmatter geschrieben
- `--tags` - kommagetrennt

## `plugin:new`

Erzeugt das Gerüst eines Plugin-Moduls (`box.json`, `ModuleConfig.bx`,
ein `models/BxSitesPlugin.bx` mit jedem Hook als Stub), nach dem Vorbild
von `examples/hello-plugin/`.

```bash title="Usage"
bxSites plugin:new --name=my-analytics-plugin [--dest=...]
```

- `--name` (erforderlich) - der Modulname/Slug des Plugins
- `--dest` - Standard ist `<projectRoot>/<name>`

Siehe [Plugins](guides/plugins.md) für die Hook-Referenz und wie das
fertige Plugin in `bxsites.yaml`s `plugins`-Array eingebunden wird.

## `install:plugin`

Lädt ein veröffentlichtes Plugin von ForgeBox herunter und legt es direkt
in die eigenen `boxlang_modules/` des Projekts - BoxLangs eigene
automatisch geladene lokale-Modul-Konvention, sodass außer der
`bxSites`-Binary selbst nichts weiter benötigt wird (kein
`box`/CommandBox beteiligt).

```bash title="Usage"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

- `--name` (erforderlich) - der zu installierende ForgeBox-Slug
- `--version` - eine bestimmte Version; weglassen für die neueste

Gibt den echten registrierten Mapping-Namen des Moduls aus, sobald es
geladen ist - füge diesen Namen zu `bxsites.yaml`s `plugins`-Array hinzu,
um es zu aktivieren (die reine Installation aktiviert ein Plugin nie -
siehe [Plugins](guides/plugins.md)).

## `theme:new`

Wirft eines der integrierten Themes in den eigenen `theme/`-Ordner des
Projekts zum Anpassen aus, passend zu mkdocs' `--theme`-Eject-Workflow.

```bash title="Usage"
bxSites theme:new --theme=material
```

- `--theme` (erforderlich) - `bootstrap`, `material`, `tailwind`, `docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook` oder `notion` - siehe [Themes](guides/themes.md#built-in)

Schlägt fehl, statt ein bestehendes `theme/` zu überschreiben. Siehe
[Themes](guides/themes.md) für den Override-Vertrag (`layout.bxm` +
`page.bxm`).

## `install:theme`

Lädt ein veröffentlichtes Theme von ForgeBox in die eigenen
`themes/<name>/` des Projekts herunter - nichts außer der
`bxSites`-Binary benötigt, genau wie bei `install:plugin`.

```bash title="Usage"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

- `--name` (erforderlich) - der zu installierende ForgeBox-Slug
- `--version` - eine bestimmte Version; weglassen für die neueste

Validiert das heruntergeladene Paket vor Abschluss gegen den
`ThemeProvider`-Vertrag (`layout.bxm` + `page.bxm`), sodass ein defektes
Paket bereits bei der Installation fehlschlägt statt erst beim nächsten
`build`. Setze `bxsites.yaml`s `theme.name` auf den installierten Namen,
um es zu verwenden - siehe
[Themes](guides/themes.md#ein-veröffentlichtes-theme-installieren).

## `theme:import`

Bestmögliche Konvertierung eines Themes aus dem Ökosystem eines anderen
statischen Site-Generators (`mkdocs`/`jekyll`/`hugo`) in ein
bx-sites-Theme-Gerüst unter `themes/<name>/` - ein Ausgangspunkt, kein
verlustfreier Ein-Befehl-Port.

```bash title="Usage"
bxSites theme:import --source=mkdocs --path=/path/to/theme --name=my-imported-theme
```

- `--source` (erforderlich) - `mkdocs`, `jekyll` oder `hugo`
- `--path` (erforderlich) - der Wurzelordner des Quell-Themes
- `--name` (erforderlich) - der Zielname, geschrieben nach `themes/<name>/`

Sicher, gegen denselben `--name` erneut auszuführen -
`layout.bxm`/`page.bxm` werden überschrieben und neu gefundene
Asset-Ordner werden zusammengeführt. Siehe
[Ein Theme importieren](guides/theme-import.md) für genau das, was
übersetzt wird und was nicht, und was danach zu prüfen ist.

## `page:rename`

Verschiebt eine Docs-Seite von einem Pfad zu einem anderen und schreibt
dabei jeden relativen Markdown-Link über `docs/**` um, der auf den alten
Pfad zeigte - dasselbe Link-Rot-Problem, das die gebaute HTML-Seite
bereits löst (`audit`), hier angewendet auf rohen Markdown-Quelltext zur
Zeit der Umbenennung.

```bash title="Usage"
bxSites page:rename --from=guides/old-name.md --to=guides/new-name.md
```

- `--from` (erforderlich) - der aktuelle, relativ zu `docs/` angegebene Pfad der Seite
- `--to` (erforderlich) - ihr neuer, relativ zu `docs/` angegebener Pfad

Nur einfache `[text](relative/path.md)`-artige Links werden umgeschrieben
- absolute URLs, `mailto:` und reine In-Page-Anker bleiben unangetastet.
`docs/assets/**` wird nie durchsucht.

Stempelt außerdem das Frontmatter `redirect_from` der verschobenen Seite
mit ihrer alten URL, sodass ein Build ([Weiterleitungen](guides/redirects.md))
weiterhin darauf antwortet, statt die Umbenennung jeden externen Link,
dessen Quelle dieses Projekt nicht kontrolliert, ins Leere laufen zu
lassen (404).

## `blog:drafts`

Listet jeden Blogbeitrag auf, dessen Frontmatter `draft: true` setzt -
`build` überspringt Entwürfe immer, daher ist dies die einzige Stelle, an
der ihre Existenz sichtbar wird.

```bash frame="terminal" title="Terminal"
bxSites blog:drafts
```

Beendet sich immer mit `0`.

## `blog:find`

Filtert Blogbeiträge nach Autor/Kategorie/Tag/Datumsbereich, ohne einen
vollständigen `build` auszuführen.

```bash title="Usage"
bxSites blog:find [--author=...] [--category=...] [--tag=...] [--since=...] [--until=...] [--drafts]
```

- `--author`, `--category`, `--tag` - Groß-/Kleinschreibung ignorierend, exakte Übereinstimmung mit einem der eigenen Werte des Beitrags
- `--since`, `--until` - ein Datum; nur Beiträge am/nach `--since` und/oder am/vor `--until` passen
- `--drafts` - auch Entwürfe einbeziehen (standardmäßig ausgeschlossen)

Jeder Filter ist optional und unabhängig - werden keine übergeben, wird
jeder veröffentlichte Beitrag aufgelistet.

## `search:query`

Führt eine Stichwortsuche gegen eine bereits gebaute
`site/search-index.json` aus - führe zuerst `build` oder `search-index`
aus. Bewertet Ergebnisse mit derselben relativen Feldgewichtung, die auch
das clientseitige Such-Widget verwendet (Titel, dann Tags, dann
Überschriften, dann Fließtext), sodass du prüfen kannst, was die Suche
eines echten Besuchers liefern würde, ohne einen Browser zu öffnen.

```bash title="Usage"
bxSites search:query --query="getting started" [--limit=10]
```

- `--query` (erforderlich) - durch Leerzeichen getrennte Suchbegriffe
- `--limit` - maximale Anzahl an Ergebnissen, Standard ist `10`

## `lint`

Ein Qualitäts-Durchlauf über den rohen `docs/`-Markdown-Quelltext vor dem
Build, getrennt von `audit` (das nur eine bereits gebaute `site/`
untersucht). Prüft:

- **Übersprungene Überschriftenebenen** - ein Seitentext, der direkt von
  `##` zu `####` springt, ohne dazwischenliegendes `###` (verwirrende
  Struktur und schlecht für Barrierefreiheit). Zeilen innerhalb eines
  eingezäunten Code-Blocks werden nie fälschlich als Überschriften
  erkannt.
- **Probleme mit Blogbeitrag-Daten** - ein Beitrag unter
  `docs/blog/posts/**` mit fehlendem oder ungültigem Frontmatter-`date`
  (`build` selbst wirft hierbei bereits einen Fehler, sobald es Beiträge
  lädt - `lint` macht es stattdessen als Befund sichtbar).

```bash frame="terminal" title="Terminal"
bxSites lint
```

Beendet sich mit `1`, wenn eine der beiden Prüfungen etwas findet, sonst
mit `0`.


## `docbox`

Erzeugt aus [DocBox](https://docbox.ortusbooks.com)' eigener JSON-Ausgabe
eine BoxLang-/CFML-API-Referenz im Content-Verzeichnis - das
BoxLang-Gegenstück zum Javadoc-Generator der Java-Plugins. Siehe
[DocBox-API-Referenz](guides/docbox.md) und
[`docbox`](configuration.md#docbox).

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites docbox --mappings:models=models --projectTitle="Meine API"
bxSites docbox --pagePathPrefix=api/classes --tags=api,classes
bxSites docbox --jsonDir=build/docbox-json
```

| Flag | Zweck |
|---|---|
| `--mappings:<name>=<pfad>` | Ein zu dokumentierender Quellbaum, wiederholbar |
| `--projectTitle` | Titel der Überblicksseite |
| `--excludes` | Regex für Pfade, die DocBox überspringt |
| `--pagePathPrefix` | Wohin die Seiten geschrieben werden |
| `--tags` | Frontmatter-Tags, kommagetrennt |
| `--jsonDir` | DocBox' JSON-Ausgabe behalten statt verwerfen |

Ohne Mappings werden die üblichen Ordner dokumentiert: `models`,
`handlers`, `bifs`, `components`, `interceptors`. Benötigt das Modul
`bx-docbox` (`install-bx-module bx-docbox`).

## `coldbox`

Dokumentiert eine ColdBox-Anwendung anhand ihrer Konventionen - Routen,
Handler, Models und WireBox-Mappings, Module, Interceptors und geplante
Tasks - ohne sie zu starten. Siehe
[ColdBox-Anwendungen](guides/coldbox.md) und
[`coldbox`](configuration.md#coldbox).

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites coldbox --appRoot=app
bxSites coldbox --include=routes,handlers
```

| Flag | Zweck |
|---|---|
| `--appRoot` | Wo die ColdBox-App liegt, relativ zum Projektstamm |
| `--pagePathPrefix` | Wohin die Seiten geschrieben werden |
| `--tags` | Frontmatter-Tags, kommagetrennt |
| `--include` | Seitengruppen: `routes`, `handlers`, `models`, `modules`, `interceptors`, `scheduler` |

Bricht mit `BxSites.NotAColdBoxApp` ab, wenn dort weder `handlers/`,
`config/ColdBox`, `config/Router` noch `modules_app/` liegen. Details je
Klasse kommen aus DocBox, `bx-docbox` macht diese Seiten also
reichhaltiger.
