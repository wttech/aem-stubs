package com.cognifide.aem.moco.wiremock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class HelloWorldMocksIntegrationTest {

  @Test
  public void shouldReturnText() {
    given()
      .when()
      .get("http://localhost:5555/hello-world")
      .then()
      .body(equalTo("Hello! I am sample stub."))
      .statusCode(200);
  }

  @Test
  public void shouldReturnFromSecured() {
    given().param("password", "secret")
      .when()
      .get("http://localhost:5555/secured")
      .then()
      .body(equalTo("Secured endpoint revealed!"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnStubScript() {
    given()
      .when()
      .get("http://localhost:5555/read-itself")
      .then()
      .body(containsString(".get(by(uri(\"/read-itself\")))"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnCurrentDate() {
    given()
      .when()
      .get("http://localhost:5555/current-date")
      .then()
      .body(containsString("Today date is"))
      .statusCode(200);
  }
}
