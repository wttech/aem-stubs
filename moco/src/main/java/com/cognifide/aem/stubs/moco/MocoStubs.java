package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.AbstractStubs;
import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.ApiUtils;
import com.github.dreamhead.moco.monitor.AbstractMonitor;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.runner;

@Component(
  service = {Stubs.class, MocoStubs.class, BindingExtensionProvider.class},
  immediate = true
)
@Designate(ocd = MocoStubs.Config.class)
public class MocoStubs extends AbstractStubs {

  private static final Logger LOG = LoggerFactory.getLogger(MocoStubs.class);

  @Reference
  private GroovyScriptManager groovyScriptManager;

  private HttpServer server;

  private Runner runner;

  private Config config;

  public HttpServer getServer() {
    return server;
  }

  private final AtomicBoolean initialized = new AtomicBoolean(false);

  // TODO does not work such approach, to be redesigned
  private void init() {
    if (initialized.compareAndSet(false, true)) {
      reset();
    }
  }

  @Override
  public void clear() {
    restart();
  }

  @Override
  public void reset() {
    clear(); // TODO reset moco via reflection
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
    server = httpServer(config.port(), ApiUtils.log(LOG::info), new AbstractMonitor() {
      @Override
      public void onMessageArrived(Request request) {
        init();
      }
    });
    runner = runner(server);
    runner.start();
  }

  private void stop() {
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

  @ObjectClassDefinition(name = "AEM Stubs - Moco Stubs")
  public @interface Config {

    @AttributeDefinition(name = "HTTP Server Port")
    int port() default 5555;
  }
}
