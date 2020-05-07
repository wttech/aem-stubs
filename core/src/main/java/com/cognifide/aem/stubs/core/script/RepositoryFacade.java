package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.StubsException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RepositoryFacade {

  private final StubScript script;

  public RepositoryFacade(StubScript script) {
    this.script = script;
  }

  public Optional<InputStream> useStream(String path) {
    return Optional.ofNullable(path)
      .map(p -> {
        if (p.startsWith("./")) {
          return String.format("%s/%s/jcr:content", script.getDirPath(), StringUtils.removeStart(p, "./"));
        } else if (!StringUtils.startsWith(p, "/")) {
          return String.format("%s/%s/jcr:content", script.getRootPath(), p);
        } else {
          return String.format("%s/jcr:content", p);
        }
      })
      .map(p -> script.getResourceResolver().getResource(p))
      .map(r -> r.adaptTo(InputStream.class))
      .map(BufferedInputStream::new);
  }

  public InputStream readStream(String path) {
    return useStream(path)
      .orElseThrow(() -> new StubsException(String.format("Cannot read repository file '%s' as stream!", path)));
  }

  public String readText(String path) {
    return useStream(path).map(input -> {
      try {
        return IOUtils.toString(input, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new StubsException(String.format("Cannot read repository file '%s' as text!", path), e);
      }
    }).orElseThrow(() -> new StubsException(String.format("Cannot read repository file '%s' as text!", path)));
  }

  public String getJson() {
    return readText(script.getResourcePath("json"));
  }

  public String getXml() {
    return readText(script.getResourcePath("xml"));
  }

  public String getTxt() {
    return readText(script.getResourcePath("txt"));
  }
}
