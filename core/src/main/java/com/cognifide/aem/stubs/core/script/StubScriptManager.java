package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;

import java.util.Optional;

public interface StubScriptManager {

  /**
   * Run script at any path.
   */
  void run(String path);

  /**
   * Runs all scripts handled by all runnables available.
   */
  void runAll();

  /**
   * Runs all stub scripts which:
   * - are located under configured root path,
   * - are having correct file extension
   * - are not matching exclusion path patterns.
   */
  void runAll(Stubs<?> runnable);

  /**
   * Check script if it is a candidate for running.
   */
  boolean isRunnable(String path);

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
  String getExtension();
}
