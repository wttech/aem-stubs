package com.cognifide.aem.stubs.groovy;

import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.scripts.ScriptResolver;
import com.icfolson.aem.groovy.console.GroovyConsoleService;

@Component(service = GroovyScriptExecutor.class)
public class GroovyScriptExecutor {
  private static Logger LOG = LoggerFactory.getLogger(GroovyScriptExecutor.class);

  @Reference
  ScriptResolver scriptResolver;

  @Reference
  GroovyConsoleService groovyConsoleService;

  public void runScript(String path) {
    LOG.info("Executing groovy script {}", path);
    scriptResolver.withResourceResolver(resolver ->
      groovyConsoleService.runScript(new DummyRequest(resolver), new DummyResponse(), path)
    );
  }

  public void runAllScripts() {
      scriptResolver.getAllScripts()
        .map(Resource::getPath)
        .forEach(this::runScript);
  }
}
