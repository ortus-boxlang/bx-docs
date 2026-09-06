---
title: KI-Agenten-Skills
order: 6.3
icon: phosphor-duotone:robot
summary: Vermitteln Sie Claude Code, Cursor, Codex und anderen KI-Coding-Assistenten fundiertes, strukturiertes Wissen über bx-sites - installieren Sie das offizielle Skill-Paket per npx, über die ColdBox-CLI oder mit bxSites' eigenem Verb skills:install.
tags: [anleitungen, ai, skills]
---

# KI-Agenten-Skills

Ein **[Agent Skill](https://code.claude.com/docs/en/skills)** ist eine kleine,
in sich geschlossene `SKILL.md`-Datei, die einem KI-Coding-Assistenten
beibringt, eine bestimmte Sache gut zu erledigen - der Assistent lädt sie
automatisch, bei Bedarf, sobald eine Aufgabe zu dem passt, was der Skill
beschreibt. Anstatt bx-sites' eigene Konventionen in jedem Gespräch neu zu
erklären (wie `::: card :::`-Blöcke funktionieren, was `bxsites.yaml`s
Schlüssel `redirects` erwartet, wie sich `page:new` von einer handgeschriebenen
Datei unterscheidet), gibt ein Skill dieses Wissen von vornherein weiter,
geschrieben so, wie bx-sites selbst funktioniert.

[`ortus-boxlang/bx-sites-skills`](https://github.com/ortus-boxlang/bx-sites-skills)
ist das offizielle Skill-Paket für dieses Projekt - dreizehn Skills, die von
der Gerüstbildung eines neuen Projekts bis zur Fehlerbehebung bei den eigenen
GitHub Actions dieses Repositorys alles abdecken. Sie funktionieren mit jedem
Assistenten, der das Agent-Skills-Format unterstützt (Claude Code, Cursor,
Codex und andere).

## Installation

Drei Wege, das Paket zu installieren - wählen Sie, was zu Ihrem Workflow passt:

### `npx skills add`

Die [`skills`-CLI](https://github.com/skillslib/skills) funktioniert mit
jedem Projekt, unabhängig von Sprache oder Laufzeitumgebung, und benötigt
nichts außer Node.js:

```bash title="Alle Skills installieren"
npx skills add ortus-boxlang/bx-sites-skills
```

```bash title="Nicht-interaktiv (CI, Skripte)"
npx -y skills add ortus-boxlang/bx-sites-skills -y
```

Installieren Sie einen einzelnen Skill statt des gesamten Pakets, indem Sie
direkt darauf verweisen:

```bash title="Nur einen Skill installieren"
npx skills add ortus-boxlang/bx-sites-skills/skills/bx-sites-deployment
```

### `coldbox ai skills install`

Wenn Sie bereits die ColdBox-CLI haben, kann sie direkt aus derselben
GitHub-Quelle installieren - siehe das
[BoxLang Skills Directory](https://skills.boxlang.io/):

```bash title="Einen einzelnen Skill installieren"
coldbox ai skills install ortus-boxlang/bx-sites-skills/bx-sites-deployment
```

### `bxSites skills:install`

bx-sites bringt außerdem sein eigenes One-Shot-Verb mit - ein dünner Wrapper
um `npx skills add`, der das gesamte Paket direkt in das aktuelle Projekt
installiert, sodass der KI-Assistent eines frisch gescaffolteten Projekts
bx-sites schon beim allerersten Prompt kennt:

```bash title="Verwendung"
bxSites skills install
# oder, äquivalent:
bxSites skills:install
```

```bash title="Nur einen Skill installieren"
bxSites skills:install --skill=bx-sites-deployment
```

Erfordert Node.js/`npx` im `PATH` (dieselbe Voraussetzung, die auch
`npx skills add` selbst hat) - die vollständige Flag-Referenz finden Sie in
der [CLI-Referenz](../cli-reference.md#skillsinstall).

## Verfügbare Skills

Jeder Skill ist eine einzelne, in sich geschlossene `SKILL.md` (keine
gebündelten Ressourcendateien), sodass er über jeden der obigen Wege
korrekt installiert wird:

| Skill | Beschreibung |
|---|---|
| `bx-sites-getting-started` | bx-sites installieren, ein neues Projekt gerüstbilden (oder ein bestehendes GitBook-/mkdocs-/Notion-Projekt migrieren), Projektstruktur, Seiten-Frontmatter, Verlinkung, build/serve/clean. |
| `bx-sites-content-blocks` | Umfangreiche `::: name :::`-Content-Blöcke - Karten, Spalten, Stepper, Buttons, Einbettungen, Seiten-Link/Link-Vorschau, Prompt, Updates, Includes, bedingter Inhalt, OpenAPI-Widget. |
| `bx-sites-markdown` | Admonitions, Fußnoten, Definitionslisten, Content-Tabs, Codeblock-Annotationen, Mermaid, Mathematik, Tabellen, Icons, responsive Bilder, Alpine.js-Interaktivität. |
| `bx-sites-variables-functions` | Wiederverwendbare `{{ variables }}` und BoxLang-Magic-Functions (`docs/functions.bxs`), einschließlich Rezepte für Status-Badge-/Bewertungs-/Fortschrittsbalken-Visualizer. |
| `bx-sites-blog-versioning-i18n` | Der Blog (`docs/blog/posts/`), versionierte Docs (`docs/versions/`), übersetzte Locales (`docs/i18n/`) und Redirects. |
| `bx-sites-content-quality` | Vorab-Build-Inhaltsprüfungen ohne vollständigen Build - `lint`, `blog:drafts`, `blog:find`, `search:query`. |
| `bx-sites-build` | `build`/`serve`/`clean`/`search-index`, plus `doctor`/`stats`/`check`-Diagnosen für eine gebaute Site. |
| `bx-sites-configuration` | Die vollständige Schlüsselreferenz für `bxsites.yaml`/`bxsites.json` - `baseURL`, `nav`, `redirects`, `markdown`, Assets und mehr. |
| `bx-sites-themes` | Ein Theme auswählen, anpassen, überschreiben, installieren oder schreiben; der `ThemeProvider`-Vertrag. |
| `bx-sites-search` | Suchanbieter - lokal (MiniSearch), Algolia DocSearch, Pagefind, und einen benutzerdefinierten Anbieter anbinden. |
| `bx-sites-plugins` | Ein bx-sites-Plugin (Build-Lifecycle-Hooks) oder einen CLI-Provider (neue `bxSites`-Verben) schreiben/installieren. |
| `bx-sites-deployment` | Deploy-Ziele (S3/Azure/GCS/Firebase/FTP/SFTP/rsync/Netlify/Vercel/Cloudflare Pages/GitHub Pages), `package`, und der GitHub-Actions-Publishing-Workflow. |
| `bx-sites-actions` | Die eigenen GitHub-Actions-Workflows von bx-sites bedienen und Fehler beheben (tests, snapshot, release, docs, pages). |

## Beispiel-Prompts

Nach der Installation beschreiben Sie einfach, was Sie möchten - Ihr
Assistent wählt selbstständig den passenden Skill:

```text title="Eine neue Site gerüstbilden"
Scaffold a new bx-sites project called "acme-docs" using the gitbook
theme, add a Getting Started page, and start the dev server.
```

```text title="Inhalte verfassen"
Add a three-step ::: stepper ::: block to docs/getting-started.md walking
through install, scaffold, and build - and a ::: cards ::: grid linking
to the three main guides.
```

```text title="Ausliefern"
Add a Netlify deploy target to bxsites.yaml and explain which
environment variable it expects for the auth token.
```

## FAQ

??? faq "Was steht eigentlich in einer SKILL.md-Datei?"
    Ein kurzer YAML-Frontmatter-Block (`name`, `description` - der Trigger,
    gegen den ein Assistent abgleicht), gefolgt von einfachen
    Markdown-Anweisungen, Konventionen und Beispielen für dieses eine Thema.
    Keine gebündelten Skripte oder Ressourcendateien - jeder Skill in diesem
    Paket ist bewusst eine einzelne Datei, sodass er auf dieselbe Weise über
    `npx skills add`, `coldbox ai skills install` und
    `bxSites skills:install` installiert wird.

??? faq "Brauche ich alle dreizehn Skills?"
    Nein - jeder Installationsweg unterstützt die Installation nur eines
    einzelnen Skills nach Namen (siehe [Installation](#installation) oben).
    Die meisten Projekte fahren aber gut damit, das gesamte Paket zu
    installieren: Ein Assistent lädt den Inhalt eines Skills nur, wenn eine
    Aufgabe tatsächlich dazu passt, sodass ungenutzte Skills zur
    Prompt-Zeit nichts kosten.

??? faq "Mit welchen KI-Assistenten funktioniert das?"
    Mit jedem Assistenten, der das Agent-Skills-Format unterstützt - Claude
    Code, Cursor, Codex und andere. `npx skills add`/`coldbox ai skills
    install` erkennen, welche Assistenten in Ihrem Projekt bereits
    konfiguriert sind, und installieren automatisch in jeden davon.

??? faq "Wie überprüfe ich, ob ein Skill wirklich installiert wurde?"
    Prüfen Sie, ob eine neue `SKILL.md` im eigenen Skills-Verzeichnis Ihres
    Assistenten existiert (z. B. `.claude/skills/bx-sites-getting-started/SKILL.md`
    für Claude Code) - oder fragen Sie Ihren Assistenten einfach etwas, das
    der Skill abdeckt (z. B. "Wie füge ich ein Netlify-Deploy-Ziel hinzu?"),
    und prüfen Sie, ob die Antwort mit dieser Dokumentation übereinstimmt.

??? faq "`bxSites skills:install` ist mit einem Fehler zu npx/Node.js fehlgeschlagen"
    Es ruft im Hintergrund das echte `npx skills add` auf und benötigt daher
    Node.js im `PATH`, genau wie dieser Befehl selbst - installieren Sie
    Node.js von [nodejs.org](https://nodejs.org/) und versuchen Sie es
    erneut. Oder überspringen Sie den eigenen Wrapper von `bxSites`
    vollständig und führen Sie direkt
    `npx skills add ortus-boxlang/bx-sites-skills` aus.

??? faq "Kann ich diese in ein Projekt installieren, das noch kein bx-sites-Projekt ist?"
    Ja - `npx skills add`/`coldbox ai skills install` funktionieren in jedem
    Verzeichnis. `bxSites skills:install` benötigt speziell einen
    existierenden `--projectRoot` (standardmäßig das aktuelle Verzeichnis),
    genau wie jedes andere `bxSites`-Verb, aber dieses Projekt muss noch
    nicht gebaut sein - Skills zu installieren, bevor Sie auch nur eine
    einzige Seite geschrieben haben, ist genau der Punkt, damit Ihr
    Assistent bx-sites schon beim allerersten Prompt kennt.

## Quelle

- Skills-Repository: [ortus-boxlang/bx-sites-skills](https://github.com/ortus-boxlang/bx-sites-skills)
- bx-sites-Repository: [ortus-boxlang/bx-sites](https://github.com/ortus-boxlang/bx-sites)
- BoxLang Skills Directory: [skills.boxlang.io](https://skills.boxlang.io/)
