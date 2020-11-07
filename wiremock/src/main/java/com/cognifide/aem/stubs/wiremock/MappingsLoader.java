package com.cognifide.aem.stubs.wiremock;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;

import com.cognifide.aem.stubs.core.StubsException;
import com.cognifide.aem.stubs.core.util.JcrUtils;
import com.cognifide.aem.stubs.wiremock.mapping.MappingCollection;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.common.JsonException;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

class MappingsLoader {
  private final WireMockApp app;

  public MappingsLoader(WireMockApp app) {
    this.app = app;
  }

  public void loadMapping(Resource file) {
    Optional.ofNullable(file.getChild(JcrUtils.JCR_CONTENT))
      .flatMap(fileContent -> Optional.of(fileContent)
        .map(r -> r.adaptTo(InputStream.class))
        .map(BufferedInputStream::new))
      .ifPresent(input -> {
        app.mappingFrom((stubMappings) -> {
          try {
            MappingCollection stubCollection = Json
              .read(IOUtils.toString(input, StandardCharsets.UTF_8.displayName()),
                MappingCollection.class);
            for (StubMapping mapping : stubCollection.getMappings()) {
              mapping.setDirty(false);
              stubMappings.addMapping(mapping);
            }
          } catch (JsonException | IOException e) {
            throw new StubsException(String
              .format("Cannot load AEM Stubs mapping from resource at path '%s'!", file.getPath()),
              e);
          }
        });
      });
  }
}
