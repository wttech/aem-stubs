package com.wttech.aem.stubs.core.script;

import com.wttech.aem.stubs.core.GroovyScriptStub;
import com.wttech.aem.stubs.core.util.JcrUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.tika.Tika;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class Repository {

    private final GroovyScriptStub script;

    private final ResourceResolver resourceResolver;

    public Repository(GroovyScriptStub script, ResourceResolver resourceResolver) {
        this.script = script;
        this.resourceResolver = resourceResolver;
    }

    public Resource getResource(String path) {
        var pathResolved = script.resolvePath(path);
        var resource = resourceResolver.getResource(pathResolved);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Resource at path '%s' does not exist!", pathResolved));
        }
        return resource;
    }

    public String readAsString(String path) throws IOException {
        return readAsString(getResource(path));
    }

    public String readAsString(Resource resource) throws IOException {
        return IOUtils.toString(readAsStream(resource), StandardCharsets.UTF_8);
    }

    public String readAsBase64(String path) throws IOException {
        return readAsBase64(getResource(path));
    }

    public String readAsBase64(Resource resource) throws IOException {
        var bytes = IOUtils.toByteArray(readAsStream(resource));
        return new String(Base64.getEncoder().encode(bytes));
    }

    public InputStream readAsStream(String path) throws IOException {
        return readAsStream(getResource(path));
    }

    public InputStream readAsStream(Resource resource) throws IOException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(BufferedInputStream::new)
                .orElseThrow(() -> new IOException(String.format("Resource at path '%s' cannot be read!", resource.getPath())));
    }

    public String detectContentType(String path) {
        return new Tika().detect(StringUtils.substringAfterLast(script.resolvePath(path), "/"));
    }

    public void render(HttpServletResponse response, String path) throws IOException {
        response.setContentType(detectContentType(path));
        IOUtils.copy(readAsStream(path), response.getOutputStream());
    }
}
