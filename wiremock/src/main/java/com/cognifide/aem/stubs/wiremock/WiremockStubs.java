package com.cognifide.aem.stubs.wiremock;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.AbstractStubs;
import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.cognifide.aem.stubs.wiremock.servlet.WiremockServlet;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;

@Component(
  service = {Stubs.class, WiremockStubs.class, BindingExtensionProvider.class, EventHandler.class},
  property = EventConstants.EVENT_TOPIC + "=" + GroovyScriptManager.SCRIPT_CHANGE_EVENT_TOPIC,
  immediate = true
)
@Designate(ocd = WiremockStubs.Config.class)
public class WiremockStubs extends AbstractStubs<WiremockApp> {

  private static final Logger LOG = LoggerFactory.getLogger(WiremockStubs.class);

  private WiremockApp app;

  private Config config;

  @Reference
  private HttpService httpService;

  @Reference
  private GroovyScriptManager groovyScriptManager;

  @Reference
  ResolverAccessor resolverAccessor;

  private String servletPath;

  @Override
  public WiremockApp getServer() {
    return app;
  }

  @Override
  public void clear() {
    restart();
  }

  @Override
  public void reset() {
    clear();
    groovyScriptManager.runAll(getClass());
  }

  @Activate
  @Modified
  protected void activate(Config config) {
    this.config = config;
    reset();
  }

  @Deactivate
  protected void deactivate() {
    stop();
  }

  private void start() {
    LOG.info("Starting AEM Stubs Wiremock Server");
    this.app = new WiremockApp(resolverAccessor, groovyScriptManager.getScriptRootPath());
    this.servletPath = getServletPath(config.path());

    try {
      httpService.registerServlet(servletPath, createServlet(), null, null);
    } catch (ServletException | NamespaceException e) {
      LOG.error("Cannot register AEM Stubs Wiremock Server at path {}", servletPath, e);
    }
  }

  private void stop() {
    LOG.info("Stopping AEM Stubs Wiremock Server");
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
    return new WiremockServlet(config.path(), app.buildStubRequestHandler());
  }

  @ObjectClassDefinition(name = "AEM Stubs - Wiremock Server")
  public @interface Config {

    @AttributeDefinition(name = "Servlet Prefix")
    String path() default "/stubs";
  }
}
