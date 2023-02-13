plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - WireMock App"

tasks {
    packageCompose {
        // TODO mergePackageProject(":core")
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
