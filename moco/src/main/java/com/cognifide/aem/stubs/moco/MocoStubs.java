package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.Stub;
import com.cognifide.aem.stubs.core.Stubs;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import com.google.common.collect.Maps;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Collections;
import java.util.Map;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.*;

@Component(service = {Stubs.class, MocoStubs.class}, immediate = true)
public class MocoStubs implements Stubs<HttpServer> {

  private Map<String, Stub<HttpServer>> stubs = Collections.synchronizedMap(Maps.newLinkedHashMap());

  private HttpServer server;

  private Runner runner;

  @Activate
  public void startServer() {
    server = httpServer(5502);
    stubs.values().forEach(s -> s.definition(server));
    runner = runner(server);
    runner.start();
  }

  @Deactivate
  public void stopServer() {
    if (runner != null) {
      runner.stop();
    }
    runner = null;
    server = null;
  }

  @Override
  public void reload() {
    stopServer();
    startServer();
  }

  @Override
  public void define(String id, Stub<HttpServer> definition) {
    stubs.put(id, definition);
    reload();
  }
}
