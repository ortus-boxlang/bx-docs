---
title: Ejemplo de salida del escaneo de controladores
order: 6.7
icon: phosphor-duotone:signpost
tags: [guías, java, spring-boot, controller-scan, ejemplo]
---

# Ejemplo de salida del escaneo de controladores

Un ejemplo real de lo que `bxSitesControllerScanDoc` (Gradle) /
`bxsites:controller-scan` (Maven) produce realmente - ver
[Plugin de Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin de Maven](maven-plugin.md#spring-boot-doc-generation) para saber
cómo activar esto en tu propio proyecto (activado por defecto cuando la
generación de OpenAPI está desactivada).

Escanea por reflexión las clases ya compiladas de tu propio proyecto en
busca de controladores Spring MVC, en una JVM bifurcada, usando los tipos
de anotación reales de Spring en el classpath de tu propio proyecto. Dado
este controlador:

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

...el generador escribe `api/controllers/com/example/BookController.md`
con este frontmatter:

```yaml title="Frontmatter generado"
---
title: "BookController"
tags: [api, controllers]
---
```

Y este cuerpo, reproducido abajo no como un bloque de código sino
renderizado de verdad - este *es* el cuerpo real de la página generada,
en vivo, confirmado contra esta misma clase controladora en la propia
suite de pruebas del plugin:

---

# BookController

`com.example.BookController`

| Method | Path | Handler |
|---|---|---|
| GET | `/api/books` | `String list()` |
| GET | `/api/books/{id}` | `String getOne(String)` |
| POST | `/api/books` | `String create(String)` |

---

Fíjate en que la ruta base a nivel de clase `@RequestMapping("/api/books")`
se combina con el mapeo propio de cada método para producir la ruta
completa en cada fila - composición real de rutas, no una concatenación
de cadenas por conjetura.

## Lo que esto no cubre

**Alcance deliberadamente reducido**, en el mismo espíritu que el
generador de Javadoc: solo se reconocen las clases anotadas directamente
con `@Controller`/`@RestController` (una anotación de estereotipo propia
construida sobre cualquiera de las dos no se reconoce); solo se reconocen
los métodos anotados directamente con
`@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/
`@DeleteMapping`/`@PatchMapping`; las clases anidadas se omiten; y como la
reflexión no tiene acceso a los comentarios de documentación a nivel de
código fuente, no hay texto descriptivo por endpoint como "List books" en
la fila de arriba - solo el método, la ruta y la firma del manejador.
Consulta las guías de Gradle/Maven enlazadas arriba para el alcance
completo.
