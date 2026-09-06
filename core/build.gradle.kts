plugins {
    `java-library`
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
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// core/ is never published standalone - each consuming plugin shades its classes
// into its own final jar. This "publish to a local, file-based repo" task is how
// gradle-plugin and maven-plugin both consume it during their own builds without
// core needing a real, permanent public coordinate.
tasks.register<Sync>("publishToLocalRepo") {
    dependsOn(tasks.jar)
    from(tasks.jar.get().archiveFile)
    into(layout.buildDirectory.dir("local-repo"))
}
