package com.cognifide.aem.stubs.wiremock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class HelloWorldTest {

  @Test
  public void shouldReturnJson() {
    given()
      .when()
      .get("http://localhost:4502/stubs/json")
      .then()
      .body("message", equalTo("Hello"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnJsonFromFile() {
    given()
      .when()
      .get("http://localhost:4502/stubs/body-file")
      .then()
      .body("message", equalTo("Hello World!"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnJsonFromTemplateFile() {
    given()
      .when()
      .get("http://localhost:4502/stubs/templated-file")
      .then()
      .body("message", equalTo("Hello Templates!"))
      .statusCode(200);
  }


  @Test
  public void shouldDeleteFine() {
    given()
      .when()
      .delete("http://localhost:4502/stubs/fine")
      .then()
      .statusCode(200);
  }

  @Test
  public void shouldGetFineWithBody() {
    given()
      .when()
      .get("http://localhost:4502/stubs/fine-with-body")
      .then()
      .body(equalTo("body content"))
      .statusCode(200);
  }

  @Test
  public void shouldGetFineWithBodyTemplatedByPebble() {
    given()
      .when()
      .get("http://localhost:4502/stubs/templated-pebble")
      .then()
      .body(equalTo("templated-pebble"))
      .statusCode(200);
  }

  @Test
  public void shouldGetFineWithBodyTemplatedByHandlebar() {
    given()
      .when()
      .get("http://localhost:4502/stubs/templated-handlebars")
      .then()
      .body(equalTo("templated-handlebars"))
      .statusCode(200);
  }
  @Test
  public void shouldReturnJsonFromFilePointedInHeaderPebble() {
    given().header("X-WM-Body-File", "samples/message.json")
      .when()
      .get("http://localhost:4502/stubs/header-body-file-pebble")
      .then()
      .body("message", equalTo("Hello World!"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnJsonFromFilePointedInHeaderHandlebars() {
    given().header("X-WM-Body-File", "samples/message.json")
      .when()
      .get("http://localhost:4502/stubs/header-body-file-handlebars")
      .then()
      .body("message", equalTo("Hello World!"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnImageFile() {
    given()
      .when()
      .get("http://localhost:4502/stubs/image")
      .then()
      .header("Content-Type", "image/png")
      .statusCode(200);
  }

  @Test
  public void shouldPost() {
    given()
      .when()
      .post("http://localhost:4502/stubs/post")
      .then()
      .body("message", equalTo("Hello Post"))
      .statusCode(200);
  }

  @Test
  public void shouldPostWithHeader() {
    given()
      .when()
      .with().header("User-Agent", "Mozilla")
      .post("http://localhost:4502/stubs/post")
      .then()
      .body("message", equalTo("Hello Post"))
      .statusCode(200);
  }

  @Test
  public void shouldPostRedirect() {
    given()
      .when()
      .post("http://localhost:4502/stubs/redirect")
      .then()
      .statusCode(302);
  }

  @Test
  public void shouldPostUnauthorized() {
    given()
      .when()
      .post("http://localhost:4502/stubs/sorry-no")
      .then()
      .statusCode(401);
  }

  @Test
  public void shouldPutStatusOnly() {
    given()
      .when()
      .put("http://localhost:4502/stubs/status-only")
      .then()
      .statusCode(418);
  }

  @Test
  public void shouldHaveHeader() {
    given()
      .when()
      .get("http://localhost:4502/stubs/with-header")
      .then()
      .header("Some-Header", "value")
      .statusCode(200);
  }

  @Test
  public void shouldAddDefaultCorsForPreflightRequest() {
    given()
      .when()
      .header("Access-Control-Request-Headers", "*")
      .header("Access-Control-Request-Method", "*")
      .options("http://localhost:4502/stubs/cors-preflight")
      .then()
      .header("Access-Control-Allow-Origin" , "*")
      .header("Access-Control-Allow-Headers", "*")
      .header("Access-Control-Allow-Methods" , "*")
      .statusCode(200);
  }

  @Test
  public void shouldNotChangeDefinedCors() {
    given()
      .when()
      .header("Access-Control-Request-Headers", "*")
      .header("Access-Control-Request-Method", "*")
      .options("http://localhost:4502/stubs/cors-preflight-overwrite")
      .then()
      .header("Access-Control-Allow-Origin" , "origin")
      .header("Access-Control-Allow-Headers", "custom-header")
      .header("Access-Control-Allow-Methods" , "method")
      .statusCode(200);
  }

  @Test
  public void shouldReturnMappingOneBody() {
    given()
      .when()
      .get("http://localhost:4502/stubs/mappings/1")
      .then()
      .body(equalTo("Hello mappings one"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnMappingTwoBody() {
    given()
      .when()
      .get("http://localhost:4502/stubs/mappings/2")
      .then()
      .body(equalTo("Hello mappings two"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnNotSupportedForDelays() {
    given()
      .when()
      .get("http://localhost:4502/stubs/delayed")
      .then()
      .statusCode(400)
      .statusLine(containsString("Delay not supported by AEM Stubs"));
  }

  @Test
  public void shouldReturnNotSupportedForChunkedDelays() {
    given()
      .when()
      .get("http://localhost:4502/stubs/chunked/delayed")
      .then()
      .statusCode(400)
      .statusLine(containsString("Chunked dribble delay not supported by AEM Stubs"));
  }


  @Test
  public void shouldReturnNotSupportedForFault() {
    given()
      .when()
      .get("http://localhost:4502/stubs/fault")
      .then()
      .statusCode(400)
      .statusLine(containsString("MALFORMED_RESPONSE_CHUNK not supported by AEM Stubs"));
  }

  @Test
  public void shouldReturn404ForNoStubDefined() {
    given()
      .when()
      .get("http://localhost:4502/stubs/not-defined-stub")
      .then()
      .statusCode(404)
      .statusLine(containsString("No stub defined for this request"));
  }

  @Test
  public void shouldProceedByExampleScenario(){
    resetScenario();
    String started = callScenario();
    String step1 = callScenario();

    resetScenario();
    String afterReset = callScenario();

    assertEquals(afterReset, started);
    assertNotEquals(step1, started);
  }

  private void resetScenario() {
    given()
      .when()
      .post("http://localhost:4502/stubs/__admin/scenarios/reset");
  }

  private String callScenario() {
    return given()
      .when()
      .get("http://localhost:4502/stubs/scenario")
      .body()
      .asString();
  }
}
