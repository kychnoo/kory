@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins {
    id("kory.kmp-library")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

dokka {
    dokkaSourceSets.commonMain {
        samples.from("src/samples/kotlin")
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

    applyDefaultHierarchyTemplate {
        common {
            group("jvmShared") {
                withJvm()
                withCompilations { it.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)
            implementation(libs.kotlinxIoCore)
        }

        getByName("jvmSharedMain") {
            dependencies {
                api(project(":kory-ktor"))

                implementation(libs.ktor.client.okhttp)
            }
        }

        commonTest {
            kotlin.srcDir("src/samples/kotlin")

            dependencies {
                implementation(kotlin("test"))
            }
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