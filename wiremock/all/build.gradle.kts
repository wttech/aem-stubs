plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock All-In-One"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")

        installBundleProject(":wiremock:system")
        mergePackageProject(":core")
        mergePackageProject(":wiremock")
        mergePackageProject(":wiremock:content")

        nestPackage("com.icfolson.aem.groovy.console:aem-groovy-console:14.0.0")
    }
}
