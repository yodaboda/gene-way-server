package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.Scopes;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;

public class TestGeneWayRequestFactoryModule extends GeneWayRequestFactoryModule {

  private Utils utils;
  private RequestUtils requestUtils;

  public TestGeneWayRequestFactoryModule(Utils utils, RequestUtils requestUtils) {
    this.utils = utils;
    this.requestUtils = requestUtils;
  }

  @Override
  protected void configure() {
    // TODO: Figure out a way to deal with
    // No scope is bound to com.google.inject.servlet.RequestScoped
    bindScope(RequestScoped.class, Scopes.SINGLETON);
    bind(Utils.class).toInstance(utils);
    bind(RequestUtils.class).toInstance(requestUtils);
  }
}
