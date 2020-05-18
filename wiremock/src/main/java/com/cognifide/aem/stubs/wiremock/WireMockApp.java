package com.cognifide.aem.stubs.wiremock;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;

public class WireMockApp {

  private final com.github.tomakehurst.wiremock.core.WireMockApp app;

  public WireMockApp(ResolverAccessor resolverAccessor, String rootPath, boolean globalTransformer) {
    WireMockOptions wiremockOptions = new WireMockOptions(resolverAccessor, rootPath, globalTransformer);
    app = new com.github.tomakehurst.wiremock.core.WireMockApp(wiremockOptions,
      new NotImplementedContainer());
  }

  public void mappingFrom(MappingsLoader loader) {
    app.loadMappingsUsing(loader);
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    app.addStubMapping(mappingBuilder.build());
  }

  public StubRequestHandler buildStubRequestHandler() {
    return app.buildStubRequestHandler();
  }
}
