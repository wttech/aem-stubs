plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:14.4.51")
    implementation("com.neva.gradle:fork-plugin:5.0.13")
    implementation("net.researchgate:gradle-release:2.6.0")
    implementation("com.netflix.nebula:nebula-project-plugin:7.0.9")
    implementation("com.github.breadmoirai:github-release:2.2.12")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
}
