package com.company.wiremockonaem.aem.groovy;

import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icfolson.aem.groovy.console.GroovyConsoleService;

@Component(
  service = ResourceChangeListener.class,
  property = {
    ResourceChangeListener.PATHS + "=" + "/var/groovyconsole/scripts/stub",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
public class StubScriptChangeListener implements ResourceChangeListener {

  private static Logger LOG = LoggerFactory.getLogger(StubScriptChangeListener.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;

  @Reference
  GroovyConsoleService groovyConsoleService;

  @Override
  public void onChange(
    List<ResourceChange> changes) {
    changes.forEach(c -> executeScript(c.getPath()));
  }

  private void executeScript(String path) {
    try {
      InputStream in = retrieveResourceResolver().getResource(path).adaptTo(InputStream.class);
      String script = IOUtils.toString(in, StandardCharsets.UTF_8);
      System.out.println(script); //execute rather :)
    } catch (IOException | RuntimeException e) {
      LOG.error("Cannot execute script", e);
    }
  }

  private ResourceResolver retrieveResourceResolver() {
    try {
      return resourceResolverFactory
        .getServiceResourceResolver(
          singletonMap(SUBSERVICE, "com.cognifide.wiremock.aem.groovy"));
    } catch (LoginException e) {
      throw new RuntimeException("Cannot create resource resolver for mapper service. Is service user mapper configured?");
    }
  }
}
