import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

wiremock.stubFor(
  get(urlEqualTo("/world"))
    .willReturn(
      okJson("{ \"message\": \"Hello World!\" }")));
