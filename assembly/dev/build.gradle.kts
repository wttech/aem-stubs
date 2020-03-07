plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Devolopment Package"

tasks {
    packageCompose {
        nestPackageProject(":core")
        nestPackageProject(":groovy-console")
    }
}
