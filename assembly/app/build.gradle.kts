plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - App"

tasks {
    packageCompose {
        // TODO mergePackageProject(":core")
        // TODO mergePackageProject(":moco")
        // TODO mergePackageProject(":wiremock")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}

