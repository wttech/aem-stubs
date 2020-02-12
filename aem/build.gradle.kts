plugins {
    id("com.cognifide.aem.instance")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "Wiremock on AEM - AEM"
aem {
    tasks {
        instanceSatisfy {
            // customizing CRX packages to be deployed as dependencies before built AEM application
            packages {
                "jetty.util" {resolve("org.eclipse.jetty:jetty-util:9.4.20.v20190813")}
                "jetty.servlets" {resolve("org.eclipse.jetty:jetty-io:9.4.20.v20190813")}
                "jetty.servlets" {resolve("org.eclipse.jetty:jetty-xml:9.4.20.v20190813")}
                "jetty.servlets" {resolve("org.eclipse.jetty:jetty-security:9.4.20.v20190813")}
                "jetty.servlets" {resolve("org.eclipse.jetty:jetty-servlets:9.4.20.v20190813")}
                "jetty.servlet" {resolve("org.eclipse.jetty:jetty-servlet:9.4.20.v20190813")}
                "jetty.server" {resolve("org.eclipse.jetty:jetty-server:9.4.20.v20190813")}
                "jetty.webapp" {resolve("org.eclipse.jetty:jetty-webapp:9.4.20.v20190813")}
                "jetty.http" {resolve("org.eclipse.jetty:jetty-http:9.4.20.v20190813")}
                "sun.misc" {resolve("net.sdruskat:net.sdruskat.fragment.sun.misc:1.0.0")}
                "groovy-console"("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
            }
        }
    }
}
