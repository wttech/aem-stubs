package com.cognifide.aem.stubs.wiremock;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.cognifide.aem.stubs.core.StubsException;
import com.cognifide.aem.stubs.core.util.JcrUtils;
import com.cognifide.aem.stubs.wiremock.mapping.MappingCollection;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.common.JsonException;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.StubManager;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.cognifide.aem.stubs.wiremock.servlet.WireMockServlet;
import com.github.tomakehurst.wiremock.http.Request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
    Optional.ofNullable(file.getChild(JcrUtils.JCR_CONTENT))
      .flatMap(fileContent -> Optional.of(fileContent)
        .map(r -> r.adaptTo(InputStream.class))
        .map(BufferedInputStream::new))
      .ifPresent(input -> {
        app.mappingFrom((stubMappings) -> {
          try {
            MappingCollection stubCollection = Json.read(IOUtils.toString(input, StandardCharsets.UTF_8.displayName()), MappingCollection.class);
            for (StubMapping mapping : stubCollection.getMappings()) {
              mapping.setDirty(false);
              stubMappings.addMapping(mapping);
            }
          } catch (JsonException | IOException e) {
            throw new StubsException(String.format("Cannot load AEM Stubs mapping from resource at path '%s'!", file.getPath()), e);
          }
        });
      });
  }

  @Override
  public void runScript(Resource resource) {
    final StubScript script = new StubScript(resource, manager, this);

    script.getCompilerConfig().addCompilationCustomizers(new ImportCustomizer()
      .addStaticStars(WireMockUtils.class.getName())
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
    app = new WireMockApp(resolverAccessor, manager.getRootPath() + "/" + getId(), config.globalTransformer());
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
    return new WireMockServlet(config.path(), app.buildStubRequestHandler());
  }

  public boolean isBypassable(ServletRequest request) {
    return config.filterBypass()
      && request instanceof HttpServletRequest
      && ((HttpServletRequest) request).getRequestURI().startsWith(config.path() + "/");
  }

  public WireMockServlet getServlet() {
    return servlet;
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
      description = "Enables Pebble template engine / templating"
        + " for response body content and file paths when loading body files. Effectively enables dynamic file loading"
        + " instead of preloading and simplifies defining stubs.")
    boolean globalTransformer() default true;
  }
}
