plugins {
    `java-library`
    `maven-publish`
}

// Matches io.boxlang - the Sonatype-verified namespace BoxLang's own
// runtime jars already publish under (io.boxlang:boxlang, :boxlang-miniserver).
// core itself is never actually published there (see the publishing note
// below) - this just keeps local coordinates consistent with the plugins
// that consume it.
group = "io.boxlang"
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
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// core/ is never published to a real, public repository - each consuming
// plugin shades its classes into its own final jar at release time (see the
// plan). During development, `maven-plugin`'s own Maven build resolves core
// from the local repository (~/.m2/repository) via this `maven-publish`
// configuration - run `./gradlew publishToMavenLocal` here first. This is a
// deliberately simpler resolution than `gradle-plugin`'s (a Gradle composite
// build via `includeBuild`, see gradle-plugin/settings.gradle.kts) because
// Maven has no equivalent of Gradle composite builds for a same-ecosystem
// project dependency.
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
