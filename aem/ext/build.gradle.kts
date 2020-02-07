plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Ext"


aem {
    tasks {
        bundleCompose {
            fragmentHost = "system.bundle"
            exportPackage("com.sun.org.apache.xalan.internal.xsltc.trax",
                    "com.sun.org.apache.xerces.internal.jaxp")
        }
    }
}
