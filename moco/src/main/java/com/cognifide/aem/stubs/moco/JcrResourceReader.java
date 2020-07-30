package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;
import org.apache.sling.api.resource.ResourceResolver;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class JcrResourceReader implements ContentResourceReader {

  private final String jcrPath;
  private final ResourceResolver resourceResolver;

  public JcrResourceReader(ResourceResolver resourceResolver, String jcrPath) {
    this.resourceResolver = resourceResolver;
    this.jcrPath = jcrPath;
  }

  @Override
  public MediaType getContentType(HttpRequest request) {
    return MediaType.HTML_UTF_8;
  }

  @Override
  public MessageContent readFor(Request request) {
    MessageContent.Builder builder = content().withContent(resourceResolver.getResource(jcrPath).getName());

    return builder.build();
  }
}
