import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package.sync")
    id("maven-publish")
    id("pmd")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = "AEM Stubs - Core"

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    jar {
        bundle {
            importPackage(
                "groovy.*;version=\"[4,7)\"",
                "org.codehaus.groovy.*;version=\"[4,7)\""
            )
            embedPackage("commons-io:commons-io:2.6", "org.apache.commons.io.*")
            embedPackage("org.apache.commons:commons-lang3:3.5", "org.apache.commons.lang3.*")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
