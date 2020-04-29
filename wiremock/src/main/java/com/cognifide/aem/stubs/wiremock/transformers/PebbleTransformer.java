package com.cognifide.aem.stubs.wiremock.transformers;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.google.common.base.MoreObjects.firstNonNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.cognifide.aem.stubs.wiremock.jcr.JcrFileReader;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import groovy.lang.Closure;

public class PebbleTransformer extends ResponseDefinitionTransformer {

  private static final String NAME = "pebble-response-template";
  private final PebbleEngine engine;
  private final JcrFileReader jcrFileReader;

  public PebbleTransformer(JcrFileReader jcrFileReader) {
    this.jcrFileReader = jcrFileReader;
    this.engine = new PebbleEngine.Builder()
      .loader(new StringLoader())
      .build();
    ;
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
    FileSource files, Parameters parameters) {
    ResponseDefinitionBuilder newResponseDefBuilder = ResponseDefinitionBuilder
      .like(responseDefinition);
    final ImmutableMap<String, Object> model = ImmutableMap.<String, Object>builder()
      .put("parameters", calculateParameters(parameters))
      .put("request", RequestTemplateModel.from(request)).build();

    //proxy
    if (responseDefinition.isProxyResponse()) {
      PebbleTemplate baseUrlTemplate = engine.getTemplate(responseDefinition.getProxyBaseUrl());
      newResponseDefBuilder.proxiedFrom(evaluate(baseUrlTemplate, model));
      return newResponseDefBuilder.build();
    }

    //body
    PebbleTemplate bodyTemplate = engine.getTemplate(getBodyTemplateString(responseDefinition));
    String newBody = evaluate(bodyTemplate, model);

    if (responseDefinition.specifiesBodyFile()) {
      PebbleTemplate fileTemplate = engine.getTemplate(jcrFileReader.readAsText(newBody));
      newBody = evaluate(fileTemplate, model);
    }

    return newResponseDefBuilder.withBody(newBody).build();
  }

  private Map<String, Object> calculateParameters(Parameters parameters) {
    return firstNonNull(parameters, Collections.<String, Object>emptyMap()).entrySet()
      .stream()
      .collect(Collectors.toMap(Entry::getKey, e -> {
        if (e.getValue() instanceof Closure) {
          return ((Closure) e.getValue()).call();
        } else if (e.getValue() instanceof Supplier<?>) {
          return ((Supplier) e.getValue()).get();
        } else {
          return e.getValue();
        }
      }));
  }

  private String getBodyTemplateString(ResponseDefinition definition) {
    return Optional.of(definition)
      .map(ResponseDefinition::getBody)
      .orElse(definition.getBodyFileName());
  }


  private String evaluate(PebbleTemplate template, Map<String, Object> context) {
    try {
      Writer writer = new StringWriter();
      template.evaluate(writer, context);

      return writer.toString();
    } catch (IOException e) {
      return throwUnchecked(e, String.class);
    }
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }

  @Override
  public String getName() {
    return NAME;
  }
}

