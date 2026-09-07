---
title: Maven-Plugin
order: 6.4
icon: phosphor-duotone:puzzle-piece
tags: [anleitungen, java, maven, integration]
---

# Maven-Plugin

Java- und Spring-Boot-Entwickler brauchen weder CommandBox noch eine
systemweite BoxLang-Installation, um einem eigenen Projekt eine
bx-sites-Website hinzuzufügen - das Maven-Plugin
`io.boxlang:bxsites-maven-plugin` lädt beim ersten Lauf alles Nötige (die
BoxLang-Laufzeitumgebung und bx-sites selbst) in einen lokalen Cache
herunter. Die einzige Voraussetzung ist ein JDK 21. Es ist das
Maven-seitige Gegenstück zum [Gradle-Plugin](gradle-plugin.md) - beide
kapseln dieselbe zugrunde liegende Logik, sodass Verb-Abdeckung und
Verhalten zwischen den beiden Build-Tools identisch bleiben.

> **Status:** Vor Version 1.0, noch nicht in Maven Central veröffentlicht
> - siehe
> [`maven-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/maven-plugin)
> im bx-sites-Repository für den Quellcode und die aktuellen
> Build-/Testanweisungen. Diese Seite beschreibt, was das Plugin nach der
> Veröffentlichung leistet; die unten beschriebene Funktionsweise ist
> bereits real und verifiziert, nur eben noch nicht als
> Maven-Central-Koordinate verfügbar.

## Schnellstart

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
mvn bxsites:new     # erzeugt docs/ + bxsites.yaml
mvn bxsites:build   # rendert docs/**.md nach site/
mvn bxsites:serve   # baut + serviert lokal mit Live-Reload
```

Die kurze Form `bxsites:<goal>` (nachweislich funktionsfähig) setzt
voraus, dass der obige `<plugin>`-Block konkret unter
`<build><plugins>` steht, nicht nur unter `<pluginManagement>` - erst das
registriert `io.boxlang` als auflösbares Goal-Präfix für das aktuelle
Projekt. Ist er dort nicht eingetragen, die vollqualifizierte Form
verwenden: `mvn io.boxlang:bxsites-maven-plugin:build`.

Ein Standard-Setup benötigt keine weitere Konfiguration - das Plugin
erkennt das Content-Verzeichnis (`docs/`, sonst `src/`) und das
Ausgabeverzeichnis (immer `<projectRoot>/site/`) automatisch. Aussehen,
Theme, Navigation und jede andere Einstellung der eigenen Website werden
komplett über `bxsites.yaml`/`.toml`/`.json` im Projektroot gesteuert,
genau wie unter [Konfiguration](../configuration.md) dokumentiert - das
Plugin duplitziert dieses Schema nirgendwo, es regelt nur *wie* und
*wann* bx-sites aus dem eigenen Build heraus läuft.

## Goals

| Goal | Was es macht |
|---|---|
| `bxsites:new` | Erzeugt ein neues bx-sites-Projekt (Content-Verzeichnis + Konfigurationsdatei). |
| `bxsites:build` | Rendert die Website nach `<projectRoot>/site/`. |
| `bxsites:serve` | Baut die Website und serviert sie lokal mit Live-Reload. Läuft im Vordergrund, bis man ihn stoppt (Strg+C). |
| `bxsites:clean` | Entfernt `<projectRoot>/site/`. Reines Verzeichnis-Löschen - kein Subprozess. |

Jedes Goal provisioniert (lädt herunter/cacht) selbst, was es braucht,
beim ersten Lauf - anders als beim Gradle-Plugin gibt es kein separates
"Provision"-Goal, das vorher laufen müsste.

Standardmäßig ist kein Goal an eine Maven-Lifecycle-Phase gebunden -
führe sie explizit aus. Falls `bxsites:build` automatisch laufen soll,
binde es selbst in einem `<executions>`-Block ein, z. B. an `pre-site`
(eine naheliegende Kombination mit Mavens eigenem `site`-Lifecycle).

## Konfiguration

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

Jeder Parameter hat einen sinnvollen Default - ein frisches Projekt muss
keinen davon setzen. Das Ausgabeverzeichnis ist hier gar nicht
einstellbar - bx-sites selbst legt es fest auf `<projectRoot>/site/`,
daher leitet das Plugin es nur ab, statt eine Einstellung anzubieten, die
ohnehin nicht beachtet würde.

## Was noch nicht gebaut ist

- **Spring-Boot-Doku-Generierung** (OpenAPI, Javadoc, Controller-Scan) - geplant.
- **Live-Output-Streaming von `bxsites:serve`** - puffert derzeit die Ausgabe mit einem 30-Minuten-Timeout, beides falsch für ein Goal, das unbegrenzt laufen soll.
- **Up-to-date-/Staleness-Prüfung** - Maven hat keine Gradle-artige, eingebaute inkrementelle Build-Engine; `bxsites:build` läuft derzeit bei jedem Aufruf komplett neu, statt zu überspringen, wenn sich nichts geändert hat (das Gradle-Plugin hat für `bxSitesBuild` bereits eine echte Up-to-date-Prüfung).
- Wrapper-Goals für die übrigen bx-sites-Verben (`deploy`, `publish`, `package`, `lint`, `check`, usw.) über die vier oben genannten Kern-Goals hinaus.

Siehe den Guide zum [Gradle-Plugin](gradle-plugin.md) für das Gegenstück
auf der Gradle-Seite - beide Plugins kapseln dieselbe zugrunde liegende
Logik, sodass Verb-Abdeckung und Verhalten zwischen den beiden Build-Tools
identisch bleiben.
