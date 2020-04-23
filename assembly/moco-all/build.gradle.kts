plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco All-In-One"

tasks {
    packageCompose {
        mergePackageProject(":core")
        mergePackageProject(":moco")

        nestPackage("com.icfolson.aem.groovy.console:aem-groovy-console:14.0.0")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact("packageCompose"))
        }
    }
}
