---
title: Servidor MCP
order: 6.4
icon: phosphor-duotone:plugs-connected
summary: Activa mcp para escribir un site/mcp-index.json completo y sin truncar en cada build, para que bxSites Cloud pueda exponer tu sitio publicado a través de un servidor MCP público de solo lectura para agentes de IA - independiente de search/searchProvider.
tags: [guías, mcp, ai]
---

# Servidor MCP

Activa `mcp: true` en `bxsites.yaml` y cada `build` escribirá
`site/mcp-index.json` junto a tus páginas renderizadas - una copia
completa y legible por máquinas del contenido de tu sitio que
[bxSites Cloud](https://bxsites.io/cloud) lee para exponer un servidor
[MCP](https://modelcontextprotocol.io/) público y de solo lectura para
tu sitio publicado, la misma idea que
["MCP servers for published docs" de GitBook](https://gitbook.com/docs/ai-for-your-readers/mcp-servers-for-published-docs).
Una vez publicado, los agentes y asistentes de IA (Claude, ChatGPT y
cualquier otro cliente compatible con MCP) pueden buscar y recuperar el
contenido de tu sitio directamente, en lugar de raspar el HTML
renderizado.

BxSites en sí solo produce el archivo `mcp-index.json` descrito en esta
página - servirlo realmente como un servidor MCP en red es tarea de
bxSites Cloud, no algo que este módulo haga por sí solo.

## Activarlo

=== "YAML"
    ```yaml title="bxsites.yaml"
    mcp: true
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "mcp": true }
    ```

`false` (predeterminado) omite todo el paso - no se escribe ningún
`mcp-index.json`, y `build` no incurre en ningún costo adicional más
allá de comprobar el indicador.

## Independiente de `search`

`mcp` no tiene nada que ver con [`search`/`searchProvider`](search.md) -
son dos interruptores independientes:

- `mcp-index.json` se genera sin importar si `search` es `true` o
  `false`, y sin importar `searchProvider.provider` (`"local"`,
  `"algolia"`, `"pagefind"` o uno personalizado).
- Activar `mcp` nunca cambia nada del cuadro de búsqueda propio de tu
  sitio de cara al visitante, y desactivar `search` nunca deshabilita
  `mcp-index.json`.

Por esto `mcp-index.json` no puede simplemente reutilizar
`search-index.json`: ese archivo solo se genera para el proveedor de
búsqueda `"local"` (un sitio respaldado por Algolia o Pagefind no
produce ningún `search-index.json`, ya que cada uno mantiene su propio
índice en otro lugar), y su campo `body` está deliberadamente truncado -
`search-index.json` se envía al navegador de cada visitante para el
cuadro de búsqueda en la página, así que mantenerlo pequeño importa.
Ninguna de estas restricciones aplica a `mcp-index.json`: se obtiene
desde el servidor por bxSites Cloud, no se envía a los visitantes, y un
cuerpo truncado arriesga que un agente de IA dé una respuesta
incompleta o incorrecta.

## El formato de `mcp-index.json`

Una entrada por cada página no oculta (la misma convención "las páginas
ocultas se excluyen" que usan `search-index.json` y la navegación):

```json title="site/mcp-index.json"
[
  {
    "title": "Getting Started",
    "url": "getting-started/index.html",
    "tags": ["guides"],
    "headings": ["Getting Started", "Installation", "Next steps"],
    "body": "Getting Started Installation Run bxSites new to scaffold a project... Next steps ..."
  }
]
```

| Campo | Descripción |
|---|---|
| `title` | El título de la página (frontmatter `title`, igual que en `search-index.json`) |
| `url` | La ruta URL de la página, relativa a la raíz del sitio |
| `tags` | El array `tags` del frontmatter de la página |
| `headings` | El texto plano de cada encabezado `h1`-`h6` de la página, en el orden del documento |
| `body` | El contenido de texto plano **completo** de la página, sin etiquetas HTML - nunca truncado |

La única diferencia respecto a la forma de las entradas de
`search-index.json` es `body`: allí se recorta a 400 caracteres con
puntos suspensivos al final; aquí es la página completa.

## Sitios con múltiples versiones e idiomas

Igual que `search-index.json`, `mcp-index.json` se escribe una vez por
cada árbol renderizado - el sitio principal, `/next/` (cuando
`versions.default` está definido), cada árbol `/versions/<name>/` y
cada sub-árbol de idioma -, de modo que cada árbol publicado obtiene su
propio `mcp-index.json` que cubre solo las páginas de ese árbol.
