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
   * Initialize server to be ready for loading mappings and running scripts.
   */
  void initServer();

  /**
   * Start initialized server after loading mappings and running scripts.
   */
  void startServer();

  /**
   * Run stub script.
   */
  void runScript(Resource resource);

  /**
   * Load stub mapping.
   */
  void loadMapping(Resource resource);
}
