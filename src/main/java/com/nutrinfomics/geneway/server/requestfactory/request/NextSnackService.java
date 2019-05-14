/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */