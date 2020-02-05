import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

group = "com.company.aem.wiremockonaem"

repositories {
    jcenter()
    maven("https://repo.adobe.com/nexus/content/groups/public")
}

plugins.withId("com.cognifide.aem.bundle") {

    dependencies {
        "compileOnly"( "org.osgi:osgi.cmpn:6.0.0")
        "compileOnly"( "org.osgi:org.osgi.core:6.0.0")
        "compileOnly"( "javax.servlet:servlet-api:2.5")
        "compileOnly"( "javax.servlet:jsp-api:2.0")
        "compileOnly"( "javax.jcr:jcr:2.0")
        "compileOnly"( "org.slf4j:slf4j-api:1.7.25")
        "compileOnly"( "org.apache.geronimo.specs:geronimo-atinject_1.0_spec:1.0")
        "compileOnly"( "org.apache.sling:org.apache.sling.api:2.16.4")
        "compileOnly"( "org.apache.sling:org.apache.sling.jcr.api:2.4.0")
        "compileOnly"( "org.apache.sling:org.apache.sling.models.api:1.3.6")
        "compileOnly"( "org.apache.sling:org.apache.sling.settings:1.3.8")
        "compileOnly"( "com.google.guava:guava:15.0")
        "compileOnly"( "com.google.code.gson:gson:2.8.2")
        "compileOnly"( "joda-time:joda-time:2.9.1")

        "compileOnly"("com.adobe.aem:uber-jar:${Aem.VERSION}:apis")
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
            testLogging {
                events = setOf(TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.SHORT
            }
        }
    }

}
