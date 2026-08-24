plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

group = "io.kory.core"
version = "unspecified"

dependencies {
    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)

    api(project(":examples"))

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}