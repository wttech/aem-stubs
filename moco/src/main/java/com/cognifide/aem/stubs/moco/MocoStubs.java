package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.AbstractStubs;
import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.script.StubScript;
import com.cognifide.aem.stubs.core.script.StubScriptManager;
import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.ApiUtils;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.runner;

@Component(
  service = {Stubs.class, MocoStubs.class, EventHandler.class},
  property = EventConstants.EVENT_TOPIC + "=" + StubScriptManager.SCRIPT_CHANGE_EVENT_TOPIC,
  immediate = true
)
@Designate(ocd = MocoStubs.Config.class)
public class MocoStubs extends AbstractStubs<HttpServer> {

  private static final Logger LOG = LoggerFactory.getLogger(MocoStubs.class);

  @Reference
  private StubScriptManager groovyScriptManager;

  private HttpServer server;

  private Runner runner;

  private Config config;

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
    groovyScriptManager.runAll();
  }

  @Override
  public void prepare(StubScript script) {
    script.getBinding().setVariable("stubs", this);
    script.getCompilerConfiguration().addCompilationCustomizers(new ImportCustomizer().addStaticStars(
      Moco.class.getName(),
      com.github.dreamhead.moco.Moco.class.getName()
    ));
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
    LOG.info("Starting AEM Stubs Moco Server");
    server = httpServer(config.port(), ApiUtils.log(LOG::info)); // TODO better handle this
    runner = runner(server);
    runner.start();
  }

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

  @ObjectClassDefinition(name = "AEM Stubs - Moco Server")
  public @interface Config {

    @AttributeDefinition(name = "HTTP Server Port")
    int port() default 5555;
  }
}
