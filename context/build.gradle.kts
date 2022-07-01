plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val ideaActive = System.getProperty("idea.active") == "true"

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.18.2")
    }
}

apply(plugin = "kotlinx-atomicfu")

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
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.6.20")
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.6.20")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.6.20")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
                implementation("io.kotest:kotest-assertions-core:5.3.2")
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {}
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {}
        }
        val nativeMain by creating { dependsOn(commonMain) }

        targets.forEach {
            it.compilations.forEach { compilation ->
                when (compilation.name) {
                    "main" ->
                        compilation.apply {
                            when (this) {
                                is org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation -> { // Native
                                    defaultSourceSet { dependsOn(nativeMain) }
                                }
                            }
                        }
                }
            }
        }
    }
}
