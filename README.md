# Opentelemetry - Kotlin

This repository contains a port of [Opentelemetry-Java](https://github.com/open-telemetry/opentelemetry-java) in to Kotlin Multiplatform

It contains ports for:
- The Tracing Api/SDK 
- The Metrics Api/SDK 

Working on JVM, JS and native (windows, mac, linux)

Most unit tests are also ported.

## Installation

To use the library, add the following to your `build.gradle.kts` file:
```kotlin
repositories {
    maven(url = "https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
}

dependencies {
    implementation("io.opentelemetry.kotlin.api:all:VERSION")
    implementation("io.opentelemetry.kotlin.api:metrics:VERSION")
    implementation("io.opentelemetry.kotlin.sdk:sdk-metrics:VERSION")
    implementation("io.opentelemetry.kotlin.sdk:sdk-trace:VERSION")
}
```



Ported by [SNK](https://www.snk.de/en/)

