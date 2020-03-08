package com.cognifide.aem.stubs.wiremock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.Container;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockApp;

public class WiremockApp extends WireMockApp {

  public WiremockApp(Options options, Container container) {
    super(options, container);
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    addStubMapping(mappingBuilder.build());
  }
}
