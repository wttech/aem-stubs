package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.of;

public final class SupplierExtractor<T> implements RequestExtractor<T> {

  private final Supplier<T> objectSupplier;

  public SupplierExtractor(final Supplier<T> objectSupplier) {
    this.objectSupplier = objectSupplier;
  }

  @Override
  public Optional<T> extract(final Request request) {
    return of(objectSupplier.get());
  }
}
