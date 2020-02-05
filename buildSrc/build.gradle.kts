plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    jcenter()
    gradlePluginPortal()
    maven("http://dl.bintray.com/cognifide/maven-public")
    maven("https://dl.bintray.com/neva-dev/maven-public")
}

dependencies {
    implementation("com.cognifide.gradle:aem-plugin:10.1.5")
    implementation("com.neva.gradle:fork-plugin:4.2.0")
    implementation("org.osgi:osgi.core:6.0.0")
    implementation("org.osgi:osgi.cmpn:6.0.0")
}
