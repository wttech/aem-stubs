plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - All-In-One"

tasks {
    packageCompose {
        // TODO mergePackageProject(":core")
        // TODO mergePackageProject(":moco")
        // TODO mergePackageProject(":wiremock")

        installBundle("org.apache.groovy:groovy:4.0.9") {
            dirPath.set("/apps/stubs/groovy")
            vaultFilterDir()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}
