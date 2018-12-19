package com.nutrinfomics.geneway.server;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.servlet.RequestScoped;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

@RequestScoped
public class RequestUtils {
  public HttpServletRequest getHttpServletRequest() {
    return RequestFactoryServlet.getThreadLocalRequest();
  }
}
