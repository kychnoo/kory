plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
}

group = "io.kory"
version = "0.0.1"

kotlin {
    jvm()
    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)

            implementation(project(":core"))
            implementation(project(":openai"))
            implementation(project(":kory-ktor"))
        }

        jvmMain.dependencies {
            implementation(project(":kory-ktor-cio"))
        }
    }
}
