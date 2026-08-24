plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

group = "io.kory.ktor"
version = "unspecified"

dependencies {
    api(libs.ktor.client.core)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization.json)
    api(libs.ktor.client.logging)

    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinxCoroutines)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}