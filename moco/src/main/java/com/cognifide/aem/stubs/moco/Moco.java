package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.RequestExtractor;

import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Moco {

  public static RequestExtractor<Object> dynamicVar(final Supplier<Object> textSupplier) {
    return new DynamicExtractor<>(checkNotNull(textSupplier, "Template variable supplier should not be null or empty"));
  }
}



