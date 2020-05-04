package com.cognifide.aem.stubs.wiremock.servlet;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter.ORIGINAL_REQUEST_KEY;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.jetty9.DefaultMultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.MultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.io.ByteStreams;

public class WiremockServlet extends HttpServlet {

  private static final Logger LOG = LoggerFactory.getLogger(WiremockServlet.class);

  private final RequestHandler requestHandler;

  private final MultipartRequestConfigurer requestConfigurer;

  private final String path;

  public WiremockServlet(String path, RequestHandler requestHandler) {
    super();
    this.requestHandler = requestHandler;
    this.requestConfigurer = new DefaultMultipartRequestConfigurer();
    this.path = path;
  }

  @Override
  protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    Request request = new WireMockHttpServletRequestAdapter(httpRequest, requestConfigurer, path);
    requestHandler.handle(request, responder(request, httpRequest, httpResponse));
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private HttpResponder responder(Request request, HttpServletRequest httpRequest,
    HttpServletResponse httpResponse) {
    return (req, resp) -> {
      httpRequest.setAttribute(ORIGINAL_REQUEST_KEY, LoggedRequest.createFrom(request));
      try {
        applyResponse(resp, httpResponse);
      } catch (Exception e) {
        throwUnchecked(e);
      }
    };
  }

  @SuppressWarnings("deprecation")
  private void applyResponse(Response response, HttpServletResponse servletResponse)
    throws IOException {
    FaultResponse faultResponse = FaultResponse.fromResponse(response);

    if (faultResponse.isNotSupported()) {
      faultResponse.sendError(servletResponse);
      return;
    }

    if (response.getStatusMessage() == null) {
      servletResponse.setStatus(response.getStatus());
    } else {
      servletResponse.setStatus(response.getStatus(), response.getStatusMessage());
    }

    for (HttpHeader header : response.getHeaders().all()) {
      for (String value : header.values()) {
        servletResponse.addHeader(header.key(), value);
      }
    }

    write(servletResponse, response.getBodyStream());
  }

  private static void write(HttpServletResponse response, InputStream content) {
    try (ServletOutputStream out = response.getOutputStream()) {
      ByteStreams.copy(content, out);
      out.flush();
    } catch (IOException e) {
      throwUnchecked(e);
    } finally {
      try {
        content.close();
      } catch (IOException e) {
        LOG.error("Cannot write Wiremock AEM Stubs response", e);
        // well, we tried
      }
    }
  }
}
