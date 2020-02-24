package com.cognifide.wiremockonaem.aem.core.groovy;

import static com.cognifide.wiremockonaem.aem.core.Configuration.GROOVY_SCRIPT_LOCATION;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.jcr.query.Query;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.wiremockonaem.aem.core.ResourceResolverGentleman;
import com.icfolson.aem.groovy.console.GroovyConsoleService;

@Component(service = GroovyScriptExecutor.class)
public class GroovyScriptExecutor {

  private static final String QUERY = String
    .format("SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])",
      GROOVY_SCRIPT_LOCATION);

  private static Logger LOG = LoggerFactory.getLogger(GroovyScriptExecutor.class);

  @Reference
  ResourceResolverGentleman resourceResolverGentleman;

  @Reference
  GroovyConsoleService groovyConsoleService;

  public void runScript(String path) {
    resourceResolverGentleman.withResolver(resolver -> {
      LOG.info("Executing changed/added script {}", path);
      groovyConsoleService.runScript(new DummyRequest(resolver), new DummyResponse(), path);
    });
  }

  public void runAllScripts() {
    resourceResolverGentleman.withResolver(resourceResolver ->
      getAllScripts(resourceResolver)
        .map(Resource::getPath)
        .forEach(this::runScript));
  }

  private Stream<Resource> getAllScripts(ResourceResolver resolver) {
    return StreamSupport.stream(findScripts(resolver), false);
  }

  private static Spliterator<Resource> findScripts(ResourceResolver resolver) {
    return queryForScripts(resolver).spliterator();
  }

  private static Iterable<Resource> queryForScripts(ResourceResolver resolver) {
    return () -> resolver.findResources(QUERY, Query.JCR_SQL2);
  }
}
