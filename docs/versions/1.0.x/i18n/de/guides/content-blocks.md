---
title: Content-Blöcke
order: 4.5
icon: phosphor-duotone:squares-four
tags: [anleitungen, markdown]
---

# Content-Blöcke

Zusätzlich zu allem in [Markdown-Erweiterungen](markdown.md) unterstützt
BxSites eine Familie von umfangreichen Content-Blöcken für Dinge, die im
reinen CommonMark gar nicht vorgesehen sind - Cards, Tabs aus Schritten,
Downloads, Embeds und mehr. Jeder verwendet dieselbe Container-Syntax
`::: name ... :::` (ein einzelnes `:::` in seiner eigenen Zeile schließt
den jeweils gerade offenen Block, oder du schreibst das schließende
`:::` direkt in dieselbe Zeile für einen Block ohne eigenen Inhalt -
`::: file src="assets/spec.pdf" :::` funktioniert genau wie die
zweizeilige Form) - keine `bxsites.yaml`-Konfiguration nötig, immer
verfügbar. Ein Block kann in einem anderen verschachtelt sein (etwa ein
Expandable mit einer Cards-Gruppe darin) - jeder wird erneut nach
weiteren Blöcken in seinem eigenen Inhalt durchsucht. Migrierst du von
GitBook? Jeder Block hier bildet direkt auf sein gleichnamiges
GitBook-Gegenstück ab - siehe
[Migration von GitBook](migrating-from-gitbook.md).

## Expandable

