import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.http.Fault;

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


    wm.stubFor(get(urlEqualTo("/with-header"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withStatusMessage("Everything was just fine!")
                    .withHeader("Some-Header", "value")))


    //Not supported BY AEM Stubs
    wm.stubFor(get(urlEqualTo("/delayed")).willReturn(
            aResponse()
                    .withStatus(200)
                    .withFixedDelay(2000)));

    wm.stubFor(get("/chunked/delayed").willReturn(
            aResponse()
                    .withStatus(200)
                    .withBody("Hello world!")
                    .withChunkedDribbleDelay(5, 1000)));

    wm.stubFor(get(urlEqualTo("/fault"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
}
