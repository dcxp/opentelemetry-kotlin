plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
        mingwX64()
        linuxX64()
    }

    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val commonMain by getting {
            dependencies {
                api(project(":context"))
                api(project(":api:all"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.7.10")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.7.10")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                
                implementation("io.kotest:kotest-assertions-core:5.4.1")
            }
        }
    }
}
