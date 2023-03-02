plugins {
    id("mpplib")
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":semconv"))
                api(project(":api:all"))

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
