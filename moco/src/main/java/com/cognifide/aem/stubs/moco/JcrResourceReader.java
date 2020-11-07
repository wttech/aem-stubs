package com.cognifide.aem.stubs.moco;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.reader.ContentResourceReader;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.dreamhead.moco.model.MessageContent.content;

public class JcrResourceReader implements ContentResourceReader {

  private static final Logger LOG = LoggerFactory.getLogger(JcrResourceReader.class);

  private final ResolverAccessor resolverAccessor;
  private final ContentResource jcrPathResource;

  public JcrResourceReader(ResolverAccessor resolverAccessor, ContentResource jcrPathResource) {
    this.resolverAccessor = resolverAccessor;
    this.jcrPathResource = jcrPathResource;
  }

  @Override
  public MediaType getContentType(HttpRequest request) {
    return MediaType.PLAIN_TEXT_UTF_8;
  }

  public MessageContent readFor(Request request) {
    return resolverAccessor.resolve(resourceResolver ->
      Optional.ofNullable(jcrPathResource.readFor(request))
        .map(MessageContent::toString)
        .map(resourceResolver::getResource)
        .map(resource -> resource.adaptTo(InputStream.class))
        .map(is -> content()
          .withContent(toString(is))
          .build())
        .orElse(content().build())
    );
  }

  private String toString(InputStream is) {
    try {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOG.warn("Could not read input stream", e);
      return "";
    }
  }
}
