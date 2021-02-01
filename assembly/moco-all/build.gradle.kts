plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
    id("com.jfrog.bintray")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco All-In-One"

tasks {
    packageCompose {
        mergePackageProject(":core")
        mergePackageProject(":moco")

        installBundle("org.codehaus.groovy:groovy-all:2.4.15")
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
