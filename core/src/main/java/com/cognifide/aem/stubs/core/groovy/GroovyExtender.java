package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.Stubs;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import com.icfolson.aem.groovy.console.api.BindingVariable;
import com.icfolson.aem.groovy.console.api.ScriptContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

import static java.util.Collections.singletonMap;

@Component(
  service = {BindingExtensionProvider.class, GroovyExtender.class},
  immediate = true
)
public class GroovyExtender implements BindingExtensionProvider {

  @Reference
  private Stubs stubs;

  @Override
  public Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
    return singletonMap("stubs", new BindingVariable(stubs, Stubs.class, ""));
  }
}
