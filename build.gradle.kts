plugins {
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
    id("org.jetbrains.kotlinx.kover") apply false
}

if (System.getenv("GITHUB_RUN_NUMBER") != null) {
    version = "1.0.${System.getenv("GITHUB_RUN_NUMBER")}"
}else{
    version = "1.0.0"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    }
}

subprojects {
    group = "io.opentelemetry"
    version = rootProject.version
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<org.gradle.api.tasks.bundling.AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
    }

    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        /*val publicationsFromMainHost = listOf("jvm", "js", "kotlinMultiplatform")
        publications{
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }*/
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

// FIXME Temporary WORKAROUND for arm64 Apple Silicon; removabl probably with kotlin 1.6.20
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>()
        .nodeVersion = "16.13.1"
}
