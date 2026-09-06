---
title: Primeros Pasos
order: 2
icon: phosphor-duotone:rocket-launch
summary: Instala el módulo, crea un proyecto y construye tu primer sitio.
tags: [guías, configuración]
---

# Primeros Pasos

## Requisito previo: instalar BoxLang

Todo lo que sigue asume que el runtime de BoxLang ya está instalado en tu
máquina - `install-bx-module` es el propio CLI de BoxLang instalando un
módulo dentro de una instalación existente, y `box install` de CommandBox
también necesita un motor de BoxLang ya presente contra el cual ejecutarse.
Si aún no tienes BoxLang, instálalo primero con cualquiera de estas dos
opciones:

- el **instalador rápido** (una sola versión, la forma más simple de
  empezar):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install.boxlang.io/ | bash
  ```

- o **BVM**, el BoxLang Version Manager (instala varias versiones de
  BoxLang en paralelo y te permite alternar entre ellas):

  ```bash frame="terminal" title="Terminal"
  curl -fsSL https://install-bvm.boxlang.io/ | bash
  bvm install latest && bvm use latest
  ```

Los instaladores para Windows y Homebrew, además de la referencia completa
de comandos de BVM, están cubiertos en la
[documentación de instalación propia de BoxLang](https://boxlang.ortusbooks.com/getting-started/installation).

## Instalación

BxSites depende de [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
para el renderizado de Markdown, [bx-esapi](https://github.com/ortus-boxlang/bx-esapi)
para la codificación HTML, [bx-yaml](https://github.com/ortus-boxlang/bx-yaml)
para leer `bxsites.yaml`, y [bx-image](https://github.com/ortus-boxlang/bx-image)
para el pipeline de imágenes responsivas (consulta
[Imágenes Responsivas](guides/images.md)) - los cuatro se instalan
automáticamente como dependencias de `box.json`, así que instalar
`bx-sites` en sí mismo es el único comando necesario, ya sea mediante el
propio instalador de binario del sistema operativo de BoxLang:

```bash frame="terminal" title="Terminal"
install-bx-module bx-sites
```

o mediante [CommandBox](https://commandbox.ortusbooks.com/):

```bash frame="terminal" title="Terminal"
box install bx-sites
```

Cualquiera de los dos lee `boxlang.executable` de `box.json` y coloca un
script `bxSites` en tu `PATH` (en `~/.boxlang/bin`), de modo que cada
comando de abajo funciona tanto en su forma corta e independiente:

```bash title="Uso"
bxSites <verb> [options]
```

como, en cualquier lugar donde BoxLang esté disponible pero ese atajo del
`PATH` no lo esté (un ejecutor de CI, un módulo registrado a mano en lugar
de instalado) - ambas formas ejecutan exactamente lo mismo:

```bash title="Uso (sin el atajo del PATH)"
boxlang bxSites <verb> [options]
```

El resto de esta guía usa la forma corta.

## Crear la estructura de un proyecto

```bash frame="terminal" title="Terminal" linenums="1"
bxSites new my-docs
cd my-docs
```

Esto crea:

```text title="Estructura del proyecto"
my-docs/
├── docs/
│   ├── assets/
│   └── index.md
└── bxsites.yaml
```

Pasa `--theme=material` o `--theme=tailwind` para generar la estructura
con un tema predeterminado diferente, y `--name="My Project Docs"` para
establecer el nombre del sitio de antemano - de lo contrario `new` lo
deriva del nombre del directorio de destino.

### Formato del archivo de configuración

`bxsites.yaml` es el formato por defecto y preferido - es lo que `new`
genera a menos que se indique lo contrario, y cada ejemplo de esta guía y
de [Configuración](configuration.md) lo muestra primero. `bxsites.json`
también es totalmente compatible, para un proyecto que lo prefiera: pasa
`--format=json` para generar uno en su lugar, o simplemente escríbelo o
renómbralo tú mismo - ConfigLoader resuelve el que esté realmente
presente entre `bxsites.yaml`/`.yml`/`.json`, en ese orden, sin necesitar
ninguna otra configuración para cambiar. Consulta
[Configuración](configuration.md) para la referencia completa de claves
en ambos formatos.

¿Ya tienes contenido en GitBook? `bxSites migrate --source=/path/to/export`
convierte una exportación de GitBook directamente en `docs/` - consulta
[Migrar desde GitBook](guides/migrating-from-gitbook.md) - y puedes pasar
directamente a [Construcción](#construcción).

## Añadir páginas

Cada archivo `.md` bajo `docs/` se convierte en una página. El anidamiento
de carpetas se convierte automáticamente en anidamiento de navegación:

!!! note "docs/ o src/"
    `docs/` es lo que genera `new` y lo que usa cada ejemplo de esta guía,
    pero un proyecto que en espíritu no es realmente "docs" (un sitio de
    marketing, un portafolio) puede usar `src/` en su lugar, sin ningún
    otro cambio: cada verbo (`build`, `serve`, `audit`, `lint`,
    `page:new`, ...) busca primero `docs/` y recurre a `src/` cuando es
    lo que realmente existe. El resultado de la construcción siempre
    termina en `site/` de cualquier forma - los dos nunca chocan, ya que
    `site/` nunca es en sí mismo un nombre válido de carpeta de origen.

```text title="docs/ → nav"
docs/
├── index.md              -> /
├── guides/
│   ├── index.md          -> /guides/
│   └── deployment.md     -> /guides/deployment/
```

(Un sitio grande puede sobrescribir por completo este orden/agrupamiento
inferido con una navegación explícita - consulta [`nav`](configuration.md#nav)).

### Enlazar entre páginas

Enlaza a otra página de la manera habitual de mkdocs - una ruta relativa
al archivo hasta su fuente `.md`, exactamente como si los dos archivos
estuvieran uno junto al otro en el disco (porque lo están):

```markdown title="Ejemplo de enlace"
Consulta [Despliegue](guides/deployment.md) o, desde esa misma guía,
[volver a Primeros Pasos](../getting-started.md#añadir-páginas).
```

BxSites reescribe cada enlace de este tipo a su URL amigable generada en
el momento de la construcción (`guides/deployment.md` ->
`/guides/deployment/index.html`, conservando anclas y cadenas de consulta),
resuelto respecto a la propia carpeta de la página *que enlaza* - `../` y
las referencias a archivos hermanos funcionan exactamente igual que al
resolver cualquier otra ruta relativa. Esta es también la razón por la
que el enlace sigue funcionando si lees el archivo directamente en GitHub
en lugar del sitio construido: es una ruta relativa real y válida a un
archivo real de cualquiera de las dos formas. Las URL absolutas, los
enlaces `mailto:` y los enlaces que ya empiezan con `/` se dejan intactos.

### Descargar una página como Markdown

Cada página construida también publica su propia fuente `.md` original
junto a ella - `docs/guides/deployment.md` termina copiado en
`site/guides/deployment.md`, justo al lado de
`site/guides/deployment/index.html` - con un enlace "Download Markdown" en
la propia página, junto a "Edit this page". No requiere configuración,
siempre está activo.

Esta es la misma motivación que [`llms.txt`](configuration.md#llmstxt) -
una persona (o un LLM) puede obtener el Markdown en bruto de una página
directamente en lugar de raspar el HTML renderizado - y dado que todo el
árbol `docs/` se refleja 1:1, los enlaces relativos propios de una página
también siguen funcionando leídos de esta manera.

Cada página puede comenzar con un pequeño bloque de frontmatter:

```markdown title="docs/guides/deployment.md" linenums="1"
---
title: Deployment
order: 2
hidden: false
description: How to deploy a built BxSites site.
tags: [guides, deployment]
icon: 🚀
summary: Everything you need to publish a built site.
ogImage: assets/deployment-card.png
toc: true
---

# Deployment

Your content here.
```

- `title` - sobrescribe el título de navegación/página (que de lo
  contrario se deriva del nombre del archivo)
- `order` - controla el orden entre páginas hermanas en la navegación (un
  número más bajo aparece primero; las páginas omitidas se ordenan al
  final, alfabéticamente)
- `hidden` - `true` excluye la página de la navegación (y de la búsqueda)
  sin excluirla de la construcción
- `description` - la descripción de tarjeta social/meta de esta página
  (consulta [`ogImage`](configuration.md#ogimage)); recurre a la
  `description` general del sitio en la configuración del sitio cuando se
  omite
- `tags` - un array de etiquetas para esta página, renderizadas como
  insignias clicables debajo del título y recopiladas en una página de
  índice `/tags/` de todo el sitio (solo se construye una vez que al
  menos una página tenga etiquetas); también aumenta la relevancia en la
  búsqueda para las consultas coincidentes
- `icon` - se muestra junto al título de la página y junto a su entrada en
  la navegación - un emoji sencillo, o un icono con nombre de una
  biblioteca incluida (`rocket`, `lucide:rocket`, `tabler:rocket`, o el
  `custom:my-icon` propio de un proyecto) - consulta
  [Iconos](guides/icons.md)
- `summary` - una frase introductoria de una línea mostrada debajo del
  título (distinta de `description`, que es solo para la etiqueta meta y
  nunca se renderiza en la propia página)
- `ogImage` - sobrescribe la imagen de tarjeta social de esta página en
  particular - consulta [`ogImage`](configuration.md#ogimage)
- `toc` - `false` oculta la propia tabla de contenido "En esta página" de
  esta página, incluso con 2 o más encabezados (el disparador habitual
  para que se renderice) - útil para una página de aterrizaje/hero que no
  quiere una TOC flotante compitiendo con su propio contenido; por
  defecto `true`

Los valores del frontmatter pueden ser listas en línea (`tags: [a, b, c]`),
listas de bloque al estilo YAML (`tags:` seguido de líneas `- item`
sangradas), o escalares de bloque con `>`/`|` para un valor de varias
líneas - aunque es un analizador pequeño escrito a mano, no YAML completo,
así que los objetos/mapas anidados no son compatibles.

## Construcción

```bash frame="terminal" title="Terminal"
bxSites build
```

Renderiza cada página de `docs/` en un sitio estático en `site/`, listo
para alojarse en cualquier lugar que sirva archivos estáticos.

## Servir localmente

```bash frame="terminal" title="Terminal"
bxSites serve
```

Construye el proyecto, sirve `site/` en `http://127.0.0.1:8080/`, y
reconstruye automáticamente cada vez que guardas un cambio bajo `docs/`,
tu configuración de sitio `bxsites.yaml`/`.json`, o una sobrescritura de
`theme/` a nivel de proyecto - tu navegador se recarga por sí solo. Pasa
`--port=3000` o `--host=0.0.0.0` para cambiar cómo se enlaza.

## Limpieza

```bash frame="terminal" title="Terminal"
bxSites clean
```

Elimina `site/` y cualquier caché de construcción, sin tocar tu fuente
`docs/`.
