package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class GeneWayServiceLocator implements ServiceLocator {

  private final Injector injector;

  @Inject
  protected GeneWayServiceLocator(final Injector injector) {
    super();
    this.injector = injector;
  }

  @Override
  public Object getInstance(Class<?> clazz) {
    return injector.getInstance(clazz);
  }
}
