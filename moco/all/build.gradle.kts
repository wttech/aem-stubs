plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco All-In-One"

tasks {
    packageCompose {
        archiveClassifier.set("moco")

        mergePackageProject(":core")
        mergePackageProject(":moco")
        mergePackageProject(":moco:content")

        nestPackage("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
    }
}
