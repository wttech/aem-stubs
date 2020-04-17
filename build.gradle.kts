plugins {
    id("com.neva.fork")
    id("com.cognifide.aem.instance.local")
}

apply(from = "gradle/fork/props.gradle.kts")
apply(from = "gradle/common.gradle.kts")

description = "AEM Stubs"
defaultTasks(":wiremock:app:packageDeploy")

aem {
    instance {
        satisfier {
            packages {
                "tool.groovy-console"("https://github.com/icfnext/aem-groovy-console/releases/download/14.0.0/aem-groovy-console-14.0.0.zip")
            }
        }
        provisioner {
            step("enable-crxde") {
                step("enable-crxde") {
                    description = "Enables CRX DE"
                    condition { once() && instance.env != "prod" }
                    sync {
                        osgi.configure("org.apache.sling.jcr.davex.impl.servlets.SlingDavExServlet", mapOf(
                                "alias" to "/crx/server"
                        ))
                    }
                }
            }
        }
    }
}
