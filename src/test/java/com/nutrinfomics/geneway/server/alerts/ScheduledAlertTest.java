package com.nutrinfomics.geneway.server.alerts;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class ScheduledAlertTest {

  private static final String CONFIG = "log4j-appender.xml";

  @ClassRule public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

  private ListAppender listAppender;

  @Before
  public void setupLoggingForTests() {
    listAppender = loggerContextRule.getListAppender("List").clear();
  }

  @Mock private Alert mockAlert;
  @Mock private ScheduledExecutorService mockScheduledService;
  @Mock private ScheduledFuture<?> mockScheduled;

  private ScheduledAlert scheduledAlert;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    scheduledAlert = new ScheduledAlert(mockAlert, mockScheduledService);
  }

  @Test
  public void schedule_AsExpected() {
    int delayInHours = 6;
    doReturn(mockScheduled)
        .when(mockScheduledService)
        .schedule(any(Runnable.class), eq((long) (delayInHours * 60)), eq(TimeUnit.MINUTES));
    scheduledAlert.schedule(delayInHours);
    verify(mockScheduledService, times(1))
        .schedule(any(Runnable.class), eq((long) (delayInHours * 60)), eq(TimeUnit.MINUTES));
  }

  @Test
  public void cancel_AsExpected() {
    int delayInHours = 6;
    doReturn(mockScheduled)
        .when(mockScheduledService)
        .schedule(any(Runnable.class), eq((long) (delayInHours * 60)), eq(TimeUnit.MINUTES));
    scheduledAlert.schedule(delayInHours);

    scheduledAlert.cancel();
    verify(mockScheduled, times(1)).cancel(false);
  }

  @Test
  public void cancel_nullScheduled_Log() {
    scheduledAlert.cancel();

    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.INFO, logEvent.getLevel());
    assertEquals(
        "Attempting to cancel a null scheduled field", logEvent.getMessage().getFormattedMessage());
  }

  @Test
  public void remind_AsExpected() {
    doNothing().when(mockAlert).remind();
    scheduledAlert.remind();
    verify(mockAlert, times(1)).remind();
  }
}
