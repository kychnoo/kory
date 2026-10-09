@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("kory.kmp-library")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
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
            implementation(libs.kotlinxIoCore)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.json)
            api(libs.ktor.client.logging)

            implementation(libs.kotlinxSerialization)
            implementation(libs.kotlinxCoroutines)
        }

        getByName("jvmSharedMain")
        nativeMain

        commonTest {
            kotlin.srcDir("src/samples/kotlin")

            dependencies {
                implementation(kotlin("test"))
            }
        }

        mingwX64Main {
            dependencies {
                implementation(libs.ktor.client.winhttp)
            }
        }

        linuxX64Main {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        appleMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}