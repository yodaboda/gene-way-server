package com.nutrinfomics.geneway.server.requestfactory;

import javax.inject.Named;
import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class GeneWayJPAModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(HibernateUtil.class);

    install(new JpaPersistModule("domainPersistence"));
  }

  @Provides
  @RequestScoped
  public @Named("dbSession") Session provideDbSession(
      Session clientSession, EntityManager entityManager, HibernateUtil hibernateUtil) {
    return hibernateUtil.selectSession(clientSession.getSid());
  }
}
