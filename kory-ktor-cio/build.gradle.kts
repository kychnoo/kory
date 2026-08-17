plugins {
    kotlin("jvm")
}

group = "io.kory.ktor-cio"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    var ktorVersion = "3.4.1"

    implementation(project(":kory-ktor"))

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:${ktorVersion}")

    implementation("ch.qos.logback:logback-classic:1.6.3")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}