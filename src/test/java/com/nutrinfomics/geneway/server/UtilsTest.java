package com.nutrinfomics.geneway.server;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.apache.logging.log4j.core.appender.AppenderSet;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

import static org.mockito.Mockito.*;

public class UtilsTest {

	private static final String CONFIG = "log4j-appender.xml";
	
    @ClassRule
    public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);
    
    private ListAppender listAppender;
    
	@Before
	public void setupLoggingForTests(){
        listAppender = loggerContextRule.getListAppender("List").clear();		
	}

	/**
	 * Build random number of cookies.
	 * @param withGwtLocale if true one of the cookies will have name "gwtLocale"
	 * @param withGwtLocaleValue if true the cookie with name with "gwtLocale" will have a legal locale string value
	 * @return a random number of cookies with one that might have <code> gwtLocale </code> as its name
	 */
	private Cookie[] buildCookies(boolean withGwtLocale, String gwtLocaleValue){
		Random rand = new Random();
		int cookieNum = rand.nextInt(10);
		Cookie[] cookies = new Cookie[cookieNum + (withGwtLocale ? 1 : 0)];
		
		for(int i = 0; i < cookieNum; ++i){
			Cookie mockedCookie = buildCookie("name" + i, "val" + i);
			cookies[i] = mockedCookie;
		}
		
		if(withGwtLocale){
			cookies[cookieNum] = buildCookie("gwtLocale", gwtLocaleValue);
		}
		
		return cookies;
	}

	/**
	 * builds a mock cookie with specified name and value
	 * @param name of the cookie
	 * @param value of the cookie
	 * @return a cookie with specified name and value
	 */
	private Cookie buildCookie(String name, String value) {
		Cookie mockedCookie = mock(Cookie.class);
		when(mockedCookie.getName()).thenReturn(name);
		when(mockedCookie.getValue()).thenReturn(value);
		return mockedCookie;
	}
	
	@Test
	public void testGetLocale() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		Cookie[] cookies = buildCookies(true, "ar");
		when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
				
		assertEquals(Locale.forLanguageTag("ar"), new Utils().getLocale(mockedRequestUtils));
		List<LogEvent> events = listAppender.getEvents();
		assertEquals(0, events.size());
	}

	@Test
	public void testGetLocaleNoGwtCookie() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		Cookie[] cookies = buildCookies(false, null);
		when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
		
		assertEquals(Locale.ENGLISH, new Utils().getLocale(mockedRequestUtils));
		List<LogEvent> events = listAppender.getEvents();
		assertEquals(1, events.size());
		LogEvent logEvent = events.get(0);
		assertEquals(Level.WARN, logEvent.getLevel());
		assertEquals("Http Request has no gwtLocale cookie!", logEvent.getMessage().toString());
	}

	@Test
	public void testGetLocaleNullCookies() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		when(mockedHttpServletRequest.getCookies()).thenReturn(null);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
		
		assertEquals(Locale.ENGLISH, new Utils().getLocale(mockedRequestUtils));
		List<LogEvent> events = listAppender.getEvents();
		LogEvent logEvent = events.get(0);
		assertEquals(Level.WARN, logEvent.getLevel());
		assertEquals("Http Request has null cookies!", logEvent.getMessage().toString());
	}

	@Test
	public void testGetLocaleNullHttpServerRequest() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(null);
		
		assertEquals(Locale.ENGLISH, new Utils().getLocale(mockedRequestUtils));
		List<LogEvent> events = listAppender.getEvents();
		LogEvent logEvent = events.get(0);
		assertEquals(Level.WARN, logEvent.getLevel());
		assertEquals("Null Http Request!", logEvent.getMessage().toString());
	}
	
	@Test
	public void testGetLocaleNullGwtLocaleValue() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		Cookie[] cookies = buildCookies(true, null);
		when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
		
		assertEquals(Locale.ENGLISH, new Utils().getLocale(mockedRequestUtils));
		
		List<LogEvent> events = listAppender.getEvents();
		LogEvent logEvent = events.get(0);
		assertEquals(Level.WARN, logEvent.getLevel());
		assertEquals("Null gwtLocale cookie value!", logEvent.getMessage().toString());

	}

	@Test
	public void testGetLocaleIllegalGwtLocaleValue() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		Cookie[] cookies = buildCookies(true, "arrrr");
		when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
		
		assertEquals(Locale.ENGLISH, new Utils().getLocale(mockedRequestUtils));
		
		List<LogEvent> events = listAppender.getEvents();
		LogEvent logEvent = events.get(0);
		assertEquals(Level.FATAL, logEvent.getLevel());
		assertEquals(IllegalArgumentException.class, logEvent.getThrown().getClass());

	}
	
	@Test
	public void testGetIP() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		String IP = "127.0.0.1";
		HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
		when(mockedHttpServletRequest.getRemoteAddr()).thenReturn(IP);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);
		
		assertEquals(IP, new Utils().getIP(mockedRequestUtils));
	}
	
	@Test
	public void testGetIPNullHttpRequest() {
		RequestUtils mockedRequestUtils = mock(RequestUtils.class);
		when(mockedRequestUtils.getHttpServletRequest()).thenReturn(null);
		
		assertEquals(null, new Utils().getIP(mockedRequestUtils));
		List<LogEvent> events = listAppender.getEvents();
		LogEvent logEvent = events.get(0);
		assertEquals(Level.WARN, logEvent.getLevel());
		assertEquals("Null Http Request!", logEvent.getMessage().toString());
	}

}
