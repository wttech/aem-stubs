package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.*;

@Component(service = MocoService.class, immediate = true)
public class MocoService {

  private HttpServer server;

  private Runner runner;

  @Activate
  public void start() {
    server = httpServer(5502);
    server.request(by(uri("/hello-world"))).response("hello world");

    runner = runner(server);
    runner.start();
  }

  @Deactivate
  public void stop() {
    runner.stop();
    runner = null;
    server = null;
  }
}
