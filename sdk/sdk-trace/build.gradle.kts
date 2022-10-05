plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    jvm { withJava() }
    js(IR) {
        nodejs { testTask { useMocha { timeout = "80000" } } }
        browser { testTask { useMocha { timeout = "80000" } } }
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
                api(project(":sdk:sdk-common"))

                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":sdk:sdk-testing"))

                implementation("org.jetbrains.kotlin:kotlin-test:1.7.20")
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.7.20")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.7.20")

                implementation("io.kotest:kotest-assertions-core:5.5.0")
                
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

                implementation("com.benasher44:uuid:0.5.0")
            }
        }
    }
}
