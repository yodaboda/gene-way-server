package com.nutrinfomics.geneway.server.requestfactory.request;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.VaryingSnack;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;

@RequestScoped
public class NextSnackService {

  public static final LocalTime FIRST_MEAL_TIME = LocalTime.of(8, 0, 0);

  private EntityManager entityManager;
  private ScheduledAlert scheduledAlert;
  private HibernateUtil hibernateUtil;
  private Clock clock;

  @Inject
  public NextSnackService(
      EntityManager entityManager,
      ScheduledAlert scheduledAlert,
      HibernateUtil hibernateUtil,
      Clock clock) {
    this.entityManager = entityManager;
    this.scheduledAlert = scheduledAlert;
    this.hibernateUtil = hibernateUtil;
    this.clock = clock;
  }

  public Snack getNextSnack(Session session, String dateString) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid());

    if (sessionDb.getCustomer().getPlan().getTodaysSnackMenu() == null) {
      setTodaysSnackMenu(sessionDb, dateString);
    }

    Snack nextSnack = calcNextSnack(sessionDb, true);
    if (nextSnack == null) {
      setTodaysSnackMenu(sessionDb, dateString);
      nextSnack = calcNextSnack(sessionDb, false);
    }

    return nextSnack;
  }

  Snack calcNextSnack(Session sessionDb, boolean sameDay) {
    MarkedSnackMenu todaysSnackMenu = sessionDb.getCustomer().getPlan().getTodaysSnackMenu();

    SnackOrderSpecification snackOrderSpecification =
        sessionDb.getCustomer().getPlan().getSnackOrderSpecification();

    MarkedSnack markedSnack = todaysSnackMenu.calcCurrentSnack(snackOrderSpecification);

    if (markedSnack != null) {
      // TODO: alert information should be retrieved from DB or should be fixed at creation time
      double inHours = getAlertDelayInHours(sessionDb, sameDay);

      this.scheduledAlert.schedule(inHours);
      //			Alerts.getInstance().createAlert(sessionDb.getCustomer(), markedSnack.getSnack(),
      // sameDay, email);
      return markedSnack.getSnack();
    }

    return null;
  }

  double getAlertDelayInHours(Session sessionDb, boolean sameDay) {
    double inHours;
    if (!sameDay) {
      LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
      LocalDateTime mealDateTime = LocalDateTime.of(tomorrow, FIRST_MEAL_TIME);

      LocalDateTime now = LocalDateTime.now(clock);

      Duration duration = Duration.between(now, mealDateTime);
      inHours = duration.toHours();
    } else {
      inHours =
          sessionDb
              .getCustomer()
              .getPlan()
              .getPlanPreferences()
              .getSnackTimes()
              .getTimeBetweenSnacks();
    }
    return inHours;
  }

  @Transactional
  void setTodaysSnackMenu(Session sessionDb, String dateString) {
    MarkedSnackMenu markedSnackMenu = calcTodaysSnackMenu(sessionDb, dateString);
    Plan plan = sessionDb.getCustomer().getPlan();
    entityManager.merge(markedSnackMenu);
    plan.setTodaysSnackMenu(markedSnackMenu);
    entityManager.merge(plan);
  }

  @Transactional
  MarkedSnackMenu calcTodaysSnackMenu(Session sessionDb, String dateString) {
    Plan plan = sessionDb.getCustomer().getPlan();
    SnackMenu snackMenu = plan.getSnackMenu();

    List<Snack> snacks = new ArrayList<>();

    for (Snack snack : snackMenu.getSnacks()) {
      if (snack instanceof VaryingSnack) {
        Snack resultSnack = getTodaysSnack((VaryingSnack) snack);
        snack = resultSnack;
      } else if (snack instanceof GeneralVaryingSnack) {
        GeneralVaryingSnack gvSnack = (GeneralVaryingSnack) snack;
        Snack todaySnack = gvSnack.pickTodaysSnack();
        if (todaySnack == null) {
          gvSnack.reset();
          todaySnack = gvSnack.pickTodaysSnack();
        }
        entityManager.merge(gvSnack);
        snack = todaySnack;
      }
      snacks.add(snack);
    }

    return new MarkedSnackMenu(dateString, snacks);
  }

  // TODO: Consider moving functionality to VaryingSnack
  @Transactional
  Snack getTodaysSnack(VaryingSnack varyingSnack) {
    try {
      List<Integer> indices = new ArrayList<>(7);
      for (int i = 0; i < 7; ++i) indices.add(i);
      Collections.shuffle(indices);

      for (int i = 0; i < indices.size(); ++i) {
        if (!varyingSnack.isEaten(indices.get(i))) {
          varyingSnack.setEaten(indices.get(i), true);
          return varyingSnack.getWeeklySnacks().get(indices.get(i));
        }
      }

      // otherwise, all snacks already eaten - end of week - reset
      for (int i = 0; i < 7; ++i) varyingSnack.setEaten(i, false);

      varyingSnack.setEaten(indices.get(0), true);
      return varyingSnack.getWeeklySnacks().get(indices.get(0));
    } finally {
      entityManager.merge(varyingSnack);
    }
  }
}
