package com.cognifide.aem.stubs.core.script;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;
import static org.apache.sling.query.SlingQuery.$;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.query.api.SearchStrategy;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.google.common.collect.Lists;

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
        resolverAccessor.consume(resolver -> runAllEach(path, runnable, resolver));
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs script '{}'! Cause: {}", path, e.getMessage(), e);
      }
    }
  }

  private void runAllEach(String path, Stubs<?> runnable, ResourceResolver resolver) {
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
    final RunAllResult result = new RunAllResult();
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Running AEM Stubs scripts under path '{}'", rootPath);

    resolverAccessor.consume(resolver -> {
      try {
        $(resolver.getResource(rootPath))
          .searchStrategy(SearchStrategy.BFS)
          .find(NODE_TYPE)
          .filter(r -> isRunnable(r.getPath()))
          .forEach(resource -> runAllEach(resource, result, runnable, resolver));
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });

    LOG.info("Running AEM Stubs scripts result: {}", result);
  }

  private void runAllEach(Resource resource, RunAllResult result, Stubs<?> runnable,
    ResourceResolver resolver) {
    try {
      result.total++;
      runAllEach(resource.getPath(), runnable, resolver);
    } catch (Exception e) {
      LOG.error("Cannot run AEM Stubs script! Cause: {}", e.getMessage(), e);
      result.failed++;
    }
  }

  @Override
  public Optional<Stubs<?>> findRunnable(String path) {
    return runnables.stream()
      .filter(runnable -> isStubScript(path, runnable) || isMappingFile(path, runnable))
      .findFirst();
  }

  private boolean isStubScript(String path, Stubs<?> runnable) {
    return wildcardMatch(path,
      format("%s/%s/**/*%s", getRootPath(), runnable.getId(), getExtension()));
  }

  private boolean isMappingFile(String path, Stubs<?> runnable) {
    return wildcardMatch(path,
      format("%s/%s/mappings/*%s", getRootPath(), runnable.getId(), "json"));
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

  private boolean isMappingFile(String path) {
    return runnables.stream().anyMatch(r -> path.contains(String.format("%s/mappings", r.getId())));
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
      .filter(this::isMappingFile)
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

  private static class RunAllResult {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance(Locale.US);

    private long startedAt = System.currentTimeMillis();

    private int total = 0;

    private int failed = 0;

    public int succeed() {
      return total - failed;
    }

    public String succeedPercent() {
      return PERCENT_FORMAT.format((double) (total - failed) / ((double) total));
    }

    public String duration() {
      return DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startedAt);
    }

    @Override
    public String toString() {
      return String
        .format("Success ratio: %s/%s=%s | Duration: %s", succeed(), total, succeedPercent(),
          duration());
    }
  }

  @ObjectClassDefinition(name = "AEM Stubs Scripts Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Script Extension")
    String scriptExtension() default ".groovy";

    @AttributeDefinition(name = "Mappings Extension")
    String mappingsExtension() default ".json";

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
