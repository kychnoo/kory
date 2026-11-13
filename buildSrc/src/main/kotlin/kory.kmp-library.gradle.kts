import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    `maven-publish`
    id("com.vanniktech.maven.publish")
}

group = "io.github.kychnoo"
version = providers.gradleProperty("kory.version").getOrElse("0.0.1-SNAPSHOT")

kotlin {
    jvmToolchain(21)

    android {
        namespace = "io.github.kychnoo.${project.name.replace("-", ".")}"
        compileSdk = 37
        minSdk = 24

        withHostTest {  }

        localDependencySelection {
            selectBuildTypeFrom.set(listOf("debug", "release"))
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = group.toString(),
        artifactId = if (project.name.startsWith("kory")) project.name else "kory-${project.name}",
        version = version.toString(),
    )

    pom {
        name = "Kory ${project.name}"
        description = providers.gradleProperty("kory.description")
            .getOrElse("Kory - Kotlin Multiplatform library for working with AI providers (${project.name} module)")
        inceptionYear = "2026"
        url = "https://github.com/kychnoo/kory"

        licenses {
            license {
                name = "Apache-2.0"
                url = "https://opensource.org/license/Apache-2.0"
            }
        }

        developers {
            developer {
                id = "kychnoo"
                name = "kychnoo"
                url = "https://github.com/kychnoo"
            }
        }

        scm {
            url = "https://github.com/kychnoo/kory"
            connection = "scm:git:git://github.com/kychnoo/kory.git"
            developerConnection = "scm:git:ssh://git@github.com/kychnoo/kory.git"
        }
    }
}