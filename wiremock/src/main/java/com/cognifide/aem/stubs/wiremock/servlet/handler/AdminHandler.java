package com.cognifide.aem.stubs.wiremock.servlet.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.Response;

class AdminHandler implements MocksRequestHandler {

  protected static final String ADMIN_ERROR_PREFIX = "Admin API error: ";

  private static final Logger LOG = LoggerFactory.getLogger(AdminHandler.class);
  private static final String ERROR_TEMPLATE = ADMIN_ERROR_PREFIX + "%s\n\n\nOriginal exception message:\n%s";
  private final RequestHandler requestHandler;
  private final HttpResponder responder;
  private final Request request;

  public AdminHandler(RequestHandler requestHandler, HttpResponder responder,
    Request request) {
    this.requestHandler = requestHandler;
    this.responder = responder;
    this.request = request;
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void handle() {
    try {
      requestHandler.handle(request, responder);
    } catch (Exception e) {
      LOG.error("AEM Stubs Admin console error", e);
      responder.respond(request, Response.response()
        .statusMessage(prepareErrorMessage(e))
        .build());
    }
  }

  private String prepareErrorMessage(Exception e){
    return String.format(ERROR_TEMPLATE, prepareErrorMessage(), e.getMessage());
  }

  private String prepareErrorMessage (){
    if (isGetMappingsUrl()) {
      return "One of your mapping definition use dynamic template value and cannot be deserialized";
    }

    return "Some error occurred. Please check logs for details";
  }

  private boolean isGetMappingsUrl() {
    return request.getUrl().contains("__admin/mappings")
      && RequestMethod.GET.equals(request.getMethod());
  }
}
