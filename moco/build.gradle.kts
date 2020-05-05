import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    pmd
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
                attribute("DynamicImport-Package", "*")
                importPackageSuffix.set("*;resolution:=optional")
                importPackage("javax.annotation;version=0.0.0")

                embedPackage("com.github.dreamhead:moco-core:1.1.0", "com.github.dreamhead.moco.*", export = true)
                embedPackage("com.github.dreamhead:moco-runner:1.1.0")
                embedPackage("com.google.guava:guava:28.2-jre", "com.google.common.*")
                embedPackage("com.jayway.jsonpath:json-path:2.4.0",
                        "com.jayway.jsonpath",
                        "com.jayway.jsonpath.internal.*",
                        "com.jayway.jsonpath.internal.filter",
                        "com.jayway.jsonpath.internal.path",
                        "com.jayway.jsonpath.spi.cache",
                        "com.jayway.jsonpath.spi.json",
                        "com.jayway.jsonpath.spi.mapper"
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
