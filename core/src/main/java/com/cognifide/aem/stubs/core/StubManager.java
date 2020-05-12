package com.cognifide.aem.stubs.core;

public interface StubManager {

  /**
   * Reload all runnables.
   */
  void reload();

  /**
   * Reload stubs runnable.
   * Restart server and apply all mappings then run all scripts.
   */
  void reload(Stubs<?> runnable);

  /**
   * Get root path where all scripts are located (for all types of runnables).
   */
  String getRootPath();
}
