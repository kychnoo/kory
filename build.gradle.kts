plugins {
    alias(libs.plugins.kotlinPluginSerialization) apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":core"))
    dokka(project(":kory-ktor"))
    dokka(project(":kory-ktor-cio"))
    dokka(project(":openai"))
}

dokka {
    dokkaPublications.html {
        moduleName.set("Kory")
        outputDirectory.set(
            layout.buildDirectory.dir("dokka/html")
        )
    }
    dokkaSourceSets.configureEach {
        samples.from(
            project(":examples").file("src/main/kotlin")
        )
    }
}