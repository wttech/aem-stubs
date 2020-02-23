package com.cognifide.wiremockonaem.aem.core.groovy;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.wiremockonaem.aem.core.ResourceResolverGentleman;
import com.icfolson.aem.groovy.console.GroovyConsoleService;

@Component(service = GroovyScriptExecutor.class)
public class GroovyScriptExecutor {

  private static Logger LOG = LoggerFactory.getLogger(GroovyScriptExecutor.class);

  @Reference
  ResourceResolverGentleman resourceResolverGentleman;

  @Reference
  GroovyConsoleService groovyConsoleService;

  public void runScript(String path) {
    resourceResolverGentleman.withResolver(resolver -> {
      LOG.info("Executing changed/added script {}", path);
      groovyConsoleService.runScript(new DummyRequest(resolver), new DummyResponse(), path);
      return true;
    });
  }
}
