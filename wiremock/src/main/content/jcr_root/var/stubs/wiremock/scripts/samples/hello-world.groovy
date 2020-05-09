stubs.server.with {
    stubFor(delete("/fine")
            .willReturn(ok()))

    stubFor(get("/fine-with-body")
            .willReturn(ok("body content")))

    stubFor(get(urlEqualTo("/body-file"))
            .willReturn(aResponse()
                    .withBodyFile("scripts/samples/message.json")
                    .withHeader("Content-Type", "application/json")))

    stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))

    stubFor(post("/post")
            .willReturn(okJson("{ \"message\": \"Hello Post\" }")))

    stubFor(post("/redirect")
            .willReturn(temporaryRedirect("/stubs/fine-with-body")))

    stubFor(post("/sorry-no")
            .willReturn(unauthorized()))

    stubFor(put("/status-only")
            .willReturn(status(418)))


    stubFor(get(urlEqualTo("/with-header"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withStatusMessage("Everything was just fine!")
                    .withHeader("Some-Header", "value")))

    // Proxies
    stubFor(get(urlMatching("/api/.*"))
            .willReturn(aResponse().proxiedFrom("http://api.nbp.pl")));
    //endpoint http://localhost:4502/stubs/api/exchangerates/rates/a/chf/


    // Not supported by AEM Stubs
    stubFor(get(urlEqualTo("/delayed")).willReturn(
            aResponse()
                    .withStatus(200)
                    .withFixedDelay(2000)))

    stubFor(get("/chunked/delayed").willReturn(
            aResponse()
                    .withStatus(200)
                    .withBody("Hello world!")
                    .withChunkedDribbleDelay(5, 1000)))

    stubFor(get(urlEqualTo("/fault"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)))
}
