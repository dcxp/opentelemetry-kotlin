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
            //mingwX64()
        } else if (os.isLinux) {
            linuxX64()
        } else if (os.isMacOsX) {
            macosArm64()
        }
    } else {
        macosX64()
        macosArm64()
        iosArm32()
        iosArm64()
        iosX64()
        // linuxArm32Hfp()
        // linuxMips32()
//        watchosArm32()
//        watchosArm64()
//        watchosX86()
//        watchosX64()
//        tvosArm64()
//        tvosX64()
        //    androidNativeArm32()
        //    androidNativeArm64()
        //mingwX64()
        linuxX64()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":context"))
                implementation("pro.streem.pbandk:pbandk-runtime:0.14.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.kotlin.test)

                implementation(libs.jetbrains.kotlinx.coroutines.core)

                implementation(libs.kotest.assertions.core)
            }
        }
    }
}