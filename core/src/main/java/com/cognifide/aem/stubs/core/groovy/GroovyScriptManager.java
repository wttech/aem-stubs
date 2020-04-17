package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.cognifide.aem.stubs.core.utils.StreamUtils;
import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.stream.Stream;

@Component(service = GroovyScriptManager.class, immediate = true)
public class GroovyScriptManager {

  private static Logger LOG = LoggerFactory.getLogger(GroovyScriptManager.class);

  static final String ROOT_PATH = "/var/groovyconsole/scripts/stubs";

  private static final String QUERY = String.format(
    "SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])", ROOT_PATH
  );

  @Reference
  private ResolverAccessor resolverAccessor;

  @Reference
  private GroovyExtender configurer;

  @Reference
  private GroovyConsoleService groovyConsoleService;

  public void run(String path) {
    LOG.info("Executing groovy script {}", path);
    RunScriptResponse response = resolverAccessor.resolve(resolver ->
      groovyConsoleService.runScript(new DummyRequest(resolver), new DummyResponse(), path)
    );
    LOG.info("Executed groovy script {}\nOutput:\n{}\nError:\n{}", path, response.getOutput(), response.getExceptionStackTrace());
  }

  @Activate
  public void runAll() {
    try {
      Stream<Resource> all = findAll();
      all.map(Resource::getPath).forEach(path -> {
        try {
          run(path);
        } catch (Exception e) {
          LOG.error("Cannot execute script at path '{}'! Cause: {}", path, e.getMessage(), e);
        }
      });
    } catch (Exception e) {
      LOG.error("Cannot search for scripts! Cause: {}", e.getMessage(), e);
    }
  }

  public Stream<Resource> findAll() {
    return resolverAccessor.resolve(r -> StreamUtils.from(r.findResources(QUERY, Query.JCR_SQL2)));
  }
}
