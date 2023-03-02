plugins {
    id("mpplib")
}



kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":context"))
                api(project(":api:all"))
                api(project(":sdk:sdk-common"))
                api(project(":sdk:sdk-trace"))

                implementation(libs.jetbrains.kotlinx.collections.immutable)
                implementation(libs.jetbrains.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":sdk:sdk-testing"))

                implementation(libs.bundles.kotlin.test)

                implementation(libs.kotest.assertions.core)
                
                implementation(libs.benasher44.uuid)

                implementation(libs.jetbrains.kotlinx.coroutines.test)
            }
        }
    }
}
