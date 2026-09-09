---
title: Responsive Images
order: 5
icon: phosphor-duotone:image
tags: [guides, images, performance]
---

# Responsive Images

Every eligible image under `docs/assets/` gets resized/WebP variants
generated automatically, and every matching `<img>` in your pages gets
rewritten into a responsive `<picture>` - no new Markdown syntax, no
config needed to turn it on. It's built on
[bx-image](https://github.com/ortus-boxlang/bx-image), a required
dependency alongside bx-markdown/bx-esapi/bx-yaml (see
[Getting Started](../getting-started.md#install)).

## How it works

Write an image the normal way - Markdown syntax or raw HTML, file-relative
to the page just like a [page link](markdown.md) already works:

```markdown title="Example"
![A freshly built site](../assets/screenshot.png)
```

At build time, `screenshot.png` gets resized down to every configured
width narrower than its own (never upscaled), plus a same-size WebP
re-encode, and the built page gets:

```html title="Rendered output" linenums="1"
<picture>
	<source type="image/webp" srcset="/assets/screenshot-400w.a3f9c2e1.webp 400w, /assets/screenshot-800w.a3f9c2e1.webp 800w, ...">
	<img src="/assets/screenshot.png" srcset="/assets/screenshot-400w.a3f9c2e1.png 400w, /assets/screenshot-800w.a3f9c2e1.png 800w, ..." sizes="(min-width: 800px) 800px, 100vw" alt="A freshly built site">
</picture>
```

A browser picks the smallest variant that satisfies `sizes`, in WebP when
it supports the format, falling back to the plain original `src` (still
served exactly as before) otherwise. Every other attribute you wrote -
`alt`, `class`, anything else - is carried over onto the rewritten `<img>`
untouched.

An image with no configured width narrower than its own (a small icon,
say) still gets a full-size WebP re-encode when `"webp"` is in
`assets.images.formats` - a real file-size win even with no responsive
breakpoint to offer.

## Captions, alignment and framing

A caption, a frame, or a multi-image gallery are all just block-level
HTML - which bx-markdown/Flexmark passes through completely untouched
(CommonMark's own "HTML block" rule), so no bx-sites-specific syntax is
needed at all:

```markdown title="Example" linenums="1"
<figure>
  <img src="../assets/screenshot.png" alt="The build output">
  <figcaption>A freshly built site</figcaption>
</figure>

<div data-with-frame="true">
  <img src="../assets/screenshot.png" alt="Framed">
</div>

<div class="bxsites-gallery">
  <img src="../assets/one.png" alt="">
  <img src="../assets/two.png" alt="">
  <img src="../assets/three.png" alt="">
</div>
```

The same is true of `x-data`/`x-show`/`@click` and any other Alpine.js
attribute - see [Interactivity with Alpine.js](interactivity.md).

## What doesn't get resized

- **SVGs** - already resolution-independent, copied through unchanged.
- **Animated GIFs** - bx-image's resize path is frame-unaware; resizing
  one would flatten it to a single frame. Copied through unchanged,
  exactly as before this feature existed.
- **Anything outside `docs/assets/`** - a remote image URL
  (`<img src="https://...">`) is left completely untouched, the same way
  [`extraCss`/`extraJs`](../configuration.md#extracss--extrajs) already
  treat an absolute URL as "used as-is."
- **An image already narrower than every configured width** - nothing to
  generate; the plain `<img>` renders exactly as it did before, unless
  `"webp"` is enabled (see above).

There's also no AVIF support yet - bx-image doesn't write that format as
of this writing. WebP alone still gets most of the size win, with far
broader tooling/browser support; this is worth revisiting if bx-image
adds AVIF upstream.

## Turning it off

=== "YAML"
    ```yaml title="bxsites.yaml"
    assets: { images: { enabled: false } }
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "assets": { "images": { "enabled": false } } }
    ```

=== "TOML"
    ```toml title="bxsites.toml"

    [assets]

    [assets.images]
    enabled = false
    ```

Falls back to plain, unprocessed `docs/assets/**` copying - exactly how
every image was handled before this feature existed.

## Choosing your own breakpoints

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    assets:
      images:
        widths: [ 480, 960, 1440 ]
        formats: [ webp ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"assets": {
    		"images": {
    			"widths": [480, 960, 1440],
    			"formats": ["webp"]
    		}
    	}
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"

    [assets]

    [assets.images]
    widths = [ 480, 960, 1440 ]
    formats = [ "webp" ]
    ```

`widths` defaults to `[400, 800, 1200, 1600]`; `formats` defaults to
`["original", "webp"]` - drop `"original"` to skip generating resized
copies in the source format at all (still keeping the plain, full-size
original as the `<img>` fallback), or drop `"webp"` to skip the WebP
`<source>` entirely. See [Configuration](../configuration.md#assets) for
every `assets.images` key.

## CSS/JS bundling

`extraCss`/`extraJs` get bundled the same way, on by default
(`assets.bundle`):

=== "YAML"
    ```yaml title="bxsites.yaml" linenums="1"
    extraCss: [ assets/a.css, assets/b.css ]
    extraJs: [ assets/app.js ]
    ```

=== "JSON"
    ```json title="bxsites.json" linenums="1"
    {
    	"extraCss": ["assets/a.css", "assets/b.css"],
    	"extraJs": ["assets/app.js"]
    }
    ```

=== "TOML"
    ```toml title="bxsites.toml" linenums="1"
    extraCss = [ "assets/a.css", "assets/b.css" ]
    extraJs = [ "assets/app.js" ]
    ```

builds one fingerprinted `assets/bundle.<hash>.css` (in the order
listed) and one `assets/bundle.<hash>.js`, instead of one `<link>`/
`<script>` tag per entry. CSS gets its comments stripped and whitespace
collapsed; JS deliberately only gets safe, structural whitespace
cleanup - never comment stripping, since a naive regex has no way to
tell a `//` inside a string (`"http://example.com"`) from a real
comment, and getting that wrong would silently corrupt a project's own
script. This is bundling and light cleanup, not a true minifier - a
vendored Java minification library is a reasonable later upgrade if
this isn't enough.

Bundling only ever activates when *every* entry in the list is a local
project file. One external URL (a CDN link) mixed in falls the whole
list back to today's exact per-URL behavior, rather than risk silently
reordering a CSS cascade a project depended on:

=== "YAML"
    ```yaml title="bxsites.yaml"
    extraCss: [ assets/custom.css, "https://cdn.example.com/lib.css" ]
    ```

=== "JSON"
    ```json title="bxsites.json"
    { "extraCss": ["assets/custom.css", "https://cdn.example.com/lib.css"] }
    ```

=== "TOML"
    ```toml title="bxsites.toml"
    extraCss = [ "assets/custom.css", "https://cdn.example.com/lib.css" ]
    ```

renders two separate `<link>` tags, unbundled, exactly as before this
feature existed.

## Fingerprinting and caching

Every generated image variant and CSS/JS bundle is content-hash-named
(`assets.fingerprint`, on by default) - a build only ever changes a
variant's own filename when its source content actually changes, which
is what makes a far-future `Cache-Control` header safe to set on a
static host. A project's own original files under `docs/assets/` keep
their plain names untouched either way - only pipeline-generated output
gets fingerprinted, so a `::: file` download card or a raw link to an
image by its own filename keeps working exactly as it always has.

Every generated variant is cached on disk under a project's own
`.cache/images/` (removed by [`bxSites clean`](../cli-reference.md#clean),
alongside `site/`) - keyed by the *source* image's own content hash, so
re-running `build` (once per version/locale tree, all sharing the same
`docs/assets/`) or `bxSites serve` after an unrelated edit doesn't
re-decode/re-resize/re-encode every screenshot in the project, only ones
that actually changed.
