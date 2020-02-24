package com.cognifide.wiremockonaem.aem.core.scripts;

import static com.cognifide.wiremockonaem.aem.core.Configuration.GROOVY_SCRIPT_LOCATION;
import static java.util.Collections.singletonMap;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;

import java.util.Spliterator;
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

@Component(service = ScriptResolver.class)
public class ScriptResolver {

  private static final String QUERY = String
    .format("SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])",
      GROOVY_SCRIPT_LOCATION);

  private static Logger LOG = LoggerFactory.getLogger(ScriptResolver.class);

  @Reference
  ResourceResolverFactory resourceResolverFactory;


  public Stream<Resource> getAllScripts() {
    return withResourceResolver(resolver ->StreamSupport.stream(findScripts(resolver), false)
    );
  }

  public <T> T withResourceResolver(Function<ResourceResolver, T> function) {
    try (ResourceResolver resolver = retrieveResourceResolver()) {
      return function.apply(resolver);
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

  private static Spliterator<Resource> findScripts(ResourceResolver resolver) {
    return queryForScripts(resolver).spliterator();
  }

  private static Iterable<Resource> queryForScripts(ResourceResolver resolver) {
    return () -> resolver.findResources(QUERY, Query.JCR_SQL2);
  }
}
