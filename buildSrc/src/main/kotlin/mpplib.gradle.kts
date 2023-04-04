plugins {
    id("common")
}

val ideaActive = System.getProperty("idea.active") == "true"
val compileNative = findProperty("compileNative") == "true"

kotlin {
    jvm { withJava() }
    js(IR) {
        nodejs { testTask { useMocha { timeout = "80s" } } }
        browser { testTask { useMocha { timeout = "80s" } } }
    }
    if(compileNative){
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
            macosArm64()
            macosX64()
            iosArm64()
            iosX64()
            linuxX64()
            // linuxArm32Hfp()
            // linuxMips32()
            watchosArm32()
            watchosArm64()
            watchosX64()
            tvosArm64()
            tvosX64()
            //    androidNativeArm32()
            //    androidNativeArm64()
            mingwX64()
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}
