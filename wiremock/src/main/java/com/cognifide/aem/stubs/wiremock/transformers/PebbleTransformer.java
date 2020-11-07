package com.cognifide.aem.stubs.wiremock.transformers;

import static com.google.common.base.MoreObjects.firstNonNull;

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
import com.github.tomakehurst.wiremock.common.ContentTypes;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableMap;

public class PebbleTransformer extends ResponseDefinitionTransformer {

  private static final String NAME = "pebble-response-template";

  private final Pebble pebble;

  private final JcrFileReader jcrFileReader;

  private final boolean global;

  public PebbleTransformer(JcrFileReader jcrFileReader, boolean global) {
    super();
    this.global = global;
    this.jcrFileReader = jcrFileReader;
    this.pebble = new Pebble();
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
      ProxyResponseDefinitionBuilder proxied =
        definitionBuilder.proxiedFrom(pebble.evaluate(definition.getProxyBaseUrl(), model));

      return copyAdditionalHeaders(definition, proxied).build();
    }

    //body
    return Optional.ofNullable(getBodyTemplateString(definition))
      .map(transformBody(definition, definitionBuilder, model))
      .map(ResponseDefinitionBuilder::build)
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

  private Function<String, ResponseDefinitionBuilder> transformBody(ResponseDefinition definition,
    ResponseDefinitionBuilder definitionBuilder, ImmutableMap<String, Object> model) {
    return body -> {
      final String newBody = pebble.evaluate(body, model);

      if (!definition.specifiesBodyFile()) {
        return definitionBuilder.withBody(newBody);
      }

      if (!hasTextMimeType(definition) || definition.specifiesBinaryBodyContent()) {
        return definitionBuilder.withBodyFile(newBody);
      } else {
        return definitionBuilder.withBody(evaluateTextBodyFromFile(model, newBody));
      }
    };
  }

  private boolean hasTextMimeType(ResponseDefinition definition) {
    HttpHeader header = definition.getHeaders().getHeader(ContentTypeHeader.KEY);
    ContentTypeHeader contentTypeHeader = header.isPresent() ?
      new ContentTypeHeader(header.firstValue()) :
      ContentTypeHeader.absent();
    return ContentTypes.determineIsTextFromMimeType(contentTypeHeader.mimeTypePart());
  }

  private String evaluateTextBodyFromFile(ImmutableMap<String, Object> model, String fileName) {
    String template = jcrFileReader.readText(fileName)
      .orElseThrow(() -> new WireMockException(
        String.format("Cannot read template '%s'!", fileName)));
    return pebble.evaluate(template, model);
  }

  private Map<String, Object> calculateParameters(Parameters parameters) {
    return firstNonNull(parameters, Collections.<String, Object>emptyMap()).entrySet()
      .stream()
      .collect(Collectors.toMap(Entry::getKey, e -> {
        if (e.getValue() instanceof DynamicParameterProvider){
          return ((DynamicParameterProvider) e.getValue()).call();
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

  @Override
  public boolean applyGlobally() {
    return global;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
