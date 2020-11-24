import static com.cognifide.aem.stubs.wiremock.WireMockUtils.*
import com.cognifide.aem.stubs.wiremock.transformers.DynamicParameterProvider

stubs.server.with {
    // template body
    stubFor(get(urlPathEqualTo("/templated-pebble"))
            .willReturn(aResponse()
                    .withBody("{{request.path[0]}}")
                    .withTransformers("pebble-response-template")))

    stubFor(get(urlPathEqualTo("/templated-handlebars"))
            .willReturn(aResponse()
                    .withBody("{{request.path.[0]}}")))

    // template file
    stubFor(get(urlPathEqualTo("/templated-file"))
            .willReturn(aResponse()
                    .withBodyFile("samples/templating/template.json")
                    .withHeader("Content-Type", "application/json")
                    .withTransformerParameter("message", "Hello Templates!")))

    stubFor(get(urlPathEqualTo("/header-body-file-pebble"))
            .willReturn(aResponse()
                    .withBodyFile("{{request.headers[\"X-WM-Body-File\"]}}")
                    .withHeader("Content-Type", "application/json")
                    .withTransformers("pebble-response-template")))

    stubFor(get(urlPathEqualTo("/header-body-file-handlebars"))
            .willReturn(aResponse()
                    .withBodyFile("{{request.headers.X-WM-Body-File}}")
                    .withHeader("Content-Type", "application/json")))

    // template with dynamic parameter
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", dynamicParameter({new Date()}))
                    .withTransformers("pebble-response-template")))

    // proxy templates
    stubFor(get(urlPathEqualTo("/templated/api"))
            .willReturn(aResponse()
                    .proxiedFrom("{{request.headers[\"X-WM-Proxy-Url\"]}}")))

}
