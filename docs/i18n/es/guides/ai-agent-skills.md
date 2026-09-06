---
title: Skills de Agentes de IA
order: 6.3
icon: phosphor-duotone:robot
summary: Dale a Claude Code, Cursor, Codex y otros asistentes de codificación con IA un conocimiento profundo y estructurado de bx-sites - instala el paquete oficial de skills mediante npx, la CLI de ColdBox, o el propio verbo skills:install de bxSites.
tags: [guías, ai, skills]
---

# Skills de Agentes de IA

Un **[Agent Skill](https://code.claude.com/docs/en/skills)** es un archivo
`SKILL.md` pequeño y autocontenido que enseña a un asistente de codificación
con IA a hacer bien una cosa concreta - el asistente lo carga
automáticamente, cuando lo necesita, siempre que una tarea coincida con lo
que describe el skill. En lugar de volver a explicarle a tu asistente las
propias convenciones de bx-sites en cada conversación (cómo funcionan los
bloques `::: card :::`, qué espera la clave `redirects` de `bxsites.yaml`,
en qué se diferencia `page:new` de un archivo escrito a mano), un skill le
entrega ese conocimiento de antemano, escrito tal como funciona bx-sites
mismo.

[`ortus-boxlang/bx-sites-skills`](https://github.com/ortus-boxlang/bx-sites-skills)
es el paquete oficial de skills para este proyecto - trece skills que
cubren desde el andamiaje de un proyecto nuevo hasta la resolución de
problemas de los propios GitHub Actions de este repositorio. Funcionan con
cualquier asistente que admita el formato Agent Skills (Claude Code,
Cursor, Codex y otros).

## Instalación

Tres formas de instalar el paquete - elige la que mejor se adapte a tu
flujo de trabajo:

### `npx skills add`

La [CLI `skills`](https://github.com/skillslib/skills) funciona con
cualquier proyecto, independientemente del lenguaje o entorno de
ejecución, y no necesita nada más que Node.js:

```bash title="Instalar todos los skills"
npx skills add ortus-boxlang/bx-sites-skills
```

```bash title="No interactivo (CI, scripts)"
npx -y skills add ortus-boxlang/bx-sites-skills -y
```

Instala un solo skill en lugar de todo el conjunto apuntando directamente
a él:

```bash title="Instalar solo un skill"
npx skills add ortus-boxlang/bx-sites-skills/skills/bx-sites-deployment
```

### `coldbox ai skills install`

Si ya tienes la CLI de ColdBox, puede instalar directamente desde la misma
fuente de GitHub - consulta el
[BoxLang Skills Directory](https://skills.boxlang.io/):

```bash title="Instalar un solo skill"
coldbox ai skills install ortus-boxlang/bx-sites-skills/bx-sites-deployment
```

### `bxSites skills:install`

bx-sites también trae su propio verbo de un solo paso - un envoltorio
delgado sobre `npx skills add` que instala todo el paquete directamente en
el proyecto actual, para que el asistente de IA de un proyecto recién
andamiado conozca bx-sites desde el primer prompt:

```bash title="Uso"
bxSites skills install
# o, equivalentemente:
bxSites skills:install
```

```bash title="Instalar solo un skill"
bxSites skills:install --skill=bx-sites-deployment
```

Requiere Node.js/`npx` en el `PATH` (el mismo requisito que tiene por su
cuenta `npx skills add`) - consulta la
[Referencia de la CLI](../cli-reference.md#skillsinstall) para la
referencia completa de flags.

## Skills disponibles

Cada skill es un único `SKILL.md` autocontenido (sin archivos de recursos
adjuntos), por lo que se instala correctamente por cualquiera de las vías
anteriores:

| Skill | Descripción |
|---|---|
| `bx-sites-getting-started` | Instalar bx-sites, andamiar un proyecto nuevo (o migrar uno existente de GitBook/mkdocs/Notion), estructura del proyecto, frontmatter de página, enlaces, build/serve/clean. |
| `bx-sites-content-blocks` | Bloques de contenido `::: name :::` completos - tarjetas, columnas, stepper, botones, incrustaciones, tarjetas de enlace a página/vista previa, prompt, updates, includes, contenido condicional, widget de OpenAPI. |
| `bx-sites-markdown` | Admoniciones, notas al pie, listas de definición, pestañas de contenido, anotaciones de bloques de código, Mermaid, matemáticas, tablas, iconos, imágenes responsivas, interactividad con Alpine.js. |
| `bx-sites-variables-functions` | `{{ variables }}` reutilizables y funciones mágicas de BoxLang (`docs/functions.bxs`), incluidas recetas de visualizadores de insignias de estado/valoraciones/barras de progreso. |
| `bx-sites-blog-versioning-i18n` | El blog (`docs/blog/posts/`), la documentación versionada (`docs/versions/`), los locales traducidos (`docs/i18n/`) y las redirecciones. |
| `bx-sites-content-quality` | Comprobaciones de contenido previas al build sin necesidad de un build completo - `lint`, `blog:drafts`, `blog:find`, `search:query`. |
| `bx-sites-build` | `build`/`serve`/`clean`/`search-index`, más diagnósticos `doctor`/`stats`/`check` sobre un sitio ya construido. |
| `bx-sites-configuration` | La referencia completa de claves de `bxsites.yaml`/`bxsites.json` - `baseURL`, `nav`, `redirects`, `markdown`, assets y más. |
| `bx-sites-themes` | Elegir, personalizar, sobrescribir, instalar o escribir un tema; el contrato `ThemeProvider`. |
| `bx-sites-search` | Proveedores de búsqueda - local (MiniSearch), Algolia DocSearch, Pagefind, y cómo conectar un proveedor personalizado. |
| `bx-sites-plugins` | Escribir/instalar un plugin de bx-sites (hooks del ciclo de vida del build) o un proveedor de CLI (nuevos verbos de `bxSites`). |
| `bx-sites-deployment` | Destinos de despliegue (S3/Azure/GCS/Firebase/FTP/SFTP/rsync/Netlify/Vercel/Cloudflare Pages/GitHub Pages), `package`, y el flujo de publicación de GitHub Actions. |
| `bx-sites-actions` | Operar y resolver problemas de los propios flujos de GitHub Actions de bx-sites (tests, snapshot, release, docs, pages). |

## Prompts de ejemplo

Una vez instalado, basta con describir lo que quieres - tu asistente elige
el skill adecuado por su cuenta:

```text title="Andamiar un sitio nuevo"
Scaffold a new bx-sites project called "acme-docs" using the gitbook
theme, add a Getting Started page, and start the dev server.
```

```text title="Redactar contenido"
Add a three-step ::: stepper ::: block to docs/getting-started.md walking
through install, scaffold, and build - and a ::: cards ::: grid linking
to the three main guides.
```

```text title="Publicarlo"
Add a Netlify deploy target to bxsites.yaml and explain which
environment variable it expects for the auth token.
```

## Preguntas frecuentes

??? faq "¿Qué hay realmente dentro de un archivo SKILL.md?"
    Un breve bloque de frontmatter YAML (`name`, `description` - el
    disparador con el que compara un asistente) seguido de instrucciones,
    convenciones y ejemplos en Markdown simple para ese único tema. Sin
    scripts ni archivos de recursos adjuntos - cada skill de este paquete
    es un único archivo, por diseño, para que se instale de la misma forma
    con `npx skills add`, `coldbox ai skills install` y
    `bxSites skills:install`.

??? faq "¿Necesito los trece skills?"
    No - todas las vías de instalación admiten instalar un solo skill por
    nombre (ver [Instalación](#instalación) más arriba). Aun así, la
    mayoría de proyectos están bien instalando el paquete completo: un
    asistente solo carga el contenido de un skill cuando una tarea
    realmente coincide con él, así que los skills sin usar no cuestan nada
    en tiempo de prompt.

??? faq "¿Con qué asistentes de IA funciona esto?"
    Con cualquier asistente que admita el formato Agent Skills - Claude
    Code, Cursor, Codex y otros. `npx skills add`/`coldbox ai skills
    install` detectan qué asistente(s) tiene ya configurados tu proyecto e
    instalan en cada uno automáticamente.

??? faq "¿Cómo verifico que un skill realmente se instaló?"
    Comprueba si hay un nuevo `SKILL.md` en el propio directorio de skills
    de tu asistente (p. ej. `.claude/skills/bx-sites-getting-started/SKILL.md`
    para Claude Code) - o simplemente pregúntale a tu asistente algo que
    cubra el skill (p. ej. "¿cómo añado un destino de despliegue de
    Netlify?") y comprueba si su respuesta coincide con esta documentación.

??? faq "`bxSites skills:install` falló con un error sobre npx/Node.js"
    Por debajo, ejecuta el `npx skills add` real, así que necesita Node.js
    en el `PATH` igual que ese comando por su cuenta - instala Node.js
    desde [nodejs.org](https://nodejs.org/) e inténtalo de nuevo. O
    sáltate por completo el propio envoltorio de `bxSites` y ejecuta
    directamente `npx skills add ortus-boxlang/bx-sites-skills`.

??? faq "¿Puedo instalar esto en un proyecto que todavía no es un proyecto bx-sites?"
    Sí - `npx skills add`/`coldbox ai skills install` funcionan en
    cualquier directorio. `bxSites skills:install` necesita
    específicamente un `--projectRoot` existente (el directorio actual por
    defecto), igual que cualquier otro verbo de `bxSites`, pero ese
    proyecto no necesita estar construido todavía - instalar los skills
    antes de haber escrito una sola página es precisamente la idea, para
    que tu asistente ya conozca bx-sites desde el primerísimo prompt.

## Fuente

- Repositorio de skills: [ortus-boxlang/bx-sites-skills](https://github.com/ortus-boxlang/bx-sites-skills)
- Repositorio de bx-sites: [ortus-boxlang/bx-sites](https://github.com/ortus-boxlang/bx-sites)
- BoxLang Skills Directory: [skills.boxlang.io](https://skills.boxlang.io/)
