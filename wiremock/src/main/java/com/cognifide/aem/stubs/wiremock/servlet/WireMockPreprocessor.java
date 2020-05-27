package com.cognifide.aem.stubs.wiremock.servlet;

import com.cognifide.aem.stubs.wiremock.WireMockStubs;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.Preprocessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component(
  immediate = true,
  service = Preprocessor.class,
  property = {
    Constants.SERVICE_RANKING + ":Integer=" + Integer.MAX_VALUE,
  }
)
public class WireMockPreprocessor implements Preprocessor {

  @Reference
  private WireMockStubs stubs;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      if (((HttpServletRequest) request).getRequestURI().startsWith(stubs.getPath() + "/")) {
        stubs.getServlet().service(request, response);
        return;
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // intentionally empty
  }

  @Override
  public void destroy() {
    // intentionally empty
  }
}
