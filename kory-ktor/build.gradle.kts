plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.4.10"
}

group = "io.kory.ktor"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "3.5.2"

    implementation("io.ktor:ktor-client-core:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}