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
    return MediaType.HTML_UTF_8;
  }

  @Override
  public MessageContent readFor(Request request) {
    AtomicReference<String> result = new AtomicReference<>("");
    resolverAccessor.consume(resourceResolver -> {
      Resource resource = resourceResolver.getResource(jcrPathResource.readFor(request).toString());
      if (resource != null) {
        InputStream inputStream = resource.adaptTo(InputStream.class);
        if (inputStream != null) {
          try {
            result.set(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
          } catch (IOException e) {
            LOG.warn("Could not read input stream", e);
          }
        }
      }
    });
    MessageContent.Builder builder = content().withContent(result.get());

    return builder.build();
  }
}
