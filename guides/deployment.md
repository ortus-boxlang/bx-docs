---
title: Deployment
order: 3
icon: phosphor-duotone:cloud-arrow-up
tags: [guides, deployment]
---

# Deployment

`site/` is a plain static site - host it anywhere that serves static
files. [`bxSites deploy`](../cli-reference.md#deploy) ships it there
directly, in one command: S3 (and any S3-compatible service - DigitalOcean
Spaces, Cloudflare R2, Backblaze B2, MinIO), Azure Blob Storage, Google
Cloud Storage, Firebase Hosting, FTP, SFTP, rsync-over-SSH, Netlify,
Vercel, Cloudflare Pages, a local directory, or GitHub Pages. Hosting on
[bxSites Cloud](https://bxsites.io) itself instead?
[`bxSites publish`](../cli-reference.md#publish) is the dedicated command
for that - see [below](#the-publish-command).

## The `deploy` command

Every target but the two simplest (`local`/`github-pages`, which work from
flags alone - see [CLI Reference](../cli-reference.md#deploy)) is
configured via a `deployments/<name>.json` file at your project root - one
file per deploy target you actually use, each naming which `target` it is
plus that target's own fields:

```bash frame="terminal" title="Terminal"
bxSites deploy --entry=production
```

```json title="deployments/production.json"
{ "target": "s3", "bucket": "my-docs-site", "accessKeyIdEnvVar": "AWS_ACCESS_KEY_ID", "secretAccessKeyEnvVar": "AWS_SECRET_ACCESS_KEY" }
```

**Secrets always come from an environment variable, never from a literal
value in `deployments/*.json`.** Every field ending in `EnvVar` names the
*environment variable* holding the real secret (an access key, a password,
an API token) - resolved live at deploy time, so `deployments/*.json`
itself is always safe to commit. A field that's a *path* to a credential
file you already manage yourself (an SSH private key, a downloaded GCP
service-account JSON key) is the one exception - a plain field, since the
file itself is what's kept out of version control, not its path. Locally,
those environment variables can come from a `.env` file too (BoxLang loads
one automatically and `getSystemSetting()` - what every target uses to
resolve them - checks it transparently) instead of exporting them into
your shell by hand; in CI, set them as real secrets on the runner.

### Deploying to every target at once

Run `bxSites deploy` with neither `--entry` nor `--target` and every
`deployments/*.json` entry is deployed in turn, off a single shared build:

```bash frame="terminal" title="Terminal"
bxSites deploy
```

The site is only built once no matter how many entries you have. One
target failing doesn't stop the rest - every entry is attempted, and the
command only exits non-zero if at least one of them failed; the summary
reports how many succeeded (e.g. `Deployed to 2/3 target(s) (1 failed)`).
Add `--verbose` (works with `--entry`/`--target` too) to print a progress
line as the build and each target start and finish, instead of just the
final summary.

### `local`

Copies the built site to any directory - a shared drive, a staging folder,
anywhere. The only target that needs no `deployments/` entry at all.

```bash frame="terminal" title="Terminal"
bxSites deploy --target=local --destination=/path/to/somewhere
```

### `github-pages`

The same push [`gh-deploy`](../cli-reference.md#gh-deploy) does, reachable
from this unified command too - also needs no `deployments/` entry:

```bash frame="terminal" title="Terminal"
bxSites deploy --target=github-pages [--branch=gh-pages] [--remote=origin] [--message="..."]
```

### `s3`

Real AWS S3, or any S3-compatible service - set `endpoint` for anything
other than AWS itself, and `forcePathStyle: true` for most non-AWS
providers.

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

The same shape (custom `endpoint` + `forcePathStyle: true`) also covers
Cloudflare R2 (`https://<accountid>.r2.cloudflarestorage.com`), Backblaze
B2, and MinIO/Wasabi.

### `azure`

Azure Blob Storage, authenticated with a SAS token, an account key, or a
full connection string - exactly one of the three.

```json title="deployments/production.json"
{
  "target": "azure",
  "account": "mystorageaccount",
  "container": "site",
  "accountKeyEnvVar": "AZURE_STORAGE_KEY"
}
```

### `gcs`

Google Cloud Storage, authenticated with a downloaded service-account JSON
key (Google Cloud Console -> IAM & Admin -> Service Accounts -> Keys).

```json title="deployments/production.json"
{
  "target": "gcs",
  "bucket": "my-docs-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `firebase`

Firebase Hosting, using the same kind of service-account key as `gcs`.

```json title="deployments/production.json"
{
  "target": "firebase",
  "siteId": "my-firebase-site",
  "serviceAccountKeyPath": "/path/to/service-account.json"
}
```

### `ftp` / `sftp`

Uploads the whole site to a remote server over FTP or SFTP, preserving its
folder structure. SFTP accepts a password or an SSH key.

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

Syncs the site to a remote server over SSH via the real `rsync` binary -
faster than FTP/SFTP for a full rebuild, since it only transfers what
changed. Requires `rsync` and `ssh` on the machine running `bxSites`.

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

Cloudflare has no officially documented REST API for direct-upload
deploys - only its `wrangler` CLI. This target reverse-engineers
Wrangler's own upload flow, and needs a BLAKE3 hash implementation on the
JVM classpath that most default Java installs don't ship - see
[CLI Reference](../cli-reference.md#deploy) and the target's own source
for the full, honest detail on this one's rough edges.

```json title="deployments/production.json"
{
  "target": "cloudflare-pages",
  "accountId": "your-account-id",
  "projectName": "my-project",
  "apiTokenEnvVar": "CLOUDFLARE_API_TOKEN"
}
```

## The `publish` command

[bxSites Cloud](https://bxsites.io) is a hosting SaaS built specifically
for bxSites sites. `bxSites publish` builds the site and ships it there
directly, over bxSites Cloud's own publish API - distinct from `deploy`'s
pluggable targets above (which all ship to infrastructure *you* own).

```bash frame="terminal" title="Terminal"
export BXSITES_CLOUD_TOKEN="cb_..."
bxSites publish
```

Configure the destination in `bxsites.yaml`'s [`cloud`](../configuration.md#cloud)
block - the target site's UUID and your bxSites Cloud instance's base URL:

```yaml title="bxsites.yaml"
cloud:
  siteId: "3f2b1c9a-....-....-............"
  apiUrl: "https://cloud.bxsites.app"
```

**The API token never goes in `bxsites.yaml`.** `publish` reads it from the
`BXSITES_CLOUD_TOKEN` environment variable, or an explicit `--token=<token>`
flag which wins when both are set:

```bash frame="terminal" title="Terminal"
bxSites publish --token=cb_...
```

On success, `publish` prints the site's live URL. It fails with a clear,
specific error (not a raw exception or stack trace) for every case worth
distinguishing:

- **No `[cloud]` block, or an incomplete one** - add `cloud.siteId`/
  `cloud.apiUrl` to `bxsites.yaml` as shown above.
- **No token available** - set `BXSITES_CLOUD_TOKEN` or pass `--token`.
- **401/403 (authentication failed)** - the token is missing, invalid, or
  doesn't have access to this site.
- **404 (site not found)** - double check `cloud.siteId` is correct.
- **Anything else (400/413/500, or a network failure)** - the actual HTTP
  status and any error body the server returned are included, for
  debugging.

## The `package` command

Prefer a plain archive over any of the targets above - attaching a build to
a GitHub release, handing it to a host that only accepts a zip upload, or
shipping it somewhere none of the pluggable targets reach?
[`bxSites package`](../cli-reference.md#package) builds the site, then zips
it into a single file whose root is the built site's own contents (not a
wrapping `site/` folder):

```bash frame="terminal" title="Terminal"
bxSites package
bxSites package --output=dist/my-site.zip
```

`--output` defaults to `<projectRoot>/site.zip`; a relative value is
resolved against the project root, and its parent directories are created
automatically if they don't already exist.

## GitHub Actions

For automatic publishing on every push, rather than a manually-run
`bxSites deploy`/`gh-deploy`, this module ships a ready-to-use GitHub
Actions workflow (`.github/workflows/pages.yml`) that publishes `main`
straight to the site root on GitHub Pages. The rest of this guide covers
that workflow, which this repo's own docs use.

Publishing more than one live doc set at once - a stable release plus
what's currently being written toward the next one - isn't a branch
concern in this workflow at all: it's `versions.default`
([Versioning](versioning.md#publishing-a-default-version-at-the-site-root)),
which builds the released version at the site root and the plain `docs/`
tree at `/next/` from the exact same `main` build, in one pass. This
repo's own `bxsites.yaml` does exactly that.

## What it does

On every push to `main` that touches `docs/`, `bxsites.yaml`, or the
module's own source (theme/pipeline changes), the workflow:

1. Installs BoxLang + [bx-markdown](https://github.com/ortus-boxlang/bx-markdown)
2. Registers this repo as a module so `boxlang bxSites build` resolves
3. Runs `boxlang bxSites build`
4. Pushes `site/` to the `gh-pages` branch, at the site root

It's also available as a manual trigger (`workflow_dispatch`) from the
Actions tab, for a one-off republish without a new commit.

## One-time setup

GitHub Pages needs to be pointed at the `gh-pages` branch before the
workflow can publish anything - this is a repository setting, not something
a workflow file can turn on by itself. The first successful run creates
`gh-pages` for you, so do this *after* the workflow has run at least once:

1. **Settings -> Pages**
2. Under **Build and deployment -> Source**, choose **Deploy from a branch**
3. Under **Branch**, choose **gh-pages** and **/ (root)**

After that, every matching push builds and deploys automatically. The
published URL shows up under **Settings -> Pages** once the first deploy
completes.

## Using this for your own project

Copy `.github/workflows/pages.yml` into your own project (adjust the
`modules:` line if your project needs anything beyond `bx-markdown`),
enable Pages as above, and every push to `main` publishes the same way.
Add [`versions.default`](versioning.md#publishing-a-default-version-at-the-site-root)
to `bxsites.yaml` if you want a released-version-at-root/next-in-progress
split like this repo's own docs use - no workflow changes needed for that,
it's driven entirely by the build itself.

## Serving from a project Pages sub-path

A GitHub *project* Pages site (as opposed to a `<user>.github.io` *user*
site) is served from `https://<user>.github.io/<repo>/`, not from the
domain root. Set `baseURL` in `bxsites.yaml` to that full URL so every
internal link, asset and nav entry gets the `/<repo>/` prefix it needs -
and so a real `sitemap.xml` gets generated too:

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

See [Configuration](../configuration.md#baseurl) for the full breakdown of
what `baseURL` does. A `<user>.github.io` user site, or any custom domain
mapped to the site root, can leave `baseURL` at its default (`/`).

## Restricting who can reach your site

There's no built-in access control here - bx-sites only ever produces a
plain static `site/`, and a static file has no concept of "who's asking."
`bxsites.json`'s [`robots: false`](../configuration.md#robotstxt) tells
well-behaved crawlers not to index a build (useful for a staging/preview
deploy you don't want turning up in search results), but it's a polite
request, not a lock - the URL still works for anyone who has it. If you
actually need to gate access, that has to happen in front of the static
files, at whichever host is serving them - a few common, static-friendly
options:

- **Cloudflare Pages/Access** - put the deployed site behind a
  [Cloudflare Access](https://developers.cloudflare.com/cloudflare-one/policies/access/)
  policy (email allowlist, SSO, or a one-time PIN), no application code
  needed.
- **Netlify** - built-in
  [password protection](https://docs.netlify.com/manage/security/secure-access-to-sites/site-protection/)
  per site or per deploy, from the site settings alone.
- **A tiny reverse-proxy** (any host) - HTTP Basic Auth in front of the
  static files (an `.htpasswd`-style rule, or a one-file
  Cloudflare Worker/Netlify Edge Function) is enough for "keep search
  engines and randoms out," though it's not real per-user identity the way
  a signed-in app would have.

None of these are bx-sites features - they're host-level settings you turn
on wherever `site/` ends up being served.
