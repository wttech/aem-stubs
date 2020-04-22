package com.cognifide.aem.stubs.core.utils;

import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.jcr.query.Query;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ResolverAccessor.class)
public class ResolverAccessor {


  private static Logger LOG = LoggerFactory.getLogger(ResolverAccessor.class);

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  public <T> T resolve(Function<ResourceResolver, T> function) {
    try (ResourceResolver resolver = retrieveResourceResolver()) {
      return function.apply(resolver);
    } catch (LoginException e) {
      LOG.error("Cannot create resource resolver for mapper service.", e);
      throw new RuntimeException(
        "Cannot create resource resolver for mapper service. Is service user mapper configured?");
    }
  }

  public void consume(Consumer<ResourceResolver> consumer) {
    resolve(resolver -> {
      consumer.accept(resolver);
      return null;
    });
  }

  private ResourceResolver retrieveResourceResolver() throws LoginException {
    return resourceResolverFactory.getServiceResourceResolver(singletonMap(SUBSERVICE, "com.cognifide.aem.stubs"));
  }
}
