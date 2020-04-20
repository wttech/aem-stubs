package com.cognifide.aem.stubs.core;

public interface Stubs<T> {

  void reload();

  void define(String id, Stub<T> definition);
}
