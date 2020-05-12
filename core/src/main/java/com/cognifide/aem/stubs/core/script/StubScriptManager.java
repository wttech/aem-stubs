package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;
import org.apache.sling.api.resource.Resource;

import java.util.Optional;
import java.util.function.Consumer;

public interface StubScriptManager {

  /**
   * Runs all scripts handled by all runnables available.
   */
  void runAll(Consumer<Resource> scriptRunner);

  /**
   * Runs all stub scripts which:
   * - are located under configured root path,
   * - are having correct file extension
   * - are not matching exclusion path patterns.
   */
  void runAll(Stubs<?> runnable, Consumer<Resource> scriptRunner);

  /**
   * Check script if it is a candidate for running.
   */
  boolean isScript(String path);

  /**
   * Allows to load all mappings available for
   */
  void mapAll(Stubs<?> runnable, Consumer<Resource> mappingLoader);

  /**
   * Check mapping if it is a candidate for application.
   */
  boolean isMapping(String path);

  /**
   * Find script runnable applicable for script.
   */
  Optional<Stubs<?>> findRunnable(String path);

  /**
   * Get root path where all scripts are located (for all types of runnables).
   */
  String getRootPath();

  /**
   * Get considered script extension.
   */
  String getScriptExtension();

  /**
   * Get considered mapping extension.
   */
  String getMappingExtension();
}
