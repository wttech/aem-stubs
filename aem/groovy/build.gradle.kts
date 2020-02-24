plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
group = "com.company.wiremockonaem.aem"
description = "Wiremock on AEM - Groovy script example"

tasks.named("packageDeploy"){
    mustRunAfter(":aem:instanceSetup")
}
