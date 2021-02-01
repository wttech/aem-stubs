plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    id("com.jfrog.bintray")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - App"

tasks {
    packageCompose {
        mergePackageProject(":core")
        mergePackageProject(":moco")
        mergePackageProject(":wiremock")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact(tasks.packageCompose))
        }
    }
}

bintray { setPublications("maven") }
bintrayOptions()
