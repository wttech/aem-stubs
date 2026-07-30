package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.StubManager;
import com.cognifide.aem.stubs.core.util.JcrUtils;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ApiUtils;
import com.github.dreamhead.moco.parser.HttpServerParser;
import groovy.lang.Binding;
import groovy.lang.Closure;
import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.runtime.MethodClosure;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.runner;

@Component(
  service = Stubs.class,
  immediate = true
)
@Designate(ocd = MocoStubs.Config.class)
public class MocoStubs implements Stubs<HttpServer> {

  public static final String ID = "moco";

  private static final Logger LOG = LoggerFactory.getLogger(MocoStubs.class);

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private StubManager manager;

  @Reference(policyOption = ReferencePolicyOption.GREEDY)
  private ResolverAccessor resolverAccessor;

  private ActualHttpServer server;

  private Runner runner;

  private Config config;

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public HttpServer getServer() {
    return server;
  }

  @Activate
  protected void activate(Config config) {
    this.config = config;
    manager.register(this);
    manager.reload(this);
  }

  @Deactivate
  protected void deactivate() {
    manager.unregister(this);
    stop();
  }

  @Override
  public void initServer() {
    stop();

    if (config.logging()) {
      server = (ActualHttpServer) httpServer(config.port(), ApiUtils.log(LOG::info));
    } else {
      server = (ActualHttpServer) httpServer(config.port());
    }
  }

  @Override
  public void startServer() {
    LOG.info("Starting AEM Stubs Moco Server");
    runner = runner(server);
    runner.start();
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void stop() {
    if (runner != null) {
      LOG.info("Stopping AEM Stubs Moco Server");
      runner.stop();
    }
    runner = null;
    server = null;
  }

  @Override
  public void runScript(Resource resource) {
    final StubScript script = new StubScript(resource, manager, this);
    final JcrResourceReaderFactory jcrResourceReaderFactory = new JcrResourceReaderFactory(resolverAccessor);
    Closure c = new MethodClosure(jcrResourceReaderFactory, "jcr");
    script.getBinding().setVariable("jcr", c);
    bindStaticMethods(script.getBinding(), MocoUtils.class, Moco.class);

    script.run();
  }

  /**
   * Binds static methods as closures instead of using an {@code ImportCustomizer}
   * static-star import, which would force cross-bundle resolution of guava types
   * embedded in this bundle (which are embedded in this jar as private package and
   * no longer available in AEM LTS / AEMaaCS as public packages)
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void bindStaticMethods(Binding binding, Class<?>... classes) {
    final Set<String> boundNames = new LinkedHashSet<>();
    for (Class<?> clazz : classes) {
      for (Method method : clazz.getMethods()) {
        if (Modifier.isStatic(method.getModifiers()) && boundNames.add(method.getName())) {
          binding.setVariable(method.getName(), new MethodClosure(clazz, method.getName()));
        }
      }
    }
  }

  @Override
  public void loadMapping(Resource file) {
    Optional.ofNullable(file.getChild(JcrUtils.JCR_CONTENT))
      .flatMap(fileContent -> Optional.of(fileContent)
        .map(r -> r.adaptTo(InputStream.class))
        .map(BufferedInputStream::new))
      .ifPresent(input -> {
        final ActualHttpServer configServer = (ActualHttpServer) new HttpServerParser().parseServer(
          ImmutableList.<InputStream>of(input), config.port(), !config.logging()
        );
        server = server.mergeServer(configServer);
      });
  }

  @ObjectClassDefinition(name = "AEM Stubs Moco Server")
  public @interface Config {

    @AttributeDefinition(name = "HTTP Server Port")
    int port() default 5555;

    @AttributeDefinition(name = "Log requests and responses")
    boolean logging() default false;
  }
}
