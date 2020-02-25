package com.cognifide.aem.stubs.groovy;

import static com.cognifide.aem.stubs.Configuration.GROOVY_SCRIPT_LOCATION;

import java.util.List;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
  service = ResourceChangeListener.class,
  property = {
    ResourceChangeListener.PATHS + "=" + GROOVY_SCRIPT_LOCATION,
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
public class StubScriptChangeListener implements ResourceChangeListener {

  private static Logger LOG = LoggerFactory.getLogger(StubScriptChangeListener.class);

  @Reference
  private GroovyScriptExecutor groovyScriptExecutor;


  @Override
  public void onChange(List<ResourceChange> changes) {
    changes.forEach(this::executeScript);
  }

  private void executeScript(ResourceChange change) {
    LOG.info("Running script {}", change.getPath());
    groovyScriptExecutor.runScript(change.getPath());
  }
}
