package com.nutrinfomics.geneway.server.domain;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Locator;

public class EntityBaseLocator extends Locator<EntityBase, Long> {

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject Provider<EntityManager> entityManager;

  @Override
  public EntityBase create(Class<? extends EntityBase> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      LOGGER.log(Level.FATAL, e.toString(), e);
    } catch (IllegalAccessException e) {
      LOGGER.log(Level.FATAL, e.toString(), e);
    }
    return null;
  }

  @Override
  public EntityBase find(Class<? extends EntityBase> clazz, Long id) {
    return entityManager.get().find(clazz, id);
  }

  @Override
  public Class<EntityBase> getDomainType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Long getId(EntityBase domainObject) {
    return domainObject.getId();
  }

  @Override
  public Class<Long> getIdType() {
    return Long.class;
  }

  @Override
  public Object getVersion(EntityBase domainObject) {
    return domainObject.getVersion();
  }
}
