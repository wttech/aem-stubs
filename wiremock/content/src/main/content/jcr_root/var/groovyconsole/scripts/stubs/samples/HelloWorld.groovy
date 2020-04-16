import static com.github.tomakehurst.wiremock.client.WireMock.*;

stubs.define "hello-world", { wm ->
    wm.stubFor(delete("/fine")
            .willReturn(ok()))

    wm.stubFor(get("/fine-with-body")
            .willReturn(ok("body content")))

    wm.stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))

    wm.stubFor(post("/post")
            .willReturn(okJson("{ \"message\": \"Hello Post\" }")))

    wm.stubFor(post("/redirect")
            .willReturn(temporaryRedirect("/wiremock/fine-with-body")))

    wm.stubFor(post("/sorry-no")
            .willReturn(unauthorized()))

    wm.stubFor(put("/status-only")
            .willReturn(status(418)))

    wm.stubFor(get("/with")
            .willReturn(ok("body content")))

    wm.stubFor(get(urlEqualTo("/with-header"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withStatusMessage("Everything was just fine!")
                    .withHeader("Some-Header", "value")))

    wm.stubFor(get(urlPathEqualTo("/templated"))
            .willReturn(aResponse()
                    .withBody("{{request.path.[0]}}")
                    .withTransformers("response-template")))
}
