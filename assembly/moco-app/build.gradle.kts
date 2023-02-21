plugins {
    id("com.cognifide.aem.package")
    `maven-publish`
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco App"

tasks {
    packageCompose {
        from(project(":core").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":core:jar") { dirPath.set("/apps/stubs/core/install")}

        from(project(":moco").layout.projectDirectory.dir("src/main/content"))
        installBundleBuilt(":moco:jar") { dirPath.set("/apps/stubs/moco/install")}

        installBundle("io.netty:netty-common:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-handler:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-buffer:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-codec:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-codec-http:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-transport:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("io.netty:netty-resolver:4.1.46.Final") { dirPath.set("/apps/stubs/moco/install") }
        installBundle("org.apache.servicemix.bundles:org.apache.servicemix.bundles.freemarker:2.3.29_1") { dirPath.set("/apps/stubs/moco/install") }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.packageCompose)
        }
    }
}
