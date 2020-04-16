package com.cognifide.aem.stubs.wiremock;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.cognifide.aem.stubs.core.Stub;
import com.cognifide.aem.stubs.core.Stubs;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import com.google.common.collect.Maps;

@Component(
  service = {Stubs.class, WiremockStubs.class},
  immediate = true
)
public class WiremockStubs implements Stubs<WireMockApp> {

  private Map<String, Stub<WireMockApp>> stubs = Collections.synchronizedMap(Maps.newLinkedHashMap());

  private WireMockApp app;

  protected RequestHandler buildStubRequestHandler() {
    return app.buildStubRequestHandler();
  }

  @Activate
  protected void start() {
    app = new WiremockApp(new WiremockConfig(), new NotImplementedContainer());
  }

  @Override
  public void reload() {
    app.resetAll();
    stubs.values().forEach(s -> s.definition(app));
  }

  @Override
  public void define(String id, Stub<WireMockApp> definition) {
    stubs.put(id, definition);
    reload();
  }
}
