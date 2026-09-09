---
title: Aplicaciones ColdBox
order: 6.8
icon: phosphor-duotone:tree-structure
tags: [guías, boxlang, coldbox, api, integration]
---

# Aplicaciones ColdBox

`bxSites coldbox` documenta una aplicación
[ColdBox](https://coldbox.ortusbooks.com) a partir de sus convenciones en
disco: rutas, event handlers, modelos y mappings de WireBox, módulos,
interceptores y tareas programadas.

Nunca arranca la aplicación. No hay que compilar nada, ninguna datasource
tiene que estar accesible y ninguna variable de entorno tiene que estar
definida, así que funciona igual en un runner de CI que en tu portátil. Lo
que ese enfoque no puede ver está en [Límites](#límites), más abajo.

## Inicio rápido

Desde la raíz de una app ColdBox que además contiene un proyecto bx-sites:

```bash frame="terminal" title="Terminal"
bxSites coldbox
bxSites build
```

Las páginas aterrizan en `docs/api/coldbox/`. Si tu app vive en otro sitio:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app
```

## Configuración

Todo es opcional; el esquema completo está en
[`coldbox`](../configuration.md#coldbox).

=== "YAML"
    ```yaml title="bxsites.yaml"
    coldbox:
      appRoot: "."
      pagePathPrefix: api/coldbox
      tags: [ api, coldbox ]
      include: [ routes, handlers, models, modules, interceptors, scheduler ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    {
    	"coldbox": {
    		"appRoot": ".",
    		"pagePathPrefix": "api/coldbox",
    		"tags": [ "api", "coldbox" ],
    		"include": [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [coldbox]
    appRoot = "."
    pagePathPrefix = "api/coldbox"
    tags = [ "api", "coldbox" ]
    include = [ "routes", "handlers", "models", "modules", "interceptors", "scheduler" ]
    ```

`include` decide qué conjuntos de páginas se generan; si dejas fuera un
token, ese conjunto se omite por completo. Cada clave tiene su flag:

```bash frame="terminal" title="Terminal"
bxSites coldbox --appRoot=app --include=routes,handlers \
	--pagePathPrefix=reference --tags=reference,api
```

## Qué se genera

```
docs/api/coldbox/
├── index.md               # resumen de la app y recuentos
├── routes.md              # todas las rutas, en orden de declaración
├── handlers/
│   ├── index.md
│   ├── Main.md
│   └── api/Orders.md      # los handlers de módulo anidan bajo su módulo
├── models/
│   ├── index.md           # modelos y los mappings del binder
│   └── UserService.md
├── modules/
│   ├── index.md
│   └── api.md
├── interceptors.md
└── scheduled-tasks.md
```

### Rutas

Todas las rutas que declaran el router de la app y el de cada módulo, en el
orden en que se declaran: ColdBox aplica la primera que encaja, así que el
orden significa algo y la página lo respeta. `resources()`/`apiResources()`
se expanden en las rutas individuales que ColdBox genera para ellas, y las
rutas de un módulo llevan el entry point en el que están realmente
montadas.

### Handlers

Una página por handler, con las rutas que lo alcanzan y la acción a la que
llega cada una, y después sus acciones enrutables con sus comentarios de
documentación y argumentos. Los hooks de ciclo de vida de ColdBox
(`preHandler`, `aroundHandler`, `onError` y compañía) tienen su propia
sección en vez de aparecer como acciones alcanzables por URL; `init` y los
métodos privados quedan fuera.

### Modelos

Una página por modelo: su scope, lo que WireBox le inyecta, sus propias
propiedades y sus métodos públicos. Las dependencias inyectadas van
separadas de las propiedades normales, porque
`property name="x" inject="y"` es cableado, no datos. El índice añade los
mappings declarados por el binder.

### Módulos, interceptores y tareas programadas

La página de un módulo lleva lo que declara su `ModuleConfig` - autor,
versión, entry point, dependencias - y lo que aporta a la aplicación, con
cada elemento enlazado a la página que lo describe. La página de
interceptores cubre las dos mitades de cómo los encuentra ColdBox: lo que
registra `config/ColdBox` y lo que declara `interceptors/`, con los métodos
públicos de cada clase como los puntos de intercepción que son. La página
de tareas lee `config/Scheduler` y el de cada módulo.

## Páginas más ricas con DocBox

Las rutas, los módulos, los interceptores y la forma de la app salen solo
de las convenciones. El detalle por clase - las acciones de un handler, los
métodos de un modelo, sus argumentos y comentarios - sale de
[DocBox](docbox.md), así que instalar `bx-docbox` hace esas páginas
bastante más ricas:

```bash frame="terminal" title="Terminal"
install-bx-module bx-docbox
```

Sin él el verbo sigue funcionando y sigue listando cada handler, modelo,
ruta y módulo; las páginas simplemente dicen qué falta y cómo obtenerlo.

## Límites

La lectura estática tiene un límite duro, y es mejor nombrarlo que
disimularlo. Lo que una aplicación decide en tiempo de ejecución no está en
disco para encontrarlo:

- Una ruta cuyo patrón o destino se construye desde una variable, o que se
  registra en un bucle.
- Un mapping de WireBox o una tarea programada con nombre calculado.
- Un módulo instalado al arrancar en lugar de versionado en
  `modules_app/`.
- Lo que `mapDirectory()` registrará realmente, que depende de lo que haya
  en disco cuando arranca la app.

Todo eso se omite en lugar de adivinarse: una página generada se queda
corta antes que mentir.

## No disponible desde Gradle ni Maven

Deliberadamente. Una aplicación ColdBox se construye y se ejecuta con
CommandBox, nunca con una herramienta de build de Java, así que no hay
tarea `bxSitesColdBoxDoc` ni goal `bxsites:coldbox`. Los plugins de
[Gradle](gradle-plugin.md) y [Maven](maven-plugin.md) sí exponen el
generador de [DocBox](docbox.md), que documenta clases BoxLang/CFML que sí
viven en el proyecto JVM que se está construyendo.
