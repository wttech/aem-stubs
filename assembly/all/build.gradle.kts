plugins {
    id("com.cognifide.aem.package")
    id("com.jfrog.bintray")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - All-In-One"

tasks {
    packageCompose {
        mergePackageProject(":core")
        mergePackageProject(":moco")
        mergePackageProject(":wiremock")

        installBundle("org.codehaus.groovy:groovy-all:2.4.15") {
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

bintrayOptions()
bintray { setPublications("maven") }
