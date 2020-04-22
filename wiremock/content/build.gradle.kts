plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock Content"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")
    }
}
