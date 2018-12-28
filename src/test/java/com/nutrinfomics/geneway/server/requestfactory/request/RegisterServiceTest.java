package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
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

public class RegisterServiceTest {

	private static final String CONFIG = "log4j-appender.xml";

	@ClassRule
	public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String UUID = "UUID";
	private static final String PHONE = "44.42.41.43";
	private static final String CODE = "1224";
	private static final String HASHED_PASSWORD = "424";

	private ListAppender listAppender;

	@Mock
	private EmailAlertMechanism mockAlertMechanism;
	@Mock
	private HibernateUtil hibernateUtil;

	@Mock
	private Customer mockCustomer;
	@Mock
	private Customer mockMergedCustomer;
	@Mock
	private Device mockDevice;
	@Mock
	private Credentials mockCredintials;
	@Mock
	private Device mockDbDevice;
	@Mock
	private Customer mockDbCustomer;
	@Mock
	private ContactInformation mockDbContactInformation;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private MimeMessage mockMimeMessage;
	private Clock clock;
	@Mock
	private PasswordUtils mockPasswordUtils;

	private RegisterService registerService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
		registerService = new RegisterService(mockEntityManager, mockAlertMechanism, RegisterServiceTest.CODE,
				hibernateUtil, clock, mockPasswordUtils);
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
		when(hibernateUtil.selectDeviceByUUID(UUID, mockEntityManager)).thenReturn(mockDbDevice);
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
		assertEquals("javax.mail.MessagingException: exception message", logEvent.getMessage().toString());
	}

	@Test
	public void getCustomerPhoneNumber_AsExpected() {
		doReturn(UUID).when(mockDevice).getUuid();
		when(hibernateUtil.selectDeviceByUUID(UUID, mockEntityManager)).thenReturn(mockDbDevice);
		doReturn(mockDbCustomer).when(mockDbDevice).getCustomer();
		doReturn(mockDbContactInformation).when(mockDbCustomer).getContactInformation();
		doReturn(PHONE).when(mockDbContactInformation).getRegisteredPhoneNumber();

		assertEquals(PHONE, registerService.getCustomerPhoneNumber(mockCustomer));
	}

	@Test
	public void getCustomerPhoneNumber_NULL_UUID_ThrowsNoResultException() {
		doReturn(null).when(mockDevice).getUuid();
		String EXCEPTION_MESSAGE = "null uuid";
		when(hibernateUtil.selectDeviceByUUID(eq(null), any())).thenThrow(new NoResultException(EXCEPTION_MESSAGE));

		thrown.expect(NoResultException.class);
		thrown.expectMessage(EXCEPTION_MESSAGE);
		registerService.getCustomerPhoneNumber(mockCustomer);
	}

	@Test
	public void registerCustomer_AsExpected() {
		registerService.registerCustomer(mockCustomer);

		verify(mockDevice, times(1)).setCode(CODE);
		verify(mockDevice, times(1)).setCodeCreation(LocalDateTime.now(clock));
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
