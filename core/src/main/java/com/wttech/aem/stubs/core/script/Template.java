package com.wttech.aem.stubs.core.script;

import com.wttech.aem.stubs.core.util.JcrUtils;
import groovy.text.GStringTemplateEngine;
import java.io.*;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.apache.sling.api.resource.Resource;

public class Template {

    private final Repository repository;

    private final GStringTemplateEngine engine;

    public Template(Repository repository) {
        this.repository = repository;
        this.engine = new GStringTemplateEngine();
    }

    public void render(Writer writer, String path, Map<?, ?> vars) throws IOException, ClassNotFoundException {
        engine.createTemplate(read(repository.getResource(path))).make(vars).writeTo(writer);
    }

    public void render(Writer writer, String path) throws IOException, ClassNotFoundException {
        render(writer, path, Map.of());
    }

    public void render(HttpServletResponse response, String path, Map<?, ?> vars)
            throws IOException, ClassNotFoundException {
        var contentType = repository.detectContentType(path);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        render(response.getWriter(), path, vars);
    }

    public void render(HttpServletResponse response, String path) throws IOException, ClassNotFoundException {
        render(response.getWriter(), path, Map.of());
    }

    private Reader read(Resource resource) throws IOException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(InputStreamReader::new)
                .orElseThrow(() ->
                        new IOException(String.format("Template at path '%s' cannot be read!", resource.getPath())));
    }

    public String renderAsString(String path, Map<?, ?> vars) throws IOException, ClassNotFoundException {
        var writer = new StringWriter();
        render(writer, path, vars);
        return writer.toString();
    }

    public String renderFromString(String template, Map<?, ?> vars) throws IOException, ClassNotFoundException {
        var writer = new StringWriter();
        engine.createTemplate(template).make(vars).writeTo(writer);
        return writer.toString();
    }
}