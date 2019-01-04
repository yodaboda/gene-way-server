package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geneway.alerts.AlertLocalization;
import com.geneway.alerts.AlertMessage;
import com.geneway.alerts.AlertRecipient;
import com.geneway.alerts.AlertSpecification;
import com.geneway.alerts.AlertType;
import com.geneway.alerts.injection.AlertsModule;
import com.geneway.alerts.injection.testing.TestAlertsModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.contact.Email;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.requestfactory.request.TestGeneWayAlertsModule;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class GeneWayAlertsModuleTest {

  @Bind
  @Mock
  private @Named("dbSession") Session mockDbSession;

  @Bind private Locale locale = Locale.getDefault();
  @Bind @Mock private ResourceBundles mockResourceBundles;

  private Injector injector;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    injector =
        Guice.createInjector(
            Modules.override(new GeneWayAlertsModule(), new AlertsModule())
                .with(new TestGeneWayAlertsModule(), new TestAlertsModule()),
            BoundFieldModule.of(this));
  }

  @Test
  public void provideAlertSpecification_AsExpected() {

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
    String email = "transportation@cs.research.bio-medicine.math.food.net";
    doReturn(email).when(mockEmail).getEmail();
    emails.add(mockEmail);
    doReturn(emails).when(mockContactInformation).getEmails();

    String localizedSubject = GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT + locale;
    String[] body = new String[] {GeneWayAlertsModule.ALERT_MESSAGE_BODY};
    String localizedBody = body[0] + locale;
    when(mockResourceBundles.getGeneWayResource(GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT, locale))
        .thenReturn(localizedSubject);
    when(mockResourceBundles.getGeneWayResource(body[0], locale)).thenReturn(localizedBody);

    AlertSpecification alertLocalization = injector.getInstance(AlertSpecification.class);

    assertEquals(
        TestGeneWayAlertsModule.USER_NAME, alertLocalization.getAlertSender().getUserName());
    assertEquals(email, alertLocalization.getAlertRecipient().getRecipient());
    assertEquals(
        GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT,
        alertLocalization.getAlertMessage().getSubject());
    assertEquals(
        localizedSubject,
        alertLocalization
            .getAlertLocalization()
            .localizeSubject(GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT));
  }

  @Test
  public void provideAlertMessage_AsExpceted() {
    AlertMessage alertMessage = injector.getInstance(AlertMessage.class);
    assertEquals(GeneWayAlertsModule.ALERT_MESSAGE_SUBJECT, alertMessage.getSubject());
    assertArrayEquals(
        new String[] {GeneWayAlertsModule.ALERT_MESSAGE_BODY}, alertMessage.getBody());
  }

  @Test
  public void provideAlertRecipient_Phone_AsExpected() {
    Customer mockCustomer = mock(Customer.class);
    doReturn(mockCustomer).when(mockDbSession).getCustomer();
    Plan mockPlan = mock(Plan.class);
    doReturn(mockPlan).when(mockCustomer).getPlan();
    PlanPreferences mockPlanPreferences = mock(PlanPreferences.class);
    doReturn(mockPlanPreferences).when(mockPlan).getPlanPreferences();
    doReturn(false).when(mockPlanPreferences).isEmailAlerts();
    ContactInformation mockContactInformation = mock(ContactInformation.class);
    doReturn(mockContactInformation).when(mockCustomer).getContactInformation();
    List<Email> emails = new ArrayList<>();
    Email mockEmail = mock(Email.class);
    String email = "transportation@cs.research.bio-medicine.math.food.net";
    doReturn(email).when(mockEmail).getEmail();
    emails.add(mockEmail);
    doReturn(emails).when(mockContactInformation).getEmails();

    String phoneNumber = "170.4.12.69.25";
    doReturn(phoneNumber).when(mockContactInformation).getRegisteredPhoneNumber();

    AlertRecipient alertRecipient = injector.getInstance(AlertRecipient.class);
    assertEquals(phoneNumber, alertRecipient.getRecipient());
    assertEquals(AlertType.SMS, alertRecipient.getAlertType());
  }

  @Test
  public void provideAlertRecipient_Email_AsExpected() {
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
    String email = "transportation@cs.research.bio-medicine.math.food.net";
    doReturn(email).when(mockEmail).getEmail();
    emails.add(mockEmail);
    doReturn(emails).when(mockContactInformation).getEmails();

    String phoneNumber = "170.4.12.69.25";
    doReturn(phoneNumber).when(mockContactInformation).getRegisteredPhoneNumber();

    AlertRecipient alertRecipient = injector.getInstance(AlertRecipient.class);
    assertEquals(email, alertRecipient.getRecipient());
    assertEquals(AlertType.E_MAIL, alertRecipient.getAlertType());
  }

  @Test
  public void provideAlertRecipient_nullContactInformation_AsExpected() {
    Customer mockCustomer = mock(Customer.class);
    doReturn(mockCustomer).when(mockDbSession).getCustomer();
    Plan mockPlan = mock(Plan.class);
    doReturn(mockPlan).when(mockCustomer).getPlan();
    PlanPreferences mockPlanPreferences = mock(PlanPreferences.class);
    doReturn(mockPlanPreferences).when(mockPlan).getPlanPreferences();
    doReturn(true).when(mockPlanPreferences).isEmailAlerts();
    ContactInformation mockContactInformation = null;
    doReturn(mockContactInformation).when(mockCustomer).getContactInformation();

    AlertRecipient alertRecipient = injector.getInstance(AlertRecipient.class);
    assertEquals(null, alertRecipient.getRecipient());
    assertEquals(AlertType.E_MAIL, alertRecipient.getAlertType());
  }

  @Test
  public void provideAlertRecipient_emptyEmails_AsExpected() {
    Customer mockCustomer = mock(Customer.class);
    doReturn(mockCustomer).when(mockDbSession).getCustomer();
    Plan mockPlan = mock(Plan.class);
    doReturn(mockPlan).when(mockCustomer).getPlan();
    PlanPreferences mockPlanPreferences = mock(PlanPreferences.class);
    doReturn(mockPlanPreferences).when(mockPlan).getPlanPreferences();
    doReturn(false).when(mockPlanPreferences).isEmailAlerts();
    ContactInformation mockContactInformation = mock(ContactInformation.class);
    doReturn(mockContactInformation).when(mockCustomer).getContactInformation();
    List<Email> emails = new ArrayList<>();
    doReturn(emails).when(mockContactInformation).getEmails();

    String phoneNumber = "170.4.12.69.25";
    doReturn(phoneNumber).when(mockContactInformation).getRegisteredPhoneNumber();

    AlertRecipient alertRecipient = injector.getInstance(AlertRecipient.class);
    assertEquals(phoneNumber, alertRecipient.getRecipient());
    assertEquals(AlertType.SMS, alertRecipient.getAlertType());
  }

  @Test
  public void provideAlertRecipient_nullEmails_AsExpected() {
    Customer mockCustomer = mock(Customer.class);
    doReturn(mockCustomer).when(mockDbSession).getCustomer();
    Plan mockPlan = mock(Plan.class);
    doReturn(mockPlan).when(mockCustomer).getPlan();
    PlanPreferences mockPlanPreferences = mock(PlanPreferences.class);
    doReturn(mockPlanPreferences).when(mockPlan).getPlanPreferences();
    doReturn(true).when(mockPlanPreferences).isEmailAlerts();
    ContactInformation mockContactInformation = mock(ContactInformation.class);
    doReturn(mockContactInformation).when(mockCustomer).getContactInformation();
    List<Email> emails = null;
    doReturn(emails).when(mockContactInformation).getEmails();

    AlertRecipient alertRecipient = injector.getInstance(AlertRecipient.class);
    assertEquals(null, alertRecipient.getRecipient());
    assertEquals(AlertType.E_MAIL, alertRecipient.getAlertType());
  }

  @Test
  public void provideAlertLocalization_AsExpceted() {
    String subject = "hello";
    String localizedSubject = subject + locale;
    String[] body = new String[] {"healthy"};
    String localizedBody = body[0] + locale;
    when(mockResourceBundles.getGeneWayResource(subject, locale)).thenReturn(localizedSubject);
    when(mockResourceBundles.getGeneWayResource(body[0], locale)).thenReturn(localizedBody);

    AlertLocalization alertLocalization = injector.getInstance(AlertLocalization.class);
    assertEquals(locale, alertLocalization.getLocale());
    assertEquals(localizedSubject, alertLocalization.localizeSubject(subject));
    assertEquals(localizedBody + "\n\r https://gene-way.com", alertLocalization.localizeBody(body));
  }
}
