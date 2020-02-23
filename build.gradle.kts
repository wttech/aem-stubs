plugins {
    id("com.neva.fork")
}

apply(from = "gradle/fork/props.gradle.kts")
description = "Wiremock on AEM - Root"
defaultTasks(":aem:instanceSetup", ":aem:core:packageDeploy")

