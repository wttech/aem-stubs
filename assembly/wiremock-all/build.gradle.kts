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

        nestPackage("com.icfolson.aem.groovy.console:aem-groovy-console:14.0.0")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(common.publicationArtifact("packageCompose"))
        }
    }
}
