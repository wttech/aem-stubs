package com.cognifide.aem.stubs.wiremock.jcr;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;

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
    return new BufferedReader(new InputStreamReader(getInputStream(fileName)))
      .lines()
      .collect(joining("\n"));
  }


  public String getAbsolutePath(String name) {
    return String.format("%s/%s", rootPath, name);
  }
}
