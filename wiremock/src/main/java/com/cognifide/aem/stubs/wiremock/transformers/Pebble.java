package com.cognifide.aem.stubs.wiremock.transformers;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

class Pebble {
  private final PebbleEngine engine;

  public Pebble(){
    this.engine = new PebbleEngine.Builder()
      .cacheActive(false)
      .loader(new StringLoader())
      .build();
  }

  public String evaluate(String template, Map<String, Object> context){
    PebbleTemplate fileTemplate = engine.getTemplate(template);
    return evaluate(fileTemplate, context);
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
}
