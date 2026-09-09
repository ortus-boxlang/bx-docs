---
title: Responsive Bilder
order: 5
icon: phosphor-duotone:image
tags: [anleitungen, bilder, performance]
---

# Responsive Bilder

Für jedes geeignete Bild unter `docs/assets/` werden automatisch skalierte
Varianten und WebP-Varianten erzeugt, und jedes passende `<img>` in deinen
Seiten wird in ein responsives `<picture>` umgeschrieben - keine neue
Markdown-Syntax, keine Konfiguration nötig, um es zu aktivieren. Es baut
auf [bx-image](https://github.com/ortus-boxlang/bx-image) auf, einer
erforderlichen Abhängigkeit neben bx-markdown/bx-esapi/bx-yaml (siehe
[Erste Schritte](../getting-started.md#installation)).

## Wie es funktioniert

Schreibe ein Bild auf die normale Weise - Markdown-Syntax oder rohes HTML,
dateirelativ zur Seite, genau wie ein [Seiten-Link](markdown.md) bereits
funktioniert:

```markdown title="Beispiel"
![Eine frisch gebaute Website](../assets/screenshot.png)
```

Zur Build-Zeit wird `screenshot.png` auf jede konfigurierte Breite
herunterskaliert, die schmaler als die eigene ist (nie hochskaliert),
plus eine WebP-Neucodierung in derselben Größe, und die gebaute Seite
erhält:

```html title="Gerendertes Ergebnis" linenums="1"
<picture>
	<source type="image/webp" srcset="/assets/screenshot-400w.a3f9c2e1.webp 400w, /assets/screenshot-800w.a3f9c2e1.webp 800w, ...">
	<img src="/assets/screenshot.png" srcset="/assets/screenshot-400w.a3f9c2e1.png 400w, /assets/screenshot-800w.a3f9c2e1.png 800w, ..." sizes="(min-width: 800px) 800px, 100vw" alt="A freshly built site">
</picture>
```

Ein Browser wählt die kleinste Variante, die `sizes` erfüllt, in WebP,
wenn er das Format unterstützt, andernfalls mit Fallback auf das
schlichte Original-`src` (weiterhin genau wie zuvor ausgeliefert). Jedes
andere Attribut, das du geschrieben hast - `alt`, `class`, alles
Weitere - wird unverändert auf das umgeschriebene `<img>` übernommen.

Ein Bild ohne konfigurierte Breite, die schmaler als die eigene ist (etwa
ein kleines Icon), erhält trotzdem eine WebP-Neucodierung in voller
Größe, wenn `"webp"` in `assets.images.formats` steht - ein echter
Dateigrößen-Gewinn, auch ganz ohne responsiven Breakpoint.

## Bildunterschriften, Ausrichtung und Rahmung

Eine Bildunterschrift, ein Rahmen oder eine Mehrbild-Galerie sind alle
einfach block-level HTML - das bx-markdown/Flexmark vollständig
unverändert durchreicht (CommonMarks eigene "HTML-Block"-Regel), sodass
dafür überhaupt keine bx-sites-spezifische Syntax nötig ist:

```markdown title="Beispiel" linenums="1"
<figure>
  <img src="../assets/screenshot.png" alt="The build output">
  <figcaption>A freshly built site</figcaption>
</figure>

<div data-with-frame="true">
  <img src="../assets/screenshot.png" alt="Framed">
</div>

<div class="bxsites-gallery">
  <img src="../assets/one.png" alt="">
  <img src="../assets/two.png" alt="">
  <img src="../assets/three.png" alt="">
</div>
```

Dasselbe gilt für `x-data`/`x-show`/`@click` und jedes andere
Alpine.js-Attribut - siehe
[Interaktivität mit Alpine.js](interactivity.md).

## Was nicht skaliert wird

- **SVGs** - bereits auflösungsunabhängig, werden unverändert kopiert.
- **Animierte GIFs** - der Skalierungspfad von bx-image kennt keine
  Frames; ein Skalieren würde sie auf ein einzelnes Frame reduzieren.
  Werden unverändert kopiert, genau wie vor Einführung dieser Funktion.
- **Alles außerhalb von `docs/assets/`** - eine externe Bild-URL
  (`<img src="https://...">`) wird vollständig unangetastet gelassen,
  genau wie [`extraCss`/`extraJs`](../configuration.md#extracss--extrajs)
  eine absolute URL bereits als "wird unverändert verwendet" behandelt.
- **Ein Bild, das bereits schmaler als jede konfigurierte Breite ist** -
  nichts zu erzeugen; das schlichte `<img>` wird genau wie zuvor
  gerendert, außer `"webp"` ist aktiviert (siehe oben).

Es gibt außerdem noch keine AVIF-Unterstützung - bx-image schreibt dieses
Format zum jetzigen Zeitpunkt noch nicht. WebP allein bringt bereits den
Großteil des Größengewinns, mit deutlich breiterer Tooling-/
Browser-Unterstützung; das lohnt sich zu überdenken, sobald bx-image
AVIF upstream hinzufügt.

## Ausschalten

=== "YAML"
    ```yaml title="bxsites.yaml"
    assets: { images: { enabled: false } }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "assets": { "images": { "enabled": false } } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [assets]

    [assets.images]
    enabled = false
    ```

Fällt zurück auf das schlichte, unverarbeitete Kopieren von
`docs/assets/**` - genau so, wie jedes Bild behandelt wurde, bevor es
diese Funktion gab.

## Eigene Breakpoints wählen

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      images:
        widths: [ 480, 960, 1440 ]
        formats: [ webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"images": {
    			"widths": [480, 960, 1440],
    			"formats": ["webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [assets]

    [assets.images]
    widths = [ 480, 960, 1440 ]
    formats = [ "webp" ]
    ```

`widths` ist standardmäßig `[400, 800, 1200, 1600]`; `formats` ist
standardmäßig `["original", "webp"]` - lass `"original"` weg, um das
Erzeugen skalierter Kopien im Quellformat komplett zu überspringen (das
schlichte Original in voller Größe bleibt trotzdem als `<img>`-Fallback
erhalten), oder lass `"webp"` weg, um die WebP-`<source>` ganz
auszulassen. Siehe [Konfiguration](../configuration.md#assets) für jeden
`assets.images`-Schlüssel.

## CSS-/JS-Bundling

`extraCss`/`extraJs` werden auf dieselbe Weise gebündelt, standardmäßig
aktiv (`assets.bundle`):

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/a.css, assets/b.css ]
    extraJs: [ assets/app.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/a.css", "assets/b.css"],
    	"extraJs": ["assets/app.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/a.css", "assets/b.css" ]
    extraJs = [ "assets/app.js" ]
    ```

baut ein einzelnes, fingerprint-versehenes `assets/bundle.<hash>.css`
(in der aufgeführten Reihenfolge) und ein `assets/bundle.<hash>.js`,
statt eines `<link>`-/`<script>`-Tags pro Eintrag. CSS werden dabei
Kommentare entfernt und Whitespace zusammengefasst; JS erhält bewusst
nur sicheres, strukturelles Whitespace-Aufräumen - niemals das Entfernen
von Kommentaren, da ein naiver regulärer Ausdruck ein `//` innerhalb
einer Zeichenkette (`"http://example.com"`) nicht von einem echten
Kommentar unterscheiden kann und ein Fehler hier stillschweigend das
eigene Skript eines Projekts beschädigen würde. Das ist Bundling plus
leichtes Aufräumen, kein echter Minifier - eine vendorierte
Java-Minifizierungsbibliothek ist ein sinnvolles späteres Upgrade,
falls das nicht ausreicht.

Bundling wird nur aktiv, wenn *jeder* Eintrag der Liste eine lokale
Projektdatei ist. Eine einzelne externe URL (ein CDN-Link) in der Liste
lässt die gesamte Liste auf das heutige, exakte Pro-URL-Verhalten
zurückfallen, statt zu riskieren, eine CSS-Kaskade stillschweigend
umzusortieren, auf die sich ein Projekt verlassen hat:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/custom.css, "https://cdn.example.com/lib.css" ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/custom.css", "https://cdn.example.com/lib.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/custom.css", "https://cdn.example.com/lib.css" ]
    ```

rendert zwei separate `<link>`-Tags, ungebündelt, genau wie vor
Einführung dieser Funktion.

## Fingerprinting und Caching

Jede erzeugte Bildvariante und jedes CSS-/JS-Bundle wird nach ihrem
Inhalts-Hash benannt (`assets.fingerprint`, standardmäßig aktiv) - ein
Build ändert den Dateinamen einer Variante nur dann, wenn sich ihr
Quellinhalt tatsächlich geändert hat, was es sicher macht, einen
weit in die Zukunft reichenden `Cache-Control`-Header auf einem
statischen Host zu setzen. Die eigenen Originaldateien eines Projekts
unter `docs/assets/` behalten so oder so ihre schlichten Namen
unangetastet - nur pipeline-erzeugte Ausgabe wird mit Fingerprint
versehen, sodass eine `::: file`-Download-Card oder ein roher Link auf
ein Bild über seinen eigenen Dateinamen weiterhin genau wie immer
funktioniert.

Jede erzeugte Variante wird auf der Festplatte unter dem eigenen
`.cache/images/` eines Projekts zwischengespeichert (entfernt von
[`bxSites clean`](../cli-reference.md#clean), zusammen mit `site/`) -
indiziert nach dem Inhalts-Hash des *Quell*-Bilds, sodass ein
erneutes Ausführen von `build` (einmal pro Versions-/Locale-Baum, alle
teilen sich dasselbe `docs/assets/`) oder `bxSites serve` nach einer
unabhängigen Änderung nicht jeden Screenshot des Projekts erneut
dekodiert/skaliert/kodiert, sondern nur die, die sich tatsächlich
geändert haben.
