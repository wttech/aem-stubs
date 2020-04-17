package com.cognifide.aem.stubs.wiremock.servlet;

import javax.servlet.ServletException;

import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import com.cognifide.aem.stubs.wiremock.WiremockStubs;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class WiremockServletRegister {
  private static Logger LOG = LoggerFactory.getLogger(WiremockServletRegister.class);

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
      LOG.error("Cannot register servlet under path {}", getServletPath(), e);
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
