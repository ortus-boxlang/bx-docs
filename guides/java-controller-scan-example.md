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
exact controller class in the plugin's own test suite:

---

# BookController

`com.example.BookController`

| Method | Path | Handler |
|---|---|---|
| GET | `/api/books` | `String list()` |
| GET | `/api/books/{id}` | `String getOne(String)` |
| POST | `/api/books` | `String create(String)` |

---

Notice the class-level `@RequestMapping("/api/books")` base path is combined
with each method's own mapping to produce the full path in each row - real
path composition, not string concatenation guesswork.

## What this doesn't cover

**Deliberately scoped down**, matching the same spirit as the Javadoc
generator: only classes directly annotated `@Controller`/`@RestController`
are recognized (a custom stereotype annotation built on either isn't); only
directly-annotated `@RequestMapping`/`@GetMapping`/`@PostMapping`/
`@PutMapping`/`@DeleteMapping`/`@PatchMapping` methods are recognized;
nested classes are skipped; and since reflection has no access to
source-level doc comments, there's no per-endpoint description text like
the row above showing "List books" - only the method, path, and handler
signature. See the Gradle/Maven guides linked above for the full scope.
