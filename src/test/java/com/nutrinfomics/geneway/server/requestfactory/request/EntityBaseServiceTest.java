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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class EntityBaseServiceTest {

  private final String SID = "SID";

  private EntityBaseService entityBaseService;

  @Mock private EntityManager mockEntityManager;
  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private EntityBase mockEntityBase;

  @Mock private Session mockSession;
  @Mock private Session mockDbSession;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    entityBaseService = new EntityBaseService(mockEntityManager, mockHibernateUtil);
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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
