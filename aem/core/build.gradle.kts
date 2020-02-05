plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "Wiremock on AEM - Core"

aem {
    tasks {
        bundleExportEmbed("com.github.tomakehurst:wiremock-jre8:2.26.0", "com.github.tomakehurst.*")
        bundlePrivateEmbed("com.google.guava:guava:27.0.1-jre",
                "com.google.common.collect",
                "com.google.common.net",
                "com.google.common.base",
                "com.google.common.graph",
                "com.google.common.escape",
                "com.google.common.hash",
                "com.google.common.io",
                "com.google.common.math",
                "com.google.common.primitives",
                "com.google.thirdparty.publicsuffix",
                "com.google.common.base.internal",
                "com.google.common.cache",
                "com.google.common.util.concurrent"
        )
        bundlePrivateEmbed("com.google.guava:failureaccess:1.0.1",
                "com.google.common.util.concurrent.internal"
        )

        bundlePrivateEmbed("com.google.errorprone:error_prone_parent:2.3.4", "com.google.errorprone.annotations",
                "com.google.errorprone.annotations.concurrent")
        bundlePrivateEmbed("org.checkerframework:checker-qual:2.11.0",
                "org.checkerframework.checker.nullness.qual;version=2.11.0")
        bundlePrivateEmbed("com.github.jknack:handlebars:4.1.2",
                "com.github.jknack.handlebars.*",
                "com.github.jknack.handlebars.internal.*"
                )
        bundlePrivateEmbed("com.github.jknack:handlebars-jackson2:4.1.2",
                "com.github.jknack.handlebars.internal.js"
        )

        bundleCompose {
            //excludePackage("sun.misc", "org.checkerframework.checker.nullness.qual")
            importPackage("!net.javacrumbs.jsonunit.core", "!org.xmlunit", "*")
            activator = "com.company.aem.wiremockonaem.aem.activator.Activator"
        }
    }
}

dependencies {
    compileOnly("com.github.tomakehurst:wiremock:2.26.0")

}
