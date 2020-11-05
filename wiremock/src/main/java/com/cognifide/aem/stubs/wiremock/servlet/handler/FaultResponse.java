package com.cognifide.aem.stubs.wiremock.servlet.handler;

import static com.cognifide.aem.stubs.wiremock.servlet.handler.AdminHandler.ADMIN_ERROR_PREFIX;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.Response;

final class FaultResponse {

  private final boolean notSupported;
  private final String msg;
  private final int statusCode;

  private FaultResponse(boolean notSupported, String msg, int statusCode) {
    this.notSupported = notSupported;
    this.msg = msg;
    this.statusCode = statusCode;
  }

  public boolean hasError() {
    return notSupported;
  }

  public void sendError(HttpServletResponse servletResponse) throws IOException {
    servletResponse.sendError(statusCode, msg);
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

    if (isAdminAPIIssue(response)) {
      return FaultResponse.notSupportedResponse(response.getStatusMessage());
    }

    return FaultResponse.supported();
  }

  private static Boolean isAdminAPIIssue(Response response) {
    return Optional.ofNullable(response.getStatusMessage())
      .map(msg -> msg.startsWith(ADMIN_ERROR_PREFIX))
      .orElse(false);
  }

  private static FaultResponse supported() {
    return new FaultResponse(false, null, -1);
  }

  private static FaultResponse notSupportedResponse(String msg) {
    return new FaultResponse(true, msg, 400);
  }

  private static FaultResponse notSupportedResponse(String msg, int statusCode) {
    return new FaultResponse(true, msg, statusCode);
  }
}
