package com.cognifide.aem.stubs.core;

import com.cognifide.aem.stubs.core.groovy.StubScript;

public interface Stubs<T> {

  T getServer();

  void clear();

  void reset();

  void prepare(StubScript script);
}
