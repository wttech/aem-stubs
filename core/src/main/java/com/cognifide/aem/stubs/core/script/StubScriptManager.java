package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.cognifide.aem.stubs.core.util.StreamUtils;
import com.google.common.collect.Lists;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.*;
import org.apache.commons.io.FilenameUtils;
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

  private static final Logger LOG = LoggerFactory.getLogger(StubScriptManager.class);

  private static final String QUERY = "SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])";

  @Reference
  private ResolverAccessor resolverAccessor;

  private Config config;

  private final List<Stubs<?>> runnables = Lists.newCopyOnWriteArrayList();

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  protected void bindStubs(Stubs<?> stubs) {
    runnables.add(stubs);
    stubs.reset();
  }

  @SuppressWarnings("PMD.NullAssignment")
  protected void unbindStubs(Stubs<?> stubs) {
    runnables.remove(stubs);
  }

  @Activate
  @Modified
  protected void update(Config config) {
    this.config = config;
  }

  /**
   * Run stub script at specified path
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void run(String path) {
    try {
      resolverAccessor.resolve(resolver -> execute(resolver, path));
    } catch (Exception e) {
      LOG.error("Cannot run AEM Stub script '{}'! Cause: {}", path, e.getMessage(), e);
    }
  }

  /**
   * Runs all stub scripts which:
   * - are located under configured root path,
   * - are not matching exclusion path patterns.
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void runAll(Stubs<?> runnable) {
    final String rootPath = String.format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Executing all AEM Stub scripts under path '{}'", rootPath);
    resolverAccessor.consume(resolver -> {
      try {
        StreamUtils.from(resolver.findResources(String.format(QUERY, rootPath), Query.JCR_SQL2))
          .filter(r -> isRunnable(r.getPath()))
          .map(Resource::getPath)
          .forEach(this::run);
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  public boolean isRunnable(String path) {
    return isExtensionCorrect(path) && isNotExcludedPath(path);
  }

  private boolean isNotExcludedPath(String path) {
    return Arrays.stream(config.excluded_paths()).noneMatch(p -> FilenameUtils.wildcardMatch(path, p));
  }

  private boolean isExtensionCorrect(String path) {
    return path.endsWith(config.extension());
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    final List<ResourceChange> scriptChanges = changes.stream()
      .filter(c -> isRunnable(c.getPath()))
      .collect(Collectors.toList());

    if (!scriptChanges.isEmpty()) {
      resolverAccessor.consume(resolver -> {
        scriptChanges.forEach(change -> {
          execute(resolver, change.getPath());
        });
      });
    }
  }

  private Object execute(ResourceResolver resolver, String path) {
    final Stubs<?> stubs = findRunnable(path);
    if (stubs == null) {
      LOG.warn("Executing Stub Script '{}' not possible - runnable not found.", path);
      return null;
    }

    final StubScript script = new StubScript(path, this, resolver);
    LOG.info("Executing Stub Script '{}'", script.getPath());
    script.getBinding().setVariable("stubs", stubs);
    stubs.prepare(script);
    final Object result = script.run();
    LOG.info("Executed Stub Script '{}'", script.getPath());
    return result;
  }

  public Stubs<?> findRunnable(String path) {
    for (Stubs<?> runnable : runnables) {
      final String pathPattern = String.format("%s/%s/*%s", getRootPath(), runnable.getId(), getExtension());
      if (FilenameUtils.wildcardMatch(path, pathPattern)) {
        return runnable;
      }
    }
    return null;
  }

  public String getRootPath() {
    return config.resource_paths();
  }

  public String getExtension() {
    return config.extension();
  }

  @ObjectClassDefinition(name = "AEM Stubs Scripts Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Extension")
    String extension() default ".groovy";

    @AttributeDefinition(name = "Excluded Paths")
    String[] excluded_paths() default {"**/internals/*"};
  }
}
