---
title: Proveedores de CLI
order: 6.2
icon: phosphor-duotone:terminal-window
tags: [guías, plugins, cli]
---

# Proveedores de CLI

Un [plugin](plugins.md) se conecta al ciclo de vida del *build* -
configuración, nav, markdown/HTML de página, post-build. Un **proveedor de
CLI** es el punto de extensión hermano para el ciclo de vida de
*comandos*: permite que un módulo de BoxLang instalado y activado
registre sus propios comandos `bxSites <verbo>`, sin tocar `bx-sites`
mismo.

Mismo modelo de activación que un plugin - un módulo se apunta por nombre,
mediante el propio array [`plugins`](../configuration.md#plugins) de
`bxsites.yaml`:

```yaml title="bxsites.yaml"
plugins: [ myBxSitesAddon ]
```

Un módulo puede implementar `models/BxSitesPlugin.bx`,
`models/BxSitesCliProvider.bx`, ambos, o ninguno - instalar/activar un
módulo es un paso; qué contratos implementa decide qué es lo que
realmente extiende.

## Escribir un proveedor de CLI

Un proveedor de CLI necesita exactamente una cosa además de los
habituales `box.json`/`ModuleConfig.bx`: una clase
`models/BxSitesCliProvider.bx` que exponga un único método `verbs()`, que
devuelve un struct de nombre de verbo → información de dispatch:

```bx title="models/BxSitesCliProvider.bx" linenums="1"
// models/BxSitesCliProvider.bx
class {

	struct function verbs() {
		return {
			"cloud:publish" : {
				class       : "models.cli.cloud.Publish@myBxSitesAddon",
				description : "Build (if needed) and publish via the configured deploy target"
			},
			"cloud:status" : {
				class       : "models.cli.cloud.Status@myBxSitesAddon",
				description : "Show license/entitlement and last deploy status"
			}
		}
	}

}
```

Cada clase de dispatch sigue exactamente la misma forma que una clase de
verbo del core bajo el propio `models/cli/` de `bx-sites` - una
`struct function run( struct options )` que devuelve
`{ exitCode, message }`:

```bx title="models/cli/cloud/Publish.bx" linenums="1"
class {
	struct function run( struct options ) {
		// arguments.options carries the same parsed-flags-plus-projectRoot
		// shape every core verb receives - see the CLI reference's
		// "How dispatch works" section.
		return { exitCode : 0, message : "Published #arguments.options.projectRoot#" }
	}
}
```

### El sufijo `@myBxSitesAddon` es obligatorio

Una ruta con puntos simple como `"models.cli.cloud.Publish"` solo se
resuelve en relación con el propio raíz de módulo de `bx-sites` - así es
como los verbos del core hacen referencia a `models/cli/Build.bx` y
similares, pero **no puede** alcanzar un módulo distinto. Las propias
clases de verbo de un proveedor deben suministrar siempre su propio
sufijo `@<mapping>` de módulo, exactamente como se muestra arriba. Esta
es también la razón por la que registrarse en la propia tabla literal de
verbos de `bx-sites` no es algo en lo que un proveedor pueda colarse - la
ruta de clase tiene que nombrar explícitamente su propio módulo.

## Nombres de verbo: de un token y de dos tokens ("compuesto")

Un nombre de verbo se registra como una única cadena unida por dos
puntos, la misma convención que el core ya usa para
`post:new`/`i18n:status`/`page:rename`. `bxSites` también acepta el
equivalente de **dos tokens de argv separados por espacio** como azúcar
sintáctico sobre ese mismo registro - `bxSites cloud publish` y
`bxSites cloud:publish` despachan de forma idéntica, en cuanto
`"cloud:publish"` es un nombre de verbo registrado. No hay un mecanismo
de registro de dos palabras separado que aprender; registra
`"cloud:publish"`, y ambas grafías funcionan gratis.

## Precedencia y modos de fallo

- **El core siempre gana.** Si un proveedor registra un nombre de verbo
  que choca con uno de los verbos incorporados de `bx-sites`, se despacha
  el verbo del core y la entrada del proveedor se ignora silenciosamente
  - un proveedor puede añadir comandos nuevos, nunca eclipsar uno
  existente.
- **Ante una colisión entre dos proveedores, gana el primero.** Si dos
  módulos activados distintos registran el mismo nombre de verbo, gana el
  que aparezca antes en el array `plugins` de `bxsites.yaml`.
- **El descubrimiento nunca rompe el dispatch del core.** Un
  `bxsites.yaml` ausente o mal formado, un proyecto que todavía no existe
  (p. ej. ejecutar `--help` fuera de cualquier proyecto), un módulo
  listado en `plugins` sin su propio `BxSitesCliProvider.bx`, o un
  proveedor cuyo `verbs()` lanza un error - ninguno de estos casos es un
  error. Todos se tratan igual que un plugin no activado: la tabla de
  verbos del core simplemente queda intacta, y todos los comandos
  incorporados de `bxSites` siguen funcionando por su cuenta.

## Un ejemplo mínimo

```text title="Estructura de myBxSitesAddon/"
myBxSitesAddon/
├── box.json                          # boxlang.moduleName is what bxsites.yaml's [plugins] references
├── ModuleConfig.bx                    # a normal BoxLang module descriptor
└── models/
    ├── BxSitesPlugin.bx               # optional - build-lifecycle hooks, see plugins.md
    ├── BxSitesCliProvider.bx          # verbs()
    └── cli/
        └── cloud/
            ├── Publish.bx             # run( options )
            └── Status.bx              # run( options )
```

Consulta [Plugins](plugins.md) para el lado del ciclo de vida del build
del mismo módulo, y la [referencia de la CLI](../cli-reference.md) para
cada verbo que trae el core mismo.
