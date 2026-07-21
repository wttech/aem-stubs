package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.RequestExtractor;

import java.util.Objects;
import java.util.function.Supplier;

public final class MocoUtils {

  private MocoUtils() {
    // intentionally empty
  }

  public static RequestExtractor<Object> suppliedVar(final Supplier<Object> textSupplier) {
    return new SupplierExtractor<>(Objects.requireNonNull(textSupplier, "Template variable supplier should not be null or empty"));
  }


}



