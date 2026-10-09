@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("kory.kmp-library")
    alias(libs.plugins.dokka)
}

kotlin {
    jvm()
    jvmToolchain(21)

    android {
        optimization {
            minify = true

            consumerKeepRules.apply {
                publish = true

                files("consumer-rules.pro")
            }
        }
    }

    applyDefaultHierarchyTemplate {
        common {
            group("jvmShared") {
                withJvm()
                withCompilations { it.platformType == KotlinPlatformType.androidJvm }
            }
        }
    }

    sourceSets {
        getByName("jvmSharedMain") {
            dependencies {
                api(project(":kory-ktor"))

                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}