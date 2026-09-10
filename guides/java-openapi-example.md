---
title: OpenAPI Output Example
order: 6.5
icon: phosphor-duotone:plug
tags: [guides, java, spring-boot, openapi, example]
---

# OpenAPI Output Example

A real example of what `bxSitesOpenApiDoc` (Gradle) / `bxsites:openapi`
(Maven) actually produces - see
[Gradle Plugin](gradle-plugin.md#spring-boot-doc-generation) or
[Maven Plugin](maven-plugin.md#spring-boot-doc-generation) for how to turn
this on in your own project.

Point `specFile` at your own springdoc-generated OpenAPI spec and the
generator copies it into `assets/openapi/`, then writes a page with this
exact frontmatter and body shape:

```yaml title="Generated frontmatter"
---
title: "Bookshelf API"
tags: [api, openapi]
---
```

```markdown title="Generated body"
# Bookshelf API

::: openapi src="assets/openapi/openapi.yaml" title="Bookshelf API"
:::
```

And here it is rendered for real, on this exact page, against a small
"Bookshelf API" spec this repo already ships at
[`assets/openapi/example.yaml`](../../assets/openapi/example.yaml) - the same
one [OpenAPI / Swagger](openapi.md) itself demonstrates:

---

# Bookshelf API

::: openapi src="assets/openapi/example.yaml" title="Bookshelf API"
:::

---

That's not a screenshot or a mockup - it's the actual interactive Swagger UI
widget bx-sites ships, driven by the actual spec file above. Pointed at your
own project's spec instead, `bxSitesOpenApiDoc`/`bxsites:openapi` produces
byte-identical output.

## What this doesn't cover

Swagger UI renders entirely client-side, so per-endpoint text (like "List
books") never reaches bx-sites' own search index - only this page's own
title/frontmatter is indexed. See the Gradle/Maven guides linked above for
the rest of the v1 scope (springdoc still has to generate the spec file
yourself; `openapi: true` still has to be set in `bxsites.yaml`, either by
hand or via `autoPatchConfig`).
