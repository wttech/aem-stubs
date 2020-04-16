plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock All-In-One"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")

        installBundleProject(":wiremock:system")
        nestPackageProject(":core")
        nestPackageProject(":wiremock")
        nestPackageProject(":wiremock:content")

        mergePackageProject(":wiremock:all.config")
        nestPackage("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
    }
}
