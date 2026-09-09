---
title: Redirecciones
order: 11
icon: phosphor-duotone:signpost
tags: [guías, redirecciones]
---

# Redirecciones

Mantén una URL antigua funcionando después de mover, renombrar o
reestructurar una página - se escribe un stub HTML estático en la ruta
antigua, para que una entrada de índice desactualizada de un buscador o
el marcador antiguo de alguien sigan aterrizando en la página correcta en
lugar de un 404. No interviene ninguna regla de reescritura del lado del
servidor (un host estático no tiene dónde ejecutar una) - el stub es
justo el HTML suficiente para que un navegador se redirija solo y un
rastreador conozca la URL canónica real.

## Por página: frontmatter `redirect_from`

Añade una o más rutas antiguas al propio frontmatter de una página:

```md title="docs/guides/new-setup.md"
---
title: New Setup
redirect_from:
  - guides/old-setup
  - setup
---
```

Cada entrada es un segmento de URL bonita - sin barra inicial/final, sin
extensión `.md`/`.html` - la misma forma que toma la propia URL de la
página. Una construcción escribe entonces un stub en cada una
(`site/guides/old-setup/index.html`, `site/setup/index.html` para el
ejemplo anterior), ambos redirigiendo a la URL real propia de esta
página.

`redirect_from` está limitado al árbol al que pertenece la propia página
- la propia página de una versión redirige dentro de esa versión
(`site/versions/2.0/old-path/`), la propia página traducida de un idioma
redirige dentro de ese idioma (`site/es/old-path/`), exactamente igual
que ya hace la propia URL real de la página. No hay nada extra que
configurar por árbol.

## Para todo el sitio: `bxsites.yaml` `redirects`

Para una URL antigua que nunca perteneció a una página específica - una
sección reestructurada, la ruta de un dominio antiguo, cualquier cosa que
no sea naturalmente el propio "nombre antiguo" de una sola página - lista
en su lugar un par explícito `from`/`to`:

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    redirects:
      - from: old-guide
        to: guides/new-guide/
      - from: moved-to-another-site
        to: https://example.com/docs
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"redirects": [
    		{ "from": "old-guide", "to": "guides/new-guide/" },
    		{ "from": "moved-to-another-site", "to": "https://example.com/docs" }
    	]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [[redirects]]
    from = "old-guide"
    to = "guides/new-guide/"

    [[redirects]]
    from = "moved-to-another-site"
    to = "https://example.com/docs"
    ```

- `from` - el segmento de URL bonita antiguo, la misma forma que
  `redirect_from` arriba
- `to` - una ruta relativa a la raíz (resuelta contra el propio `baseURL`
  del sitio, la misma convención que ya usan `theme.logo`/`ogImage`) o
  una URL `https://` completa, para redirigir completamente fuera del
  sitio

`redirects` solo se aplica siempre al árbol principal del sitio - un `to`
sin adornos es una ruta relativa a la raíz que solo es inequívoca en la
raíz del sitio. Un árbol de versión/idioma que quiera el mismo mapeo de
URL antigua necesita en su lugar su propio `redirect_from` a nivel de
página.

## `page:rename` lo hace por ti

Renombrar/mover una página con
[`page:rename`](../cli-reference.md#pagerename) añade automáticamente su
ruta antigua al propio `redirect_from` de la página movida - además de
reescribir cada enlace relativo de Markdown que apuntaba a ella, la
propia URL antigua sigue funcionando también:

```bash title="Uso"
bxSites page:rename --from=guides/old-setup.md --to=guides/new-setup.md
```

Renombrar una página más de una vez simplemente sigue añadiendo - la
lista `redirect_from` de una página puede llevar tantas rutas antiguas
como haya tenido a lo largo del tiempo.

## Conflictos

Una construcción falla por completo, en lugar de sobrescribir contenido
real silenciosamente, si:

- La propia ruta `from` de una redirección choca con una página real ya
  construida en esa ruta (`BxSites.RedirectConflict`)
- Dos redirecciones (entradas `redirect_from`, entradas de configuración
  `redirects`, o una de cada una) apuntan ambas a la misma ruta `from`

## Qué queda fuera de alcance (por ahora)

- **Las entradas de blog no obtienen `redirect_from`.** La clave de
  frontmatter solo se lee para páginas regulares de `docs/`, no para
  `docs/blog/posts/**` - una entrada de blog movida necesita en su lugar
  su propia entrada de configuración `redirects`.
- **Sin redirecciones de comodín/patrón.** Cada `from` es una ruta
  antigua exacta - no hay un comodín `guides/old/*`.
