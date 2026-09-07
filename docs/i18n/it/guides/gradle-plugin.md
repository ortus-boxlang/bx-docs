---
title: Plugin Gradle
order: 6.3
icon: phosphor-duotone:gear-six
tags: [guide, java, gradle, integration]
---

# Plugin Gradle

Gli sviluppatori Java e Spring Boot non hanno bisogno di CommandBox né di
un'installazione di BoxLang a livello di sistema per aggiungere un sito
bx-sites al proprio progetto - il plugin Gradle `io.boxlang.bxsites`
scarica tutto ciò di cui ha bisogno (il runtime BoxLang e bx-sites stesso)
in una cache locale la prima volta che viene eseguito. L'unico requisito è
un JDK 21.

> **Stato:** pre-1.0, non ancora pubblicato sul Gradle Plugin Portal -
> vedi
> [`gradle-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/gradle-plugin)
> nel repository bx-sites per il codice sorgente e le istruzioni attuali
> di build/test. Questa pagina documenta cosa farà una volta pubblicato;
> i meccanismi descritti qui sotto sono già reali e verificati, solo non
> ancora disponibili come dipendenza `plugins { }` a riga singola.

## Avvio rapido

```kotlin title="build.gradle.kts"
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # crea docs/ + bxsites.yaml
./gradlew bxSitesBuild   # renderizza docs/**.md in site/
./gradlew bxSitesServe   # compila + serve localmente con live reload
```

Una configurazione predefinita non richiede altro - il plugin rileva
automaticamente la directory dei contenuti (`docs/`, altrimenti `src/`) e
la directory di output (sempre `<projectRoot>/site/`). L'aspetto, il
tema, la nav e qualsiasi altra impostazione del proprio sito sono
controllati interamente da `bxsites.yaml`/`.toml`/`.json` nella root del
progetto, esattamente come documentato in
[Configurazione](../configuration.md) - il plugin non duplica mai quello
schema, si limita a gestire *come* e *quando* bx-sites viene eseguito dal
tuo build.

## Task

| Task | Cosa fa |
|---|---|
| `bxSitesNew` | Genera un nuovo progetto bx-sites. Non collegato ad alcun lifecycle - eseguilo una volta, esplicitamente. |
| `bxSitesBuild` | Renderizza il sito. Controllo reale di aggiornamento: viene rieseguito solo quando i contenuti, la configurazione o le versioni fissate cambiano davvero. |
| `bxSitesServe` | Compila e serve il sito localmente con live reload. Viene eseguito in foreground finché non viene interrotto. |
| `bxSitesClean` | Rimuove la directory `site/` generata. |

`bxSitesBuild` non viene mai eseguito automaticamente come parte di
`assemble` a meno che non lo si attivi esplicitamente (vedi
`hookIntoAssemble` più sotto) - un build della documentazione è
un'attività distinta, spesso più lenta, rispetto alla compilazione del
codice vero e proprio.

## Configurazione

```kotlin title="build.gradle.kts"
bxSites {
    projectRoot.set(layout.projectDirectory)
    boxlangMiniserverVersion.set("1.18.0-snapshot")   // versione fissata del runtime BoxLang
    bxSitesVersion.set("1.0.0-snapshot")               // versione fissata di bx-sites
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                        // opzionale: esegue bxSitesBuild come parte di assemble
    hookIntoCheck.set(true)                            // verbi lint/check attivi di default (in arrivo, non ancora implementato)
}
```

Ogni proprietà ha un default sensato. La directory di output non è
affatto configurabile qui - bx-sites stesso la fissa a
`<projectRoot>/site/`, quindi il plugin la deriva invece di esporre
un'impostazione che comunque non verrebbe rispettata.

## Cosa non è ancora stato costruito

- **Generazione di documentazione per Spring Boot** (OpenAPI, Javadoc, scansione dei controller) - pianificato.
- **Lo streaming dell'output live di `bxSitesServe`** - attualmente bufferizza l'output con un timeout di 30 minuti, entrambi sbagliati per un task pensato per l'esecuzione indefinita.
- Task wrapper per gli altri verbi di bx-sites (`deploy`, `publish`, `package`, `lint`, `check`, ecc.) oltre ai quattro principali sopra.

Vedi la guida al [Plugin Maven](maven-plugin.md) per l'equivalente sul
lato Maven - entrambi i plugin racchiudono la stessa logica sottostante,
quindi la copertura dei verbi e il comportamento restano identici tra i
due build tool.
