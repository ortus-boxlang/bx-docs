---
title: OpenAPI-Ausgabebeispiel
order: 6.5
icon: phosphor-duotone:plug
tags: [anleitungen, java, spring-boot, openapi, beispiel]
---

# OpenAPI-Ausgabebeispiel

Ein reales Beispiel dessen, was `bxSitesOpenApiDoc` (Gradle) /
`bxsites:openapi` (Maven) tatsächlich erzeugt - siehe
[Gradle-Plugin](gradle-plugin.md#spring-boot-doc-generation) oder
[Maven-Plugin](maven-plugin.md#spring-boot-doc-generation), um das im
eigenen Projekt zu aktivieren.

Richte `specFile` auf die eigene, von springdoc erzeugte
OpenAPI-Spezifikation, und der Generator kopiert sie nach
`assets/openapi/` und schreibt dann eine Seite mit genau diesem
Frontmatter und diesem Body:

```yaml title="Erzeugtes Frontmatter"
---
title: "Bookshelf API"
tags: [api, openapi]
---
```

```markdown title="Erzeugter Body"
# Bookshelf API

::: openapi src="assets/openapi/openapi.yaml" title="Bookshelf API"
:::
```

Und hier ist es echt gerendert, auf genau dieser Seite, anhand einer
kleinen "Bookshelf API"-Spezifikation, die dieses Repository bereits
mitliefert unter
[`assets/openapi/example.yaml`](../assets/openapi/example.yaml) - derselben,
die auch [OpenAPI / Swagger](openapi.md) selbst zeigt:

---

# Bookshelf API

::: openapi src="assets/openapi/example.yaml" title="Bookshelf API"
:::

---

Das ist kein Screenshot und kein Mockup - es ist das echte, interaktive
Swagger-UI-Widget, das bx-sites mitliefert, angetrieben von der oben
gezeigten Spezifikationsdatei. Auf die eigene Projekt-Spezifikation
gerichtet, erzeugt `bxSitesOpenApiDoc`/`bxsites:openapi` byte-identische
Ausgabe.

## Was das nicht abdeckt

Swagger UI rendert vollständig clientseitig, daher erreicht Text pro
Endpunkt (wie "List books") nie den eigenen Suchindex von bx-sites - nur
Titel/Frontmatter dieser Seite selbst werden indiziert. Siehe die oben
verlinkten Gradle-/Maven-Guides für den Rest des v1-Umfangs (springdoc
muss die Spezifikationsdatei weiterhin selbst erzeugen; `openapi: true`
muss weiterhin in `bxsites.yaml` gesetzt sein, entweder manuell oder über
`autoPatchConfig`).
