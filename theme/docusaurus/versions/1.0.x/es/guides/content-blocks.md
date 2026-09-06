---
title: Bloques de Contenido
order: 4.5
icon: phosphor-duotone:squares-four
summary: Cuadrículas de tarjetas, columnas, un stepper, tarjetas de archivo/incrustación/vista previa, un registro de cambios y contenido reutilizable.
tags: [guías, markdown]
---

# Bloques de Contenido

Además de todo lo que hay en [Extensiones de Markdown](markdown.md), BX
Sites admite una familia de bloques de contenido enriquecido para cosas
que el CommonMark plano no contempla en absoluto - tarjetas, pestañas de
pasos, descargas, incrustaciones, y más. Cada uno usa la misma sintaxis
de contenedor `::: name ... :::` (un `:::` solo en su propia línea
cierra el bloque que esté abierto en ese momento, o escribe el `:::` de
cierre en la misma línea para un bloque sin cuerpo propio -
`::: file src="assets/spec.pdf" :::` funciona exactamente igual que la
forma de dos líneas) - sin necesidad de configuración en `bxsites.yaml`,
siempre disponible. Un bloque puede anidarse dentro de otro (un
expandible que contiene un grupo de tarjetas, por ejemplo) - cada uno se
vuelve a analizar en busca de más bloques dentro de su propio contenido.
¿Estás migrando desde GitBook? Cada bloque de aquí se corresponde
directamente con su homólogo de GitBook del mismo nombre - consulta
[Migrar desde GitBook](migrating-from-gitbook.md).

## Expandible

