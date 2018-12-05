package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class PlanServiceTest {

	private final String SID = "SID";
	
	private PlanService planService;
	
	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private HibernateUtil mockHibernateUtil;

	
	@Mock
	private Session mockSession;

	@Mock
	private Session mockDbSession;
	@Mock
	private Customer mockDbCustomer;

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		planService = new PlanService(mockEntityManagerProvider, mockHibernateUtil);
		setupMockDbSession();
		setupMockEntityProvider();
		setupMockHibernateUtil();
	}

	private void setupMockDbSession() {
		doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
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

	private void setupMockHibernateUtil() {
		when(mockHibernateUtil.selectSession(SID, mockEntityManagerProvider)).thenReturn(mockDbSession);
		doReturn(SID).when(mockSession).getSid();
	}
	private void setupMockEntityProvider() {
		doReturn(mockEntityManager).when(mockEntityManagerProvider).get();
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
	public void testGetPlanPreferences() {
		fail("Not yet implemented");
	}

	@Test
	public void testMarkCurrentSnack() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateSpecifications() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSnackOrderSpecification() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIngredients() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMenuSummary() {
		fail("Not yet implemented");
	}

}
