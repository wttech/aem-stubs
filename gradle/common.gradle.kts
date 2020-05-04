import com.cognifide.gradle.aem.AemExtension

group = "com.cognifide.aem.stubs"

repositories {
    jcenter()
    mavenCentral()
    maven("https://repo.adobe.com/nexus/content/groups/public")
    maven("https://dl.bintray.com/acs/releases")
}

plugins.withId("maven-publish") {
    configure<PublishingExtension> {
        repositories {
            maven("https://nexus.cognifide.com/content/repositories/cognifide-internal") {
                name = "internal"
                credentials {
                    username = project.findProperty("releaseUsername")?.toString()
                    password = project.findProperty("releasePassword")?.toString()
                }
            }
        }
    }
}

plugins.withId("com.cognifide.aem.common") {
    configure<AemExtension> {
        `package` {
            commonDir.set(rootProject.file("src/aem/package"))
            validator {
                base("com.adobe.acs:acs-aem-commons-oakpal-checks:4.3.4")
            }
        }
    }
}

plugins.withId("com.cognifide.aem.bundle") {

    dependencies {
        "compileOnly"("org.osgi:osgi.cmpn:6.0.0")
        "compileOnly"("org.osgi:org.osgi.core:6.0.0")
        "compileOnly"("javax.servlet:servlet-api:2.5")
        "compileOnly"("javax.servlet:jsp-api:2.0")
        "compileOnly"("javax.jcr:jcr:2.0")
        "compileOnly"("org.slf4j:slf4j-api:1.7.25")
        "compileOnly"("org.apache.geronimo.specs:geronimo-annotation_1.3_spec:1.0")
        "compileOnly"("org.apache.geronimo.specs:geronimo-atinject_1.0_spec:1.0")
        "compileOnly"("org.apache.sling:org.apache.sling.api:2.16.4")
        "compileOnly"("org.apache.sling:org.apache.sling.jcr.api:2.4.0")
        "compileOnly"("com.google.guava:guava:15.0")

        "compileOnly"("org.codehaus.groovy:groovy-all:2.4.15")
        "compileOnly"("org.apache.felix:org.apache.felix.http.servlet-api:1.1.2")
        "compileOnly"("commons-io:commons-io:2.6")
        "compileOnly"("org.apache.commons:commons-lang3:3.6")
    }
}

plugins.withId("java") {

    dependencies {
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.3.2")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.3.2")
        "testImplementation"("io.wcm:io.wcm.testing.aem-mock.junit5:2.3.2")
    }

    tasks {
        withType<Test>().configureEach {
            failFast = true
            useJUnitPlatform()
            testLogging.showStandardStreams = true
        }
    }

}

plugins.withId("org.gradle.pmd") {
    configure<PmdExtension> {
        isConsoleOutput = true
        ruleSets = listOf()
        ruleSetFiles = files(rootProject.file("pmd.xml"))
        rulePriority = 3 // TODO 5
    }
}
