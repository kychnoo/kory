import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    `maven-publish`
}

group = "io.kory"
version = "0.0.1"

kotlin {
    jvmToolchain(21)

    android {
        namespace = "io.kory.${project.name.replace("-", ".")}"
        compileSdk = 37
        minSdk = 24

        withHostTest {  }

        localDependencySelection {
            selectBuildTypeFrom.set(listOf("debug", "release"))
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            url = "https://github.com/kychnoo/kory"
            licenses {
                name = "Apache-2.0"
                url = "https://opensource.org/license/Apache-2.0"
            }
            developers {
                developer {
                    id = "kychnoo"
                    name = "kychnoo"
                }
            }
            scm {
                url = "https://github.com/kychnoo/kory"
            }
        }
    }
}