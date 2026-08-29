plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
//    alias(libs.plugins.dokka)
}

group = "io.kory.core"
version = "0.0.1"

//dokka {
//    dokkaSourceSets.main {
//        samples.from(
//            project(":examples")
//                .file("src/main/kotlin")
//        )
//    }
//}

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
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerialization)
            implementation(libs.kotlinxIoCore)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}