plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "Wiremock on AEM - Core"

aem {
    tasks {
        bundlePrivateEmbed("com.github.tomakehurst:wiremock-jre8:2.21.0")
        bundlePrivateEmbed("com.google.guava:guava:27.0.1-jre",
                "com.google.common.base",
                "com.google.common.base.internal",
                "com.google.common.collect",
                "com.google.common.net",
                "com.google.common.escape",
                "com.google.common.hash",
                "com.google.common.io",
                "com.google.common.graph",
                "com.google.common.math",
                "com.google.common.primitives",
                "com.google.thirdparty.publicsuffix")


        bundlePrivateEmbed("org.checkerframework:checker-qual:2.11.0",
                "org.checkerframework.framework.qual;version=2.11.0", "org.checkerframework.checker.nullness.qual")
        bundlePrivateEmbed("com.google.errorprone:error_prone_parent:2.3.4", "com.google.errorprone.annotations",
                "com.google.errorprone.annotations.concurrent")
        bundlePrivateEmbed("com.google.guava:failureaccess:1.0.1",
                "com.google.common.util.concurrent.internal"
        )

        bundleCompose {
            activator = "com.company.aem.wiremockonaem.aem.activator.Activator"
        }
    }
}

dependencies {
    compileOnly("com.github.tomakehurst:wiremock-jre8:2.21.0")

}
