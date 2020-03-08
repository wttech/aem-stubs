plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco App"

tasks {
    packageCompose {
        archiveClassifier.set("moco")

        nestPackageProject(":core")
        nestPackageProject(":moco")
        nestPackageProject(":moco:content")
    }
}
