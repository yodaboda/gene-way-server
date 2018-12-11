package com.nutrinfomics.geneway.server.alerts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class AlertsTest {

	private static final String CONFIG = "log4j-appender.xml";
	
    @ClassRule
    public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);
    
    private ListAppender listAppender;
    
	@Before
	public void setupLoggingForTests(){
        listAppender = loggerContextRule.getListAppender("List").clear();		
	}

	private Alerts alerts;
	
	@Test
	public void alerts_AsExpected() {
		alerts = new Alerts();
		List<LogEvent> events = listAppender.getEvents();
		assertEquals(4, events.size());
		String[] logMessages = new String[] {"Detection supported for the following languages:", 
												"he", "ar", "en"};
		for(int i = 0; i < 4; ++i) {
			LogEvent logEvent = events.get(i);
			assertEquals(Level.INFO, logEvent.getLevel());
			assertEquals(logMessages[i], logEvent.getMessage().getFormattedMessage());	
		}
	}
	
	@Test
	public void addAlert_AsExpected() {
		alerts = new Alerts();
		ScheduledAlert mockScheduledAlert = mock(ScheduledAlert.class);
		long key = 4;
		assertNull(alerts.addAlert(key, mockScheduledAlert));
	}

	@Test
	public void addAlert_nullAlert_AsExpected() {
		alerts = new Alerts();
		ScheduledAlert scheduledAlert = null;
		long key = 4;
		assertNull(alerts.addAlert(key, scheduledAlert));
	}

	@Test
	public void getAlert_AsExpected() {
		alerts = new Alerts();
		ScheduledAlert mockScheduledAlert = mock(ScheduledAlert.class);
		long key = 4;
		alerts.addAlert(key, mockScheduledAlert);
		assertEquals(mockScheduledAlert, alerts.getAlert(key));
		
	}

	@Test
	public void getAlert_nonExistingKey_null() {
		alerts = new Alerts();
		ScheduledAlert mockScheduledAlert = mock(ScheduledAlert.class);
		long key = 4;
		alerts.addAlert(key, mockScheduledAlert);
		assertEquals(null, alerts.getAlert((long)22));
		
	}

	@Test
	public void removeAlert_AsExpected() {
		alerts = new Alerts();
		ScheduledAlert mockScheduledAlert = mock(ScheduledAlert.class);
		long key = 4;
		alerts.addAlert(key, mockScheduledAlert);
		assertEquals(mockScheduledAlert, alerts.removeAlert(key));

	}

	@Test
	public void removeAlert_nonExistingAlert_null() {
		alerts = new Alerts();
		ScheduledAlert mockScheduledAlert = mock(ScheduledAlert.class);
		long key = 4;
		alerts.addAlert(key, mockScheduledAlert);
		assertEquals(null, alerts.removeAlert(6));

	}

}
