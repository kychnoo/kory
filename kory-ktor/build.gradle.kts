plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
//    alias(libs.plugins.dokka)
}

group = "io.kory.ktor"
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
            implementation(libs.kotlinxIoCore)
            api(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.serialization.json)
            api(libs.ktor.client.logging)

            implementation(libs.kotlinxSerialization)
            implementation(libs.kotlinxCoroutines)
        }

        jvmMain
        nativeMain

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}