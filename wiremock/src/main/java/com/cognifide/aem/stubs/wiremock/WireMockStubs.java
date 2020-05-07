package com.cognifide.aem.stubs.wiremock;

import javax.servlet.ServletException;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.script.StubScriptManager;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.cognifide.aem.stubs.wiremock.servlet.WireMockServlet;
import com.github.tomakehurst.wiremock.http.Request;

@Component(
  service = {Stubs.class, WireMockStubs.class},
  immediate = true
)
@Designate(ocd = WireMockStubs.Config.class)
public class WireMockStubs implements Stubs<WireMockApp> {

  public static final String ID = "wiremock";

  private static final Logger LOG = LoggerFactory.getLogger(WireMockStubs.class);

  private WireMockApp app;

  private Config config;

  @Reference
  private HttpService httpService;

  @Reference
  private StubScriptManager stubScriptManager;

  @Reference
  private ResolverAccessor resolverAccessor;

  private String servletPath;

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public WireMockApp getServer() {
    return app;
  }

  @Override
  public void clear() {
    restart();
  }

  @Override
  public void reset() {
    clear();
    stubScriptManager.runAll(this);
  }

  @Override
  public void prepare(StubScript script) {
    script.getCompilerConfig().addCompilationCustomizers(new ImportCustomizer()
      .addStaticStars(WireMockUtils.class.getName())
      .addStarImports(Request.class.getPackage().getName())
    );
  }

  @Activate
  protected void activate(Config config) {
    this.config = config;
  }

  @Modified
  protected void modify(Config config) {
    this.config = config;
    reset();
  }

  @Deactivate
  protected void deactivate() {
    stop();
  }

  private void start() {
    LOG.info("Starting AEM Stubs Wiremock Server");
    this.app = new WireMockApp(resolverAccessor, stubScriptManager.getRootPath() + "/" + getId(),
      config.globalTransformer());
    this.servletPath = getServletPath(config.path());

    try {
      httpService.registerServlet(servletPath, createServlet(), null, null);
    } catch (ServletException | NamespaceException e) {
      LOG.error("Cannot register AEM Stubs Wiremock Server at path {}", servletPath, e);
    }
  }

  @SuppressWarnings("PMD.NullAssignment")
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

  private WireMockServlet createServlet() {
    return new WireMockServlet(config.path(), app.buildStubRequestHandler());
  }

  @ObjectClassDefinition(name = "AEM Stubs WireMock Server")
  public @interface Config {

    @AttributeDefinition(name = "Servlet Prefix")
    String path() default "/stubs";

    @AttributeDefinition(name = "Global Template Transformer", description = "Enables Pebble template engine / templating for response body content and file paths when loading body files. Effectively enables dynamic file loading instead of preloading and simplifies defining stubs.")
    boolean globalTransformer() default true;
  }
}
