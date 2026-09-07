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

dependencies {
    implementation("io.boxlang:bxsites-core")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // For ProjectBuilder-based unit tests of plugin/task wiring - fast, no
    // network, distinct from the TestKit-based functionalTest suite below.
    testImplementation(gradleTestKit())
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
