package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.Stubs;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import static org.apache.sling.api.resource.observation.ResourceChange.ChangeType.REMOVED;

@Component(
  service = {ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
public class GroovyReloader implements ResourceChangeListener {

  @Reference
  private GroovyScriptManager scripts;

  @Reference
  private Stubs stubs;

  @Override
  public void onChange(List<ResourceChange> changes) {
    if (changes.stream().anyMatch(c -> c.getType() == REMOVED)) {
      stubs.reload();
      scripts.runAll();
    } else {
      changes.forEach(rc -> scripts.run(rc.getPath()));
    }
  }
}
