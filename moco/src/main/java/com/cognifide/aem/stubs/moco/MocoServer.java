package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.groovy.GroovyScriptManager;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import com.icfolson.aem.groovy.console.api.BindingVariable;
import com.icfolson.aem.groovy.console.api.ScriptContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Map;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.runner;
import static java.util.Collections.singletonMap;

@Component(
  service = {Stubs.class, MocoServer.class, BindingExtensionProvider.class},
  immediate = true
)
@Designate(ocd = MocoServer.Config.class)
public class MocoServer implements Stubs, BindingExtensionProvider {

  @Reference
  private GroovyScriptManager groovyScriptManager;

  private HttpServer server;

  private Runner runner;

  private Config config;

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

  @Activate
  @Modified
  protected void activate(Config config) {
    this.config = config;
    groovyScriptManager.await(getClass(), this::reset);
  }

  @Deactivate
  protected void deactivate() {
    stop();
  }

  private void start() {
    server = httpServer(config.port());
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

  @Override
  public Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
    return singletonMap("moco", new BindingVariable(server, HttpServer.class, ""));
  }

  @ObjectClassDefinition(name = "AEM Stubs - Moco Server")
  public @interface Config {

    @AttributeDefinition(description = "HTTP Server Port")
    int port() default 5555;
  }
}
