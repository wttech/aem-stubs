plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    id("com.jfrog.bintray")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - All-In-One"

tasks {
    packageCompose {
        mergePackageProject(":core")
        mergePackageProject(":moco")
        mergePackageProject(":wiremock")

        installBundle("org.apache.groovy:groovy:4.0.9") {
            dirPath.set("/apps/stubs/groovy")
            vaultFilterDir()
        }
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
