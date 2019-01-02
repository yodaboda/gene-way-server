package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geneway.alerts.injection.AlertsModule;
import com.geneway.alerts.injection.testing.TestAlertsModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nutrinfomics.geneway.server.PasswordUtils;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.contact.Email;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.requestfactory.GeneWayAlertsModule;
import com.nutrinfomics.geneway.server.requestfactory.GeneWayJPAModule;
import com.nutrinfomics.geneway.server.requestfactory.TestGeneWayJPAModule;

public class RegisterServiceIntegrationTest {

  private static final String CUSTOMER_NICK_NAME = "not blank";

  private static final String CONFIG = "log4j-appender.xml";

  private final String[] BODY = new String[] {GeneWayAlertsModule.ALERT_MESSAGE_BODY};
  private final String LOCALIZED_BODY = BODY[0] + "locale";
  private final String EMAIL = "transportation@cs.research.bio-medicine.math.food.net";

  private static final String UUID = "uuid";

  private static final String PHONE = "44170";

  private static final String PASSWORD = "8 + 14 = 22";

  private final String SID = "coffee";

  // TODO: Tests in RegisterServiceTest start failing when Logger tracker is
  // enabled.
  // @ClassRule
  // public static LoggerContextRule loggerContextRule = new
  // LoggerContextRule(CONFIG);
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Bind
  @Named("code")
  private String code = "1224";

  @Bind @Mock private ResourceBundles mockResourceBundles;

  @Mock private Session mockDbSession;
  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private Utils mockUtils;
  @Mock private RequestUtils mockRequestUtils;
  @Bind private Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
  @Bind @Mock private SecureRandom mockSecureRandom;
  @Bind @Mock private Session mockClientSession;

  @Mock private Device mockDbDevice;
  @Mock private Customer mockDbCustomer;
  @Mock private ContactInformation mockDbContactInformation;

  private Customer customer = new Customer();
  private Device device = new Device();
  private Credentials credentials = new Credentials();

  @Inject private RegisterService registerService;
  @Inject private PasswordUtils passwordUtis;
  @Inject private Locale locale;

  private Injector injector;

  private ListAppender listAppender;

  private PersistService service;
  @Inject private EntityManager entityManager;
  private EntityTransaction entityTransaction;

  @Before
  public void setUpJPA() {
    MockitoAnnotations.initMocks(this);

    doAnswer(
            invocation -> {
              Object[] args = invocation.getArguments();
              byte[] bytes = ((byte[]) args[0]);
              for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = 1;
              }
              return null;
            })
        .when(mockSecureRandom)
        .nextBytes(any());

    setupHibernate();

    injector =
        Guice.createInjector(
            Modules.override(new GeneWayJPAModule(), new GeneWayAlertsModule())
                .with(
                    new TestGeneWayJPAModule(mockHibernateUtil, mockUtils, mockRequestUtils),
                    new TestGeneWayAlertsModule()),
            Modules.override(new AlertsModule()).with(new TestAlertsModule()),
            BoundFieldModule.of(this));

    service = injector.getInstance(PersistService.class);
    service.start();

    injector.injectMembers(this);

    entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();

    setupCustomer();

