package com.cognifide.aem.stubs.core;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

import com.cognifide.aem.stubs.core.util.JcrUtils;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Component(
  service = {StubManager.class, ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
@Designate(ocd = ConfigurableStubManager.Config.class)
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class ConfigurableStubManager implements StubManager, ResourceChangeListener {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableStubManager.class);

  @Reference
  private ResolverAccessor resolverAccessor;

  private Config config;

  private final List<Stubs<?>> runnables = new CopyOnWriteArrayList<>();

  @Override
  public void reload() {
    runnables.forEach(this::reload);
  }

  @Override
  public void reload(Stubs<?> runnable) {
    StubReload reload = new StubReload();
    runnable.initServer();
    mapAll(reload, runnable);
    runAll(reload, runnable);
    runnable.startServer();
    LOG.info(reload.summary());
  }

  private void runAll(StubReload reload, Stubs<?> runnable) {
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Running AEM Stubs scripts with extension '{}' under path '{}'", config.scriptExtension(), rootPath);

    resolverAccessor.consume(resolver -> {
      try {
        runAllUnderPath(reload, rootPath, resolver, runnable);
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  private void runAllUnderPath(StubReload reload, String rootPath, ResourceResolver resolver, Stubs<?> runnable) {
    final AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
      @Override
      protected void visit(Resource resource) {
        if (resource.isResourceType(JcrUtils.NT_FILE) && isScript(resource.getPath())) {
          try {
            reload.scriptsTotal++;
            runnable.runScript(resource);
          } catch (Exception e) {
            reload.scriptsFailed++;
            LOG.error("Cannot execute AEM Stubs script at path '{}'!", resource.getPath(), e);
          }
        }
      }
    };
    visitor.accept(resolver.getResource(rootPath));
  }

  @Override
  public Optional<Stubs<?>> findRunnable(String path) {
    return runnables.stream()
      .filter(runnable -> isScript(path, runnable) || isMapping(path, runnable))
      .findFirst();
  }

  private boolean isScript(String path, Stubs<?> runnable) {
    return StringUtils.startsWith(path, format("%s/%s/", getRootPath(), runnable.getId()))
      && StringUtils.endsWith(path, config.scriptExtension());
  }

  private boolean isMapping(String path, Stubs<?> runnable) {
    return StringUtils.startsWith(path, format("%s/%s/", getRootPath(), runnable.getId()))
      && StringUtils.endsWith(path, config.mappingExtension());
  }

  private boolean isScript(String path) {
    return isScriptExtension(path) && isNotExcludedPath(path);
  }

  private void mapAll(StubReload reload, Stubs<?> runnable) {
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Loading AEM Stubs mappings with extension '{}' under path '{}'", config.mappingExtension(), rootPath);

    resolverAccessor.consume(resolver -> {
      final AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
        @Override
        protected void visit(Resource resource) {
          if (resource.isResourceType(JcrUtils.NT_FILE) && isMapping(resource.getPath())) {
            try {
              reload.mappingsTotal++;
              runnable.loadMapping(resource);
            } catch (Exception e) {
              reload.mappingsFailed++;
              LOG.error("Cannot load AEM Stubs mapping at path '{}'!", resource.getPath(), e);
            }
          }
        }
      };
      visitor.accept(resolver.getResource(rootPath));
    });
  }

  private boolean isMapping(String path) {
    return isMappingExtension(path) && isNotExcludedPath(path);
  }

  private boolean isScriptExtension(String path) {
    return path.endsWith(config.scriptExtension());
  }

  private boolean isMappingExtension(String path) {
    return path.endsWith(config.mappingExtension());
  }

  private boolean isNotExcludedPath(String path) {
    return Arrays.stream(config.excluded_paths()).noneMatch(p -> wildcardMatch(path, p));
  }

  @Override
  public String getRootPath() {
    return config.resource_paths();
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    if (!config.resetOnChange()) {
      return;
    }

    changes.stream()
      .map(ResourceChange::getPath)
      .map(p -> StringUtils.removeEnd(p, "/" + JcrUtils.JCR_CONTENT))
      .map(this::findRunnable)
      .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
      .distinct()
      .forEach(this::reload);
  }

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  protected void bindStubs(Stubs<?> stubs) {
    runnables.add(stubs);
    reload(stubs);
  }

  @SuppressWarnings("PMD.NullAssignment")
  protected void unbindStubs(Stubs<?> stubs) {
    runnables.remove(stubs);
  }

  @Activate
  @Modified
  protected void update(Config config) {
    this.config = config;
    reload();
  }

  @ObjectClassDefinition(name = "AEM Stubs Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/conf/stubs";

    @AttributeDefinition(name = "Script Extension")
    String scriptExtension() default ".stub.groovy";

    @AttributeDefinition(name = "Mapping Extension")
    String mappingExtension() default ".stub.json";

    @AttributeDefinition(name = "Excluded Paths")
    String[] excluded_paths() default {"**/samples/*"};

    @AttributeDefinition(
      name = "Reload On Change",
      description = "Restart the server then load all mappings and run all scripts if any of those are changed.")
    boolean resetOnChange() default true;
  }
}
