package com.cognifide.aem.stubs.wiremock;

import static com.cognifide.aem.stubs.wiremock.cors.CorsConfiguration.enabled;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.service.component.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.metatype.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.StubManager;
import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.cognifide.aem.stubs.wiremock.cors.CorsConfiguration;
import com.cognifide.aem.stubs.wiremock.servlet.WireMockServlet;
import com.cognifide.aem.stubs.wiremock.transformers.DynamicParameterProvider;
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

  private MappingsLoader mappingsLoader;

  private Config config;

  @Reference
  private HttpService httpService;

  private WireMockServlet servlet;

  @Reference
  private StubManager manager;

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
  public void loadMapping(Resource file) {
    mappingsLoader.loadMapping(file);
  }

  @Override
  public void runScript(Resource resource) {
    final StubScript script = new StubScript(resource, manager, this);

    script.getCompilerConfig().addCompilationCustomizers(new ImportCustomizer()
      .addStaticStars(WireMockUtils.class.getName())
      .addStaticStars(DynamicParameterProvider.class.getName())
      .addStarImports(Request.class.getPackage().getName())
    );

    script.run();
  }

  @Activate
  protected void activate(Config config) {
    this.config = config;
  }

  @Modified
  protected void modify(Config config) {
    this.config = config;
    manager.reload(this);
  }

  @Deactivate
  protected void deactivate() {
    stop();
  }

  private void start() {
    LOG.info("Starting AEM Stubs WireMock Servlet");
    app = new WireMockApp(new WireMockOptionsFactory(this).create());
    mappingsLoader = new MappingsLoader(app);
    servletPath = getServletPath(config.path());

    try {
      servlet = createServlet();
      httpService.registerServlet(servletPath, servlet, null, null);
    } catch (ServletException | NamespaceException e) {
      LOG.error("Cannot register AEM Stubs WireMock Servlet at path {}", servletPath, e);
    }
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void stop() {
    LOG.info("Stopping AEM Stubs WireMock Servlet");
    if (servletPath != null) {
      httpService.unregister(servletPath);
      servletPath = null;
      servlet = null;
    }
    if (app != null) {
      app = null;
    }
  }

  @Override
  public void initServer() {
    stop();
    start();
  }

  @Override
  public void startServer() {
    // already started on init
  }

  private String getServletPath(String path) {
    return String.format("%s/*", path);
  }

  private WireMockServlet createServlet() {
    return new WireMockServlet(config.path(), app, getCorsConfiguration());
  }

  private CorsConfiguration getCorsConfiguration() {
    return config.corsEnabled() ? enabled(config.allowHeaders(), config.allowMethods(), config.allowOrigin())
      : CorsConfiguration.disabled();
  }

  public boolean isBypassable(ServletRequest request) {
    return config.filterBypass()
      && request instanceof HttpServletRequest
      && ((HttpServletRequest) request).getRequestURI().startsWith(config.path() + "/");
  }

  public WireMockServlet getServlet() {
    return servlet;
  }

  public ResolverAccessor getResolverAccessor() {
    return resolverAccessor;
  }

  public String getRootPath() {
    return manager.getRootPath();
  }

  public Config getConfig() {
    return config;
  }


  @ObjectClassDefinition(name = "AEM Stubs WireMock Server")
  public @interface Config {

    @AttributeDefinition(name = "Servlet Prefix")
    String path() default "/stubs";

    @AttributeDefinition(
      name = "Filter Bypass",
      description = "Disables requests filtering by installed OSGi HTTP Whiteboard pre-processors (like Sling Referrer Filter and SSL filter)"
    )
    boolean filterBypass() default true;

    @AttributeDefinition(
      name = "Global Template Transformer",
      description = "Enables template engine / templating. Handlebars and Pebbles engines are supported"
        + " for response body content and file paths when loading body files. Effectively enables dynamic file loading"
        + " instead of preloading and simplifies defining stubs.")
    TransformerEngine globalTransformer() default TransformerEngine.HANDLEBARS;

    @AttributeDefinition(
      name = "Request journal",
      description = "Enable the request journal, which records incoming requests for later verification. Protects against reserving too much memory."
    )
    boolean requestJournalEnabled() default false;

    @AttributeDefinition(
      name = "Max Request Journal Entries",
      description = "Set maximum number of entries in request journal (if enabled). When this limit is reached oldest entries will be discarded. 0 means no limits."
    )
    int requestJournalMaxSize() default 200;

    @AttributeDefinition(
      name = "CORS Enabled",
      description = "Enable automatic sending of CORS headers")
    boolean corsEnabled() default true;

    @AttributeDefinition(name = "Allow-Headers",
      description = "Access-Control-Allow-Headers")
    String allowHeaders() default "*";

    @AttributeDefinition(name = "Allow-Methods",
      description = "Access-Control-Allow-Methods")
    String allowMethods() default "*";

    @AttributeDefinition(name = "Allow-Origin",
      description = "Access-Control-Allow-Origin")
    String allowOrigin() default "*";
  }
}
