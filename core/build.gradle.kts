import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    id("org.owasp.dependencycheck")
    pmd
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Core"

tasks {
    jar {
        bundle {
            embedPackage("commons-io:commons-io:2.6", "org.apache.commons.io.*")
            embedPackage("org.apache.commons:commons-lang3:3.5", "org.apache.commons.lang3.*")
        }
    }
}
