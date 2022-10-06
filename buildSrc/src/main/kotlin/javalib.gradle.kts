plugins {
    id("common")
}
val compileNative = findProperty("compileNative") == "true"

kotlin {
    jvm { withJava() }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}
