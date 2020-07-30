package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;

import java.util.concurrent.atomic.AtomicReference;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class JcrResourceReader implements ContentResourceReader {

  private final String jcrPath;
  private final ResolverAccessor resolverAccessor;

  public JcrResourceReader(ResolverAccessor resolverAccessor, String jcrPath) {
    this.resolverAccessor = resolverAccessor;
    this.jcrPath = jcrPath;
  }

  @Override
  public MediaType getContentType(HttpRequest request) {
    return MediaType.HTML_UTF_8;
  }

  @Override
  public MessageContent readFor(Request request) {
    AtomicReference<String> result = new AtomicReference<>("");
    resolverAccessor.consume(resourceResolver -> result.set(resourceResolver.getResource(jcrPath).getName()));
    MessageContent.Builder builder = content().withContent(result.get());

    return builder.build();
  }
}
