plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - App"

tasks {
    packageCompose {
        mergePackageProject(":faker")
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
