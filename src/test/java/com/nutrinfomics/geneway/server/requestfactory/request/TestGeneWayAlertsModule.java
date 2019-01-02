package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.geneway.alerts.AlertSender;
import com.geneway.alerts.injection.testing.TestAlertsModule;
import com.google.inject.Scopes;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.requestfactory.GeneWayAlertsModule;

public class TestGeneWayAlertsModule extends GeneWayAlertsModule {

  public static final String USER_NAME = "alertsUser";
  public static final char[] PASSWORD = "123456".toCharArray();
  public static final String LOCALHOST = TestAlertsModule.LOCALHOST;
  public static final String USER_EMAIL = USER_NAME + "@localhost";

  public static AlertSender mockAlertSender = mock(AlertSender.class);

  public TestGeneWayAlertsModule() {
    doReturn(USER_NAME).when(TestGeneWayAlertsModule.mockAlertSender).getUserName();
    doReturn(PASSWORD).when(TestGeneWayAlertsModule.mockAlertSender).getPassword();
    doReturn(LOCALHOST).when(TestGeneWayAlertsModule.mockAlertSender).getHost();
    doReturn(USER_EMAIL).when(TestGeneWayAlertsModule.mockAlertSender).getEmail();
  }

  @Override
  protected void configure() {
    // TODO: Figure out a way to deal with
    // No scope is bound to com.google.inject.servlet.RequestScoped
    bindScope(RequestScoped.class, Scopes.SINGLETON);

    bind(AlertSender.class).toInstance(mockAlertSender);
  }
}
