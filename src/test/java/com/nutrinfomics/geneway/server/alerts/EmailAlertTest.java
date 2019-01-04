package com.nutrinfomics.geneway.server.alerts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.geneway.alerts.AlertMechanism;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class EmailAlertTest {

  private static final String CONFIG = "log4j-appender.xml";

  @ClassRule public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

  private ListAppender listAppender;

  @Before
  public void setupLoggingForTests() {
    listAppender = loggerContextRule.getListAppender("List").clear();
  }

  @Test
  public void testRemindAddressException() throws AddressException, MessagingException {
    AlertMechanism mockedAlertMechanism = mock(AlertMechanism.class);
    doThrow(new AddressException("wrong address")).when(mockedAlertMechanism).send();
    EmailAlert emailAlert = new EmailAlert(mockedAlertMechanism);

    emailAlert.remind();

    List<LogEvent> events = listAppender.getEvents();
    assertEquals(1, events.size());
    LogEvent logEvent = events.get(0);
    assertEquals(Level.FATAL, logEvent.getLevel());
    assertEquals(
        "javax.mail.internet.AddressException: wrong address", logEvent.getMessage().toString());
  }

  @Test
  public void testRemindMessagingException() throws AddressException, MessagingException {
    AlertMechanism mockedAlertMechanism = mock(AlertMechanism.class);
    doThrow(new MessagingException("messaging exception")).when(mockedAlertMechanism).send();
    EmailAlert emailAlert = new EmailAlert(mockedAlertMechanism);

    emailAlert.remind();

    List<LogEvent> events = listAppender.getEvents();
    assertEquals(1, events.size());
    LogEvent logEvent = events.get(0);
    assertEquals(Level.FATAL, logEvent.getLevel());
    assertEquals(
        "javax.mail.MessagingException: messaging exception", logEvent.getMessage().toString());
  }
}
