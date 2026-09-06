plugins {
    `java-gradle-plugin`
}

group = "com.ortussolutions.bxsites"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ortussolutions.bxsites:bxsites-core")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // For ProjectBuilder-based unit tests of plugin/task wiring - fast, no
    // network, distinct from the TestKit-based functionalTest suite below.
    testImplementation(gradleTestKit())
}

gradlePlugin {
    plugins {
        create("bxSites") {
            id = "com.ortussolutions.bxsites"
            implementationClass = "ortus.boxlang.bxsites.gradle.BxSitesPlugin"
            displayName = "BX Sites"
            description = "Generate a bx-sites documentation site from a Gradle build, with zero prerequisite installs beyond a JDK."
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
