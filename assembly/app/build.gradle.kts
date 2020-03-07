plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Application"

tasks {
    packageCompose {
        installBundleProject(":system")
        nestPackageProject(":core")
    }
}
