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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */