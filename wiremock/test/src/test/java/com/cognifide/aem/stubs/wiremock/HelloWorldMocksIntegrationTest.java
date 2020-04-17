package com.cognifide.aem.stubs.wiremock;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

public class HelloWorldMocksIntegrationTest {

  @Test
  public void shouldReturnJson() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/json")
      .then()
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
      .statusCode(200);
  }

  @Test
  public void shouldPost() {
    given()
      .when()
      .post("http://localhost:4502/wiremock/post")
      .then()
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
  public void shouldHasHeader() {
    given()
      .when()
      .get("http://localhost:4502/wiremock/with-header")
      .then()
      .header("Some-Header", "value")
      .statusCode(200);
  }
}
