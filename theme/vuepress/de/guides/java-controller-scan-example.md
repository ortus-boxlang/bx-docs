---
title: Controller-Scan-Ausgabebeispiel
order: 6.7
icon: phosphor-duotone:signpost
tags: [anleitungen, java, spring-boot, controller-scan, beispiel]
---

# Controller-Scan-Ausgabebeispiel

Ein reales Beispiel dessen, was `bxSitesControllerScanDoc` (Gradle) /
`bxsites:controller-scan` (Maven) tatsächlich erzeugt - siehe
[Gradle-Plugin](gradle-plugin.md#spring-boot-doc-generation) oder
[Maven-Plugin](maven-plugin.md#spring-boot-doc-generation), um das im
eigenen Projekt zu aktivieren (standardmäßig aktiv, wenn die
OpenAPI-Generierung ausgeschaltet ist).

Er durchsucht die bereits kompilierten Klassen des eigenen Projekts per
Reflection in einer abgespaltenen JVM nach Spring-MVC-Controllern, unter
Verwendung der echten Spring-Annotationstypen auf dem eigenen Classpath
des Projekts. Bei diesem Controller:

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

...schreibt der Generator `api/controllers/com/example/BookController.md`
mit diesem Frontmatter:

```yaml title="Erzeugtes Frontmatter"
---
title: "BookController"
tags: [api, controllers]
---
```

Und diesem Body, hier nicht als Codeblock, sondern echt gerendert - das
*ist* der tatsächliche Body der erzeugten Seite, live, bestätigt anhand
genau dieser Controller-Klasse in der eigenen Testsuite des Plugins:

---

# BookController

`com.example.BookController`

| Method | Path | Handler |
|---|---|---|
| GET | `/api/books` | `String list()` |
| GET | `/api/books/{id}` | `String getOne(String)` |
| POST | `/api/books` | `String create(String)` |

---

Man beachte: Der klassenweite Basispfad `@RequestMapping("/api/books")`
wird mit dem jeweils eigenen Mapping jeder Methode kombiniert, um den
vollständigen Pfad in jeder Zeile zu erzeugen - echte Pfadzusammensetzung,
kein geratenes String-Zusammenfügen.

## Was das nicht abdeckt

**Bewusst reduzierter Umfang**, im gleichen Geist wie beim
Javadoc-Generator: Nur direkt mit `@Controller`/`@RestController`
annotierte Klassen werden erkannt (eine eigene Stereotyp-Annotation, die
auf einer der beiden aufbaut, nicht); nur direkt annotierte
`@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/
`@DeleteMapping`/`@PatchMapping`-Methoden werden erkannt; verschachtelte
Klassen werden übersprungen; und da Reflection keinen Zugriff auf
Doc-Kommentare auf Quellcode-Ebene hat, gibt es keinen
Beschreibungstext pro Endpunkt wie "List books" in der Zeile oben - nur
Methode, Pfad und Handler-Signatur. Siehe die oben verlinkten
Gradle-/Maven-Guides für den vollständigen Umfang.
