---
title: Tablas
order: 4.6
icon: phosphor-duotone:table
tags: [guides, markdown]
---

# Tablas

Tablas de pipes [GFM](https://github.github.com/gfm/#tables-extension-)
estándar, además de todo lo que hay en [Extensiones de
Markdown](markdown.md) - sin necesidad de configuración en
`bxsites.yaml`, siempre activas:

```markdown title="Example" linenums="1"
| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |
```

Lo que se renderiza como:

| Feature      | Community | Enterprise |
| ------------ | :-------: | ---------: |
| Themes       |    10     |         10 |
| Multi-locale |    Yes    |        Yes |
| Support      |  Forums   |     24/7   |

Una fila de `---` bajo el encabezado activa la tabla; pon dos puntos en
esa fila separadora para controlar la alineación por columna - `:---`
izquierda, `:---:` centro, `---:` derecha (sin ningún dos puntos, cada
columna usa alineación izquierda por defecto).

## El contenido de las celdas es markdown inline normal

`code`, **negrita**, *cursiva*, y [enlaces](../index.md) funcionan todos
dentro de una celda exactamente igual que en cualquier otra parte de la
página:

```markdown title="Example" linenums="1"
| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |
```

Lo que se renderiza como:

| Setting | Value |
| --- | --- |
| Default theme | `bootstrap` |
| Docs | [Themes guide](themes.md) |
| Status | **Stable** |

## Escapar un pipe dentro de una celda

Un `|` literal dentro del propio texto plano de una celda necesita una
barra invertida, `\|` - uno sin escapar se interpreta como el separador
de la siguiente columna en su lugar:

```markdown title="Example" linenums="1"
| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |
```

Lo que se renderiza como:

| Expression | Meaning |
| --- | --- |
| a \| b | bitwise OR |

Un `|` dentro de código en línea no necesita ningún escape en absoluto -
el span de código (`` `a | b` ``) ya lo protege:

| Expression | Meaning |
| --- | --- |
| `a | b` | bitwise OR |

## Filas cortas y largas

Una fila de datos no tiene que coincidir exactamente con el número de
columnas del encabezado - una fila corta se rellena con celdas vacías, y
una fila larga tiene sus celdas adicionales descartadas silenciosamente,
ambos comportamientos controlados por
`tableOptions.appendMissingColumns`/`discardExtraColumns` más abajo:

```markdown title="Example" linenums="1"
| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |
```

Lo que se renderiza como:

| One | Two | Three |
| --- | --- | --- |
| a | b |
| c | d | e | f |

## Configurar el análisis

El manejo de filas cortas/largas, la propia rigurosidad de la fila
separadora `---`, y la clase CSS con la que se renderiza cada `<table>`
están todos controlados por
[`markdown.tableOptions`](../configuration.md#markdown) de
`bxsites.yaml`; los valores por defecto que se muestran a lo largo de
esta página son casi siempre lo que quieres.

## Desplazamiento responsivo y un encabezado fijo

Toda tabla renderizada se envuelve automáticamente en un `div`
`.bxsites-table-wrap` - sin necesidad de configuración en
`bxsites.yaml`, sin markdown adicional. Esto le da a una tabla ancha su
propia barra de desplazamiento horizontal en lugar de desbordar la
página, y limita las tablas altas (más allá de un `max-height`) a una
altura fija con su propia barra de desplazamiento vertical, con la fila
de encabezado fijada en su sitio mientras el cuerpo se desplaza por
debajo - una tabla corta como las de arriba nunca llega a tener barra de
desplazamiento alguna, ya que ya cabe sin problema. Una sobrescritura
personalizada en `theme/` puede modificar el estilo de
`.bxsites-table-wrap` (su `max-height`, en particular) igual que
cualquier otra clase CSS.

## Tematización

Todo tema incorporado renderiza una tabla como una tarjeta autónoma - un
wrapper redondeado y con borde, una franja de encabezado con tinte,
separadores horizontales de fila sin líneas verticales de rejilla y un
tinte de cebra/hover en las filas - y pinta todo eso a partir de seis
propiedades personalizadas de CSS, declaradas por modo (`:root` y
`[data-theme="dark"]`) en el propio `assets/style.css` del tema:

| Token | Qué pinta |
| --- | --- |
| `--bxsites-table-bg` | La superficie de la tarjeta, detrás de cada fila |
| `--bxsites-table-head-bg` | La fila de encabezado y el campo de filtro sobre ella |
| `--bxsites-table-head-text` | El texto de las etiquetas del encabezado |
| `--bxsites-table-border` | El contorno de la tarjeta y los separadores de fila |
| `--bxsites-table-stripe-bg` | Las filas pares - un tinte alfa sobre la superficie de la tarjeta |
| `--bxsites-table-hover-bg` | La fila bajo el cursor - el mismo, algo más intenso |

Como todas están declaradas en ambos modos, una tabla sigue al
conmutador de tema igual que el resto de la página, en lugar de dejar una
losa blanca de modo claro sobre una página oscura. Puedes redirigir
cualquiera de ellas desde
[`extraCss`](themes.md#personalizar-colores-sin-sobrescribir-un-tema) - sin
necesidad de un override de tema:

```css title="docs/assets/brand.css" linenums="1"
[data-theme="dark"] {
	--bxsites-table-head-bg: #241b2e;
	--bxsites-table-hover-bg: rgba(167, 139, 250, 0.12);
}
```

Mantén `--bxsites-table-stripe-bg`/`-hover-bg` como colores alfa: se
pintan encima de lo que ponga `--bxsites-table-bg`, así que un valor
opaco cubre la superficie de la tarjeta en vez de teñirla. Todo lo que va
más allá del color - el padding, el radio de las esquinas, las etiquetas
en mayúsculas del encabezado - es un override real de `theme/`, igual que
cualquier otro CSS de un tema.

## Más allá de los datos simples

Dos recetas más se construyen directamente encima de una tabla simple
como las de arriba:

- ¿Necesitas una insignia de estado o una valoración con estrellas en una
  celda? Consulta [Recetas de
  visualización](variables-and-functions.md#recetas-de-visualización).
- ¿Necesitas que un lector realmente pueda ordenar o filtrar una tabla
  del lado del cliente, en lugar de solo leerla? Consulta [Una tabla
  ordenable y filtrable](interactivity.md#una-tabla-ordenable-y-filtrable).
