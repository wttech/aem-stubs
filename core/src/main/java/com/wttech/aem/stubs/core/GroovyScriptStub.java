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

    @Override
    public boolean isRequested(HttpServletRequest request) throws StubException {
        return false; // TODO delegate to groovy script method 'isRequested'
    }

    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws StubException {
        // TODO delegate to groovy script method 'respond'
    }
}
