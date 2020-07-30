package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Identifiable;
import com.github.dreamhead.moco.resource.ResourceConfigApplier;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import org.apache.sling.api.resource.ResourceResolver;

import static com.github.dreamhead.moco.resource.IdFactory.id;

public class JcrResourceReaderFactory {

  private final ResolverAccessor resolverAccessor;

  public JcrResourceReaderFactory(ResolverAccessor resolverAccessor) {
    this.resolverAccessor = resolverAccessor;
  }

  public ResponseHandler jcr(String jcrPath) {
    return new JcrContentHandler(contentResource(id("jcr"), null, new JcrResourceReader(resolverAccessor, jcrPath)));
  }

  private static ContentResource contentResource(final Identifiable id, final ResourceConfigApplier applier,
                                                 final ContentResourceReader reader) {
    return new ContentResource(id, applier, reader);
  }

}
