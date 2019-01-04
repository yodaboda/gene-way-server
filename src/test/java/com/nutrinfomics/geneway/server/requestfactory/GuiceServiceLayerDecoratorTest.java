package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

import java.util.Locale;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Provider;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBaseLocator;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.requestfactory.request.PlanService;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class GuiceServiceLayerDecoratorTest {

  @Inject private GuiceServiceLayerDecorator layerDecorator;
  
  @Mock private Utils mockUtils;
  @Mock private RequestUtils mockRequestUtils;
  
  @Bind @Mock private Provider<EntityManager> mockEntityManagerProvider;
  @Mock private EntityManager mockEntityManager;
  @Bind @Mock private HibernateUtil mockHibernateUtil;

  @Before
  public void setup() {

	  MockitoAnnotations.initMocks(this);
	  
	  doReturn(mockEntityManager).when(mockEntityManagerProvider).get();
	  
	    Locale defaultLocale = Locale.ITALIAN;
	    doReturn(defaultLocale).when(mockUtils).getLocale();
	    
    Guice.createInjector(
            Modules.override(new GeneWayRequestFactoryModule())
                .with(new TestGeneWayRequestFactoryModule(mockUtils, mockRequestUtils)),
            BoundFieldModule.of(this))
        .injectMembers(this);
  }

  @Test
  public void createLocatorClassOfDevice_AsExpected() {
    var locator = layerDecorator.createLocator(EntityBaseLocator.class);
    var device = locator.create(Device.class);
    assertNotNull(device);
  }

  @Test
  public void createServiceLocator_PlanService_AsExpected() {
	  var serviceLocator = layerDecorator.createServiceLocator(GeneWayServiceLocator.class);
	  var planService = serviceLocator.getInstance(PlanService.class);
	  assertNotNull(planService);
  }
  
//  @Test
//  public void createServiceInstanceClassOfQPlanService_AsExpected() {
//	  var serviceLocator = layerDecorator.createServiceInstance(RequestContext.class);
//	  var planService = ((GeneWayServiceLocator)serviceLocator).getInstance(PlanService.class);
//	  assertNotNull(planService);
//  }

  @Test
  public void validate_Credentials_1Violation() {
    Credentials credentials = new Credentials();
    credentials.setPassword("1");
    var constraintViolationSet = layerDecorator.validate(credentials);
    assertTrue(constraintViolationSet.size() == 1);
  }
  
  @Test
  public void validate_Customer_NoViolation() {
    Customer customer = new Customer();
    customer.setNickName("Oracle");
    var constraintViolationSet = layerDecorator.validate(customer);
    assertTrue(constraintViolationSet.isEmpty());
  }
}
