package com.company.wiremockonaem.aem.groovy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.wiremockonaem.aem.groovy.executor.GroovyScriptExecutor;

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
  private GroovyScriptExecutor groovyScriptExecutor;


  @Override
  public void onChange(
    List<ResourceChange> changes) {
    changes.forEach(c -> executeScript(c.getPath()));
  }

  private void executeScript(String path) {
    LOG.info("Running script {}", path);
    groovyScriptExecutor.runScript(path);
  }
}
