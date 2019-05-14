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

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

import com.nutrinfomics.geneway.server.PasswordUtils;
import com.nutrinfomics.geneway.server.UUIDGenerator;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.shared.AuthenticationException;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class AuthenticationServiceTest {

  private static final String CONFIG = "log4j-appender.xml";

  @ClassRule public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

  @Rule public ExpectedException thrown = ExpectedException.none();

  private final String SID = "22-4-8-14-3";
  private final String IDENTFIER_CODE = "449";
  private final String CLIENT_UUID = "170";
  private final String IP = "17.9.6.76";

  private AuthenticationService authenticationService;

  private ListAppender listAppender;

  @Mock private EntityManager mockEntityManager;
  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private EntityBase mockEntityBase;

  @Mock private Session mockSession;
  @Mock private Session mockDbSession;
  @Mock private Identifier mockIdentifier;
  @Mock private Identifier mockDbIdentifier;
  private Clock clock;
  @Mock private Utils mockUtils;
  @Mock private UUIDGenerator mockUuidGenerator;
  @Mock private PasswordUtils mockPasswordUtils;

  private final UUID RANDOM_UUID = UUID.fromString("3dd4fa6e-2899-4429-b820-d34fe8df5d22");

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    clock = Clock.fixed(Instant.EPOCH, ZoneId.of("Europe/Stockholm"));
    authenticationService =
        new AuthenticationService(
            mockEntityManager,
            mockHibernateUtil,
            clock,
            mockUtils,
            mockUuidGenerator,
            mockPasswordUtils);
    setupMockEntityProvider();
    setupMockHibernateUtil();
    doReturn(RANDOM_UUID).when(mockUuidGenerator).randomUUID();
  }

  private void setupMockHibernateUtil() {
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);
    doReturn(SID).when(mockSession).getSid();
  }

  private void setupMockEntityProvider() {
    when(mockEntityManager.merge(any())).thenReturn(null);
  }

  @Test
  public void unlock_True() {
    doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
    when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE)).thenReturn(mockDbIdentifier);
    doReturn(CLIENT_UUID).when(mockDbIdentifier).getUuid();
    doReturn(CLIENT_UUID).when(mockIdentifier).getUuid();

    assertTrue(authenticationService.unlock(mockIdentifier));
  }

  @Test
  public void unlock_nullUUID_True() {
    doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
    when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE)).thenReturn(mockDbIdentifier);
    doReturn(null).when(mockDbIdentifier).getUuid();
    doReturn(CLIENT_UUID).when(mockIdentifier).getUuid();

    assertTrue(authenticationService.unlock(mockIdentifier));

    verify(mockDbIdentifier, times(1)).setUuid(CLIENT_UUID);
  }

  @Test
  public void unlock_emptyUUID_True() {
    doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
    when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE)).thenReturn(mockDbIdentifier);
    doReturn("").when(mockDbIdentifier).getUuid();
    doReturn(CLIENT_UUID).when(mockIdentifier).getUuid();

    assertTrue(authenticationService.unlock(mockIdentifier));

    verify(mockDbIdentifier, times(1)).setUuid(CLIENT_UUID);
  }

  @Test
  public void unlock_NoResultExpection_False() {
    doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
    String exceptionMessage = "No Result";
    when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE))
        .thenThrow(new NoResultException(exceptionMessage));

    assertFalse(authenticationService.unlock(mockIdentifier));

    // TODO: re-enable log check
    //		List<LogEvent> events = listAppender.getEvents();
    //		assertEquals(1, events.size());
    //		LogEvent logEvent = events.get(0);
    //		assertEquals(Level.DEBUG, logEvent.getLevel());
    //		assertEquals(exceptionMessage, logEvent.getMessage().toString());
  }

  @Test
  public void confirmValuationTermsOfService_AsExpected() {
    when(mockHibernateUtil.selectIdentifierFromUUID(CLIENT_UUID)).thenReturn(mockDbIdentifier);
    when(mockUtils.getIP()).thenReturn(IP);

    assertEquals(IP, authenticationService.confirmValuationTermsOfService(CLIENT_UUID));

    verify(mockDbIdentifier, times(1)).setEvaluationTermsAcceptanceIP(IP);
    verify(mockDbIdentifier, times(1)).setEvaluationTermsAcceptanceTime(Date.from(clock.instant()));
  }

  @Test
  public void confirmValuationTermsOfService_MissingUUID_NULL() {
    when(mockUtils.getIP()).thenReturn(IP);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("UUID " + CLIENT_UUID + " is not on record");
    authenticationService.confirmValuationTermsOfService(CLIENT_UUID);
  }

  @Test
  public void authenticateCode_AsExpected() throws AuthenticationException {
    String code = "170.144.22.17";

    Device mockDbDevice = mock(Device.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    OffsetDateTime offsetDateTime = OffsetDateTime.now(clock);
    doReturn(offsetDateTime).when(mockDbDevice).getCodeCreationTimestamp();
    doReturn(code).when(mockDbDevice).getCode();

    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(code).when(mockDevice).getCode();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();

    assertTrue(authenticationService.authenticateCode(mockCustomer));
  }

  @Test
  public void authenticateCode_expiredAuthentication_AuthenticationException()
      throws AuthenticationException {
    String code = "44.1.3.49.47";

    Device mockDbDevice = mock(Device.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    OffsetDateTime offsetDateTime = OffsetDateTime.now(clock);
    offsetDateTime = offsetDateTime.minusMinutes(45);
    doReturn(offsetDateTime).when(mockDbDevice).getCodeCreationTimestamp();
    doReturn(code).when(mockDbDevice).getCode();

    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(code).when(mockDevice).getCode();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();

    thrown.expect(AuthenticationException.class);
    thrown.expectMessage("EXPIRED");
    authenticationService.authenticateCode(mockCustomer);
  }

  @Test
  public void authenticateCode_codeMismatch_AsExpected() throws AuthenticationException {
    String code = "8.14.48";

    Device mockDbDevice = mock(Device.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    OffsetDateTime offsetDateTime = OffsetDateTime.now(clock);
    doReturn(offsetDateTime).when(mockDbDevice).getCodeCreationTimestamp();
    doReturn(code + "9").when(mockDbDevice).getCode();

    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(code).when(mockDevice).getCode();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();

    assertFalse(authenticationService.authenticateCode(mockCustomer));
  }

  @Test
  public void authenticateCustomer_AsExpected() throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbSession).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(password).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, password)).thenReturn(true);
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    assertEquals(mockDbSession, authenticationService.authenticateCustomer(mockCustomer));
  }

  @Test
  public void authenticateCustomer_invalidUUID_ThrowsException() throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenThrow(new NoResultException());
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbSession).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(password).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, password)).thenReturn(true);
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    thrown.expect(AuthenticationException.class);
    thrown.expectMessage("INVALID_DEVICE_UUID");
    authenticationService.authenticateCustomer(mockCustomer);
  }

  @Test
  public void authenticateCustomer_nullDbSession_AsExpected() throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(null).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(password).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, password)).thenReturn(true);
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    assertEquals(null, authenticationService.authenticateCustomer(mockCustomer));
  }

  @Test
  public void authenticateCustomer_invalidDevice_ThrowsException() throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID + "737").when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbSession).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(password).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, password)).thenReturn(true);
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    thrown.expect(AuthenticationException.class);
    thrown.expectMessage("INVALID_DEVICE_UUID");
    authenticationService.authenticateCustomer(mockCustomer);
  }

  @Test
  public void authenticateCustomer_invalidPassword_ThrowsException()
      throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    String dBPassword = "";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(null).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(dBPassword).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, dBPassword)).thenReturn(false);
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    thrown.expect(AuthenticationException.class);
    thrown.expectMessage("INVALID_PASSWORD");
    authenticationService.authenticateCustomer(mockCustomer);
  }

  @Test
  public void authenticateCustomer_nullDbDevice_AsExpected() throws AuthenticationException {
    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);
    Credentials mockCredentials = mock(Credentials.class);
    String password = "random";
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();
    doReturn(mockCredentials).when(mockCustomer).getCredentials();
    doReturn(password).when(mockCredentials).getPassword();

    Device mockDbDevice = mock(Device.class);
    Customer mockDbCustomer = mock(Customer.class);
    Credentials mockDbCredentials = mock(Credentials.class);
    when(mockHibernateUtil.selectDeviceByUUID(CLIENT_UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbSession).when(mockDbCustomer).getSession();
    doReturn(mockDbCredentials).when(mockDbCustomer).getCredentials();
    doReturn(password).when(mockDbCredentials).getPassword();
    when(mockPasswordUtils.checkPassword(password, password)).thenReturn(true);
    doReturn(null).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(null).when(mockDbDevice).getCode();
    doReturn(null).when(mockDbDevice).getCodeCreationTimestamp();

    doNothing().when(mockEntityManager).persist(any());

    assertEquals(mockDbSession, authenticationService.authenticateCustomer(mockCustomer));
  }

  @Test
  public void authenticateSession_AsExpected() throws AuthenticationException {
    Customer mockDbCustomer = mock(Customer.class);
    Device mockDbDevice = mock(Device.class);

    doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
    doReturn(mockDbSession).when(mockDbCustomer).getSession();
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(SID).when(mockDbSession).getSid();

    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);

    doReturn(mockCustomer).when(mockSession).getCustomer();
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDevice).getUuid();

    assertEquals(mockDbSession, authenticationService.authenticateSession(mockSession));
  }

  @Test
  public void authenticateSession_unequalUUID_ThrowException() throws AuthenticationException {
    Customer mockDbCustomer = mock(Customer.class);
    Device mockDbDevice = mock(Device.class);

    doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
    doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
    doReturn(CLIENT_UUID).when(mockDbDevice).getUuid();
    doReturn(SID).when(mockDbSession).getSid();

    Customer mockCustomer = mock(Customer.class);
    Device mockDevice = mock(Device.class);

    doReturn(mockCustomer).when(mockSession).getCustomer();
    doReturn(mockDevice).when(mockCustomer).getDevice();
    doReturn(CLIENT_UUID + "12").when(mockDevice).getUuid();

    thrown.expect(AuthenticationException.class);
    thrown.expectMessage("INVALID_SESSION");
    authenticationService.authenticateSession(mockSession);
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */