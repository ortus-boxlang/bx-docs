---
title: Esempio di output della scansione controller
order: 6.7
icon: phosphor-duotone:signpost
tags: [guide, java, spring-boot, controller-scan, esempio]
---

# Esempio di output della scansione controller

Un esempio reale di ciò che `bxSitesControllerScanDoc` (Gradle) /
`bxsites:controller-scan` (Maven) produce davvero - vedi
[Plugin Gradle](gradle-plugin.md#spring-boot-doc-generation) o
[Plugin Maven](maven-plugin.md#spring-boot-doc-generation) per sapere come
attivarlo nel tuo progetto (attivo di default quando la generazione
OpenAPI è disattivata).

Esegue una scansione per reflection delle classi già compilate del tuo
progetto, in una JVM biforcata, alla ricerca di controller Spring MVC,
usando i veri tipi di annotazione Spring sul classpath del tuo progetto.
Dato questo controller:

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

...il generatore scrive `api/controllers/com/example/BookController.md`
con questo frontmatter:

```yaml title="Frontmatter generato"
---
title: "BookController"
tags: [api, controllers]
---
```

E questo corpo, riprodotto qui sotto non come un blocco di codice ma
renderizzato per davvero - questo *è* il corpo reale della pagina
generata, live, confermato proprio contro questa classe controller nella
suite di test del plugin:

---

# BookController

`com.example.BookController`

| Method | Path | Handler |
|---|---|---|
| GET | `/api/books` | `String list()` |
| POST | `/api/books` | `String create(String)` |
| GET | `/api/books/{id}` | `String getOne(String)` |

---

Nota come il percorso base a livello di classe `@RequestMapping("/api/books")`
venga combinato con il mapping proprio di ciascun metodo per produrre il
percorso completo in ogni riga - vera composizione dei percorsi, non una
concatenazione di stringhe indovinata. L'ordine delle righe arriva
direttamente dalla reflection della JVM sulla classe compilata (non
necessariamente l'ordine del sorgente - comportamento reale, non sistemato
per questo esempio).

Gli endpoint sono resi come una semplice tabella pipe Markdown, di
proposito. bx-sites dà a ogni tabella da dieci righe in su un proprio
campo di filtro dal vivo, così un controller con una lunga lista di
endpoint ottiene ricerca e filtro gratis, mentre una tabella pipe resta
leggibile nel Markdown grezzo, viene tematizzata ovunque ed entra per
intero nell'indice di ricerca. Le pagine [Javadoc](java-javadoc-example.md),
i cui membri sono sezioni e non righe di tabella, portano invece la barra
di chip Alpine.js del generatore stesso.

## Cosa non copre

**Ambito deliberatamente ridotto**, nello stesso spirito del generatore
Javadoc: vengono riconosciute solo le classi annotate direttamente con
`@Controller`/`@RestController` (una propria annotazione stereotipo
costruita su una delle due non viene riconosciuta); vengono riconosciuti
solo i metodi annotati direttamente con
`@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/
`@DeleteMapping`/`@PatchMapping`; le classi annidate vengono saltate; e
poiché la reflection non ha accesso ai commenti di documentazione a
livello di codice sorgente, non c'è alcun testo descrittivo per singolo
endpoint come "List books" nella riga sopra - solo metodo, percorso e
firma dell'handler. Consulta le guide Gradle/Maven collegate sopra per
l'ambito completo.
