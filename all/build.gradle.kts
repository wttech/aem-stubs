plugins {
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - All-In-One"

tasks {
    packageCompose {
        installBundleProject(":ext")
        nestPackageProject(":core")
        nestPackageProject(":groovy")
        nestPackage("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
    }
}
