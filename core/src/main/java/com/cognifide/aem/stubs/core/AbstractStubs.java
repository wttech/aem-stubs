package com.cognifide.aem.stubs.core;

import com.icfolson.aem.groovy.console.api.BindingExtensionProvider;
import com.icfolson.aem.groovy.console.api.BindingVariable;
import com.icfolson.aem.groovy.console.api.ScriptContext;

import java.util.Map;

import static java.util.Collections.singletonMap;

public abstract class AbstractStubs implements Stubs, BindingExtensionProvider {

  @Override
  public Map<String, BindingVariable> getBindingVariables(ScriptContext scriptContext) {
    return singletonMap(
      "stubs", new BindingVariable(this, Stubs.class,
        "https://bitbucket.cognifide.com/users/krystian.panek/repos/aem-stubs/browse/core/src/main/java/com/cognifide/aem/stubs/core/Stubs.java")
    );
  }

}
