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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.alerts.Alerts;
import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class MarkSnackServiceTest {

  private final String SID = "SID";
  private final long SNACK_ID = 1;

  @Mock private EntityManager mockEntityManager;
  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private Alerts mockAlerts;

  @Mock private Session mockSession;
  @Mock private Snack mockSnack;
  @Mock private SnackHistory mockSnackHistory;
  @Mock private Session mockDbSession;
  @Mock private Customer mockDbCustomer;
  @Mock private Plan mockDbPlan;
  @Mock private PlanPreferences mockDbPlanPreferences;
  @Mock private MarkedSnackMenu mockDbMarkedSnackMenu;
  @Mock private SnackOrderSpecification mockDbSnackOrderSpecification;
  @Mock private MarkedSnack mockDbMarkedSnack;
  @Mock private Snack mockDbSnack;
  @Mock private SnackHistory mockDbSnackHistory;
  @Mock private ScheduledAlert mockScheduledAlert;

  private MarkSnackService markSnackService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    markSnackService = new MarkSnackService(mockEntityManager, mockHibernateUtil, mockAlerts);
    setupMockDbSession();
    setupMockEntityProvider();
    setupMockHibernateUtil();
  }

  private void setupMockDbSession() {
    doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
    doReturn(mockDbPlan).when(mockDbCustomer).getPlan();
    doReturn(mockDbPlanPreferences).when(mockDbPlan).getPlanPreferences();
    doReturn(mockDbMarkedSnackMenu).when(mockDbPlan).getTodaysSnackMenu();
    doReturn(mockDbSnackOrderSpecification).when(mockDbPlan).getSnackOrderSpecification();
    when(mockDbMarkedSnackMenu.calcCurrentSnack(mockDbSnackOrderSpecification))
        .thenReturn(mockDbMarkedSnack);
    doNothing().when(mockDbMarkedSnack).setMarked(true);
    when(mockEntityManager.merge(mockDbSnackHistory)).thenReturn(null);
    doReturn(mockDbSnack).when(mockDbMarkedSnack).getSnack();
    doReturn(SNACK_ID).when(mockDbSnack).getId();
    doNothing().when(mockScheduledAlert).cancel();
    when(mockAlerts.getAlert(SNACK_ID)).thenReturn(mockScheduledAlert);
  }

  private void setupMockHibernateUtil() {
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);
    doReturn(SID).when(mockSession).getSid();
  }

  private void setupMockEntityProvider() {
    when(mockEntityManager.merge(any())).thenReturn(null);
  }

  @Test
  public void markCurrentSnack_AsExpected() {
    markSnackService.markCurrentSnack(mockSession, mockSnack, mockSnackHistory);

    verify(mockDbMarkedSnack, times(1)).setMarked(true);
    verify(mockAlerts, times(1)).getAlert(SNACK_ID);
    verify(mockScheduledAlert, times(1)).cancel();
    verify(mockEntityManager, times(1)).merge(mockSnackHistory);
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */