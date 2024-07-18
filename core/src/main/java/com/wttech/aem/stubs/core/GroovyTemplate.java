package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.JcrUtils;
import groovy.text.GStringTemplateEngine;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.tika.Tika;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Optional;

public class GroovyTemplate {

    private final ResourceResolver resourceResolver;

    private final GStringTemplateEngine engine;

    public GroovyTemplate(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.engine = new GStringTemplateEngine();
    }

    public void render(Writer writer, String path, Map<?, ?> vars) throws IOException, ClassNotFoundException {
        var resource = resourceResolver.getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Template at path '%s' does not exist!", path));
        }
        engine.createTemplate(read(resource)).make(vars).writeTo(writer);
    }

    public void render(Writer writer, String path) throws IOException, ClassNotFoundException {
        render(writer, path, Map.of());
    }

    public void render(HttpServletResponse response, String path, Map<?,?> vars) throws IOException, ClassNotFoundException {
        render(response.getWriter(), path, vars);
        var contentType = new Tika().detect(StringUtils.substringAfterLast(path, "/"));
        if (contentType != null) {
            response.setContentType(contentType);
        }
    }

    public void render(HttpServletResponse response, String path) throws IOException, ClassNotFoundException {
        render(response.getWriter(), path, Map.of());
    }

    private Reader read(Resource resource) throws IOException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(InputStreamReader::new)
                .orElseThrow(() -> new IOException(String.format("Template at path '%s' cannot be read!", resource.getPath())));
    }
}
