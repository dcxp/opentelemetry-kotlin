plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

buildscript { dependencies { classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.0") } }

apply(plugin = "kotlinx-atomicfu")

group = "de.snk.dcxp.api.core"

apply(plugin = "kotlinx-atomicfu")

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    jvm { withJava() }
    // TODO fix for js build

        js(IR) {
            nodejs{
                testTask {
                    useMocha {
                        timeout = "80000"
                    }
                }
            }
            browser{
                testTask {
                    useMocha {
                        timeout = "80000"
                    }
                }
            }
        }

    // TODO fix code for native build
    /*
        if (ideaActive) {
          val os = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
          if(os.isWindows){
            mingwX64()
          }else if(os.isLinux){
            linuxX64()
          }else if(os.isMacOsX){
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
    */
    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val atomicFu: String by project
        val kotlinVersion: String by project
        val koTestVersion: String by project
        val coroutineVersion: String by project
        val kotlinxCollectionsImmutable: String by project
        val commonMain by getting {
            dependencies {
                api(project(":context"))
                api(project(":api:metrics"))
                api(project(":sdk:sdk-common"))

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
                api(project(":sdk:sdk-testing"))

                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
                implementation("io.kotest:kotest-assertions-core:$koTestVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
            }
        }
    }
}
