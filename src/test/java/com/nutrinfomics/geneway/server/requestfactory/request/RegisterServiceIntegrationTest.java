package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geneway.alerts.AlertLocalization;
import com.geneway.alerts.AlertMessage;
import com.geneway.alerts.AlertRecipient;
import com.geneway.alerts.AlertSender;
import com.geneway.alerts.AlertSpecification;
import com.geneway.alerts.AlertType;
import com.geneway.alerts.injection.AlertsModule;
import com.geneway.alerts.injection.testing.TestAlertsModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.requestfactory.GeneWayJPAModule;
import com.nutrinfomics.geneway.server.requestfactory.TestGeneWayJPAModule;

public class RegisterServiceIntegrationTest {

  private static final String CONFIG = "log4j-appender.xml";

  private final String SUBJECT = "subject";
  private final String BODY = "body";

  private static final String USER_NAME = "alertsUser";
  private static final String USER_EMAIL = USER_NAME + "@localhost";

  private static final String UUID = "uuid";

  private static final String PHONE = "44170";

  // TODO: Tests in RegisterServiceTest start failing when Logger tracker is enabled.
  //    @ClassRule
  //    public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);
  @Rule public ExpectedException thrown = ExpectedException.none();

  @Bind
  @Named("code")
  String code = "1224";

  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private Utils mockUtils;
  @Mock private RequestUtils mockRequestUtils;
  @Bind private Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

  // TODO: Eventually this should be provided from the GeneWayRequestFactoryModule.
  @Bind @Mock private AlertSpecification mockedAlertSpecification;

  @Mock private AlertMessage mockedAlertMessage;
  @Mock private AlertRecipient mockedAlertRecipient;
  @Mock private AlertLocalization mockedAlertLocalization;
  @Mock private AlertSender mockedAlertSender;

  @Mock private Device mockDbDevice;
  @Mock private Customer mockDbCustomer;
  @Mock private ContactInformation mockDbContactInformation;

  private Customer customer = new Customer();

  @Inject private RegisterService registerService;
  @Inject private PersistService service;
  private Injector injector;

  private ListAppender listAppender;

  @Before
  public void setUpJPA() {
    MockitoAnnotations.initMocks(this);

    setupAlert();

    setupCustomer();

    setupHibernate();

    injector =
        Guice.createInjector(
        		Modules.override(new GeneWayJPAModule()).with(new TestGeneWayJPAModule(mockHibernateUtil, mockUtils, mockRequestUtils)),
            Modules.override(new AlertsModule()).with(new TestAlertsModule()),
            BoundFieldModule.of(this));

    injector.injectMembers(this);
    service.start();
    injector.getInstance(EntityManager.class).getTransaction().begin();
  }
  // TODO: should be removed and hibernateUtil should not be mocked.
  private void setupHibernate() {
    when(mockHibernateUtil.selectDeviceByUUID(eq(UUID), any())).thenReturn(mockDbDevice);
    doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
    doReturn(mockDbContactInformation).when(mockDbCustomer).getContactInformation();
    doReturn(PHONE).when(mockDbContactInformation).getRegisteredPhoneNumber();
  }
  // TODO: init database with customer details to be retrieved.
  private void setupCustomer() {
    Device device = new Device();
    device.setUuid("uuid");
    Credentials credentials = new Credentials();

    customer.setDevice(device);
    customer.setCredentials(credentials);
    customer.setNickName("not blank");
  }

  private void setupAlert() {
    String[] bodyStrings = new String[] {BODY};

    when(mockedAlertSpecification.getAlertLocalization()).thenReturn(mockedAlertLocalization);
    when(mockedAlertSpecification.getAlertMessage()).thenReturn(mockedAlertMessage);
    when(mockedAlertSpecification.getAlertRecipient()).thenReturn(mockedAlertRecipient);
    when(mockedAlertSpecification.getAlertSender()).thenReturn(mockedAlertSender);

    when(mockedAlertRecipient.getAlertType()).thenReturn(AlertType.SMS);
    when(mockedAlertRecipient.getRecipient()).thenReturn("to be overriden");
    when(mockedAlertMessage.getBody()).thenReturn(bodyStrings);
    when(mockedAlertMessage.getSubject()).thenReturn(SUBJECT);
    when(mockedAlertLocalization.localizeSubject(SUBJECT)).thenReturn("subject");
    when(mockedAlertLocalization.localizeBody(bodyStrings)).thenReturn("body");
    when(mockedAlertLocalization.getLocale()).thenReturn(Locale.forLanguageTag("ar"));
    doReturn(USER_NAME).when(mockedAlertSender).getUserName();
    doReturn("123456").when(mockedAlertSender).getPassword();
    doReturn(TestAlertsModule.LOCALHOST).when(mockedAlertSender).getHost();
    doReturn(USER_EMAIL).when(mockedAlertSender).getEmail();

    TestAlertsModule.MAIL_SERVER.start();
    TestAlertsModule.MAIL_SERVER.setUser(
        mockedAlertSender.getHost(),
        mockedAlertSender.getUserName(),
        mockedAlertSender.getPassword());
  }

  @After
  public void tearDownTransaction() {
    EntityManager entityManager = injector.getInstance(EntityManager.class);
    entityManager.close();
    service.stop();
    TestAlertsModule.MAIL_SERVER.stop();
  }

  private void rollbackTransaction() {
    injector.getInstance(EntityManager.class).getTransaction().rollback();
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
    assertTrue(String.valueOf(m.getContent()).contains(BODY));
    assertEquals(AlertsModule.SMS_RECIPIENT_EMAIL_ADDRESS, m.getAllRecipients()[0].toString());
  }

  @Test
  public void registerCustomer_AsExpected() {
    // TODO: check why rolling back is not working?
    registerService.registerCustomer(customer);

    rollbackTransaction();
  }
}
