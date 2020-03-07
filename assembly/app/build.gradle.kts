plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Application Only Package"

tasks {
    packageCompose {
        installBundleProject(":system")
        nestPackageProject(":core")
        nestPackageProject(":groovy-console")
    }
}
