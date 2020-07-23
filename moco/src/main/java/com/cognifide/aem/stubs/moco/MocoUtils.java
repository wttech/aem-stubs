package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Identifiable;
import com.github.dreamhead.moco.resource.ResourceConfigApplier;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.github.dreamhead.moco.resource.reader.FileResourceReader;

import java.util.function.Supplier;

import static com.github.dreamhead.moco.resource.IdFactory.id;
import static com.github.dreamhead.moco.resource.ResourceConfigApplierFactory.fileConfigApplier;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MocoUtils {

  private MocoUtils() {
    // intentionally empty
  }

  public static RequestExtractor<Object> suppliedVar(final Supplier<Object> textSupplier) {
    return new SupplierExtractor<>(checkNotNull(textSupplier, "Template variable supplier should not be null or empty"));
  }

  public static ResponseHandler jcr(String jcrPath) {
    return new JcrContentHandler(contentResource(id("jcr"), null, new JcrResourceReader(jcrPath)));
  }

  private static ContentResource contentResource(final Identifiable id, final ResourceConfigApplier applier,
                                                 final ContentResourceReader reader) {
    return new ContentResource(id, applier, reader);
  }

}



