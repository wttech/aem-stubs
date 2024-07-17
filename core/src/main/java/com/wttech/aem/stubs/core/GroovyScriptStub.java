package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.JcrUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.Optional;

public class GroovyScriptStub implements Stub {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyScriptStub.class);

    private final Resource resource;

    private final Binding binding = new Binding();

    private final CompilerConfiguration compilerConfig = new CompilerConfiguration();

    private final GroovyShell shell = new GroovyShell(binding, compilerConfig);


    public GroovyScriptStub(Resource resource) {
        this.resource = resource;

        binding.setVariable("resourceResolver", resource.getResourceResolver());
        binding.setVariable("log", LoggerFactory.getLogger(String.format("%s(%s)", getClass().getSimpleName(), getPath())));
    }

    @Override
    public String getId() {
        return resource.getPath();
    }

    public String getPath() {
        return resource.getPath();
    }

    // TODO provide utility methods to map '/stubs/products/123' to '/conf/stubs/products/123.GET.groovy'
    @Override
    public boolean request(HttpServletRequest request) throws StubRequestException {
        try {
            LOG.info("Stub '{}' is matching request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
            var result = (Boolean) invokeMethod("request", new Object[]{request});
            if (result) {
                LOG.info("Stub '{}' matched request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
            } else {
                LOG.info("Stub '{}' did not match request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
            }
            return result;
        } catch (StubException e) {
            throw new StubRequestException(String.format("Cannot invoke 'request' method of stub script '%s'", getPath()), e);
        }
    }

    // TODO var id = $.path('/stubs/products/{id}').get('id');
    // TODO script could look like: $.respond(request, response).method('GET').path('/stubs/products/{id}').body('{"id": 123, "name": "Product"}')
    @Override
    public void respond(HttpServletRequest request, HttpServletResponse response) throws StubResponseException {
        try {
            LOG.info("Stub '{}' is responding to request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
            invokeMethod("respond", new Object[]{request, response});
            LOG.info("Stub '{}' responded to request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
        } catch (StubException e) {
            throw new StubResponseException(String.format("Cannot invoke 'response' method of stub script '%s'", getPath()), e);
        }
    }

    private Object invokeMethod(String name, Object[] args) throws StubException {
        try {
            GroovyShell shell = new GroovyShell();
            Script script = shell.parse(readSourceCode());
            return script.invokeMethod(name, args);
        } catch (Exception e) {
            throw new StubResponseException(String.format("Cannot invoke method '%s' of script '%s'", name, getPath()), e);
        }
    }

    // TODO maybe cache it as bytearray to improve lookup performance
    private Reader readSourceCode() throws StubException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(InputStreamReader::new)
                .orElseThrow(() -> new StubException(String.format("Cannot read stub script '%s'!", getPath())));
    }
}
