package com.cognifide.wiremockonaem.aem.core;

import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ResourceResolverGentleman.class)
public class ResourceResolverGentleman {

  private static Logger LOG = LoggerFactory.getLogger(ResourceResolverGentleman.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;


  public void withResolver(Consumer<ResourceResolver> consumer) {
    try (ResourceResolver resolver = retrieveResourceResolver()) {
      consumer.accept(resolver);
    } catch (LoginException e) {
      LOG.error("Cannot create resource resolver for mapper service.", e);
      throw new RuntimeException(
        "Cannot create resource resolver for mapper service. Is service user mapper configured?");
    }
  }

  private ResourceResolver retrieveResourceResolver() throws LoginException {
    return resourceResolverFactory
      .getServiceResourceResolver(
        singletonMap(SUBSERVICE, "com.cognifide.wiremock.aem.core"));
  }
}
