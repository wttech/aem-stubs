import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("com.cognifide.aem.bundle")
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.package.sync")
    pmd
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

description = """Java Faker"""


tasks {
    jar {
        bundle {
            attribute("DynamicImport-Package", "*")
            importPackageSuffix.set("*;resolution:=optional")
//            /exportPackage("com.github.tomakehurst.wiremock.*")
            embedPackage("org.yaml:snakeyaml:1.23:android", "org.yaml.*")
            embedPackage("com.github.mifmif:generex:1.0.2", "com.github.mifmif.*")
        }
    }
}
