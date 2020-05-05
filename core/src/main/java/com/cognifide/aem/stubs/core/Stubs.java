package com.cognifide.aem.stubs.core;

import com.cognifide.aem.stubs.core.script.StubScript;

/**
 * Defines stub engine.
 */
public interface Stubs<T> {

  /**
   * Unique ID being a repository folder name too.
   */
  String getId();

  /**
   * Specific server implementation.
   */
  T getServer();

  /**
   * Restart server only.
   */
  void clear();

  /**
   * Restart server then run all stub scripts.
   */
  void reset();

  /**
   * Prepare stub script before execution.
   */
  void prepare(StubScript script);
}
