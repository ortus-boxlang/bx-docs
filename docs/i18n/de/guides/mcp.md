---
title: MCP-Server
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Aktiviere mcp, um bei jedem Build eine vollständige, unabgeschnittene site/mcp-index.json zu schreiben, damit bxSites Cloud deine veröffentlichte Website über einen öffentlichen, schreibgeschützten MCP-Server für KI-Agenten bereitstellen kann - unabhängig von search/searchProvider.
tags: [anleitungen, mcp, ai]
---

# MCP-Server

Schalte `mcp: true` in `bxsites.yaml` ein, und jeder `build` schreibt
`site/mcp-index.json` neben deinen gerenderten Seiten - eine
vollständige, maschinenlesbare Kopie des Inhalts deiner Website, die
[bxSites Cloud](https://bxsites.io/cloud) liest, um einen öffentlichen,
schreibgeschützten [MCP](https://modelcontextprotocol.io/)-Server für
deine veröffentlichte Website bereitzustellen - dieselbe Idee wie
[GitBooks „MCP servers for published docs"](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Nach der Veröffentlichung können KI-Agenten und -Assistenten (Claude,
ChatGPT und jeder andere MCP-fähige Client) den Inhalt deiner Website
direkt durchsuchen und abrufen, statt gerendertes HTML zu scrapen.

BxSites selbst erzeugt ausschließlich die auf dieser Seite beschriebene
Datei `mcp-index.json` - sie tatsächlich als MCP-Server über das
Netzwerk auszuliefern, ist Aufgabe von bxSites Cloud, nicht etwas, das
dieses Modul selbst übernimmt.

## Aktivieren

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

`false` (Standard) überspringt den gesamten Schritt - es wird keine
`mcp-index.json` geschrieben, und `build` verursacht über die reine
Prüfung des Flags hinaus keine zusätzlichen Kosten.

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
Navigation verwenden):

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ..."
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

Der einzige Unterschied zur Eintragsform von `search-index.json` ist
`body`: dort wird sie auf 400 Zeichen mit abschließender Ellipse
gekürzt; hier ist es die vollständige Seite.

## Websites mit mehreren Versionen und Sprachen

Genau wie `search-index.json` wird `mcp-index.json` einmal pro
gerendertem Baum geschrieben - die Hauptseite, `/next/` (wenn
`versions.default` gesetzt ist), jeder `/versions/<name>/`-Baum und
jeder Sprach-Unterbaum -, sodass jeder veröffentlichte Baum seine
eigene `mcp-index.json` erhält, die nur die eigenen Seiten dieses
Baums abdeckt.
