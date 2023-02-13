plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco All-In-One"

tasks {
    packageCompose {
        from(project(":core").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":core:jar") { dirPath.set("/apps/stubs/core/install")}

        from(project(":moco").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":moco:jar") { dirPath.set("/apps/stubs/moco/install")}

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

