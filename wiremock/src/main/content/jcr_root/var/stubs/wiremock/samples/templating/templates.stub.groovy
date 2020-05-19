import static com.cognifide.aem.stubs.wiremock.WireMockUtils.*

stubs.server.with {
    // template body
    stubFor(get(urlPathEqualTo("/templated"))
            .willReturn(aResponse()
                    .withBody("{{request.path[0]}}")))

    stubFor(get(urlPathEqualTo("/faker"))
            .willReturn(aResponse()
                    .withBody("title: {{parameters.title}}")
                    .withTransformerParameter("title", {faker.book().title()})))

    // template file
    stubFor(get(urlPathEqualTo("/templated-file"))
            .willReturn(aResponse()
                    .withBodyFile("samples/templating/template.json")
                    .withHeader("Content-Type", "application/json")
                    .withTransformerParameter("message", "Hello Templates!")))

    stubFor(get(urlPathEqualTo("/header-body-file"))
            .willReturn(aResponse()
                    .withBodyFile("{{request.headers[\"X-WM-Body-File\"]}}")
                    .withHeader("Content-Type", "application/json")))

    // template file - file name passed as http header
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", {new Date()})))

    // proxy templates
    stubFor(get(urlPathEqualTo("/templated/api"))
            .willReturn(aResponse()
                    .proxiedFrom("{{request.headers[\"X-WM-Proxy-Url\"]}}")))

}
