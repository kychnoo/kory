plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
}

group = "io.kory.ktor-cio"
version = "unspecified"

dependencies {
    api(project(":kory-ktor"))

    implementation(libs.ktor.client.cio)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}