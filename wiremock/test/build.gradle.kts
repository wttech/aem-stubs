import com.cognifide.gradle.aem.bundle.tasks.bundle

plugins {
    id("java")
}

apply(from = rootProject.file("gradle/common.gradle.kts"))

dependencies {
    compileOnly(project(":core"))

    testImplementation("io.rest-assured:rest-assured:3.3.0")
    testImplementation("io.rest-assured:json-schema-validator:3.3.0")

}
