import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

group = "io.kory.openai"
version = "0.0.1"

dokka {
    dokkaSourceSets.commonMain {
        samples.from(
            rootProject.files("examples/src/commonMain/kotlin"),
        )
    }
}

kotlin {
    jvm()
    jvmToolchain(21)

    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosArm64()

    mingwX64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(project(":kory-ktor"))

            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

tasks.withType<KotlinNativeLink>().configureEach {
    val hostOs = System.getProperty("os.name").lowercase()
    val target = this.target

    if (hostOs.startsWith("windows") && (target.contains("linux") || target.contains("macos") || target.contains("ios"))) {
        enabled = false
    }
}