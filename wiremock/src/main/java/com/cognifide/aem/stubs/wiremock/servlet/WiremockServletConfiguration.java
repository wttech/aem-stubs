package com.cognifide.aem.stubs.wiremock.servlet;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.github.tomakehurst.wiremock.stubbing.Scenario;

@ObjectClassDefinition(
  name = "AEM Stubs - Wiremock Servlet Configuration",
  description = "Wiremock Servlet Configuration"
)
public @interface WiremockServletConfiguration {

  @AttributeDefinition(
    name = "path",
    description = "URL prefix for AEM Stubs"
  )
  String path() default "/wiremock";

}

