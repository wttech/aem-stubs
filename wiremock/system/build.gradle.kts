import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Wiremock System Extension"

aem {
    tasks {
        jar {
            bundle {
                symbolicName = "com.adobe.cognifide.aem.stubs.wiremock.system"
                fragmentHost = "system.bundle"
                exportPackage(
                        "sun.misc",
                        "com.sun.org.apache.xalan.internal.xsltc.trax",
                        "com.sun.org.apache.xerces.internal.jaxp"
                )
            }
        }
    }
}
