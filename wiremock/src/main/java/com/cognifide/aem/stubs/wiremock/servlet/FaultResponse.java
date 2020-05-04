package com.cognifide.aem.stubs.wiremock.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.Response;

final class FaultResponse {

  private final boolean supported;
  private final String msg;
  private final int statusCode;

  private FaultResponse(boolean supported, String msg, int statusCode) {
    this.supported = supported;
    this.msg = msg;
    this.statusCode = statusCode;
  }

  public boolean isNotSupported() {
    return !supported;
  }

  public void sendError(HttpServletResponse response) throws IOException {
    response.sendError(statusCode, msg);
  }

  public static FaultResponse fromResponse(Response response) {
    if (!response.wasConfigured()) {
      return FaultResponse.notSupportedResponse("No stub defined for this request", 404);
    }

    if (response.shouldAddChunkedDribbleDelay()) {
      return FaultResponse
        .notSupportedResponse("Chunked dribble delay not supported by AEM Stubs");
    }
    Fault fault = response.getFault();
    if (fault != null) {
      return FaultResponse
        .notSupportedResponse(
          String.format("%s not supported by AEM Stubs", fault.name()));
    }

    if (response.getInitialDelay() != 0) {
      return FaultResponse
        .notSupportedResponse("Delay not supported by AEM Stubs");
    }

    return FaultResponse.supportedResponse();
  }

  private static FaultResponse supportedResponse() {
    return new FaultResponse(false, null, -1);
  }

  private static FaultResponse notSupportedResponse(String msg) {
    return new FaultResponse(false, msg, 400);
  }

  private static FaultResponse notSupportedResponse(String msg, int statusCode) {
    return new FaultResponse(false, msg, statusCode);
  }
}
