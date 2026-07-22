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

        installBundle("io.netty:netty-common:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-buffer:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-resolver:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-transport:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-transport-native-unix-common:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-handler:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-codec-base:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-codec-compression:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-codec-http:4.2.12.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("org.freemarker:freemarker:2.3.34") { dirPath.set("/apps/stubs/moco/install") }

        installBundle("org.apache.groovy:groovy:4.0.9") { dirPath.set("/apps/stubs/moco/install")}
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}

