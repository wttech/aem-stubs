package com.cognifide.aem.stubs.wiremock.jcr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.cognifide.aem.stubs.core.StubsException;
import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import org.apache.commons.io.IOUtils;

public class JcrFileReader {

  private final ResolverAccessor resolverAccessor;
  private final String rootPath;

  public JcrFileReader(ResolverAccessor resolverAccessor, String rootPath) {
    this.resolverAccessor = resolverAccessor;
    this.rootPath = rootPath;
  }

  public InputStream getInputStream(String fileName) {
    String path = String.format("%s/jcr:content", getAbsolutePath(fileName));
    return resolverAccessor.resolve(r -> r.getResource(path).adaptTo(InputStream.class));
  }

  public String readAsText(String fileName) {
    try {
      return IOUtils.toString(new BufferedInputStream(getInputStream(fileName)), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new StubsException(String.format("Cannot read JCR file '%s'!", fileName), e);
    }
  }

  public String getAbsolutePath(String name) {
    return String.format("%s/%s", rootPath, name);
  }
}
