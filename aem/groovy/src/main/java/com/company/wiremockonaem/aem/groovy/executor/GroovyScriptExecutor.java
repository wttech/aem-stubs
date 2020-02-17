package com.company.wiremockonaem.aem.groovy.executor;

import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
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
      DummyRequest request = new DummyRequest(resolver);
      DummyResponse response = new DummyResponse();

      System.out.println(">>> running " + path);
      LOG.info(">>> running script path: {}", path);
      InputStream in = resolver.getResource(path).adaptTo(InputStream.class);
      String script = null;
      try {
        script = IOUtils.toString(in, StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println(script); //execute rather :)
      LOG.info(">>> running script {}", script);

      groovyConsoleService.runScript(request, response, path);
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
