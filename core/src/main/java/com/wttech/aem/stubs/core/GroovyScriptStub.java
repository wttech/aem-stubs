package com.wttech.aem.stubs.core;

import com.google.gson.Gson;
import com.wttech.aem.stubs.core.script.Repository;
import com.wttech.aem.stubs.core.script.Template;
import com.wttech.aem.stubs.core.util.JcrUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
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

    private GroovyShell shell;

    public GroovyScriptStub(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return getPath();
    }

    public String getPath() {
        return resource.getPath();
    }

    public String getDirPath() {
        return StringUtils.substringBeforeLast(getPath(), "/");
    }

    public String resolvePath(String path) {
        if (path.startsWith("/")) {
            return path;
        }
        return getDirPath() + "/" + path;
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
            throw new StubRequestException(String.format("Stub script '%s' cannot match request properly", getId()), e);
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
            throw new StubResponseException(String.format("Stub script '%s' cannot respond properly", getId()), e);
        }
    }

    @Override
    public void fail(HttpServletRequest request, HttpServletResponse response, Exception exception) throws StubResponseException {
        try {
            LOG.info("Stub '{}' is handling failed request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
            invokeMethod("fail", new Object[]{request, response, exception});
            LOG.info("Stub '{}' handled failed request '{} {}'", getId(), request.getMethod(), request.getRequestURI());
        } catch (StubException e) {
            throw new StubResponseException(String.format("Stub script '%s' cannot handle failed request properly", getId()), e);
        }
    }

    private GroovyShell getOrCreateShell() {
        if (shell == null) {
            var binding = new Binding();
            binding.setVariable("resourceResolver", resource.getResourceResolver());
            binding.setVariable("log", LoggerFactory.getLogger(String.format("%s(%s)", getClass().getSimpleName(), getId())));
            binding.setVariable("gson", new Gson());
            var repository = new Repository(this, resource.getResourceResolver());
            binding.setVariable("repository", repository);
            binding.setVariable("template", new Template(repository));

            var compilerConfiguration = new CompilerConfiguration();
            ImportCustomizer importCustomizer = new ImportCustomizer();
            importCustomizer.addImport("StringUtils", "org.apache.commons.lang3.StringUtils");
            importCustomizer.addImport("HttpServletRequest", "javax.servlet.http.HttpServletRequest");
            importCustomizer.addImport("HttpServletResponse", "javax.servlet.http.HttpServletResponse");
            compilerConfiguration.addCompilationCustomizers(importCustomizer);

            shell = new GroovyShell(binding, compilerConfiguration);
        }
        return shell;
    }

    private Object invokeMethod(String name, Object[] args) throws StubException {
        try {
            var script = getOrCreateShell().parse(readSourceCode());
            return script.invokeMethod(name, args);
        } catch (CompilationFailedException e) {
            throw new StubException(String.format("Stub script '%s' cannot be compiled", getId()), e);
        } catch (MissingMethodException e) {
            throw new StubException(String.format("Stub script '%s' does not define method '%s'", getId(), name), e);
        } catch (Exception e) {
            throw new StubException(String.format("Stub script '%s' has a method '%s' that cannot be properly invoked (e.g. throws exception)", getId(), name), e);
        }
    }

    // TODO maybe cache it as bytearray to improve lookup performance
    private Reader readSourceCode() throws StubException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(InputStreamReader::new)
                .orElseThrow(() -> new StubException(String.format("Cannot read stub script '%s'!", getId())));
    }
}
