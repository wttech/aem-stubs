package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.RequestExtractor;

import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoUtils {

  private MocoUtils() {
    // intentionally empty
  }

  public static RequestExtractor<Object> suppliedVar(final Supplier<Object> textSupplier) {
    return new SupplierExtractor<>(checkNotNull(textSupplier, "Template variable supplier should not be null or empty"));
  }


}



