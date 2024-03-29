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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.SnackTimes;
import com.nutrinfomics.geneway.server.domain.plan.VaryingSnack;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class NextSnackServiceTest {
  private static final double TIME_BETWEEN_SNACKS = 1.2;
  private static final double NEXT_DAY_TIME_BETWEEN_SNACKS = 30.;

  @Rule public ExpectedException thrown = ExpectedException.none();

  private NextSnackService nextSnackService;

  @Mock private EntityManager mockEntityManager;
  @Mock private ScheduledAlert mockScheduledAlert;
  @Mock private HibernateUtil mockHibernateUtil;
  private Clock clock;

  private final String SID = "SID";
  @Mock private Session mockSession;
  @Mock private Session mockDbSession;
  @Mock private Customer mockDbCustomer;
  @Mock private Plan mockDbPlan;
  @Mock private PlanPreferences mockDbPlanPreferences;
  @Mock private SnackTimes mockDbSnackTimes;
  @Mock private MarkedSnackMenu mockDbMarkedSnackMenu;
  @Mock private SnackOrderSpecification mockDbSnackOrderSpecification;
  @Mock private MarkedSnack mockDbMarkedSnack;
  @Mock private Snack mockDbSnack;
  @Mock private SnackMenu mockDbSnackMenu;
  @Mock private VaryingSnack mockVaryingSnack;
  @Mock private GeneralVaryingSnack mockGeneralVaryingSnack;
  @Mock private Snack mockTodaySnack;
  private List<Snack> weeklySnacks;
  @Mock private Snack mockWeeklySnacksPickedSnack;

  private boolean called = false;

  private Answer<Void> resetAnswer =
      new Answer<Void>() {
        public Void answer(InvocationOnMock invocation) throws Throwable {
          called = true;
          return null;
        }
      };

  private Answer<Snack> pickTodaysSnackAnswer =
      new Answer<Snack>() {
        public Snack answer(InvocationOnMock invocation) throws Throwable {
          return called ? mockTodaySnack : null;
        }
      };

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
    nextSnackService =
        new NextSnackService(mockEntityManager, mockScheduledAlert, mockHibernateUtil, clock);
    setupMockDbSession();
    setupMockEntityProvider();
    setupMockHibernateUtil();
  }

  private void setupMockHibernateUtil() {
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);
    doReturn(SID).when(mockSession).getSid();
  }

  private void setupMockEntityProvider() {
    when(mockEntityManager.merge(any())).thenReturn(null);
  }

  private void setupMockDbSession() {
    doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
    doReturn(mockDbPlan).when(mockDbCustomer).getPlan();
    doReturn(mockDbMarkedSnackMenu).when(mockDbPlan).getTodaysSnackMenu();
    doReturn(mockDbSnackOrderSpecification).when(mockDbPlan).getSnackOrderSpecification();

    doReturn(mockDbPlanPreferences).when(mockDbPlan).getPlanPreferences();
    doReturn(mockDbSnackTimes).when(mockDbPlanPreferences).getSnackTimes();
    when(mockDbMarkedSnackMenu.calcCurrentSnack(mockDbSnackOrderSpecification))
        .thenReturn(mockDbMarkedSnack);
    doReturn(mockDbSnack).when(mockDbMarkedSnack).getSnack();
    doReturn(TIME_BETWEEN_SNACKS).when(mockDbSnackTimes).getTimeBetweenSnacks();
    doReturn(mockDbSnackMenu).when(mockDbPlan).getSnackMenu();
    List<Snack> snacks = new ArrayList<>();
    doReturn(snacks).when(mockDbSnackMenu).getSnacks();
    snacks.add(mockVaryingSnack);
    snacks.add(mockGeneralVaryingSnack);
    doReturn(mockTodaySnack).when(mockGeneralVaryingSnack).pickTodaysSnack();
    weeklySnacks = Collections.nCopies(7, mockWeeklySnacksPickedSnack);
    when(mockVaryingSnack.isEaten(any())).thenReturn(false);
    doNothing().when(mockVaryingSnack).setEaten(anyInt(), anyBoolean());
    doReturn(weeklySnacks).when(mockVaryingSnack).getWeeklySnacks();
  }

  @Test
  public void calcNextSnack_AsExpected() {
    boolean sameDay = true;
    assertEquals(mockDbSnack, nextSnackService.calcNextSnack(mockDbSession, sameDay));
    verify(mockScheduledAlert, times(1)).schedule(TIME_BETWEEN_SNACKS);
  }

  @Test
  public void calcNextSnack_nextDay_AsExpected() {
    boolean sameDay = false;
    assertEquals(mockDbSnack, nextSnackService.calcNextSnack(mockDbSession, sameDay));
    verify(mockScheduledAlert, times(1)).schedule(NEXT_DAY_TIME_BETWEEN_SNACKS);
  }

  @Test
  public void calcNextSnack_null() {
    doReturn(null).when(mockDbMarkedSnack).getSnack();
    boolean sameDay = true;
    assertEquals(null, nextSnackService.calcNextSnack(mockDbSession, sameDay));
  }

  @Test
  public void getNextSnack_AsExppected() {
    String dateString = "77-01-170";
    assertEquals(mockDbSnack, nextSnackService.getNextSnack(mockSession, dateString));
  }

  @Test
  public void getAlertDelayInHours_sameDay_AsExpected() {
    boolean sameDay = true;
    assertEquals(
        TIME_BETWEEN_SNACKS, nextSnackService.getAlertDelayInHours(mockDbSession, sameDay), 0.0010);
  }

  @Test
  public void getAlertDelayInHours_nextDay_AsExpected() {
    doReturn(NEXT_DAY_TIME_BETWEEN_SNACKS).when(mockDbSnackTimes).getTimeBetweenSnacks();
    boolean sameDay = false;
    assertEquals(
        NEXT_DAY_TIME_BETWEEN_SNACKS,
        nextSnackService.getAlertDelayInHours(mockDbSession, sameDay),
        0.0010);
  }

  @Test
  public void setTodaysSnackMenu_AsExpected() {
    String dateString = "42-63-170";
    List<Snack> snacks = new ArrayList<>();
    snacks.add(mockTodaySnack);
    snacks.add(mockWeeklySnacksPickedSnack);
    MarkedSnackMenu markedSnackMenu = new MarkedSnackMenu(dateString, snacks);

    nextSnackService.setTodaysSnackMenu(mockDbSession, dateString);

    verify(mockEntityManager, times(1)).merge(markedSnackMenu);
    verify(mockDbPlan, times(1)).setTodaysSnackMenu(markedSnackMenu);
    verify(mockEntityManager, times(1)).merge(mockDbPlan);
  }

  @Test
  public void calcTodaysSnackMenu_AsExpected() {
    List<Snack> snacks = new ArrayList<>();
    snacks.add(mockTodaySnack);
    snacks.add(mockWeeklySnacksPickedSnack);
    String dateString = "17-4-96";
    MarkedSnackMenu markedSnackMenu = new MarkedSnackMenu(dateString, snacks);
    MarkedSnackMenu calculatedMarkedSnackMenu =
        nextSnackService.calcTodaysSnackMenu(mockDbSession, dateString);
    assertEquals(markedSnackMenu, calculatedMarkedSnackMenu);
  }

  @Test
  public void calcTodaysSnackMenu_nullPickedSnack_AsExpected() {
    doAnswer(resetAnswer).when(mockGeneralVaryingSnack).reset();
    doAnswer(pickTodaysSnackAnswer).when(mockGeneralVaryingSnack).pickTodaysSnack();

    List<Snack> snacks = new ArrayList<>();
    snacks.add(mockTodaySnack);
    snacks.add(mockWeeklySnacksPickedSnack);
    String dateString = "17-4-96";
    MarkedSnackMenu markedSnackMenu = new MarkedSnackMenu(dateString, snacks);
    MarkedSnackMenu calculatedMarkedSnackMenu =
        nextSnackService.calcTodaysSnackMenu(mockDbSession, dateString);
    assertEquals(markedSnackMenu, calculatedMarkedSnackMenu);
    verify(mockGeneralVaryingSnack, times(1)).reset();
  }

  @Test
  public void getTodaysSnack_AsExpected() {
    assertEquals(mockWeeklySnacksPickedSnack, nextSnackService.getTodaysSnack(mockVaryingSnack));
    verify(mockVaryingSnack, times(1)).isEaten(anyInt());
  }

  @Test
  public void getTodaysSnack_allEaten_AsExpected() {
    when(mockVaryingSnack.isEaten(any())).thenReturn(true);

    assertEquals(mockWeeklySnacksPickedSnack, nextSnackService.getTodaysSnack(mockVaryingSnack));
    verify(mockVaryingSnack, times(7)).isEaten(anyInt());
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
