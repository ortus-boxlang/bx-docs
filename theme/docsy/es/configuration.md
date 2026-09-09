---
title: Configuración
order: 4
icon: phosphor-duotone:gear-six
summary: Cada clave de la configuración del sitio, su valor por defecto y qué hace.
tags: [referencia, configuración]
---

# Configuración

Cada proyecto tiene una configuración de sitio única en su raíz -
`bxsites.yaml` (o `.yml`), el formato por defecto y preferido, o
`bxsites.json` para un proyecto que prefiera quedarse con él. Ambos son
totalmente compatibles y producen exactamente el mismo resultado;
`bxSites new` genera `bxsites.yaml` a menos que se pase `--format=json`
(consulta [Primeros Pasos](getting-started.md#formato-del-archivo-de-configuración)).
Si un proyecto de algún modo tiene más de uno, `bxsites.yaml` prevalece,
luego `bxsites.yml`, luego `bxsites.json`.

=== "YAML"
    ```yaml
    name: "My Docs"
    description: ""
    baseURL: "/"
    theme:
      name: bootstrap
      options: {}
      logo: ""
      favicon: ""
    search: true
    mcp: false
    searchProvider:
      provider: local
      algolia: { appId: "", apiKey: "", indexName: "", insights: false }
    nav: []
    markdown:
      enableAdmonition: true
    repo:
      url: ""
      editUri: ""
    social: []
    footer: false
    lastUpdated: false
    mermaid: false
    math: false
    analytics:
      provider: ""
      id: ""
    ogImage: ""
    generateOgImages: false
    extraCss: []
    extraJs: []
    plugins: []
    i18n:
      defaultLocale: { code: en, label: English }
      locales: []
    blog:
      postsPerPage: 10
      feed: true
    variables: {}
    ```

=== "JSON"
    ```json
    {
    	"name": "My Docs",
    	"description": "",
    	"baseURL": "/",
    	"theme": {
    		"name": "bootstrap",
    		"options": {},
    		"logo": "",
    		"favicon": ""
    	},
    	"search": true,
    	"mcp": false,
    	"searchProvider": {
    		"provider": "local",
    		"algolia": { "appId": "", "apiKey": "", "indexName": "", "insights": false }
    	},
    	"nav": [],
    	"markdown": { "enableAdmonition": true },
    	"repo": {
    		"url": "",
    		"editUri": ""
    	},
    	"social": [],
    	"footer": false,
    	"lastUpdated": false,
    	"mermaid": false,
    	"math": false,
    	"analytics": {
    		"provider": "",
    		"id": ""
    	},
    	"ogImage": "",
    	"generateOgImages": false,
    	"extraCss": [],
    	"extraJs": [],
    	"plugins": [],
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": []
    	},
    	"blog": {
    		"postsPerPage": 10,
    		"feed": true
    	},
    	"variables": {}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    name = "My Docs"
    description = ""
    baseURL = "/"
    search = true
    mcp = false
    nav = []
    social = []
    footer = false
    lastUpdated = false
    extraCss = []
    extraJs = []
    plugins = []
    variables = {}

    [theme]
    name = "bootstrap"
    options = {}
    logo = ""
    favicon = ""

    [markdown]
    enableAdmonition = true

    [repo]
    url = ""
    editUri = ""
    ```

Solo `name` es obligatorio - todo lo demás recurre a los valores por
defecto mostrados arriba. Un objeto `theme` parcial se combina un nivel
de profundidad, así que `{theme: {name: material}}` por sí solo conserva
las `options` por defecto (vacías). Cada clave de abajo se llama y tiene
la misma forma en ambos formatos - cambia de pestaña en cualquier ejemplo
de abajo para verlo en la otra forma.

## `name`

El nombre del sitio, mostrado en la marca de la cabecera y en los títulos
de página. Obligatorio.

## `description`

Una descripción de sitio opcional, usada como `<meta name="description">`
y `og:description` de reserva para cualquier página que no defina su
propio frontmatter `description` (consulta
[Primeros Pasos](getting-started.md#añadir-páginas)).

## `baseURL`

Controla cómo se antepone el prefijo a cada enlace interno, ruta de
recurso y entrada de navegación, y también actúa como la URL canónica
del sitio para `sitemap.xml`, `robots.txt`, `llms.txt`, y la propia
etiqueta `<link rel="canonical">` de cada página.

- Dejado en blanco o `"/"` (el valor por defecto) - los enlaces
  permanecen relativos a la raíz (`/page/`), y no se genera ni
  `sitemap.xml`, ni una línea `Sitemap:` en `robots.txt`, ni un
  `llms.txt` con URL absoluta, ni una etiqueta
  `<link rel="canonical">` (no hay un dominio canónico a partir del
  cual construirlos).
- Una ruta simple, por ejemplo `"my-docs"` o `"/my-docs/"` - se asume que
  el sitio se sirve desde esa subruta, y cada enlace interno, entrada de
  navegación y recurso lleva ese prefijo (`/my-docs/page/`). Siguen sin
  generarse `sitemap.xml`/etiquetas canónicas, ya que todavía no hay un
  dominio absoluto.
- Una URL completa, por ejemplo `"https://docs.example.com/"` - la parte
  de la ruta (`/` aquí) se usa de la misma forma que lo haría una ruta
  simple, **y** `sitemap.xml` se escribe en el momento de la construcción
  con la URL absoluta de cada página no oculta bajo ese dominio,
  `robots.txt` obtiene una línea `Sitemap:` que apunta a él, y cada
  página obtiene su propia `<link rel="canonical">` correcta (la propia
  página de un árbol de versión/idioma sigue apuntando a la URL *de ese
  mismo árbol*, no a la del sitio principal).

`llms.txt` (consulta [más abajo](#llmstxt)) siempre se escribe; simplemente
prefiere una URL absoluta cuando `baseURL` la proporciona.

## `llms.txt`

Cada construcción escribe un `llms.txt` en la raíz del sitio - un índice
en Markdown simple de cada página no oculta, siguiendo la convención
emergente de [llms.txt](https://llmstxt.org) para ayudar a las
herramientas basadas en LLM a navegar un sitio sin rastrear su HTML
renderizado. No hay clave de configuración para esto; se genera
automáticamente, usando una URL absoluta por enlace cuando `baseURL` es
una URL completa, o una relativa a `basePath` en caso contrario.

## `sitemap.xml`

Se escribe en la raíz del sitio, pero solo cuando `baseURL` es una URL
completa (ver arriba) - un sitemap necesita un dominio absoluto para
tener sentido. Enumera cada página no oculta según el protocolo de
[sitemaps.org](https://www.sitemaps.org/).

## `robots.txt`

Cada construcción escribe un `robots.txt` en la raíz del sitio - no hace
falta ninguna clave de configuración a menos que quieras cambiar su
comportamiento por defecto, permisivo:

=== "YAML"
    ```yaml title="bxsites.yaml"
    robots: false
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "robots": false }
    ```

- `true` (el valor por defecto) - `Allow: /` para cada rastreador, además
  de una línea `Sitemap:` que apunta a `sitemap.xml` cuando `baseURL` es
  una URL completa (ver arriba).
- `false` - `Disallow: /` para cada rastreador en su lugar, y sin línea
  `Sitemap:` - la necesidad habitual de "no indexes en absoluto este
  despliegue de staging/interno". Esto es solo una exclusión de
  *rastreadores*, no un control de acceso - el sitio sigue siendo
  totalmente alcanzable por cualquiera que tenga la URL; consulta
  [Despliegue](guides/deployment.md#restringir-quién-puede-acceder-a-tu-sitio)
  si de verdad necesitas restringir quién puede acceder a él.

¿Necesitas más que el simple interruptor de activar/desactivar - rutas
específicas no permitidas, varias líneas `Sitemap:`, un `Crawl-delay`,
reglas por agente de usuario? Coloca tu propio `robots.txt` justo al lado
de `index.md` (`docs/robots.txt`, o `src/robots.txt` para un proyecto
basado en `src/` - consulta
[`docs/` o `src/`](getting-started.md#añadir-páginas)) y se copia tal
cual, byte a byte, en lugar del generado, en cada construcción - la clave
`robots` de arriba se ignora por completo en cuanto existe este archivo.

## `theme`

- `theme.name` - uno de los temas incorporados (`bootstrap`, `material`,
  `tailwind`), o el nombre de un tema personalizado que proporciones
  mediante una carpeta `theme/` en la raíz del proyecto (consulta
  [Temas](guides/themes.md))
- `theme.logo` - ruta/URL a una imagen mostrada junto al nombre del sitio
  en la marca de la cabecera (en lugar del glifo "⚡" por defecto) - una
  ruta relativa (por ejemplo `"assets/logo.svg"`, resuelta contra
  `docs/assets/`) lleva el prefijo `baseURL` como cualquier otro recurso
  interno; una URL absoluta se usa tal cual. Dejado en blanco (el valor
  por defecto), la cabecera muestra "⚡ &lt;nombre del sitio&gt;".
- `theme.favicon` - ruta/URL a un favicon, resuelta de la misma forma que
  `theme.logo`. Dejado en blanco (el valor por defecto), no se renderiza
  ningún `<link rel="icon">` en absoluto (recurriendo al comportamiento
  por defecto propio del navegador).
- `theme.options` - opciones específicas del tema, leídas por todos los
  temas incorporados:
  - `theme.options.colorMode` - `"auto"` (el valor por defecto), `"light"`
    o `"dark"`. Controla qué modo ve un visitante por primera vez antes de
    haber elegido uno propio mediante el interruptor claro/oscuro de la
    cabecera - `"auto"` sigue la preferencia de su sistema operativo,
    `"light"`/`"dark"` fija un valor por defecto. Una vez que un visitante
    activa el interruptor, su propia elección (guardada en `localStorage`)
    siempre prevalece en visitas posteriores, independientemente de este
    valor.

    === "YAML"
        ```yaml
        theme: { options: { colorMode: dark } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "colorMode": "dark" } } }
        ```
  - `theme.options.navCollapsible` - `false` (el valor por defecto)
    renderiza cada sección de navegación siempre expandida, como hoy.
    `true` da a cada sección con hijos un botón de alternancia que el
    visitante puede pulsar para colapsarla/expandirla - ya sea que esa
    sección sea un simple encabezado de grupo (una carpeta sin
    `index.md`) o que enlace a su propia página. La sección que contiene
    la página en la que estás actualmente siempre empieza abierta,
    independientemente de `navExpandAll`, así que navegar hasta ahí nunca
    entierra el propio enlace en el que te encuentras.
  - `theme.options.navExpandAll` - solo relevante cuando `navCollapsible`
    es `true`. `true` (el valor por defecto) inicia cada sección abierta;
    `false` inicia cada sección colapsada, excepto la que contiene la
    página actual.

    === "YAML"
        ```yaml
        theme: { options: { navCollapsible: true, navExpandAll: false } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "navCollapsible": true, "navExpandAll": false } } }
        ```
  - `theme.options.tocPosition` - dónde se renderiza la propia tabla de
    contenido "En esta página" de una página. `"top"` (el valor por
    defecto) la renderiza en línea, en la parte superior del artículo,
    como hoy. `"sticky"` la traslada a su propia columna a la derecha que
    permanece visible mientras el artículo se desplaza por debajo de
    ella - la misma lista "En esta página", solo que fija, lo cual ayuda
    en páginas largas. La columna fija solo cabe en viewports anchos (se
    oculta por debajo del punto en el que un diseño de 3 columnas
    resultaría apretado); por debajo de ese ancho, el modo `sticky`
    renderiza en su lugar una barra colapsable "En esta página" fijada en
    la parte superior del viewport mientras se hace scroll - toca para
    expandir la lista, un tratamiento habitual para viewports estrechos -
    así que la tabla de contenido sigue siendo accesible en
    cualquier ancho de viewport, solo cambia de forma según el espacio
    disponible.

    === "YAML"
        ```yaml
        theme: { options: { tocPosition: sticky } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "tocPosition": "sticky" } } }
        ```
  - `theme.options.pageMetaPosition` - dónde se renderiza la fila de
    editar-esta-página/descargar-markdown/última-actualización en
    relación con el propio contenido de la página. `"bottom"` (el valor
    por defecto) la renderiza como una pequeña nota de pie justo antes de
    que termine el artículo. `"top"` la renderiza en su lugar cerca del
    título, el mismo lugar donde siempre se renderizaba antes de que
    existiera esta opción.

    === "YAML"
        ```yaml
        theme: { options: { pageMetaPosition: top } }
        ```

    === "JSON"
        ```json
        { "theme": { "options": { "pageMetaPosition": "top" } } }
        ```

## `search`

`true` (el valor por defecto) construye un índice de búsqueda estático y
conecta el cuadro de búsqueda; `false` omite ambos por completo - sin
`search-index.json`, sin interfaz de búsqueda, sin JS adicional enviado.
Consulta [Búsqueda](guides/search.md).

## `searchProvider`

Qué interfaz de búsqueda conecta `search: true`:

- `provider` - `"local"` (el valor por defecto) es la propia búsqueda
  estática/del lado del cliente de bx-sites (`search-index.json` +
  lunr.js, consulta [Búsqueda](guides/search.md#local-el-predeterminado)).
  `"algolia"` conecta en su lugar
  [Algolia DocSearch](guides/search.md#algolia), y `"pagefind"` conecta
  [Pagefind](guides/search.md#pagefind). Cualquier otro valor es un
  proveedor personalizado propio de un proyecto, conectado mediante una
  sobrescritura `theme/` - consulta
  [Búsqueda](guides/search.md#otros-proveedores-de-búsqueda).
- `algolia` - obligatorio cuando `provider` es `"algolia"`: `appId`,
  `apiKey` (la clave API pública *solo de búsqueda*, no una clave de
  administrador) e `indexName`, exactamente como los espera el propio
  cliente DocSearch de Algolia. `insights` (`false` por defecto) activa la
  analítica de clics/conversión de DocSearch.

  === "YAML"
      ```yaml title="bxsites.yaml" linenums="1"
      search: true
      searchProvider:
        provider: algolia
        algolia:
          appId: ABC123
          apiKey: a1b2c3d4e5f6...
          indexName: my-docs
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
      			"indexName": "my-docs"
      		}
      	}
      }
      ```

- `pagefind` - ambas claves opcionales cuando `provider` es `"pagefind"`:
  `bin` (por defecto `"pagefind"`) es el nombre/ruta del ejecutable de la
  CLI, resuelto contra `PATH` cuando es un nombre simple; `options` es un
  array de flags de CLI adicionales en bruto que se pasan tal cual. La
  propia CLI de `pagefind` debe estar ya instalada y en el `PATH` - BX
  Sites la invoca externamente (como hace con `git` para
  `lastUpdated`/`gh-deploy`), no la instala por ti.

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

## `mcp`

`false` (predeterminado) lo omite por completo. `true` escribe tres
archivos en cada `build` - un `mcp-index.json` completo y sin truncar,
un `mcp-nav.json` por árbol, y un `mcp-manifest.json` de todo el sitio
que enumera cada árbol - para el servidor MCP público de solo lectura de
[bxSites Cloud](https://bxsites.io/cloud) para tu sitio publicado.
Totalmente independiente de `search`/`searchProvider` anterior - se
generan sin importar qué proveedor de búsqueda (o ninguno) esté
configurado. Generar los archivos en sí es gratis; bxSites Cloud solo
los expone realmente como servidor MCP activo en sus planes de pago, no
en el plan gratuito. Consulta [Servidor MCP](guides/mcp.md).

## `nav`

Por defecto, la navegación se infiere de la propia estructura de
carpetas/archivos de `docs/` (con el frontmatter `order`/`hidden`) - bien
para sitios pequeños, pero uno grande puede superarla: una navegación
explícita te permite titular, agrupar y ordenar las páginas como quieras,
independientemente de dónde vivan realmente sus archivos.

Un array vacío (el valor por defecto) significa "inferir de la estructura
de carpetas". Un array no vacío reemplaza esa inferencia por completo -
el orden del array se convierte en el orden de la navegación, y una
página no referenciada en ningún lugar de él igualmente se construye,
solo que no se enlaza desde la navegación (igual que `hidden: true`).
Cada entrada es o bien:

- una cadena con una ruta simple relativa a docs/, por ejemplo
  `"guides/setup.md"` - el título proviene del propio frontmatter/nombre
  de archivo de esa página, igual que daría la inferencia por carpetas
- un objeto `{ "title", "path", "icon", "children" }` - `path`, `icon` y
  `children` son todos opcionales; una entrada solo con `title` y sin
  `path` es un encabezado de grupo sin enlace (como una carpeta sin
  `index.md` hoy en día), y un `title`/`icon` explícito siempre sobrescribe
  el título/icono propio de la página enlazada en la navegación (el
  `<h1>`/`<title>` real de la página queda intacto - solo cambia la
  etiqueta/icono de navegación) - consulta
  [Iconos](guides/icons.md) para lo que puede ser un valor
  de `icon`

Una entrada solo con `title`, con `children` y sin `path` es exactamente
una etiqueta contenedora/de sección de menú - un encabezado no clicable
que simplemente agrupa a sus hijos:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    nav:
      - index.md
      - title: Main Components
        children:
          - title: Quick Start
            path: guides/setup.md
          - guides/deployment.md
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"nav": [
    		"index.md",
    		{
    			"title": "Main Components",
    			"children": [
    				{ "title": "Quick Start", "path": "guides/setup.md" },
    				"guides/deployment.md"
    			]
    		}
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[nav]]
    path = "index.md"

    [[nav]]
    title = "Main Components"
    children = [
      { title = "Quick Start", path = "guides/setup.md" },
      { path = "guides/deployment.md" }
    ]
    ```

Dale a esa misma entrada de grupo un `path` en su lugar y se convierte en
una sección enlazada normal (con su propia página de aterrizaje, más
hijos) en lugar de una etiqueta simple - ambas formas se anidan bajo
`theme.options.navCollapsible` de la misma manera (consulta arriba).

Para una navegación lo bastante grande como para saturar `bxsites.yaml`,
muévela a su propio archivo `docs/nav.json` en su lugar - la misma forma
de array, simplemente como el contenido de nivel superior de todo el
archivo:

```json
[
	"index.md",
	{ "title": "Guides", "children": [ "guides/setup.md" ] }
]
```

El propio `nav` de `bxsites.yaml`, cuando no está vacío, siempre prevalece
sobre `docs/nav.json`. Solo el árbol principal respeta cualquiera de los
dos - un árbol `docs/versions/<name>/` siempre infiere su navegación de su
propia estructura de carpetas, incluso cuando el árbol principal tiene una
explícita.

## `redirects`

`[]` (el valor por defecto) - redirecciones de URL antiguas `from`/`to`
para todo el sitio, aplicadas solo al árbol principal:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"
    ```

- `redirects[].from` - el segmento de URL antiguo (sin barra inicial/final,
  sin extensión) donde se escribe una redirección estática
- `redirects[].to` - una ruta relativa a la raíz (resuelta contra
  `baseURL`) o una URL `https://` completa

El propio `redirect_from` de una página en su frontmatter es la
alternativa por página y por árbol (funciona también dentro de un árbol
de versión/idioma) - consulta [Redirecciones](guides/redirects.md) para
el panorama completo, incluyendo cómo `page:rename` lo añade
automáticamente.

## `markdown`

Se reenvía tal cual a la propia configuración del módulo de
[bx-markdown](https://github.com/ortus-boxlang/bx-markdown) antes de
renderizar cada página. BxSites no redefine ni valida estas claves; lo que
sea que pongas aquí es el propio conjunto de opciones de bx-markdown,
directamente - así que esta lista puede divergir de la propia de
bx-markdown a medida que evoluciona. Las tablas, `~~tachado~~`, las
casillas de tarea `- [ ]` y la tabla de contenido en la página están
siempre activas, sin interruptor. La única excepción es
`enableAdmonition` - bx-markdown por sí mismo lo establece en `false` por
defecto, pero BxSites lo establece en `true` por defecto (consulta la
[guía de Extensiones de Markdown](guides/markdown.md)).

| Clave | Valor por defecto | Efecto |
|---|---|---|
| `enableAdmonition` | `true` *(valor por defecto de BxSites; el propio valor por defecto de bx-markdown es `false`)* | Bloques de aviso `!!!`/`???`/`???+` - consulta la [guía de Extensiones de Markdown](guides/markdown.md#admoniciones) |
| `enableFootnotes` | `false` | Referencias de nota al pie `[^label]` - consulta la [guía de Extensiones de Markdown](guides/markdown.md#notas-al-pie) |
| `enableDefinitionLists` | `false` | Listas `Term\n:   Definition` - consulta la [guía de Extensiones de Markdown](guides/markdown.md#listas-de-definiciones) |
| `autoLinkUrls` | `true` | Enlaza automáticamente URL y direcciones de correo sin formato |
| `anchorLinks` | `true` | Añade un enlace de ancla clicable a cada encabezado |
| `anchorSetId` | `true` | Estampa un atributo `id` en cada encabezado |
| `achorSetName` *(sic)* | `true` | Estampa un atributo `name` en cada encabezado |
| `anchorWrapText` | `false` | Envuelve todo el texto del encabezado en el enlace de ancla, en lugar de solo un marcador simple |
| `anchorClass` | `"anchor"` | Clase CSS en el `<a>` de ancla |
| `anchorPrefix` / `anchorSuffix` | `""` | HTML sin procesar inyectado inmediatamente antes/después del texto del encabezado |
| `enableYouTubeTransformer` | `false` | Incrusta automáticamente enlaces de YouTube sin formato como un reproductor |
| `codeStyleHTMLOpen` / `codeStyleHTMLClose` | `"<code>"` / `"</code>"` | HTML envolvente alrededor de los fragmentos de código en línea |
| `fencedCodeLanguageClassPrefix` | `"language-"` | Prefijo de clase del que dependen el resaltador de sintaxis del lado del cliente de bx-sites (y Mermaid, ver abajo), por ejemplo ` ```js ` -> `class="language-js"` |
| `tableOptions.columnSpans` | `true` | Respeta las celdas de tabla combinadas al estilo `colspan` |
| `tableOptions.appendMissingColumns` | `true` | Rellena una fila corta hasta el número de columnas del encabezado |
| `tableOptions.discardExtraColumns` | `true` | Descarta celdas adicionales en una fila demasiado larga |
| `tableOptions.className` | `"table"` | Clase CSS en cada `<table>` renderizada |
| `tableOptions.headerSeparationColumnMatch` | `true` | Exige que la fila separadora `---` coincida con el número de columnas del encabezado |

Cada tabla renderizada también recibe automáticamente un wrapper de scroll responsive y encabezado fijo, sin clave de configuración propia - ver [Tablas](guides/tables.md#desplazamiento-responsivo-y-un-encabezado-fijo).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    markdown:
      enableFootnotes: true
      enableDefinitionLists: true
      anchorLinks: false
      enableYouTubeTransformer: true
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"markdown": {
    		"enableFootnotes": true,
    		"enableDefinitionLists": true,
    		"anchorLinks": false,
    		"enableYouTubeTransformer": true
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [markdown]
    enableFootnotes = true
    enableDefinitionLists = true
    anchorLinks = false
    enableYouTubeTransformer = true
    ```

## `repo`

Añade un enlace con icono de repositorio a la cabecera (todos los temas
incorporados) y, cuando ambas claves están definidas, un enlace "Edit this
page" en cada página.

- `repo.url` - la URL de tu repositorio, por ejemplo
  `"https://github.com/acme/docs"`. Renderiza el enlace con icono de la
  cabecera por sí solo; déjalo en blanco para omitirlo por completo.
- `repo.editUri` - el segmento de ruta entre la URL del repositorio y la
  ruta de origen propia de una página, por ejemplo `"edit/main/docs/"`
  (la propia convención de URL de "editar" de GitHub). Combinado con
  `repo.url` y la ruta de origen relativa a `docs/` de una página para
  construir su enlace de edición - por ejemplo, con el ejemplo anterior,
  `docs/guides/setup.md` obtiene
  `https://github.com/acme/docs/edit/main/docs/guides/setup.md`. También
  requiere `repo.url`; déjalo en blanco para omitir los enlaces de edición
  mientras sigues mostrando el icono de la cabecera.

=== "YAML"
    ```yaml title="bxsites.yaml"
    repo: { url: "https://github.com/acme/docs", editUri: "edit/main/docs/" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "repo": { "url": "https://github.com/acme/docs", "editUri": "edit/main/docs/" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [repo]
    url = "https://github.com/acme/docs"
    editUri = "edit/main/docs/"
    ```

## `social`

Un array de enlaces sociales/externos renderizados en el pie de página
(consulta [`footer`](#footer) - no tiene efecto a menos que también esté
activado). Cada entrada necesita una `url`; `icon` selecciona de un
pequeño conjunto de iconos incorporado (`github`, `twitter`/`x`,
`youtube`, `linkedin`, `facebook`, `bluesky`, `threads`, `slack`,
`patreon`, `rss`, `email`, recurriendo a un glifo de enlace genérico para
cualquier otra cosa), y `label` establece el nombre accesible/tooltip del
enlace (por defecto `icon`, y luego `"Link"`).

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    social:
      - { url: "https://twitter.com/acme", icon: twitter, label: Twitter }
      - { url: "https://acme.com/rss.xml", icon: rss, label: RSS }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"social": [
    		{ "url": "https://twitter.com/acme", "icon": "twitter", "label": "Twitter" },
    		{ "url": "https://acme.com/rss.xml", "icon": "rss", "label": "RSS" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [[social]]
    url = "https://twitter.com/acme"
    icon = "twitter"
    label = "Twitter"

    [[social]]
    url = "https://acme.com/rss.xml"
    icon = "rss"
    label = "RSS"
    ```

## `footer`

`false` (el valor por defecto) - sin pie de página en absoluto. `true`
añade uno a cada página: una línea de copyright (`© <year> <site name>`),
los enlaces `social` (si los hay), y un crédito "Built with BxSites".

=== "YAML"
    ```yaml title="bxsites.yaml"
    footer: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "footer": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    footer = true
    ```

## `lastUpdated`

`false` (el valor por defecto) - sin fecha de última actualización. `true`
añade una línea "Last updated" junto al enlace de edición (o por sí sola,
si `repo.editUri` no está definido), obtenida de `git log` sobre el propio
archivo Markdown de cada página en el momento de la construcción. Se omite
silenciosamente para una página de la que git no tiene historial - un
`git init` reciente sin commits todavía, una construcción ejecutándose
desde un zip descargado sin `.git` en absoluto, o git no estando instalado
en la máquina de construcción - en lugar de romper la construcción.

=== "YAML"
    ```yaml title="bxsites.yaml"
    lastUpdated: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "lastUpdated": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    lastUpdated = true
    ```

## `analytics`

Conecta el análisis de vistas de página. Actualmente solo admite Google
Analytics (`gtag.js`):

- `analytics.provider` - `"google"` para activarlo; dejado en blanco (el
  valor por defecto), no se envía ningún script de análisis en absoluto.
- `analytics.id` - el ID de medición de Google Analytics (por ejemplo,
  `"G-ABC123"`). Obligatorio cuando `provider` es `"google"`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    analytics: { provider: google, id: "G-ABC123" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "analytics": { "provider": "google", "id": "G-ABC123" } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [analytics]
    provider = "google"
    id = "G-ABC123"
    ```

## `ogImage`

Ruta/URL a una imagen de tarjeta social por defecto, renderizada como
`og:image` (emparejada con un `twitter:card` de `summary_large_image`) en
cada página que no la sobrescriba - resuelta de la misma forma que
`theme.logo` (las rutas relativas llevan el prefijo `baseURL`, las URL
absolutas se usan tal cual). Dejado en blanco (el valor por defecto) y
con `generateOgImages` desactivado, no se renderiza ninguna etiqueta
`og:image`/`twitter:card`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    ogImage: assets/social-card.png
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "ogImage": "assets/social-card.png" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    ogImage = "assets/social-card.png"
    ```

El propio `ogImage` del frontmatter de una página (consulta
[Primeros Pasos](getting-started.md#añadir-páginas)) siempre prevalece sobre
este valor por defecto de todo el sitio para esa página en particular.

### `generateOgImages`

`false` (el valor por defecto) - sin tarjetas por página. `true` renderiza
una tarjeta social PNG real de 1200x630 para cada página que aún no tenga
su propio `ogImage` en el frontmatter - el título de la página sobre el
degradado de marca, escrito en `site/assets/og/<page>.png` - en lugar de
que cada página comparta una imagen genérica de todo el sitio. Puro
`java.awt`/`javax.imageio` por debajo (parte de cualquier JVM en la que
se ejecute BoxLang), así que esto no necesita navegador headless, servicio
externo, ni acceso a red en el momento de la construcción.

=== "YAML"
    ```yaml title="bxsites.yaml"
    generateOgImages: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "generateOgImages": true }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    generateOgImages = true
    ```

## `extraCss` / `extraJs`

Arrays de URL de hojas de estilo/scripts adicionales para incluir en cada
página, añadidos después de los propios recursos del tema - cada entrada
se resuelve de la misma forma que `theme.logo` (una ruta relativa lleva el
prefijo `baseURL`; una URL absoluta se usa tal cual). Las entradas de
`extraJs` se cargan con `defer`.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/custom.css ]
    extraJs: [ assets/custom.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/custom.css"],
    	"extraJs": ["assets/custom.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/custom.css" ]
    extraJs = [ "assets/custom.js" ]
    ```

## `assets`

=== "YAML"

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    [assets]
    fingerprint = true
    bundle = true

    [assets.images]
    enabled = true
    widths = [ 400, 800, 1200, 1600 ]
    formats = [ "original", "webp" ]
    ```
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      fingerprint: true
      bundle: true
      images:
        enabled: true
        widths: [ 400, 800, 1200, 1600 ]
        formats: [ original, webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"fingerprint": true,
    		"bundle": true,
    		"images": {
    			"enabled": true,
    			"widths": [400, 800, 1200, 1600],
    			"formats": ["original", "webp"]
    		}
    	}
    }
    ```

El pipeline de recursos - redimensionado de imágenes/WebP mediante
[bx-image](https://github.com/ortus-boxlang/bx-image) (una dependencia
obligatoria, instalada junto a bx-markdown/bx-esapi) y empaquetado de
CSS/JS. Todo aquí está activo por defecto con valores razonables - un
proyecto recién creado con `bxSites new` no necesita tocar nada de esto.
Consulta [Imágenes Responsivas](guides/images.md) para el panorama
completo, incluido lo que deliberadamente no está cubierto (AVIF, GIFs
animados, SVGs).

- `assets.fingerprint` - `true` (el valor por defecto). Asigna un nombre
  con hash de contenido a cada variante de imagen generada y cada paquete
  CSS/JS (por ejemplo, `screenshot-800w.a3f9c2e1.webp`,
  `bundle.a3f9c2e1.css`), de modo que puedan servirse con cabeceras de
  caché seguras y de larga duración - una construcción del proyecto solo
  cambia el propio nombre del archivo cuando su contenido realmente
  cambia. No renombra los archivos originales propios de un proyecto bajo
  `docs/assets/` - solo se le asigna huella digital a la salida generada
  por el pipeline, así que cualquier otra cosa que referencie un recurso
  por su nombre de archivo simple (una tarjeta de descarga `::: file`, un
  enlace de markdown en bruto) sigue funcionando sin cambios.
- `assets.bundle` - `true` (el valor por defecto). Concatena
  `extraCss`/`extraJs` en un único archivo con huella digital cada uno -
  BoxLang/JVM puro, sin cadena de herramientas de Node/esbuild. Recurre
  exactamente al comportamiento actual `<link>`/`<script>` por URL, sin
  cambios, en cuanto cualquier entrada de la lista es una URL externa (un
  enlace de CDN) o nombra un archivo que no existe - consulta
  [Imágenes Responsivas](guides/images.md#empaquetado-de-cssjs).
- `assets.images.enabled` - `true` (el valor por defecto). Toda imagen
  elegible bajo `docs/assets/**` (`.png`/`.jpg`/`.jpeg`) obtiene variantes
  redimensionadas/WebP generadas mediante bx-image, y cada `<img>`
  coincidente se reescribe en un `<picture>` con `srcset`. Establece
  `false` para recurrir a la copia de imágenes simple y sin procesar,
  exactamente como antes de que existiera esta función.
- `assets.images.widths` - puntos de ruptura a generar, en píxeles. Un
  ancho igual o mayor que el propio ancho de una imagen dada se omite
  automáticamente para esa imagen - nunca se hace upscale.
- `assets.images.formats` - `"original"` mantiene el formato de origen
  como el fallback de `<img>`; `"webp"` añade una variante
  `<source type="image/webp">` del mismo tamaño. Ambos activos por
  defecto.

## `mermaid`

`false` (el valor por defecto) - sin soporte de diagramas
[Mermaid](https://mermaid.js.org/) en absoluto. `true` carga `mermaid.js`
del lado del cliente y renderiza cada bloque de código con fence
` ```mermaid ` como un diagrama. Consulta
[Extensiones de Markdown](guides/markdown.md#diagramas) para la sintaxis.

=== "YAML"
    ```yaml title="bxsites.yaml"
    mermaid: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mermaid": true }
    ```

## `math`

`false` (el valor por defecto) - sin [KaTeX](https://katex.org/) en
absoluto. `true` lo carga del lado del cliente y compone `$...$`/`$$...$$`
escrito directamente en el markdown de una página. Consulta
[Extensiones de Markdown](guides/markdown.md#matemáticas) para la sintaxis.

=== "YAML"
    ```yaml title="bxsites.yaml"
    math: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "math": true }
    ```

Las admoniciones (cuadros de aviso al estilo nota/advertencia/consejo),
las pestañas de contenido y las anotaciones de código con fence
`hl_lines`/`linenums`/`title` están siempre disponibles en el markdown de
cualquier página, sin necesidad de configuración - consulta
[Extensiones de Markdown](guides/markdown.md#admoniciones).

## `openapi`

`false` (el valor por defecto) - sin
[Swagger UI](https://swagger.io/tools/swagger-ui/) en absoluto. `true` lo
carga del lado del cliente y renderiza cada bloque de contenido
`::: openapi src="..."` como un widget interactivo para la especificación
OpenAPI/Swagger referenciada (JSON o YAML). Consulta
[OpenAPI / Swagger](guides/openapi.md) para la
sintaxis.

=== "YAML"
    ```yaml title="bxsites.yaml"
    openapi: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "openapi": true }
    ```

## `plugins`

`[]` (el valor por defecto) - un array de nombres de módulos de BoxLang
para activar como plugins. Instalar un módulo de plugin (`box install`)
nunca lo activa por sí solo; también tiene que nombrarse aquí. Consulta
[Plugins](guides/plugins.md) para saber cómo escribir uno.

=== "YAML"
    ```yaml title="bxsites.yaml"
    plugins: [ myBxSitesPlugin ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "plugins": ["myBxSitesPlugin"] }
    ```

## `i18n`

Metadatos para la convención de carpetas de idioma
[`docs/i18n/<code>/`](guides/i18n.md) - un idioma se construye
automáticamente en cuanto su carpeta existe; `i18n` simplemente
proporciona su etiqueta de visualización/dirección para el selector de
idioma.

- `i18n.defaultLocale` - `{ "code", "label", "flag", "strings" }` para el
  propio árbol `docs/` regular del proyecto, con el valor por defecto
  `{ "code": "en", "label": "English" }`. Solo hace falta definirlo cuando
  tu idioma predeterminado no es el inglés.
- `i18n.locales` - `[]` (el valor por defecto) - un array de
  `{ "code", "label", "dir", "flag", "strings" }` para cada otro idioma.
  `code` cumple una doble función como nombre de la carpeta
  `docs/i18n/<code>/` y como prefijo de URL generado - solo
  letras/dígitos/guiones (`es`, `pt-BR`, `zh-Hans`). `dir` es `"ltr"` (el
  valor por defecto) o `"rtl"`. `flag` es una sobrescritura opcional con
  emoji para el icono de bandera del selector de idioma - la mayoría de
  los códigos comunes ya se resuelven por sí solos a una bandera
  razonable. `strings` sobrescribe las cadenas de UI de la interfaz del
  tema propias de ese idioma (marcador de posición de búsqueda, "En esta
  página," la página 404, ...) - consulta
  [Internacionalización](guides/i18n.md#interfaz-del-tema-cadenas-de-ui)
  para la lista completa de claves; `de`/`es`/`it`/`ja` ya incluyen una
  traducción integrada, así que `strings` solo hace falta para
  sobrescribir una clave o añadir otro idioma.

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    i18n:
      defaultLocale: { code: en, label: English }
      locales:
        - { code: es, label: Español }
        - { code: ar, label: العربية, dir: rtl }
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"i18n": {
    		"defaultLocale": { "code": "en", "label": "English" },
    		"locales": [
    			{ "code": "es", "label": "Español" },
    			{ "code": "ar", "label": "العربية", "dir": "rtl" }
    		]
    	}
    }
    ```

Consulta [Internacionalización](guides/i18n.md) para el panorama completo
- la reserva de páginas sin traducir, el selector de idioma y lo que
todavía no está traducido.

## `blog`

Opciones para el [blog](guides/blog.md) - en sí misma una función por
convención (`docs/blog/posts/`), sin ninguna clave aquí obligatoria para
activarla.

- `blog.postsPerPage` - `10` (el valor por defecto) - cuántas entradas por
  página en `/blog/`, en cada página de categoría y en cada página
  `/blog/archive/<year>/` antes de pasar a `.../page/2/`.
- `blog.feed` - `true` (el valor por defecto) - si se escribe
  `/blog/feed.xml` (RSS 2.0). Solo tiene sentido con un `baseURL`
  absoluto, el mismo requisito que `sitemap.xml`.
- `blog.feedLimit` - `25` (el valor por defecto) - limita
  `/blog/feed.xml` a esta cantidad de entradas más recientes. `0`
  significa sin límite (cada entrada, completa). La mayoría de los
  lectores de feeds solo se preocupan por lo nuevo, así que un feed sin
  límite en un blog con cientos de entradas simplemente desperdicia ancho
  de banda en cada sondeo - consulta [Blog: Feed](guides/blog.md#feed).

=== "YAML"
    ```yaml title="bxsites.yaml"
    blog: { postsPerPage: 10, feed: true, feedLimit: 25 }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "blog": { "postsPerPage": 10, "feed": true, "feedLimit": 25 } }
    ```

Consulta [Blog](guides/blog.md) para el frontmatter de entradas/autores,
categorías, imágenes destacadas y metadatos de SEO/redes sociales.

## `variables`

`{}` (el valor por defecto) - un objeto de valores reutilizables, con la
forma que prefieras, referenciado desde cualquier página de Markdown como
`{{ dotted.path }}`. Consulta
[Variables y Funciones Mágicas](guides/variables-and-functions.md).

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product: { name: "BoxLang", supportEmail: "support@example.com" }
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": { "name": "BoxLang", "supportEmail": "support@example.com" }
    	}
    }
    ```

```markdown title="docs/index.md"
Welcome to {{ company }}! We build {{ product.name }}.
```

Un archivo `docs/functions.bxs` (sin clave de configuración propia - por
convención, igual que `docs/nav.json`/`docs/blog/authors.yml`) añade
"funciones mágicas" de BoxLang junto a `variables` - invocables de la
misma forma, como `{{ $name(...) }}`. Consulta
[Variables y Funciones Mágicas](guides/variables-and-functions.md#funciones-mágicas).

Una carpeta `docs/data/*.yaml`/`.yml`/`.json` (tampoco con clave de
configuración propia) añade datos estructurados, con forma anidada/de
array - accesibles como `{{ data.<archivo>.<clave> }}` - para cualquier
cosa que no encaje bien en la propia forma plana de `variables` (una
lista de equipo, una tabla de precios). Consulta
[Archivos de Datos](guides/data-files.md).

## Versionado

Los documentos versionados son cuestión de convención, no de
configuración - no hay ninguna clave de `bxsites.yaml` para ello. Añade
una carpeta `docs/versions/`, y cada subcarpeta directa dentro de ella se
construye como su propio árbol de documentos totalmente autocontenido,
junto a tu `docs/` regular (que siempre se construye como "Latest"):

```
docs/
├── index.md
├── guides/
└── versions/
    ├── 1.0/
    │   ├── index.md
    │   └── guides/
    └── 2.0/
        ├── index.md
        └── guides/
```

Cada carpeta de versión es un árbol normal con forma de `docs/` - su
propio `index.md`, su propia navegación, sus propias páginas - construido
en `site/versions/<name>/` con cada enlace interno prefijado en
consecuencia, y compartiendo el único `bxsites.yaml` de configuración/tema
del proyecto. Los nombres de versión se ordenan de más reciente a más
antiguo, numéricamente en lugar de alfabéticamente (de modo que `2.0` se
ordena antes que `10.0`), y cada tema renderiza automáticamente un
desplegable selector de versión en la cabecera en cuanto existe más de
una versión - no hay nada que activar. Un archivo suelto colocado
directamente bajo `docs/versions/` (no dentro de una subcarpeta) se
ignora.

`sitemap.xml` y `llms.txt` incluyen las páginas de todas las versiones
junto a las del sitio principal.


## `docbox`

Ajustes de [`bxSites docbox`](cli-reference.md#docbox), que genera una
referencia de API BoxLang/CFML desde DocBox - consulta
[Referencia de API con DocBox](guides/docbox.md). Todas las claves son
opcionales; sin bloque `docbox` se documentan las carpetas convencionales
(`models`, `handlers`, `bifs`, `components`, `interceptors`).

- `projectTitle` - título de la página de resumen. Por defecto, el `name`
  del sitio más `" API"`.
- `mappings` - nombre de mapping a directorio de fuentes. El nombre es lo
  que aparece como paquete en las páginas generadas.
- `excludes` - regex de rutas que DocBox omite.
- `pagePathPrefix` - dónde se escriben las páginas. Por defecto
  `api/docbox`.
- `tags` - tags de frontmatter en cada página generada. Por defecto
  `[ "api", "docbox" ]`.

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "Mi API"
      mappings:
        models: models
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "Mi API",
    		"mappings": { "models": "models" },
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [docbox]
    projectTitle = "Mi API"
    pagePathPrefix = "api/docbox"
    tags = [ "api", "docbox" ]

    [docbox.mappings]
    models = "models"
    ```

## `coldbox`

Ajustes de [`bxSites coldbox`](cli-reference.md#coldbox), que documenta una
aplicación ColdBox a partir de sus convenciones - consulta
[Aplicaciones ColdBox](guides/coldbox.md). Todas las claves son opcionales.

- `appRoot` - dónde vive la aplicación ColdBox, relativo a la raíz del
  proyecto. Por defecto, la propia raíz.
- `pagePathPrefix` - dónde se escriben las páginas. Por defecto
  `api/coldbox`.
- `tags` - tags de frontmatter en cada página generada. Por defecto
  `[ "api", "coldbox" ]`.
- `include` - qué conjuntos de páginas generar: `routes`, `handlers`,
  `models`, `modules`, `interceptors`, `scheduler`. Todos por defecto.

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    [coldbox]
    appRoot = "."
    pagePathPrefix = "api/coldbox"
    tags = [ "api", "coldbox" ]
    include = [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    ```
