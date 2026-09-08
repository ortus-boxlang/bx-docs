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
| `bxsites:build` | Rendert die Website nach `<projectRoot>/site/`. Überspringt den Subprozess, wenn sich seit dem letzten Build nichts unter dem Content-Verzeichnis oder in der Konfigurationsdatei geändert hat - siehe [Staleness-Prüfung beim Build](#staleness-prufung-beim-build) unten. |
| `bxsites:serve` | Baut die Website und serviert sie lokal mit Live-Reload. Läuft im Vordergrund, bis man ihn stoppt (Strg+C). |
| `bxsites:clean` | Entfernt `<projectRoot>/site/`. Reines Verzeichnis-Löschen - kein Subprozess. |
| `bxsites:search-index` | Baut `site/search-index.json` neu, ohne einen vollständigen Site-Build. |
| `bxsites:lint` | Prüft die Markdown-Quellen unter docs/. |
| `bxsites:deploy` | Baut die Website und deployt sie zum konfigurierten Ziel. |
| `bxsites:publish` | Baut die Website und veröffentlicht sie in bxSites Cloud. |
| `bxsites:package` | Baut die Website und packt sie als `site.zip`. |
| `bxsites:stats` | Meldet Seiten-/Wortzahlen und weitere Statistiken zur gebauten Website. |
| `bxsites:doctor` | Führt bx-sites' eigene Projekt-Diagnose aus. |

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

## Staleness-Prüfung beim Build

Maven hat keine Gradle-artige, eingebaute inkrementelle Build-Engine,
daher implementiert `bxsites:build` eine eigene, leichtgewichtige
Prüfung: Der neueste Änderungszeitpunkt (mtime) unter dem
Content-Verzeichnis (plus der Konfigurationsdatei, falls vorhanden) wird
mit dem neuesten Zeitpunkt verglichen, der bereits in
`<projectRoot>/site/` vorhanden ist. Ist nichts neuer, protokolliert das
Goal, dass es übersprungen wird, und kehrt zurück, ohne bx-sites
überhaupt erneut aufzurufen. Einen Rebuild trotzdem erzwingen:

```bash
mvn bxsites:build -Dbxsites.build.forceRebuild=true
```

## Was noch nicht gebaut ist

- **Spring-Boot-Doku-Generierung** (OpenAPI, Javadoc, Controller-Scan) - geplant.
- **Live-Output-Streaming von `bxsites:serve`** - puffert derzeit die Ausgabe mit einem 30-Minuten-Timeout, beides falsch für ein Goal, das unbegrenzt laufen soll.

Siehe den Guide zum [Gradle-Plugin](gradle-plugin.md) für das Gegenstück
auf der Gradle-Seite - beide Plugins kapseln dieselbe zugrunde liegende
Logik, sodass Verb-Abdeckung und Verhalten zwischen den beiden Build-Tools
identisch bleiben.