Ein einfacher einklappbarer Bereich - kein Callout-Icon/keine Farbe, im
Gegensatz zu einer einklappbaren Admonition (`???`, siehe
[Admonitions](markdown.md#einklappbare-admonitions)):

```markdown title="Beispiel" linenums="1"
::: expandable "Is this different from a collapsible admonition?"
Yes - this has no type/icon/color, just a plain expand/collapse section.
Add `open="true"` to start it expanded.
:::
```

::: expandable "Unterscheidet sich das von einer einklappbaren Admonition?"
Ja - dies hat kein Typ/Icon/Farbe, nur einen einfachen
Auf-/Zuklapp-Bereich. Füge `open="true"` hinzu, um ihn ausgeklappt zu
starten.
:::

## Cards

Ein Raster aus Link-Cards, jede ihr eigenes `::: card` innerhalb eines
`::: cards`-Wrappers - `title`, `icon`, `image` und `href` sind alle
optional (eine Card ohne `href` wird als schlichte, nicht klickbare Card
gerendert). `icon` wird auf dieselbe Weise aufgelöst wie
Frontmatter-/Nav-`icon`-Werte - ein reines Emoji, oder ein benanntes
Icon aus einer mitgelieferten Bibliothek (`icon="phosphor-duotone:rocket-launch"`,
`icon="lucide:rocket"`, ...) - siehe [Icons](icons.md):

```markdown title="Beispiel" linenums="1"
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
::: card title="Erste Schritte" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Installiere, erstelle und baue deine erste Website.
:::
::: card title="Themes" icon="phosphor-duotone:palette" href="themes.md"
Passe ein integriertes Theme an oder schreibe dein eigenes.
:::
:::

## Columns

Ein nebeneinanderliegendes Layout - `::: column` akzeptiert ein
optionales `width` (eine reine CSS-Länge/-Prozentangabe, z. B. `"40%"`);
Spalten ohne explizite Breite teilen sich die Reihe gleichmäßig:

```markdown title="Beispiel" linenums="1"
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
Die breitere Spalte.
:::
::: column
Die schmalere.
:::
:::

## Stepper

Eine nummerierte, verbundene Abfolge von Schritten:

```markdown title="Beispiel" linenums="1"
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
::: step "Installieren"
`install-bx-module bx-sites`
:::
::: step "Aufsetzen"
`bxSites new`
:::
:::

Das eigene, optionale `color`-Attribut eines Schritts markiert seinen
Marker mit einer von vier semantischen Farben - dem Standard (kein
`color`), `success`, `warning` oder `danger` - unabhängig von der
Position des Schritts in der Abfolge:

```markdown title="Beispiel" linenums="1"
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
::: step "Daten sichern" color="success"
Routineaufgabe, jederzeit sicher auszuführen.
:::
::: step "Optional: Telemetrie aktivieren" color="warning"
Überspringe diesen Schritt, wenn du dir nicht sicher bist.
:::
::: step "Alte Installation löschen" color="danger"
Unumkehrbar - stelle sicher, dass die Sicherung oben abgeschlossen ist.
:::
:::

Der nummerierte Marker, die Verbindungslinie und jede der drei
`color`-Paletten oben lassen sich unabhängig von der restlichen Palette
der Website themen, über CSS-Custom-Properties - siehe
[Farben anpassen](themes.md#farben-anpassen-ohne-ein-theme-zu-überschreiben).

## File

Eine Download-Card für ein PDF, ein Video oder ein beliebiges anderes
Projekt-Asset - `src` wird auf dieselbe Weise aufgelöst wie
`theme.logo`/die Frontmatter-`ogImage` (relativ zu `docs/assets/`):

```markdown title="Beispiel" linenums="1"
::: file src="assets/spec.pdf" title="API Specification" :::
```

::: file src="assets/og-image.png" title="Site Preview Image" :::

## Buttons

Eine Call-to-Action-Schaltfläche - `::: button` für sich allein, oder
mehrere nebeneinander in einer Reihe innerhalb eines
`::: buttons`-Wrappers. Das führende `"Label"` und `href` sind die
einzigen Angaben, die die meisten Schaltflächen brauchen:

```markdown title="Beispiel" linenums="1"
::: button "Get Started" href="../getting-started.md" style="primary" :::
```

::: button "Erste Schritte" href="../getting-started.md" style="primary" :::

Ein paar optionale Attribute verleihen jeder Schaltfläche ihre eigenen
Fähigkeiten:

- `style="primary"` oder `style="secondary"` (der Standard) - voll
  ausgefüllt in Akzentfarbe vs. Outline.
- `size="small"`, `"medium"` (der Standard) oder `"large"`.
- `icon="..."` - wird auf dieselbe Weise aufgelöst wie das eigene `icon`
  einer Card (ein reines Emoji, oder ein benanntes Icon wie
  `icon="phosphor-duotone:rocket-launch"` - siehe
  [Themes: Icons](themes.md#icons)).
- `target="_blank"` - öffnet den Link in einem neuen Tab statt im selben
  (`rel="noopener noreferrer"` wird automatisch hinzugefügt).
- `disabled="true"` - rendert eine inaktive, nicht klickbare Schaltfläche
  (kein `href` nötig) für einen "Demnächst verfügbar"-Aufruf.

```markdown title="Beispiel" linenums="1"
::: buttons
::: button "Read the docs" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Star on GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Coming soon" disabled="true" :::
:::
```

::: buttons
::: button "Dokumentation lesen" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Auf GitHub anschauen" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Demnächst verfügbar" disabled="true" :::
:::

## Embed

Ein responsives iframe-Embed für einen erkannten Anbieter - derzeit
YouTube, Vimeo, CodePen, Spotify, Loom und Figma. Eine URL von woanders
fällt stattdessen auf eine schlichte "visit ↗"-Link-Card zurück, statt
auf ein iframe, das ohnehin nicht rendern würde (die meisten Websites
blockieren das Einbetten in Frames):

```markdown title="Beispiel" linenums="1"
::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::
```

::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::

## Page Link

Eine ausführliche Vorschau-Card, die zu einer anderen Seite verlinkt -
`href` folgt derselben dateirelativen Konvention wie ein gewöhnlicher
[Seiten-Link](../getting-started.md#zwischen-seiten-verlinken). Anders als
eine Card werden Titel/Icon/Zusammenfassung automatisch aus der eigenen
Frontmatter der Zielseite gezogen, sodass sie synchron bleibt, wenn
diese Seite umbenannt wird oder sich ihre Zusammenfassung ändert:

```markdown title="Beispiel" linenums="1"
::: page-link href="../getting-started.md" :::
```

::: page-link href="../getting-started.md" :::

## Link Preview

Eine ausführliche Vorschau-Card für eine *externe* URL - dieselbe
Card-Form wie `::: page-link`, aber für einen Link, der keine der
eigenen Seiten dieser Website ist, es also keine Seite gibt, aus der
sich Titel/Zusammenfassung automatisch ziehen ließen. Jedes Feld kommt
aus den eigenen Attributen der Direktive: nur `url` ist erforderlich,
`title` fällt, wenn weggelassen, auf die reine URL zurück, und
`description`/`image` sind beide optional. Es gibt keinen
Build-Zeit-Abruf der Ziel-URL, um diese automatisch zu befüllen -
dieselbe Überlegung, die [`site:check`](../cli-reference.md#sitecheck) auf
interne Links beschränkt, gilt auch hier, sodass eine langsame oder
nicht erreichbare externe Website die Build-Zeit niemals beeinflusst:

```markdown title="Beispiel" linenums="1"
::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::
```

::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::

## KI-Prompt

Ein gestalteter Container für einen wiederverwendbaren KI-Prompt. Der
Inhalt des Blocks *ist* der Prompt-Text, geschrieben als gewöhnliches
Markdown (sodass Überschriften, Listen und Code darin weiterhin ihre
eigene Formatierung erhalten); jeder Prompt bekommt eine
"Copy"-Schaltfläche, die genau diesen Quelltext kopiert,
Formatierungs-Markup inklusive, bereit zum Einfügen in das jeweilige
KI-Tool, mit dem du gerade arbeitest. `description` (eine optionale,
einzeilige Zusammenfassung) und `icon` (aufgelöst auf dieselbe Weise wie
das eigene `icon` von `::: card` - fällt bei Weglassen standardmäßig auf
ein Glitzer-Icon zurück) sind beide optional:

```markdown title="Beispiel" linenums="1"
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

Füge `expanded="preview"` hinzu, um einen langen Prompt auf eine kurze,
ausblendende Vorschau zu klemmen, bis die Leserin auf "Show more"
klickt, oder `expanded="hidden"`, um ihn vollständig eingeklappt hinter
einer "Show prompt"-Schaltfläche zu starten - praktisch für eine Seite,
die mehrere Prompts hintereinander auflistet. Lasse `expanded` weg (oder
setze es auf `"full"`, den Standard), um immer den gesamten Prompt
anzuzeigen:

```markdown title="Beispiel" linenums="1"
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

Es gibt hier kein "Open in AI providers"-Menü - bx-sites spricht nie mit
einem KI-Anbieter eines Drittanbieters, daher ist die eigene
"Copy"-Schaltfläche eines Prompts der einzige Weg, ihn in das jeweilige
Tool zu bekommen, mit dem du gerade arbeitest.

## Updates (Changelog)

Eine datierte, taggbare Changelog-Liste - `::: update` akzeptiert
`date="YYYY-MM-DD"` und optional durch Kommas getrennte `tags`:

```markdown title="Beispiel" linenums="1"
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
::: update date="2026-01-15" tags="feature,fix"
Dunkelmodus hinzugefügt und einen Ausrichtungsfehler in der Fußzeile behoben.
:::
::: update date="2026-01-01"
Erstveröffentlichung.
:::
:::

Eine Seite mit einem `::: updates`-Block erhält außerdem ihre eigene
`feed.xml` (RSS 2.0), die daneben geschrieben wird, sobald `baseURL` in
`bxsites.yaml` eine vollständige URL ist - dieselbe Voraussetzung wie
bei `sitemap.xml` - sodass Leser genau den Changelog dieser einen Seite
abonnieren können.

## Wiederverwendbare Inhalte (Includes)

`::: include src="..."` fügt an dieser Stelle das rohe Markdown einer
anderen Datei ein. Anders als jeder Block oben wird daraus echter
Seiteninhalt (Überschriften, Absätze, seine eigenen verschachtelten
Blöcke), nicht etwas, das in ein Widget verpackt wird - nützlich für
einen Warn-/Hinweistext, der sich über mehrere Seiten wiederholt. Lege
das Partial selbst unter `docs/includes/` ab - dieselbe reservierte
Ordner-Konvention wie `assets/`/`versions/`/`i18n/`/`blog/`. Eine Datei
unter `includes/` wird nie als eigene Seite gebaut und erscheint nie in
Navigation/Suche/Sitemap/Tags - sie existiert nur, um in andere Seiten
eingefügt zu werden:

```text title="docs/-Layout"
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

Ein **bloßer** `src` (ohne führendes `./` oder `../`) wird immer gegen
das eigene `docs/includes/` des aktuellen Baums aufgelöst, egal wie tief
die einbindende Seite selbst verschachtelt ist - `guides/deep/setup.md`
oben erreicht dieselbe Datei wie `index.md`, beide mit exakt demselben
`src`:

```markdown title="Aus index.md oder guides/deep/setup.md"
::: include src="beta-notice.md"
```

Ein bloßer `src` kann auch in einen Unterordner von `includes/` selbst
zeigen:

```markdown title="Beispiel"
::: include src="legal/terms.md"
```

Stelle stattdessen `./` oder `../` vor `src`, um ein seitennahes
Fragment zu erreichen, das nicht im zentralen `includes/`-Ordner leben
soll - diese Form löst dateirelativ zum eigenen Verzeichnis der
*einbindenden* Seite auf, dieselbe Konvention wie ein gewöhnlicher
Seiten-Link:

```markdown title="Aus guides/deep/setup.md, eine Ebene höher statt zentralisiert"
::: include src="../local-note.md"
```

Ein Versions-/Locale-Baum erhält sein eigenes `includes/` auf dieselbe
Weise - eine Seite unter `docs/versions/2.0/` löst einen bloßen `src`
gegen `docs/versions/2.0/includes/` auf, und eine unter
`docs/i18n/es/` gegen `docs/i18n/es/includes/` - die Partials jedes
Baums gehören ihm selbst, sie werden nicht mit dem `docs/includes/` des
Hauptbaums geteilt.

Eine eingebundene Datei kann selbst eine weitere einbinden (eine
zirkuläre Kette wirft zur Build-Zeit `BxSites.CircularInclude`, statt
endlos zu laufen).

## Conditional content

Zeigt eine von mehreren Varianten eines Blocks, je nach der eigenen Wahl
der Leserin - "Free" vs. "Pro"-Anleitung auf derselben Seite, zum
Beispiel. Das hier ist eine vollständig statische Website ohne jede Art
von Besucher-Identität, also gibt es, anders als bei einer Plattform mit
echtem Backend, kein serverseitig ausgewertetes "wer ist diese Leserin" -
die Leserin trifft die Wahl selbst, und ihre Wahl wird einfach im eigenen
Browser (`localStorage`) gemerkt, auch für jede spätere Seite:

```markdown title="Beispiel" linenums="1"
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

`::: conditional key="..." value="..."` kennzeichnet eine Variante; `key`
ist der jeweilige Präferenzname, auf den umgeschaltet wird (`"plan"`
oben, es könnte aber genauso gut `"os"`, `"language"`, was auch immer
sein), und `value` ist die eine Einstellung, für die dieser bestimmte
Block angezeigt werden soll. Jede Variante wird immer im HTML gerendert
- clientseitig nur versteckt, niemals weggelassen - sodass eine Leserin
mit deaktiviertem JavaScript (oder ein Such-Crawler) weiterhin jede
Variante sieht statt keine.

`::: audience-switcher key="..." options="value:Label,value:Label,..."`
ist ein optionales, fertiges Steuerelement - eine Schaltfläche pro
Option, die sofort jeden `::: conditional`-Block mit demselben `key`
umschaltet, überall auf der Seite. Du brauchst es überhaupt nicht: ein
Link, der auf `?plan=pro` endet, setzt beim Laden automatisch dieselbe
Präferenz (praktisch, um einen direkten Link zu "der Pro-Version dieser
Seite" zu teilen), und das eigene Theme-Override eines Projekts kann
stattdessen direkt `window.bxSitesSetPreference( key, value )` aufrufen,
um es von einer eigenen UI aus zu steuern.

## Schleife und Bedingung (datengesteuert)

`::: for` und `::: if` rendern ihren eigenen Inhalt gegen
[wiederverwendbare Daten](data-files.md) - den eigenen Wert einer
`docs/data/*.yaml`-/`.json`-Datei, adressiert per Punktpfad. Anders als
jeder Block oben nehmen diese beiden einen bloßen Ausdruck statt
`key="value"`-Attributen entgegen - bewusst schmal, dieselbe
Nur-Punktpfad-Philosophie, die `{{ }}` selbst bereits verwendet (keine
Vergleichsoperatoren in dieser ersten Version):

```markdown title="Beispiel" linenums="1"
::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::
```

::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::

`::: for <item>, <index> in <dotted.path>` bindet `<item>`/`<index>` auf
dieselbe Weise, wie es BoxLangs eigene Zwei-Variablen-`for`-Schleife für
das tut, worauf der Pfad auflöst - Element + 1-basierter Index für ein
Array (wie oben), oder Schlüssel + Wert für ein Struct, in beiden Fällen
dieselbe Syntax:

```markdown title="Beispiel" linenums="1"
::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::
```

::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::

`::: if <dotted.path>` rendert seinen Inhalt nur, wenn der aufgelöste
Wert truthy ist - ein leeres Array/Struct/String, `0` und `false` gelten
allesamt als falsy:

```markdown title="Beispiel" linenums="1"
::: if data.flags.betaBanner
Beta features are enabled on this build.
:::
```

::: if data.flags.betaBanner
Beta-Features sind in diesem Build aktiviert.
:::

Verkette `::: elseif <dotted.path>` (beliebig viele davon) und ein
abschließendes, bloßes `::: else` nach einem `::: if` für echte
`if`/`elseif`/`else`-Semantik - die erste truthy Bedingung gewinnt,
`::: else` (ohne eigene Bedingung) fängt auf, was übrig bleibt, und eine
Bedingung nach der gewinnenden wird nie überhaupt aufgelöst, sodass ein
fehlerhafter `::: elseif`-Pfad den Build erst dann bricht, wenn sein
eigener Zweig tatsächlich erreicht wird. Die gesamte Kette schließt mit
**einem** abschließenden `:::` - `::: elseif`/`::: else` markieren
selbst, wo der vorherige Zweig endet, es ist also kein `:::` vor jedem
von ihnen nötig:

```markdown title="Beispiel" linenums="1"
::: if data.flags.darkModeDefault
Dark mode is on by default.
::: elseif data.flags.betaBanner
Beta features are enabled, though dark mode isn't on by default.
::: else
Nothing special about this build.
:::
```

::: if data.flags.darkModeDefault
Der Dunkelmodus ist standardmäßig aktiviert.
::: elseif data.flags.betaBanner
Beta-Features sind aktiviert, auch wenn der Dunkelmodus nicht
standardmäßig an ist.
::: else
Nichts Besonderes an diesem Build.
:::

Ein `:::` vor einem `::: elseif`/`::: else` funktioniert ebenfalls
weiterhin, falls du lieber jeden Zweig explizit schließen möchtest -
beide Formen werden identisch geparst.

Beide Inhalte können gewöhnliches Markdown und sogar weitere
Content-Blöcke enthalten - einschließlich eines weiteren
`::: for`/`::: if`, genauso verschachtelt wie jeder Block oben. Siehe
[Datendateien: Daten verwenden](data-files.md#daten-verwenden) für die
vollständige Schleifen-/Bedingungs-Geschichte, einschließlich der zwei
weiteren Wege, mit `data.*` zu arbeiten - ein Theme-Override, oder eine
magische Funktion.

## Kursindex

`::: course id="..." :::` rendert die gesamten Lektionen eines
[Kurses](courses.md) als einen einzigen, nummerierten, verlinkten Index
- eine große "1. Introduction, 2. Windows Installation, 3. Mac
Installation ..."-Liste, jede Nummer ein echter Link, aufgebaut aus
einem `docs/data/courses.yaml`-Manifest statt von Hand verfasst:

```markdown title="Example" linenums="1"
::: course id="getting-started" :::
```

Anders als jeder Block oben nimmt dieser nur eine bloße `id` entgegen,
keinen eigenen `href`/Inhalt - die Lektionen selbst, und ihre
Reihenfolge, stammen vollständig aus dem Manifest. Siehe
[Kurse](courses.md) für das Manifestformat, die kursbezogene
Lektion-zu-Lektion-Navigation, und wie der Lesefortschritt erfasst
wird, sobald der Index auf der Seite steht.
