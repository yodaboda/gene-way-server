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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geneway.alerts.impl.EmailAlertMechanism;
import com.nutrinfomics.geneway.server.PasswordUtils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class RegisterServiceTest {

  private static final String CONFIG = "log4j-appender.xml";

  @ClassRule public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);
  @Rule public ExpectedException thrown = ExpectedException.none();

  private static final String UUID = "UUID";
  private static final String PHONE = "44.42.41.43";
  private static final String CODE = "1224";
  private static final String HASHED_PASSWORD = "424";

  private ListAppender listAppender;

  @Mock private EmailAlertMechanism mockAlertMechanism;
  @Mock private HibernateUtil hibernateUtil;

  @Mock private Customer mockCustomer;
  @Mock private Customer mockMergedCustomer;
  @Mock private Device mockDevice;
  @Mock private Credentials mockCredintials;
  @Mock private Device mockDbDevice;
  @Mock private Customer mockDbCustomer;
  @Mock private ContactInformation mockDbContactInformation;
  @Mock private EntityManager mockEntityManager;
  @Mock private MimeMessage mockMimeMessage;
  private Clock clock;
  @Mock private PasswordUtils mockPasswordUtils;

  private RegisterService registerService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
    registerService =
        new RegisterService(
            mockEntityManager,
            mockAlertMechanism,
            RegisterServiceTest.CODE,
            hibernateUtil,
            clock,
            mockPasswordUtils);
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(mockCredintials).when(mockCustomer).getCredentials();
    doReturn("hash").when(mockCredintials).getHashedPassword();
    when(mockEntityManager.merge(mockCustomer)).thenReturn(mockMergedCustomer);
    doReturn(HASHED_PASSWORD).when(mockPasswordUtils).hashPassword(any());
  }

  @Before
  public void loggingForTests() {
    listAppender = loggerContextRule.getListAppender("List").clear();
  }

  @Test
  public void register_AsExpected() throws MessagingException {
    doReturn(UUID).when(mockDevice).getUuid();
    when(hibernateUtil.selectDeviceByUUID(UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbContactInformation).when(mockDbCustomer).getContactInformation();
    doReturn(PHONE).when(mockDbContactInformation).getRegisteredPhoneNumber();
    doReturn(mockMimeMessage).when(mockAlertMechanism).getMimeMessage();
    doNothing().when(mockAlertMechanism).send();

    registerService.register(mockCustomer);

    verify(mockAlertMechanism, times(1)).getMimeMessage();
    verify(mockMimeMessage, times(1)).setSubject(PHONE);
  }

  @Test
  public void register_NullCustomer_throwIllegalArgumentException() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The customer cannot be null");

    registerService.register(null);
  }

  @Test
  public void sendAlert_AsExpected() throws MessagingException {
    doReturn(mockMimeMessage).when(mockAlertMechanism).getMimeMessage();
    doNothing().when(mockAlertMechanism).send();

    registerService.sendAlert(PHONE);

    verify(mockAlertMechanism, times(1)).getMimeMessage();
    verify(mockMimeMessage, times(1)).setSubject(PHONE);
  }

  @Test
  public void sendAlert_MessagingException() throws MessagingException {
    doReturn(mockMimeMessage).when(mockAlertMechanism).getMimeMessage();
    doThrow(new MessagingException("exception message")).when(mockAlertMechanism).send();

    registerService.sendAlert(PHONE);

    List<LogEvent> events = listAppender.getEvents();
    assertEquals(1, events.size());
    LogEvent logEvent = events.get(0);
    assertEquals(Level.FATAL, logEvent.getLevel());
    assertEquals(
        "javax.mail.MessagingException: exception message", logEvent.getMessage().toString());
  }

  @Test
  public void getCustomerPhoneNumber_AsExpected() {
    doReturn(UUID).when(mockDevice).getUuid();
    when(hibernateUtil.selectDeviceByUUID(UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbContactInformation).when(mockDbCustomer).getContactInformation();
    doReturn(PHONE).when(mockDbContactInformation).getRegisteredPhoneNumber();

    assertEquals(PHONE, registerService.getCustomerPhoneNumber(mockCustomer));
  }

  @Test
  public void getCustomerPhoneNumber_NULL_UUID_ThrowsNoResultException() {
    doReturn(null).when(mockDevice).getUuid();
    String EXCEPTION_MESSAGE = "null uuid";
    when(hibernateUtil.selectDeviceByUUID(null))
        .thenThrow(new NoResultException(EXCEPTION_MESSAGE));

    thrown.expect(NoResultException.class);
    thrown.expectMessage(EXCEPTION_MESSAGE);
    registerService.getCustomerPhoneNumber(mockCustomer);
  }

  @Test
  public void registerCustomer_AsExpected() {
    registerService.registerCustomer(mockCustomer);

    verify(mockDevice, times(1)).setCode(CODE);
    verify(mockDevice, times(1)).setCodeCreationTimestamp(OffsetDateTime.now(clock));
    verify(mockCredintials, times(1)).setHashedPassword(HASHED_PASSWORD);
  }

  @Test
  public void registerCustomer_NullDevice_throwIllegalArgumentException() {
    doReturn(null).when(mockCustomer).getDevice();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The device cannot be null");

    registerService.registerCustomer(mockCustomer);
  }

  @Test
  public void registerCustomer_NullCredintials_throwIllegalArgumentException() {
    doReturn(null).when(mockCustomer).getCredentials();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The credentials cannot be null");

    registerService.registerCustomer(mockCustomer);
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */