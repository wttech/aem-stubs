package com.cognifide.aem.stubs.groovy;

import static com.cognifide.aem.stubs.Configuration.GROOVY_SCRIPT_LOCATION;
import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.observation.ResourceChange.ChangeType.REMOVED;

import java.util.List;
import java.util.Map;

import com.cognifide.aem.stubs.moco.Moco;
import com.github.dreamhead.moco.HttpServer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import com.icfolson.aem.groovy.console.api.BindingVariable;
import com.icfolson.aem.groovy.console.api.ScriptContext;

@Component(
  service = {BindingExtensionProvider.class, ResourceChangeListener.class}, immediate = true,
  property = {
    ResourceChangeListener.PATHS + "=" + GROOVY_SCRIPT_LOCATION,
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
public class GroovyScriptConfigurer implements ResourceChangeListener, BindingExtensionProvider {

  @Reference
  private GroovyScriptExecutor scriptExecutor;

  @Reference
  private Moco moco;

  @Override
  public Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
    return singletonMap("moco", new BindingVariable(moco.getServer(), HttpServer.class, "moco"));
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    if (hasRemovedScript(changes)) {
      moco.restartServer();
      scriptExecutor.runAllScripts();
    } else {
      changes.forEach(this::executeScript);
    }
  }

  private void executeScript(ResourceChange change) {
    scriptExecutor.runScript(change.getPath());
  }

  private boolean hasRemovedScript(List<ResourceChange> changes) {
    return changes.stream().anyMatch(c -> c.getType() == REMOVED );
  }
}
