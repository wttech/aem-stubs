plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco All-In-One"

tasks {
    packageCompose {
        // TODO mergePackageProject(":core")
        // TODO mergePackageProject(":moco")

        installBundle("org.apache.groovy:groovy:4.0.9")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}

