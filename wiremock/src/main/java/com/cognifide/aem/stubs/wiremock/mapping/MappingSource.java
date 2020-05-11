package com.cognifide.aem.stubs.wiremock.mapping;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.common.JsonException;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import com.github.tomakehurst.wiremock.standalone.MappingFileException;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;

public class MappingSource extends JsonFileMappingsSource {

  private final FileSource mappingsFileSource;
  private final String mappingExtension;

  public MappingSource(FileSource mappingsFileSource, String mappingExtension) {
    super(mappingsFileSource);
    this.mappingsFileSource = mappingsFileSource;
    this.mappingExtension = mappingExtension;
  }

  @Override
  public void loadMappingsInto(StubMappings stubMappings) {
    mappingsFileSource.listFilesRecursively()
      .stream()
      .filter(mappingFile -> mappingFile.name().endsWith(mappingExtension))
      .forEach(mappingFile -> {
        try {
          MappingCollection stubCollection = Json
            .read(mappingFile.readContentsAsString(), MappingCollection.class);
          for (StubMapping mapping : stubCollection.getMappings()) {
            mapping.setDirty(false);
            stubMappings.addMapping(mapping);
          }
        } catch (JsonException e) {
          throw new MappingFileException(mappingFile.getPath(), e.getErrors().first().getDetail());
        }
      });
  }
}
