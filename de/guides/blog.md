---
title: Blog
order: 10
icon: phosphor-duotone:newspaper
tags: [anleitungen, blog]
---

# Blog

Ein Blog ist eine weitere Convention-over-Configuration-Funktion, in
derselben Form wie
[Versionen](versioning.md)/[i18n](i18n.md) oder der
[Tags-Index](../getting-started.md#seiten-hinzufügen) - lege Beiträge unter
`docs/blog/posts/` ab, und BxSites baut `/blog/` (paginiert), eine
Kategorie-Seite pro Kategorie, eine Jahresarchiv-Seite pro Kalenderjahr,
eine Autoren-Seite pro Autor, einen RSS-Feed pro Kategorie plus einen für
den gesamten Blog, und eine `/blog/stats/`-Seite - ganz ohne
Konfiguration. Ein Projekt ohne `docs/blog/posts/`-Ordner hat schlicht
keinen Blog - sonst ändert sich nichts.

## Einen Beitrag schreiben

Jede `.md`-Datei unter `docs/blog/posts/`, in beliebiger Tiefe, ist ein
Beitrag - Unterordner sind vollständig optional und dienen nur deiner
eigenen redaktionellen Übersichtlichkeit. Ein flacher Ordner funktioniert
für eine Handvoll Beiträge einwandfrei; sobald du in die Hunderte gehst,
hält das Ablegen von Beiträgen unter `docs/blog/posts/2026/` (oder
`docs/blog/posts/2026/03/`, oder jedem beliebigen Schema) den Dateibaum
deines Editors übersichtlich, ohne etwas umzubenennen oder eine
Frontmatter-Datumspräfix-Konvention anzufassen. Nichts davon beeinflusst
die gebaute Website - die Sortierreihenfolge eines Beitrags, sein
Jahresarchiv und seine URL (`blog/<slug>/`) werden allein aus der
Frontmatter abgeleitet, nie daraus, wo die Datei zufällig liegt, sodass
der Ordner eines Beitrags und sein tatsächliches `date` immer
unabhängig voneinander sein dürfen:

```text title="Projektstruktur"
docs/blog/posts/
├── hello-world.md              (flach ist auch in Ordnung)
├── 2026/
│   ├── announcing-2-0.md
│   └── 03/
│       └── a-deep-dive.md
```

Frontmatter, für jeden Beitrag, egal wo er abgelegt ist:

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

- `date` (erforderlich) - alles, was BxSites parsen kann (`2026-08-15`,
  oder ein vollständiges Datum mit Uhrzeit). Legt die eigene
  Sortierreihenfolge des Beitrags fest (neueste zuerst) sowie sein
  `<pubDate>`/`article:published_time`.
- `authors` - eine Liste von IDs, die zu Einträgen in
  [`docs/blog/authors.yml`](#authors) passen, oder ein schlichter Name
  ohne passenden Eintrag (wird als nicht verlinkter Text gerendert,
  statt den Build scheitern zu lassen - praktisch für einen einmaligen
  Gastbeitrag).
- `categories` - die eigene Taxonomie eines Beitrags, jede erhält ihre
  eigene `/blog/category/<slug>/`-Seite (und ihren eigenen
  `/blog/category/<slug>/feed.xml`-RSS-Feed - siehe [Feed](#feed)).
  Unabhängig von `tags` unten.
- `tags` - dieselbe websiteweite `tags`-Frontmatter, die jede andere
  Seite bereits hat (siehe [Erste Schritte](../getting-started.md#seiten-hinzufügen))
  - die Tags eines Beitrags werden als Badges gerendert und fließen in
  den Haupt-`/tags/`-Index ein, zusammen mit jeder anderen getaggten
  Seite.
- `summary` - ein einzeiliger Auszug, gezeigt auf `/blog/`/
  Kategorie-Seiten und im RSS-Feed, verwendet, wenn ein Beitrag keine
  `<!-- more -->`-Markierung hat. Ohne beides fällt BxSites auf eine
  reine Klartext-Kürzung des eigenen Textkörpers des Beitrags zurück.
- `image` - ein Beitragsbild (ein zu `docs/assets/`-relativer Pfad, oder
  eine vollständige URL) - gezeigt oben im Beitrag und als Thumbnail auf
  jeder Listen-/Kategorie-Card. Wird außerdem das eigene
  `og:image`/die Twitter-Card des Beitrags, sofern `ogImage` das nicht
  separat überschreibt. Ein zu `docs/assets/`-relatives Bild (und der
  eigene `avatar` eines Autors, unten) erhält dieselbe responsive
  `<picture>`-/`srcset`-/WebP-Behandlung wie jedes andere Bild unter
  `docs/assets/` - siehe [Bilder](images.md).
- `slug` - überschreibt das URL-Segment (`/blog/<slug>/`) - standardmäßig
  aus dem Dateinamen abgeleitet.
- `draft: true` - schließt den Beitrag von einem echten `bxSites build`
  vollständig aus. `bxSites serve` zeigt ihn trotzdem an (mit einem
  sichtbaren "🚧 Entwurf"-Banner auf dem Beitrag selbst und einer Card
  mit gestricheltem Rand überall, wo er gelistet wird), sodass du einen
  Entwurf lokal Korrektur lesen kannst, bevor er fertig ist - siehe
  [Entwürfe ansehen](#entwürfe-ansehen).

Jeder andere Seiten-Frontmatter-Schlüssel, der bereits in
[Erste Schritte](../getting-started.md#seiten-hinzufügen) dokumentiert ist
(`icon`, `description`, `ogImage`, `toc`), funktioniert auch auf einem
Beitrag.

## Beitragsbilder und andere Blog-Assets

`docs/assets/blog/` ist nichts Besonderes, außer ein gewöhnlicher
Unterordner von `docs/assets/` (bereits vollständig nach `site/assets/`
kopiert) - dort erwartet diese Anleitung (und die
Convention-over-Configuration-Suche nach Autoren-Avataren unten)
lediglich Beitragsbilder/Autorenfotos, damit das eigene `docs/assets/`
eines Projekts nicht durch die Vermischung von Blog-Bildern mit dem Rest
seiner Diagramme und Icons unübersichtlich wird. Der Ort wird nirgends
erzwungen - jeder `docs/assets/**`-Pfad funktioniert in `image`/`avatar`.

## Autoren

`docs/blog/authors.yml` ist optional - ein Eintrag pro Autoren-ID,
referenziert durch die eigene `authors`-Liste eines Beitrags:

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

Nur `name` ist erforderlich. Jeder Autor, der von mindestens einem
Beitrag referenziert wird, erhält seine eigene
`/blog/authors/<id>/`-Seite (Bio, Socials, jeder Beitrag, den er
geschrieben hat) - ein Autor, dem noch niemand einen Beitrag zuschreibt,
bekommt keine Seite, selbst wenn er im Verzeichnis steht.

**Avatar, per Konvention** - lege eine Datei unter
`docs/assets/blog/authors/<id>.{jpg,jpeg,png,webp,svg}` ab, und sie wird
automatisch erkannt, kein `avatar:`-Schlüssel nötig. Ein explizites
`avatar` in `authors.yml` (eine URL oder ein zu `docs/assets/`-relativer
Pfad) überschreibt immer die Convention-over-Configuration-Suche.

## Kategorien, Archive, Pagination und der "Blog"-Nav-Eintrag

Jeder eigenständige `categories`-Wert über alle Beiträge hinweg erhält
seine eigene `/blog/category/<slug>/`-Seite, die nur die Beiträge dieser
einen Kategorie listet. Jedes Kalenderjahr mit mindestens einem Beitrag
erhält außerdem seine eigene `/blog/archive/<year>/`-Seite
(`/blog/archive/2026/`, `/blog/archive/2025/`, ...), vollständig aus der
eigenen `date`-Frontmatter jedes Beitrags abgeleitet - keine
Ordnerstruktur oder Dateinamenskonvention erforderlich, sodass es nie
darauf ankommt, wo die `.md`-Datei eines Beitrags unter
`docs/blog/posts/` tatsächlich liegt (flach, oder in eigene Unterordner
zur einfacheren Durchsicht beim Bearbeiten aufgeteilt), ob das zu seinem
`date` passt. Die Hauptliste `/blog/` erhält automatisch
"Nach Jahr durchsuchen"-/"Nach Kategorie durchsuchen"-Linkblöcke, jeweils
mit einer Beitragsanzahl pro Jahr/Kategorie, sobald Beiträge sich über
mehr als ein Jahr/eine Kategorie erstrecken - ein einzelnes Jahr oder
eine einzelne Kategorie allein lohnt keinen Linkblock, also wird er so
oder so weggelassen.

Die Hauptliste `/blog/`, jede Kategorie-Seite und jede
Jahresarchiv-Seite paginieren alle identisch - `blog.postsPerPage` in
der Website-Konfiguration steuert, wie viele Beiträge pro Seite
(Standard `10`); ab Seite 2 geht es weiter zu `.../page/2/`,
`.../page/3/` usw.

Ein einzelner "Blog"-Eintrag wird automatisch zur Hauptnavigation
hinzugefügt, sobald `docs/blog/posts/` mindestens einen
Nicht-Entwurf-Beitrag hat - keine Änderung an `nav`/`docs/nav.json`
nötig. Standardmäßig wird er als Letztes angehängt, nach allem anderen.
Um ihn stattdessen an einer bestimmten Stelle zu platzieren, füge deinen
eigenen Eintrag mit einer expliziten `url` hinzu (umgeht die übliche
Regel, dass `path` auf eine echte Seite passen muss, da der Blog keine
`docs/`-Seite ist) zu deinem `nav`-Array oder `docs/nav.json` - das
unterdrückt den automatisch angehängten Eintrag vollständig, es gibt
also nie ein Duplikat:

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

Einzelne Beiträge werden nicht selbst zur Navigation hinzugefügt
(genau wie der Tags-Index) - sie sind erreichbar von `/blog/`, ihrer
eigenen Kategorie-Seite, ihrer eigenen Jahresarchiv-Seite, der Seite
ihres Autors, der Suche und den Vorherige-/Nächste-Links
untereinander (Beiträge, die chronologisch benachbart sind, unabhängig
von der eigenen Vorherige-/Nächste-Kette der regulären Navigation).

Die eigene Meta-Zeile jedes Beitrags (auf seiner Card und seiner
Detailseite) zeigt außerdem eine geschätzte Lesezeit neben dem Datum -
eine grobe Schätzung Wortanzahl / 200 Wörter pro Minute, dieselbe
Größenordnung, die die meisten Lesezeit-Funktionen verwenden, nicht
konfigurierbar.

## Feed

`/blog/feed.xml` - ein Standard-RSS-2.0-Feed der neuesten Beiträge,
neueste zuerst, geschrieben, sobald die Website-Konfiguration eine
absolute `baseURL` auflöst (dieselbe Voraussetzung wie bei
`sitemap.xml`) und `blog.feed` nicht auf `false` gesetzt ist. Jede
Kategorie erhält außerdem ihren eigenen gefilterten Feed unter
`/blog/category/<slug>/feed.xml`. Beide sind auf `blog.feedLimit`
Beiträge begrenzt (Standard `25`) - die meisten Feed-Reader interessieren
sich nur für das Neueste, sodass ein unbegrenzter Feed auf einem großen
Blog bei jedem Poll einfach Bandbreite verschwendet; setze ihn auf `0`
für jeden Beitrag, unbegrenzt:

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

## Entwürfe ansehen

`draft: true` hält einen Beitrag vollständig aus einem echten
`bxSites build` heraus - aber `bxSites serve` bindet ihn trotzdem ein,
sodass du einen Entwurf durchlesen kannst (jeden Link anklicken, das
Beitragsbild prüfen, sehen, wie er auf `/blog/` gelistet wird), bevor er
fertig ist. Ein angezeigter Entwurf trägt immer ein sichtbares
"🚧 Entwurf"-Banner - auf seiner eigenen Detailseite, und als Card mit
gestricheltem Rand überall, wo er gelistet wird (die Hauptliste
`/blog/`, seine eigenen Kategorie-/Archiv-/Autoren-Seiten) - sodass es
nie eine Unklarheit darüber gibt, was tatsächlich veröffentlicht ist.
Stoppe `bxSites serve` und führe `bxSites build` aus, und derselbe
Entwurf ist verschwunden, genau als hätte es ihn nie gegeben.

## Statistiken

`/blog/stats/` - eine Handvoll aggregierter Cards über den Blog als
Ganzes: Beiträge insgesamt, Wörter insgesamt geschrieben,
durchschnittliche Lesezeit, Kategorie-/Mitwirkenden-/Jahres-Anzahlen,
und drei "Spotlight"-Cards (längster Beitrag, aktivste Kategorie,
aktivster Autor), jede verlinkt zur echten Seite, um die es geht. Rein
aus den für diesen Build bereits geladenen Beiträgen berechnet - keine
separate Analyse, kein Tracking, nichts zwischen Builds gespeichert -
und immer gebaut, selbst für einen brandneuen Blog mit noch null
Beiträgen. Verlinkt vom unteren Ende der Hauptliste `/blog/`.

## SEO und Social

Jeder Beitrag erhält bereits alles, was eine normale Seite auch bekommt
(`<meta name="description">`, `og:description`,
`og:image`+`twitter:card`, wenn ein Bild gesetzt ist - siehe
[Konfiguration: `ogImage`](../configuration.md#ogimage)), plus ein paar
beitragsspezifische Tags, die jedes integrierte Theme automatisch
hinzufügt: `og:type` ist `"article"` statt `"website"`, und
`article:published_time`/`article:author` (einer pro anerkanntem Autor,
der ein `url` in `authors.yml` gesetzt hat) sind im `<head>` der Seite
enthalten.

## Suche

Beiträge werden in dieselbe `search-index.json` indexiert wie jede
andere Seite (Modulspezifikation, Abschnitt 7) - keine separate
Blog-Such-UI, die bestehende Suchbox findet Beiträge bereits neben
Docs-Seiten.

## Das Erscheinungsbild des Blogs anpassen

Es gibt kein separates "Blog-Theme" zu schreiben - jede Blog-Seite (die
Hauptliste `/blog/`, eine Kategorie-/Archiv-/Autoren-Seite,
`/blog/stats/`, und die eigene Detailseite jedes Beitrags) rendert über
genau dasselbe `layout.bxm`/`page.bxm` wie jede andere Seite deiner
Website, sodass ein Blog automatisch wie der Rest deiner Docs aussieht,
und jede Theme-Überschreibung, die du bereits vorgenommen hast (siehe
[Themes](themes.md#ein-theme-überschreiben)), unverändert darauf angewendet
wird, ohne zusätzlichen Verdrahtungsaufwand.

Das blog-spezifische Markup selbst (Beitrags-Cards, die
Datum-/Autor-/Lesezeit-Meta-Zeile, der Pager, der Profilblock eines
Autors, die "Nach Jahr durchsuchen"-/"Nach Kategorie durchsuchen"-Link-Listen)
wird als schlichtes HTML mit einer Handvoll fester Klassennamen gebaut
und dann in `page.contentHtml` eingefügt, genau wie eine konvertierte
Markdown-Seite:

| Klasse | Wo sie auftaucht |
|---|---|
| `blog-post-card` / `blog-post-card--draft` | Die Card jedes Beitrags auf `/blog/`, einer Kategorie-Seite oder einer Archiv-Seite |
| `blog-post-meta` | Die Datum-/Autor-/Lesezeit-Zeile, auf einer Card und auf der eigenen Seite eines Beitrags |
| `blog-post-featured-image` | Die `image`-Frontmatter eines Beitrags, auf seiner eigenen Detailseite |
| `blog-draft-badge` | Das "🚧 Entwurf"-Banner (nur `bxSites serve`) |
| `blog-pager` | Vorherige-/Nächste-Pagination-Links auf einer paginierten Liste |
| `blog-author-profile` | Der Bio-/Socials-Block eines Autors auf seiner `/blog/authors/<id>/`-Seite |
| `blog-archive-links` / `blog-category-links` | Die "Nach Jahr durchsuchen"-/"Nach Kategorie durchsuchen"-Linkblöcke auf `/blog/` |

Zwei Wege, es umzugestalten, genau wie bei jeder anderen Seite:

- **Eine schnelle visuelle Anpassung** - ziele mit deinem eigenen
  [`extraCss`](../configuration.md#extracss--extrajs) auf diese Klassen,
  genauso wie du
  [die Farben eines Themes anpasst](themes.md#farben-anpassen-ohne-ein-theme-zu-überschreiben).
  Die eigenen Regeln eines integrierten Themes für diese Klassen liegen
  in seiner `assets/style.css` (z. B.
  `resources/themes/bootstrap/assets/style.css`), wenn du einen
  Ausgangspunkt zum Überschreiben brauchst.
- **Strukturelle Änderungen** - da Blog-Seiten `layout.bxm`/`page.bxm`
  mit allem anderen teilen, ändert das
  [Überschreiben eines Themes](themes.md#ein-theme-überschreiben) (oder das
  [Schreiben eines von Grund auf](themes.md#ein-theme-von-grund-auf-schreiben))
  das Chrome des Blogs (Header, Nav, Footer, Artikel-Wrapper) gleich mit
  jeder anderen Seite - es gibt keine separate Blog-Vorlage zu kopieren.

Was du nicht tun kannst, ist, das Markup von Beitrags-Card/Pager/
Autoren-Profil selbst gegen ein eigenes auszutauschen - es wird einmal
von `BlogBuilder.bx` erzeugt, nicht aus einer Vorlagendatei in `theme/`
gelesen, sodass das Umgestalten mit CSS (oben) der unterstützte Weg ist,
statt eine Pro-Komponenten-Überschreibung.
