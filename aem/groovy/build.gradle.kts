plugins {
    id("java")
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

group = "com.company.wiremockonaem.aem"
description = "Wiremock on AEM - Groovy"


aem {
    tasks {
    }
}

dependencies {
    compileOnly(project(":aem:core"))
    compileOnly("com.icfolson.aem.groovy.console:aem-groovy-console:14.0.0")
}
