package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackTimes;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;

public class NextSnackServiceTest {
	private static final double TIME_BETWEEN_SNACKS = 1.2;
	private static final double NEXT_DAY_TIME_BETWEEN_SNACKS = 30.;

	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	private NextSnackService nextSnackService;
	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private ScheduledAlert mockScheduledAlert;
	@Mock
	private HibernateUtil mockHibernateUtil;
	private Clock clock;

	@Mock
	private Session mockDbSession;
	@Mock
	private Customer mockDbCustomer;
	@Mock
	private Plan mockDbPlan;
	@Mock
	private PlanPreferences mockDbPlanPreferences;
	@Mock
	private SnackTimes mockDbSnackTimes;
	@Mock
	private MarkedSnackMenu mockDbMarkedSnackMenu;
	@Mock
	private SnackOrderSpecification mockDbSnackOrderSpecification;
	@Mock
	private MarkedSnack mockDbMarkedSnack;
	@Mock
	private Snack mockDbSnack;
	
	@Before 
	public void mock() {
		MockitoAnnotations.initMocks(this);
		clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
		nextSnackService = new NextSnackService(mockEntityManagerProvider, mockScheduledAlert,
												mockHibernateUtil, clock);
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
	public void testGetNextSnack() {
		fail("Not yet implemented");
	}

	@Test
	public void getAlertDelayInHours_sameDay_AsExpected() {
		boolean sameDay = true;
		assertEquals(TIME_BETWEEN_SNACKS, 
					nextSnackService.getAlertDelayInHours(mockDbSession, sameDay),
					0.0010);
	}

	@Test
	public void getAlertDelayInHours_nextDay_AsExpected() {
		doReturn(NEXT_DAY_TIME_BETWEEN_SNACKS).when(mockDbSnackTimes).getTimeBetweenSnacks();
		boolean sameDay = false;
		assertEquals(NEXT_DAY_TIME_BETWEEN_SNACKS, 
					nextSnackService.getAlertDelayInHours(mockDbSession, sameDay),
					0.0010);
	}

	@Test
	public void testSetTodaysSnackMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalcTodaysSnackMenu() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTodaysSnack() {
		fail("Not yet implemented");
	}

}
