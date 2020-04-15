import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Moco"

dependencies {
    compileOnly(project(":core"))
}

aem {
    tasks {
        jar {
            bundle {
                embedPackage("com.github.dreamhead:moco-core:1.1.0", "com.github.dreamhead.moco.*", export = true)
                embedPackage("com.github.dreamhead:moco-runner:1.1.0")
                embedPackage("com.google.guava:guava:28.2-jre", "com.google.common.*")
                // embedPackage("com.jayway.jsonpath:json-path:2.4.0", "com.jayway.jsonpath.*") // TODO support json path
                excludePackage(
                        "sun.misc",
                        "com.jayway.jsonpath",
                        "com.sun.nio.file",
                        "org.apache.commons.cli",
                        "com.google.appengine.api",
                        "com.google.appengine.api.utils",
                        "com.google.apphosting.api",
                        "com.google.errorprone.annotations",
                        "com.google.errorprone.annotations.concurrent",
                        "com.google.thirdparty.publicsuffix",
                        "org.checkerframework.checker.nullness.qual"
                )
            }
        }

        packageCompose {
            installBundle("io.netty:netty-common:4.1.46.Final")
            installBundle("io.netty:netty-handler:4.1.46.Final")
            installBundle("io.netty:netty-buffer:4.1.46.Final")
            installBundle("io.netty:netty-codec:4.1.46.Final")
            installBundle("io.netty:netty-codec-http:4.1.46.Final")
            installBundle("io.netty:netty-transport:4.1.46.Final")
            installBundle("io.netty:netty-resolver:4.1.46.Final")
            installBundle("org.apache.servicemix.bundles:org.apache.servicemix.bundles.freemarker:2.3.29_1")
        }
    }
}
