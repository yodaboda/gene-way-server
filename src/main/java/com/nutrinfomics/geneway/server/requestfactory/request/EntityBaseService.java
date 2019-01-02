package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Session;

@RequestScoped
public class EntityBaseService {

  private EntityManager entityManager;
  private HibernateUtil hibernateUtil;

  @Inject
  public EntityBaseService(EntityManager entityManager, HibernateUtil hibernateUtil) {
    this.entityManager = entityManager;
    this.hibernateUtil = hibernateUtil;
  }

  @Transactional
  public void persist(EntityBase entityBase) {
    entityManager.persist(entityBase);
  }

  @Transactional
  public void merge(EntityBase entityBase) {
    entityManager.merge(entityBase);
  }

  @Transactional
  public void remove(EntityBase entityBase) {
    entityManager.remove(entityBase);
  }

  @Transactional
  public void mergePersonalDetails(Session session, PersonalDetails personalDetails) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
    sessionDb.getCustomer().setPersonalDetails(personalDetails);
    personalDetails.setCustomer(sessionDb.getCustomer());
  }
}
