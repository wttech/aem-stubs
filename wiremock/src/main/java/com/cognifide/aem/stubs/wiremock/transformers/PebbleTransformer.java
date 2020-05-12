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
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cognifide.aem.stubs.wiremock.WireMockException;
import com.cognifide.aem.stubs.wiremock.util.JcrFileReader;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.ProxyResponseDefinitionBuilder;
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

  private final boolean global;

  public PebbleTransformer(JcrFileReader jcrFileReader, boolean global) {
    super();
    this.global = global;
    this.jcrFileReader = jcrFileReader;
    this.engine = new PebbleEngine.Builder()
      .cacheActive(false)
      .loader(new StringLoader())
      .build();
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition definition,
    FileSource files, Parameters parameters) {
    ResponseDefinitionBuilder definitionBuilder = ResponseDefinitionBuilder
      .like(definition);
    final ImmutableMap<String, Object> model = ImmutableMap.<String, Object>builder()
      .put("parameters", calculateParameters(parameters))
      .put("request", RequestTemplateModel.from(request)).build();

    //proxy
    if (definition.isProxyResponse()) {
      PebbleTemplate baseUrlTemplate = engine.getTemplate(definition.getProxyBaseUrl());
      ProxyResponseDefinitionBuilder proxied =
        definitionBuilder.proxiedFrom(evaluate(baseUrlTemplate, model));

      return copyAdditionalHeaders(definition, proxied).build();
    }

    //body
    return Optional.ofNullable(getBodyTemplateString(definition))
      .map(transformBody(definition, definitionBuilder, model))
      .orElse(definition);
  }

  private ProxyResponseDefinitionBuilder copyAdditionalHeaders(ResponseDefinition definition,
    ProxyResponseDefinitionBuilder proxied) {
    Optional.ofNullable(definition.getAdditionalProxyRequestHeaders())
      .ifPresent(httpHeaders ->
        httpHeaders.all()
          .forEach(h -> h.values().forEach(v -> proxied.withAdditionalRequestHeader(h.key(), v)))
      );

    return proxied;
  }

  private Function<String, ResponseDefinition> transformBody(ResponseDefinition definition,
    ResponseDefinitionBuilder definitionBuilder, ImmutableMap<String, Object> model) {
    return body -> {
      PebbleTemplate bodyTemplate = engine.getTemplate(body);
      final String newBodyOrigin = evaluate(bodyTemplate, model);
      String newBody;

      if (definition.specifiesBodyFile()) {
        String template = jcrFileReader.readText(newBodyOrigin)
          .orElseThrow(() -> new WireMockException(
            String.format("Cannot read template '%s'!", newBodyOrigin)));
        PebbleTemplate fileTemplate = engine.getTemplate(template);
        newBody = evaluate(fileTemplate, model);
      } else {
        newBody = newBodyOrigin;
      }
      return definitionBuilder.withBody(newBody).build();
    };
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
    return global;
  }

  @Override
  public String getName() {
    return NAME;
  }
}

