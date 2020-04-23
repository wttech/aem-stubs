plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco App"

tasks {
    packageCompose {
        nestPackageProject(":core")
        nestPackageProject(":moco")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact("packageCompose"))
        }
    }
}
