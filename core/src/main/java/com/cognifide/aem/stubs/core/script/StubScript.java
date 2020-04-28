package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.StubsException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.*;
import java.util.Optional;

public class StubScript {

  private final ResourceResolver resourceResolver;

  private final String path;

  private final Binding binding = new Binding();

  private final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

  private final GroovyShell shell = new GroovyShell(binding, compilerConfiguration);

  public StubScript(ResourceResolver resourceResolver, String path) {
    this.resourceResolver = resourceResolver;
    this.path = path;

    binding.setVariable("script", this);
    binding.setVariable("resourceResolver", resourceResolver);
  }

  public Reader getSourceCode() {
    return Optional.ofNullable(resourceResolver.getResource(path + "/jcr:content"))
      .map(r -> r.adaptTo(InputStream.class))
      .map(BufferedInputStream::new)
      .map(InputStreamReader::new)
      .orElseThrow(() -> new StubsException(String.format("Cannot read Groovy Stub Script '%s'!", path)));
  }

  public ResourceResolver getResourceResolver() {
    return resourceResolver;
  }

  public Binding getBinding() {
    return binding;
  }

  public CompilerConfiguration getCompilerConfiguration() {
    return compilerConfiguration;
  }

  public String getPath() {
    return path;
  }

  public Object run() {
    final Script shellScript = shell.parse(getSourceCode());
    return shellScript.run();
  }
}
