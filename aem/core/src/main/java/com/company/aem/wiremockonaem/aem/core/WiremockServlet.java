package com.company.aem.wiremockonaem.aem.core;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter.ORIGINAL_REQUEST_KEY;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.core.FaultInjector;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.http.ChunkedDribbleDelay;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.jetty9.DefaultMultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.BodyChunker;
import com.github.tomakehurst.wiremock.servlet.FaultInjectorFactory;
import com.github.tomakehurst.wiremock.servlet.MultipartRequestConfigurer;
import com.github.tomakehurst.wiremock.servlet.NoFaultInjectorFactory;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import com.github.tomakehurst.wiremock.servlet.WireMockHttpServletRequestAdapter;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WiremockServlet extends HttpServlet {

  private static final Gson GSON = new GsonBuilder()
    .disableHtmlEscaping().serializeNulls().setPrettyPrinting()
    .create();

  private WireMockApp wireMockApp;
  private RequestHandler requestHandler;
  private FaultInjectorFactory faultHandlerFactory;
  private Notifier notifier;
  private MultipartRequestConfigurer multipartRequestConfigurer;

  @Override
  public void init(ServletConfig config) {
    wireMockApp = new WireMockApp(new AEMConfiguration(), new NotImplementedContainer());
    get(urlEqualTo("/ok")).willReturn(aResponse().withStatus(204)).build();


    stubFor(get(urlEqualTo("/some/thing"))
      .willReturn(aResponse()
        .withHeader("Content-Type", "text/plain")
        .withBody("Hello world!")));


    requestHandler = wireMockApp.buildStubRequestHandler();
    faultHandlerFactory = new NoFaultInjectorFactory();
    notifier = new ConsoleNotifier(true);
    multipartRequestConfigurer = new DefaultMultipartRequestConfigurer();
  }

  private void stubFor(MappingBuilder mappingBuilder){
    wireMockApp.addStubMapping(mappingBuilder.build());
  }

  @Override
  protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    throws IOException {
    Request request = new WireMockHttpServletRequestAdapter(httpRequest, multipartRequestConfigurer,
      "/wiremock");
    WiremockServlet.ServletHttpResponder responder = new WiremockServlet.ServletHttpResponder(
      httpRequest, httpResponse);
    requestHandler.handle(request, responder);

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
      if (Thread.currentThread().isInterrupted()) {
        return;
      }

      httpServletRequest.setAttribute(ORIGINAL_REQUEST_KEY, LoggedRequest.createFrom(request));

      respondTo(request, response);
    }


    private void respondTo(Request request, Response response) {
      try {
        if (response.wasConfigured()) {
          applyResponse(response, httpServletRequest, httpServletResponse);
        }
      } catch (Exception e) {
        throwUnchecked(e);
      }
    }
  }

  public void applyResponse(Response response, HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse) {
    Fault fault = response.getFault();
    if (fault != null) {
      FaultInjector faultInjector = faultHandlerFactory
        .buildFaultInjector(httpServletRequest, httpServletResponse);
      fault.apply(faultInjector);
      httpServletResponse.addHeader(Fault.class.getName(), fault.name());
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
      writeAndTranslateExceptionsWithChunkedDribbleDelay(httpServletResponse,
        response.getBodyStream(), response.getChunkedDribbleDelay());
    } else {
      writeAndTranslateExceptions(httpServletResponse, response.getBodyStream());
    }
  }

  private static void writeAndTranslateExceptions(HttpServletResponse httpServletResponse,
    InputStream content) {
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

  private void writeAndTranslateExceptionsWithChunkedDribbleDelay(
    HttpServletResponse httpServletResponse, InputStream bodyStream,
    ChunkedDribbleDelay chunkedDribbleDelay) {
    try (ServletOutputStream out = httpServletResponse.getOutputStream()) {
      byte[] body = ByteStreams.toByteArray(bodyStream);

      if (body.length < 1) {
        notifier.error("Cannot chunk dribble delay when no body set");
        out.flush();
        return;
      }

      byte[][] chunkedBody = BodyChunker.chunkBody(body, chunkedDribbleDelay.getNumberOfChunks());

      int chunkInterval = chunkedDribbleDelay.getTotalDuration() / chunkedBody.length;

      for (byte[] bodyChunk : chunkedBody) {
        Thread.sleep(chunkInterval);
        out.write(bodyChunk);
        out.flush();
      }

    } catch (IOException e) {
      throwUnchecked(e);
    } catch (InterruptedException ignored) {
      // Ignore the interrupt quietly since it's probably the client timing out, which is a completely valid outcome
    }
  }

}
