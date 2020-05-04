package com.cognifide.aem.stubs.wiremock.util;

import com.cognifide.aem.stubs.core.StubsException;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

public class JcrFileReader {

  private final ResolverAccessor resolverAccessor;

  private final String rootPath;

  public JcrFileReader(ResolverAccessor resolverAccessor, String rootPath) {
    this.resolverAccessor = resolverAccessor;
    this.rootPath = rootPath;
  }

  public JcrFileReader(ResolverAccessor resolverAccessor) {
    this(resolverAccessor, null);
  }

  public <T> Optional<T> useStream(String fileName, Function<InputStream, T> processor) {
    return resolverAccessor.resolve(resolver ->
      Optional.of(resolver)
        .map(r -> r.getResource(String.format("%s/jcr:content", getAbsolutePath(fileName))))
        .map(r-> r.adaptTo(InputStream.class))
        .map(BufferedInputStream::new)
        .map(processor)
    );
  }

  public InputStream readStream(String fileName) {
    return useStream(fileName, Function.identity())
      .orElseThrow(() -> new StubsException(String.format("Cannot read JCR file '%s'!", fileName)));
  }

  public Optional<String> readText(String fileName) {
    return useStream(fileName, input -> {
      try {
        return IOUtils.toString(input, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new StubsException(String.format("Cannot read JCR file '%s'!", fileName), e);
      }
    });
  }

  public String getAbsolutePath(String name) {
    if (rootPath == null || rootPath.isEmpty() || name.startsWith("/")) {
      return name;
    }
    return String.format("%s/%s", rootPath, name);
  }
}
