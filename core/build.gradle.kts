plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

group = "io.kory.core"
version = "0.0.1"

dokka {
    dokkaSourceSets.main {
        samples.from(
            project(":examples")
                .file("src/main/kotlin")
        )
    }
}

dependencies {
    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}