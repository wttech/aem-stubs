plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock App"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")

        installBundleProject(":wiremock:system")
        mergePackageProject(":core")
        mergePackageProject(":wiremock")
        mergePackageProject(":wiremock:content")
    }
}
