---
title: Referencia de API con DocBox
order: 6.7
icon: phosphor-duotone:brackets-curly
tags: [guías, boxlang, docbox, api, integration]
---

# Referencia de API con DocBox

`bxSites docbox` convierte tus clases BoxLang/CFML en una referencia de API
temada y buscable dentro de tu propio sitio: el equivalente en BoxLang de
los generadores de Javadoc que los plugins de
[Gradle](gradle-plugin.md) y [Maven](maven-plugin.md) ofrecen a los
proyectos Java.

No analiza ni una línea de BoxLang por su cuenta.
[DocBox](https://docbox.ortusbooks.com) ya hace bien ese trabajo, así que
aquí se ejecuta su propia estrategia JSON y el resultado se convierte en
páginas Markdown normales. Aterrizan en tu directorio de contenido como
cualquier otra página, de modo que el siguiente `build` las tema, las
indexa para la búsqueda y las sirve igual que si las hubieras escrito a
mano.

## Requisito previo

El módulo `bx-docbox` debe estar instalado en el runtime de BoxLang:

```bash frame="terminal" title="Terminal"
# Binario del sistema
install-bx-module bx-docbox

# CommandBox
box install bx-docbox
```

Si falta, el verbo termina con un mensaje accionable en lugar de una traza
de pila.

## Inicio rápido

```bash frame="terminal" title="Terminal"
bxSites docbox
bxSites build
```

Sin configuración alguna, `docbox` documenta las carpetas de código
BoxLang convencionales que tu proyecto realmente tenga - `models`,
`handlers`, `bifs`, `components`, `interceptors` - y escribe las páginas
en `docs/api/docbox/`.

## Configuración

Todo es opcional; el esquema completo está en
[`docbox`](../configuration.md#docbox).

=== "YAML"
    ```yaml title="bxsites.yaml"
    docbox:
      projectTitle: "Mi API"
      mappings:
        models: models
        bifs: bifs
      excludes: "tests|build"
      pagePathPrefix: api/docbox
      tags: [ api, docbox ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"docbox": {
    		"projectTitle": "Mi API",
    		"mappings": { "models": "models", "bifs": "bifs" },
    		"excludes": "tests|build",
    		"pagePathPrefix": "api/docbox",
    		"tags": [ "api", "docbox" ]
    	}
    }
    ```

Cada clave tiene un flag que la sobrescribe para una ejecución:

```bash frame="terminal" title="Terminal"
bxSites docbox --mappings:models=models --projectTitle="Mi API" \
	--pagePathPrefix=api/classes --tags=api,classes --excludes=tests
```

`--jsonDir=<ruta>` conserva la salida JSON de DocBox en lugar de
descartarla.

## Qué se genera

Tres tipos de página: un índice general, un índice por paquete y una página
por clase.

```
docs/api/docbox/
├── index.md                    # todos los paquetes y clases
├── models/
│   ├── index.md                # las clases de este paquete
│   ├── UserService.md
│   └── security/
│       ├── index.md
│       └── Auth.md
```

La página de una clase lleva su propio docblock, sus bloques `property`
declarados y sus funciones agrupadas por acceso - primero el constructor,
después public, package y private - cada una con su firma completa, su
descripción, su tabla de parámetros y el texto de `@return`. Los miembros
van dentro de la misma barra de filtro de Alpine.js que usan las páginas de
Javadoc, para que una clase larga siga siendo legible de un vistazo.

## Enlazarlas desde tu navegación

Las páginas generadas son contenido normal, así que aparecen solas en la
navegación automática por directorios. Para colocarlas deliberadamente,
nombra el índice en tu propia [`nav`](../configuration.md#nav):

=== "YAML"
    ```yaml title="bxsites.yaml"
    nav:
      - title: Referencia
        children:
          - api/docbox/index.md
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"nav": [
    		{ "title": "Referencia", "children": [ "api/docbox/index.md" ] }
    	]
    }
    ```

## Lo que deliberadamente no hace

- **Sin enlaces entre clases.** Un objetivo de `extends`/`implements`, o un
  tipo nombrado en una firma, se muestra como código en línea aunque esa
  clase tenga su propia página generada.
- **Sin miembros heredados.** Solo lo que la clase declara por sí misma,
  igual que la propia salida JSON de DocBox.
- **Sin cableado de navegación.** Las páginas van bajo `pagePathPrefix`;
  colocarlas es decisión tuya.

## De dónde salen los metadatos

La estrategia JSON de DocBox serializa el nombre, el paquete, el tipo,
`extends` y las funciones de una clase, pero no sus bloques `property`
declarados, las interfaces que implementa, sus anotaciones de clase ni el
texto `@return` de cada función. Esos cuatro importan en una referencia de
BoxLang, y las propiedades sobre todo, porque llevan cada campo de
`accessors` y cada `inject` de WireBox. Por eso bx-sites los relee de los
mismos metadatos de clase que usó DocBox y los fusiona antes de renderizar.
Nada se analiza dos veces.

## Desde Gradle o Maven

Ambos plugins de build exponen también este generador, para un proyecto JVM
cuyas fuentes incluyan clases BoxLang/CFML - consulta
[Plugin de Gradle](gradle-plugin.md#boxlang-doc-generation) y
[Plugin de Maven](maven-plugin.md#boxlang-doc-generation).

Documentar una aplicación ColdBox es un verbo aparte - consulta
[Aplicaciones ColdBox](coldbox.md).
