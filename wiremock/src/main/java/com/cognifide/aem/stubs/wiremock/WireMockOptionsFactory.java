package com.cognifide.aem.stubs.wiremock;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Collections;
import java.util.Map;

import com.cognifide.aem.stubs.wiremock.transformers.PebbleTransformer;
import com.cognifide.aem.stubs.wiremock.util.JcrFileReader;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionLoader;

class WireMockOptionsFactory {

  private final WireMockStubs wireMockStubs;
  private final Map<String, Extension> extensions = newLinkedHashMap();;

  public WireMockOptionsFactory(WireMockStubs wireMockStubs) {
    this.wireMockStubs = wireMockStubs;
    this.addExtensions();
  }

  WireMockOptions create(){
    return new WireMockOptions(wireMockStubs.getResolverAccessor(),
      getRootPath(),
      wireMockStubs.getConfig().requestJournalEnabled(),
      wireMockStubs.getConfig().maxRequestJournalEntries(),
      extensions);
  }

  private String getRootPath(){
    return wireMockStubs.getRootPath() + "/" + wireMockStubs.getId();
  }

  private void addExtensions() {
    JcrFileReader jcrFileReader = new JcrFileReader(wireMockStubs.getResolverAccessor(), getRootPath());
    extensions.putAll(ExtensionLoader.asMap(
      Collections.singletonList(new PebbleTransformer(jcrFileReader,
        wireMockStubs.getConfig().globalTransformer())))
    );
  }
}
