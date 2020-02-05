package com.company.aem.wiremockonaem.aem.activator;


import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import com.company.aem.wiremockonaem.aem.core.HelloServlet;


public final class Activator implements BundleActivator {

  @Override
  public void start(BundleContext context)
    throws Exception {
    System.out.println(".......................Activating");
    // create and register servlets
    final HelloServlet servlet1 = new HelloServlet();
    final Dictionary<String, Object> servlet1Props = new Hashtable<>();
    servlet1Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/wiremock2/*");
    servlet1Props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
      "(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=wiremock2)");

    servlet1Props.put("context.init.RequestHandlerClass", "com.github.tomakehurst.wiremock.http.StubRequestHandle");
    servlet1Props.put("context.init.mappedUnder", "/wiremock2");

    context.registerService(Servlet.class, servlet1, servlet1Props);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // nothing to do, services are unregistered automatically
  }
}
