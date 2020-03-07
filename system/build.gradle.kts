plugins {
    id("com.cognifide.aem.bundle")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - System Extension"

aem {
    tasks {
        bundleCompose {
            symbolicName = "com.adobe.cognifide.aem.stubs.system"
            fragmentHost = "system.bundle"
            exportPackage(
                    "sun.misc",
                    "com.sun.org.apache.xalan.internal.xsltc.trax",
                    "com.sun.org.apache.xerces.internal.jaxp"
            )
        }
    }
}
