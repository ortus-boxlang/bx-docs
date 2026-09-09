---
title: Búsqueda
order: 2
icon: phosphor-duotone:magnifying-glass
tags: [guías, búsqueda]
---

# Búsqueda

BxSites incluye un proveedor de búsqueda por defecto y se puede apuntar
a otros mediante [`searchProvider`](../configuration.md#searchprovider)
de `bxsites.yaml` - `search: true`/`false` sigue siendo el interruptor
maestro de encendido/apagado, sin importar qué proveedor esté activo.

## Local (el predeterminado)

La búsqueda de BxSites es completamente estática y del lado del
cliente - el mismo enfoque que usa [mkdocs](https://www.mkdocs.org/) por
defecto: un índice construido una vez en el momento de `build`, y
[lunr.js](https://lunrjs.com/) haciendo la búsqueda real en el navegador
del visitante. No hay servidor, base de datos ni servicio de búsqueda
externo involucrado.

## Cómo funciona

1. En el momento de `build`, `SearchIndexer` recorre cada página no
   oculta y escribe `site/search-index.json`: una entrada por página con
   su `title`, `url`, las `tags` del frontmatter, el texto de cada
   encabezado de la página, y una copia de texto plano truncada de su
   cuerpo (con las etiquetas HTML eliminadas).
2. El parcial `search.bxm` de cada tema renderiza un cuadro de búsqueda;
   `layout.bxm` solo lo incluye (junto con `lunr.js` y los scripts
   compartidos de `search.js`) cuando `search` de `bxsites.yaml` es
   `true` y `searchProvider.provider` es `"local"` (el predeterminado -
   consulta [Otros proveedores](#otros-proveedores-de-búsqueda) más
   abajo para lo que cambia con uno distinto).
3. En el navegador, el widget compartido `assets/search.js` obtiene
   `search-index.json` una vez, construye un índice `lunr` a partir de él
   (`title` con la mayor ponderación, luego las `tags` del frontmatter,
   luego `headings`, luego el texto plano del cuerpo), y vuelve a
   buscar en él en cada pulsación de tecla - sin ida y vuelta de red por
   consulta.

## Atajos de teclado

- **`/`** enfoca el cuadro de búsqueda de la barra lateral desde
  cualquier lugar de la página (a menos que ya estés escribiendo en otro
  campo) - la misma convención que usa
  [mkdocs-material](https://squidfunk.github.io/mkdocs-material/). El
  cuadro de búsqueda muestra una pequeña pista `Ctrl K`/`⌘K` (detectada
  según la plataforma) para que el atajo de abajo sea descubrible.
- **Cmd/Ctrl+K** en su lugar abre una superposición aparte, al estilo
  paleta de comandos - una ventana modal centrada sobre un fondo
  oscurecido, construida enteramente en JS (sin necesidad de cambios en
  las plantillas de tema) y compartida por todos los temas incorporados.
  Las flechas arriba/abajo mueven un resaltado entre los resultados,
  **Enter** navega hasta el resultado resaltado, y **Escape** (o hacer
  clic en el fondo oscurecido) la cierra - la misma convención de
  "Búsqueda rápida"/⌘K que comparten Algolia DocSearch, Pagefind,
  VitePress y Docusaurus.
- **`Escape`** también cierra el propio desplegable de resultados del
  cuadro de la barra lateral y le quita el foco, de forma independiente
  a la paleta de arriba.

La paleta reutiliza exactamente el mismo índice `lunr` ya construido que
construye el propio widget de la barra lateral, en lugar de obtener
`search-index.json` una segunda vez - solo está disponible para `local`
(el proveedor por defecto); `algolia` obtiene su propio Cmd+K gratis del
propio DocSearch (`keyboardShortcuts` es `true` por defecto), y
`pagefind` obtiene Cmd+K conectado por `layout.bxm` para enfocar su
propio `PagefindUI`, ya que esa biblioteca no lo vincula por sí misma -
ninguno de los dos abre la propia paleta de este módulo.

## Desactivarla

=== "YAML"
    ```yaml title="bxsites.yaml"
    search: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "search": false }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    search = false
    ```

Omite la construcción de `search-index.json` por completo, y omite el
cuadro de búsqueda, el script incluido/autoalojado de `lunr.js`, y el
widget compartido `search.js` en cada página renderizada - un proyecto
con la búsqueda desactivada no envía nada relacionado con búsqueda en
absoluto. Este es el interruptor maestro - se aplica sin importar qué
`searchProvider` esté configurado.

## Reconstruir solo el índice

```bash frame="terminal" title="Terminal"
bxSites search-index
```

Útil si solo necesitas actualizar `search-index.json` - `build` ya hace
esto como uno de sus propios pasos, así que no necesitas ejecutar esto
por separado después de una construcción normal. Solo se ejecuta para
proveedores que usan el índice local (`"local"`, y cualquier proveedor
que bx-sites no conozca de otro modo) - es un no-op (`skipped: true`)
cuando `searchProvider.provider` es `"algolia"` o `"pagefind"`, ya que
ninguno de los dos lo usa nunca.

## Algolia

Establece `searchProvider.provider` en `"algolia"` para sustituir el
cuadro de búsqueda por [Algolia DocSearch](https://docsearch.algolia.com/)
- la misma búsqueda alojada por un rastreador que admiten
mkdocs-material, VitePress, Starlight y Docusaurus:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: algolia
      algolia:
        appId: ABC123
        apiKey: a1b2c3d4e5f6...
        indexName: my-docs
        insights: false
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "algolia",
    		"algolia": {
    			"appId": "ABC123",
    			"apiKey": "a1b2c3d4e5f6...",
    			"indexName": "my-docs",
    			"insights": false
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "algolia"

    [searchProvider.algolia]
    appId = "ABC123"
    apiKey = "a1b2c3d4e5f6..."
    indexName = "my-docs"
    insights = false
    ```

`appId`, `apiKey` e `indexName` son obligatorios - `apiKey` es la clave
API pública **solo de búsqueda** que te da DocSearch (nunca una clave de
administrador; se envía directamente en cada página renderizada).
`insights` (`false` por defecto) activa la propia analítica de
clics/conversión de DocSearch.

Con `algolia` activo:

- No se construye ningún `search-index.json`, y el widget compartido
  `lunr.js`/`search.js` no se incluye - Algolia sirve los resultados
  desde su propio índice alojado, poblado por el
  [rastreador de DocSearch](https://docsearch.algolia.com/docs/what-is-docsearch/)
  o por tu propia configuración de
  [Algolia Crawler](https://www.algolia.com/products/search-and-discovery/crawler/),
  no por nada que BxSites escriba en el momento de la construcción.
  Igualmente necesitas registrar el sitio con DocSearch (o ejecutar tu
  propio rastreador) por separado - BxSites solo conecta el widget del
  cliente.
- Cada tema incorporado renderiza en su lugar un contenedor vacío
  `#bxsites-search-algolia`, y `layout.bxm` carga `@docsearch/css`/
  `@docsearch/js` desde jsDelivr y llama a `docsearch({...})` contra él -
  DocSearch renderiza su propio botón de búsqueda y modal dentro de ese
  contenedor.

## Pagefind

Establece `searchProvider.provider` en `"pagefind"` para sustituir el
cuadro de búsqueda por [Pagefind](https://pagefind.app/) - otro motor de
búsqueda totalmente estático/sin servidor, pero indexado a partir del
propio HTML *construido* de `site/` en lugar de rastreado como Algolia:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    search: true
    searchProvider:
      provider: pagefind
      pagefind: { bin: pagefind, options: [] }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"search": true,
    	"searchProvider": {
    		"provider": "pagefind",
    		"pagefind": { "bin": "pagefind", "options": [] }
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    search = true

    [searchProvider]
    provider = "pagefind"

    [searchProvider.pagefind]
    bin = "pagefind"
    options = []
    ```

Ambas claves de `pagefind` son opcionales - `bin` (por defecto
`"pagefind"`) es el nombre/ruta del ejecutable, resuelto contra `PATH`
cuando es un nombre simple; `options` es un array de flags de CLI en
bruto adicionales pasados directamente (por ejemplo
`["--exclude-selectors", ".no-index"]`).

Con `pagefind` activo:

- **El propio CLI de `pagefind` ya debe estar instalado y en `PATH`** -
  BxSites lo invoca mediante shell (no hay un binding nativo de
  BoxLang, la misma razón por la que `lastUpdated`/`gh-deploy` invocan
  `git` mediante shell), no lo instala por ti. Consulta la
  [propia documentación de instalación de Pagefind](https://pagefind.app/docs/installation/).
  A diferencia de `lastUpdated`, un binario ausente/fallido hace fallar
  `build` de forma explícita (`BxSites.PagefindFailed`) en lugar de
  degradarse silenciosamente - publicar un sitio cuyo proveedor de
  búsqueda configurado no funciona es peor que una construcción fallida.
- Justo después de que cada árbol de documentación (principal + versiones
  + idiomas) se escribe y se generan `sitemap.xml`/`llms.txt`, BxSites
  ejecuta `pagefind --site <siteDir> [...options]` contra *todo* el
  `site/` construido - así que un sitio multiversión/multiidioma obtiene
  todo indexado en un solo paso, a diferencia del propio
  `search-index.json` por árbol de bx-sites. Pagefind escribe su propio
  paquete directamente en `site/pagefind/` - autoalojado, sin CDN
  involucrado.
- No se construye ningún `search-index.json`, y el widget compartido
  `lunr.js`/`search.js` no se incluye (igual que con `algolia`) - y
  `bxSites search-index` es un no-op por la misma razón (consulta
  arriba).
- Cada tema incorporado renderiza un contenedor vacío
  `#bxsites-search-pagefind`, y `layout.bxm` carga
  `site/pagefind/pagefind-ui.{css,js}` y llama a
  `new PagefindUI({...})` contra él - Pagefind renderiza su propio
  cuadro de búsqueda y resultados en línea dentro de ese contenedor.

## Otros proveedores de búsqueda

`searchProvider.provider` no está limitado a
`"local"`/`"algolia"`/`"pagefind"` - cualquier otro valor es aceptado
por `bxsites.yaml` tal cual (la propia validación de configuración de BX
Sites solo comprueba los tres proveedores de arriba). No hay ningún hook
de plugin para esto - los temas incorporados simplemente no renderizan
nada para un nombre de proveedor no reconocido, y conectar un cuarto
servicio de búsqueda (Meilisearch, Typesense, etc.) es una
[sobrescritura de tema](themes.md#sobrescribir-un-tema) a nivel de
proyecto: copia un tema incorporado en el propio `theme/` de tu proyecto
y añade el marcado/scripts de tu proveedor a su `layout.bxm`/
`search.bxm`, leyendo `siteConfig.searchProvider` para decidir cuándo
renderizarlos - ramas `searchProviderName eq "..."` para el punto de
montaje en `search.bxm`, ramas equivalentes en `layout.bxm` para su
CSS/JS, y (si no está alojado por un rastreador como Algolia) cualquier
paso de indexación que ese producto necesite contra `site/` después de
`build` - la misma forma que ya usan el propio `layout.bxm`/
`BuildPipeline.bx` de este módulo para `algolia`/`pagefind`.
