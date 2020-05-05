package com.cognifide.aem.stubs.wiremock;

import com.cognifide.aem.stubs.core.StubsException;

public class WireMockException extends StubsException {

  public WireMockException(String message) {
    super(message);
  }

  public WireMockException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
