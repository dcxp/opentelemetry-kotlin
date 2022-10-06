plugins {
    id("mpplib")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":context"))
                implementation(project(":sdk:sdk-all"))
                implementation(project(":sdk:sdk-common"))

                implementation(libs.jetbrains.kotlinx.collections.immutable)
                implementation(libs.jetbrains.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {

                implementation(libs.bundles.kotlin.test)

                implementation(libs.kotest.assertions.core)
                
                implementation(libs.jetbrains.kotlinx.coroutines.test)
            }
        }
    }
}
