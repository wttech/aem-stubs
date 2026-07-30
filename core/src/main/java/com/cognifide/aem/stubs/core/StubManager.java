package com.cognifide.aem.stubs.core;

import java.util.Optional;

public interface StubManager {

  /**
   * Register a stubs runnable. Called by each Stubs implementation from its own
   * activate method.
   */
  void register(Stubs<?> runnable);

  /**
   * Unregister a stubs runnable, counterpart of {@link #register(Stubs)}.
   */
  void unregister(Stubs<?> runnable);

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
   * Find runnable by path of stub script or mapping.
   */
  Optional<Stubs<?>> findRunnable(String path);

  /**
   * Get root path where all scripts are located (for all types of runnables).
   */
  String getRootPath();
}
