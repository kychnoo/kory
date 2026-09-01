plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
}

group = "io.kory.ktor-cio"
version = "0.0.1"

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
            api(project(":kory-ktor"))
            implementation(libs.ktor.client.cio)
        }

        jvmMain
    }
}