package com.wttech.aem.stubs.core;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public class StaticFileStub implements Stub {

    private final Resource resource;

    public StaticFileStub(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getPath();
    }

    // TODO match part of path to the request url, match resource extension to the request accept header or requested extension
    // TODO for example when requesting GET '/stubs/products/123' the resource '/conf/stubs/products/123.GET.json' should be responded
    // TODO for example when requesting POST '/stubs/products/123' the resource '/conf/stubs/products/123.POST.json' should be responded
    @Override
    public boolean isRequested(HttpServletRequest request) throws StubException {
        return false;
    }

    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws StubException {
        var inputStream = resource.adaptTo(InputStream.class);
        if (inputStream == null) {
            throw new StubException(String.format("Cannot read file stub resource '%s'", resource.getPath()));
        }

        try {
            response.setContentType("application/json; charset=utf-8"); // TODO tika.detect()
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            throw new StubException(String.format("Cannot write file stub response '%s'", resource.getPath()), e);
        }
    }
}
