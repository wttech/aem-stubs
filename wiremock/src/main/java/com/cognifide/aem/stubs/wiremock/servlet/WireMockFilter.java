package com.cognifide.aem.stubs.wiremock.servlet;

import com.cognifide.aem.stubs.wiremock.WireMockStubs;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.*;
import java.io.IOException;

/**
 * Bypass for AEM less or equal to 6.3.x for avoiding AEM built-in filters
 * intercepting calls to WireMock Servlet unnecessarily.
 */
@Component(
  immediate = true,
  service = Filter.class,
  property = {
    Constants.SERVICE_RANKING + ":Integer=" + Integer.MAX_VALUE,
    "osgi.http.whiteboard.filter.pattern=/",
    "osgi.http.whiteboard.context.select=(osgi.http.whiteboard.context.name=*)"
  }
)
public class WireMockFilter implements Filter {

  @Reference
  private WireMockStubs stubs;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (stubs.isBypassable(request)) {
      stubs.getServlet().service(request, response);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // intentionally empty
  }

  @Override
  public void destroy() {
    // intentionally empty
  }
}
