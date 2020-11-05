package com.cognifide.aem.stubs.wiremock;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Collections;
import java.util.Map;

import com.cognifide.aem.stubs.wiremock.transformers.PebbleTransformer;
import com.cognifide.aem.stubs.wiremock.util.JcrFileReader;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionLoader;

class WireMockOptionsFactory {

  private final WireMockStubs stubs;
  private final Map<String, Extension> extensions = newLinkedHashMap();

  public WireMockOptionsFactory(WireMockStubs stubs) {
    this.stubs = stubs;
    this.addExtensions();
  }

  WireMockOptions create(){
    return new WireMockOptions(stubs.getResolverAccessor(),
      getRootPath(),
      stubs.getConfig().requestJournalEnabled(),
      stubs.getConfig().requestJournalMaxSize(),
      extensions);
  }

  private String getRootPath(){
    return stubs.getRootPath() + "/" + stubs.getId();
  }

  private void addExtensions() {
    JcrFileReader jcrFileReader = new JcrFileReader(stubs.getResolverAccessor(), getRootPath());
    extensions.putAll(ExtensionLoader.asMap(
      Collections.singletonList(new PebbleTransformer(jcrFileReader,
        stubs.getConfig().globalTransformer())))
    );
  }
}
