package com.cognifide.aem.stubs.core.script;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

import com.cognifide.aem.stubs.core.Stubs;
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
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component(
  service = {StubScriptManager.class, ResourceChangeListener.class},
  immediate = true,
  property = {
    ResourceChangeListener.CHANGES + "=" + "REMOVED",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED"
  }
)
@Designate(ocd = ConfigurableStubScriptManager.Config.class)
public class ConfigurableStubScriptManager implements StubScriptManager, ResourceChangeListener {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableStubScriptManager.class);

  private static final String NODE_TYPE = "nt:file";

  private static final String ON_CHANGE_RESET_ALL = "reset_all";

  private static final String ON_CHANGE_RUN_CHANGED = "run_changed";

  private static final String ON_CHANGE_NOTHING = "nothing";

  @Reference
  private ResolverAccessor resolverAccessor;

  private Config config;

  private final List<Stubs<?>> runnables = Lists.newCopyOnWriteArrayList();

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void run(String path) {
    Stubs<?> runnable = findRunnable(path).orElse(null); // TODO someday Java 9 'ifPresentOrElse()'
    if (runnable == null) {
      LOG.error("Cannot run AEM Stubs script '{}' - runnable not found!", path);
    } else {
      try {
        resolverAccessor.consume(resolver -> run(path, runnable, resolver));
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs script '{}'! Cause: {}", path, e.getMessage(), e);
      }
    }
  }

  private void run(String path, Stubs<?> runnable, ResourceResolver resolver) {
    final StubScript script = new StubScript(path, this, runnable, resolver);
    LOG.debug("Running AEM Stubs script started '{}'", script.getPath());
    runnable.prepare(script);
    script.run();
    LOG.debug("Running AEM Stubs script finished '{}'", script.getPath());
  }

  @Override
  public void runAll() {
    for (Stubs<?> runnable : runnables) {
      runAll(runnable);
    }
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void runAll(Stubs<?> runnable) {
    final StubScriptRun result = new StubScriptRun();
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Running AEM Stubs scripts under path '{}'", rootPath);

    resolverAccessor.consume(resolver -> {
      try {
        runAllUnderPath(rootPath, runnable, resolver, result);
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });

    LOG.info("Running AEM Stubs scripts result: {}", result);
  }

  private void runAllUnderPath(String rootPath, Stubs<?> runnable, ResourceResolver resolver, StubScriptRun result) {
    final AbstractResourceVisitor visitor = new AbstractResourceVisitor() {
      @Override
      protected void visit(Resource resource) {
        if (resource.isResourceType(NODE_TYPE) && isRunnable(resource.getPath())) {
          runAllEachPath(resource.getPath(), runnable, resolver, result);
        }
      }
    };
    visitor.accept(resolver.getResource(rootPath));
  }

  private void runAllEachPath(String scriptPath, Stubs<?> runnable, ResourceResolver resolver, StubScriptRun result) {
    try {
      result.total++;
      run(scriptPath, runnable, resolver);
    } catch (Exception e) {
      LOG.error("Cannot run AEM Stubs script! Cause: {}", e.getMessage(), e);
      result.failed++;
    }
  }

  @Override
  public Optional<Stubs<?>> findRunnable(String path) {
    return runnables.stream()
      .filter(runnable -> isScript(path, runnable) || isMapping(path, runnable))
      .findFirst();
  }

  private boolean isScript(String path, Stubs<?> runnable) {
    return wildcardMatch(path,
      format("%s/%s/**/*%s", getRootPath(), runnable.getId(), getExtension()));
  }

  private boolean isMapping(String path, Stubs<?> runnable) {
    return wildcardMatch(path,
      format("%s/%s/**/*%s", getRootPath(), runnable.getId(), config.mappingsExtension()));
  }

  @Override
  public boolean isRunnable(String path) {
    return isExtensionCorrect(path) && isNotExcludedPath(path);
  }

  private boolean isNotExcludedPath(String path) {
    return Arrays.stream(config.excluded_paths()).noneMatch(p -> wildcardMatch(path, p));
  }

  private boolean isExtensionCorrect(String path) {
    return path.endsWith(config.scriptExtension());
  }

  private boolean isMapping(String path) {
    return runnables.stream().anyMatch(r -> isMapping(path, r));
  }

  @Override
  public String getRootPath() {
    return config.resource_paths();
  }

  @Override
  public String getExtension() {
    return config.scriptExtension();
  }

  @Override
  public void onChange(List<ResourceChange> changes) {
    final List<String> mappingPaths = getMappingPaths(changes);

    if (!mappingPaths.isEmpty()) {
      resetAll(mappingPaths);
      return;
    }

    final List<String> scriptPaths = getScriptPaths(changes);

    if (!scriptPaths.isEmpty()) {
      if (ON_CHANGE_RUN_CHANGED.equalsIgnoreCase(config.on_change())) {
        scriptPaths.forEach(this::run);
      } else if (ON_CHANGE_RESET_ALL.equalsIgnoreCase(config.on_change())) {
        resetAll(scriptPaths);
      }
    }
  }

  private void resetAll(List<String> paths) {
    paths.stream().map(this::findRunnable)
      .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
      .distinct()
      .forEach(Stubs::reset);
  }

  private List<String> getScriptPaths(List<ResourceChange> changes) {
    return changes.stream()
      .map(ResourceChange::getPath)
      .filter(this::isRunnable)
      .collect(Collectors.toList());
  }

  private List<String> getMappingPaths(List<ResourceChange> changes) {
    return changes.stream()
      .map(ResourceChange::getPath)
      .filter(this::isMapping)
      .collect(Collectors.toList());
  }

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

  @ObjectClassDefinition(name = "AEM Stubs Scripts Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Script Extension")
    String scriptExtension() default ".groovy";

    @AttributeDefinition(name = "Mappings Extension")
    String mappingsExtension() default ".stub.json";

    @AttributeDefinition(name = "Excluded Paths")
    String[] excluded_paths() default {"**/samples/*"};

    @AttributeDefinition(
      name = "On change",
      options = {
        @Option(label = "Restart server and run all scripts", value = ON_CHANGE_RESET_ALL),
        @Option(label = "Run changed script only", value = ON_CHANGE_RUN_CHANGED),
        @Option(label = "Do nothing", value = ON_CHANGE_NOTHING)
      }
    )
    String on_change() default ON_CHANGE_RESET_ALL;
  }
}
