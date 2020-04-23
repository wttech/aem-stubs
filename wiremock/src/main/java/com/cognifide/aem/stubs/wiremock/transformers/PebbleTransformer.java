package com.cognifide.aem.stubs.wiremock.transformers;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.google.common.base.MoreObjects.firstNonNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.cognifide.aem.stubs.wiremock.jcr.JcrFileReader;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

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
      .put("parameters", firstNonNull(parameters, Collections.<String, Object>emptyMap()))
      .put("request", RequestTemplateModel.from(request)).build();

    PebbleTemplate bodyTemplate = engine.getTemplate(getTemplateString(responseDefinition));
    String newBody = applyTemplatedResponseBody(model, bodyTemplate);

    if (responseDefinition.specifiesBodyFile()) {
      newResponseDefBuilder.withBody(jcrFileReader.readAsText(newBody));
    } else {
      newResponseDefBuilder.withBody(newBody);
    }

    return newResponseDefBuilder.build();
  }

  private String getTemplateString(ResponseDefinition definition) {
    return Optional.of(definition)
      .map(ResponseDefinition::getBody)
      .orElse(definition.getBodyFileName());
  }


  private String applyTemplatedResponseBody(ImmutableMap<String, Object> model,
    PebbleTemplate bodyTemplate) {
    return uncheckedApplyTemplate(bodyTemplate, model);
  }

  private String uncheckedApplyTemplate(PebbleTemplate template, Map<String, Object> context) {
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

