plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock All-In-One"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")

        mergePackageProject(":wiremock:all.config")
        nestPackageProject(":core")
        nestPackageProject(":wiremock")
        nestPackageProject(":wiremock:content")
        installBundleProject(":wiremock:system")

        nestPackage("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
    }
}
