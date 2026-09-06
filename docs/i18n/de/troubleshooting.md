---
title: Fehlerbehebung
order: 2.4
icon: phosphor-duotone:lifebuoy
summary: Häufige Probleme bei Einrichtung, Build und Serve diagnostizieren - und wo du Hilfe findest, wenn diese Seite es nicht abdeckt.
tags: [fehlerbehebung, faq]
---

# Fehlerbehebung

## Zuerst `doctor` ausführen

Bevor du weiter suchst, führe den eingebauten Health-Check aus - er deckt
die meisten Probleme auf dieser Seite in einem Schritt ab:

```bash frame="terminal" title="Terminal"
bxSites doctor
```

Er prüft die JVM-Version, dass `docs/` (oder `src/`) existiert, dass
`bxsites.yaml`/`.json` tatsächlich geparst und validiert werden kann, dass
die benötigten BoxLang-Module installiert und aktiviert sind, und - falls
ein projektweites `theme/`-Override existiert - dass es den Theme-Vertrag
erfüllt. Er beendet sich mit `1`, falls eine Prüfung fehlschlägt, und gibt
aus, was nicht stimmt; nichts hiervon verändert dein Projekt.

## Häufige Probleme

??? bug "`No docs/ directory found`"
    `build`/`serve`/`site:check`/etc. suchen `docs/` (mit Fallback auf `src/`)
    relativ zum aktuellen Verzeichnis, oder relativ zu
    `--projectRoot=<path>`, falls angegeben. Führe den Befehl entweder
    innerhalb des Projekt-Root-Ordners aus, oder gib `--projectRoot` an:

    ```bash frame="terminal" title="Terminal"
    bxSites build --projectRoot=/pfad/zu/meine-docs
    ```

??? bug "`bxsites.yaml`/`.json` lässt sich nicht parsen oder validieren"
    Führe `bxSites doctor` aus, um genau zu sehen, welchen Key/welche
    Zeile der Config-Loader abgelehnt hat. Häufige Ursachen: gemischte
    Tabs und Leerzeichen bei der YAML-Einrückung, ein überzähliges Komma
    in JSON, oder ein Key, der ein Array erwartet (wie `nav` oder
    `i18n.locales`), aber als reiner String geschrieben wurde. Siehe
    [Konfiguration](configuration.md) für die vollständige
    Key-Referenz.

??? bug "`bx-markdown`/`bx-esapi`/`bx-yaml`/`bx-image` nicht installiert/aktiviert"
    `build`, `serve` und `search-index` benötigen alle vier
    BoxLang-Module. Die Installation von `bx-sites` selbst installiert sie
    automatisch als `box.json`-Abhängigkeiten
    (`install-bx-module bx-sites` oder `box install bx-sites`) - siehst du
    diesen Fehler trotzdem, ist entweder die Installation nicht
    abgeschlossen, oder das Modul wurde von Hand ohne seine
    Abhängigkeiten registriert. Ein erneutes `box install` im
    Projekt-Root löst alles neu auf; `bxSites doctor` bestätigt, welches
    Modul (falls überhaupt eines) noch fehlt.

??? bug "Ein projektweites `theme/`-Override lässt sich nicht bauen"
    Ein eigener `theme/`-Ordner muss sowohl `layout.bxm` als auch
    `page.bxm` bereitstellen - `doctor` meldet, welche Datei fehlt.
    Siehe [Themes](guides/themes.md) für den vollständigen Vertrag,
    oder führe `bxSites theme:new` aus, um ein eingebautes Theme als
    funktionierenden Ausgangspunkt auszuwerfen, statt eines von Grund auf
    neu zu schreiben.

??? bug "`serve` bemerkt eine Änderung nicht"
    `serve` beobachtet `docs/`, deine `bxsites.yaml`/`.json` und ein
    projektweites `theme/`-Override - eine Änderung anderswo (z. B. an
    einer Datei unter `resources/` in einem Modul-Checkout, nicht einem
    echten Projekt) löst keinen Rebuild aus. Wird eine echte Änderung
    trotzdem nicht übernommen, stoppe `serve`, führe `bxSites clean` aus,
    um einen veralteten Build-Cache zu leeren, und starte `bxSites serve`
    erneut.

??? bug "Ein Build wirkt veraltet, oder CI meldet Erfolg, aber nichts hat sich geändert"
    `build` löscht zuvor gebautes Output nicht, das keiner
    Quellseite mehr entspricht. Führe `bxSites clean` vor `build` aus, um
    `site/` und jeden Build-Cache vollständig zu entfernen, und baue dann
    neu von Grund auf. Meldet ein CI-Schritt Erfolg, aber die deployte
    Website spiegelt es nicht wider, prüfe das eigentliche Build-Log auf
    `Error:` - ein abgestürzter Build kann in manchen CI-Setups trotzdem
    einen irreführenden Erfolgsstatus melden.

??? bug "Eine übersetzte Seite zeigt einen Hinweis auf fehlende Übersetzung"
    Das ist erwartet, kein Bug: Ein Locale muss nicht jede Seite übersetzt
    haben, um nutzbar zu sein. Eine Seite, die in `docs/i18n/<code>/`
    fehlt, wird trotzdem unter ihrer erwarteten URL gebaut und zeigt den
    Inhalt des Standard-Locale mit einem kleinen Hinweis oben auf der
    Seite. Siehe [Internationalisierung (i18n)](guides/i18n.md).

??? bug "`i18n:status` meldet 100 %, aber eine Übersetzung wirkt trotzdem veraltet"
    `i18n:status` prüft nur, ob eine Seite pro Locale *existiert*, nicht
    die inhaltliche Übereinstimmung pro Seite - eine Locale-Kopie kann
    existieren, aber trotzdem einen Abschnitt vermissen, der später zur
    Standard-Locale-Seite hinzugefügt wurde. Vergleiche die Locale-Datei
    im Zweifel direkt mit ihrem Standard-Locale-Gegenstück.

## Immer noch nicht weiter?

Wenn nichts davon dein Problem abdeckt, wende dich an einen der folgenden
Support-Kanäle - siehe [Mitmachen](contribute.md) für die vollständige
Liste:

::: cards
::: card title="Ortus Community Discourse" icon="phosphor-duotone:chats-circle" href="https://community.ortussolutions.com"
Stelle Fragen und durchsuche bestehende Diskussionen.
:::
::: card title="Box Slack Team" icon="phosphor-duotone:slack-logo" href="https://boxteam.slack.com"
Chatte in Echtzeit mit der Community und den Maintainern.
:::
::: card title="Issue eröffnen" icon="phosphor-duotone:bug" href="https://github.com/ortus-boxlang/bx-sites/issues"
Für einen reproduzierbaren Bug, mit angehängter `bxSites doctor`-Ausgabe.
:::
::: card title="Professioneller Support" icon="phosphor-duotone:handshake" href="services.md"
Soll das Team hinter BxSites es für dich lösen? Ortus Solutions bietet
professionellen Support und Beratung an.
:::
:::
