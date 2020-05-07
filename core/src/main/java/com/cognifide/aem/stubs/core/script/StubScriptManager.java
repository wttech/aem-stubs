package com.cognifide.aem.stubs.core.script;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.wildcardMatch;

import com.cognifide.aem.stubs.core.Stubs;
import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.cognifide.aem.stubs.core.util.StreamUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import javax.jcr.query.Query;
import java.text.NumberFormat;
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
@Designate(ocd = StubScriptManager.Config.class)
public class StubScriptManager implements ResourceChangeListener {

  private static final Logger LOG = LoggerFactory.getLogger(StubScriptManager.class);

  private static final String QUERY = "SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])";

  private static final String ON_CHANGE_RESET_ALL = "reset_all";

  private static final String ON_CHANGE_RUN_CHANGED = "run_changed";

  private static final String ON_CHANGE_NOTHING = "nothing";

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
      resolverAccessor.consume(resolver -> execute(resolver, path));
    } catch (Exception e) {
      LOG.error("Cannot run AEM Stub script '{}'! Cause: {}", path, e.getMessage(), e);
    }
  }

  /**
   * Runs all stub scripts handled by all runnables available.
   */
  public void runAll() {
    for (Stubs<?> runnable : runnables) {
      runAll(runnable);
    }
  }

  /**
   * Runs all stub scripts which:
   * - are located under configured root path,
   * - are having correct file extension
   * - are not matching exclusion path patterns.
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void runAll(Stubs<?> runnable) {
    final String rootPath = format("%s/%s", getRootPath(), runnable.getId());

    LOG.info("Running AEM Stubs scripts under path '{}'", rootPath);
    resolverAccessor.consume(resolver -> {
      try {
        final RunResult result = new RunResult();
        StreamUtils.from(resolver.findResources(format(QUERY, rootPath), Query.JCR_SQL2))
          .filter(r -> isRunnable(r.getPath()))
          .map(Resource::getPath)
          .forEach(path -> {
            try {
              result.total++;
              execute(resolver, path);
            } catch (Exception e) {
              LOG.error("Cannot run AEM Stubs script! Cause: {}", e.getMessage(), e);
              result.failed++;
            }
          });
        LOG.info("Running AEM Stubs scripts ended - success ratio ({}/{}={})", result.getSucceed(), result.getTotal(),
          result.getSucceedPercent());
      } catch (Exception e) {
        LOG.error("Cannot run AEM Stubs scripts! Cause: {}", e.getMessage(), e);
      }
    });
  }

  public boolean isRunnable(String path) {
    return isExtensionCorrect(path) && isNotExcludedPath(path);
  }

  private boolean isNotExcludedPath(String path) {
    return Arrays.stream(config.excluded_paths()).noneMatch(p -> wildcardMatch(path, p));
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
      if (ON_CHANGE_RUN_CHANGED.equalsIgnoreCase(config.on_change())) {
        scriptChanges.forEach(c -> run(c.getPath()));
      } else if (ON_CHANGE_RESET_ALL.equalsIgnoreCase(config.on_change())) {
        scriptChanges.stream()
          .map(c -> findRunnable(c.getPath()))
          .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
          .distinct()
          .forEach(Stubs::reset);
      }
    }
  }

  private Object execute(ResourceResolver resolver, String path) {
    Stubs<?> runnable = findRunnable(path).orElse(null);
    if (runnable == null) {
      LOG.error("Executing Stub Script '{}' not possible - runnable not found.", path);
      return null;
    }

    final StubScript script = new StubScript(path, this, runnable, resolver);
    LOG.debug("Executing Stub Script '{}'", script.getPath());
    script.getBinding().setVariable("stubs", runnable);
    runnable.prepare(script);
    final Object result = script.run();
    LOG.debug("Executed Stub Script '{}'", script.getPath());
    return result;
  }

  public Optional<Stubs<?>> findRunnable(String path) {
    return runnables.stream()
      .filter(runnable -> wildcardMatch(path, format("%s/%s/**/*%s", getRootPath(), runnable.getId(), getExtension())))
      .findFirst();
  }

  public String getRootPath() {
    return config.resource_paths();
  }

  public String getExtension() {
    return config.extension();
  }

  private static class RunResult {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance(Locale.US);

    private int total = 0;

    private int failed = 0;

    public int getTotal() {
      return total;
    }

    public int getFailed() {
      return failed;
    }

    public int getSucceed() {
      return total - failed;
    }

    public String getSucceedPercent() {
      return PERCENT_FORMAT.format((double) (total - failed) / ((double) total));
    }
  }

  @ObjectClassDefinition(name = "AEM Stubs Scripts Manager")
  public @interface Config {

    @AttributeDefinition(name = "Root Path")
    String resource_paths() default "/var/stubs";

    @AttributeDefinition(name = "Extension")
    String extension() default ".groovy";

    @AttributeDefinition(name = "Excluded Paths")
    String[] excluded_paths() default {"**/internals/*"};

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
