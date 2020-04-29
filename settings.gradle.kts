rootProject.name = "stubs"

include("system")
include("core")

include("moco")

include("wiremock")
include("wiremock:test")

include("assembly:moco-all")
include("assembly:moco-app")
include("assembly:wiremock-all")
include("assembly:wiremock-app")
