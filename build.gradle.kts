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
                    ":assembly:all:packageDeploy",
                    ":wiremock:integrationTest",
                    ":moco:integrationTest"
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
                deployPackage(project(":assembly:all").tasks.named("packageCompose"))
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
                ":assembly:all:packageCompose",
                ":assembly:app:packageCompose",
                ":assembly:wiremock-all:packageCompose",
                ":assembly:wiremock-app:packageCompose",
                ":assembly:moco-all:packageCompose",
                ":assembly:moco-app:packageCompose"
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

    afterReleaseBuild {
        dependsOn(
            // Jars
            ":core:bintrayUpload",
            ":wiremock:bintrayUpload",
            ":moco:bintrayUpload",

            // ZIPs
            ":assembly:all:bintrayUpload",
            ":assembly:app:bintrayUpload",
            ":assembly:moco-all:bintrayUpload",
            ":assembly:moco-app:bintrayUpload",
            ":assembly:wiremock-all:bintrayUpload",
            ":assembly:wiremock-app:bintrayUpload"
        )
    }

    register("fullRelease") {
        dependsOn("release", "githubRelease")
    }

    instanceProvision {
        dependsOn(":assembly:all:packageCompose")
    }
}
