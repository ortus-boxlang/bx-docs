---
title: Despliegue
order: 3
icon: phosphor-duotone:cloud-arrow-up
tags: [guías, despliegue]
---

# Despliegue

`site/` es un sitio estático simple - aloja donde sea que sirva archivos
estáticos. [`bxSites deploy`](../cli-reference.md#deploy) lo envía allí
directamente, en un solo comando: S3 (y cualquier servicio compatible con
S3 - DigitalOcean Spaces, Cloudflare R2, Backblaze B2, MinIO), Azure Blob
Storage, Google Cloud Storage, Firebase Hosting, FTP, SFTP, rsync sobre
SSH, Netlify, Vercel, Cloudflare Pages, un directorio local, o GitHub
Pages.

## El comando `deploy`

Todo destino salvo los dos más simples (`local`/`github-pages`, que
funcionan solo con opciones - consulta
[Referencia de la CLI](../cli-reference.md#deploy)) se configura mediante
un archivo `deployments/<name>.json` en la raíz de tu proyecto - un
archivo por cada destino de despliegue que realmente uses, cada uno
indicando en `target` cuál es, además de los propios campos de ese
destino:

```bash frame="terminal" title="Terminal"
bxSites deploy --entry=production
```

```json title="deployments/production.json"
{ "target": "s3", "bucket": "my-docs-site", "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID", "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY" }
```

**Los secretos siempre provienen de una variable de entorno, nunca de un
valor literal en `deployments/*.json`.** Cada campo que termina en
`EnvVar` indica el nombre de la *variable de entorno* que contiene el
secreto real (una clave de acceso, una contraseña, un token de API) -
resuelta en el momento del despliegue, de modo que `deployments/*.json`
en sí siempre es seguro de incluir en un commit. Un campo que es una
*ruta* a un archivo de credenciales que ya gestionas tú mismo (una clave
privada SSH, una clave JSON de cuenta de servicio de GCP descargada) es
la única excepción - un campo normal, ya que lo que se mantiene fuera del
control de versiones es el propio archivo, no su ruta. Localmente, esas
variables de entorno también pueden provenir de un archivo `.env` (BoxLang
carga uno automáticamente y `getSystemSetting()` - lo que usa cada destino
para resolverlas - lo consulta de forma transparente) en lugar de
exportarlas a mano en tu shell; en CI, configúralas como secretos reales
en el ejecutor.

### Desplegar a todos los destinos a la vez

Ejecuta `bxSites deploy` sin `--entry` ni `--target` y cada entrada
`deployments/*.json` se despliega por turno, a partir de una única
construcción compartida:

```bash frame="terminal" title="Terminal"
bxSites deploy
```

El sitio solo se construye una vez sin importar cuántas entradas tengas.
Que un destino falle no detiene el resto - se intenta cada entrada, y el
comando solo sale con un código distinto de cero si al menos uno de ellos
falló; el resumen reporta cuántos tuvieron éxito (por ejemplo, `Deployed
to 2/3 target(s) (1 failed)`). Añade `--verbose` (también funciona con
`--entry`/`--target`) para imprimir una línea de progreso a medida que la
construcción y cada destino comienzan y terminan, en lugar de solo el
resumen final.

### `local`

Copia el sitio construido a cualquier directorio - una unidad compartida,
una carpeta de staging, donde sea. El único destino que no necesita
ninguna entrada en `deployments/`.

```bash frame="terminal" title="Terminal"
bxSites deploy --target=local --destination=/path/to/somewhere
```

### `github-pages`

El mismo push que hace [`gh-deploy`](../cli-reference.md#gh-deploy),
también accesible desde este comando unificado - tampoco necesita
ninguna entrada en `deployments/`:

```bash frame="terminal" title="Terminal"
bxSites deploy --target=github-pages [--branch=gh-pages] [--remote=origin] [--message="..."]
```

### `s3`

AWS S3 real, o cualquier servicio compatible con S3 - configura
`endpoint` para cualquier cosa que no sea AWS, y `forcePathStyle: true`
para la mayoría de los proveedores que no son AWS.

```json title="deployments/production.json"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "region": "us-east-1",
  "prefix": "",
  "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID",
  "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY"
}
```

```json title="deployments/spaces.json (DigitalOcean Spaces)"
{
  "target": "s3",
  "bucket": "my-docs-site",
  "endpoint": "https://nyc3.digitaloceanspaces.com",
  "forcePathStyle": true,
  "accessKeyIdEnvVar": "SPACES_KEY",
  "secretAccessKeyEnvVar": "SPACES_SECRET"
}
```

La misma forma (`endpoint` personalizado + `forcePathStyle: true`)
también cubre Cloudflare R2
(`https://<accountid>.r2.cloudflarestorage.com`), Backblaze B2, y
MinIO/Wasabi.

### `azure`

Azure Blob Storage, autenticado con un token SAS, una clave de cuenta, o
una cadena de conexión completa - exactamente uno de los tres.

```json title="deployments/production.json"
{
  "target": "azure",
  "account": "mystorageaccount",
  "container": "site",
  "accountKeyEnvVar": "AZURE_STORAGE_KEY"
}
```

### `gcs`

Google Cloud Storage, autenticado con una clave JSON de cuenta de
servicio descargada (Google Cloud Console -> IAM & Admin -> Service
Accounts -> Keys).

```json title="deployments/production.json"
{
  "target": "gcs",
  "bucket": "my-docs-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `firebase`

Firebase Hosting, usando el mismo tipo de clave de cuenta de servicio que
`gcs`.

```json title="deployments/production.json"
{
  "target": "firebase",
  "siteId": "my-firebase-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `ftp` / `sftp`

Sube todo el sitio a un servidor remoto por FTP o SFTP, conservando su
estructura de carpetas. SFTP acepta una contraseña o una clave SSH.

```json title="deployments/production.json"
{
  "target": "sftp",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "key": "/home/me/.ssh/id_rsa"
}
```

### `rsync`

Sincroniza el sitio con un servidor remoto por SSH mediante el binario
real de `rsync` - más rápido que FTP/SFTP para una reconstrucción
completa, ya que solo transfiere lo que cambió. Requiere `rsync` y `ssh`
en la máquina que ejecuta `bxSites`.

```json title="deployments/production.json"
{
  "target": "rsync",
  "host": "example.com",
  "username": "deploy",
  "remotePath": "/var/www/html",
  "identityFile": "/home/me/.ssh/id_rsa"
}
```

### `netlify`

```json title="deployments/production.json"
{
  "target": "netlify",
  "siteId": "my-site-id-or-name.netlify.app",
  "authTokenEnvVar": "NETLIFY_AUTH_TOKEN"
}
```

### `vercel`

```json title="deployments/production.json"
{
  "target": "vercel",
  "projectId": "my-project",
  "authTokenEnvVar": "VERCEL_TOKEN"
}
```

### `cloudflare-pages`

Cloudflare no tiene una API REST documentada oficialmente para
despliegues de carga directa - solo su CLI `wrangler`. Este destino
aplica ingeniería inversa al propio flujo de carga de Wrangler, y
necesita una implementación de hash BLAKE3 en el classpath de la JVM que
la mayoría de las instalaciones de Java por defecto no incluyen -
consulta [Referencia de la CLI](../cli-reference.md#deploy) y el propio
código fuente del destino para el detalle completo y honesto sobre las
asperezas de este caso.

```json title="deployments/production.json"
{
  "target": "cloudflare-pages",
  "accountId": "your-account-id",
  "projectName": "my-project",
  "apiTokenEnvVar": "CLOUDFLARE_API_TOKEN"
}
```

## El comando `package`

¿Prefieres un archivo simple antes que cualquiera de los destinos de
arriba - adjuntar una construcción a un release de GitHub, entregarla a
un host que solo acepta una carga en zip, o enviarla a algún lugar que
ninguno de los destinos conectables alcanza?
[`bxSites package`](../cli-reference.md#package) construye el sitio y
luego lo comprime en un único archivo cuya raíz son los propios
contenidos del sitio construido (no una carpeta `site/` que lo envuelva):

```bash frame="terminal" title="Terminal"
bxSites package
bxSites package --output=dist/my-site.zip
```

`--output` por defecto es `<projectRoot>/site.zip`; un valor relativo se
resuelve contra la raíz del proyecto, y sus directorios padre se crean
automáticamente si todavía no existen.

## GitHub Actions (publicación multiversión)

Para la publicación automática en cada push, en lugar de ejecutar
`bxSites deploy`/`gh-deploy` a mano, este módulo incluye un flujo de
trabajo de GitHub Actions listo para usar (`.github/workflows/pages.yml`)
que publica `main` y `development` como dos versiones del mismo sitio
activas de forma independiente en GitHub Pages. El resto de esta guía
cubre ese flujo de trabajo, que la propia documentación de este
repositorio usa.

## Qué hace

En cada push a `main` o `development` que toque `docs/`, `bxsites.yaml`, o
la propia fuente del módulo (cambios de tema/canalización), el flujo de
trabajo:

1. Instala BoxLang + [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
2. Registra este repositorio como un módulo para que `boxlang bxSites build` se resuelva
3. En cualquier rama que no sea `main`, apunta `baseURL` a
   `.../<branch-name>/` solo para esta construcción (consulta
   [más abajo](#publishing-two-versions-at-once))
4. Ejecuta `boxlang bxSites build`
5. Empuja `site/` a la rama `gh-pages` - `main` a la raíz del sitio,
   `development` a `/development/` - sin tocar la otra versión

También está disponible como un disparador manual (`workflow_dispatch`)
desde la pestaña Actions, para una republicación puntual sin un nuevo
commit.

## Configuración inicial

GitHub Pages necesita apuntar a la rama `gh-pages` antes de que el flujo
de trabajo pueda publicar nada - esto es una configuración del
repositorio, no algo que un archivo de flujo de trabajo pueda activar por
sí solo. La primera ejecución exitosa crea `gh-pages` por ti, así que
haz esto *después* de que el flujo de trabajo se haya ejecutado al menos
una vez:

1. **Settings -> Pages**
2. En **Build and deployment -> Source**, elige **Deploy from a branch**
3. En **Branch**, elige **gh-pages** y **/ (root)**

Después de eso, cada push coincidente construye y despliega
automáticamente. La URL publicada aparece en **Settings -> Pages** en
cuanto se completa el primer despliegue.

## Publicar dos versiones a la vez

`main` se publica en la raíz del sitio
(`https://<user>.github.io/<repo>/`) - trata esto como la documentación
estable/publicada. `development` se publica en `/development/`
(`https://<user>.github.io/<repo>/development/`) - la documentación más
reciente, no publicada. Ambas permanecen activas simultáneamente: el
trabajo de cada rama solo empuja a `gh-pages` con `keep_files: true` y su
propio `destination_dir`, de modo que un despliegue de `development`
nunca sobrescribe el contenido de `main` y viceversa.

El propio `bxsites.yaml` de `main` debería tener `baseURL` configurado a
la raíz del sitio (`https://<user>.github.io/<repo>/`); el flujo de
trabajo lo sobrescribe para cualquier otra rama en el momento de la
construcción, así que el `bxsites.yaml` de `development` no necesita su
propia entrada `baseURL` para que esto funcione.

Para añadir una tercera rama (por ejemplo, una vista previa de
`release/2.0`), añádela a la lista `on.push.branches` y dale su propio
paso de despliegue `if: github.ref_name == '...'` con
`destination_dir: release-2.0` (o similar) - el patrón es el mismo que el
de `development`.

## Usar esto para tu propio proyecto

Copia `.github/workflows/pages.yml` en tu propio proyecto (ajusta la
línea `modules:` si tu proyecto necesita algo más allá de
`bx-markdown`), activa Pages como arriba, y los push a `main`/
`development` se publicarán de la misma manera. Si solo quieres una
única versión publicada, elimina la rama que no necesites de
`on.push.branches` y su paso de despliegue correspondiente.

## Servir desde una subruta de Pages de proyecto

Un sitio de GitHub *Project* Pages (a diferencia de un sitio *user*
`<user>.github.io`) se sirve desde `https://<user>.github.io/<repo>/`, no
desde la raíz del dominio. Configura `baseURL` en `bxsites.yaml` con esa
URL completa para que cada enlace interno, recurso y entrada de
navegación obtenga el prefijo `/<repo>/` que necesita - y para que
también se genere un `sitemap.xml` real:

=== "YAML"
    ```yaml title="bxsites.yaml"
    baseURL: "https://<user>.github.io/<repo>/"
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "baseURL": "https://<user>.github.io/<repo>/" }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    baseURL = "https://<user>.github.io/<repo>/"
    ```

Consulta [Configuración](../configuration.md#baseurl) para el desglose
completo de qué hace `baseURL`. Un sitio de usuario `<user>.github.io`, o
cualquier dominio personalizado asignado a la raíz del sitio, puede dejar
`baseURL` en su valor por defecto (`/`).

## Restringir quién puede acceder a tu sitio

Aquí no hay control de acceso incorporado - bx-sites solo produce un
`site/` estático simple, y un archivo estático no tiene ningún concepto
de "quién lo está pidiendo". El [`robots: false`](../configuration.md#robotstxt)
de `bxsites.json` le dice a los rastreadores bien portados que no indexen
una construcción (útil para un despliegue de staging/vista previa que no
quieres que aparezca en los resultados de búsqueda), pero es una petición
educada, no un candado - la URL sigue funcionando para cualquiera que la
tenga. Si de verdad necesitas restringir el acceso, eso tiene que suceder
delante de los archivos estáticos, en el propio host que los sirve -
algunas opciones habituales, adecuadas para sitios estáticos:

- **Cloudflare Pages/Access** - coloca el sitio desplegado detrás de una
  política de [Cloudflare Access](https://developers.cloudflare.com/cloudflare-one/policies/access/)
  (lista blanca de correos, SSO, o un PIN de un solo uso), sin necesidad
  de código de aplicación.
- **Netlify** - [protección con contraseña](https://docs.netlify.com/manage/security/secure-access-to-sites/site-protection/)
  incorporada, por sitio o por despliegue, solo desde los ajustes del
  sitio.
- **Un pequeño proxy inverso** (cualquier host) - HTTP Basic Auth delante
  de los archivos estáticos (una regla al estilo `.htpasswd`, o un
  Cloudflare Worker/Netlify Edge Function de un solo archivo) es
  suficiente para "mantener fuera a los motores de búsqueda y a
  cualquiera al azar", aunque no es una identidad real por usuario como
  la que tendría una aplicación con inicio de sesión.

Nada de esto son funciones de bx-sites - son ajustes a nivel de host que
activas dondequiera que termine sirviéndose `site/`.
