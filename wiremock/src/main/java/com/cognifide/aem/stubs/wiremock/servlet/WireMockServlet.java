package com.cognifide.aem.stubs.wiremock.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cognifide.aem.stubs.wiremock.WireMockApp;
import com.cognifide.aem.stubs.wiremock.cors.CorsConfiguration;
import com.cognifide.aem.stubs.wiremock.servlet.handler.MocksHandlerFactory;

public class WireMockServlet extends HttpServlet {

  private final MocksHandlerFactory factory;

  public WireMockServlet(String path, WireMockApp app, CorsConfiguration corsConfiguration) {
    super();
    factory = new MocksHandlerFactory(app.buildStubRequestHandler(), app.buildAdminHandler(), path, corsConfiguration);
  }

  @Override
  protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    factory.create(httpRequest, httpResponse).handle();
  }
}
