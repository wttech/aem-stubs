plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock Content"

tasks {
    packageCompose {
        archiveClassifier.set("wiremock")
    }
}
