rootProject.name = "opentelemetry-kotlin"

pluginManagement {
    plugins {
        id("org.jetbrains.kotlinx.kover") version "0.6.1"
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":dependency-sync")

include(":api:all")

include(":api:metrics")

include(":sdk:sdk-trace")

include(":sdk:sdk-metrics")

include(":sdk:sdk-testing")

include(":sdk:sdk-common")

include(":sdk:sdk-all")

include(":context")

include(":semconv")