val kotlinVersion = "1.6.20"

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "de.snk.dcxp"

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    jvm { withJava() }
    /*js(IR) {
        nodejs()
        browser()
    }*/
    if (ideaActive) {
        // macosArm64()
    } else {
        macosX64()
        macosArm64()
        iosArm32()
        iosArm64()
        iosX64()
        linuxX64()
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
    }

    sourceSets {
        val kotlinVersion = "1.6.10"
        val kotlinxDatetime = "0.3.2"
        val kotlinxCollectionsImmutable = "0.3.5"
        val uuid = "0.4.1"
        val koTestVersion = "5.3.1"
        val coroutineVersion = "1.6.2"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetime")
                implementation(
                    "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutable"
                )
                implementation("com.benasher44:uuid:$uuid")

                // Kotlin
                implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion")
                // Coroutines
                implementation(
                    "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
                )
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
                // Kotest
                implementation("io.kotest:kotest-assertions-core:$koTestVersion")
            }
        }
    }
}
