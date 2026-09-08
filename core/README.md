# bx-sites Core (internal)

Shared, build-tool-agnostic Java library behind the
[Gradle plugin](../gradle-plugin/README.md) and
[Maven plugin](../maven-plugin/README.md) — the bx-sites verb registry,
artifact provisioning (downloads and checksum-verifies the BoxLang
runtime + bx-sites into a shared cache), and subprocess invocation. No
Gradle or Maven API is used anywhere in here, by design: it runs
identically from a Gradle task or a Maven Mojo.

**Not published anywhere, not meant to be depended on directly.** At
release time, each of the two plugins shades this module's classes into
its own final jar, so a consumer of either plugin never resolves
`io.boxlang:bxsites-core` itself. During development, `gradle-plugin`
resolves it via a Gradle composite build (`includeBuild`), and
`maven-plugin` resolves it from the local Maven repository — run
`./gradlew publishToMavenLocal` here first if you're working on
`maven-plugin`.

```bash
./gradlew test   # fast, no network - see src/test/java
```

Apache-2.0, same as bx-sites itself.
