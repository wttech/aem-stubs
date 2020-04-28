plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock All-In-One"

tasks {
    packageCompose {
        installBundleProject(":wiremock:system")
        mergePackageProject(":core")
        mergePackageProject(":wiremock")

        installBundle("org.codehaus.groovy:groovy-all:2.4.15")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact("packageCompose"))
        }
    }
}
