package com.cognifide.aem.stubs.wiremock;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter.ORIGINAL_REQUEST_KEY;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.jetty9.DefaultMultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.FaultInjectorFactory;
import com.github.tomakehurst.wiremock.servlet.MultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.NoFaultInjectorFactory;
import com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.io.ByteStreams;

public class WiremockServlet extends HttpServlet {

  private final RequestHandler requestHandler;
  private final FaultInjectorFactory faultHandlerFactory;
  private final Notifier notifier;
  private final MultipartRequestConfigurer multipartRequestConfigurer;
  private final String path;

  WiremockServlet(String path, RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
    this.faultHandlerFactory = new NoFaultInjectorFactory();
    this.notifier = new ConsoleNotifier(true);
    this.multipartRequestConfigurer = new DefaultMultipartRequestConfigurer();
    this.path = path;
  }

  @Override
  protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    Request request = new WireMockHttpServletRequestAdapter(httpRequest, multipartRequestConfigurer,
      path);
    ;
    ServletHttpResponder responder = new ServletHttpResponder(
      httpRequest, httpResponse);
    requestHandler.handle(request, responder);
  }

  public void applyResponse(Response response, HttpServletResponse httpServletResponse)
    throws IOException {
    Fault fault = response.getFault();
    if (fault != null) {
      httpServletResponse.sendError(400, "Faults not supported!");
      return;
    }

    if (response.getStatusMessage() == null) {
      httpServletResponse.setStatus(response.getStatus());
    } else {
      httpServletResponse.setStatus(response.getStatus(), response.getStatusMessage());
    }

    for (HttpHeader header : response.getHeaders().all()) {
      for (String value : header.values()) {
        httpServletResponse.addHeader(header.key(), value);
      }
    }

    if (response.shouldAddChunkedDribbleDelay()) {
      httpServletResponse.sendError(400, "Chunked dribble delay not supported by AEM Stubs");
    } else {
      write(httpServletResponse, response.getBodyStream());
    }
  }

  private static void write(HttpServletResponse httpServletResponse,InputStream content) {
    try (ServletOutputStream out = httpServletResponse.getOutputStream()) {
      ByteStreams.copy(content, out);
      out.flush();
    } catch (IOException e) {
      throwUnchecked(e);
    } finally {
      try {
        content.close();
      } catch (IOException e) {
        // well, we tried
      }
    }
  }

  private class ServletHttpResponder implements HttpResponder {

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    public ServletHttpResponder(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
      this.httpServletRequest = httpServletRequest;
      this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void respond(final Request request, final Response response) {
      httpServletRequest.setAttribute(ORIGINAL_REQUEST_KEY, LoggedRequest.createFrom(request));
      try {
        if (response.wasConfigured()) {
          applyResponse(response, httpServletResponse);
        } else {
          httpServletResponse.sendError(404, "No stub defined for this request");
        }
      } catch (Exception e) {
        throwUnchecked(e);
      }

    }
  }
}
