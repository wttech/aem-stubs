plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - WireMock All-In-One"

tasks {
    packageCompose {
        from(project(":core").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":core:jar") { dirPath.set("/apps/stubs/core/install")}

        from(project(":wiremock").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":wiremock:jar") { dirPath.set("/apps/stubs/wiremock/install")}

        installBundle("org.apache.groovy:groovy:4.0.9") { dirPath.set("/apps/stubs/wiremock/install") }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}
