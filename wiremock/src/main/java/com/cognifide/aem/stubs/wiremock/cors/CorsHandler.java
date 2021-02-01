package com.cognifide.aem.stubs.wiremock.cors;

import static java.util.Objects.isNull;

import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.google.common.net.HttpHeaders;

public final class CorsHandler {

  private final CorsConfiguration configuration;
  private final boolean corsPreflightRequest;
  private final HttpServletResponse httpServletResponse;
  private final Response response;

  public CorsHandler(CorsConfiguration configuration,
    Request request, Response response, HttpServletResponse httpServletResponse) {
    this.configuration = configuration;
    this.corsPreflightRequest = isPreflightRequest(request);
    this.httpServletResponse = httpServletResponse;
    this.response = response;
  }

  private boolean isPreflightRequest(Request request) {
    return configuration.isCorsEnabled() && "OPTIONS".equals(request.getMethod().getName()) &&
      !isNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS)) &&
      !isNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
  }

  public void handleHeaders() {
    if (!configuration.isCorsEnabled()) {
      return;
    }

    if (isPreflightRequest()) {
      httpServletResponse.setStatus(200);
      addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, configuration.getAllowHeaders());
      addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, configuration.getAllowMethods());
    }

    addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, configuration.getAllowOrigin());
  }

  private void addHeader(String key, String value) {
    if (response.getHeaders().keys().contains(key)) {
      return;
    }

    httpServletResponse.addHeader(key, value);
  }
  public boolean isPreflightRequest() {
    return corsPreflightRequest;
  }
}
