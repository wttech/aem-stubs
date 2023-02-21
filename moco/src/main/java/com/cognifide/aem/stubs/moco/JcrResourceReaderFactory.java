package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.ContentHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Identifiable;
import com.github.dreamhead.moco.resource.ResourceConfigApplier;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;

import static com.github.dreamhead.moco.resource.IdFactory.id;
import static com.github.dreamhead.moco.resource.ResourceFactory.textResource;
import static com.google.common.base.Preconditions.checkNotNull;

public class JcrResourceReaderFactory {

  private final ResolverAccessor resolverAccessor;

  public JcrResourceReaderFactory(ResolverAccessor resolverAccessor) {
    this.resolverAccessor = resolverAccessor;
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static ContentResource contentResource(final Identifiable id, final ResourceConfigApplier applier,
                                                 final ContentResourceReader reader) {
    return new ContentResource(id, applier, reader);
  }

  public ResponseHandler jcr(String jcrPath) {
    return new ContentHandler(contentResource(id("jcr"), null, new JcrResourceReader(resolverAccessor, textResource(checkNotNull(jcrPath, "Text should not be null")))));
  }

  public ResponseHandler jcr(ContentResource jcrResource) {
    return new ContentHandler(contentResource(id("jcr"), null, new JcrResourceReader(resolverAccessor, jcrResource)));
  }

}
