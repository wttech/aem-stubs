plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock App"

tasks {
    packageCompose {
        installBundleProject(":wiremock:system")
        mergePackageProject(":core")
        mergePackageProject(":wiremock")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact("packageCompose"))
        }
    }
}
