package com.cognifide.aem.stubs.core;

// import groovy.lang.Closure;

public interface Stubs<T> {

  void reload();

  void define(String id, Stub<T> definition);

//  default void define(String id, Closure definitionClosure) {
//    define(id, server -> {
//      definitionClosure.setDelegate(server);
//      definitionClosure.call();
//    });
//  }
}
