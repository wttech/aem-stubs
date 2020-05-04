package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.StubsException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RepositoryFacade {

  private final ResourceResolver resolver;

  private final String absoluteRoot;

  private final String relativeRoot;

  public RepositoryFacade(ResourceResolver resolver, String relativeRoot, String absoluteRoot) {
    this.resolver = resolver;
    this.relativeRoot = relativeRoot;
    this.absoluteRoot = absoluteRoot;
  }

  public static RepositoryFacade forScript(String path, StubScriptManager manager, ResourceResolver resolver) {
    return new RepositoryFacade(resolver, StringUtils.substringBeforeLast(path, "/"), manager.getRootPath());
  }

  public Optional<InputStream> useStream(String path) {
    return Optional.ofNullable(path)
      .map(p -> {
        if (p.startsWith("./")) {
          return String.format("%s/%s/jcr:content", relativeRoot, StringUtils.removeStart(p, "./"));
        } else if (!StringUtils.startsWith(p, "/")) {
          return String.format("%s/%s/jcr:content", absoluteRoot, p);
        } else {
          return String.format("%s/jcr:content", p);
        }
      })
      .map(resolver::getResource)
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
}
