plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - WireMock App"

tasks {
    packageCompose {
        from(project(":core").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":core:jar") { dirPath.set("/apps/stubs/core/install") }

        from(project(":wiremock").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":wiremock:jar") { dirPath.set("/apps/stubs/wiremock/install") }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}
