---
title: Variables y Funciones Mágicas
order: 12
icon: phosphor-duotone:magic-wand
tags: [guías, variables, funciones]
---

# Variables y Funciones Mágicas

Dos funciones pequeñas y relacionadas para mantener fuera de tu Markdown
tanto los datos repetidos como la lógica repetida: **variables
reutilizables**, definidas una sola vez en `bxsites.yaml` e insertadas en
cualquier página con `{{ }}`, y **funciones mágicas**, pequeños ayudantes
de BoxLang que escribes una sola vez en `docs/functions.bxs` y llamas de
la misma forma - en todas partes, sin import, sin plugin, sin cableado
alguno.

Ambas comparten una única sintaxis:

```text
{{ dotted.path }}          # a reusable variable
{{ $name(arg1, arg2) }}    # a magic function call
```

## Variables reutilizables

Añade un bloque `variables` a `bxsites.yaml` - con la forma que prefieras,
plana o anidada:

=== "YAML"
    ```yaml title="bxsites.yaml"
    variables:
      company: "Ortus Solutions"
      product:
        name: "BoxLang"
        supportEmail: "support@example.com"
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"variables": {
    		"company": "Ortus Solutions",
    		"product": {
    			"name": "BoxLang",
    			"supportEmail": "support@example.com"
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [variables]
    company = "Ortus Solutions"

    [variables.product]
    name = "BoxLang"
    supportEmail = "support@example.com"
    ```

Luego referencia cualquiera de sus valores, por ruta con puntos, desde
cualquier página de Markdown:

```markdown title="docs/index.md"
# Welcome to {{ company }}

We build {{ product.name }} tools. Need help? Write us at
{{ product.supportEmail }}.
```

se construye como:

```html
<h1>Welcome to Ortus Solutions</h1>
<p>We build BoxLang tools. Need help? Write us at support@example.com.</p>
```

