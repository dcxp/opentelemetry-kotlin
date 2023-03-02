plugins {
    id("mpplib")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":context"))
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