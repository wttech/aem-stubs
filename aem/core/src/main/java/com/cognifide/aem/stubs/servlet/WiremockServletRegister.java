package com.cognifide.aem.stubs.servlet;

import static com.cognifide.aem.stubs.Configuration.URL_PREFIX;
import static java.lang.String.format;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.cognifide.aem.stubs.groovy.GroovyScriptExecutor;
import com.cognifide.aem.stubs.wiremock.Wiremock;

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

  @Activate
  public void start() {
    try {
      httpService.registerServlet(getServletPath(), createServlet(),null, null);
      groovyScriptExecutor.runAllScripts();
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (NamespaceException e) {
      e.printStackTrace();
    }
  }

  @Deactivate
  public void stop(){
    httpService.unregister(getServletPath());
  }

  private String getServletPath(){
    return format("%s/*", URL_PREFIX);
  }

  private WiremockServlet createServlet(){
    return new WiremockServlet(URL_PREFIX, wiremock);
  }
}
