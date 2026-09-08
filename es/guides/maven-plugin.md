---
title: Plugin de Maven
order: 6.4
icon: phosphor-duotone:puzzle-piece
tags: [guías, java, maven, integration]
---

# Plugin de Maven

Los desarrolladores de Java y Spring Boot no necesitan CommandBox ni una
instalación de BoxLang a nivel de sistema para añadir un sitio bx-sites a
su propio proyecto - el plugin de Maven
`io.boxlang:bxsites-maven-plugin` descarga todo lo que necesita (el
runtime de BoxLang y bx-sites mismo) a una caché local la primera vez que
se ejecuta. El único requisito previo es un JDK 21. Es el equivalente en
Maven del [Plugin de Gradle](gradle-plugin.md) - ambos envuelven la misma
lógica subyacente, así que la cobertura de verbos y el comportamiento se
mantienen idénticos entre ambas herramientas de build.

> **Estado:** pre-1.0, aún no publicado en Maven Central - ver
> [`maven-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/maven-plugin)
> en el repositorio de bx-sites para el código fuente y las instrucciones
> actuales de build/pruebas. Esta página documenta lo que hace una vez
> publicado; la mecánica descrita abajo ya es real y está verificada,
> solo que todavía no está disponible como coordenada de Maven Central.

## Inicio rápido

```xml title="pom.xml"
<build>
  <plugins>
    <plugin>
      <groupId>io.boxlang</groupId>
      <artifactId>bxsites-maven-plugin</artifactId>
      <version>&lt;version&gt;</version>
    </plugin>
  </plugins>
</build>
```

```bash
mvn bxsites:new     # crea docs/ + bxsites.yaml
mvn bxsites:build   # renderiza docs/**.md en site/
mvn bxsites:serve   # compila + sirve localmente con recarga en vivo
```

La forma corta `bxsites:<goal>` (confirmada que funciona) requiere que el
bloque `<plugin>` de arriba esté declarado concretamente bajo
`<build><plugins>`, no solo en `<pluginManagement>` - eso es lo que
registra `io.boxlang` como un prefijo de goal resoluble para el proyecto
actual. Sin esa declaración, usa la forma totalmente cualificada:
`mvn io.boxlang:bxsites-maven-plugin:build`.

Una configuración por defecto no necesita nada más - el plugin detecta
automáticamente el directorio de contenido (`docs/`, o `src/` si no
existe) y el directorio de salida (siempre `<projectRoot>/site/`). El
aspecto, tema, navegación y cualquier otro ajuste de tu sitio se controla
por completo mediante `bxsites.yaml`/`.toml`/`.json` en la raíz del
proyecto, exactamente como se documenta en
[Configuración](../configuration.md) - el plugin nunca duplica ese
esquema, solo gestiona *cómo* y *cuándo* se ejecuta bx-sites desde tu
build.

## Goals

| Goal | Qué hace |
|---|---|
| `bxsites:new` | Genera un nuevo proyecto bx-sites (directorio de contenido + archivo de configuración). |
| `bxsites:build` | Renderiza el sitio en `<projectRoot>/site/`. Omite volver a ejecutar el subproceso cuando nada bajo el directorio de contenido o el archivo de configuración ha cambiado desde el último build - ver [Comprobación de staleness del build](#comprobacion-de-staleness-del-build) más abajo. |
| `bxsites:serve` | Compila y sirve el sitio localmente con recarga en vivo. Se ejecuta en primer plano hasta que lo detienes (Ctrl+C). |
| `bxsites:clean` | Elimina `<projectRoot>/site/`. Borrado de directorio simple - sin subproceso. |
| `bxsites:search-index` | Reconstruye `site/search-index.json` sin un build completo del sitio. |
| `bxsites:lint` | Analiza (lint) las fuentes Markdown de docs/. |
| `bxsites:deploy` | Compila el sitio y lo despliega en el destino configurado. |
| `bxsites:publish` | Compila el sitio y lo publica en bxSites Cloud. |
| `bxsites:package` | Compila el sitio y lo empaqueta en `site.zip`. |
| `bxsites:stats` | Informa del recuento de páginas/palabras y otras estadísticas del sitio compilado. |
| `bxsites:doctor` | Ejecuta los propios diagnósticos de salud del proyecto de bx-sites. |

Cada goal se autoprovisiona (descarga/cachea) lo que necesita, en su
primera ejecución - a diferencia del plugin de Gradle, no existe un goal
de "provisión" separado que ejecutar antes.

Por defecto ningún goal está vinculado a ninguna fase del lifecycle de
Maven - ejecútalos explícitamente. Si quieres que `bxsites:build` se
ejecute automáticamente, vincúlalo tú mismo en un bloque `<executions>`,
por ejemplo a `pre-site` (una combinación natural con el propio lifecycle
`site` de Maven).

## Configuración

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <projectRoot>${project.basedir}</projectRoot>
    <boxlangMiniserverVersion>1.18.0-snapshot</boxlangMiniserverVersion>
    <bxSitesVersion>1.0.0-snapshot</bxSitesVersion>
    <boxlangHomeDir>${project.build.directory}/bxsites/boxlang-home</boxlangHomeDir>
  </configuration>
</plugin>
```

Cada parámetro tiene un valor por defecto razonable - un proyecto nuevo
no necesita fijar ninguno de ellos. El directorio de salida no es
configurable aquí en absoluto - bx-sites mismo lo fija en
`<projectRoot>/site/`, así que el plugin lo deriva en lugar de exponer un
ajuste que de todos modos no se respetaría.

## Comprobación de staleness del build

Maven no tiene un motor de build incremental integrado al estilo Gradle,
así que `bxsites:build` implementa su propia comprobación ligera: compara
la marca de tiempo (mtime) más reciente bajo el directorio de contenido
(más el archivo de configuración, si existe) con la marca de tiempo más
reciente ya presente en `<projectRoot>/site/`. Si nada es más reciente, el
goal registra que se está omitiendo y retorna sin volver a invocar
bx-sites en absoluto. Fuerza un rebuild de todas formas con:

```bash
mvn bxsites:build -Dbxsites.build.forceRebuild=true
```

## Lo que aún no está construido

- **Generación de documentación para Spring Boot** (OpenAPI, Javadoc, escaneo de controladores) - planificado.
- **El streaming de salida en vivo de `bxsites:serve`** - actualmente almacena en búfer la salida con un timeout de 30 minutos, ambas cosas incorrectas para un goal pensado para ejecutarse indefinidamente.

Consulta la guía del [Plugin de Gradle](gradle-plugin.md) para el
equivalente del lado de Gradle - ambos plugins envuelven la misma lógica
subyacente, así que la cobertura de verbos y el comportamiento se
mantienen idénticos entre ambas herramientas de build.
