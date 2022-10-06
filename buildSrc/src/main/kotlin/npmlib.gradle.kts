plugins {
    id("common")
}
val compileNative = findProperty("compileNative") == "true"

kotlin {
    js(IR) {
        binaries.library()
        nodejs { testTask { useMocha { timeout = "20000" } } }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}
