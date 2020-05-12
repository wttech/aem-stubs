package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.StubsException;
import com.cognifide.aem.stubs.core.util.JcrUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;

public class StubScript {

  private final StubScriptManager manager;

  private final Stubs<?> runnable;

  private final Binding binding = new Binding();

  private final CompilerConfiguration compilerConfig = new CompilerConfiguration();

  private final GroovyShell shell = new GroovyShell(binding, compilerConfig);

  private final Repository repository;

  private final Logger logger;

  private final Resource resource;

  private final ResourceResolver resourceResolver;

  public StubScript(Resource resource, StubScriptManager manager, Stubs<?> runnable) {
    this.resource = resource;
    this.resourceResolver = resource.getResourceResolver();
    this.manager = manager;
    this.runnable = runnable;
    this.logger = createLogger(resource.getPath());
    this.repository = new Repository(this);

    binding.setVariable("script", this);
    binding.setVariable("stubs", runnable);
    binding.setVariable("resourceResolver", resourceResolver);
    binding.setVariable("logger", logger);
    binding.setVariable("repository", repository);
  }

  public Binding getBinding() {
    return binding;
  }

  public CompilerConfiguration getCompilerConfig() {
    return compilerConfig;
  }

  public String getPath() {
    return resource.getPath();
  }

  public String getRootPath() {
    return manager.getRootPath() + "/" + runnable.getId();
  }

  public String getDirPath() {
    return StringUtils.substringBeforeLast(getPath(), "/");
  }

  public String getBaseName() {
    return FilenameUtils.getBaseName(getPath());
  }

  public String getResourcePath(String extension) {
    return String.format("%s/%s.%s", getDirPath(), getBaseName(), extension);
  }

  public StubScriptManager getManager() {
    return manager;
  }

  public ResourceResolver getResourceResolver() {
    return resourceResolver;
  }

  public Stubs<?> getRunnable() {
    return runnable;
  }

  public Repository getRepository() {
    return repository;
  }

  public Logger getLogger() {
    return logger;
  }

  public Object run() {
    final Script shellScript = shell.parse(readSourceCode());
    return shellScript.run();
  }

  private Reader readSourceCode() {
    return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
      .map(r -> r.adaptTo(InputStream.class))
      .map(InputStreamReader::new)
      .orElseThrow(() -> new StubsException(String.format("Cannot read stub script '%s'!", getPath())));
  }

  private Logger createLogger(String path) {
    return LoggerFactory.getLogger(String.format("%s(%s)", getClass().getSimpleName(), path));
  }
}
