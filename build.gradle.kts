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

val doNotPublish = setOf("gradle-dependency")

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
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>()
        .nodeVersion = "16.13.1"
}
// Build pipeline Tasks
tasks.register("checkMac") {
    dependsOnTaskOfSubprojectsByName("macosArm64Test")
    dependsOnTaskOfSubprojectsByName("macosX64Test")
    dependsOnTaskOfSubprojectsByName("IosArm64Test")
    dependsOnTaskOfSubprojectsByName("IosArm32Test")
}

tasks.register("checkWindows") {
    dependsOnTaskOfSubprojectsByName(":mingwX64Test")
}

tasks.register("checkLinux") {
    dependsOnTaskOfSubprojectsByName("jvmTest")
    dependsOnTaskOfSubprojectsByName("JsTest")
    dependsOnTaskOfSubprojectsByName("linuxX64Test")
    dependsOnTaskOfSubprojectsByName("linuxArm32HfpTest")
}

tasks.register("publishMac") {
    dependsOnTaskOfSubprojectsByName("publishIosArm32PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishIosArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishIosSimulatorArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishIosSimulatorArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishTvosArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishTvosSimulatorArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishTvosX64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishWatchosArm32PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishWatchosArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishWatchosSimulatorArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishWatchosX86PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishMacosArm64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishMacosX64PublicationToMavenRepository")
}

tasks.register("publishWindows") { dependsOnTaskOfSubprojectsByName("publishMingwX64PublicationToMavenRepository") }

tasks.register("publishLinux") {
    dependsOnTaskOfSubprojectsByName("publishLinuxX64PublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishLinuxArm32HfpPublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishJvmPublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishJsPublicationToMavenRepository")
    dependsOnTaskOfSubprojectsByName("publishKotlinMultiplatformPublicationToMavenRepository")
}

fun getTaskOfSubprojectsByName(name: String): List<Task> {
    return project.subprojects.mapNotNull { project -> project.tasks.findByName(name) }
}

fun Task.dependsOnTaskOfSubprojectsByName(name: String){
    this.dependsOn(getTaskOfSubprojectsByName(name))
}
/*fun dependsOnTaskOfSubprojectsByName(name: String){
    getTaskOfSubprojectsByName(name).forEach{task ->
        task()
    }
}*/