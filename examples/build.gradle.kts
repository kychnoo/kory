plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

group = "io.kory"
version = "unspecified"

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)

    implementation(project(":core"))
    implementation(project(":openai"))
    implementation(project(":kory-ktor"))
    implementation(project(":kory-ktor-cio"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}