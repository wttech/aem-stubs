plugins {
    id("com.neva.fork")
}

apply(from = "gradle/fork/props.gradle.kts")
description = "Wiremock on AEM - Root"
defaultTasks(":aem:ext:packageDeploy", ":aem:core:packageDeploy", ":aem:groovy:packageDeploy")

task("develop"){
    dependsOn(":aem:instanceSetup", ":aem:ext:packageDeploy", ":aem:core:packageDeploy", ":aem:groovy:packageDeploy")
}
