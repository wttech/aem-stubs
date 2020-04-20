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

        nestPackage("com.icfolson.aem.groovy.console:aem-groovy-console:14.0.0")
    }
}
