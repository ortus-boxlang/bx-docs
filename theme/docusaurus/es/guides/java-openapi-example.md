---
title: Ejemplo de salida OpenAPI
order: 6.5
icon: phosphor-duotone:plug
tags: [guías, java, spring-boot, openapi, ejemplo]
---

# Ejemplo de salida OpenAPI

Un ejemplo real de lo que `bxSitesOpenApiDoc` (Gradle) /
`bxsites:openapi` (Maven) produce realmente - ver
[Plugin de Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin de Maven](maven-plugin.md#spring-boot-doc-generation) para saber
cómo activar esto en tu propio proyecto.

Apunta `specFile` a tu propia especificación OpenAPI generada por
springdoc, y el generador la copia en `assets/openapi/`, para luego
escribir una página con exactamente este frontmatter y este cuerpo:

```yaml title="Frontmatter generado"
---
title: "Bookshelf API"
tags: [api, openapi]
---
```

```markdown title="Cuerpo generado"
# Bookshelf API

::: openapi src="assets/openapi/openapi.yaml" title="Bookshelf API"
:::
```

Y aquí está, renderizado de verdad, en esta misma página, contra una
pequeña especificación "Bookshelf API" que este repositorio ya incluye en
[`assets/openapi/example.yaml`](../assets/openapi/example.yaml) - la
misma que demuestra [OpenAPI / Swagger](openapi.md):

---

# Bookshelf API

::: openapi src="assets/openapi/example.yaml" title="Bookshelf API"
:::

---

Eso no es una captura de pantalla ni una maqueta - es el widget de
Swagger UI interactivo real que incluye bx-sites, alimentado por el
archivo de especificación de arriba. Apuntado a la especificación de tu
propio proyecto, `bxSitesOpenApiDoc`/`bxsites:openapi` produce una salida
byte a byte idéntica.

## Lo que esto no cubre

Swagger UI se renderiza por completo en el cliente, así que el texto por
endpoint (como "List books") nunca llega al propio índice de búsqueda de
bx-sites - solo se indexan el título/frontmatter de esta página. Consulta
las guías de Gradle/Maven enlazadas arriba para el resto del alcance de
v1 (springdoc sigue teniendo que generar el archivo de especificación por
tu cuenta; `openapi: true` sigue teniendo que estar activado en
`bxsites.yaml`, ya sea a mano o mediante `autoPatchConfig`).
