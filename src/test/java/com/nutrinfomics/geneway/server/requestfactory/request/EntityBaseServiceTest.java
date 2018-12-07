package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class EntityBaseServiceTest {

	private final String SID = "SID";

	private EntityBaseService entityBaseService;
	
	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private HibernateUtil mockHibernateUtil;
	@Mock
	private EntityBase mockEntityBase;

	@Mock
	private Session mockSession;
	@Mock
	private Session mockDbSession;

	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		entityBaseService = new EntityBaseService(mockEntityManagerProvider, mockHibernateUtil);
		setupMockEntityProvider();
		setupMockHibernateUtil();
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
	public void persist_AsExpected() {
		doNothing().when(mockEntityManager).persist(mockEntityBase);
		entityBaseService.persist(mockEntityBase);
		
		verify(mockEntityManager, times(1)).persist(mockEntityBase);
	}

	@Test
	public void merge_AsExpected() {
		when(mockEntityManager.merge(mockEntityBase)).thenReturn(null);
		entityBaseService.merge(mockEntityBase);
		
		verify(mockEntityManager, times(1)).merge(mockEntityBase);
	}

	@Test
	public void remove_AsExpected() {
		doNothing().when(mockEntityManager).remove(mockEntityBase);
		entityBaseService.remove(mockEntityBase);
		
		verify(mockEntityManager, times(1)).remove(mockEntityBase);
	}

	@Test
	public void mergePersonalDetails_AsExpected() {
		PersonalDetails mockPersonalDetails = mock(PersonalDetails.class);
		Customer mockDbCustomer = mock(Customer.class);
		
		doNothing().when(mockPersonalDetails).setCustomer(any());
		doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
		doNothing().when(mockDbCustomer).setPersonalDetails(mockPersonalDetails);
		
		entityBaseService.mergePersonalDetails(mockSession, mockPersonalDetails);

		verify(mockDbCustomer, times(1)).setPersonalDetails(mockPersonalDetails);
		verify(mockPersonalDetails, times(1)).setCustomer(mockDbCustomer);
	}

}
