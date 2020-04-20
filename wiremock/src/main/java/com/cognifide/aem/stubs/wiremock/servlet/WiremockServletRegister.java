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
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Designate(ocd = WiremockServletConfiguration.class)
public class WiremockServletRegister {
  private static Logger LOG = LoggerFactory.getLogger(WiremockServletRegister.class);

  @Reference
  private WiremockStubs stubs;

  @Reference
  private HttpService httpService;

  @Reference
  private GroovyScriptManager scripts;

  @Activate
  public void start(WiremockServletConfiguration configuration) {
    String servletPath = getServletPath(configuration.path());
    try {
      httpService.registerServlet(servletPath,
        createServlet(configuration.path()),null, null);
      scripts.runAll();
    } catch (ServletException | NamespaceException e) {
      LOG.error("Cannot register AEM Stubs Wiremock integration servlet at path {}", servletPath, e);
    }
  }

  @Deactivate
  public void stop(WiremockServletConfiguration configuration){
    httpService.unregister(getServletPath(configuration.path()));
  }

  private WiremockServlet createServlet(String path){
    return new WiremockServlet(path, stubs.buildStubRequestHandler());
  }

  private String getServletPath(String path){
    return String.format("%s/*", path);
  }
}
