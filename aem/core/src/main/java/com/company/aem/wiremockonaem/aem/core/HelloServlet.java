package com.company.aem.wiremockonaem.aem.core;

import com.day.cq.wcm.api.NameConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @link http://[host]:[port]/content/we-retail/us/en.hello.json
 */
/*@Component(
  service = Servlet.class,
  property = {
    "sling.servlet.paths=/bin/wiremock",
    "sling.servlet.resourceTypes=" + NameConstants.NT_PAGE
  }
)*/
/*@Component
@Properties({
  @Property( name = HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_REGEX,
    value = "/"
  ),
  @Property( name = HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
    value = ("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=org.osgi.service.http)")
  )
})
@Service*/
/*@Component(
  service = Servlet.class,
  property = {
    // http://javadox.com/org.osgi/osgi.cmpn/6.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_PATTERN
    HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/bin/sample/felix/servlet",
    HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=org.osgi.service.http)"
  }
)*/
public class HelloServlet extends HttpServlet {

  private static final Gson GSON = new GsonBuilder()
    .disableHtmlEscaping().serializeNulls().setPrettyPrinting()
    .create();


  @Override
  public void init(ServletConfig config) {
    System.out.println("Name" + config.getServletName());
    System.out.println(">>> RequestHandlerClass " + config.getInitParameter("RequestHandlerClass"));
    System.out.println(">>> mappedUnder " + config.getInitParameter("mappedUnder"));
  }
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
    Map<String, String> result = ImmutableMap.of("message", "Hello World from pure felix activated in component!");

    response.setContentType(MediaType.JSON_UTF_8.toString());
    response.getWriter().write(GSON.toJson(result));
  }
}
