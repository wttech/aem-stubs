package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Identifiable;
import com.github.dreamhead.moco.resource.ResourceConfigApplier;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import org.apache.sling.api.resource.ResourceResolver;

import static com.github.dreamhead.moco.resource.IdFactory.id;

public class JcrResourceReaderFactory {

  private final ResourceResolver resourceResolver;

  public JcrResourceReaderFactory(ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
  }

  public ResponseHandler jcr(String jcrPath) {
    return new JcrContentHandler(contentResource(id("jcr"), null, new JcrResourceReader(resourceResolver, jcrPath)));
  }

  private static ContentResource contentResource(final Identifiable id, final ResourceConfigApplier applier,
                                                 final ContentResourceReader reader) {
    return new ContentResource(id, applier, reader);
  }

}
