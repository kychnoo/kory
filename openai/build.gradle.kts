plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.dokka)
}

group = "io.kory.openai"
version = "unspecified"


dependencies {
    api(project(":core"))
    api(project(":kory-ktor"))
    api(project(":examples"))

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