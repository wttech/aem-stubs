import org.gradle.api.Project
import com.jfrog.bintray.gradle.BintrayExtension

fun Project.bintrayOptions() = extensions.getByType(BintrayExtension::class.java).apply {
  user = (findProperty("bintray.user") ?: System.getenv("BINTRAY_USER"))?.toString()
  key = (findProperty("bintray.key") ?: System.getenv("BINTRAY_KEY"))?.toString()
  with(pkg) {
    repo = "maven-public"
    name = "aem-stubs"
    userOrg = "cognifide"
    setLicenses("Apache-2.0")
    vcsUrl = "https://github.com/Cognifide/aem-stubs.git"
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
