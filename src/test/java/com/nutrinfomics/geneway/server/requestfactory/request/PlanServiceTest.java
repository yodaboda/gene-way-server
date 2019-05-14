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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.specification.AbstractFoodSpecification;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class PlanServiceTest {

  private final String SID = "SID";

  private PlanService planService;

  @Mock private EntityManager mockEntityManager;
  @Mock private HibernateUtil mockHibernateUtil;

  @Mock private Session mockSession;

  @Mock private Session mockDbSession;
  @Mock private Customer mockDbCustomer;
  @Mock private Plan mockDbPlan;
  @Mock private PlanPreferences mockDbPlanPreferences;
  @Mock private MarkedSnackMenu mockDbMarkedSnackMenu;
  @Mock private SnackOrderSpecification mockDbSnackOrderSpecification;
  @Mock private MarkedSnack mockDbMarkedSnack;
  @Mock private SnackHistory mockDbSnackHistory;
  @Mock private SnackMenu mockDbSnackMenu;

  private static final List<String> SNACKS_SUMMARY = new ArrayList<>();
  private static final List<FoodItemType> FOOD_ITEM_TYPES =
      Arrays.asList(FoodItemType.BEEF_SIRLOIN, FoodItemType.ALMOND);
  private static final List<Snack> SNACKS = new ArrayList<>();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    planService = new PlanService(mockEntityManager, mockHibernateUtil);
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
    doReturn(mockDbSnackMenu).when(mockDbPlan).getSnackMenu();
    doReturn(SNACKS).when(mockDbSnackMenu).getSnacks();

    //		doReturn(mockDbPlan).when(mockDbCustomer).getPlan();
    //		doReturn(mockDbMarkedSnackMenu).when(mockDbPlan).getTodaysSnackMenu();
    //		doReturn(mockDbSnackOrderSpecification).when(mockDbPlan).getSnackOrderSpecification();
    //
    //		doReturn(mockDbPlanPreferences).when(mockDbPlan).getPlanPreferences();
    //		doReturn(mockDbSnackTimes).when(mockDbPlanPreferences).getSnackTimes();
    //		when(mockDbMarkedSnackMenu.calcCurrentSnack(mockDbSnackOrderSpecification))
    //									.thenReturn(mockDbMarkedSnack);
    //		doReturn(mockDbSnack).when(mockDbMarkedSnack).getSnack();
    //		doReturn(TIME_BETWEEN_SNACKS).when(mockDbSnackTimes).getTimeBetweenSnacks();
    //		doReturn(mockDbSnackMenu).when(mockDbPlan).getSnackMenu();
    //		List<Snack> snacks = new ArrayList<>();
    //		doReturn(snacks).when(mockDbSnackMenu).getSnacks();
    //		snacks.add(mockVaryingSnack);
    //		snacks.add(mockGeneralVaryingSnack);
    //		doReturn(mockTodaySnack).when(mockGeneralVaryingSnack).pickTodaysSnack();
    //		weeklySnacks = Collections.nCopies(7, mockWeeklySnacksPickedSnack);
    //		when(mockVaryingSnack.isEaten(any())).thenReturn(false);
    //		doNothing().when(mockVaryingSnack).setEaten(anyInt(), anyBoolean());
    //		doReturn(weeklySnacks).when(mockVaryingSnack).getWeeklySnacks();
  }

  @BeforeClass
  public static void initSnacks() {
    FoodItem foodItem = new FoodItem(.4F, MeasurementUnit.GRAM, FOOD_ITEM_TYPES.get(0));
    Snack snack = new Snack(foodItem);
    GeneralVaryingSnack varyingSnack = new GeneralVaryingSnack();
    varyingSnack.add(snack);
    SNACKS.add(varyingSnack);
    SNACKS_SUMMARY.add(varyingSnack.getSummary());

    FoodItem beefFoodItem = new FoodItem(.12345678F, MeasurementUnit.GRAM, FOOD_ITEM_TYPES.get(1));
    Snack beefSnack = new Snack(beefFoodItem);
    SNACKS.add(beefSnack);
    SNACKS_SUMMARY.add(beefSnack.getSummary());
  }

  private void setupMockHibernateUtil() {
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);
    doReturn(SID).when(mockSession).getSid();
  }

  private void setupMockEntityProvider() {
    when(mockEntityManager.merge(any())).thenReturn(null);
  }

  @Test
  public void setDemo_AsExpected() {
    Customer mockDemoCustomer = mock(Customer.class);
    Plan mockDemoPlan = mock(Plan.class);

    when(mockEntityManager.find(Customer.class, new Long(4))).thenReturn(mockDemoCustomer);
    doReturn(mockDemoPlan).when(mockDemoCustomer).getPlan();
    doNothing().when(mockDbCustomer).setPlan(any());

    planService.setDemo(mockSession);
    verify(mockDbCustomer, times(1)).setPlan(any());
  }

  @Test
  public void getPlanPreferences_AsExpected() {
    assertEquals(mockDbPlanPreferences, planService.getPlanPreferences(mockSession));
  }

  @Test
  public void updateSpecifications_AsExpected() {
    AbstractFoodSpecification foodSpecification = null;
    planService.updateSpecifications(mockDbSnackOrderSpecification, foodSpecification);
    verify(mockEntityManager, times(1)).merge(mockDbSnackOrderSpecification);
    verify(mockEntityManager, times(1)).flush();
    verify(mockEntityManager, times(1)).remove(foodSpecification);
  }

  @Test
  public void getSnackOrderSpecification_AsExpected() {
    assertEquals(
        mockDbSnackOrderSpecification, planService.getSnackOrderSpecification(mockSession));
  }

  @Test
  public void getIngredients_AsExpected() {
    Set<FoodItemType> expected = new HashSet<>(FOOD_ITEM_TYPES);
    String dateString = "24-12-63";
    assertEquals(expected, planService.getIngredients(mockSession, dateString));
  }

  @Test
  public void getMenuSummary_AsExpected() {
    String dateString = "17-9-17";
    assertEquals(SNACKS_SUMMARY, planService.getMenuSummary(mockSession, dateString));
  }

  @Test
  public void getMenuSummary_EmptySnackMenu_AsExpecetd() {
    List<Snack> emptySnacks = new ArrayList<>();
    doReturn(emptySnacks).when(mockDbSnackMenu).getSnacks();
    String dateString = "17-9-17";
    assertTrue(planService.getMenuSummary(mockSession, dateString).isEmpty());
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
