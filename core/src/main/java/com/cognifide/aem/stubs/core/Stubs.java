package com.cognifide.aem.stubs.core;

import org.apache.sling.api.resource.Resource;

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
   * Restart server then run all stub scripts.
   */
  void reload();

  /**
   * Run stub script.
   */
  void runScript(Resource resource);

  /**
   * Load stub mapping.
   */
  void loadMapping(Resource resource);
}
