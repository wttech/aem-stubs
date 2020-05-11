package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.script.StubScriptManager;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ApiUtils;
import com.github.dreamhead.moco.mount.MountPredicate;
import com.github.dreamhead.moco.mount.MountTo;
import com.github.dreamhead.moco.parser.HttpServerParser;
import com.google.common.collect.ImmutableList;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.runner;

@Component(
  service = {Stubs.class, MocoStubs.class},
  immediate = true
)
@Designate(ocd = MocoStubs.Config.class)
public class MocoStubs implements Stubs<HttpServer> {

  public static final String ID = "moco";

  private static final Logger LOG = LoggerFactory.getLogger(MocoStubs.class);

  @Reference
  private StubScriptManager scriptManager;

  @Reference
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

  @Override
  public void clear() {
    restart();
  }

  @Override
  public void reset() {
    clear();
    scriptManager.runAll(this);
  }

  @Override
  public void prepare(StubScript script) {
    script.getCompilerConfig().addCompilationCustomizers(new ImportCustomizer().addStaticStars(
      MocoUtils.class.getName(),
      Moco.class.getName()
    ));
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
    LOG.info("Starting AEM Stubs Moco Server");

    if (config.logging()) {
      server = (ActualHttpServer) httpServer(config.port(), ApiUtils.log(LOG::info));
    } else {
      server = (ActualHttpServer) httpServer(config.port());
    }

    resolverAccessor.consume(resolver -> {
      Optional.ofNullable(resolver.getResource("/var/stubs/moco/config.json/jcr:content"))
      .map(r -> r.adaptTo(InputStream.class))
      .map(BufferedInputStream::new)
      .ifPresent(input -> {
        ActualHttpServer configServer = (ActualHttpServer) new HttpServerParser().parseServer(
          ImmutableList.of(input), Optional.of(config.port())
        );
         server = server.mergeServer(configServer);
      });
    });

    runner = runner(server);
    runner.start();
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void stop() {
    LOG.info("Stopping AEM Stubs Moco Server");
    if (runner != null) {
      runner.stop();
    }
    runner = null;
    server = null;
  }

  private void restart() {
    stop();
    start();
  }

  @ObjectClassDefinition(name = "AEM Stubs Moco Server")
  public @interface Config {

    @AttributeDefinition(name = "HTTP Server Port")
    int port() default 5555;

    @AttributeDefinition(name = "Log requests and responses")
    boolean logging() default false;
  }
}
