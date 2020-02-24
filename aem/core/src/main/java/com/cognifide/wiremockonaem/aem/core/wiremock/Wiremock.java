package com.cognifide.wiremockonaem.aem.core.wiremock;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.core.WireMockApp;
import com.github.tomakehurst.wiremock.http.RequestHandler;
import com.github.tomakehurst.wiremock.servlet.NotImplementedContainer;

@Component(
  service = Wiremock.class
)
public class Wiremock {

  private WireMockApp wireMockApp;

  @Activate
  public void start() {
    wireMockApp = new WireMockApp(new AEMConfiguration(), new NotImplementedContainer());
  }

  public void stubFor(MappingBuilder mappingBuilder){
    wireMockApp.addStubMapping(mappingBuilder.build());
  }

  public RequestHandler buildStubRequestHandler(){
    return wireMockApp.buildStubRequestHandler();
  }

  public void clearStubs(){
    wireMockApp.resetAll();
  }

}
