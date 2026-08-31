plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinPluginSerialization)
}

kotlin {
    jvm()

    mingwX64() {
        binaries {
            executable {
                entryPoint = "io.kory.app.main"
                baseName = "kory-app"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":openai"))
            implementation(project(":kory-ktor"))

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }

        jvmMain.dependencies {
            implementation(project(":kory-ktor-cio"))
        }
    }
}
