package com.cognifide.aem.stubs.wiremock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class HelloWorldMocksIntegrationTest {

  @Test
  public void shouldReturnJson() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/json")
      .then()
      .body("message", equalTo("Hello"))
      .statusCode(200);
  }

  @Test
  public void shouldDeleteFine() {
    given()
      .when()
      .delete("http://localhost:4502/wiremock/fine")
      .then()
      .statusCode(200);
  }

  @Test
  public void shouldGetFineWithBody() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/fine-with-body")
      .then()
      .body(equalTo("body content"))
      .statusCode(200);
  }

  @Test
  public void shouldPost() {
    given()
      .when()
      .post("http://localhost:4502/wiremock/post")
      .then()
      .body("message", equalTo("Hello Post"))
      .statusCode(200);
  }

  @Test
  public void shouldPostRedirect() {
    given()
      .when()
      .post("http://localhost:4502/wiremock/redirect")
      .then()
      .statusCode(302);
  }

  @Test
  public void shouldPostUnauthorized() {
    given()
      .when()
      .post("http://localhost:4502/wiremock/sorry-no")
      .then()
      .statusCode(401);
  }

  @Test
  public void shouldPutStatusOnly() {
    given()
      .when()
      .put("http://localhost:4502/wiremock/status-only")
      .then()
      .statusCode(418);
  }

  @Test
  public void shouldHaveHeader() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/with-header")
      .then()
      .header("Some-Header", "value")
      .statusCode(200);
  }

  @Test
  public void shouldReturnNotSupportedForDelays() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/delayed")
      .then()
      .statusCode(400)
      .statusLine(containsString("Faults not supported by AEM Stubs. Tried to simulate DELAY"));
  }

  @Test
  public void shouldReturnNotSupportedForChunkedDelays() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/chunked/delayed")
      .then()
      .statusCode(400)
      .statusLine(containsString("Faults not supported by AEM Stubs. Tried to simulate Chunked dribble delay"));
  }


  @Test
  public void shouldReturnNotSupportedForFault() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/fault")
      .then()
      .statusCode(400)
      .statusLine(containsString("Faults not supported by AEM Stubs. Tried to simulate MALFORMED_RESPONSE_CHUNK"));
  }

  @Test
  public void shouldReturn404ForNoStubDefined() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/not-defined-stub")
      .then()
      .statusCode(404)
      .statusLine(containsString("No stub defined for this request"));
  }
}
