rootProject.name = "bxsites-gradle-plugin"

// core/ is a plain Gradle java-library, included here as a composite build so
// gradle-plugin can depend on it as a normal project dependency during
// development without needing core published anywhere. Before core's first
// real publish, this dependency substitution is replaced by shading core's
// classes into this plugin's own final jar - see build.gradle.kts.
includeBuild("../core")
