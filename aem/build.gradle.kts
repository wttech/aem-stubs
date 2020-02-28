plugins {
    id("com.cognifide.aem.instance")
}
apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "Stubs on AEM"
aem {
    tasks {
        instanceSatisfy {
            // customizing CRX packages to be deployed as dependencies before built AEM application
            packages {
                "sun.misc" {resolve("net.sdruskat:net.sdruskat.fragment.sun.misc:1.0.0")}
                "groovy-console"("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
            }
        }
    }
}
