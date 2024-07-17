package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.ResourceTreeSpliterator;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component(service = StubRepository.class, immediate = true)
@Designate(ocd = StubRepository.Config.class)
public class StubRepository {

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
            result = Stream.concat(result, StreamSupport.stream(new ResourceTreeSpliterator(root), false).map(this::fromResource));
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
