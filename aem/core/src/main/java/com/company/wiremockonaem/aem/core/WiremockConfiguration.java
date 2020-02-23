package com.company.wiremockonaem.aem.core;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import javax.jcr.query.Query;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
  service = WiremockConfiguration.class
)
public class WiremockConfiguration {
  static final String URL_PREFIX = "/wiremock";
  public static final String GROOVY_SCRIPT_LOCATION = "/var/groovyconsole/scripts/stub";
  private static final String QUERY = String.format("SELECT script.* FROM [nt:file] AS script WHERE ISDESCENDANTNODE(script, [%s])", GROOVY_SCRIPT_LOCATION);

  @Reference
  private ResourceResolverGentleman resourceResolverGentleman;

  @Activate
  public void start() {
  }

  public String getUrlPrefix(){
    return URL_PREFIX;
  }

  public List<String> getAllScript(){
    return resourceResolverGentleman.withResolver(resourceResolver ->
      StreamSupport.stream(findScripts(resourceResolver), false)
        .map(Resource::getPath)
        .collect(toList()));
  }

  private static Spliterator<Resource> findScripts(ResourceResolver resolver){
    return queryForScripts(resolver).spliterator();
  }
  private static Iterable<Resource> queryForScripts(ResourceResolver resolver) {
    return () -> resolver.findResources(QUERY, Query.JCR_SQL2);
  }

}
