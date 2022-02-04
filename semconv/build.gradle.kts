plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

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

    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val kotlinVersion: String by project
        val kodeinVersion: String by project
        val coroutineVersion: String by project
        val commonMain by getting { dependencies { api(project(":api:all")) } }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.6.10")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.6.10")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0" + "-native-mt")
            }
        }
    }
}
