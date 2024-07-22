package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.JcrUtils;
import com.wttech.aem.stubs.core.util.ResourceSpliterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = StubRepository.class, immediate = true)
@Designate(ocd = StubRepository.Config.class)
public class StubRepository {

    private static final String RESOLVER_SUBSERVICE = "stubs";

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Config config;

    @ObjectClassDefinition(name = "AEM Stubs - Repository")
    public @interface Config {

        @AttributeDefinition(name = "Search paths", description = "JCR repository paths to search for stub resources.")
        String[] searchPaths() default {"/conf/stubs"};

        @AttributeDefinition(
                name = "Classifier",
                description = "Resource name part used to distinguish stubs from other files.")
        String classifier() default "stub";
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
            Stream<Resource> stream = ResourceSpliterator.stream(root, this::isStub);
            result = Stream.concat(result, stream.map(this::fromResource));
        }
        return result;
    }

    private boolean isStub(Resource resource) {
        return resource.isResourceType(JcrUtils.NT_FILE) && resource.getName().contains(config.classifier());
    }

    private Stub fromResource(Resource resource) {
        if (resource.getName().endsWith(".groovy")) {
            return new GroovyScriptStub(resource);
        } else {
            return new StaticFileStub(resource);
        }
    }

    public ResourceResolver createResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, RESOLVER_SUBSERVICE));
    }

    public Optional<Resource> findResource(ResourceResolver resolver, String subPath) {
        for (var path : config.searchPaths()) {
            var result = resolver.getResource(String.format("%s/%s", path, subPath));
            if (result != null) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    public Optional<Stub> findStub(ResourceResolver resolver, String subPath) {
        return findResource(resolver, subPath).filter(this::isStub).map(this::fromResource);
    }

    public Optional<Stub> findSpecialStub(ResourceResolver resolver, String subPath) {
        return findResource(resolver, subPath).map(this::fromResource);
    }
}
