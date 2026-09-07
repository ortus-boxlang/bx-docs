---
title: Gradle-Plugin
order: 6.3
icon: phosphor-duotone:gear-six
tags: [anleitungen, java, gradle, integration]
---

# Gradle-Plugin

Java- und Spring-Boot-Entwickler brauchen weder CommandBox noch eine
systemweite BoxLang-Installation, um dem eigenen Java-Projekt eine
bx-sites-Website hinzuzufügen - das Gradle-Plugin `io.boxlang.bxsites`
lädt beim ersten Lauf alles Nötige (die BoxLang-Laufzeitumgebung und
bx-sites selbst) in einen lokalen Cache herunter. Die einzige
Voraussetzung ist ein JDK 21.

> **Status:** Vor Version 1.0, noch nicht im Gradle Plugin Portal
> veröffentlicht - siehe
> [`gradle-plugin/`](https://github.com/ortus-boxlang/bx-sites/tree/development/gradle-plugin)
> im bx-sites-Repository für den Quellcode und die aktuellen
> Build-/Testanweisungen. Diese Seite beschreibt, was das Plugin nach der
> Veröffentlichung leistet; die unten beschriebene Funktionsweise ist
> bereits real und verifiziert, nur eben noch nicht als
> One-Line-`plugins { }`-Abhängigkeit verfügbar.

## Schnellstart

```kotlin title="build.gradle.kts"
plugins {
    id("io.boxlang.bxsites") version "<version>"
}
```

```bash
./gradlew bxSitesNew    # erzeugt docs/ + bxsites.yaml
./gradlew bxSitesBuild   # rendert docs/**.md nach site/
./gradlew bxSitesServe   # baut + serviert lokal mit Live-Reload
```

Ein Standard-Setup benötigt keine weitere Konfiguration - das Plugin
erkennt das Content-Verzeichnis (`docs/`, sonst `src/` - außer in einem
Projekt mit angewendetem Java-Plugin, wo `src/` das eigene Java-Quellverzeichnis
ist und niemals als bx-sites-Content verwendet wird) und das
Ausgabeverzeichnis (immer `<projectRoot>/site/`) automatisch. Aussehen,
Theme, Navigation und jede andere Einstellung der eigenen Website werden
komplett über `bxsites.yaml`/`.toml`/`.json` im Projektroot gesteuert,
genau wie unter [Konfiguration](../configuration.md) dokumentiert - das
Plugin duplitziert dieses Schema nirgendwo, es regelt nur *wie* und
*wann* bx-sites aus dem eigenen Build heraus läuft.

## Tasks

| Task | Was er macht |
|---|---|
| `bxSitesNew` | Erzeugt ein neues bx-sites-Projekt. In keinen Lifecycle eingebunden - einmal explizit ausführen. |
| `bxSitesBuild` | Rendert die Website. Echte Up-to-date-Prüfung: läuft nur erneut, wenn sich Content, Konfiguration oder die gepinnten Versionen tatsächlich geändert haben. |
| `bxSitesServe` | Baut die Website und serviert sie lokal mit Live-Reload. Läuft im Vordergrund, bis er gestoppt wird. |
| `bxSitesClean` | Entfernt das gebaute Verzeichnis `site/`. |

`bxSitesBuild` läuft nie automatisch als Teil von `assemble`, außer man
aktiviert das gezielt (siehe `hookIntoAssemble` unten) - ein Docs-Build
ist ein eigenständiges, oft langsameres Anliegen als das Kompilieren des
eigentlichen Codes.

## Konfiguration

```kotlin title="build.gradle.kts"
bxSites {
    projectRoot.set(layout.projectDirectory)
    boxlangMiniserverVersion.set("1.18.0-snapshot")   // gepinnte BoxLang-Laufzeitversion
    bxSitesVersion.set("1.0.0-snapshot")               // gepinnte bx-sites-Version
    boxlangHomeDir.set(layout.buildDirectory.dir("bxsites/boxlang-home"))
    hookIntoAssemble.set(false)                        // Opt-in: bxSitesBuild als Teil von assemble ausführen
    hookIntoCheck.set(true)                            // Lint-/Check-Verben standardmäßig aktiv (folgt später, noch nicht implementiert)
}
```

Jede Eigenschaft hat einen sinnvollen Default. Das Ausgabeverzeichnis ist
hier gar nicht einstellbar - bx-sites selbst legt es fest auf
`<projectRoot>/site/`, daher leitet das Plugin es nur ab, statt eine
Einstellung anzubieten, die ohnehin nicht beachtet würde.

## Was noch nicht gebaut ist

- **Spring-Boot-Doku-Generierung** (OpenAPI, Javadoc, Controller-Scan) - geplant.
- **Live-Output-Streaming von `bxSitesServe`** - puffert derzeit die Ausgabe mit einem 30-Minuten-Timeout, beides falsch für einen Task, der unbegrenzt laufen soll.
- Wrapper-Tasks für die übrigen bx-sites-Verben (`deploy`, `publish`, `package`, `lint`, `check`, usw.) über die vier oben genannten Kern-Tasks hinaus.

Siehe den Guide zum [Maven-Plugin](maven-plugin.md) für das Gegenstück
auf der Maven-Seite - beide Plugins kapseln dieselbe zugrunde liegende
Logik, sodass Verb-Abdeckung und Verhalten zwischen den beiden Build-Tools
identisch bleiben.