    setupAlert();
  }

  // TODO: should be removed and hibernateUtil should not be mocked.
  private void setupHibernate() {
    when(mockHibernateUtil.selectDeviceByUUID(UUID)).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbContactInformation).when(mockDbCustomer).getContactInformation();
    doReturn(PHONE).when(mockDbContactInformation).getRegisteredPhoneNumber();

    doReturn(SID).when(mockClientSession).getSid();
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);
    Locale locale = Locale.getDefault();
    doReturn(locale).when(mockUtils).getLocale();

    Customer mockCustomer = mock(Customer.class);
    doReturn(mockCustomer).when(mockDbSession).getCustomer();
    Plan mockPlan = mock(Plan.class);
    doReturn(mockPlan).when(mockCustomer).getPlan();
    PlanPreferences mockPlanPreferences = mock(PlanPreferences.class);
    doReturn(mockPlanPreferences).when(mockPlan).getPlanPreferences();
    doReturn(true).when(mockPlanPreferences).isEmailAlerts();
    ContactInformation mockContactInformation = mock(ContactInformation.class);
    doReturn(mockContactInformation).when(mockCustomer).getContactInformation();
    List<Email> emails = new ArrayList<>();
    Email mockEmail = mock(Email.class);

    doReturn(EMAIL).when(mockEmail).getEmail();
    emails.add(mockEmail);
    doReturn(emails).when(mockContactInformation).getEmails();

    String localizedSubject = GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT + locale;
    when(mockResourceBundles.getGeneWayResource(GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT, locale))
        .thenReturn(localizedSubject);
    when(mockResourceBundles.getGeneWayResource(BODY[0], locale)).thenReturn(LOCALIZED_BODY);
  }

  private void setupCustomer() {
    customer.setDevice(device);
    device.setUuid(UUID);
    customer.setCredentials(credentials);
    customer.setNickName(CUSTOMER_NICK_NAME);
    credentials.setPassword(PASSWORD);
  }

  private void setupAlert() {

    TestAlertsModule.MAIL_SERVER.start();
    TestAlertsModule.MAIL_SERVER.setUser(
        TestGeneWayAlertsModule.mockAlertSender.getHost(),
        TestGeneWayAlertsModule.mockAlertSender.getUserName(),
        Arrays.toString(TestGeneWayAlertsModule.mockAlertSender.getPassword()));
  }

  @After
  public void tearDownTransaction() {
    entityManager.close();
    service.stop();
    TestAlertsModule.MAIL_SERVER.stop();
  }

  private void rollbackTransaction() {
    entityTransaction.rollback();
  }

  @Test
  public void register_AsExpected() {
    // TODO: store dbDevice in database to be retrieved by the register operation.
    Device dbDevice = new Device();
    Customer dbCustomer = new Customer();
    dbDevice.setCustomer(dbCustomer);
    dbDevice.setUuid("uuid");
    dbCustomer.setDevice(dbDevice);

    registerService.register(customer);

    rollbackTransaction();
  }

  @Test
  public void sendAlert_AsExpected() throws MessagingException, IOException {
    String phone = "4442414322";
    registerService.sendAlert(phone);

    MimeMessage[] messages = TestAlertsModule.MAIL_SERVER.getReceivedMessages();
    assertNotNull(messages);
    assertEquals(1, messages.length);
    MimeMessage m = messages[0];
    assertEquals(phone, m.getSubject());
    assertTrue(String.valueOf(m.getContent()).contains(LOCALIZED_BODY));
    assertEquals(EMAIL, m.getAllRecipients()[0].toString());
  }

  @Test
  public void registerCustomer_AsExpected() {
    registerService.registerCustomer(customer);

    List<Customer> customers =
        entityManager.createQuery("Select c from Customer c", Customer.class).getResultList();

    assertEquals(1, customers.size());
    Customer emCustomer = customers.get(0);
    Device emDevice = emCustomer.getDevice();
    Credentials emCredentials = emCustomer.getCredentials();
    assertEquals(code, emDevice.getCode());
    assertTrue(passwordUtis.checkHashedPassword(PASSWORD, emCredentials.getHashedPassword()));

    rollbackTransaction();
  }

  @Test
  public void registerCustomer_NullDevice_Exception() {
    customer.setDevice(null);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(RegisterService.EXCEPTION_MESSAGE_NULL_DEVICE);
    registerService.registerCustomer(customer);
  }

  @Test
  public void registerCustomer_NullCredentials_Exception() {
    customer.setCredentials(null);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(RegisterService.EXCEPTION_MESSAGE_NULL_CREDENTIALS);
    registerService.registerCustomer(customer);
  }

  @Test
  public void getCustomerPhoneNumber_AsExpected() {
    assertEquals(PHONE, registerService.getCustomerPhoneNumber(customer));
    // Read only operation - no need to roll-back.
  }
}
