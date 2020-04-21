import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.willReturn;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.temporaryRedirect;
import static com.github.tomakehurst.wiremock.client.WireMock.withStatusMessage;
import static com.github.tomakehurst.wiremock.client.WireMock.withStatus;
import static com.github.tomakehurst.wiremock.client.WireMock.withHeader;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.withChunkedDribbleDelay;
import static com.github.tomakehurst.wiremock.client.WireMock.withBody;
import static com.github.tomakehurst.wiremock.client.WireMock.withFault;
import static com.github.tomakehurst.wiremock.client.WireMock.withFixedDelay;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.http.Fault

wiremock.with {
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


    // Not supported BY AEM Stubs
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
