---
title: Plugin de Gradle
order: 6.3
icon: phosphor-duotone:gear-six
tags: [guías, java, gradle, integration]
---

# Plugin de Gradle

Los desarrolladores de Java y Spring Boot no necesitan CommandBox ni una
instalación de BoxLang a nivel de sistema para añadir un sitio bx-sites a
su propio proyecto - el plugin de Gradle `io.boxlang.bxsites` descarga
todo lo que necesita (el runtime de BoxLang y bx-sites mismo) a una caché
local la primera vez que se ejecuta. El único requisito previo es un
JDK 21.

> **Estado:** pre-1.0, aún no publicado en el Gradle Plugin Portal - ver
> [`gradle-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/gradle-plugin)
> en el repositorio de bx-sites para el código fuente y las instrucciones
> actuales de build/pruebas. Esta página documenta lo que hace una vez
> publicado; la mecánica descrita abajo ya es real y está verificada,
> solo que todavía no está disponible como una dependencia de una sola
> línea en `plugins { }`.

## Inicio rápido

```kotlin title="build.gradle.kts"
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # crea docs/ + bxsites.yaml
./gradlew bxSitesBuild   # renderiza docs/**.md en site/
./gradlew bxSitesServe   # compila + sirve localmente con recarga en vivo
```

Una configuración por defecto no necesita nada más - el plugin detecta
automáticamente el directorio de contenido (`docs/`, o `src/` si no
existe - salvo en un proyecto con el plugin de Java aplicado, donde
`src/` es tu propio directorio de fuentes Java y nunca se usa como
contenido de bx-sites) y el directorio de salida (siempre
`<projectRoot>/site/`). El
aspecto, tema, navegación y cualquier otro ajuste de tu sitio se controla
por completo mediante `bxsites.yaml`/`.toml`/`.json` en la raíz del
proyecto, exactamente como se documenta en
[Configuración](../configuration.md) - el plugin nunca duplica ese
esquema, solo gestiona *cómo* y *cuándo* se ejecuta bx-sites desde tu
build.

## Tasks

| Task | Qué hace |
|---|---|
| `bxSitesNew` | Genera un nuevo proyecto bx-sites. No está vinculada a ningún lifecycle - ejecútala una vez, de forma explícita. |
| `bxSitesBuild` | Renderiza el sitio. Comprobación real de actualidad: se vuelve a ejecutar solo cuando tu contenido, configuración o las versiones fijadas realmente cambian. |
| `bxSitesServe` | Compila y sirve el sitio localmente con recarga en vivo. Se ejecuta en primer plano hasta que se detiene. |
| `bxSitesClean` | Elimina el directorio `site/` generado. |
| `bxSitesSearchIndex` | Reconstruye `site/search-index.json` sin un build completo del sitio. |
| `bxSitesLint` | Analiza (lint) las fuentes Markdown de docs/. Vinculada a `check` por defecto (ver `hookIntoCheck` más abajo). |
| `bxSitesDeploy` | Compila el sitio y lo despliega en el destino configurado. |
| `bxSitesPublish` | Compila el sitio y lo publica en bxSites Cloud. |
| `bxSitesPackage` | Compila el sitio y lo empaqueta en `site.zip`. |
| `bxSitesStats` | Informa del recuento de páginas/palabras y otras estadísticas del sitio compilado. |
| `bxSitesDoctor` | Ejecuta los propios diagnósticos de salud del proyecto de bx-sites. |

`bxSitesBuild` nunca se ejecuta automáticamente como parte de `assemble`
a menos que lo habilites (ver `hookIntoAssemble` más abajo) - un build de
documentación es una preocupación distinta, a menudo más lenta, que
compilar tu propio código.

## Configuración

```kotlin title="build.gradle.kts"
bxSites {
    projectRoot.set(layout.projectDirectory)
    boxlangMiniserverVersion.set("1.18.0-snapshot")   // versión fijada del runtime de BoxLang
    bxSitesVersion.set("1.0.0-snapshot")               // versión fijada de bx-sites
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                        // opcional: ejecutar bxSitesBuild como parte de assemble
    hookIntoCheck.set(true)                            // vincula bxSitesLint a `check` por defecto
}
```

Cada propiedad tiene un valor por defecto razonable. El directorio de
salida no es configurable aquí en absoluto - bx-sites mismo lo fija en
`<projectRoot>/site/`, así que el plugin lo deriva en lugar de exponer un
ajuste que de todos modos no se respetaría.

## Lo que aún no está construido

- **Generación de documentación para Spring Boot** (OpenAPI, Javadoc, escaneo de controladores) - planificado.
- **El streaming de salida en vivo de `bxSitesServe`** - actualmente almacena en búfer la salida con un timeout de 30 minutos, ambas cosas incorrectas para una task pensada para ejecutarse indefinidamente.

Consulta la guía del [Plugin de Maven](maven-plugin.md) para el
equivalente del lado de Maven - ambos plugins envuelven la misma lógica
subyacente, así que la cobertura de verbos y el comportamiento se
mantienen idénticos entre ambas herramientas de build.
