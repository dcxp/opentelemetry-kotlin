rootProject.name = "opentelemetry-kotlin"

pluginManagement {
    val kotlinVersion = "1.6.10"
    plugins {
        id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
        id("org.jetbrains.kotlin.plugin.serialization") version "$kotlinVersion"
        id("org.jetbrains.kotlinx.kover") version "0.5.0"
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":gradle-dependency")

include(":context")

include(":semconv")

include(":api:all")

include(":api:metrics")

include(":sdk:sdk-trace")

include(":sdk:sdk-metrics")

include(":sdk:sdk-testing")

include(":sdk:sdk-common")

include(":sdk:sdk-all") // include(":combined")
