package com.cognifide.aem.stubs.wiremock;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;


public class AdminConsoleTest {

  @Test
  public void shouldReturnAllScenarios() {
    given()
      .when()
      .get("http://localhost:4502/stubs/__admin/scenarios")
      .then()
      .body("scenarios", hasSize(1))
      .statusCode(200);
  }

  @Test
  public void shouldResetScenarios() {
    given()
      .when()
      .post("http://localhost:4502/stubs/__admin/scenarios/reset")
      .then()
      .statusCode(200);
  }

  @Test
  public void shouldReturnMappingForScenario() {
    given()
      .when()
      .get("http://localhost:4502/stubs/__admin/mappings/e8f16deb-133b-4e93-8840-9f60728314a5")
      .then()
      .body("request.url", equalTo("/scenario"))
      .statusCode(200);
  }

  @Test
  public void shouldReturnAllMappings() {
    given()
      .when()
      .get("http://localhost:4502/stubs/__admin/mappings")
      .then()
      .body("meta.total", equalTo(26))
      .statusCode(200);
  }
}
