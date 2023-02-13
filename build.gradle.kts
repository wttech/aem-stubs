plugins {
    id("com.neva.fork")
    id("com.cognifide.aem.instance.local")
    id("net.researchgate.release")
    id("com.github.breadmoirai.github-release")
}

apply(from = "gradle/fork/props.gradle.kts")
apply(from = "gradle/common.gradle.kts")

description = "AEM Stubs"
defaultTasks(":develop")

common {
    tasks {
        registerSequence("develop") {
            dependsOn(
                    ":instanceSetup",
                    ":assembly:wiremock-all:packageDeploy",
                    ":wiremock:integrationTest",
            )
        }
    }
}


aem {
    instance {
        provisioner {
            gradle.projectsEvaluated {
                enableCrxDe()
                deployPackage("com.neva.felix:search-webconsole-plugin:1.3.0")
                deployPackage(project(":assembly:wiremock-all").tasks.named("packageCompose"))
                step("enableStubsSamples") {
                    version.set("2")
                    sync {
                        osgi.configure("com.cognifide.aem.stubs.core.ConfigurableStubManager", mapOf(
                                "excluded.paths" to listOf("**/internals/*")
                        ))
                    }
                }
            }
        }
    }
}

githubRelease {
    owner("wttech")
    repo("aem-stubs")
    token((findProperty("github.token") ?: "").toString())
    tagName(project.version.toString())
    releaseName(project.version.toString())
    draft((findProperty("github.draft") ?: "false").toString().toBoolean())
    overwrite((findProperty("github.override") ?: "true").toString().toBoolean())

    gradle.projectsEvaluated {
        releaseAssets(listOf(
                ":assembly:wiremock-all:packageCompose",
                ":assembly:wiremock-app:packageCompose",
        ).map { rootProject.tasks.getByPath(it) })
    }

    val prerelease = (findProperty("github.prerelease") ?: "true").toString().toBoolean()
    if (prerelease) {
        prerelease(true)
    } else {
        body { """
        |# What's new
        |
        |TBD
        |
        |# Upgrade notes
        |
        |Nothing to do.
        |
        |# Contributions
        |
        |None.
        """.trimMargin()
        }
    }
}

tasks {
    named("githubRelease") {
        mustRunAfter(release)
    }
    register("fullRelease") {
        dependsOn("release", "githubRelease")
    }
    instanceProvision {
        dependsOn(":assembly:wiremock-all:packageCompose")
    }
}
