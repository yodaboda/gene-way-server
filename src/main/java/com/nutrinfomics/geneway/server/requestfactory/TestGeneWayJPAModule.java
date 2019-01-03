package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.Scopes;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.data.HibernateUtil;

public class TestGeneWayJPAModule extends GeneWayJPAModule {

  private HibernateUtil hibernateUtil;

  public TestGeneWayJPAModule(HibernateUtil hibernateUtil) {
    this.hibernateUtil = hibernateUtil;
  }

  @Override
  protected void configure() {
    install(new JpaPersistModule("testUnit"));
    // TODO: Figure out a way to deal with
    // No scope is bound to com.google.inject.servlet.RequestScoped
    bindScope(RequestScoped.class, Scopes.SINGLETON);
    bind(HibernateUtil.class).toInstance(hibernateUtil);
  }
}
