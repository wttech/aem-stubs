import com.github.tomakehurst.wiremock.http.Fault
import static com.cognifide.aem.stubs.wiremock.Wiremock.*

stubs.define "hello-world", {
    it.with {
        stubFor(delete("/fine")
                .willReturn(ok()))

        stubFor(get("/fine-with-body")
                .willReturn(ok("body content")))

        stubFor(get("/json")
                .willReturn(okJson("{ \"message\": \"Hello\" }")))

        stubFor(post("/post")
                .willReturn(okJson("{ \"message\": \"Hello Post\" }")))

        stubFor(post("/redirect")
                .willReturn(temporaryRedirect("/wiremock/fine-with-body")))

        stubFor(post("/sorry-no")
                .willReturn(unauthorized()))

        stubFor(put("/status-only")
                .willReturn(status(418)))


        stubFor(get(urlEqualTo("/with-header"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withStatusMessage("Everything was just fine!")
                        .withHeader("Some-Header", "value")))


        //Not supported BY AEM Stubs
        stubFor(get(urlEqualTo("/delayed")).willReturn(
                aResponse()
                        .withStatus(200)
                        .withFixedDelay(2000)));

        stubFor(get("/chunked/delayed").willReturn(
                aResponse()
                        .withStatus(200)
                        .withBody("Hello world!")
                        .withChunkedDribbleDelay(5, 1000)));

        stubFor(get(urlEqualTo("/fault"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    }
}
