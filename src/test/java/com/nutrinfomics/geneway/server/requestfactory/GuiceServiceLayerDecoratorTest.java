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
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */