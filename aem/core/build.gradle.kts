plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "Wiremock on AEM - Core"

aem {
    tasks {
        bundlePrivateEmbed("com.github.tomakehurst:wiremock:2.1.6",
                "com.github.tomakehurst.wiremock",
                "com.github.tomakehurst.wiremock.servlet",
                "com.github.tomakehurst.wiremock.common",
                "com.github.tomakehurst.wiremock.core",
                "com.github.tomakehurst.wiremock.extension",
                "com.github.tomakehurst.wiremock.extension.responsetemplating",
                "com.github.tomakehurst.wiremock.admin",
                "com.github.tomakehurst.wiremock.admin.model",
                "com.github.tomakehurst.wiremock.admin.tasks",
                "com.github.tomakehurst.wiremock.global",
                "com.github.tomakehurst.wiremock.verification.*",
                "com.github.tomakehurst.wiremock.stubbing",
                "com.github.tomakehurst.wiremock.recording",
                "com.github.tomakehurst.wiremock.http",
                "com.github.tomakehurst.wiremock.http.trafficlistener",
                "com.github.tomakehurst.wiremock.jetty9",
                "com.github.tomakehurst.wiremock.client",
                "com.github.tomakehurst.wiremock.matching",
                "com.github.tomakehurst.wiremock.security",
                "com.github.tomakehurst.wiremock.standalone",
                "com.github.tomakehurst.wiremock.extension.responsetemplating.helpers"
        )
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
        bundlePrivateEmbed("xmlunit:xmlunit:1.6",
                "org.custommonkey.xmlunit",
                "org.custommonkey.xmlunit.examples",
                        "org.custommonkey.xmlunit.exceptions",
                        "org.custommonkey.xmlunit.jaxp13",
                        "org.custommonkey.xmlunit.util")
        bundlePrivateEmbed("org.xmlunit:xmlunit-core:2.6.3", "org.xmlunit.*")
        bundlePrivateEmbed("com.jayway.jsonpath:json-path:2.4.0",
                "com.jayway.jsonpath",
                "com.jayway.jsonpath.internal.*",
                "com.jayway.jsonpath.internal.filter",
                "com.jayway.jsonpath.internal.path",
                "com.jayway.jsonpath.spi.cache",
                "com.jayway.jsonpath.spi.json",
                "com.jayway.jsonpath.spi.mapper")
        bundlePrivateEmbed("net.sf.jopt-simple:jopt-simple:5.0.4", "joptsimple.*")


        bundlePrivateEmbed("com.github.jknack:handlebars:4.1.2",
                "com.github.jknack.handlebars",
                "com.github.jknack.handlebars.cache",
                "com.github.jknack.handlebars.context",
                "com.github.jknack.handlebars.io",
                "com.github.jknack.handlebars.internal.*",
                "com.github.jknack.handlebars.helper"
        )
        bundlePrivateEmbed("org.apache.tapestry:tapestry-json:5.3.7",
                "org.codehaus.jettison.*")

        bundlePrivateEmbed("org.codehaus.jettison:jettison:1.4.0",
                "org.apache.tapestry5.json")

        bundlePrivateEmbed("net.minidev:json-smart:2.3",
                "net.minidev.json",
                "net.minidev.json.parser",
                "net.minidev.asm",
                "net.minidev.json.annotate",
                "net.minidev.json.reader",
                "net.minidev.asm.ex",
                "net.minidev.json.writer")

        bundlePrivateEmbed("org.ow2.asm:asm:7.0",
                "org.objectweb.asm")
        bundlePrivateEmbed("com.flipkart.zjsonpatch:zjsonpatch:0.4.9",
                "com.flipkart.zjsonpatch")
/*
        bundlePrivateEmbed("junit:junit:4.12",
                "junit.framework",
                "junit.extensions",
                "junit.runner",
                "org.junit.runner",
                "org.junit",
                "org.junit.internal",
                "org.junit.internal.builders",
                "org.junit.internal.requests",
                "org.junit.internal.runners",
                "org.junit.runner.manipulation",
                "org.junit.runner.notification",
                "org.junit.runners",
                "org.junit.runners.model",
                "org.junit.internal.runners.model",
                "org.junit.internal.runners.rules",
                "org.junit.internal.runners.statements",
                "org.junit.runners.parameterized",
                "org.junit.validator",
                "org.junit.rules",
                "org.junit.internal.matchers",
                "org.junit.matchers")
        bundlePrivateEmbed("org.hamcrest:hamcrest-all:1.3",
                "org.hamcrest.*")


        bundlePrivateEmbed("org.jmock:jmock:2.5.1",
                "org.jmock.core")
        bundlePrivateEmbed("org.jmock:jmock-junit4:2.5.1",
                "org.jmock.core")
*/


        bundleCompose {
            activator = "com.company.aem.wiremockonaem.aem.activator.Activator"
            importPackages = listOf("!junit.framework", "!org.junit", "!org.junit.internal", "!com.github.tomakehurst.wiremock.junit", "*")
        }
    }
}

dependencies {
    compileOnly("com.github.tomakehurst:wiremock-jre8:2.21.0")

}
