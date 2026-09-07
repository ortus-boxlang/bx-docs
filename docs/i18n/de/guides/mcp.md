---
title: MCP-Server
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Aktiviere mcp, um bei jedem Build eine vollständige, unabgeschnittene site/mcp-index.json (sowie site/mcp-manifest.json und eine mcp-nav.json pro Baum) zu schreiben, damit bxSites Cloud deine veröffentlichte Website über einen öffentlichen, schreibgeschützten MCP-Server für KI-Agenten bereitstellen kann - unabhängig von search/searchProvider.
tags: [anleitungen, mcp, ai]
---

# MCP-Server

Schalte `mcp: true` in `bxsites.yaml` ein, und jeder `build` schreibt drei
Dateien neben deinen gerenderten Seiten - eine vollständige,
maschinenlesbare Kopie des Inhalts, der Navigation und der Baumstruktur
deiner Website, die [bxSites Cloud](https://bxsites.io/cloud) liest, um
einen öffentlichen, schreibgeschützten
[MCP](https://modelcontextprotocol.io/)-Server für deine veröffentlichte
Website bereitzustellen - dieselbe Idee wie
[GitBooks „MCP servers for published docs"](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Nach der Veröffentlichung können KI-Agenten und -Assistenten (Claude,
ChatGPT und jeder andere MCP-fähige Client) den Inhalt deiner Website
direkt durchsuchen, browsen und abrufen, statt gerendertes HTML zu
scrapen:

- `site/mcp-index.json` - ein vollständiger Eintrag pro Seite, pro Baum
- `site/mcp-nav.json` - die eigene Navigationsstruktur dieses Baums, pro Baum
- `site/mcp-manifest.json` - ein websiteweiter Index aller Bäume
  (Hauptseite, Versionen, Sprachen) und wo die eigenen Dateien jedes
  Baums von oben zu finden sind

BxSites selbst erzeugt ausschließlich die auf dieser Seite beschriebenen
Dateien - sie tatsächlich als MCP-Server über das Netzwerk auszuliefern,
ist Aufgabe von bxSites Cloud, nicht etwas, das dieses Modul selbst
übernimmt.

!!! note "Erfordert einen kostenpflichtigen bxSites-Cloud-Plan"
    Das Erzeugen von `mcp-index.json` selbst ist kostenlos und funktioniert
    allein mit `bxSites build`, ganz ohne Cloud-Konto. Es tatsächlich als
    laufenden MCP-Server für eine veröffentlichte Website bereitzustellen,
    ist eine Funktion von bxSites Cloud, die nur in den kostenpflichtigen
    Plänen enthalten ist - nicht im kostenlosen Plan. Aktuelle Details zu
    den Plänen findest du unter
    [bxsites.io/cloud](https://bxsites.io/cloud).

## Aktivieren

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

`false` (Standard) überspringt den gesamten Schritt - es werden weder
`mcp-index.json`, `mcp-nav.json` noch `mcp-manifest.json` geschrieben,
und `build` verursacht über die reine Prüfung des Flags hinaus keine
zusätzlichen Kosten.

## Unabhängig von `search`

`mcp` hat nichts mit [`search`/`searchProvider`](search.md) zu tun - die
beiden sind voneinander unabhängige Schalter:

- `mcp-index.json` wird unabhängig davon erzeugt, ob `search` `true`
  oder `false` ist, und unabhängig von `searchProvider.provider`
  (`"local"`, `"algolia"`, `"pagefind"` oder ein eigener).
- Das Aktivieren von `mcp` ändert nie etwas an der eigenen,
  besucherseitigen Suchbox deiner Website, und das Deaktivieren von
  `search` deaktiviert niemals `mcp-index.json`.

Deshalb kann `mcp-index.json` nicht einfach `search-index.json`
wiederverwenden: Diese Datei wird nur für den `"local"`-Such-Provider
gebaut (eine Algolia- oder Pagefind-gestützte Website erzeugt gar keine
`search-index.json`, da beide ihren eigenen Index anderswo führen), und
ihr `body`-Feld ist absichtlich abgeschnitten - `search-index.json` wird
an den Browser jedes Besuchers für die Suchbox auf der Seite
ausgeliefert, weshalb es wichtig ist, sie klein zu halten. Keine dieser
Einschränkungen gilt für `mcp-index.json`: Sie wird serverseitig von
bxSites Cloud abgerufen, nicht an Besucher ausgeliefert, und ein
abgeschnittener Textkörper riskiert, dass ein KI-Agent eine unvollständige
oder falsche Antwort gibt.

## Das Format von `mcp-index.json`

Ein Eintrag pro nicht versteckter Seite (dieselbe Konvention „versteckte
Seiten werden ausgeschlossen", die auch `search-index.json`/die
Navigation verwenden), sowohl für reguläre Doku-Seiten als auch für
[Blog](blog.md)-Beiträge:

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ...",
    "type": "page",
    "categories": [],
    "updatedAt": "2026-08-18T10:15:00.000Z"
  },
  {
    "title": "Announcing bxSites 2.0",
    "url": "blog/announcing-bxsites-2/index.html",
    "tags": ["release"],
    "headings": ["Announcing bxSites 2.0"],
    "body": "Announcing bxSites 2.0 Today we're shipping...",
    "type": "post",
    "categories": ["Releases"],
    "updatedAt": "2026-08-15"
  }
]
```

| Feld | Beschreibung |
|---|---|
| `title` | Der Titel der Seite (Frontmatter `title`, wie bei `search-index.json`) |
| `url` | Der URL-Pfad der Seite, relativ zur Website-Wurzel |
| `tags` | Das Frontmatter-Array `tags` der Seite |
| `headings` | Der reine Text jeder Überschrift `h1`-`h6` auf der Seite, in Dokumentreihenfolge |
| `body` | Der **vollständige** reine Textinhalt der Seite, HTML-Tags entfernt - nie abgeschnitten |
| `type` | `"page"` für eine reguläre Doku-Seite, `"post"` für einen [Blog](blog.md)-Beitrag |
| `categories` | Das eigene Frontmatter-Array `categories` eines Beitrags; bei einer Doku-Seite immer `[]` (Doku-Seiten haben keine Kategorien) |
| `updatedAt` | Das eigene Frontmatter-`date` eines Beitrags; bei einer Doku-Seite deren eigenes Frontmatter-`date`, sofern gesetzt, sonst der Zeitpunkt der letzten Änderung ihrer Quelldatei als ISO-8601-Zeitpunkt. `""`, wenn keines von beidem verfügbar ist. |

Abgesehen von `type`/`categories`/`updatedAt` ist dies dieselbe
Eintragsform wie bei `search-index.json`, mit einem Unterschied: dort
wird `body` auf 400 Zeichen mit abschließender Ellipse gekürzt; hier ist
es die vollständige Seite.

## Das Format von `mcp-manifest.json`

Einmal pro Website-Build geschrieben (nicht einmal pro Baum), listet
`site/mcp-manifest.json` jeden Baum auf, der eine eigene
`mcp-index.json`/`mcp-nav.json` erhalten hat, damit der MCP-Server von
bxSites Cloud versions- und sprachbewusste Tools anbieten kann, ohne die
eigenen Verzeichniskonventionen von bx-sites erraten zu müssen:

```json title="site/mcp-manifest.json"
[
  { "path": "", "label": "1.0.x", "version": "1.0.x", "locale": "en", "default": true },
  { "path": "next", "label": "Next", "version": null, "locale": "en", "default": false },
  { "path": "versions/0.9", "label": "0.9", "version": "0.9", "locale": "en", "default": false },
  { "path": "es", "label": "Español", "version": "1.0.x", "locale": "es", "default": false }
]
```

| Feld | Beschreibung |
|---|---|
| `path` | Der eigene, wurzelrelative Pfad dieses Baums - `""` für den Hauptbaum an der Website-Wurzel, sonst `"next"`/`"versions/<name>"`/`"<localeCode>"`/`"versions/<name>/<localeCode>"`. Verbinde ihn mit `/mcp-index.json` (oder `/mcp-nav.json`), um die eigene Datei dieses Baums zu erhalten, z. B. `"versions/0.9/mcp-index.json"` - und für die Wurzel selbst einfach `"mcp-index.json"` (dort ist `path` gleich `""`). |
| `label` | Eine menschenlesbare Bezeichnung für diesen Baum - die eigene Bezeichnung des Versionsschalters (der Name von `versions.default`, der eigene Name einer einfachen Version oder `"Latest"`/`"Next"`), kombiniert mit der eigenen Bezeichnung der Sprache für einen Sprach-Unterbaum |
| `version` | Der Name aus `docs/versions/<name>/`, den dieser Baum rendert, oder `null`, wenn es sich um keine benannte Version handelt (der unversionierte Hauptbaum oder `/next/`) |
| `locale` | Der eigene Sprachcode dieses Baums - `i18n.defaultLocale.code` für einen Baum ohne Sprach-Suffix, sonst der eigene Code dieser Sprache |
| `default` | `true` für den einen Baum, der an der Website-Wurzel gerendert wird - denjenigen, auf dem ein Besucher/KI-Agent ohne Angabe von Version oder Sprache landet |

## Das Format von `mcp-nav.json`

Neben der eigenen `mcp-index.json` jedes Baums geschrieben (Website-
Wurzel, `/next/`, jeder `/versions/<name>/`-Baum, jeder Sprach-Unterbaum)
ist `mcp-nav.json` die eigene Navigation dieses Baums - genau dieselbe
verschachtelte `{ title, url, order, icon, children }`-Struktur, aus der
das Theme selbst die Seitenleiste rendert -, damit der MCP-Server von
bxSites Cloud ein `get_nav`/Inhaltsverzeichnis-Tool anbieten kann, ohne
eine zweite, eigene Navigationsform zu erfinden:

```json title="site/mcp-nav.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "order": 1,
    "icon": "phosphor-duotone:rocket-launch",
    "children": []
  },
  {
    "title": "Guides",
    "url": "",
    "order": 2,
    "icon": "",
    "children": [
      { "title": "Search", "url": "guides/search/index.html", "order": 1, "icon": "", "children": [] }
    ]
  }
]
```

Ein Ordner-Gruppenknoten ohne eigene `index.md` (wie `"Guides"` oben)
hat eine leere `url` - er ist keine Seite, sondern nur eine Überschrift
für seine `children`.

## Websites mit mehreren Versionen und Sprachen

Genau wie `search-index.json` werden `mcp-index.json`/`mcp-nav.json`
einmal pro gerendertem Baum geschrieben - die Hauptseite, `/next/` (wenn
`versions.default` gesetzt ist), jeder `/versions/<name>/`-Baum und
jeder Sprach-Unterbaum -, sodass jeder veröffentlichte Baum sein eigenes
Dateipaar erhält, das nur die eigenen Seiten und die eigene Navigation
dieses Baums abdeckt. `mcp-manifest.json` hingegen wird einmal pro Build
geschrieben, nur an der Website-Wurzel, und listet jeden dieser Bäume auf.
