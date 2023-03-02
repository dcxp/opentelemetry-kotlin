plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.gradleplugin.kotlin.main)
    implementation(libs.gradleplugin.kotlinx.serialization)
}

