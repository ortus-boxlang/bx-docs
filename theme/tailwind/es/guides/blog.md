---
title: Blog
order: 10
icon: phosphor-duotone:newspaper
tags: [guías, blog]
---

# Blog

Un blog es otra función por convención, con la misma forma que
[versiones](../configuration.md#versionado)/[i18n](i18n.md) o el
[índice de etiquetas](../getting-started.md#añadir-páginas) - coloca entradas
bajo `docs/blog/posts/`, y BxSites construye `/blog/` (paginado), una
página de categoría por categoría, una página de archivo por año
calendario, una página de autor por autor, un feed RSS por categoría más
uno para todo el blog, y una página `/blog/stats/`, sin necesidad de
configuración. Un proyecto sin carpeta `docs/blog/posts/` simplemente no
tiene blog - nada más cambia.

## Escribir una entrada

Cada archivo `.md` bajo `docs/blog/posts/`, a cualquier profundidad, es
una entrada - las subcarpetas son totalmente opcionales y puramente para
tu propia comodidad al editar. Una carpeta plana funciona bien para un
puñado de entradas; una vez que llegas a los cientos, archivar las
entradas bajo `docs/blog/posts/2026/` (o `docs/blog/posts/2026/03/`, o
cualquier esquema que prefieras) mantiene navegable el árbol de archivos
de tu editor sin renombrar nada ni tocar ninguna convención de prefijo de
fecha en el frontmatter. Nada de esto afecta al sitio construido - el
orden de una entrada, su archivo de año, y su URL (`blog/<slug>/`) se
derivan únicamente del frontmatter, nunca de dónde resulte estar el
archivo, así que la carpeta de una entrada y su `date` real siempre
pueden no coincidir:

```text title="Project structure"
docs/blog/posts/
├── hello-world.md              (flat is fine too)
├── 2026/
│   ├── announcing-2-0.md
│   └── 03/
│       └── a-deep-dive.md
```

Frontmatter, para cualquier entrada sin importar dónde esté archivada:

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

- `date` (obligatorio) - cualquier fecha que BxSites pueda analizar
  (`2026-08-15`, o una fecha-hora completa). Establece el propio orden
  de la entrada (más reciente primero) y su
  `<pubDate>`/`article:published_time`.
- `authors` - una lista de ids que coincidan con entradas de
  [`docs/blog/authors.yml`](#authors), o un nombre simple sin ninguna
  entrada coincidente (renderizado como texto sin enlazar en lugar de
  hacer fallar la construcción - útil para una entrada de invitado
  puntual).
- `categories` - la propia taxonomía de una entrada, cada una con su
  propia página `/blog/category/<slug>/` (y su propio feed RSS
  `/blog/category/<slug>/feed.xml` - consulta [Feed](#feed)). Sin
  relación con `tags`, más abajo.
- `tags` - el mismo frontmatter `tags` de todo el sitio que ya tiene
  cualquier otra página (consulta
  [Primeros Pasos](../getting-started.md#añadir-páginas)) - las etiquetas de
  una entrada se renderizan como insignias y se incorporan al índice
  principal `/tags/` junto a cualquier otra página etiquetada.
- `summary` - un extracto de una línea mostrado en `/blog/`/páginas de
  categoría y en el feed RSS, usado cuando una entrada no tiene marcador
  `<!-- more -->`. Sin ninguno de los dos, BxSites recurre a una
  truncación en texto plano del propio cuerpo de la entrada.
- `image` - una imagen destacada (una ruta relativa a `docs/assets/`, o
  una URL completa) - mostrada en la parte superior de la entrada y como
  miniatura en cada tarjeta de lista/categoría. También se convierte en
  el propio `og:image`/tarjeta de Twitter de la entrada a menos que
  `ogImage` lo sobrescriba por separado. Una imagen relativa a
  `docs/assets/` (y el propio `avatar` de un autor, más abajo) obtiene el
  mismo tratamiento responsivo de `<picture>`/`srcset`/WebP que
  cualquier otra imagen bajo `docs/assets/` - consulta
  [Imágenes](images.md).
- `slug` - sobrescribe el segmento de URL (`/blog/<slug>/`) - derivado
  del nombre de archivo por defecto.
- `draft: true` - excluye la entrada por completo de un `bxSites build`
  real. `bxSites serve` la muestra en vista previa de todos modos (con un
  banner visible "🚧 Draft" en la propia entrada y una tarjeta con borde
  discontinuo en cualquier lugar donde aparezca listada), para que
  puedas corregir una entrada en borrador localmente antes de que esté
  lista - consulta [Vista previa de borradores](#previewing-drafts).

Cualquier otra clave de frontmatter a nivel de página ya documentada en
[Primeros Pasos](../getting-started.md#añadir-páginas) (`icon`, `description`,
`ogImage`, `toc`) funciona también en una entrada.

## Imágenes destacadas y otros recursos del blog

`docs/assets/blog/` no tiene nada de especial más allá de ser una
subcarpeta ordinaria de `docs/assets/` (ya copiada en su totalidad a
`site/assets/`) - es simplemente donde esta guía (y la búsqueda por
convención de avatar de autor de más abajo) espera que vivan las
portadas de entradas/fotos de autor, para que los propios
`docs/assets/` de un proyecto no se saturen mezclando imágenes de blog
con el resto de sus diagramas e iconos. Nada obliga a usar esa
ubicación - cualquier ruta `docs/assets/**` funciona en `image`/`avatar`.

## Autores

`docs/blog/authors.yml` es opcional - una entrada por id de autor,
referenciada por la propia lista `authors` de una entrada:

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

Solo `name` es obligatorio. Cada autor referenciado por al menos una
entrada obtiene su propia página `/blog/authors/<id>/` (biografía,
redes sociales, cada entrada que haya escrito) - un autor a quien
todavía no se le haya acreditado ninguna entrada no obtiene página,
aunque figure en la lista.

**Avatar, por convención** - coloca un archivo en
`docs/assets/blog/authors/<id>.{jpg,jpeg,png,webp,svg}` y se recoge
automáticamente, sin necesidad de una clave `avatar:`. Un `avatar`
explícito en `authors.yml` (una URL o una ruta relativa a
`docs/assets/`) siempre sobrescribe la búsqueda por convención.

## Categorías, archivos, paginación y la entrada de nav "Blog"

Cada valor distinto de `categories` entre todas las entradas obtiene su
propia página `/blog/category/<slug>/`, listando solo las entradas de
esa categoría. Cada año calendario con al menos una entrada también
obtiene su propia página `/blog/archive/<year>/`
(`/blog/archive/2026/`, `/blog/archive/2025/`, ...), derivada
íntegramente del propio frontmatter `date` de cada entrada - sin
necesidad de ninguna estructura de carpetas ni convención de nombre de
archivo, así que dónde vive realmente el archivo `.md` de una entrada
bajo `docs/blog/posts/` (plano, o dividido en tus propias subcarpetas
para facilitar la navegación mientras editas) nunca tiene que coincidir
con su `date`. La lista principal `/blog/` obtiene bloques de enlaces
"Browse by year"/"Browse by category", cada uno con un recuento de
entradas por año/categoría, automáticamente en cuanto las entradas
abarcan más de un año/categoría - un único año o categoría por sí solo
no justifica un bloque de enlaces, así que se omite en cualquier caso.

La lista principal `/blog/`, cada página de categoría, y cada página de
archivo por año paginan de forma idéntica - `blog.postsPerPage` en la
configuración del sitio controla cuántas entradas hay por página (por
defecto `10`); a partir de la página 2 se pasa a `.../page/2/`,
`.../page/3/`, etc.

Se añade automáticamente una única entrada "Blog" a la nav principal, en
cuanto `docs/blog/posts/` tiene al menos una entrada que no sea
borrador - sin necesidad de ningún cambio en `nav`/`docs/nav.json`. Por
defecto se añade al final, después de todo lo demás. Para colocarla en
un lugar específico en su lugar, añade tu propia entrada con una `url`
explícita (evita la regla habitual de que `path` debe coincidir con una
página real, ya que el blog no es una página de `docs/`) a tu array
`nav` o a `docs/nav.json` - hacerlo suprime por completo la entrada
añadida automáticamente, así que nunca hay una duplicada:

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

Las entradas individuales no se añaden a la nav por sí mismas (igual que
el índice de etiquetas) - son accesibles desde `/blog/`, su propia
página de categoría, su propio archivo por año, la página de su autor,
la búsqueda, y los enlaces de anterior/siguiente entre ellas (entradas
adyacentes cronológicamente entre sí, independientes de la propia cadena
de anterior/siguiente de la nav regular).

La propia línea de metadatos de cada entrada (en su tarjeta y en su
página de detalle) también muestra un tiempo de lectura estimado junto
a la fecha - una estimación aproximada de recuento de palabras / 200
ppm, la misma cifra que usan la mayoría de las funciones de tiempo de
lectura, no configurable.

## Feed

`/blog/feed.xml` - un feed RSS 2.0 estándar de las entradas más
recientes, más nuevas primero, escrito siempre que la configuración del
sitio resuelva un `baseURL` absoluto (el mismo requisito que
`sitemap.xml`) y `blog.feed` no esté establecido en `false`. Cada
categoría también obtiene su propio feed filtrado en
`/blog/category/<slug>/feed.xml`. Ambos están limitados a
`blog.feedLimit` entradas (por defecto `25`) - la mayoría de los
lectores de feeds solo se preocupan por lo que es nuevo, así que un feed
sin límite en un blog grande simplemente desperdicia ancho de banda en
cada sondeo; establécelo en `0` para no tener límite:

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

## Vista previa de borradores

`draft: true` mantiene una entrada por completo fuera de un `bxSites
build` real - pero `bxSites serve` la incluye de todos modos, para que
puedas leer un borrador completo (y hacer clic en cada enlace, comprobar
la imagen destacada, ver cómo se lista en `/blog/`) antes de que esté
lista. Un borrador en vista previa siempre lleva un banner visible
"🚧 Draft" - en su propia página de detalle, y como una tarjeta con
borde discontinuo en cualquier lugar donde esté listado (la lista
principal `/blog/`, sus propias páginas de categoría/archivo/autor) -
así que nunca hay ambigüedad sobre qué está realmente publicado. Detén
`bxSites serve` y ejecuta `bxSites build` y el mismo borrador desaparece,
exactamente como si no existiera.

## Estadísticas

`/blog/stats/` - un puñado de tarjetas agregadas sobre el blog en su
conjunto: total de entradas, total de palabras escritas, tiempo de
lectura promedio, recuentos de categorías/colaboradores/años, y tres
tarjetas "destacadas" (entrada más larga, categoría más activa, autor
más activo) cada una enlazada a la página real de la que trata.
Calculado puramente a partir de las entradas ya cargadas para esta
construcción - sin analítica separada, sin seguimiento, nada persistido
entre construcciones - y siempre construido, incluso para un blog
completamente nuevo sin entradas todavía. Enlazado desde la parte
inferior de la lista principal `/blog/`.

## SEO y redes sociales

Cada entrada ya obtiene todo lo que obtiene una página normal
(`<meta name="description">`, `og:description`, `og:image`+
`twitter:card` cuando hay una imagen establecida - consulta
[Configuración: `ogImage`](../configuration.md#ogimage)) más algunas
etiquetas específicas de entradas que cada tema incorporado añade
automáticamente: `og:type` es `"article"` en lugar de `"website"`, y
`article:published_time`/`article:author` (uno por cada autor
acreditado que tenga un `url` establecido en `authors.yml`) se incluyen
en el `<head>` de la página.

## Búsqueda

Las entradas se indexan en el mismo `search-index.json` que cualquier
otra página (sección 7 de la especificación del módulo) - sin ninguna
interfaz de búsqueda de blog separada, el cuadro de búsqueda ya
existente encuentra entradas junto a las páginas de documentación.

## Personalizar la apariencia del blog

No hay ningún "tema de blog" separado que escribir - cada página del
blog (la lista principal `/blog/`, una página de categoría/archivo/
autor, `/blog/stats/`, y la propia página de detalle de cada entrada) se
renderiza a través del mismo `layout.bxm`/`page.bxm` que cualquier otra
página de tu sitio, así que un blog automáticamente se ve como el resto
de tu documentación, y cualquier sobrescritura de tema que ya hayas
hecho (consulta [Temas](themes.md#sobrescribir-un-tema)) se le aplica sin
cambios, sin ningún cableado adicional.

El propio marcado específico del blog (tarjetas de entrada, la línea de
metadatos de fecha/autor/tiempo de lectura, el paginador, el bloque de
perfil de un autor, las listas de enlaces "Browse by year"/"Browse by
category") se construye como HTML simple con un puñado de nombres de
clase fijos, y luego se inserta en `page.contentHtml` igual que una
página de Markdown convertida:

| Clase | Dónde aparece |
|---|---|
| `blog-post-card` / `blog-post-card--draft` | La tarjeta de cada entrada en `/blog/`, una página de categoría, o una página de archivo |
| `blog-post-meta` | La línea de fecha/autor/tiempo de lectura, en una tarjeta y en la propia página de una entrada |
| `blog-post-featured-image` | El `image` del frontmatter de una entrada, en su propia página de detalle |
| `blog-draft-badge` | El banner "🚧 Draft" (solo con `bxSites serve`) |
| `blog-pager` | Enlaces de paginación anterior/siguiente en una lista paginada |
| `blog-author-profile` | El bloque de biografía/redes sociales de un autor en su página `/blog/authors/<id>/` |
| `blog-archive-links` / `blog-category-links` | Los bloques de enlaces "Browse by year"/"Browse by category" en `/blog/` |

Dos formas de darle un nuevo estilo, igual que cualquier otra página:

- **Un ajuste visual rápido** - apunta a estas clases desde tu propio
  [`extraCss`](../configuration.md#extracss--extrajs), de la misma forma
  que
  [personalizarías los colores de un tema](themes.md#personalizar-colores-sin-sobrescribir-un-tema).
  Las propias reglas de un tema incorporado para estas clases viven en
  su `assets/style.css` (por ejemplo
  `resources/themes/bootstrap/assets/style.css`) si quieres un punto de
  partida para sobrescribir.
- **Cambios estructurales** - dado que las páginas de blog comparten
  `layout.bxm`/`page.bxm` con todo lo demás,
  [sobrescribir un tema](themes.md#sobrescribir-un-tema) (o
  [escribir uno desde cero](themes.md#escribir-un-tema-desde-cero))
  cambia el armazón del blog (cabecera, nav, pie de página, envoltorio
  del artículo) junto con cualquier otra página - no hay ninguna
  plantilla de blog separada que copiar.

Lo que no puedes hacer es sustituir tú mismo el propio marcado de
tarjeta de entrada/paginador/perfil de autor por el tuyo - se genera una
sola vez mediante `BlogBuilder.bx`, no se lee de un archivo de plantilla
en `theme/`, así que darle un nuevo estilo con CSS (arriba) es la vía
compatible en lugar de una sobrescritura por componente.
