package com.wttech.aem.stubs.core;

import com.wttech.aem.stubs.core.util.JcrUtils;
import groovy.text.GStringTemplateEngine;
import org.apache.sling.api.resource.Resource;

import java.io.*;
import java.util.Map;
import java.util.Optional;

public class GroovyTemplate {

    private final Resource resource;

    public GroovyTemplate(Resource resource) {
        this.resource = resource;
    }

    public String getPath() {
        return resource.getPath();
    }

    public void render(Writer writer) throws StubException {
        var engine = new GStringTemplateEngine();
        var binding = Map.of("resource", resource);
        try {
            engine.createTemplate(readSourceCode()).make(binding).writeTo(writer);
        } catch (IOException | ClassNotFoundException e) {
            throw new StubException(String.format("Cannot render stub template '%s'!", getPath()));
        }
    }

    private Reader readSourceCode() throws StubException {
        return Optional.ofNullable(resource.getChild(JcrUtils.JCR_CONTENT))
                .map(r -> r.adaptTo(InputStream.class))
                .map(InputStreamReader::new)
                .orElseThrow(() -> new StubException(String.format("Cannot read stub template '%s'!", getPath())));
    }
}
