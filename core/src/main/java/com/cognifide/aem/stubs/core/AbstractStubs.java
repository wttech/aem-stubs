package com.cognifide.aem.stubs.core;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractStubs<T> implements Stubs<T>, EventHandler {

  @Override
  public void handleEvent(Event event) {
    reset();
  }
}
