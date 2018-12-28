package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.alerts.Alerts;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;

public class MarkSnackService {
  private Provider<EntityManager> entityManager;
  private HibernateUtil hibernateUtil;
  private Alerts alerts;

  @Inject
  public MarkSnackService(
      Provider<EntityManager> entityManager, HibernateUtil hibernateUtil, Alerts alerts) {
    this.entityManager = entityManager;
    this.hibernateUtil = hibernateUtil;
    this.alerts = alerts;
  }

  @Transactional
  public void markCurrentSnack(Session session, Snack snack, SnackHistory snackHistory) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager.get());
    MarkedSnackMenu todaysSnackMenu = sessionDb.getCustomer().getPlan().getTodaysSnackMenu();

    SnackOrderSpecification snackOrderSpecification =
        sessionDb.getCustomer().getPlan().getSnackOrderSpecification();

    MarkedSnack markedSnack = todaysSnackMenu.calcCurrentSnack(snackOrderSpecification);
    markedSnack.setMarked(true);
    alerts.getAlert(markedSnack.getSnack().getId()).cancel();

    //		entityManager.get().merge(todaysSnackMenu);

    entityManager.get().merge(snackHistory);
  }
}
