package com.company.wiremockonaem.aem.core;

import static com.company.wiremockonaem.aem.core.Wiremock.URL_PREFIX;
import static java.lang.String.format;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.company.wiremockonaem.aem.core.groovy.GroovyScriptExecutor;

@Component(
  immediate = true
)
public class WiremockServletRegister {
  @Reference
  private Wiremock wiremock;

  @Reference
  private HttpService httpService;

  @Reference
  private GroovyScriptExecutor groovyScriptExecutor;

  @Reference
  private WiremockConfiguration wiremockConfiguration;

  @Activate
  public void start() {
    try {
      httpService.registerServlet(format("%s/*", URL_PREFIX), new WiremockServlet(wiremock), null, null);
      wiremockConfiguration.getAllScript().forEach(path -> groovyScriptExecutor.runScript(path));
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (NamespaceException e) {
      e.printStackTrace();
    }
  }
}
