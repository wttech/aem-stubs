plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - All-In-One Package"

tasks {
    packageCompose {
        installBundleProject(":system")
        nestPackageProject(":core")
        nestPackageProject(":groovy-console")
        nestPackage("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
    }
}
