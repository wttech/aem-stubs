package com.cognifide.aem.stubs.wiremock.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.jetty9.DefaultMultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.MultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter;

public class MocksHandlerFactory {

  private final RequestHandler mocksHandler;
  private final RequestHandler adminHandler;
  private final MultipartRequestConfigurer requestConfigurer;
  private final String path;

  public MocksHandlerFactory(RequestHandler mocksHandler,
    RequestHandler adminHandler, String path) {
    this.mocksHandler = mocksHandler;
    this.adminHandler = adminHandler;
    this.requestConfigurer = new DefaultMultipartRequestConfigurer();
    this.path = path;
  }

  public MocksRequestHandler create(HttpServletRequest httpRequest,
    HttpServletResponse httpResponse) {
    Request request = new WireMockHttpServletRequestAdapter(httpRequest, requestConfigurer, path);
    HttpResponder responder = new Responder(httpRequest, httpResponse);

    if (isAdmin(httpRequest)) {
      return new AdminHandler(adminHandler, responder, request, path);
    }

    return new StubsHandler(mocksHandler, responder, request);
  }

  private boolean isAdmin(HttpServletRequest httpRequest) {
    return httpRequest.getPathInfo().contains("__admin");
  }
}
