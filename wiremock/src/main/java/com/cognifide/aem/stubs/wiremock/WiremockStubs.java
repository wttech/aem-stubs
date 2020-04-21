package com.cognifide.aem.stubs.wiremock;

import com.cognifide.aem.stubs.core.AbstractStubs;
import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import com.cognifide.aem.stubs.wiremock.servlet.WiremockServlet;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import org.osgi.service.component.annotations.*;

import com.cognifide.aem.stubs.core.Stubs;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component(
  service = {Stubs.class, WiremockStubs.class, BindingExtensionProvider.class},
  immediate = true
)
@Designate(ocd = WiremockStubs.Config.class)
public class WiremockStubs extends AbstractStubs {

  private static final Logger LOG = LoggerFactory.getLogger(WiremockStubs.class);

  private WireMockApp app;

  private Config config;

  @Reference
  private HttpService httpService;

  @Reference
  private GroovyScriptManager groovyScriptManager;

  private String servletPath;

  private final AtomicBoolean initialized = new AtomicBoolean(false);

  public void init() {
    if (initialized.compareAndSet(false, true)) {
      reset();
    }
  }

  @Override
  public void clear() {
    app.resetAll();
  }

  @Override
  public void reset() {
    clear();
    groovyScriptManager.runAll();
  }

  @Activate
  @Modified
  protected void activate(Config config) {
    this.config = config;
    start();
  }

  @Deactivate
  protected void deactivate() {
    stop();
  }

  private void start() {
    this.app = new WiremockApp(new WiremockConfig(), new NotImplementedContainer());
    this.servletPath = getServletPath(config.path());

    try {
      httpService.registerServlet(servletPath, createServlet(), null, null);
    } catch (ServletException | NamespaceException e) {
      LOG.error("Cannot register AEM Stubs Wiremock Server at path {}", servletPath, e);
    }
  }

  private void stop() {
    if (servletPath != null) {
      httpService.unregister(servletPath);
      servletPath = null;
    }
    if (app != null) {
      app = null;
    }
  }

  private void restart() {
    stop();
    start();
  }

  private String getServletPath(String path) {
    return String.format("%s/*", path);
  }

  private WiremockServlet createServlet() {
    return new WiremockServlet(this, config.path(), app.buildStubRequestHandler());
  }

  @ObjectClassDefinition(name = "AEM Stubs - Wiremock Stubs")
  public @interface Config {

    @AttributeDefinition(name = "Servlet Prefix")
    String path() default "/wiremock";
  }
}
