package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.*;

@Component(service = Moco.class, immediate = true)
public class MocoService implements Moco {

  private HttpServer server;

  private Runner runner;

  @Activate
  public void startServer() {
    server = httpServer(5502);

    server.request(by(uri("/debug"))).response("it works!");

    runner = runner(server);
    runner.start();

    server.request(by(uri("/debug2"))).response("it works!");
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
  public void restartServer() {
    stopServer();
    startServer();
  }

  @Override
  public HttpServer getServer() {
    return server;
  }
}
