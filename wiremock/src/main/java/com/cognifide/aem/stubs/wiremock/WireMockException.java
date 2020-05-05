package com.cognifide.aem.stubs.wiremock;

import com.cognifide.aem.stubs.core.StubsException;

public class WiremockException extends StubsException {

  public WiremockException(String message) {
    super(message);
  }

  public WiremockException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
