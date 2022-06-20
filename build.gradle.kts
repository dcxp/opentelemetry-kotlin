plugins {
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
    id("org.jetbrains.kotlinx.kover") apply false
}

if (System.getenv("GITHUB_RUN_NUMBER") != null) {
    version = "1.0.${System.getenv("GITHUB_RUN_NUMBER")}"
} else {
    version = "1.0.0"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.18.0")
    }
}

val doNotPublish = setOf("gradle-dependency")

subprojects {
    if (this.file("src").exists() && !doNotPublish.contains(this.name)) {
        apply(plugin = "kotlinx-atomicfu")
    }
}

subprojects {
    group = "io.opentelemetry.kotlin"
    version = rootProject.version
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<org.gradle.api.tasks.bundling.AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
    }
    if (doNotPublish.contains(this.name)) {
        return@subprojects
    }
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
                credentials {
                    username =
                        project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password =
                        project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

// FIXME Temporary WORKAROUND for arm64 Apple Silicon; removabl probably with kotlin 1.6.20
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject
        .the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>()
        .nodeVersion = "16.13.1"
}
// Build pipeline Tasks
tasks.register("checkMac") {
    dependsOnTaskOfSubprojectsByName("darwinTest")
    dependsOnTaskOfSubprojectsByName("macosArm64Test")
}

tasks.register("checkWindows") { dependsOnTaskOfSubprojectsByName("mingwX64Test") }

tasks.register("checkLinux") {
    dependsOnTaskOfSubprojectsByName("jvmTest")
    dependsOnTaskOfSubprojectsByName("jsTest")
    dependsOnTaskOfSubprojectsByName("linuxX64Test")
    dependsOnTaskOfSubprojectsByName("linuxArm32HfpTest")
}

tasks.register("publishMac") {
    dependsOnTaskOfSubprojectsByName("publishIosArm32PublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishIosArm64PublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName(
        "publishIosSimulatorArm64PublicationToGitHubPackagesRepository"
    )
    dependsOnTaskOfSubprojectsByName(
        "publishIosSimulatorArm64PublicationToGitHubPackagesRepository"
    )
    //    dependsOnTaskOfSubprojectsByName("publishTvosArm64PublicationToGitHubPackagesRepository")
    //
    // dependsOnTaskOfSubprojectsByName("publishTvosSimulatorArm64PublicationToGitHubPackagesRepository")
    //    dependsOnTaskOfSubprojectsByName("publishTvosX64PublicationToGitHubPackagesRepository")
    //
    // dependsOnTaskOfSubprojectsByName("publishWatchosArm32PublicationToGitHubPackagesRepository")
    //
    // dependsOnTaskOfSubprojectsByName("publishWatchosArm64PublicationToGitHubPackagesRepository")
    //
    // dependsOnTaskOfSubprojectsByName("publishWatchosSimulatorArm64PublicationToGitHubPackagesRepository")
    //    dependsOnTaskOfSubprojectsByName("publishWatchosX86PublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishMacosArm64PublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishMacosX64PublicationToGitHubPackagesRepository")
}

tasks.register("publishWindows") {
    dependsOnTaskOfSubprojectsByName("publishMingwX64PublicationToGitHubPackagesRepository")
}

tasks.register("publishLinux") {
    dependsOnTaskOfSubprojectsByName("publishLinuxX64PublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishLinuxArm32HfpPublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishJvmPublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName("publishJsPublicationToGitHubPackagesRepository")
    dependsOnTaskOfSubprojectsByName(
        "publishKotlinMultiplatformPublicationToGitHubPackagesRepository"
    )
}

fun getTaskOfSubprojectsByName(name: String): List<Task> {
    val list = project.subprojects.mapNotNull { project -> project.tasks.findByName(name) }
    println("Warning no tasks found with name: $name")
    return list
}

fun Task.dependsOnTaskOfSubprojectsByName(name: String) {
    this.dependsOn(getTaskOfSubprojectsByName(name))
}
/*fun dependsOnTaskOfSubprojectsByName(name: String){
    getTaskOfSubprojectsByName(name).forEach{task ->
        task()
    }
}*/
