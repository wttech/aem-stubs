package com.cognifide.aem.stubs.wiremock.servlet.handler;

import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;

class StubsHandler implements MocksRequestHandler {
  private final RequestHandler requestHandler;
  private final HttpResponder responder;
  private final Request request;

  public StubsHandler(RequestHandler adminHandler,
    HttpResponder responder, Request request) {
    this.requestHandler = adminHandler;
    this.responder = responder;
    this.request = request;
  }

  @Override
  public void handle() {
    requestHandler.handle(request, responder);
  }
}
