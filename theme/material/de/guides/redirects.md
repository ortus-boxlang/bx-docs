---
title: Weiterleitungen
order: 11
icon: phosphor-duotone:signpost
tags: [anleitungen, weiterleitungen]
---

# Weiterleitungen

Halte eine alte URL funktionsfähig, nachdem du eine Seite verschiebst,
umbenennst oder umstrukturierst - an der alten URL wird ein statischer
HTML-Stub geschrieben, sodass ein veralteter Index-Eintrag einer
Suchmaschine oder jemandes altes Lesezeichen weiterhin auf der richtigen
Seite landet, statt einen 404 zu liefern. Es ist keine serverseitige
Rewrite-Regel beteiligt (ein statischer Host hat nirgendwo, um eine
auszuführen) - der Stub ist gerade genug HTML, damit ein Browser sich
selbst weiterleitet und ein Crawler die echte kanonische URL erfährt.

## Pro Seite: Frontmatter `redirect_from`

Füge einen oder mehrere alte Pfade zur eigenen Frontmatter einer Seite hinzu:

```md title="docs/guides/new-setup.md"
---
title: New Setup
redirect_from:
  - guides/old-setup
  - setup
---
```

Jeder Eintrag ist ein hübsches URL-Segment - kein
führender/abschließender Schrägstrich, keine `.md`/`.html`-Dateiendung -
dieselbe Form, die die eigene URL der Seite annimmt. Ein Build schreibt
dann für jeden einen Stub (`site/guides/old-setup/index.html`,
`site/setup/index.html` für das obige Beispiel), die beide zur echten
eigenen URL dieser Seite weiterleiten.

`redirect_from` ist auf den jeweiligen Baum beschränkt, zu dem die Seite
selbst gehört - die eigene Seite einer Version leitet innerhalb dieser
Version weiter (`site/versions/2.0/old-path/`), die eigene übersetzte
Seite einer Locale leitet innerhalb dieser Locale weiter
(`site/es/old-path/`), genau so, wie es die eigene echte URL der Seite
bereits tut. Es gibt nichts Zusätzliches pro Baum zu konfigurieren.

## Websiteweit: `bxsites.yaml` `redirects`

Für eine alte URL, die nie zu einer bestimmten Seite gehörte - ein
umstrukturierter Abschnitt, der Pfad einer alten Domain, alles, was
nicht natürlich der eigene "alte Name" einer einzelnen Seite ist -
liste stattdessen ein explizites `from`/`to`-Paar auf:

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

- `from` - das alte hübsche URL-Segment, dieselbe Form wie `redirect_from` oben
- `to` - entweder ein root-relativer Pfad (aufgelöst gegen die eigene
  `baseURL` der Site, dieselbe Konvention, die `theme.logo`/`ogImage`
  bereits verwenden) oder eine vollständige `https://`-URL, um komplett
  auf eine andere Site weiterzuleiten

`redirects` gilt immer nur für den Hauptbaum der Site - ein bloßes `to`
ist ein root-relativer Pfad, der nur an der Site-Wurzel eindeutig ist.
Ein Versions-/Locale-Baum, der dieselbe alte URL-Zuordnung möchte,
braucht stattdessen sein eigenes seitenbezogenes `redirect_from`.

## `page:rename` setzt das für dich

Das Umbenennen/Verschieben einer Seite mit
[`page:rename`](../cli-reference.md#pagerename) fügt ihren alten Pfad
automatisch dem eigenen `redirect_from` der verschobenen Seite hinzu -
zusätzlich zum Umschreiben jedes relativen Markdown-Links, der darauf
zeigte, funktioniert auch die alte URL selbst weiter:

```bash title="Usage"
bxSites page:rename --from=guides/old-setup.md --to=guides/new-setup.md
```

Eine Seite mehr als einmal umzubenennen, fügt einfach weiter hinzu - die
`redirect_from`-Liste einer Seite kann so viele alte Pfade tragen, wie sie
im Laufe der Zeit hatte.

## Konflikte

Ein Build schlägt komplett fehl, statt echten Inhalt stillschweigend zu
überschreiben, wenn:

- Der eigene `from`-Pfad einer Weiterleitung mit einer echten Seite
  kollidiert, die bereits an diesem Pfad gebaut wurde (`BxSites.RedirectConflict`)
- Zwei Weiterleitungen (`redirect_from`-Einträge, `redirects`-Konfigurationseinträge,
  oder einer von jedem) beide denselben `from`-Pfad anvisieren

## Was (vorerst) außen vor bleibt

- **Blogbeiträge erhalten kein `redirect_from`.** Der Frontmatter-Schlüssel
  wird nur für reguläre `docs/`-Seiten gelesen, nicht für
  `docs/blog/posts/**` - ein verschobener Blogbeitrag braucht stattdessen
  seinen eigenen `redirects`-Konfigurationseintrag.
- **Keine Wildcard-/Muster-Weiterleitungen.** Jedes `from` ist ein
  exakter alter Pfad - es gibt kein `guides/old/*`-Catch-all.
