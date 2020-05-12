package com.cognifide.aem.stubs.core;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

import com.cognifide.aem.stubs.core.util.JcrUtils;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.google.common.collect.Lists;
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
import java.util.stream.Collectors;
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

  private final List<Stubs<?>> runnables = Lists.newCopyOnWriteArrayList();

  @Override
  public void reload(Stubs<?> runnable) {
    StubReload reload = new StubReload();
    runnable.initServer();
    runAll(runnable);
    mapAll(runnable);
    runnable.startServer();
    LOG.info(reload.toString());
  }

  private void runAll(Stubs<?> runnable) {
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Running AEM Stubs scripts under path '{}'", rootPath);

    resolverAccessor.consume(resolver -> {
      try {
        runAllUnderPath(rootPath, resolver, runnable);
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  private void runAllUnderPath(String rootPath, ResourceResolver resolver, Stubs<?> runnable) {
    final AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
      @Override
      protected void visit(Resource resource) {
        if (resource.isResourceType(JcrUtils.NT_FILE) && isScript(resource.getPath())) {
          runnable.runScript(resource);
        }
      }
    };
    visitor.accept(resolver.getResource(rootPath));
  }

  private Optional<Stubs<?>> findRunnable(String path) {
    return runnables.stream()
      .filter(runnable -> isScript(path, runnable) || isMapping(path, runnable))
      .findFirst();
  }

  private boolean isScript(String path, Stubs<?> runnable) {
    return wildcardMatch(path, format("%s/%s/**/*%s", getRootPath(), runnable.getId(), config.scriptExtension()));
  }

  private boolean isMapping(String path, Stubs<?> runnable) {
    return wildcardMatch(path, format("%s/%s/**/*%s", getRootPath(), runnable.getId(), config.mappingExtension()));
  }

  private boolean isScript(String path) {
    return isScriptExtension(path) && isNotExcludedPath(path);
  }

  private void mapAll(Stubs<?> runnable) {
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    resolverAccessor.consume(resolver -> {
      final AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
        @Override
        protected void visit(Resource resource) {
          if (resource.isResourceType(JcrUtils.NT_FILE) && isMapping(resource.getPath())) {
            runnable.loadMapping(resource);
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
    final List<String> pathsChanged = changes.stream()
      .map(ResourceChange::getPath)
      .collect(Collectors.toList());
    resetRunnables(pathsChanged);
  }

  private void resetRunnables(List<String> paths) {
    paths.stream()
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
  }

  @ObjectClassDefinition(name = "AEM Stubs Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Script Extension")
    String scriptExtension() default ".stub.groovy";

    @AttributeDefinition(name = "Mapping Extension")
    String mappingExtension() default ".stub.json";

    @AttributeDefinition(name = "Excluded Paths")
    String[] excluded_paths() default {"**/samples/*"};

    @AttributeDefinition(
      name = "Reset On Change",
      description = "Restart the server and apply all scripts and mappings if any of those are changed.")
    boolean resetOnChange() default true;
  }
}
