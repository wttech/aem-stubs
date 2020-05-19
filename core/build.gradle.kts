import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    pmd
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Core"
dependencies {
    compileOnly(project(":faker"))
}
