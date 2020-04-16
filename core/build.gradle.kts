plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Core"
