plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:16.0.7")
    implementation("com.neva.gradle:fork-plugin:7.0.11")
    implementation("net.researchgate:gradle-release:3.0.2")
    implementation("com.netflix.nebula:nebula-project-plugin:10.1.2")
    implementation("com.github.breadmoirai:github-release:2.4.1")
}
