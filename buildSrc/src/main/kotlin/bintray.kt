import org.gradle.api.Project
import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact

fun Project.bintrayOptions() {
  extensions.getByType(BintrayExtension::class.java).apply {
    user = (findProperty("bintray.user") ?: System.getenv("BINTRAY_USER"))?.toString()
    key = (findProperty("bintray.key") ?: System.getenv("BINTRAY_KEY"))?.toString()
    with(pkg) {
      repo = "maven-public"
      name = "aem-stubs"
      userOrg = "cognifide"
      setLicenses("Apache-2.0")
      vcsUrl = "https://github.com/wttech/aem-stubs.git"
      setLabels("aem", "cq", "mock", "stubs", "mocking", "stubbing", "wiremock", "moco")
      with(version) {
        name = project.version.toString()
        desc = "${project.description} ${project.version}"
        vcsTag = project.version.toString()
      }
    }
    publish = (project.findProperty("bintray.publish") ?: "true").toString().toBoolean()
    override = (project.findProperty("bintray.override") ?: "false").toString().toBoolean()
  }

  tasks.withType(BintrayUploadTask::class.java).configureEach {
    doFirst {
      extensions.getByType(PublishingExtension::class.java).publications
        .filterIsInstance<MavenPublication>()
        .forEach { publication ->
          val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
          if (moduleFile.exists()) {
            publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
              override fun getDefaultExtension() = "module"
            })
          }
        }
    }
  }
}
