plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

buildscript { dependencies { classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.0") } }

apply(plugin = "kotlinx-atomicfu")

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    jvm { withJava() }
    js(IR) {
        nodejs()
        browser()
    }

    if (ideaActive) {
        val os =
            org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
                .getCurrentOperatingSystem()
        if (os.isWindows) {
            mingwX64()
        } else if (os.isLinux) {
            linuxX64()
        } else if (os.isMacOsX) {
            macosX64("darwin")
        }
    } else {
        macosX64()
        iosArm32()
        iosArm64()
        iosX64()
        // linuxArm32Hfp()
        // linuxMips32()
        watchosArm32()
        watchosArm64()
        watchosX86()
        watchosX64()
        tvosArm64()
        tvosX64()
        //    androidNativeArm32()
        //    androidNativeArm64()
        mingwX64()
        linuxX64()
    }
    val publicationsFromMainHost =
        listOf(jvm(), js()).map { it.name } + "kotlinMultiplatform"
    publishing {
        publications {
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }
    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val atomicFu: String by project
        val kotlinVersion: String by project
        val koTestVersion: String by project
        val coroutineVersion: String by project
        val kotlinxCollectionsImmutable: String by project
        val commonMain by getting {
            dependencies {
                implementation(project(":context"))
                implementation(project(":sdk:sdk-all"))
                implementation(project(":sdk:sdk-common"))

                implementation("org.jetbrains.kotlinx:atomicfu:$atomicFu")
                implementation(
                    "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutable"
                )
                implementation(
                    "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion" + "-native-mt"
                )
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
                implementation("io.kotest:kotest-assertions-core:$koTestVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
            }
        }
    }
}
