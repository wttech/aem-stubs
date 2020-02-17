package com.company.wiremockonaem.aem.groovy.executor;

import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icfolson.aem.groovy.console.GroovyConsoleService;

@Component(service = GroovyScriptExecutor.class)
public class GroovyScriptExecutor {

  private static Logger LOG = LoggerFactory.getLogger(GroovyScriptExecutor.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;

  @Reference
  GroovyConsoleService groovyConsoleService;

  public void runScript(String path) {
    try (ResourceResolver resolver = retrieveResourceResolver()) {
      groovyConsoleService.runScript(new DummyRequest(resolver), new DummyResponse(), path);
    }
  }

  private ResourceResolver retrieveResourceResolver() {
    try {
      return resourceResolverFactory
        .getServiceResourceResolver(
          singletonMap(SUBSERVICE, "com.cognifide.wiremock.aem.groovy"));
    } catch (LoginException e) {
      throw new RuntimeException(
        "Cannot create resource resolver for mapper service. Is service user mapper configured?");
    }
  }
}
