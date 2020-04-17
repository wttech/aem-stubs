package com.cognifide.aem.stubs.wiremock;

import javax.servlet.ServletException;

import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component(immediate = true)
public class WiremockServletRegister {

  @Reference
  private WiremockStubs stubs;

  @Reference
  private HttpService httpService;

  @Reference
  private GroovyScriptManager scripts;

  @Activate
  public void start() {
    try {
      httpService.registerServlet(getServletPath(), createServlet(),null, null);
      scripts.runAll();
    } catch (ServletException | NamespaceException e) {
      e.printStackTrace();
    }
  }

  @Deactivate
  public void stop(){
    httpService.unregister(getServletPath());
  }

  private WiremockServlet createServlet(){
    return new WiremockServlet("/wiremock", stubs.buildStubRequestHandler());
  }

  private String getServletPath(){
    return "/wiremock/*"; // TODO make it configurable
  }
}
