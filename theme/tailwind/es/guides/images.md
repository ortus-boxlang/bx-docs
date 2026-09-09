---
title: Imágenes Responsivas
order: 5
icon: phosphor-duotone:image
tags: [guías, imágenes, rendimiento]
---

# Imágenes Responsivas

Toda imagen elegible bajo `docs/assets/` obtiene variantes redimensionadas/
WebP generadas automáticamente, y cada `<img>` coincidente en tus páginas
se reescribe como un `<picture>` responsivo - sin sintaxis nueva de
Markdown, sin necesidad de configuración para activarlo. Está construido
sobre [bx-image](https://github.com/ortus-boxlang/bx-image), una
dependencia obligatoria junto a bx-markdown/bx-esapi/bx-yaml (consulta
[Primeros Pasos](../getting-started.md#instalación)).

## Cómo funciona

Escribe una imagen de la forma habitual - sintaxis de Markdown o HTML en
bruto, relativa al archivo respecto a la página igual que ya funciona un
[enlace de página](markdown.md):

```markdown title="Example"
![A freshly built site](../assets/screenshot.png)
```

En el momento de la construcción, `screenshot.png` se redimensiona hacia
abajo a cada ancho configurado más estrecho que el suyo propio (nunca se
amplía), más una recodificación WebP del mismo tamaño, y la página
construida obtiene:

```html title="Rendered output" linenums="1"
<picture>
	<source type="image/webp" srcset="/assets/screenshot-400w.a3f9c2e1.webp 400w, /assets/screenshot-800w.a3f9c2e1.webp 800w, ...">
	<img src="/assets/screenshot.png" srcset="/assets/screenshot-400w.a3f9c2e1.png 400w, /assets/screenshot-800w.a3f9c2e1.png 800w, ..." sizes="(min-width: 800px) 800px, 100vw" alt="A freshly built site">
</picture>
```

Un navegador elige la variante más pequeña que satisfaga `sizes`, en WebP
cuando admite el formato, recurriendo al `src` original sin procesar
(servido exactamente igual que antes) en caso contrario. Cualquier otro
atributo que hayas escrito - `alt`, `class`, cualquier otra cosa - se
traslada sin cambios al `<img>` reescrito.

Una imagen sin ningún ancho configurado más estrecho que el suyo propio
(un icono pequeño, por ejemplo) igualmente obtiene una recodificación
WebP a tamaño completo cuando `"webp"` está en `assets.images.formats` -
una reducción real de tamaño de archivo incluso sin ningún punto de
ruptura responsivo que ofrecer.

## Leyendas, alineación y marcos

Una leyenda, un marco, o una galería de varias imágenes son todos
simplemente HTML a nivel de bloque - que bx-markdown/Flexmark deja pasar
completamente intacto (la propia regla de "bloque HTML" de CommonMark),
así que no se necesita ninguna sintaxis específica de bx-sites en
absoluto:

```markdown title="Example" linenums="1"
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

Lo mismo aplica a `x-data`/`x-show`/`@click` y a cualquier otro atributo
de Alpine.js - consulta
[Interactividad con Alpine.js](interactivity.md).

## Lo que no se redimensiona

- **SVG** - ya independientes de la resolución, se copian sin cambios.
- **GIF animados** - la ruta de redimensionado de bx-image no distingue
  fotogramas; redimensionar uno lo aplanaría a un solo fotograma. Se
  copian sin cambios, exactamente igual que antes de que existiera esta
  función.
- **Cualquier cosa fuera de `docs/assets/`** - una URL de imagen remota
  (`<img src="https://...">`) se deja completamente intacta, de la misma
  forma que [`extraCss`/`extraJs`](../configuration.md#extracss--extrajs)
  ya tratan una URL absoluta como "se usa tal cual".
- **Una imagen ya más estrecha que cualquier ancho configurado** - no hay
  nada que generar; el `<img>` simple se renderiza exactamente igual que
  antes, a menos que `"webp"` esté activado (consulta arriba).

Tampoco hay todavía soporte para AVIF - bx-image no escribe ese formato
a día de hoy. Solo WebP ya consigue la mayor parte de la reducción de
tamaño, con un soporte de herramientas/navegadores mucho más amplio; vale
la pena revisar esto si bx-image añade AVIF en el futuro.

## Desactivarlo

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

Recurre a la copia simple y sin procesar de `docs/assets/**` - exactamente
como se manejaba cada imagen antes de que existiera esta función.

## Elegir tus propios puntos de ruptura

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

`widths` por defecto es `[400, 800, 1200, 1600]`; `formats` por defecto
es `["original", "webp"]` - elimina `"original"` para omitir por
completo la generación de copias redimensionadas en el formato de origen
(conservando aun así el original simple a tamaño completo como
alternativa del `<img>`), o elimina `"webp"` para omitir por completo el
`<source>` de WebP. Consulta [Configuración](../configuration.md#assets)
para cada clave de `assets.images`.

## Empaquetado de CSS/JS

`extraCss`/`extraJs` se empaquetan de la misma forma, activado por
defecto (`assets.bundle`):

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

construye un `assets/bundle.<hash>.css` con huella digital único (en el
orden indicado) y un `assets/bundle.<hash>.js`, en lugar de una etiqueta
`<link>`/`<script>` por entrada. El CSS obtiene sus comentarios
eliminados y los espacios en blanco colapsados; el JS deliberadamente
solo obtiene una limpieza segura y estructural de espacios en blanco -
nunca eliminación de comentarios, ya que una expresión regular ingenua
no tiene forma de distinguir un `//` dentro de una cadena
(`"http://example.com"`) de un comentario real, y equivocarse en eso
corrompería silenciosamente el propio script de un proyecto. Esto es
empaquetado y limpieza ligera, no un verdadero minificador - una
biblioteca de minificación de Java incluida es una mejora razonable a
futuro si esto no resulta suficiente.

El empaquetado solo se activa cuando *cada* entrada de la lista es un
archivo local del proyecto. Una URL externa (un enlace de CDN) mezclada
en la lista hace que toda ella recurra al comportamiento actual exacto
por URL, en lugar de arriesgarse a reordenar silenciosamente una cascada
CSS de la que dependía un proyecto:

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

renderiza dos etiquetas `<link>` separadas, sin empaquetar, exactamente
igual que antes de que existiera esta función.

## Huella digital y caché

Cada variante de imagen generada y cada paquete de CSS/JS tiene un
nombre con huella digital de contenido (`assets.fingerprint`, activado
por defecto) - una construcción solo cambia el propio nombre de archivo
de una variante cuando su contenido de origen realmente cambia, que es
lo que hace seguro establecer una cabecera `Cache-Control` de futuro
lejano en un alojamiento estático. Los archivos originales propios de un
proyecto bajo `docs/assets/` mantienen sus nombres simples sin cambios
en cualquier caso - solo la salida generada por el pipeline obtiene
huella digital, así que una tarjeta de descarga `::: file` o un enlace
en bruto a una imagen por su propio nombre de archivo sigue funcionando
exactamente igual que siempre.

Cada variante generada se almacena en caché en disco bajo el propio
`.cache/images/` de un proyecto (eliminado por
[`bxSites clean`](../cli-reference.md#clean), junto a `site/`) - indexada
por el propio hash de contenido de la imagen *de origen*, así que volver
a ejecutar `build` (una vez por árbol de versión/idioma, todos
compartiendo el mismo `docs/assets/`) o `bxSites serve` después de una
edición sin relación no vuelve a decodificar/redimensionar/recodificar
cada captura de pantalla del proyecto, solo las que realmente cambiaron.
