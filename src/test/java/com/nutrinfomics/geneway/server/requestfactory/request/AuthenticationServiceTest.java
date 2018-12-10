package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.inject.Provider;
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

import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.shared.AuthenticationException;

public class AuthenticationServiceTest {

	private static final String CONFIG = "log4j-appender.xml";	

    @ClassRule
    public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

	@Rule
    public ExpectedException thrown= ExpectedException.none();

	private final String SID = "22-4-8-14-3";
	private final String IDENTFIER_CODE = "449";
	private final String UUID = "170";
	private final String IP = "17.9.6.76";

	private AuthenticationService authenticationService;
	
	private ListAppender listAppender;
	
	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private HibernateUtil mockHibernateUtil;
	@Mock
	private EntityBase mockEntityBase;

	@Mock
	private Session mockSession;
	@Mock
	private Session mockDbSession;
	@Mock
	private Identifier mockIdentifier;
	@Mock
	private Identifier mockDbIdentifier;
	private Clock clock;
	@Mock
	private Utils mockUtils;
	
	@Before 
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		clock = Clock.fixed(Instant.EPOCH, ZoneId.of("Europe/Stockholm"));
		authenticationService = new AuthenticationService(mockEntityManagerProvider, 
														mockHibernateUtil, clock, mockUtils);
		setupMockEntityProvider();
		setupMockHibernateUtil();
	}

	private void setupMockHibernateUtil() {
		when(mockHibernateUtil.selectSession(SID, mockEntityManagerProvider)).thenReturn(mockDbSession);
		doReturn(SID).when(mockSession).getSid();
	}
	private void setupMockEntityProvider() {
		doReturn(mockEntityManager).when(mockEntityManagerProvider).get();
		when(mockEntityManager.merge(any())).thenReturn(null);
	}

	@Test
	public void unlock_True() {
		doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
		when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE, 
												mockEntityManagerProvider)).thenReturn(mockDbIdentifier);
		doReturn(UUID).when(mockDbIdentifier).getUuid();
		doReturn(UUID).when(mockIdentifier).getUuid();
		
		assertTrue(authenticationService.unlock(mockIdentifier));
	}

	@Test
	public void unlock_nullUUID_True() {
		doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
		when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE, 
												mockEntityManagerProvider)).thenReturn(mockDbIdentifier);
		doReturn(null).when(mockDbIdentifier).getUuid();
		doReturn(UUID).when(mockIdentifier).getUuid();
		
		assertTrue(authenticationService.unlock(mockIdentifier));
		
		verify(mockDbIdentifier, times(1)).setUuid(UUID);
	}

	@Test
	public void unlock_emptyUUID_True() {
		doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
		when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE, 
												mockEntityManagerProvider)).thenReturn(mockDbIdentifier);
		doReturn("").when(mockDbIdentifier).getUuid();
		doReturn(UUID).when(mockIdentifier).getUuid();
		
		assertTrue(authenticationService.unlock(mockIdentifier));
		
		verify(mockDbIdentifier, times(1)).setUuid(UUID);
	}

	@Test
	public void unlock_NoResultExpection_False() {
		doReturn(IDENTFIER_CODE).when(mockIdentifier).getIdentifierCode();
		String exceptionMessage = "No Result";
		when(mockHibernateUtil.selectIdentifier(IDENTFIER_CODE, 
												mockEntityManagerProvider)).thenThrow(new NoResultException(exceptionMessage));

		assertFalse(authenticationService.unlock(mockIdentifier));

		//TODO: re-enable log check
//		List<LogEvent> events = listAppender.getEvents();
//		assertEquals(1, events.size());
//		LogEvent logEvent = events.get(0);
//		assertEquals(Level.DEBUG, logEvent.getLevel());
//		assertEquals(exceptionMessage, logEvent.getMessage().toString());
	}

	
	@Test
	public void confirmValuationTermsOfService_AsExpected() {
		when(mockHibernateUtil.selectIdentifierFromUUID(UUID, mockEntityManagerProvider))
								.thenReturn(mockDbIdentifier);
		when(mockUtils.getIP()).thenReturn(IP);
		
		assertEquals(IP, authenticationService.confirmValuationTermsOfService(UUID));
		
		verify(mockDbIdentifier, times(1)).setEvaluationTermsAcceptanceIP(IP);
		verify(mockDbIdentifier, times(1)).setEvaluationTermsAcceptanceTime(Date.from(clock.instant()));
	}

	@Test
	public void confirmValuationTermsOfService_MissingUUID_NULL() {
		when(mockUtils.getIP()).thenReturn(IP);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("UUID " + UUID + " is not on record");
		authenticationService.confirmValuationTermsOfService(UUID);
	}

	@Test
	public void authenticateCode_AsExpected() throws AuthenticationException {
		String code = "170.144.22.17";

		Device mockDbDevice = mock(Device.class);
		when(mockHibernateUtil.selectDeviceByUUID(UUID, mockEntityManagerProvider))
								.thenReturn(mockDbDevice);
		LocalDateTime localDateTime = LocalDateTime.now(clock);
		doReturn(localDateTime).when(mockDbDevice).getCodeCreation();
		doReturn(code).when(mockDbDevice).getCode();
		
		Customer mockCustomer = mock(Customer.class);
		Device mockDevice = mock(Device.class);
		doReturn(mockDevice).when(mockCustomer).getDevice();
		doReturn(code).when(mockDevice).getCode();
		doReturn(UUID).when(mockDevice).getUuid();
		
		assertTrue(authenticationService.authenticateCode(mockCustomer));
	}

	@Test
	public void authenticateCode_expiredAuthentication_AuthenticationException() throws AuthenticationException {
		String code = "44.1.3.49.47";

		Device mockDbDevice = mock(Device.class);
		when(mockHibernateUtil.selectDeviceByUUID(UUID, mockEntityManagerProvider))
								.thenReturn(mockDbDevice);
		LocalDateTime localDateTime = LocalDateTime.now(clock);
		localDateTime = localDateTime.minusMinutes(45);
		doReturn(localDateTime).when(mockDbDevice).getCodeCreation();
		doReturn(code).when(mockDbDevice).getCode();
		
		Customer mockCustomer = mock(Customer.class);
		Device mockDevice = mock(Device.class);
		doReturn(mockDevice).when(mockCustomer).getDevice();
		doReturn(code).when(mockDevice).getCode();
		doReturn(UUID).when(mockDevice).getUuid();
		
		thrown.expect(AuthenticationException.class);
		thrown.expectMessage("EXPIRED");
		authenticationService.authenticateCode(mockCustomer);
	}

	@Test
	public void authenticateCode_codeMismatch_AsExpected() throws AuthenticationException {
		String code = "8.14.48";

		Device mockDbDevice = mock(Device.class);
		when(mockHibernateUtil.selectDeviceByUUID(UUID, mockEntityManagerProvider))
								.thenReturn(mockDbDevice);
		LocalDateTime localDateTime = LocalDateTime.now(clock);
		doReturn(localDateTime).when(mockDbDevice).getCodeCreation();
		doReturn(code + "9").when(mockDbDevice).getCode();
		
		Customer mockCustomer = mock(Customer.class);
		Device mockDevice = mock(Device.class);
		doReturn(mockDevice).when(mockCustomer).getDevice();
		doReturn(code).when(mockDevice).getCode();
		doReturn(UUID).when(mockDevice).getUuid();
		
		assertFalse(authenticationService.authenticateCode(mockCustomer));
	}

	
	@Test
	public void authenticateCustomer_AsExpected() {
		fail("Not yet implemented");
	}

	@Test
	public void authenticateSession_AsExpected() throws AuthenticationException {
		Customer mockDbCustomer = mock(Customer.class);
		Device mockDbDevice = mock(Device.class);
		
		doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
		doReturn(mockDbSession).when(mockDbCustomer).getSession();
		doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
		doReturn(UUID).when(mockDbDevice).getUuid();
		doReturn(SID).when(mockDbSession).getSid();
		
		Customer mockCustomer = mock(Customer.class);
		Device mockDevice = mock(Device.class);
		
		doReturn(mockCustomer).when(mockSession).getCustomer();
		doReturn(mockDevice).when(mockCustomer).getDevice();
		doReturn(UUID).when(mockDevice).getUuid();

		assertEquals(mockDbSession, authenticationService.authenticateSession(mockSession));
	}

	@Test
	public void authenticateSession_unequalUUID_ThrowException() throws AuthenticationException {
		Customer mockDbCustomer = mock(Customer.class);
		Device mockDbDevice = mock(Device.class);
		
		doReturn(mockDbCustomer).when(mockDbSession).getCustomer();
		doReturn(mockDbDevice).when(mockDbCustomer).getDevice();
		doReturn(UUID).when(mockDbDevice).getUuid();
		doReturn(SID).when(mockDbSession).getSid();
		
		Customer mockCustomer = mock(Customer.class);
		Device mockDevice = mock(Device.class);
		
		doReturn(mockCustomer).when(mockSession).getCustomer();
		doReturn(mockDevice).when(mockCustomer).getDevice();
		doReturn(UUID + "12").when(mockDevice).getUuid();

		thrown.expect(AuthenticationException.class);
		thrown.expectMessage("INVALID_SESSION");
		authenticationService.authenticateSession(mockSession);
	}

}
