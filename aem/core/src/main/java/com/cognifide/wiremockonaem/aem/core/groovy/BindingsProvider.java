package com.cognifide.wiremockonaem.aem.core.groovy;

import static java.util.Collections.singletonMap;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.cognifide.wiremockonaem.aem.core.Wiremock;
import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import com.icfolson.aem.groovy.console.api.BindingVariable;
import com.icfolson.aem.groovy.console.api.ScriptContext;

@Component(service = BindingExtensionProvider.class, immediate = true)
public class BindingsProvider implements BindingExtensionProvider{

  @Reference
  private Wiremock wiremock;

  @Override
  public Map<String, BindingVariable> getBindingVariables(
    ScriptContext scriptContext) {
    return singletonMap("wiremock", new BindingVariable(wiremock, Wiremock.class, "wiremock"));
  }
}
