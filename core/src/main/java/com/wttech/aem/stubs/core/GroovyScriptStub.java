package com.wttech.aem.stubs.core;

import org.apache.sling.api.resource.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GroovyScriptStub implements Stub {

    private final Resource resource;

    public GroovyScriptStub(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getPath();
    }

    // TODO provide utility methods to map '/stubs/products/123' to '/conf/stubs/products/123.GET.groovy'
    @Override
    public boolean isRequested(HttpServletRequest request) throws StubException {
        // TODO delegate to groovy script method 'isRequested';
        // TODO script could look like: $.match(request).method('GET').path('/stubs/products/{id}')
        return false;
    }

    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws StubException {
        // TODO delegate to groovy script method 'respond'
        // TODO var id = $.path('/stubs/products/{id}').get('id');
        // TODO script could look like: $.respond(request, response).method('GET').path('/stubs/products/{id}').body('{"id": 123, "name": "Product"}')
    }
}