Una sección colapsable simple - sin icono/color de aviso, a diferencia de
una admonición colapsable (`???`, consulta
[Admoniciones](markdown.md#admoniciones-colapsables)):

```markdown title="Example" linenums="1"
::: expandable "Is this different from a collapsible admonition?"
Yes - this has no type/icon/color, just a plain expand/collapse section.
Add `open="true"` to start it expanded.
:::
```

::: expandable "¿Es esto diferente de una admonición colapsable?"
Sí - esto no tiene tipo/icono/color, solo una sección simple de
expandir/colapsar. Añade `open="true"` para que empiece expandida.
:::

## Tarjetas

Una cuadrícula de tarjetas de enlace, cada una su propia `::: card`
dentro de un envoltorio `::: cards` - `title`, `icon`, `image` y `href`
son todos opcionales (una tarjeta sin `href` se renderiza como una
tarjeta simple, no clicable). `icon` se resuelve de la misma forma que
los valores `icon` de frontmatter/nav - un emoji sencillo, o un icono con
nombre de una biblioteca incluida (`icon="phosphor-duotone:rocket-launch"`,
`icon="lucide:rocket"`, ...) - consulta [Iconos](icons.md):

```markdown title="Example" linenums="1"
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
::: card title="Primeros Pasos" icon="phosphor-duotone:rocket-launch" href="../getting-started.md"
Instala, crea la estructura y construye tu primer sitio.
:::
::: card title="Temas" icon="phosphor-duotone:palette" href="themes.md"
Personaliza un tema incorporado o escribe el tuyo propio.
:::
:::

## Columnas

Un diseño lado a lado - `::: column` acepta un `width` opcional (una
longitud/porcentaje CSS simple, por ejemplo `"40%"`); las columnas sin un
ancho explícito comparten la fila equitativamente:

```markdown title="Example" linenums="1"
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
La columna más ancha.
:::
::: column
La más estrecha.
:::
:::

## Stepper

Una secuencia numerada y conectada de pasos:

```markdown title="Example" linenums="1"
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
::: step "Instalar"
`install-bx-module bx-sites`
:::
::: step "Crear estructura"
`bxSites new`
:::
:::

El atributo `color` opcional de un paso marca su indicador con uno de
cuatro colores semánticos - el predeterminado (sin `color`), `success`,
`warning` o `danger` - independientemente de la posición del paso en la
secuencia:

```markdown title="Example" linenums="1"
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
::: step "Respalda tus datos" color="success"
Rutinario, seguro de ejecutar en cualquier momento.
:::
::: step "Opcional: activar telemetría" color="warning"
Omite este paso si no estás seguro.
:::
::: step "Elimina la instalación anterior" color="danger"
Irreversible - asegúrate de que el respaldo anterior haya terminado primero.
:::
:::

El indicador numerado, la línea de conexión y cada una de las tres
paletas de `color` de arriba se pueden personalizar de forma
independiente al resto de la paleta del sitio, mediante propiedades CSS
personalizadas - consulta
[Personalizar colores](themes.md#personalizar-colores-sin-sobrescribir-un-tema).

## Archivo

Una tarjeta de descarga para un PDF, video, o cualquier otro recurso del
proyecto - `src` se resuelve de la misma forma que ya lo hacen
`theme.logo`/el `ogImage` del frontmatter (relativo a `docs/assets/`):

```markdown title="Example" linenums="1"
::: file src="assets/spec.pdf" title="API Specification" :::
```

::: file src="assets/og-image.png" title="Imagen de vista previa del sitio" :::

## Botones

Un botón de llamada a la acción - `::: button` por sí solo, o varios
dispuestos en una fila dentro de un envoltorio `::: buttons`. La
`"Label"` inicial y el `href` son las únicas piezas que necesita la
mayoría de los botones:

```markdown title="Example" linenums="1"
::: button "Get Started" href="../getting-started.md" style="primary" :::
```

::: button "Primeros Pasos" href="../getting-started.md" style="primary" :::

Unos pocos atributos opcionales le dan a cada botón sus propias
habilidades:

- `style="primary"` o `style="secondary"` (el valor predeterminado) -
  acento sólido frente a contorno.
- `size="small"`, `"medium"` (el valor predeterminado) o `"large"`.
- `icon="..."` - se resuelve de la misma forma que el propio `icon` de
  una tarjeta (un emoji sencillo, o un icono con nombre como
  `icon="phosphor-duotone:rocket-launch"` - consulta
  [Temas: Iconos](themes.md#iconos)).
- `target="_blank"` - abre el enlace en una pestaña nueva en lugar de la
  misma (`rel="noopener noreferrer"` se añade automáticamente).
- `disabled="true"` - renderiza un botón inerte, no clicable (sin
  necesidad de `href`) para una llamada a la acción de "próximamente".

```markdown title="Example" linenums="1"
::: buttons
::: button "Read the docs" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Star on GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Coming soon" disabled="true" :::
:::
```

::: buttons
::: button "Lee la documentación" href="../getting-started.md" icon="phosphor-duotone:book-open" size="large" :::
::: button "Danos una estrella en GitHub" href="https://github.com/ortus-boxlang/bx-sites" style="secondary" target="_blank" :::
::: button "Próximamente" disabled="true" :::
:::

## Incrustación

Una incrustación de iframe responsiva para un proveedor reconocido -
actualmente YouTube, Vimeo, CodePen, Spotify, Loom y Figma. Una URL de
cualquier otro lugar recurre a una simple tarjeta de enlace "visit ↗" en
lugar de un iframe que simplemente se negaría a renderizarse (la mayoría
de los sitios bloquean ser incrustados en un frame):

```markdown title="Example" linenums="1"
::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="A demo" :::
```

::: embed url="https://www.youtube.com/watch?v=dQw4w9WgXcQ" title="Una demostración" :::

## Enlace de página

Una tarjeta de vista previa enriquecida que enlaza a otra página - `href`
sigue la misma convención relativa a archivos que un
[enlace de página](../getting-started.md#enlazar-entre-páginas)
ordinario. A diferencia de una tarjeta, su título/icono/resumen se
extraen automáticamente del propio frontmatter de la página de destino,
así que se mantiene sincronizado si esa página se renombra o cambia su
resumen:

```markdown title="Example" linenums="1"
::: page-link href="../getting-started.md" :::
```

::: page-link href="../getting-started.md" :::

## Vista previa de enlace

Una tarjeta de vista previa enriquecida para una URL *externa* - la
misma forma de tarjeta que `::: page-link`, pero para un enlace que no
es una de las propias páginas de este sitio, así que no hay ninguna
página de la que extraer automáticamente un título/resumen. Cada campo
proviene de los propios atributos de la directiva: solo `url` es
obligatorio, `title` recurre a la URL desnuda cuando se omite, y
`description`/`image` son ambos opcionales. No hay ninguna obtención en
el momento de la construcción de la URL de destino para autocompletar
estos campos - el mismo razonamiento que mantiene a
[`audit`](../cli-reference.md) limitado solo a enlaces internos se
aplica también aquí, de modo que un sitio de terceros lento o
inalcanzable nunca afecta al tiempo de construcción:

```markdown title="Example" linenums="1"
::: link-preview url="https://boxlang.io" title="BoxLang" description="A dynamic, multi-paradigm JVM language." :::
```

::: link-preview url="https://boxlang.io" title="BoxLang" description="Un lenguaje JVM dinámico y multiparadigma." :::

## Prompt de IA

Un contenedor con estilo propio para un prompt de IA reutilizable. El
cuerpo del bloque *es* el texto del prompt, escrito como
Markdown normal (así que los encabezados, listas y código que contenga
siguen recibiendo su propio formato); todo prompt obtiene un botón
"Copy" que copia ese texto fuente exacto, marcado de formato incluido,
listo para pegar en cualquier herramienta de IA con la que lo estés
usando. `description` (un resumen opcional de una línea) e `icon`
(resuelto de la misma forma que el propio `icon` de `::: card` - por
defecto usa un glifo de destello cuando se omite) son ambos opcionales:

```markdown title="Ejemplo" linenums="1"
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

Añade `expanded="preview"` para recortar un prompt largo a una vista
previa corta, con desvanecido, hasta que quien lee haga clic en "Show
more", o `expanded="hidden"` para que empiece completamente colapsado
detrás de un botón "Show prompt" - útil para una página que enumera
varios prompts seguidos. Omite `expanded` (o ajústalo a `"full"`, el
valor predeterminado) para mostrar siempre el prompt completo:

```markdown title="Ejemplo" linenums="1"
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

Aquí no hay ningún menú "Open in AI providers" - bx-sites nunca se
comunica con un proveedor de IA externo, así que el propio botón "Copy"
de un prompt es la única forma de llevarlo a la herramienta que estés
usando.

## Novedades (registro de cambios)

Una lista de registro de cambios con fecha y etiquetable - `::: update`
acepta `date="YYYY-MM-DD"` y unas `tags` opcionales separadas por comas:

```markdown title="Example" linenums="1"
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
Se añadió el modo oscuro y se corrigió un error de alineación en el pie
de página.
:::
::: update date="2026-01-01"
Lanzamiento inicial.
:::
:::

Una página con un bloque `::: updates` también obtiene su propio
`feed.xml` (RSS 2.0) escrito junto a ella en cuanto `baseURL` de
`bxsites.yaml` es una URL completa - el mismo requisito que
`sitemap.xml` - de modo que los lectores puedan suscribirse solo al
registro de cambios de esa página.

## Contenido reutilizable (inclusiones)

`::: include src="..."` empalma el Markdown en bruto de otro archivo en
ese punto. A diferencia de todos los bloques anteriores, esto se
convierte en contenido de página real (encabezados, párrafos, sus
propios bloques anidados), no algo envuelto en un widget - útil para una
advertencia/aviso repetido en varias páginas. Coloca el propio parcial
bajo `docs/includes/` - la misma convención de carpeta reservada que
`assets/`/`versions/`/`i18n/`/`blog/`. Un archivo bajo `includes/` nunca
se construye como su propia página y nunca aparece en la
navegación/búsqueda/sitemap/etiquetas - solo existe para empalmarse en
otras páginas:

```text title="Estructura de docs/"
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

Un `src` **simple** (sin `./` ni `../` iniciales) siempre se resuelve
contra el propio `docs/includes/` del árbol actual, sin importar cuán
anidada esté la página que lo incluye - `guides/deep/setup.md` de arriba
llega al mismo archivo que `index.md`, ambos con exactamente el mismo
`src`:

```markdown title="Desde index.md o desde guides/deep/setup.md, indistintamente"
::: include src="beta-notice.md"
```

Un `src` simple también puede apuntar a una subcarpeta del propio
`includes/`:

```markdown title="Example"
::: include src="legal/terms.md"
```

Antepón `./` o `../` a `src` en su lugar para llegar a un fragmento
adyacente a la página que no está pensado para vivir en el `includes/`
centralizado - esa forma se resuelve relativa al archivo, respecto a la
propia carpeta de la página *que incluye*, la misma convención que un
enlace de página ordinario:

```markdown title="Desde guides/deep/setup.md, un nivel arriba en lugar de centralizado"
::: include src="../local-note.md"
```

Un árbol de versión/idioma obtiene su propio `includes/` de la misma
forma - una página bajo `docs/versions/2.0/` resuelve un `src` simple
contra `docs/versions/2.0/includes/`, y una bajo `docs/i18n/es/` contra
`docs/i18n/es/includes/` - los parciales de cada árbol son propios, no
se comparten con el `docs/includes/` del árbol principal.

Un archivo incluido puede a su vez incluir otro (una cadena circular
lanza `BxSites.CircularInclude` en el momento de la construcción en
lugar de entrar en un bucle infinito).

## Contenido condicional

Muestra una de varias variantes de un bloque según la propia elección de
quien lee - instrucciones "Free" frente a "Pro" en la misma página,
digamos. Este es un sitio totalmente estático sin ningún tipo de
identidad de visitante, así que, a diferencia de una plataforma con un
backend real, no hay un "quién es este lector" evaluado en el servidor -
quien lee elige por sí mismo, y su elección simplemente se recuerda en
su propio navegador (`localStorage`) también para cada página posterior:

```markdown title="Ejemplo" linenums="1"
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

`::: conditional key="..." value="..."` marca una variante; `key` es el
nombre de preferencia que sea que estés alternando (`"plan"` arriba,
aunque igual podría ser `"os"`, `"language"`, cualquier cosa), y `value`
es el ajuste concreto para el que este bloque en particular debe
mostrarse. Cada variante siempre se renderiza en el HTML - oculta del
lado del cliente, nunca omitida - así que un lector con JavaScript
desactivado (o un rastreador de búsqueda) sigue viendo todas las
variantes en lugar de ninguna.

`::: audience-switcher key="..." options="value:Label,value:Label,..."`
es un control opcional, ya preparado - un botón por opción, que cambia
inmediatamente cada bloque `::: conditional` que comparta esa misma
`key`, en cualquier lugar de la página. No lo necesitas en absoluto: un
enlace que termine en `?plan=pro` establece automáticamente la misma
preferencia al cargar (útil para compartir un enlace directo a "la
versión Pro de esta página"), y la sobrescritura de tema propia de un
proyecto puede llamar directamente a
`window.bxSitesSetPreference( key, value )` para controlarlo desde una
UI personalizada en su lugar.

## Bucle y condicional (basado en datos)

`::: for` y `::: if` renderizan su propio contenido contra [datos
reutilizables](data-files.md) - el propio valor de un archivo
`docs/data/*.yaml`/`.json`, direccionado por ruta con puntos. A
diferencia de todos los bloques anteriores, estos dos toman una
expresión simple en lugar de atributos `key="value"` - deliberadamente
estrecho, la misma filosofía de solo-ruta-con-puntos que ya usa el
propio `{{ }}` (sin operadores de comparación en esta primera versión):

```markdown title="Example" linenums="1"
::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::
```

::: for member, idx in data.team
{{ idx }}. **{{ member.name }}** - {{ member.role }}
:::

`::: for <item>, <index> in <dotted.path>` enlaza `<item>`/`<index>` de
la misma forma en que ya lo hace el propio bucle `for` de dos variables
de BoxLang para lo que sea que resuelva la ruta - elemento + índice en
base 1 para un array (como arriba), o clave + valor para un struct, con
la sintaxis idéntica en ambos casos:

```markdown title="Example" linenums="1"
::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::
```

::: for name, enabled in data.flags
- {{ name }}: {{ enabled }}
:::

`::: if <dotted.path>` renderiza su contenido solo cuando el valor
resuelto es verdadero - un array/struct/cadena vacío, `0` y `false`
cuentan todos como falso:

```markdown title="Example" linenums="1"
::: if data.flags.betaBanner
Beta features are enabled on this build.
:::
```

::: if data.flags.betaBanner
Las funciones beta están activadas en esta compilación.
:::

Encadena `::: elseif <dotted.path>` (cualquier cantidad de ellos) y un
`::: else` final sin condición después de un `::: if` para una semántica
real de `if`/`elseif`/`else` - la primera condición verdadera gana,
`::: else` (sin condición propia) captura lo que quede, y una condición
posterior a la ganadora nunca llega siquiera a resolverse, así que una
ruta de `::: elseif` con un error tipográfico solo rompe la construcción
una vez que realmente se alcanza su propia rama. Toda la cadena se cierra
con **un** `:::` final - `::: elseif`/`::: else` marcan por sí mismos
dónde termina la rama anterior, así que no hace falta ningún `:::` antes
de cada uno:

```markdown title="Example" linenums="1"
::: if data.flags.darkModeDefault
Dark mode is on by default.
::: elseif data.flags.betaBanner
Beta features are enabled, though dark mode isn't on by default.
::: else
Nothing special about this build.
:::
```

::: if data.flags.darkModeDefault
El modo oscuro está activado por defecto.
::: elseif data.flags.betaBanner
Las funciones beta están activadas, aunque el modo oscuro no lo está por
defecto.
::: else
Nada especial en esta compilación.
:::

Un `:::` antes de un `::: elseif`/`::: else` también sigue funcionando,
si prefieres cerrar cada rama explícitamente - ambas formas se analizan
de forma idéntica.

Ambos cuerpos pueden contener Markdown normal e incluso otros bloques de
contenido - incluyendo otro `::: for`/`::: if`, anidado exactamente igual
que cualquier bloque anterior. Consulta
[Archivos de Datos: Consumir datos](data-files.md#consumir-datos) para
la historia completa de bucle/condicional, incluyendo las otras dos
formas de trabajar con `data.*` - una sobrescritura de tema, o una
función mágica.

## Índice de curso

`::: course id="..." :::` renderiza las lecciones de todo un
[curso](courses.md) como un índice numerado y enlazado - una gran lista
"1. Introducción, 2. Instalación en Windows, 3. Instalación en Mac..."
donde cada número es un enlace real, construida a partir de un
manifiesto `docs/data/courses.yaml` en lugar de redactarse a mano:

```markdown title="Example" linenums="1"
::: course id="getting-started" :::
```

A diferencia de todos los bloques anteriores, este solo toma un `id`
desnudo, sin `href` ni contenido propio de cuerpo - las propias lecciones,
y su orden, provienen enteramente del manifiesto. Consulta
[Cursos](courses.md) para el formato del manifiesto, la navegación de
lección a lección de ámbito propio, y cómo se rastrea el progreso de
quien lee una vez que el índice está en la página.
