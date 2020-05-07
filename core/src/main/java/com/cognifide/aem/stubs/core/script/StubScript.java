package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.StubsException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@SuppressWarnings("PMD.DataClass")
public class StubScript {

  private final String path;

  private final StubScriptManager manager;

  private final Stubs<?> runnable;

  private final Binding binding = new Binding();

  private final CompilerConfiguration compilerConfig = new CompilerConfiguration();

  private final GroovyShell shell = new GroovyShell(binding, compilerConfig);

  private final RepositoryFacade repository;

  private final Logger logger;

  private final ResourceResolver resourceResolver;

  public StubScript(String path, StubScriptManager manager, Stubs<?> runnable, ResourceResolver resolver) {
    this.path = path;
    this.manager = manager;
    this.runnable = runnable;
    this.resourceResolver = resolver;
    this.logger = createLogger(path);
    this.repository = new RepositoryFacade(this);

    binding.setVariable("script", this);
    binding.setVariable("logger", logger);
    binding.setVariable("repository", repository);
    binding.setVariable("resourceResolver", resolver);
  }

  public Binding getBinding() {
    return binding;
  }

  public CompilerConfiguration getCompilerConfig() {
    return compilerConfig;
  }

  public String getPath() {
    return path;
  }

  public String getRootPath() {
    return manager.getRootPath() + "/" + runnable.getId();
  }

  public String getDirPath() {
    return StringUtils.substringBeforeLast(path, "/");
  }

  public String getBaseName() {
    return FilenameUtils.getBaseName(path);
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

  public RepositoryFacade getRepository() {
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
    return repository.useStream(path)
      .map(InputStreamReader::new)
      .orElseThrow(() -> new StubsException(String.format("Cannot read stub script '%s'!", path)));
  }

  private Logger createLogger(String path) {
    return LoggerFactory.getLogger(String.format("%s(%s)", getClass().getSimpleName(), path));
  }
}
