import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    pmd
}

apply(from = rootProject.file("gradle/common.gradle.kts"))
description = "AEM Stubs - Wiremock"

dependencies {
    compileOnly(project(":core"))
}

aem {
    tasks {
        jar {
            bundle {
                exportPackage("com.github.tomakehurst.wiremock.*")
                excludePackage(
                        "junit.*",
                        "org.junit.*",
                        "com.github.tomakehurst.wiremock.junit.*",
                        "com.github.tomakehurst.wiremock.jetty92.*",
                        "com.github.tomakehurst.wiremock.jetty94.*",
                        "org.eclipse.jetty.servlets.gzip.*"
                )
                        embedPackage("io.pebbletemplates:pebble:3.1.0", "com.mitchellbosecke.pebble.*")
                embedPackage("org.unbescape:unbescape:1.0", "org.unbescape.*")
                embedPackage("org.checkerframework:checker-qual:3.3.0", "org.checkerframework.checker.index.qual.*")
                embedPackage("com.github.ben-manes.caffeine:caffeine:2.8.1", "com.github.benmanes.caffeine.cache.*",
                "com.github.benmanes.caffeine.base")
                embedPackage("org.apache.commons:commons-lang3:3.9", "org.apache.commons.lang3.*")
                embedPackage("org.apache.commons:commons-collections4:4.4", "org.apache.commons.collections4.*")
                embedPackage("com.github.tomakehurst:wiremock:2.21.0", "com.github.tomakehurst.wiremock.*")
                embedPackage("org.eclipse.jetty:jetty-servlets:9.4.20.v20190813", "org.eclipse.jetty.servlets")
                embedPackage("com.google.guava:guava:27.0.1-jre",
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
                        "com.google.thirdparty.publicsuffix"
                )
                embedPackage("org.checkerframework:checker-qual:2.11.0", "org.checkerframework.framework.qual;version=2.11.0", "org.checkerframework.checker.nullness.qual")
                embedPackage("com.google.errorprone:error_prone_parent:2.3.4", "com.google.errorprone.annotations", "com.google.errorprone.annotations.concurrent")
                embedPackage("com.google.guava:failureaccess:1.0.1", "com.google.common.util.concurrent.internal")
                embedPackage("xmlunit:xmlunit:1.6",
                        "org.custommonkey.xmlunit",
                        "org.custommonkey.xmlunit.examples",
                        "org.custommonkey.xmlunit.exceptions",
                        "org.custommonkey.xmlunit.jaxp13",
                        "org.custommonkey.xmlunit.util"
                )
                embedPackage("org.xmlunit:xmlunit-core:2.6.3", "org.xmlunit.*")
                embedPackage("com.jayway.jsonpath:json-path:2.4.0",
                        "com.jayway.jsonpath",
                        "com.jayway.jsonpath.internal.*",
                        "com.jayway.jsonpath.internal.filter",
                        "com.jayway.jsonpath.internal.path",
                        "com.jayway.jsonpath.spi.cache",
                        "com.jayway.jsonpath.spi.json",
                        "com.jayway.jsonpath.spi.mapper"
                )
                embedPackage("net.sf.jopt-simple:jopt-simple:5.0.4", "joptsimple.*")
                embedPackage("com.github.jknack:handlebars:4.1.2",
                        "com.github.jknack.handlebars",
                        "com.github.jknack.handlebars.cache",
                        "com.github.jknack.handlebars.context",
                        "com.github.jknack.handlebars.io",
                        "com.github.jknack.handlebars.internal.*",
                        "com.github.jknack.handlebars.helper"
                )
                embedPackage("org.apache.tapestry:tapestry-json:5.3.7", "org.codehaus.jettison.*")
                embedPackage("org.codehaus.jettison:jettison:1.4.0", "org.apache.tapestry5.json")
                embedPackage("net.minidev:json-smart:2.3",
                        "net.minidev.json",
                        "net.minidev.json.parser",
                        "net.minidev.asm",
                        "net.minidev.json.annotate",
                        "net.minidev.json.reader",
                        "net.minidev.asm.ex",
                        "net.minidev.json.writer"
                )
                embedPackage("org.ow2.asm:asm:7.0", "org.objectweb.asm")
                embedPackage("com.flipkart.zjsonpatch:zjsonpatch:0.4.9", "com.flipkart.zjsonpatch")
            }
        }

        packageCompose {
            installBundleProject(":core")
        }
    }
}
