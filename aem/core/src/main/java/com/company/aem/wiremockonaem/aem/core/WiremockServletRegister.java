package com.company.aem.wiremockonaem.aem.core;


import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.github.tomakehurst.wiremock.servlet.WireMockHandlerDispatchingServlet;

@Component
public class WiremockServletRegister {
  @Reference
  private HttpService httpService;

  @Activate
  public void start(){
    try {
      Dictionary initWiremockParams = new Hashtable();
      initWiremockParams.put("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.StubRequestHandle");
      initWiremockParams.put("mappedUnder", "/wiremock");


/*
      Dictionary initWiremockAdminParams = new Hashtable();
      initWiremockParams.put("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.StubRequestHandle");
      initWiremockParams.put("mappedUnder", "/wiremock-admin");
*/

      HttpContext httpContext = httpService.createDefaultHttpContext();
      httpService.registerServlet("/wiremock/*", new WiremockServlet(), initWiremockParams, httpContext);
      //httpService.registerServlet("/wiremock-admin/*", new HelloServlet(), initWiremockAdminParams, httpContext);
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (NamespaceException e) {
      e.printStackTrace();
    }
  }

}
