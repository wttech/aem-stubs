package com.wttech.aem.stubs.core;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

@Component(service = StubRepository.class, immediate = true)
public class StubRepository {

    private static final Logger LOG = LoggerFactory.getLogger(StubRepository.class);

    @Reference
    private ResourceResolverFactory resolverFactory;


    public Stream<Stub> findStubs() throws StubException {
        try (var resolver = resolverFactory.getServiceResourceResolver(null)) {


            return Stream.empty();
        } catch (LoginException e) {
            throw new StubException("Cannot access repository while finding stubs", e);
        }
    }

}
