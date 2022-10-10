plugins {
    id("com.rickbusarow.gradle-dependency-sync") version "0.11.4"
}

group = "io.opentelemetry.kotlin"

dependencySync {
    gradleBuildFile.set("${rootDir}/dependency-sync/build.gradle.kts")
    typeSafeFile.set("${rootDir}/gradle/libs.versions.toml")
}

val kotlinVersion = "1.7.20"
val kotlinxCollectionsImmutable = "0.3.5"
val koTestVersion = "5.5.1"
val coroutineVersion = "1.6.4"

dependencies {
    dependencySync("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    dependencySync(
        "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutable"
    )
    dependencySync("com.benasher44:uuid:0.5.0")
    dependencySync("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
    dependencySync("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.18.4")
    dependencySync("io.kotest:kotest-assertions-core:5.5.1")

    // Kotlin
    dependencySync("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    dependencySync("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    dependencySync("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
    dependencySync("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
    // Coroutines
    dependencySync(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
    )
    dependencySync("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    // Kotest
    dependencySync("io.kotest:kotest-assertions-core:$koTestVersion")
}


