plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco Content"

tasks {
    packageCompose {
        archiveClassifier.set("moco")
    }
}