Una variable `{{ }}` se resuelve una sola vez, en el momento de la
construcción, contra lo que sea que tenga en ese instante el propio
bloque `variables` de `bxsites.yaml` - renombra un producto, actualiza una
dirección de soporte o cambia un año en un solo lugar, y cada página que
lo usa recoge el cambio en la siguiente construcción. Consulta
[`variables`](../configuration.md#variables) en la referencia de
configuración.

## Funciones mágicas

Añade un archivo `docs/functions.bxs` (o `src/functions.bxs`, si tu
proyecto usa `src/` - consulta [Primeros Pasos](../getting-started.md)) -
un script BoxLang plano. Cualquier función que nombres con un `$` inicial
se convierte en una *función mágica*: invocable desde `{{ }}` en
Markdown, e invocable directamente, sin nada más, desde las propias
sobrescrituras `.bxm` de [`theme/`](themes.md#sobrescribir-un-tema) de un
proyecto.

```bx title="docs/functions.bxs" linenums="1"
function $shout( text ) {
	return uCase( arguments.text ) & "!"
}

function $badge( label, kind = "info" ) {
	return '<span class="badge bg-' & arguments.kind & '">' & arguments.label & '</span>'
}
```

```markdown title="docs/index.md"
{{ $shout('this is important') }}

Status: {{ $badge('Stable', 'success') }}
```

se construye como:

```html
<p>THIS IS IMPORTANT!</p>
<p>Status: <span class="badge bg-success">Stable</span></p>
```

Una función mágica puede devolver cualquier cosa convertible con
`toString()` - texto plano, HTML, un número - y se empalma directamente
en el markdown de la página antes de que se convierta, así que devolver
HTML real (como `$badge()` arriba) funciona exactamente como cabría
esperar.

Una función declarada *sin* un `$` inicial en ese mismo `functions.bxs`
es solo un ayudante privado, pensado para ser llamado únicamente desde
tus otras funciones con prefijo `$` en el mismo archivo (todas se cargan
en el mismo ámbito, así que una puede llamar a otra sin prefijo) - `{{ }}`
nunca puede llamar a una directamente (solo un destino de llamada
`$name(...)` se reconoce jamás), y tampoco forma parte de la superficie
pública documentada que una sobrescritura de tema debería llamar, aunque
resulte técnicamente alcanzable también ahí:

```bx title="docs/functions.bxs"
private string function formatPrice( amount ) {
	return "$" & numberFormat( arguments.amount, "9.99" )
}

function $price( amount ) {
	return formatPrice( arguments.amount )
}
```

### Llamar a una función mágica desde una sobrescritura de tema

Como una función mágica queda enlazada directamente en el ámbito de la
plantilla, el propio `theme/page.bxm` (o `layout.bxm`) de un proyecto
puede llamarla sin prefijo, exactamente de la misma forma en que ya lee
`variables.page`/`variables.siteConfig`:

```bx title="theme/page.bxm (excerpt)"
<p class="build-banner">#$shout( 'built with boxlang' )#</p>
```

### Variables de contexto

El propio cuerpo de cada función mágica también puede leer un conjunto
fijo de "variables de soporte" - sin prefijo, sin necesidad de ningún
argumento - sin importar si se invoca desde `{{ }}` en Markdown o sin
prefijo desde una sobrescritura de tema:

| Variable | Qué es |
|---|---|
| `siteConfig` | La propia configuración `bxsites.yaml` del sitio (ya con valores por defecto/validada) |
| `page` | La página actual (consulta la nota más abajo - no todos los campos están todavía completos cuando se llama desde Markdown) |
| `nav` | El propio árbol de navegación de este árbol |
| `basePath` | Ruta base relativa a la raíz, terminada en `/` |
| `versions` | Entradas del selector de versiones - `[ { label, url } ]` |
| `currentVersion` | Qué entrada de `versions` se está renderizando en este momento |
| `locales` | Entradas del selector de idioma - `[ { code, label, url, dir, flag } ]` |
| `currentLocale` | El código de qué entrada de `locales` se está renderizando en este momento |
| `currentLocaleDir` | `"ltr"`/`"rtl"` para el idioma actual |
| `data` | Los propios [archivos de datos](data-files.md) de este proyecto - `docs/data/*.yaml`/`.json`, una clave por archivo - `{}` cuando el proyecto no tiene ninguno |

```bx title="docs/functions.bxs"
function $sitename() {
	return siteConfig.name
}

function $pagetitle() {
	return page.title
}
```

```markdown title="docs/index.md"
Site: {{ $sitename() }}
Page: {{ $pagetitle() }}
```

**`page` no está igual de completa en ambos lugares.** Llamada desde
Markdown, `page` es el propio struct de esta página en concreto *tal como
se cargó desde disco* - `title`/`description`/`tags`/`icon`/`summary`/
`ogImage`/`urlPath`/`relativePath`/`body`/etc. ya están ahí, pero los
campos que solo se conocen una vez que todas las páginas del árbol han
terminado de convertirse - `toc`, `prevPage`/`nextPage`, `breadcrumbs`,
`editUrl`/`lastUpdated`, `iconHtml`, `markdownUrl`, `canonicalUrl`,
`course` - todavía no existen en ella. Llamada sin prefijo desde
`page.bxm`, `page` es el struct totalmente enriquecido, con todos esos
incluidos.

`page.course` está siempre presente (un struct totalmente vacío
`{id:"", courseTitle:"", lessonIndex:0, lessonTotal:0,
prevLesson:{title:"",url:""}, nextLesson:{title:"",url:""}, indexUrl:""}`
para una página que no forma parte de ningún [curso](courses.md)) -
`prevLesson`/`nextLesson` tienen ámbito solo a ese curso en concreto,
independientes del `prevPage`/`nextPage` de todo el sitio de más arriba,
que siempre recorre todo el árbol de navegación en su lugar. Cualquier
otra variable de soporte (`siteConfig`, `nav`, `basePath`, `versions`,
`currentVersion`, `locales`, `currentLocale`, `currentLocaleDir`) es
idéntica en ambos lugares.

### Sintaxis de los argumentos

Los argumentos de una llamada a función mágica son literales o
referencias a variables simples, separados por comas - sin llamadas a
función anidadas ni expresiones en esta primera versión:

- Números: `{{ $discount(20) }}`
- Cadenas entre comillas: `{{ $greet('World') }}` o `{{ $greet("World") }}`
- Booleanos: `{{ $badge('Beta', true) }}`
- Una referencia a variable con puntos, sin `{{ }}`: `{{ $greet(product.name) }}`

## Recetas de visualización

Una función mágica que devuelve HTML no se limita a una insignia de
estado - es una forma de propósito general de obtener celdas visuales
(una valoración con estrellas, un chip de color, una barra de progreso)
sin necesitar ningún selector de columnas respaldado por base de datos -
el origen basado en git y en Markdown plano de bx-sites no tiene
equivalente alguno de eso. Las cuatro de abajo son el propio
[`docs/functions.bxs`](https://github.com/ortus-boxlang/bx-sites/blob/development/docs/functions.bxs)
de este sitio, renderizándose en vivo justo en esta misma página.

### Valoraciones

```bx title="docs/functions.bxs"
function $stars( required numeric rating, numeric max = 5 ) {
	var filled = min( max( round( arguments.rating ), 0 ), arguments.max )
	var stars = repeatString( "★", filled ) & repeatString( "☆", arguments.max - filled )
	return '<span title="' & arguments.rating & ' out of ' & arguments.max & '" style="color:##f5a623;letter-spacing:2px">' & stars & '</span>'
}
```

`` `{{ $stars(4) }}` `` se renderiza como: {{ $stars(4) }}

### Chips de estado

```bx title="docs/functions.bxs"
function $badge( required string label, string kind = "info" ) {
	var palette = {
		"info"    : { "bg" : "##e0edff", "fg" : "##1d4ed8" },
		"success" : { "bg" : "##dcfce7", "fg" : "##15803d" },
		"danger"  : { "bg" : "##fee2e2", "fg" : "##b91c1c" },
		"warning" : { "bg" : "##fef9c3", "fg" : "##854d0e" }
	}
	var pick = palette.keyExists( arguments.kind ) ? palette[ arguments.kind ] : { "bg" : "##f1f5f9", "fg" : "##475569" }
	return '<span style="display:inline-block;padding:0.1em 0.6em;border-radius:999px;font-size:0.85em;font-weight:600;background:'
		& pick.bg & ";color:" & pick.fg & '">' & encodeForHTML( arguments.label ) & "</span>"
}
```

`` `{{ $badge('Stable', 'success') }}` `` se renderiza como: {{ $badge('Stable', 'success') }} - y `` `{{ $badge('Beta', 'info') }}` ``: {{ $badge('Beta', 'info') }}

### Barras de progreso

```bx title="docs/functions.bxs"
function $progress( required numeric percent ) {
	var pct = min( max( arguments.percent, 0 ), 100 )
	return '<span style="display:inline-block;width:120px;height:8px;background:##e5e7eb;border-radius:999px;overflow:hidden;vertical-align:middle"><span style="display:block;height:100%;width:'
		& pct & '%;background:##2563eb"></span></span> ' & pct & "%"
}
```

`` `{{ $progress(72) }}` `` se renderiza como: {{ $progress(72) }}

### Indicadores de tendencia

```bx title="docs/functions.bxs"
function $trend( required numeric value ) {
	var isUp = arguments.value >= 0
	var arrow = isUp ? "▲" : "▼"
	var color = isUp ? "##16a34a" : "##dc2626"
	var sign = isUp ? "+" : ""
	return '<span style="color:' & color & ';font-weight:600">' & arrow & " " & sign & numberFormat( arguments.value, "0.0" ) & "%</span>"
}
```

`` `{{ $trend(4.2) }}` `` se renderiza como: {{ $trend(4.2) }} - `` `{{ $trend(-1.8) }}` ``: {{ $trend(-1.8) }}

### Dentro de una celda de tabla

`{{ }}` se resuelve contra el Markdown en bruto antes incluso de que se
analicen las [tablas](tables.md), así que cualquiera de las
anteriores funciona dentro de las celdas de una tabla de pipes igual que
en cualquier otra parte de la página:

```markdown title="Example" linenums="1"
| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |
```

Lo que se renderiza como:

| Feature | Status | Rating |
| --- | --- | --- |
| Dark mode | {{ $badge('Stable', 'success') }} | {{ $stars(5) }} |
| Table sort | {{ $badge('Beta', 'info') }} | {{ $stars(4) }} |

## Mostrar la sintaxis de forma literal

Un `{{ }}` mostrado dentro de un bloque de código con fence (tres
comillas invertidas o más, como todos los ejemplos de esta página) se
deja completamente intacto en lugar de resolverse - la misma convención
que este módulo ya usa para las matemáticas `$...$` y el contenido de
pestañas `=== "Tab"`. A diferencia de esos dos casos, un `{{ }}`
mostrado en código *en línea* (`` `{{ example }}` ``, con comillas
invertidas simples o dobles) también está protegido - cada punto de la
lista anterior que muestra `` `{{ $discount(20) }}` `` en línea es un
ejemplo real y funcional de ello.

Un `{{ }}` cuyo contenido no se parezca ni a una ruta de variable ni a
una llamada `$name(...)` - la propia sintaxis `{{ }}` de otro motor de
plantillas mostrada en el texto, por ejemplo - se deja intacto en lugar
de tratarse como un error. Solo un token que *parezca* una variable o una
llamada a función mágica, pero que no se resuelva, hace fallar la
construcción (consulta [Errores](#errores) más abajo) - eso es
deliberado, para detectar un error tipográfico real sin interpretar
erróneamente texto `{{ }}` no relacionado como sintaxis rota.

## Ámbito

- `functions.bxs` es de todo el proyecto - un solo archivo, cargado una
  vez, con el mismo conjunto de funciones mágicas disponible en cada
  página, tanto en el árbol principal como en cada árbol de
  [versión](versioning.md)/[idioma](i18n.md). No necesitas duplicarlo en
  `docs/versions/<name>/` ni en `docs/i18n/<code>/`.
- `variables` es igualmente un único bloque de `bxsites.yaml` de todo el
  proyecto - no es traducible por idioma en sí mismo. Un proyecto
  multilingüe que quiera texto de variable distinto por idioma puede en
  su lugar recurrir a una función mágica que decida según
  `siteConfig.i18n.defaultLocale.code` (o simplemente mantener el valor
  neutral respecto al idioma - un nombre de producto, un correo de
  soporte).

## Nombres reservados

Una sobrescritura `theme/page.bxm`/`layout.bxm` que llama a una función
mágica sin prefijo (`$name(...)`) funciona porque toda función cargada -
con prefijo `$` o ayudante privado por igual - se enlaza directamente en
ese mismo ámbito de renderizado de la plantilla, justo junto a los
`variables.page`/`variables.siteConfig`/etc. incorporados que ya lee
cualquier tema. Eso significa que una función de `functions.bxs` que
comparta nombre con uno de esos ya tiene uno propio: evita `page`, `nav`,
`siteConfig`, `themeDir`, `basePath`, `moduleAssetsDir`, `versions`,
`currentVersion`, `locales`, `currentLocale`, `currentLocaleDir`,
`strings`, `requiredFiles`, `stringsResolver` y `data` como nombre propio
para un ayudante privado (una función mágica con prefijo `$` nunca puede
chocar con ninguno de estos, ya que ninguno de ellos empieza con `$`).
Consulta [Archivos de Datos: Ámbito](data-files.md#ámbito) para la nota
sobre el propio nombre reservado de `data`.

## Errores

- `BxSites.UnknownVariable` - un `{{ dotted.path }}` (o un argumento de
  `$name(...)` que parece una referencia a variable) no coincide con nada
  del bloque `variables` de `bxsites.yaml`.
- `BxSites.UnknownFunction` - una llamada `{{ $name(...) }}` no coincide
  con ninguna función con prefijo `$` en `docs/functions.bxs`.
- `BxSites.InvalidFunctions` - `docs/functions.bxs` no pudo cargarse (un
  error de sintaxis de BoxLang en el propio archivo).
- `BxSites.InvalidConfig` - la clave `variables` de `bxsites.yaml` está
  presente, pero no es un objeto.
