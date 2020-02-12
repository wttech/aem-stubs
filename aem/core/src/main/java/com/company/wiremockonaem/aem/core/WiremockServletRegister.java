package com.company.wiremockonaem.aem.core;

import static com.company.wiremockonaem.aem.core.Wiremock.URL_PREFIX;
import static java.lang.String.format;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component(
  immediate = true
)
public class WiremockServletRegister {
  @Reference
  private Wiremock wiremock;

  @Reference
  private HttpService httpService;

  @Activate
  public void start() {
    try {
      httpService.registerServlet(format("%s/*", URL_PREFIX), new WiremockServlet(wiremock), null, null);
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (NamespaceException e) {
      e.printStackTrace();
    }
  }
}
