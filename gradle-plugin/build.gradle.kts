plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.3.1"
}

// io.boxlang is the Sonatype-verified namespace BoxLang's own runtime jars
// already publish under - kept consistent with maven-plugin/core even
// though the Gradle Plugin Portal itself doesn't require domain
// verification the way Maven Central does.
group = "io.boxlang"
// The Gradle Plugin Portal hard-rejects any version ending in "-SNAPSHOT" -
// confirmed directly against its own publishing docs: every publish there
// is permanent and immutable, so it doesn't support a mutable snapshot
// channel at all (unlike Maven Central). The release workflow overrides
// this via -PreleaseVersion=<real version>, derived from the git tag, right
// before running publishPlugins; local dev/CI-test builds keep this
// -SNAPSHOT placeholder.
version = (findProperty("releaseVersion") as String?) ?: "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

// bxsites-core is never published anywhere on its own (see core/README.md) -
// its classes must be physically bundled into this plugin's own jar instead.
// Deliberately NOT declared on `implementation`/`api`: those configurations
// back the `runtimeElements`/`apiElements` variants that `java-gradle-plugin`
// publishes core's coordinates through (an unresolvable, unversioned
// dependency - confirmed: publish validation fails outright without this).
// `bundled` is resolvable but never consumed/published, so it's invisible to
// the published POM by construction; it's added directly to the main source
// set's own compile/runtime classpaths below so the code still compiles and
// runs normally, and merged into the `jar` task's output so the shipped
// artifact is genuinely self-contained.
val bundled: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    bundled("io.boxlang:bxsites-core")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // For ProjectBuilder-based unit tests of plugin/task wiring - fast, no
    // network, distinct from the TestKit-based functionalTest suite below.
    testImplementation(gradleTestKit())
}

// Extend the resolvable classpath configurations directly (rather than
// reassigning the source sets' own compileClasspath/runtimeClasspath
// FileCollection properties) - java-gradle-plugin's PluginUnderTestMetadata
// task captures a reference to sourceSets.main.runtimeClasspath at plugin
// apply() time, before this script body runs, so re-assigning that property
// later wouldn't be reflected in what TestKit gives the plugin-under-test
// (confirmed: the functionalTest suite failed with a decoration error since
// bxsites-core's classes were missing from that classpath). extendsFrom is
// a live relationship resolved lazily at first use, so it doesn't have this
// ordering pitfall.
configurations.named("compileClasspath") { extendsFrom(bundled) }
configurations.named("runtimeClasspath") { extendsFrom(bundled) }
configurations.named("testCompileClasspath") { extendsFrom(bundled) }
configurations.named("testRuntimeClasspath") { extendsFrom(bundled) }

tasks.named<Jar>("jar") {
    from({ bundled.map { if (it.isDirectory) it else zipTree(it) } })
}

gradlePlugin {
    // Required by the Gradle Plugin Portal (com.gradle.plugin-publish) -
    // publishing fails without these two.
    website.set("https://github.com/ortus-boxlang/bx-sites")
    vcsUrl.set("https://github.com/ortus-boxlang/bx-sites.git")

    plugins {
        create("bxSites") {
            id = "io.boxlang.bxsites"
            implementationClass = "ortus.boxlang.bxsites.gradle.BxSitesPlugin"
            displayName = "BX Sites"
            description = "Generate a bx-sites documentation site from a Gradle build, with zero prerequisite installs beyond a JDK."
            tags.set(listOf("boxlang", "documentation", "static-site", "bxsites"))
        }
    }
    // Wires up the functionalTest source set below as a Gradle TestKit-backed
    // test suite, separate from the fast unit `test` task.
    testSourceSets(sourceSets.create("functionalTest") {
        // populated by the block below
    })
}

val functionalTest by sourceSets.getting

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

dependencies {
    "functionalTestImplementation"(gradleTestKit())
}

configurations.named("functionalTestCompileClasspath") { extendsFrom(bundled) }
configurations.named("functionalTestRuntimeClasspath") { extendsFrom(bundled) }

val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs the functional test suite (Gradle TestKit)."
    group = "verification"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    useJUnitPlatform()
    // These tests provision real artifacts from the network - see the plan's
    // "Testing / verification" section.
    shouldRunAfter(tasks.named("test"))
}

tasks.named("check") {
    dependsOn(functionalTestTask)
}

tasks.test {
    useJUnitPlatform()
}
