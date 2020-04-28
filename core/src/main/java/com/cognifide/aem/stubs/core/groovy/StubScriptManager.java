package com.cognifide.aem.stubs.core.groovy;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.cognifide.aem.stubs.core.utils.StreamUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.apache.commons.io.FilenameUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.query.Query;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component(
  service = {StubScriptManager.class, ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
@Designate(ocd = StubScriptManager.Config.class)
public class StubScriptManager implements ResourceChangeListener {

  public static final String SCRIPT_CHANGE_EVENT_TOPIC = "com/cognifide/aem/stubs/ScriptChange";

  public static final String SCRIPT_CHANGE_EVENT_RESOURCE_CHANGES = "resourceChanges";

  private static final Logger LOG = LoggerFactory.getLogger(StubScriptManager.class);

  private static final String QUERY = "SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])";

  @Reference
  private ResolverAccessor resolverAccessor;

  @Reference
  private StubScriptExecutor stubScriptExecutor;

  @Reference
  private EventAdmin eventAdmin;

  private Config config;

  private BundleContext bundleContext;

  @Activate
  @Modified
  protected void update(Config config, BundleContext bundleContext) {
    this.config = config;
    this.bundleContext = bundleContext;
  }

  /**
   * Run stub script at specified path
   */
  public void run(String path) {
    resolverAccessor.resolve(resolver -> stubScriptExecutor.execute(resolver, path));
  }

  /**
   * Runs all stub scripts which:
   * - are located under configured root path,
   * - are not matching exclusion path patterns.
   */
  public void runAll() {
    LOG.info("Executing all AEM Stub scripts under path '{}'", config.resource_paths());
    resolverAccessor.consume(resolver -> {
      try {
        StreamUtils.from(resolver.findResources(String.format(QUERY, config.resource_paths()), Query.JCR_SQL2))
          .filter(r -> filter(r.getPath()))
          .map(Resource::getPath)
          .forEach(this::run);
           } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  private boolean filter(String path) {
    return isNotExcludedPath(path) && isGroovyScript(path);
  }

  private boolean isNotExcludedPath(String path){
    return Arrays.stream(config.excluded_paths()).noneMatch(p -> FilenameUtils.wildcardMatch(path, p));
  }

  private boolean isGroovyScript(String path){
    return path.endsWith(".groovy");
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    final List<ResourceChange> scriptChanges = changes.stream()
      .filter(c -> filter(c.getPath()))
      .collect(Collectors.toList());

    if (!scriptChanges.isEmpty()) {
      eventAdmin.postEvent(new Event(SCRIPT_CHANGE_EVENT_TOPIC, ImmutableMap.of(
        SCRIPT_CHANGE_EVENT_RESOURCE_CHANGES, scriptChanges
      )));
    }
  }

  public String getScriptRootPath(){
    return config.resource_paths();
  }

  @ObjectClassDefinition(name = "AEM Stubs Scripts Manager")
  public @interface Config {

    @AttributeDefinition(name = "Scripts Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Scripts Excluded Paths")
    String[] excluded_paths() default {"**/internals/*"};
  }
}
