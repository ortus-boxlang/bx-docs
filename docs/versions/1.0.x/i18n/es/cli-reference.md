---
title: Referencia de la CLI
order: 3
icon: phosphor-duotone:terminal-window
summary: Cada verbo de bxSites y sus opciones.
tags: [referencia, cli]
---

# Referencia de la CLI

```bash title="Uso"
bxSites <verb> [options]
```

`box install bx-sites` coloca un script `bxSites` independiente en tu
`PATH` (mediante `boxlang.executable` de `box.json`), de modo que cada
verbo de abajo puede ejecutarse tanto de esa forma corta, como en la
forma `boxlang bxSites <verb>` - ambas ejecutan exactamente lo
mismo; usa la forma más larga en cualquier lugar donde el atajo del
`PATH` no esté configurado (un ejecutor de CI, un módulo registrado a
mano):

```bash title="Uso (sin el atajo del PATH)"
boxlang bxSites <verb> [options]
```

Cada verbo acepta `--projectRoot=<path>` (o una ruta posicional simple)
para apuntar a un proyecto distinto del directorio actual, y las dos
opciones globales de abajo pueden aparecer antes de cualquier verbo.

Cada `docs/` mencionado abajo se aplica igualmente a un proyecto que use
`src/` en su lugar - consulta [Primeros Pasos](getting-started.md#añadir-páginas)
para la convención `docs/`-o-`src/`. `new` siempre genera `docs/`.

## Opciones globales

| Flag | Descripción |
|---|---|
| `-h`, `--help` | Muestra el uso y sale |
| `-v`, `--version` | Muestra la versión del módulo y sale |

## `new`

Crea la estructura de un proyecto de documentación.

```bash title="Uso"
bxSites new [path] [--name=...] [--theme=<consulta guides/themes.md para los 10>] [--description=...] [--format=yaml|json]
```

- `--name` - el nombre del sitio escrito en la configuración del sitio (por defecto, el nombre del directorio de destino)
- `--theme` - por defecto `bootstrap`
- `--description` - la descripción del sitio escrita en la configuración del sitio
- `--format` - `yaml` (por defecto, genera `bxsites.yaml`) o `json` (genera `bxsites.json`) - consulta [Configuración](configuration.md)

## `build`

Renderiza `docs/**.md` en un sitio estático en `site/`. También construye
el índice de búsqueda (a menos que `search` sea `false` en la
configuración del sitio, o que `searchProvider` esté configurado a un
proveedor - como `algolia`/`pagefind` - que no lo use, consulta
[Búsqueda](guides/search.md)), ejecuta la CLI de `pagefind` contra el
`site/` terminado cuando `searchProvider.provider` es `"pagefind"`, y
copia el tema + `docs/assets/**` en `site/`.

```bash frame="terminal" title="Terminal"
bxSites build
```

## `serve`

Construye y sirve el sitio localmente con recarga en vivo.

```bash title="Uso"
bxSites serve [--port=8080] [--host=127.0.0.1]
```

Se ejecuta en primer plano hasta que se interrumpe (Ctrl+C).

## `search-index`

Reconstruye `site/search-index.json` de forma independiente, sin volver a
renderizar páginas ni copiar recursos. `build` ya ejecuta este mismo paso
automáticamente - este verbo existe para cuando solo necesitas actualizar
el índice. Solo cubre alguna vez el árbol `docs/` principal, incluso en
un proyecto con `docs/versions/`/`docs/i18n/` - una construcción real
(`build`) escribe en su lugar el propio índice acotado de cada árbol
(consulta [Versionado](guides/versioning.md#qué-queda-fuera-de-alcance-por-ahora)).

```bash frame="terminal" title="Terminal"
bxSites search-index
```

## `clean`

Elimina `site/` y cualquier caché de construcción, dejando intactos
`docs/` y la configuración del sitio.

```bash frame="terminal" title="Terminal"
bxSites clean
```

## `gh-deploy`

Construye el sitio y luego lo empuja con force-push a una rama al estilo
`gh-pages` - un commit por despliegue, sin historial acumulado en esa
rama, siguiendo la propia convención `mkdocs gh-deploy` de mkdocs.
Requiere que el proyecto sea un repositorio git con un remoto configurado;
nunca toca tu propia rama actual ni tu árbol de trabajo (hace el push
desde un `git worktree` desechable).

```bash title="Uso"
bxSites gh-deploy [--branch=gh-pages] [--remote=origin] [--message="..."]
```

- `--branch` - por defecto `gh-pages`
- `--remote` - por defecto `origin`
- `--message` - el mensaje del único commit de la rama, por defecto `"Deploy site via bxSites gh-deploy"`

Consulta [Despliegue](guides/deployment.md) para la configuración completa
de GitHub Pages (activar Pages para la rama, `baseURL`, etc.).

## `deploy`

Construye el sitio y luego lo envía a un destino de despliegue real - S3
(y cualquier servicio compatible con S3 - DigitalOcean Spaces, Cloudflare
R2, Backblaze B2, MinIO), Azure Blob Storage, Google Cloud Storage,
Firebase Hosting, FTP, SFTP, rsync sobre SSH, Netlify, Vercel, Cloudflare
Pages, un directorio local, o GitHub Pages (el mismo push que hace
`gh-deploy`, solo que también accesible desde este comando unificado).

```bash title="Uso"
bxSites deploy --entry=<name> [--verbose]
bxSites deploy [--target=local|github-pages] [target-specific flags] [--verbose]
bxSites deploy [--verbose]
```

Tres formas de invocarlo:

1. **`--entry=<name>`** - despacha al destino que declare un archivo
   `deployments/<name>.json` (consulta más abajo). Todo destino salvo
   `local`/`github-pages` lo necesita - hay más configuración de la que
   un par de opciones puede razonablemente llevar.
2. **`--target=<name>` con sus propias opciones** - un atajo solo con
   opciones para los dos destinos más simples, que no necesitan ninguna
   carpeta `deployments/`: `local` (`--destination=<path>`) y
   `github-pages` (`[--branch] [--remote] [--message]`, todos los campos
   opcionales, con los mismos valores por defecto que `gh-deploy`).
3. **Ni una opción ni la otra - despliega todo.** Cada entrada
   `deployments/*.json` se despliega por turno, a partir de una única
   construcción compartida (el sitio se construye una sola vez, no una
   vez por destino). Requiere que exista al menos una entrada
   `deployments/*.json`. Que un destino falle no detiene el resto - se
   intenta cada entrada, y el comando solo sale con un código distinto de
   cero si al menos uno de ellos falló; el resumen final reporta cuántos
   tuvieron éxito (por ejemplo, `Deployed to 2/3 target(s) (1 failed)`).

`--verbose` imprime una línea de progreso a medida que la construcción
comienza/termina y cada destino comienza/termina, en lugar de solo el
resumen final de una línea.

Consulta [Despliegue](guides/deployment.md) para la configuración propia
de cada destino y un ejemplo real de `deployments/*.json` para cada uno.

## `package`

Construye el sitio y luego lo comprime en un único archivo distribuible -
un zip simple cuya raíz son los propios contenidos del sitio construido
(no una carpeta `site/` que lo envuelva), listo para adjuntar a un
release o entregar a cualquier host que solo acepte una carga en zip.

```bash title="Uso"
bxSites package [--output=<path>]
```

`--output` por defecto es `<projectRoot>/site.zip` (un valor relativo se
resuelve contra la raíz del proyecto); los directorios padre de un
destino anidado se crean automáticamente.

## `migrate`

Convierte un proyecto de documentación existente en este - `--from`
elige el formato de origen: `gitbook` (el predeterminado), `mkdocs`,
`markdown-zip`, o `notion`.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites migrate --source=/path/to/gitbook-export
bxSites migrate --source=/path/to/mkdocs-project --from=mkdocs
bxSites migrate --source=/path/to/export.zip --from=markdown-zip
bxSites migrate --source=/path/to/notion-export --from=notion
```

- `--source` (obligatorio) - ruta al directorio raíz de la exportación/proyecto (`SUMMARY.md` para `gitbook`, `mkdocs.yml` para `mkdocs`), o un archivo `.zip` (`markdown-zip`; `notion` acepta tanto un `.zip` como una carpeta ya extraída)
- `--from` - `gitbook` (predeterminado), `mkdocs`, `markdown-zip`, o `notion`

### `--from=gitbook` (predeterminado)

Una exportación de GitBook - una tabla de contenidos `SUMMARY.md` más sus
archivos `.md`, el propio formato de sincronización en disco de GitBook -
en el árbol `docs/` de este proyecto: `SUMMARY.md` se convierte en
`docs/nav.json`, la sintaxis `{% block %}` se convierte en su equivalente
de bx-sites (directivas `::: name`, o la sintaxis nativa de pestañas
`=== "Title"` / admoniciones `!!! type` cuando ya existe una
correspondencia más cercana - consulta
[Bloques de Contenido](guides/content-blocks.md)), los archivos
`README.md` se convierten en `index.md`, y `.gitbook/assets/**` se copia
a `docs/assets/gitbook/`.

### `--from=mkdocs`

Un proyecto mkdocs - `mkdocs.yml` más su carpeta `docs/` - en un proyecto
bx-sites completo: `mkdocs.yml` se convierte en `bxsites.yaml` +
`docs/nav.json`, y cada página se copia prácticamente sin cambios, ya que
la propia sintaxis de admoniciones/pestañas/matemáticas/anotaciones de
código de mkdocs-material ya *es* la propia sintaxis nativa de bx-sites -
consulta [Migrar desde mkdocs](guides/migrating-from-mkdocs.md). Los
recursos que no son `.md` (imágenes que habitualmente están junto a la
página que las usa, mkdocs no tiene una única convención de carpeta de
recursos) se reubican en `docs/assets/mkdocs/` y sus referencias se
reescriben.

### `--from=markdown-zip`

Un `.zip` simple de archivos Markdown - sin ningún formato de
exportación propietario que traducir, ya que el propio anidamiento de
una carpeta ya *es* la propia convención de navegación de bx-sites y un
enlace `.md` relativo de página a página ya se resuelve de la forma que
bx-sites espera. Mayormente una copia directa: cada archivo que no es
`.md` (una imagen, digamos) se reubica en `docs/assets/imported/` y la
propia referencia de cada página a él se reescribe en consecuencia. No
se escribe ningún `bxsites.yaml`/`docs/nav.json` - un zip simple no
lleva consigo ningún nombre de sitio ni estructura de navegación propios
que traducir.

### `--from=notion`

Un archivo de exportación de Notion "Export as Markdown & CSV" (un
`.zip`, o una carpeta ya extraída) - maneja las dos peculiaridades
propias de Notion que ninguna otra migración de aquí tiene que resolver:
cada carpeta de página/subpágina lleva como sufijo un espacio y un id de
32 caracteres (para distinguir páginas con el mismo título, nunca pensado
para leerse), y el título de una página se repite como un `# Heading`
literal al principio en lugar de llevarse en el frontmatter. Ambas cosas
se limpian: el sufijo del id se elimina y el nombre restante se
convierte en slug para el nombre del archivo de salida, el encabezado
inicial se convierte en un campo `title` real del frontmatter en lugar
de una primera línea duplicada, y cada destino de enlace/imagen (que
Notion escribe codificado como URL, apuntando todavía a los nombres
originales con sufijo de id) se reescribe en consecuencia. Los archivos
que no son `.md` se reubican en `docs/assets/imported/`, igual que
`markdown-zip` arriba.

### Los cuatro

Imprime un resumen de las páginas (y, para mkdocs/markdown-zip/notion,
los recursos) convertidos y, cuando algo no pudo convertirse
automáticamente, una lista de exactamente qué necesita revisión manual -
nada se descarta silenciosamente. Un archivo de destino, `bxsites.yaml`,
o `docs/nav.json` que ya exista se sobrescribe (también se informa), así
que revisa la salida migrada antes de confirmarla.

## `site:check`

Una puerta de calidad de contenido de nivel CI sobre un `site/` ya
construido - ejecuta `build` primero. Verifica:

- **Enlaces/imágenes internos rotos** - cualquier `<a href>`/`<img src>`
  que apunte a una página o recurso que no exista en `site/`. Hace
  fallar la verificación.
- **Texto alternativo faltante** - cualquier `<img>` sin ningún atributo
  `alt` en absoluto. Un `alt=""` vacío (el marcado correcto para una
  imagen puramente decorativa) no se marca. Hace fallar la verificación.
- **Páginas huérfanas** - páginas que existen en `site/` pero no son
  alcanzables siguiendo enlaces desde la propia página de inicio de
  ningún árbol (el `index.html` del sitio principal, y el propio de cada
  versión/idioma). Solo informativo - nunca hace fallar la verificación,
  ya que una página que un proyecto dejó deliberadamente fuera de su
  propia navegación (por ejemplo, `hidden: true` en el frontmatter) se
  *supone* que solo es alcanzable mediante un enlace directo.

```bash frame="terminal" title="Terminal" linenums="1"
bxSites build
bxSites site:check
```

Sale con `1` cuando hay algún enlace/imagen roto o alguna imagen sin
`alt`, `0` en caso contrario (las páginas huérfanas nunca afectan el
código de salida). Deliberadamente solo enlaces internos - no hace
solicitudes HTTP para verificar URLs externas, lo cual corresponde a una
herramienta dedicada de verificación de enlaces ejecutada como su propio
trabajo.

## `stats`

Un informe de resumen de solo lectura de un `site/` ya construido -
ejecuta `build` primero. Reporta:

- **Páginas y palabras** - conteo total de páginas y un conteo aproximado
  de palabras (etiquetas eliminadas, el mismo estándar de "suficientemente
  bueno para una estimación" que usa la propia cifra de tiempo de lectura
  del blog), más un desglose por árbol en cuanto hay más de uno (una
  versión, o un idioma no predeterminado).
- **Versiones e idiomas** - nombres de cada carpeta `docs/versions/`/
  `docs/i18n/` no predeterminada.
- **Blog** - conteos de entradas/categorías/autores/años activos, tomados
  directamente de la propia forma de carpeta de `site/blog/` (así que
  siempre coincide con lo realmente publicado, borradores excluidos) -
  `none` cuando no hay blog.
- **Etiquetas** - el número de etiquetas distintas en todo el sitio.
- **Índice de búsqueda** - conteo de entradas y tamaño de archivo de
  `search-index.json`, o `none` cuando la búsqueda está desactivada o hay
  un proveedor no local activo.
- **Salida del sitio** - conteo total de archivos y tamaño en disco del
  `site/` construido.

```bash
bxSites build
bxSites stats
```

Siempre sale con `0` - puramente informativo, nada aquí es una
compuerta de aprobado/fallido (ese es el trabajo de `site:check`).

## `doctor`

Una verificación puntual de salud de entorno/configuración - el verbo
"ejecuta esto antes de reportar un error". Verifica la versión de la JVM,
que `docs/` exista, que `bxsites.yaml`/`.json` realmente analice y valide,
que los módulos de BoxLang requeridos (`bx-markdown`, `bx-esapi`,
`bx-yaml`, `bx-image`) estén instalados y activados, y - si existe una
sobrescritura `theme/` a nivel de proyecto - que satisfaga el contrato de
los dos archivos obligatorios `layout.bxm`/`page.bxm`.

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Sale con `1` si alguna verificación falla, `0` en caso contrario. Nada
aquí modifica un proyecto - puramente diagnóstico.

## `post:new`

Genera una nueva entrada de blog en `docs/blog/posts/<slug>.md`.

```bash title="Uso"
bxSites post:new --title="My New Post" [--slug=...] [--date=...] [--authors=...] [--categories=...] [--tags=...] [--draft]
```

- `--title` (obligatorio) - también se convierte en el `title` del frontmatter de la entrada
- `--slug` - por defecto, un `--title` convertido en slug
- `--date` - por defecto, la fecha de hoy (`yyyy-MM-dd`)
- `--authors`, `--categories`, `--tags` - separados por comas
- `--draft` - por defecto `true` (pasa `--!draft` para publicarlo de inmediato)

Consulta [Blog](guides/blog.md) para la referencia completa del frontmatter.

## `version:new`

Crea una instantánea del árbol `docs/` actual en `docs/versions/<name>/`,
excluyendo `assets/`, `versions/`, `i18n/` y `blog/` (cada uno es su
propio árbol cargado por separado, no parte de la instantánea).

```bash title="Uso"
bxSites version:new --name=1.0
```

- `--name` (obligatorio) - la carpeta/etiqueta de la versión, por ejemplo `1.0`

Consulta [la sección "Versionado" de Configuración](configuration.md#versionado).

## `i18n:status`

Reporta la cobertura de traducción por idioma - para cada idioma
configurado, cuántas páginas del árbol predeterminado existen (en la
misma ruta relativa) bajo `docs/i18n/<code>/`, y cuáles todavía faltan.

```bash frame="terminal" title="Terminal"
bxSites i18n:status
```

Siempre sale con `0` - puramente informativo.

## `i18n:new`

Genera una nueva carpeta de idioma `docs/i18n/<code>/`, sembrando un
`index.md` copiado del propio `index.md` del idioma predeterminado
cuando existe uno.

```bash title="Uso"
bxSites i18n:new --code=es
```

- `--code` (obligatorio) - el código del idioma, por ejemplo `es`, `fr`, `pt-BR`

Consulta [Internacionalización](guides/i18n.md) para conectar el nuevo
idioma en el `i18n.locales` de `bxsites.yaml`.

## `page:new`

Genera una única página de documentación en una ruta arbitraria bajo
`docs/`, con el frontmatter solicitado ya completado.

```bash title="Uso"
bxSites page:new --path=guides/setup.md [--title=...] [--description=...] [--icon=...] [--tags=...] [--order=...]
```

- `--path` (obligatorio) - relativo a `docs/`, debe terminar en `.md`
- `--title`, `--description`, `--icon`, `--order` - escritos en el frontmatter
- `--tags` - separadas por comas

## `plugin:new`

Genera el esqueleto de un módulo de plugin (`box.json`,
`ModuleConfig.bx`, un `models/BxSitesPlugin.bx` con cada hook ya
esbozado) siguiendo el patrón de `examples/hello-plugin/`.

```bash title="Uso"
bxSites plugin:new --name=my-analytics-plugin [--dest=...]
```

- `--name` (obligatorio) - el nombre/slug del módulo del plugin
- `--dest` - por defecto, `<projectRoot>/<name>`

Consulta [Plugins](guides/plugins.md) para la referencia de hooks y cómo
conectar el plugin terminado en el array `plugins` de `bxsites.yaml`.

## `install:plugin`

Descarga un plugin publicado desde ForgeBox y lo coloca directamente en
el propio `boxlang_modules/` del proyecto - la propia convención de
módulos locales autocargados de BoxLang, de modo que no hace falta nada
más allá del propio binario `bxSites` (no interviene `box`/CommandBox).

```bash title="Uso"
bxSites install:plugin --name=bx-sites-plugin-analytics [--version=1.2.0]
```

- `--name` (obligatorio) - el slug de ForgeBox a instalar
- `--version` - una versión específica; omite para la más reciente

Imprime de vuelta el nombre de mapeo real del módulo registrado en cuanto
se carga - añade ese nombre al array `plugins` de `bxsites.yaml` para
activarlo (instalar por sí solo nunca activa un plugin - consulta
[Plugins](guides/plugins.md)).

## `theme:new`

Extrae uno de los temas incorporados a la propia carpeta `theme/` del
proyecto para personalizarlo, siguiendo el flujo de trabajo de extracción
`--theme` de mkdocs.

```bash title="Uso"
bxSites theme:new --theme=material
```

- `--theme` (obligatorio) - `bootstrap`, `material`, `tailwind`, `docsy`, `slate`, `docusaurus`, `justthedocs`, `vuepress`, `gitbook` o `notion` - consulta [Temas](guides/themes.md#incorporados)

Falla en lugar de sobrescribir un `theme/` existente. Consulta
[Temas](guides/themes.md) para el contrato de sobrescritura (`layout.bxm`
+ `page.bxm`).

## `install:theme`

Descarga un tema publicado desde ForgeBox al propio `themes/<name>/` del
proyecto - nada más que el binario `bxSites` necesario, igual que
`install:plugin`.

```bash title="Uso"
bxSites install:theme --name=bx-sites-theme-blog1 [--version=1.0.0]
```

- `--name` (obligatorio) - el slug de ForgeBox a instalar
- `--version` - una versión específica; omite para la más reciente

Valida el paquete descargado contra el contrato `ThemeProvider`
(`layout.bxm` + `page.bxm`) antes de terminar, así que un paquete roto
falla en el momento de la instalación en lugar de en la siguiente
`build`. Configura el `theme.name` de `bxsites.yaml` con el nombre
instalado para usarlo - consulta
[Temas](guides/themes.md#instalar-un-tema-publicado).

## `theme:import`

Conversión de mejor esfuerzo de un tema del ecosistema de otro generador
de sitios estáticos (`mkdocs`/`jekyll`/`hugo`) en un scaffold de tema
bx-sites bajo `themes/<name>/` - un punto de partida, no una migración
sin pérdidas en un solo comando.

```bash title="Uso"
bxSites theme:import --source=mkdocs --path=/path/to/theme --name=my-imported-theme
```

- `--source` (obligatorio) - `mkdocs`, `jekyll` o `hugo`
- `--path` (obligatorio) - la propia carpeta raíz del tema de origen
- `--name` (obligatorio) - el nombre de destino, escrito en `themes/<name>/`

Seguro de volver a ejecutar contra el mismo `--name` - `layout.bxm`/
`page.bxm` se sobrescriben y cualquier carpeta de recursos recién
encontrada se combina. Consulta
[Importar un tema](guides/theme-import.md) para exactamente qué se
traduce y qué no, y qué revisar después.

## `page:rename`

Mueve una página de documentación de una ruta a otra, reescribiendo cada
enlace Markdown relativo en todo `docs/**` que apuntaba a la ruta
antigua - el mismo problema de enlaces rotos que ya resuelve el lado HTML
construido (`site:check`), aplicado al código fuente Markdown en bruto en el
momento de renombrar en su lugar.

```bash title="Uso"
bxSites page:rename --from=guides/old-name.md --to=guides/new-name.md
```

- `--from` (obligatorio) - la ruta actual de la página, relativa a `docs/`
- `--to` (obligatorio) - su nueva ruta, relativa a `docs/`

Solo se reescriben los enlaces simples de estilo
`[text](relative/path.md)` - las URLs absolutas, `mailto:` y los anclajes
puros dentro de la página se dejan intactos. `docs/assets/**` nunca se
analiza.

También estampa el propio `redirect_from` del frontmatter de la página
movida con su URL antigua, de modo que una construcción
([Redirecciones](guides/redirects.md)) siga respondiendo por ella en
lugar de dejar que el renombrado devuelva un 404 a cada enlace externo
cuya fuente este proyecto no controla.

## `blog:drafts`

Lista cada entrada de blog cuyo frontmatter establece `draft: true` -
`build` siempre omite los borradores, así que este es el único lugar
donde su existencia se muestra.

```bash frame="terminal" title="Terminal"
bxSites blog:drafts
```

Siempre sale con `0`.

## `blog:find`

Filtra entradas de blog por autor/categoría/etiqueta/rango de fechas, sin
ejecutar una `build` completa.

```bash title="Uso"
bxSites blog:find [--author=...] [--category=...] [--tag=...] [--since=...] [--until=...] [--drafts]
```

- `--author`, `--category`, `--tag` - coincidencia exacta sin distinguir mayúsculas/minúsculas contra cualquiera de los propios valores de la entrada
- `--since`, `--until` - una fecha; solo coinciden entradas en o después de `--since` y/o en o antes de `--until`
- `--drafts` - incluye también entradas en borrador (excluidas por defecto)

Cada filtro es opcional e independiente - no pasar ninguno lista cada
entrada publicada.

## `search:query`

Ejecuta una consulta de palabras clave contra un `site/search-index.json`
ya construido - ejecuta `build` o `search-index` primero. Clasifica los
resultados usando la misma ponderación relativa de campos que usa el
propio widget de búsqueda del lado del cliente (título, luego etiquetas,
luego encabezados, luego cuerpo), así que puedes verificar qué
mostraría la búsqueda de un visitante real sin abrir un navegador.

```bash title="Uso"
bxSites search:query --query="getting started" [--limit=10]
```

- `--query` (obligatorio) - términos de búsqueda separados por espacios
- `--limit` - resultados máximos a devolver, por defecto `10`

## `lint`

Un paso de calidad de contenido previo a la construcción sobre el código
fuente Markdown en bruto de `docs/`, distinto de `site:check` (que solo
inspecciona un `site/` ya construido). Verifica:

- **Saltos de nivel de encabezado** - un cuerpo de página que salta
  directamente de `##` a `####` sin ningún `###` en medio (estructura
  confusa, y mala para la accesibilidad). Las líneas dentro de un bloque
  de código con fence nunca se confunden con encabezados.
- **Problemas de fecha en entradas de blog** - una entrada de
  `docs/blog/posts/**` con una `date` de frontmatter faltante o inválida
  (el propio `build` lanza un error por esto en el momento en que carga
  las entradas - `lint` lo muestra como un hallazgo en su lugar).

```bash frame="terminal" title="Terminal"
bxSites lint
```

Sale con `1` cuando cualquiera de las dos verificaciones encuentra algo,
`0` en caso contrario.
