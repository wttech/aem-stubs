package com.wttech.aem.stubs.core;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;

public class StaticFileStub implements Stub {

    private final Resource resource;

    public StaticFileStub(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getPath();
    }

    /**
       TODO match part of path to the request url, match resource extension to the request accept header or requested
       TODO for example when requesting GET '/stubs/products/123' the resource '/conf/stubs/products/123.GET.json'
       TODO for example when requesting POST '/stubs/products/123' the resource '/conf/stubs/products/123.POST.json'
     */
    @Override
    public boolean request(HttpServletRequest request) throws StubRequestException {
        return false;
    }

    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws StubResponseException {
        var inputStream = resource.adaptTo(InputStream.class);
        if (inputStream == null) {
            throw new StubResponseException(String.format("Cannot read file stub resource '%s'", resource.getPath()));
        }

        try {
            response.setContentType("application/json; charset=utf-8"); // TODO tika.detect()
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            throw new StubResponseException(
                    String.format("Cannot write file stub response '%s'", resource.getPath()), e);
        }
    }

    @Override
    public void fail(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws StubResponseException {
        throw new StubResponseException(
                String.format("Stub '%s' is static and thus cannot handle failed requests!", getId()), e);
    }
}
