plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    // jcenter() removed: Bintray/JCenter was shut down in 2021 and no longer serves artifacts.
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:16.0.7")
    // com.neva.gradle:fork-plugin removed: its transitive dependency
    // com.neva.commons:gitignore-file-filter:1.0.0 was only ever published to JCenter
    // (now dead), so the plugin can no longer be resolved from any live repository.
    // It only powered the interactive local-instance setup GUI, not the package build.
    implementation("net.researchgate:gradle-release:3.0.2")
    implementation("com.netflix.nebula:nebula-project-plugin:10.1.2")
    implementation("com.github.breadmoirai:github-release:2.4.1")
}
