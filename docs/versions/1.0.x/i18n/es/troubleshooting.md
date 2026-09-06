---
title: Solución de problemas
order: 2.4
icon: phosphor-duotone:lifebuoy
summary: Diagnostica problemas comunes de configuración, build y serve - y dónde obtener ayuda si esta página no lo cubre.
tags: [solución-de-problemas, faq]
---

# Solución de problemas

## Primero ejecuta `doctor`

Antes de investigar más, ejecuta la verificación de salud integrada -
resuelve la mayoría de los problemas de esta página de un solo golpe:

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Verifica la versión de la JVM, que `docs/` (o `src/`) exista, que
`bxsites.yaml`/`.json` realmente se pueda parsear y validar, que los
módulos de BoxLang requeridos estén instalados y activados, y - si existe
un override de `theme/` a nivel de proyecto - que cumpla el contrato del
tema. Termina con código `1` si alguna verificación falla e imprime qué
está mal; nada de esto modifica tu proyecto.

## Problemas comunes

??? bug "`No docs/ directory found`"
    `build`/`serve`/`site:check`/etc. buscan `docs/` (con respaldo en `src/`)
    relativo al directorio actual, o a `--projectRoot=<path>` si lo
    pasaste. Ejecuta el comando desde dentro de la carpeta raíz de tu
    proyecto, o pasa `--projectRoot`:

    ```bash frame="terminal" title="Terminal"
    bxSites build --projectRoot=/ruta/a/mis-docs
    ```

??? bug "`bxsites.yaml`/`.json` falla al parsear o validar"
    Ejecuta `bxSites doctor` para ver exactamente qué clave/línea rechazó
    el cargador de configuración. Causas comunes: mezclar tabulaciones y
    espacios en la indentación YAML, una coma sobrante en JSON, o una
    clave que espera un array (como `nav` o `i18n.locales`) escrita como
    un string simple. Consulta [Configuración](configuration.md) para la
    referencia completa de claves.

??? bug "`bx-markdown`/`bx-esapi`/`bx-yaml`/`bx-image` no instalado/activado"
    `build`, `serve` y `search-index` necesitan estos cuatro módulos de
    BoxLang. Instalar `bx-sites` en sí los instala automáticamente como
    dependencias de `box.json` (`install-bx-module bx-sites` o
    `box install bx-sites`) - si ves esto, o la instalación no terminó, o
    el módulo se registró a mano sin sus dependencias. Volver a ejecutar
    `box install` desde la raíz de tu proyecto resuelve todo de nuevo;
    `bxSites doctor` confirma qué módulo (si alguno) sigue faltando.

??? bug "Un override de `theme/` del proyecto falla al construir"
    Una carpeta `theme/` personalizada debe proporcionar tanto
    `layout.bxm` como `page.bxm` - `doctor` indica cuál falta. Consulta
    [Temas](guides/themes.md) para el contrato completo, o ejecuta
    `bxSites theme:new` para exportar un tema integrado como punto de
    partida funcional en lugar de escribir uno desde cero.

??? bug "`serve` no detecta un cambio"
    `serve` observa `docs/`, tu `bxsites.yaml`/`.json`, y un override de
    `theme/` a nivel de proyecto - un cambio en cualquier otro lugar (por
    ejemplo, editar un archivo bajo `resources/` en un checkout de
    módulo, no en un proyecto real) no dispara una reconstrucción. Si un
    cambio real no se refleja, detén `serve`, ejecuta `bxSites clean`
    para limpiar cualquier caché de build obsoleta, y vuelve a ejecutar
    `bxSites serve`.

??? bug "Un build se ve desactualizado, o CI reporta éxito pero nada cambió"
    `build` no elimina el output previamente construido que ya no tiene
    una página fuente correspondiente. Ejecuta `bxSites clean` antes de
    `build` para eliminar `site/` y cualquier caché de build por
    completo, y luego reconstruye desde cero. Si un paso de CI reporta
    éxito pero el sitio desplegado no lo refleja, revisa el log real del
    paso de build en busca de `Error:` - un build que falla puede seguir
    reportando un estado de éxito engañoso en algunas configuraciones de
    CI.

??? bug "Una página traducida muestra un aviso de página sin traducir"
    Eso es esperado, no un error: un idioma no necesita tener todas sus
    páginas traducidas para ser usable. Una página que falta en
    `docs/i18n/<código>/` igual se construye en su URL esperada, mostrando
    el contenido del idioma predeterminado con un pequeño aviso en la
    parte superior. Consulta
    [Internacionalización (i18n)](guides/i18n.md).

??? bug "`i18n:status` reporta 100 % pero una traducción sigue viéndose desactualizada"
    `i18n:status` solo verifica la *presencia* de la página por idioma,
    no la paridad de contenido por página - una copia de idioma puede
    existir pero aun así faltarle una sección añadida después a la
    página del idioma predeterminado. Compara el archivo del idioma
    directamente con su equivalente en el idioma predeterminado si
    sospechas esto.

## ¿Sigues atascado?

Si nada de lo anterior lo cubre, contacta a través de uno de los canales
de soporte a continuación - consulta [Contribuir](contribute.md) para la
lista completa:

::: cards
::: card title="Ortus Community Discourse" icon="phosphor-duotone:chats-circle" href="https://community.ortussolutions.com"
Haz preguntas y busca en discusiones existentes.
:::
::: card title="Box Slack Team" icon="phosphor-duotone:slack-logo" href="https://boxteam.slack.com"
Chatea en tiempo real con la comunidad y los mantenedores.
:::
::: card title="Reportar un issue" icon="phosphor-duotone:bug" href="https://github.com/ortus-boxlang/bx-sites/issues"
Para un error reproducible, con la salida de `bxSites doctor` adjunta.
:::
::: card title="Soporte profesional" icon="phosphor-duotone:handshake" href="services.md"
¿Prefieres que el equipo detrás de BxSites lo resuelva por ti? Ortus
Solutions ofrece soporte profesional y consultoría.
:::
:::
