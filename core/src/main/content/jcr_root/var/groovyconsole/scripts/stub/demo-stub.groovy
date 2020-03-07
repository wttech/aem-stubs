import static com.github.tomakehurst.wiremock.client.WireMock.*;

wiremock.stubFor(delete("/fine")
        .willReturn(ok()));

wiremock.stubFor(get("/fine-with-body")
        .willReturn(ok("body content")));

wiremock.stubFor(get("/json")
        .willReturn(okJson("{ \"message\": \"Hello\" }")));

wiremock.stubFor(post("/redirect")
        .willReturn(temporaryRedirect("/new/place")));

wiremock.stubFor(post("/sorry-no")
        .willReturn(unauthorized()));

wiremock.stubFor(put("/status-only")
        .willReturn(status(418)));

wiremock.stubFor(get(urlPathEqualTo("/templated"))
        .willReturn(aResponse()
                .withBody("{{request.path.[0]}}")
                .withTransformers("response-template")));
