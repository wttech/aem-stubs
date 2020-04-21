package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.cognifide.aem.stubs.core.utils.StreamUtils;
import com.icfolson.aem.groovy.console.GroovyConsoleService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.apache.commons.io.FilenameUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.Arrays;
import java.util.List;

import static org.apache.sling.api.resource.observation.ResourceChange.ChangeType.REMOVED;

@Component(
  service = {GroovyScriptManager.class, ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
@Designate(ocd = GroovyScriptManager.Config.class)
public class GroovyScriptManager implements ResourceChangeListener {

  private static final Logger LOG = LoggerFactory.getLogger(GroovyScriptManager.class);

  private static final String QUERY = "SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])";

  @Reference
  private ResolverAccessor resolverAccessor;

  @Reference
  private GroovyConsoleService groovyConsole;

  private Config config;

  private BundleContext bundleContext;

  @Activate
  @Modified
  protected void update(Config config, BundleContext bundleContext) {
    this.config = config;
    this.bundleContext = bundleContext;
  }

  public void run(String path) {
    LOG.info("Executing groovy script {}", path);
    RunScriptResponse response = resolverAccessor.resolve(resolver ->
      groovyConsole.runScript(new DummyRequest(resolver), new DummyResponse(), path)
    );
    LOG.info("Executed groovy script {}\nOutput:\n{}\nError:\n{}", path, response.getOutput(), response.getExceptionStackTrace());
  }

  public void runAll() {
    resolverAccessor.consume(resolver -> {
      try {
        StreamUtils.from(resolver.findResources(String.format(QUERY, config.resource_paths()), Query.JCR_SQL2))
          .filter(r -> Arrays.stream(config.excluded_paths()).noneMatch(p -> FilenameUtils.wildcardMatch(r.getPath(), p)))
          .map(Resource::getPath)
          .forEach(this::run);
           } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    if (changes.stream().anyMatch(c -> c.getType() == REMOVED)) {
      LOG.debug("Reloading all AEM Stubs scripts");
      runAll();
    } else {
      LOG.debug("Reloading changed AEM Stubs scripts");
      changes.forEach(rc -> run(rc.getPath()));
    }
  }

  @ObjectClassDefinition(name = "AEM Stubs - Groovy Script Manager")
  public @interface Config {

    @AttributeDefinition(name = "Scripts Root Path")
    String resource_paths() default "/var/groovyconsole/scripts/stubs";

    @AttributeDefinition(name = "Scripts Excluded Paths")
    String[] excluded_paths() default {"**/internals/*"};
  }
}
