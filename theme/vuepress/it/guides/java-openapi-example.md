---
title: Esempio di output OpenAPI
order: 6.5
icon: phosphor-duotone:plug
tags: [guide, java, spring-boot, openapi, esempio]
---

# Esempio di output OpenAPI

Un esempio reale di ciò che `bxSitesOpenApiDoc` (Gradle) /
`bxsites:openapi` (Maven) produce davvero - vedi
[Plugin Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin Maven](maven-plugin.md#spring-boot-doc-generation) per sapere come
attivarlo nel tuo progetto.

Punta `specFile` alla tua specifica OpenAPI generata da springdoc, e il
generatore la copia in `assets/openapi/`, poi scrive una pagina con
esattamente questo frontmatter e questo corpo:

```yaml title="Frontmatter generato"
---
title: "Bookshelf API"
tags: [api, openapi]
---
```

```markdown title="Corpo generato"
# Bookshelf API

::: openapi src="assets/openapi/openapi.yaml" title="Bookshelf API"
:::
```

Ed ecco renderizzato per davvero, proprio su questa pagina, a partire da
una piccola specifica "Bookshelf API" che questo repository già fornisce
in [`assets/openapi/example.yaml`](../assets/openapi/example.yaml) - la
stessa che dimostra [OpenAPI / Swagger](openapi.md):

---

# Bookshelf API

::: openapi src="assets/openapi/example.yaml" title="Bookshelf API"
:::

---

Non è uno screenshot né un mockup - è il vero widget interattivo Swagger
UI fornito da bx-sites, alimentato dal file di specifica qui sopra.
Puntato alla specifica del tuo progetto, `bxSitesOpenApiDoc`/
`bxsites:openapi` produce un output byte per byte identico.

## Cosa non copre

Swagger UI viene renderizzato interamente lato client, quindi il testo
per singolo endpoint (come "List books") non raggiunge mai l'indice di
ricerca di bx-sites - viene indicizzato solo il titolo/frontmatter di
questa pagina. Consulta le guide Gradle/Maven collegate sopra per il
resto dell'ambito v1 (springdoc deve comunque generare tu stesso il file
di specifica; `openapi: true` deve comunque essere impostato in
`bxsites.yaml`, sia a mano che tramite `autoPatchConfig`).
