---
title: Controller Scan Output Example
order: 6.7
icon: phosphor-duotone:signpost
tags: [guides, java, spring-boot, controller-scan, example]
---

# Controller Scan Output Example

A real example of what `bxSitesControllerScanDoc` (Gradle) /
`bxsites:controller-scan` (Maven) actually produces - see
[Gradle Plugin](gradle-plugin.md#spring-boot-doc-generation) or
[Maven Plugin](maven-plugin.md#spring-boot-doc-generation) for how to turn
this on in your own project (on by default when OpenAPI generation is off).

It reflection-scans your project's own compiled classes for Spring MVC
controllers in a forked JVM, using the real Spring annotation types on your
project's own classpath. Given this controller:

```java title="com/example/BookController.java"
package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    public String list() {
        return "[]";
    }

    @GetMapping("/{id}")
    public String getOne(@PathVariable String id) {
        return "{}";
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "{}";
    }
}
```

...the generator writes `api/controllers/com/example/BookController.md`
with this frontmatter:

```yaml title="Generated frontmatter"
---
title: "BookController"
tags: [api, controllers]
---
```

And this body, reproduced below not as a code block but rendered for real -
this *is* the generated page's actual body, live, confirmed against this
exact controller class in the plugin's own test suite. Try the GET chip,
or type "create" into the search box:

---

# BookController

`com.example.BookController`

<style>
.bx-filter-toolbar { display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem; margin: 1.5rem 0; }
.bx-filter-search { flex: 1 1 12rem; min-width: 8rem; padding: 0.4rem 0.75rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: var(--bxsites-bg); color: var(--bxsites-text); font-size: 0.9rem; }
.bx-filter-chip { padding: 0.35rem 0.9rem; border: 1px solid var(--bxsites-border); border-radius: 999px; background: transparent; color: var(--bxsites-muted); font-size: 0.85rem; cursor: pointer; transition: all 0.15s ease; }
.bx-filter-chip:hover { border-color: var(--bxsites-accent); color: var(--bxsites-text); }
.bx-filter-chip.bx-filter-chip-on { background: var(--bxsites-accent); border-color: var(--bxsites-accent); color: var(--bxsites-bg); }
</style>

<div class="bx-filter" x-data="{ q: '', k: 'all' }">

<div class="bx-filter-toolbar">
<input type="search" class="bx-filter-search" x-model="q" placeholder="Search...">
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='all' }" @click="k='all'">All</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='get' }" @click="k='get'">GET</button>
<button type="button" class="bx-filter-chip" :class="{ 'bx-filter-chip-on': k==='post' }" @click="k='post'">POST</button>
</div>

<div class="bxsites-table-wrap"><table class="table">
<thead><tr><th>Method</th><th>Path</th><th>Handler</th></tr></thead>
<tbody>
<tr data-k="get" data-n="/api/books String list()" x-show="(k==='all'||k===$el.dataset.k)&&(!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase()))"><td>GET</td><td><code>/api/books</code></td><td><code>String list()</code></td></tr>
<tr data-k="post" data-n="/api/books String create(String)" x-show="(k==='all'||k===$el.dataset.k)&&(!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase()))"><td>POST</td><td><code>/api/books</code></td><td><code>String create(String)</code></td></tr>
<tr data-k="get" data-n="/api/books/{id} String getOne(String)" x-show="(k==='all'||k===$el.dataset.k)&&(!q||$el.dataset.n.toLowerCase().includes(q.toLowerCase()))"><td>GET</td><td><code>/api/books/{id}</code></td><td><code>String getOne(String)</code></td></tr>
</tbody>
</table></div>

</div>

---

Notice the class-level `@RequestMapping("/api/books")` base path is combined
with each method's own mapping to produce the full path in each row - real
path composition, not string concatenation guesswork. Row order comes
straight from the JVM's own reflection over the compiled class (not
necessarily source order - real behavior, not tidied up for this example).
The toolbar/chips/search box above are real, working Alpine.js, built
entirely from plain HTML attributes and a small inline stylesheet driven by
this theme's own `--bxsites-*` variables, exactly like the Javadoc
generator's toolbar.

## What this doesn't cover

**Deliberately scoped down**, matching the same spirit as the Javadoc
generator: only classes directly annotated `@Controller`/`@RestController`
are recognized (a custom stereotype annotation built on either isn't); only
directly-annotated `@RequestMapping`/`@GetMapping`/`@PostMapping`/
`@PutMapping`/`@DeleteMapping`/`@PatchMapping` methods are recognized;
nested classes are skipped; and since reflection has no access to
source-level doc comments, there's no per-endpoint description text like
"List books" next to a row above - only the method, path, and handler
signature. See the Gradle/Maven guides linked above for the full scope.
