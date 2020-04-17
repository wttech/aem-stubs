package com.cognifide.aem.stubs.wiremock.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.Response;

class FaultResponse {

  private final boolean notSupported;
  private final String msg;
  private final int statusCode;

  private FaultResponse(boolean notSupported, String msg, int statusCode) {
    this.notSupported = notSupported;
    this.msg = msg;
    this.statusCode = statusCode;
  }

  boolean isNotSupported() {
    return notSupported;
  }

  void sendError(HttpServletResponse httpServletResponse) throws IOException {
    httpServletResponse.sendError(statusCode, msg);
  }

  static FaultResponse of(Response response) {
    if (!response.wasConfigured()) {
      return FaultResponse.notSupported("No stub defined for this request", 404);
    }

    if (response.shouldAddChunkedDribbleDelay()) {
      return FaultResponse
        .notSupported("Chunked dribble delay not supported by AEM Stubs");
    }
    Fault fault = response.getFault();
    if (fault != null) {
      return FaultResponse
        .notSupported(
          String.format("%s not supported by AEM Stubs", fault.name()));
    }

    if (response.getInitialDelay() != 0) {
      return FaultResponse
        .notSupported("Delay not supported by AEM Stubs");
    }

    return FaultResponse.supported();
  }

  private static FaultResponse supported() {
    return new FaultResponse(false, null, -1);
  }

  private static FaultResponse notSupported(String msg) {
    return new FaultResponse(true, msg, 400);
  }

  private static FaultResponse notSupported(String msg, int statusCode) {
    return new FaultResponse(true, msg, statusCode);
  }
}
