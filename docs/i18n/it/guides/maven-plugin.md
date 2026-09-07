---
title: Plugin Maven
order: 6.4
icon: phosphor-duotone:puzzle-piece
tags: [guide, java, maven, integration]
---

# Plugin Maven

Gli sviluppatori Java e Spring Boot non hanno bisogno di CommandBox né di
un'installazione di BoxLang a livello di sistema per aggiungere un sito
bx-sites al proprio progetto - il plugin Maven
`io.boxlang:bxsites-maven-plugin` scarica tutto ciò di cui ha bisogno (il
runtime BoxLang e bx-sites stesso) in una cache locale la prima volta che
viene eseguito. L'unico requisito è un JDK 21. È la controparte Maven del
[Plugin Gradle](gradle-plugin.md) - entrambi racchiudono la stessa logica
sottostante, quindi la copertura dei verbi e il comportamento restano
identici tra i due build tool.

> **Stato:** pre-1.0, non ancora pubblicato su Maven Central - vedi
> [`maven-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/maven-plugin)
> nel repository bx-sites per il codice sorgente e le istruzioni attuali
> di build/test. Questa pagina documenta cosa farà una volta pubblicato;
> i meccanismi descritti qui sotto sono già reali e verificati, solo non
> ancora disponibili come coordinata Maven Central.

## Avvio rapido

```xml title="pom.xml"
<build>
  <plugins>
    <plugin>
      <groupId>io.boxlang</groupId>
      <artifactId>bxsites-maven-plugin</artifactId>
      <version>&lt;version&gt;</version>
    </plugin>
  </plugins>
</build>
```

```bash
mvn bxsites:new     # crea docs/ + bxsites.yaml
mvn bxsites:build   # renderizza docs/**.md in site/
mvn bxsites:serve   # compila + serve localmente con live reload
```

La forma breve `bxsites:<goal>` (confermata funzionante) richiede che il
blocco `<plugin>` sopra sia dichiarato specificamente sotto
`<build><plugins>`, non solo in `<pluginManagement>` - è questo che
registra `io.boxlang` come prefisso di goal risolvibile per il progetto
corrente. Senza quella dichiarazione, usa la forma completamente
qualificata: `mvn io.boxlang:bxsites-maven-plugin:build`.

Una configurazione predefinita non richiede altro - il plugin rileva
automaticamente la directory dei contenuti (`docs/`, altrimenti `src/`) e
la directory di output (sempre `<projectRoot>/site/`). L'aspetto, il
tema, la nav e qualsiasi altra impostazione del proprio sito sono
controllati interamente da `bxsites.yaml`/`.toml`/`.json` nella root del
progetto, esattamente come documentato in
[Configurazione](../configuration.md) - il plugin non duplica mai quello
schema, si limita a gestire *come* e *quando* bx-sites viene eseguito dal
tuo build.

## Goal

| Goal | Cosa fa |
|---|---|
| `bxsites:new` | Genera un nuovo progetto bx-sites (directory dei contenuti + file di configurazione). |
| `bxsites:build` | Renderizza il sito in `<projectRoot>/site/`. |
| `bxsites:serve` | Compila e serve il sito localmente con live reload. Viene eseguito in foreground finché non lo interrompi (Ctrl+C). |
| `bxsites:clean` | Rimuove `<projectRoot>/site/`. Semplice cancellazione di directory - nessun sottoprocesso. |

Ogni goal si autoprovvisiona (scarica/cache) ciò di cui ha bisogno, alla
prima esecuzione - a differenza del plugin Gradle, non esiste un goal di
"provisioning" separato da eseguire prima.

Per default nessun goal è collegato a nessuna fase del lifecycle Maven -
eseguili esplicitamente. Se vuoi che `bxsites:build` venga eseguito
automaticamente, collegalo tu stesso in un blocco `<executions>`, ad
esempio a `pre-site` (un abbinamento naturale con il lifecycle `site`
integrato di Maven).

## Configurazione

```xml title="pom.xml"
<plugin>
  <groupId>io.boxlang</groupId>
  <artifactId>bxsites-maven-plugin</artifactId>
  <configuration>
    <projectRoot>${project.basedir}</projectRoot>
    <boxlangMiniserverVersion>1.18.0-snapshot</boxlangMiniserverVersion>
    <bxSitesVersion>1.0.0-snapshot</bxSitesVersion>
    <boxlangHomeDir>${project.build.directory}/bxsites/boxlang-home</boxlangHomeDir>
  </configuration>
</plugin>
```

Ogni parametro ha un default sensato - un progetto nuovo non deve
impostarne nessuno. La directory di output non è affatto configurabile
qui - bx-sites stesso la fissa a `<projectRoot>/site/`, quindi il plugin
la deriva invece di esporre un'impostazione che comunque non verrebbe
rispettata.

## Cosa non è ancora stato costruito

- **Generazione di documentazione per Spring Boot** (OpenAPI, Javadoc, scansione dei controller) - pianificato.
- **Lo streaming dell'output live di `bxsites:serve`** - attualmente bufferizza l'output con un timeout di 30 minuti, entrambi sbagliati per un goal pensato per l'esecuzione indefinita.
- **Controllo di aggiornamento/staleness** - Maven non ha un motore di build incrementale integrato in stile Gradle; `bxsites:build` attualmente riesegue l'intero build a ogni invocazione invece di saltarlo quando nulla è cambiato (il plugin Gradle ha invece un controllo reale di aggiornamento per `bxSitesBuild`).
- Goal wrapper per gli altri verbi di bx-sites (`deploy`, `publish`, `package`, `lint`, `check`, ecc.) oltre ai quattro principali sopra.

Vedi la guida al [Plugin Gradle](gradle-plugin.md) per l'equivalente sul
lato Gradle - entrambi i plugin racchiudono la stessa logica sottostante,
quindi la copertura dei verbi e il comportamento restano identici tra i
due build tool.
