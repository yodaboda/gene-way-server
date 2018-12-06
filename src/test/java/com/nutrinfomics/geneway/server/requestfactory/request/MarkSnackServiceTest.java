package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
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

public class MarkSnackServiceTest {

	private final String SID = "SID";
	private final long SNACK_ID = 1;

	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private HibernateUtil mockHibernateUtil;
	@Mock
	private Alerts mockAlerts;
	
	@Mock
	private Session mockSession;
	@Mock
	private Snack mockSnack;
	@Mock
	private SnackHistory mockSnackHistory;
	@Mock
	private Session mockDbSession;
	@Mock
	private Customer mockDbCustomer;
	@Mock
	private Plan mockDbPlan;
	@Mock
	private PlanPreferences mockDbPlanPreferences;
	@Mock
	private MarkedSnackMenu mockDbMarkedSnackMenu;
	@Mock
	private SnackOrderSpecification mockDbSnackOrderSpecification;
	@Mock
	private MarkedSnack mockDbMarkedSnack;
	@Mock
	private Snack mockDbSnack;
	@Mock
	private SnackHistory mockDbSnackHistory;
	@Mock
	private ScheduledAlert mockScheduledAlert;


	private MarkSnackService markSnackService;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		markSnackService = new MarkSnackService(mockEntityManagerProvider, mockHibernateUtil,
												mockAlerts);
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
		when(mockDbMarkedSnackMenu.calcCurrentSnack(mockDbSnackOrderSpecification)).thenReturn(mockDbMarkedSnack);
		doNothing().when(mockDbMarkedSnack).setMarked(true);
		when(mockEntityManager.merge(mockDbSnackHistory)).thenReturn(null);
		doReturn(mockDbSnack).when(mockDbMarkedSnack).getSnack();
		doReturn(SNACK_ID).when(mockDbSnack).getId();
		doNothing().when(mockScheduledAlert).cancel();
		when(mockAlerts.getSnackAlert(SNACK_ID)).thenReturn(mockScheduledAlert);
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
	public void markCurrentSnack_AsExpected() {
		markSnackService.markCurrentSnack(mockSession, mockSnack, mockSnackHistory);

		verify(mockDbMarkedSnack, times(1)).setMarked(true);
		verify(mockAlerts, times(1)).getSnackAlert(SNACK_ID);
		verify(mockScheduledAlert, times(1)).cancel();
		verify(mockEntityManager, times(1)).merge(mockSnackHistory);
	}

}
