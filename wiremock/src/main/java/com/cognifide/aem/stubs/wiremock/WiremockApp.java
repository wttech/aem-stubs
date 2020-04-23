package com.cognifide.aem.stubs.wiremock;

import java.util.Map;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.ProxyResponseRenderer;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import com.github.tomakehurst.wiremock.http.StubResponseRenderer;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;
import com.github.tomakehurst.wiremock.verification.DisabledRequestJournal;
import com.google.common.collect.ImmutableList;

public class WiremockApp {

  private final WireMockApp app;

  public WiremockApp(ResolverAccessor resolverAccessor, String rootPath) {
    WiremockConfig wiremockConfig = new WiremockConfig(resolverAccessor, rootPath);
    app = new WireMockApp(wiremockConfig, new NotImplementedContainer());
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    app.addStubMapping(mappingBuilder.build());
  }

  public StubRequestHandler buildStubRequestHandler(){
    Options options = app.getOptions();
    Map<String, PostServeAction > postServeActions = options.extensionsOfType(PostServeAction.class);
    return new StubRequestHandler(
      app,
      new StubResponseRenderer(
        options.filesRoot(),
        app.getGlobalSettingsHolder(),
        new ProxyResponseRenderer(
          options.proxyVia(),
          options.httpsSettings().trustStore(),
          options.shouldPreserveHostHeader(),
          options.proxyHostHeader(),
          app.getGlobalSettingsHolder()),
        ImmutableList.copyOf(options.extensionsOfType(ResponseTransformer.class).values())
      ),
      app,
      postServeActions,
      new DisabledRequestJournal()
    ){
      @Override
      protected boolean logRequests() {
        return false;
      }
    };
  }
}
