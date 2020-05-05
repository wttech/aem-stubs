import static com.cognifide.aem.stubs.wiremock.WireMockUtils.*

stubs.server.with {
    // template body
    stubFor(get(urlPathEqualTo("/templated"))
            .willReturn(aResponse()
                    .withBody("{{request.path[0]}}")
                    .withTransformers("pebble-response-template")))

    // template file
    stubFor(get(urlPathEqualTo("/templated-file"))
            .willReturn(aResponse()
                    .withBodyFile("samples/templating/template.json")
                    .withHeader("Content-Type", "application/json")
                    .withTransformerParameter("message", "Hello Templates!")
                    .withTransformers("pebble-response-template")))

    stubFor(get(urlPathEqualTo("/header-body-file"))
            .willReturn(aResponse()
                    .withBodyFile("{{request.headers[\"X-WM-Body-File\"]}}")
                    .withHeader("Content-Type", "application/json")
                    .withTransformers("pebble-response-template")))

    // template file - file name passed as http header
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", {new Date()})
                    .withTransformers("pebble-response-template")))

    // proxy templates
    stubFor(get(urlPathEqualTo("/templated/api"))
            .willReturn(aResponse()
                    .proxiedFrom("{{request.headers[\"X-WM-Proxy-Url\"]}}")
                    .withTransformers("response-template")))

}
