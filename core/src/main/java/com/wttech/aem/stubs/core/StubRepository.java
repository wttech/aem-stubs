package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.StreamUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Stream;

@Component(service = StubRepository.class, immediate = true)
public class StubRepository {

    private static final Logger LOG = LoggerFactory.getLogger(StubRepository.class);

    // TODO complete setup: https://medium.com/@toimrank/aem-service-user-mapping-and-resourceresolver-bd4a15d8cff2
    private static final String RESOLVER_SUBSERVICE = "stubs";


    @Reference
    private ResourceResolverFactory resolverFactory;

    private Config config;

    @ObjectClassDefinition(name = "AEM Stubs - Repository")
    public @interface Config {

        @AttributeDefinition(name = "Search paths")
        String[] searchPaths() default { "/conf/stubs" };
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        this.config = config;
    }

    public Stream<Stub> findStubs(ResourceResolver resolver) throws StubException {
        Stream<Stub> result = Stream.empty();
        for (var path : config.searchPaths()) {
            var root = resolver.getResource(path);
            if (root == null) {
                throw new StubException(String.format("Cannot read stubs search path '%s'!", path));
            }
            result = Stream.concat(result, StreamUtils.asStream(root.listChildren()).map(this::fromResource));
        }
        return result;
    }

    private Stub fromResource(Resource resource) {
        if (resource.getName().endsWith(".groovy")) {
            return new GroovyScriptStub(resource);
        } else {
            return new StaticFileStub(resource);
        }
    }

    public ResourceResolver createResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(Map.of(ResourceResolverFactory.SUBSERVICE, RESOLVER_SUBSERVICE));
    }

}
