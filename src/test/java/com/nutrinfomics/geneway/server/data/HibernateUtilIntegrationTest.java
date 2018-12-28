package com.nutrinfomics.geneway.server.data;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.nutrinfomics.geneway.server.domain.device.Device;

public class HibernateUtilIntegrationTest {

	private PersistService service;

	private Injector injector;

	@Inject
	private HibernateUtil hibernateUtil;

	private EntityManager entityManager;
	private EntityTransaction entityTransaction;

	public class BindingModule extends AbstractModule {

		@Override
		protected void configure() {
			bindScope(RequestScoped.class, Scopes.SINGLETON);

		}

	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		injector = Guice.createInjector(new JpaPersistModule("testUnit"), new BindingModule(),
				BoundFieldModule.of(this));

		service = injector.getInstance(PersistService.class);
		service.start();

		injector.injectMembers(this);

		entityManager = injector.getInstance(EntityManager.class);
		entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
	}

	@After
	public void shotdown() {
		entityTransaction.rollback();
		entityManager.close();
		service.stop();
	}
//	@Test
//	public void testGetCustomer() {
//		fail("Not yet implemented");
//	}

	@Test
	public void selectDeviceByUUID_AsExpected() {
		String uuid = "777";
		String code = "e";
		Device device = new Device();
		device.setUuid(uuid);
		device.setCode(code);
		entityManager.persist(device);

		Device deviceDb = hibernateUtil.selectDeviceByUUID(uuid);
		assertEquals(uuid, deviceDb.getUuid());
		assertEquals(code, deviceDb.getCode());

	}

//	@Test
//	public void testSelectCustomer() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSelectCustomerBasedOnPhoneNumber() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetSession() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSelectSessionStringProviderOfEntityManager() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSelectSessionString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSelectIdentifier() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSelectIdentifierFromUUID() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetCustomers() {
//		fail("Not yet implemented");
//	}

}
