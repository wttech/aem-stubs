package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.Stubs;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.cognifide.aem.stubs.core.groovy.GroovyScriptManager.ROOT_PATH;
import static org.apache.sling.api.resource.observation.ResourceChange.ChangeType.REMOVED;

@Component(
  service = {ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.PATHS + "=" + ROOT_PATH,
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
public class GroovyReloader implements ResourceChangeListener {

  private static Logger LOG = LoggerFactory.getLogger(GroovyReloader.class);
  @Reference
  private GroovyScriptManager scripts;

  @Reference
  private Stubs stubs;

  @Override
  public void onChange(List<ResourceChange> changes) {
    if (changes.stream().anyMatch(c -> c.getType() == REMOVED)) {
      LOG.debug("Reloading wiremock definitions and running all script");
      stubs.reload();
      scripts.runAll();
    } else {
      LOG.debug("Running changed wiremock stub scripts");
      changes.forEach(rc -> scripts.run(rc.getPath()));
    }
  }
}
